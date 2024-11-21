package com.amlan.micrometer.custom_micrometer_tracing.tracing;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JmsPublisherInterceptor implements MethodInterceptor{

    private TraceInfoService traceInfoService;
    private String clientAppId;
    private String sourceAppId;

    public JmsPublisherInterceptor(TraceInfoService traceInfoService,String clientAppId, String sourceAppId){
        this.traceInfoService=traceInfoService;
        this.clientAppId=clientAppId;
        this.sourceAppId=sourceAppId;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        traceInfoService.setSpanAppId(clientAppId);
        if(traceInfoService.getTraceAppId()==null){
            traceInfoService.setTraceAppId(sourceAppId);
        }
        return invocation.proceed();
    }

}
