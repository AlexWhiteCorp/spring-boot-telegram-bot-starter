package com.alexcorp.springspirit.core;

import com.alexcorp.springspirit.core.session.BotSession;
import com.alexcorp.springspirit.exception.UpdateHandlerNotFoundException;
import datadog.trace.api.DDTags;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FileHandlerContext implements HandlerContext {

    private final ApplicationContext appCtx;
    private final RequestContext requestContext;
    private final ParamsInjector paramsInjector;

    private final StateMachine stateMachine = new StateMachine();
    private final List<BotCommandHandler> commandHandlers = new ArrayList<>();

    public void addHandler(Class<?>[] states, Method method) {
        BotCommandHandler container = new BotCommandHandler(null, null, states, method);

        if (states != null) {
            for (Class<?> state : states) {
                stateMachine.loadTreeNode(state);
            }
        }

        commandHandlers.add(container);
    }

    @SneakyThrows
    @Override
    public Object handle(Update update) {
        return handle(update, update.getMessage().getText());
    }

    @SneakyThrows
    public Object handle(Update update, String command) {
        Message msg = update.getMessage();
        BotSession session = requestContext.getSession();
        Class<?> currState = session.getState();

        for (BotCommandHandler container : commandHandlers) {
            if (stateMachine.isAvailableState(currState, container.getStates())) {
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

        throw new UpdateHandlerNotFoundException(msg.getText(), currState);
    }

    private Map<String, String> extractVars(String commandInput, String commandTemplate) {

        return Map.of();
    }
}
