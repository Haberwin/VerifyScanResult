package com.miracle.verifyScanResult;

import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeDeviceConnectionEvent;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.BarcodeReaderInfo;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class MainActivityTest implements AidcManager.CreatedCallback,BarcodeReader.BarcodeListener, AidcManager.BarcodeDeviceListener, BarcodeReader.TriggerListener{
    private static final String TAG = "example_demo";

    private AidcManager mAidcManager;

    private BarcodeReader mBarcodeReader;
    private BarcodeReader mInternalScannerReader;



    @Override
    public void onBarcodeEvent(BarcodeReadEvent barcodeReadEvent) {

    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {

    }

    @Override
    public void onTriggerEvent(TriggerStateChangeEvent triggerStateChangeEvent) {

    }
    @Override
    public void onCreated(AidcManager aidcManager) {
        mAidcManager = aidcManager;
    }





    BarcodeReader initBarcodeReader(BarcodeReader mReader, String mReaderName) {
        if (mReader == null) {
            try {
                if (mReaderName == null) {
                    mReader = mAidcManager.createBarcodeReader();
                } else {
                    mReader = mAidcManager.createBarcodeReader(mReaderName);
                }
            }
            catch (Exception e) {
                Log.e(TAG, "error", e);
            }
            try {
                mReader.claim();
                Log.d(TAG, "Call DCS interface claim() " + mReaderName);
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
            }
            try {
                mReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE, BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL);
                mReader.setProperty(BarcodeReader.PROPERTY_DATA_PROCESSOR_LAUNCH_EZ_CONFIG, false);

            } catch (UnsupportedPropertyException e2) {
                e2.printStackTrace();
            }
        }
        return mReader;
    }

    @Override
    public void onBarcodeDeviceConnectionEvent(BarcodeDeviceConnectionEvent barcodeDeviceConnectionEvent) {

    }



    BarcodeReader initAllBarcodeReaderAndSetDefault(AidcManager mAidcManager) {
        List<BarcodeReaderInfo> readerList = mAidcManager.listBarcodeDevices();
        Log.d(TAG, "initAllBarcodeReaderAndSetDefault readerList = "+readerList);
        mInternalScannerReader = null;

        for (BarcodeReaderInfo reader : readerList) {
            if ("dcs.scanner.imager".equals(reader.getName())) {
                mInternalScannerReader = initBarcodeReader(mInternalScannerReader, reader.getName());
            }
        }

        Log.d(TAG, "initAllBarcodeReaderAndSetDefault mInternalScannerReader = "+mInternalScannerReader);

        if (mInternalScannerReader != null) {
            mBarcodeReader = mInternalScannerReader;
        }
        else {
            Log.d(TAG, "No reader find");
        }
        if (mBarcodeReader != null) {
            try {
                mBarcodeReader.addBarcodeListener(this);
                mBarcodeReader.addTriggerListener(this);
                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_DATA_PROCESSOR_LAUNCH_EZ_CONFIG, false);
            }
            catch (Throwable e2) {
                e2.printStackTrace();
            }
        }
        return mBarcodeReader;
    }
    @Before
    public void initScanner() throws  InterruptedException {
        AidcManager.create(InstrumentationRegistry.getInstrumentation().getTargetContext(), MainActivityTest.this);
        sleep(3000);
        initAllBarcodeReaderAndSetDefault(mAidcManager);
    }
    @Test()
    public void testLunchEZ() throws UnsupportedPropertyException {
        mBarcodeReader.setProperty(BarcodeReader.PROPERTY_DATA_PROCESSOR_LAUNCH_EZ_CONFIG,false);
        assertEquals(mBarcodeReader.getBooleanProperty(BarcodeReader.PROPERTY_DATA_PROCESSOR_LAUNCH_EZ_CONFIG),false);
    }

    @Test
    public void onFailureEvent() {
        assertEquals(1,1);
    }


}