package com.miracle.verifyScanResult;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketService extends Service {
    private boolean isServiceDestroyed = false;
    private final IBinder binder=new LocalBinder();
    private Handler mHandler;
    private String TAG="LWH";
    private InputStream is;
    private InputStreamReader isr;
    private BufferedReader br;
    private Socket client;
    private OutputStream op;


    public SocketService() {
    }

    public class LocalBinder extends Binder {
        public SocketService getService(){
            return SocketService.this;
        }
    }
    public void setHandler(Handler handler){
        mHandler=handler;
    }
    public boolean connectServer(String server_IP){
        try {
            client=new Socket(server_IP, 8888);
            is = client.getInputStream();

            // 步骤2：创建输入流读取器对象 并传入输入流对象
            // 该对象作用：获取服务器返回的数据
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
//        new Thread(new TcpClient()).start();
        return binder;
    }

    private class RecieveData implements Runnable{
        @Override
        public void run() {
            while(!isServiceDestroyed){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                while(!isServiceDestroyed){
                                    String str = br.readLine();
                                    switch (str){
                                        case "Update":
                                            Message updateBarcode=new Message();

                                            mHandler.sendMessage(updateBarcode);
                                            break;
                                        default:
                                            break;
                                    }
                                    Log.i(TAG, "收到客户端发来的消息" + str);
                                    if (TextUtils.isEmpty(str)){
                                        //客户端断开了连接
                                        Log.i(TAG, "客户端断开了连接");
                                        break;
                                    }

                                }
                                is.close();
                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }
                        }
                    }).start();

            }

        }

    }

    private class SendMessage implements Runnable{
        String msg;
        private void SendMessage(String msg){
            this.msg=msg;
        }
        @Override
        public void run() {
            PrintWriter out = null;
            try {
                op = client.getOutputStream();

                // 步骤2：写入需要发送的数据到输出流对象中
                op.write((msg+"\n").getBytes("utf-8"));
                // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞

                // 步骤3：发送数据到服务端
                op.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
