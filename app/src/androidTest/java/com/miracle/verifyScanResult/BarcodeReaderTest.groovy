package com.miracle.verifyScanResult

import android.content.Context
import android.util.Log
import android.view.WindowManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeDeviceConnectionEvent;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.BarcodeReaderInfo;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith;
@RunWith(AndroidJUnit4.class)
class BarcodeReaderTest {

    private BarcodeReader mBarcodeReader;
    private BarcodeReader mInternalScannerReader;

    class MyCreatedCallbackTest implements AidcManager.CreatedCallback {
        MyCreatedCallback() {
        }

        @Override
        void onCreated(AidcManager aidcManager) {
            Log.d(TAG, "MyCreatedCallback onCreate !!!");
            mAidcManager = aidcManager;
            mAidcManager.addBarcodeDeviceListener(BarcodeReaderTest.this);
            initAllBarcodeReaderAndSetDefault();
        }
    }


    void initAllBarcodeReaderAndSetDefault() {
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
            }
            catch (Throwable e2) {
                e2.printStackTrace();
            }
        }
    }
    @Before
    void setUp() {
        super.setUp()
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        AidcManager.create(this, new MyCreatedCallbackTest());
    }
    @Test
    void testSetNotification(){
        mBarcodeReader.setProperty(BarcodeReader.PROPERTY_NOTIFICATION_GOOD_READ_ENABLED, true);
        assertEquals(mBarcodeReader.getBooleanProperty(BarcodeReader.PROPERTY_NOTIFICATION_GOOD_READ_ENABLED),true);
        mBarcodeReader.setProperty(BarcodeReader.PROPERTY_NOTIFICATION_GOOD_READ_ENABLED, false);
        assertEquals(mBarcodeReader.getBooleanProperty(BarcodeReader.PROPERTY_NOTIFICATION_GOOD_READ_ENABLED),false);

    }
    @Test
    void testSome(){
        assert 1;
    }

    void tearDown() {
    }
}
