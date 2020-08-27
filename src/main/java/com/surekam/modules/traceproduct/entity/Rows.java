package com.surekam.modules.traceproduct.entity;
import java.util.List;


public class Rows {

    private int total;
    private List<TraceData> traceData;
    public void setTotal(int total) {
         this.total = total;
     }
     public int getTotal() {
         return total;
     }

    public void setTraceData(List<TraceData> traceData) {
         this.traceData = traceData;
     }
     public List<TraceData> getTraceData() {
         return traceData;
     }

}