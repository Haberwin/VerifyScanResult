package com.miracle.verifyScanResult;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {
    private MutableLiveData <Integer> intervalTime;
    public MyViewModel(){
        intervalTime=new MutableLiveData<>();
        intervalTime.postValue(1000);
    }
    public MutableLiveData <Integer> getCorrectResult(){
        return intervalTime;
    }
    public void setCorrectResult(int time){
        intervalTime.postValue(time);
    }
}
