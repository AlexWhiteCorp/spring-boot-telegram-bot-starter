package com.alexcorp.springspirit.core;

import com.alexcorp.springspirit.core.session.BotSession;
import com.alexcorp.springspirit.exception.UpdateHandlerNotFoundException;
import datadog.trace.api.DDTags;
import datadog.trace.api.interceptor.MutableSpan;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class MessageHandlerContext implements HandlerContext {

    private final ApplicationContext appCtx;
    private final RequestContext requestContext;
    private final ParamsInjector paramsInjector;

    private final StateMachine stateMachine = new StateMachine();
    private final Map<Pattern, BotCommandHandler> commandHandlers = new HashMap<>();

    public void addCommand(String command, Class<?>[] states, Method method) {
        //TODO: перевірити, чи немає вже хендлера для такої команди(return false)

        String regex = command.replaceAll("\\$\\{.*}", ".*");
        Pattern commandPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        BotCommandHandler container = new BotCommandHandler(command, regex, states, method);

        if (states != null) {
            for (Class<?> state : states) {
                stateMachine.loadTreeNode(state);
            }
        }

        commandHandlers.put(commandPattern, container);
    }

    @SneakyThrows
    @Override
    public Object handle(Update update) {
        return handle(update, update.getMessage().getText());
    }

    @SneakyThrows
    public Object handle(Update update, String command) {
        Message msg = update.getMessage();
        if (msg == null) {
            msg = update.getCallbackQuery().getMessage();
        }

        BotSession session = requestContext.getSession();
        Class<?> currState = session.getState();

        for (Map.Entry<Pattern, BotCommandHandler> pair : commandHandlers.entrySet()) {
            BotCommandHandler container = pair.getValue();

            if (stateMachine.isAvailableState(currState, container.getStates())) {
                Matcher matcher = pair.getKey().matcher(command);

                if (matcher.find()) {
                    Method handler = container.getMethod();
                    Map<String, String> vars = extractVars(command, container.getCommand());
                    Object[] paramValues = paramsInjector.getMethodParamValues(container.getMethod(), update, session, vars);
                    Class<?> controllerBeanClass = handler.getDeclaringClass();
                    Object controllerBeanObject = appCtx.getBean(controllerBeanClass);

                    Tracer tracer = GlobalTracer.get();
                    Tracer.SpanBuilder spanBuilder = tracer.buildSpan("@BotCommand");
                    spanBuilder.withTag(DDTags.SERVICE_NAME, "prophase-bot");
                    spanBuilder.withTag("resource.name", container.getCommand());
                    for (Map.Entry<String, String> entry : vars.entrySet()) {
                        spanBuilder.withTag("var." + entry.getKey(), entry.getValue());
                    }
                    Span span = spanBuilder.start();
                    ((MutableSpan) span).getLocalRootSpan();

                    try (Scope scope = tracer.activateSpan(span)) {
                        Object result = handler.invoke(controllerBeanObject, paramValues);
                        if (result instanceof Redirect redirect) {
                            return handle(update, redirect.getCommand());
                        }

                        return result;
                    } finally {
                        span.finish();
                    }
                }
            }
        }

        throw new UpdateHandlerNotFoundException(msg.getText(), currState);
    }

    private Map<String, String> extractVars(String commandInput, String commandTemplate) {
        Map<String, String> vars = new HashMap<>();

        String var = commandInput.replace("check-answer-", "");

        vars.put("questionId", var);

        return vars;
    }
}
