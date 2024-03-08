package com.miracle.verifyScanResult;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

class Barcode {
    private String mData;
    private String mAIM;
    private String mCodeId;
    private Charset mCharset;

    Barcode(String barcodeData, Charset charset, String aimId, String codeId) {
        this.mData=barcodeData;
        this.mCharset=charset;
        this.mAIM=aimId;
        this.mCodeId=codeId;
    }


    public String getData() {
        return new String(mData.getBytes(mCharset));
    }

    public void setData(String mData) {
        this.mData = mData;
    }

    public String getBarcodeType() {
        return "AIM id:"+mAIM+"\r\nCode id:"+mCodeId;
    }

    public void setCodeId(String mCodeId) {
        this.mCodeId = mCodeId;
    }


    public void setAIM(String mAIM) {
        this.mAIM = mAIM;
    }

    public Charset getCharset() {
        return mCharset;
    }

    public void setCharset(Charset mCharset) {
        this.mCharset = mCharset;
    }
}
