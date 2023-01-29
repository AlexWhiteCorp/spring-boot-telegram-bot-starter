package com.alexcorp.springspirit.core.tracing;

import datadog.trace.api.DDSpanTypes;
import datadog.trace.api.DDTags;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.lang.reflect.Method;

@Slf4j
@Primary
@Component
public class TracingTransactionInterceptor extends TransactionInterceptor {

    public TracingTransactionInterceptor(TransactionAttributeSource attributeSource) {
        super();
        setTransactionAttributeSource(attributeSource);
    }

    @Override
    protected Object invokeWithinTransaction(Method method,
                                             Class<?> targetClass,
                                             InvocationCallback invocation) throws Throwable {
        Tracer tracer = GlobalTracer.get();
        Span parent = tracer.activeSpan();
        Span span = tracer.buildSpan("TransactionManager")
                .withTag("resource.name", targetClass.getSimpleName() + "::" + method.getName())
                .withTag(DDTags.SPAN_TYPE, DDSpanTypes.HIBERNATE)
                .withTag(DDTags.SERVICE_NAME, "transaction-manager")
                .asChildOf(parent)
                .start();

        try (Scope scope = tracer.activateSpan(span)) {
            return super.invokeWithinTransaction(method, targetClass, invocation);
        } finally {
            span.finish();
        }
    }
}
