package com.amlan.micrometer.custom_micrometer_tracing.tracing;

import brave.baggage.BaggageField;

public class TraceInfoService {

    BaggageField traceAppId;
    BaggageField spanAppId;

    public TraceInfoService (BaggageField traceAppId, BaggageField spanAppId){
        this.traceAppId=traceAppId;
        this.spanAppId=spanAppId;
    }

    public boolean setTraceAppId(String traceId){
       return this.traceAppId !=null?this.traceAppId.updateValue(traceId) : false;
    }

    public String getTraceAppId(){
        return this.traceAppId !=null?this.traceAppId.getValue():null;
    }

    public boolean setSpanAppId(String spanId){
        return this.spanAppId!=null?this.spanAppId.updateValue(spanId):false;
    }
    public String getSpanAppId(){
        return this.spanAppId!=null?this.spanAppId.getValue():null;
    }

}
