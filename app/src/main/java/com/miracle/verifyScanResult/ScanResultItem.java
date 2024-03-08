package com.miracle.verifyScanResult;

class ScanResultItem {
    private String result;
    private int times=0;
    private Long mDecodeTime=0L;
    private String codeType;
    ScanResultItem(String result,String codeType, Long decodeTime){
        this.result=result;
        this.codeType=codeType;
        this.mDecodeTime=decodeTime;

    }
    void addTimes(){
        this.times++;
    }
    void clearTimes(){
        this.times=0;
    }
    String getResult(){
        return this.result;
    }
    int getTimes(){
        return this.times;
    }
    Long getDecodeTime(){
        return this.mDecodeTime;
    }
    void setCodeType(String codeType){
        this.codeType=codeType;
    }
    String getCodeType(){
        return this.codeType;
    }

    void setDecodeTime(Long decodeTime) {
        this.mDecodeTime = decodeTime;
    }
}
