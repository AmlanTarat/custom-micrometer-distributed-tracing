package com.amlan.micrometer.custom_micrometer_tracing.config;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import com.amlan.micrometer.custom_micrometer_tracing.tracing.JmsPublisherInterceptor;
import com.amlan.micrometer.custom_micrometer_tracing.tracing.TraceInfoService;

import brave.baggage.BaggageField;
import brave.baggage.CorrelationScopeConfig;
import brave.context.slf4j.MDCScopeDecorator;
import brave.propagation.CurrentTraceContext;

@Configuration
@AutoConfigureBefore({BraveAutoConfiguration.class})
@ConditionalOnProperty(value = "management.tracing.enable")
public class TraceBraveAutoConfiguration {

    @Value("${spring.application.name}")
    private String clientAppId;
    @Value("@{${spring.application.index}}")
    private String sourceAppId;

    public BaggageField traceAppId(){
        return BaggageField.create("traceAppId");
    }

    public BaggageField spanAppId(){
        return BaggageField.create("spanAppId");
    }

    public TraceInfoService traceInfoService(){
        return new TraceInfoService(traceAppId(), spanAppId());
    }

    public CurrentTraceContext.ScopeDecorator mdcScopeDecorator(){
        return MDCScopeDecorator.newBuilder().clear()
                    .add(CorrelationScopeConfig.SingleCorrelationField.newBuilder(traceAppId()).flushOnUpdate().build())
                    .add(CorrelationScopeConfig.SingleCorrelationField.newBuilder(spanAppId()).flushOnUpdate().build())
                    .build();
    }

    public Advisor advisorFedexJmsPublisher(){
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* org.springframework.jms.core.JmsOperations.convertAndSend(..))");
        return new DefaultPointcutAdvisor(pointcut, new JmsPublisherInterceptor(traceInfoService(), clientAppId, sourceAppId));
    }
}
