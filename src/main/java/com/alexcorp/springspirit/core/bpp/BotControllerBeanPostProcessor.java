package com.alexcorp.springspirit.core.bpp;

import com.alexcorp.springspirit.core.MessageHandlerContext;
import com.alexcorp.springspirit.annotation.BotCommand;
import com.alexcorp.springspirit.annotation.BotController;
import com.alexcorp.springspirit.annotation.State;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@RequiredArgsConstructor
public class BotControllerBeanPostProcessor implements BeanPostProcessor {

    private final MessageHandlerContext msgHandlerContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        BotController controllerAnnotation = bean.getClass().getAnnotation(BotController.class);
        if(controllerAnnotation == null) return bean;

        State stateAnnotation = bean.getClass().getAnnotation(State.class);

        String[] controllerCommandsRegEx = controllerAnnotation.value();
        Class[] controllerStates = null;
        if(stateAnnotation != null) controllerStates = stateAnnotation.value();

        Method[] methods = bean.getClass().getDeclaredMethods();

        for (Method method : methods) {
            BotCommand commandAnnotation = method.getAnnotation(BotCommand.class);

            if(commandAnnotation != null) {
                String[] commandsRegEx;
                if(commandAnnotation.value().length != 0) {
                    commandsRegEx = commandAnnotation.value();
                } else {
                    commandsRegEx = controllerCommandsRegEx;
                }

                Class<?>[] commandStates;
                State commandState = method.getAnnotation(State.class);
                if(commandState != null && commandState.value().length != 0) {
                    commandStates = commandState.value();
                } else {
                    commandStates = controllerStates;
                }

                for(String command : commandsRegEx) {
                    msgHandlerContext.addCommand(command, commandStates, method);
                }
            }
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
