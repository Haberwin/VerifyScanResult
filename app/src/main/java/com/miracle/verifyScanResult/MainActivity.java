package com.miracle.verifyScanResult;

import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeDeviceConnectionEvent;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.BarcodeReaderInfo;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;

import java.util.ArrayList;
import java.util.List;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import static java.lang.Thread.sleep;

//import com.honeywell.aidc.InvalidScannerNameException;

public class MainActivity extends AppCompatActivity
        implements BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener, AidcManager.BarcodeDeviceListener{
    public static final String TAG = "example_demo";

    private AidcManager mAidcManager;
    private BarcodeReader mBarcodeReader;
    private BarcodeReader mInternalScannerReader;
    private boolean mKeyPressed = false;
    private TextView resultText;
    public ScanResultItem correctResult,failedResult;
    public String correctValue="";

    private List<ScanResultItem> resultList =new ArrayList<>();
    public ScanResultAdapter scanResultAdapter;
    private Parcelable mListState=null;
    private String LIST_STATE="result_list_state";

    public boolean continuousScan=false;
    public int intervalTime=1000;
    public Long startDecodeTime=0L;
    private ListView listView;
    Object binding;
    MyViewModel myViewModel;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
        mListState=listView.onSaveInstanceState();
        outState.putParcelable(LIST_STATE,mListState);

    }




    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mListState=savedInstanceState.getParcelable(LIST_STATE);
    }

    class MyCreatedCallback implements AidcManager.CreatedCallback {
        MyCreatedCallback() {
        }

        @Override
        public void onCreated(AidcManager aidcManager) {
            Log.d(TAG, "MyCreatedCallback onCreate !!!");
            mAidcManager = aidcManager;
            mAidcManager.addBarcodeDeviceListener(MainActivity.this);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
            try {
                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_NOTIFICATION_GOOD_READ_ENABLED, true);
                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, true);

                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_EAN_13_ENABLED, true);
                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_EAN_8_ENABLED, true);
                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_CODE_39_FULL_ASCII_ENABLED, true);
                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_CODE_93_ENABLED, true);
                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED,true);
                mBarcodeReader.setProperty(BarcodeReader.PROPERTY_DATA_PROCESSOR_LAUNCH_BROWSER,false);
            } catch (UnsupportedPropertyException e) {
                e.printStackTrace();
            }

        }
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

    public void onBarcodeDeviceConnectionEvent(BarcodeDeviceConnectionEvent event) {
        Log.d(TAG, event.getBarcodeReaderInfo() + " Connection status: " + event.getConnectionStatus());
    }

    public void onBarcodeEvent(final BarcodeReadEvent event) {
        runOnUiThread(new Runnable() {
            public void run() {

                Log.d(TAG,"Enter onBarcodeEvent ==> "+ event.getBarcodeData());
                String barcodeDate = new String(event.getBarcodeData().getBytes(event.getCharset()));
                Log.d(TAG, "Enter onBarcodeEvent ==> " + barcodeDate);

                resultText.setText(barcodeDate);

                    if (!barcodeDate.equals(correctValue)) {
                        int i;
                        for( i=0;i<resultList.size();i++){
                            if (barcodeDate.equals(resultList.get(i).getResult())) {
                                resultList.get(i).addTimes();
                                resultList.get(i).setDecodeTime(System.currentTimeMillis()-startDecodeTime);
                                break;
                            }

                        }
                        if (i==resultList.size()){
                            ScanResultItem errorResult =new ScanResultItem(barcodeDate,System.currentTimeMillis()-startDecodeTime);
                            errorResult.addTimes();

                            resultList.add(errorResult);
                        }

                    }
                    else {

                        if(correctResult ==null){
                            correctResult=new ScanResultItem(correctValue,System.currentTimeMillis()-startDecodeTime);
                            resultList.add(correctResult);
                        }
                        correctResult.setDecodeTime(System.currentTimeMillis()-startDecodeTime);
                        correctResult.addTimes();
                    }

                scanResultAdapter.notifyDataSetChanged();
            }
        });
        if(continuousScan){
            try{
                Thread.sleep(intervalTime);
                doScan(true);

            }catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void onFailureEvent(final BarcodeFailureEvent event) {
        runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "Enter onFailureEvent ===> " + event.getTimestamp());
                resultText.setText(R.string.failed);
                failedResult.addTimes();
                scanResultAdapter.notifyDataSetChanged();
            }
        });
        if(continuousScan){
            try{
                Thread.sleep(intervalTime);
                doScan(true);

            }catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void onTriggerEvent(TriggerStateChangeEvent event) {
        if (event.getState()) {
            if (!mKeyPressed) {
                mKeyPressed = true;
                doScan(true);
            }
        } else {
            mKeyPressed = false;
            doScan(false);
        }
        Log.d(TAG, "OnTriggerEvent status: " + event.getState());
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity onCreate !!!");
        initializeUI();
        AidcManager.create(this, new MyCreatedCallback());
    }

    protected void onResume() {
        super.onResume();
        if (this.mInternalScannerReader != null) {
            try {
                this.mInternalScannerReader.claim();
                Log.d(TAG, "Claim internal scanner");
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
            }
        }
        if(mListState!=null){
            listView.onRestoreInstanceState(mListState);

            mListState=null;
        }
    }

    protected void onPause() {
        super.onPause();

        if (this.mInternalScannerReader != null) {
            this.mInternalScannerReader.release();
            Log.d(TAG, "Release internal scanner");
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (this.mInternalScannerReader != null) {
            this.mInternalScannerReader.removeBarcodeListener(this);
            this.mInternalScannerReader.removeTriggerListener(this);
            this.mInternalScannerReader.close();
            this.mInternalScannerReader = null;
            Log.d(TAG, "Close internal scanner");
        }
        if (this.mAidcManager != null) {
            this.mAidcManager.removeBarcodeDeviceListener(this);
            this.mAidcManager.close();
        }
    }

    @SuppressWarnings("ClickableViewAccessibility")
    private void initializeUI() {

        setContentView(R.layout.activity_first);
        scanResultAdapter =new ScanResultAdapter(MainActivity.this,R.layout.resule_list_item, resultList);
        listView = findViewById(R.id.result_list);
        listView.setAdapter(scanResultAdapter);

        //binding = DataBindingUtil.setContentView(this, R.layout.activity_first);
        //myViewModel=new ViewModelProvider(this).get(MyViewModel.class);
        //binding.setData(myViewModel);
        //binding.setLifecycleOwner(this);

        final Button btnScan =  findViewById(R.id.scan_button);
        final Button confirm =findViewById(R.id.comfirm);
        final Switch continuous =findViewById(R.id.automatic_switch);
        final EditText intervalText =findViewById(R.id.intervalNumber);

        continuous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                continuousScan=isChecked;
                if(isChecked){
                    try{
                        intervalTime=Integer.parseInt(intervalText.getText().toString());

                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }catch (NumberFormatException e){
                        intervalTime=1000;
                        Toast.makeText(MainActivity.this,"Interval time format error! Set Interval time to 1000ms.",Toast.LENGTH_SHORT).show();
                    }
                    doScan(true);


                }
            }
        });


        failedResult=new ScanResultItem("Failed",0L);
        resultList.add(failedResult);
        resultText =findViewById(R.id.result_text);
        final EditText correctResultText =findViewById(R.id.correct_result);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultList.clear();

                correctResultText.setText(resultText.getText());

                correctValue=correctResultText.getText().toString();

                for(int i=0;i<resultList.size();i++){
                    if (correctValue.equals(resultList.get(i).getResult())){
                        return;
                    }
                }
                correctResult=new ScanResultItem(correctValue,System.currentTimeMillis()-startDecodeTime);
                resultList.add(correctResult);
            }
        });



        //btnScan.setTypeface(null, 1);
        btnScan.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
//                if( correctValue.equals("")){
//                    Toast.makeText(MainActivity.this,"Please input correct result First!",Toast.LENGTH_LONG).show();
//                    return false;
//                }


                switch (event.getAction()) {
                    case KeyEvent.ACTION_DOWN:

                        try {
                            Log.d(TAG, "Soft scan button onTouch down");
                            doScan(true);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    case KeyEvent.ACTION_UP:
                        Log.d(TAG, "Soft scan button onTouch up");
                        doScan(false);
                        break;
                }
                return true;
            }


        });








        //mTextView = findViewById(R.id.tv_show);
    }

    void doScan(boolean do_scan) {
        try {
            if (do_scan) {
                Log.d(TAG, "Start a new Scan!");
            } else {
                Log.d(TAG, "Cancel last Scan!");
            }
            startDecodeTime=System.currentTimeMillis();
            mBarcodeReader.decode(do_scan);
        } catch (ScannerNotClaimedException e) {
            Log.e(TAG, "catch ScannerNotClaimedException",e);
            e.printStackTrace();
        } catch (ScannerUnavailableException e2) {
            Log.e(TAG, "catch ScannerUnavailableException",e2);
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

}
