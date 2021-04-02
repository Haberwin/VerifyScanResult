package com.miracle.verifyScanResult;

class ScanResultItem {
    private String result;
    private int times=0;
    private Long decodeTime=0L;
    ScanResultItem(String result, Long decodeTime){
        this.result=result;
        this.decodeTime=decodeTime;

    }
    void addTimes(){
        this.times++;
    }
    String getResult(){
        return this.result;
    }
    int getTimes(){
        return this.times;
    }
    Long getDecodeTime(){
        return this.decodeTime;
    }

    void setDecodeTime(Long decodeTime) {
        this.decodeTime = decodeTime;
    }
}
