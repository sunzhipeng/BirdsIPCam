package com.szp.birdsipcam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.szp.birdsipcam.net.WiFi_AP;
import com.szp.birdsipcam.rtp.RtpSenderWrapper;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, PreviewCallback{

    private static final String TAG = "BirdsIPCam";

    private int width = 800;
    private int height = 480;
    private byte[] h264 = new byte[width * height * 3];

    private RtpSenderWrapper mRtpSenderWrapper;
    private AvcEncoder avcCodec;
    private Camera m_camera;

    private SurfaceView   m_prevewview;
    private SurfaceHolder m_surfaceHolder;

    private WiFi_AP wiFi_ap;
    private WifiManager wifiManager;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectAll()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wiFi_ap = new WiFi_AP(wifiManager);


        //创建rtp并填写需要发送数据流的地址，直播中需要动态获取客户主动请求的地址
        mRtpSenderWrapper = new RtpSenderWrapper("192.168.43.50", 5004, false);
        try {
            int framerate = 30;
            int bitrate = 2500000;
            avcCodec = new AvcEncoder(width, height, framerate, bitrate);
        } catch (IOException e) {
            e.printStackTrace();
        }


        m_prevewview = findViewById(R.id.SurfaceViewPlay);

        m_surfaceHolder = m_prevewview.getHolder(); // 绑定SurfaceView，取得SurfaceHolder对象
        m_surfaceHolder.setFixedSize(width, height); // 预览大小設置
        //m_surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        m_surfaceHolder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.v(TAG, "MainActivity+surfaceCreated");
        try {
            m_camera = Camera.open();
            m_camera.setPreviewDisplay(m_surfaceHolder);
            Camera.Parameters parameters = m_camera.getParameters();
            parameters.setPreviewSize(width, height);
            parameters.setPictureSize(width, height);
            parameters.setPreviewFormat(ImageFormat.YV12);
            m_camera.setParameters(parameters);
            m_camera.setPreviewCallback(this);
            m_camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.v(TAG, "MainActivity+surfaceDestroyed");
        m_camera.setPreviewCallback(null);  //！！这个必须在前，不然退出出错
        m_camera.release();
        m_camera = null;
        avcCodec.close();
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Log.v(TAG, "MainActivity+h264 start");
        int ret = avcCodec.offerEncoder(bytes, h264);
        if (ret > 0) {
            //实时发送数据流
            mRtpSenderWrapper.sendAvcPacket(h264, 0, ret, 0);
        }
        Log.v(TAG, "MainActivity+h264 end");
        Log.v(TAG, "-----------------------------------------------------------------------");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
