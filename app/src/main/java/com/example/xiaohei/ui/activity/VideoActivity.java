package com.example.xiaohei.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.daniulive.smartpublisher.SmartPublisherJni;
import com.eventhandle.SmartEventCallback;
import com.example.xiaohei.R;
import com.example.xiaohei.context.EventConfig;
import com.example.xiaohei.context.MyApplication;
import com.example.xiaohei.enumpackage.MyEnum;
import com.example.xiaohei.event.InformationEvent;
import com.example.xiaohei.manager.PlayerManager;
import com.example.xiaohei.paintview.Point;

import com.example.xiaohei.socketdata.BaseServiceData;

import com.example.xiaohei.ui.view.SketchpadView;
import com.example.xiaohei.util.LogUtils;
import com.google.gson.Gson;

import com.voiceengine.NTAudioRecord;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VideoActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {
    //打印日志的标识符
    private static String TAG = "SmartPublisher";
    //初始化音频处理对象
    NTAudioRecord audioRecord_ = null;    //for audio capture
    //视频推流的对象
    private SmartPublisherJni libPublisher = null;
    //打开闪关灯的按钮
    private Button btnOpenFlash;
    //设置推动的类型  音频  视频  视频和音频
    private int pushType = 0;
    /* 推流分辨率选择
     * 0: 640*480
     * 1: 320*240
     * 2: 176*144
     * 3: 1280*720
     * */
    private Spinner resolutionSelector;
    private Timer timer;
    /* video软编码profile设置
     * 1: baseline profile
     * 2: main profile
     * 3: high profile
     * */
    private int sw_video_encoder_profile = 2;    //default with baseline profile
    public int servicedata;
    //视频管理的按钮
    private Button btnRecoderMgr;
    //切换摄像头的图片
    private ImageView imgSwitchCamera;
    //开始推流的按钮
    private Button btnStartStop;
    //视频调阅按钮 (功能暂时还未添加)
    private Button btnSeeVideo;
    //静音按钮
    private Button btnJy;
    //进行本地录制的视频的按钮
    private Button btnStartRecorder;
    private Button btnCaptureImage;
    //视频的显示控件
    private SurfaceView mSurfaceView = null;
    //视频绘制图形的自定义控件
    private SurfaceHolder mSurfaceHolder = null;
    //录制视频的相机控件
    private Camera mCamera = null;
    //进行自动对焦的监听事件
    private Camera.AutoFocusCallback myAutoFocusCallback = null;
    private boolean mPreviewRunning = false;
    private boolean isStart = false;
    private boolean isPushing = false;
    private boolean isRecording = false;
    private String publishURL;
    public int progress = 100;
    private String baseURL;
    private String printText = "URL:";
    private String txt = "当前状态";
    private static final int FRONT = 1;        //前置摄像头标记
    private static final int BACK = 2;        //后置摄像头标记
    private int currentCameraType = BACK;    //当前打开的摄像头标记
    private static final int PORTRAIT = 1;    //竖屏
    private static final int LANDSCAPE = 2;    //横屏
    private int currentOrigentation = LANDSCAPE;
    private int curCameraIndex = -1;
    private int openId = 0;//标识打开闪光的变量
    private int videoWidth = 640;
    private int videoHight = 480;
    private int frameCount = 0;
    private String recDir = "/sdcard/daniulive/rec";    //for recorder path
    private int sw_video_encoder_speed = 6;
    //进行视频播放的对象
    private PlayerManager player;
    private boolean is_hardware_encoder = false;
    private Context myContext;
    private SeekBar btnChangeJu;//改变焦距的控件
    //屏幕快照存储的位置的字符串 使用屏幕快照的前提是开始推流
    private String imageSavePath;
    private SketchpadView mSketchpadView;//图形绘制的控件

    static {
        System.loadLibrary("SmartPublisher");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate..");

        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);    //屏幕常亮

        setContentView(R.layout.activity_video);
        myContext = this.getApplicationContext();
        //设置快照路径(具体路径可自行设置)
        File storageDir = getOwnCacheDirectory(myContext, "daniuimage");//创建保存的路径
        imageSavePath = storageDir.getPath();
        Log.i(TAG, "快照存储路径: " + imageSavePath);
        resolutionSelector = (Spinner) findViewById(R.id.resolutionSelctor);
        mSketchpadView = (SketchpadView) findViewById(R.id.drawviewid);
        setSketchpadView(mSketchpadView);
        final String[] resolutionSel = new String[]{"高分辨率", "中分辨率", "低分辨率", "超高分辨率"};
        ArrayAdapter<String> adapterResolution = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, resolutionSel);
        adapterResolution.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        resolutionSelector.setAdapter(adapterResolution);
        resolutionSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                if (isStart || isPushing || isRecording) {
                    Log.e(TAG, "Could not switch resolution during publishing..");
                    return;
                }

                Log.i(TAG, "[推送分辨率]Currently choosing: " + resolutionSel[position]);

                SwitchResolution(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnRecoderMgr = (Button) findViewById(R.id.button_recoder_manage);
        btnRecoderMgr.setOnClickListener(new ButtonRecorderMangerListener());
        btnStartStop = (Button) findViewById(R.id.button_start_stop);
        btnStartStop.setOnClickListener(new ButtonStartListener());
        btnStartRecorder = (Button) findViewById(R.id.button_start_recorder);
        btnStartRecorder.setOnClickListener(new ButtonStartRecorderListener());
        btnChangeJu = (SeekBar) findViewById(R.id.change_ju);
        btnChangeJu.setOnSeekBarChangeListener(new MySeekBarListener());
        btnCaptureImage = (Button) findViewById(R.id.button_capture_image);
        btnCaptureImage.setOnClickListener(new ButtonCaptureImageListener());
        btnOpenFlash = (Button) findViewById(R.id.flashid);
        btnOpenFlash.setOnClickListener(new OpenFlashListener());
        imgSwitchCamera = (ImageView) findViewById(R.id.button_switchCamera);
        btnSeeVideo = (Button) findViewById(R.id.button_see_video);
        btnSeeVideo.setOnClickListener(new MySeeVideoListener());
        imgSwitchCamera.setOnClickListener(new SwitchCameraListener());
        btnJy = (Button) findViewById(R.id.button_jingyin);
        btnJy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSketchpadView.clearAllStrokes();
            }
        });
        mSurfaceView = (SurfaceView) this.findViewById(R.id.surface);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        getDataFromLogin();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //自动聚焦变量回调
        myAutoFocusCallback = new Camera.AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                if (success)//success表示对焦成功
                {
                    Log.i(TAG, "onAutoFocus succeed...");
                } else {
                    Log.i(TAG, "onAutoFocus failed...");
                }
            }
        };

        libPublisher = new SmartPublisherJni();
        player = new PlayerManager(this, R.id.video_view);


    }

    //用来处理登陆的界面传递过来的数据
    private void getDataFromLogin() {
        //获取视频推送的地址
        Intent intent = getIntent();
        baseURL = intent.getStringExtra("pushurl");
    }


    class MySeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int progress1 = progress / 10;
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setZoom(progress1);
            mCamera.setParameters(parameters);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }


    class OpenFlashListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (openId == 0) {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
                openId = 1;
            } else {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                openId = 0;
            }
        }
    }

    class SwitchCameraListener implements View.OnClickListener {
        public void onClick(View v) {
            Log.i(TAG, "Switch camera..");
            try {
                switchCamera();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    void SwitchResolution(int position) {
        Log.i(TAG, "Current Resolution position: " + position);

        switch (position) {
            case 0:
                videoWidth = 640;
                videoHight = 480;
                break;
            case 1:
                videoWidth = 320;
                videoHight = 240;
                break;
            case 2:
                videoWidth = 176;
                videoHight = 144;
                break;
            case 3:
                videoWidth = 1280;
                videoHight = 720;
                break;
            default:
                videoWidth = 640;
                videoHight = 480;
        }

        mCamera.stopPreview();
        initCamera(mSurfaceHolder);
    }

    void CheckInitAudioRecorder() {
        if (audioRecord_ == null) {
            audioRecord_ = new NTAudioRecord(this, 1);
        }

        if (audioRecord_ != null) {
            Log.i(TAG, "onCreate, call executeAudioRecordMethod..");
            // auido_ret: 0 ok, other failed
            int auido_ret = audioRecord_.executeAudioRecordMethod();
            Log.i(TAG, "onCreate, call executeAudioRecordMethod.. auido_ret=" + auido_ret);
        }
    }

    //Configure recorder related function.
    void ConfigRecorderFuntion(boolean isNeedLocalRecorder) {
        if (libPublisher != null) {
            if (isNeedLocalRecorder) {
                if (recDir != null && !recDir.isEmpty()) {
                    int ret = libPublisher.SmartPublisherCreateFileDirectory(recDir);
                    if (0 == ret) {
                        if (0 != libPublisher.SmartPublisherSetRecorderDirectory(recDir)) {
                            Log.e(TAG, "Set recoder dir failed , path:" + recDir);
                            return;
                        }

                        if (0 != libPublisher.SmartPublisherSetRecorder(1)) {
                            Log.e(TAG, "SmartPublisherSetRecoder failed.");
                            return;
                        }

                        if (0 != libPublisher.SmartPublisherSetRecorderFileMaxSize(200)) {
                            Log.e(TAG, "SmartPublisherSetRecoderFileMaxSize failed.");
                            return;
                        }

                    } else {
                        Log.e(TAG, "Create recoder dir failed, path:" + recDir);
                    }
                }
            } else {
                if (0 != libPublisher.SmartPublisherSetRecorder(0)) {
                    Log.e(TAG, "SmartPublisherSetRecoder failed.");
                    return;
                }
            }
        }
    }

    class ButtonRecorderMangerListener implements View.OnClickListener {
        public void onClick(View v) {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }

            Intent intent = new Intent();
            intent.setClass(VideoActivity.this, RecorderManager.class);
            intent.putExtra("RecoderDir", recDir);
            startActivity(intent);
        }
    }

    class EventHande implements SmartEventCallback {
        @Override
        public void onCallback(int code, long param1, long param2, String param3, String param4, Object param5) {
            switch (code) {
                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_STARTED:
                    txt = "开始。。";
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_CONNECTING:
                    txt = "连接中。。";
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_CONNECTION_FAILED:
                    txt = "连接失败。。";
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_CONNECTED:
                    txt = "连接成功。。";
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_DISCONNECTED:
                    txt = "连接断开。。";
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_STOP:
                    txt = "关闭。。";
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_RECORDER_START_NEW_FILE:
                    Log.i(TAG, "开始一个新的录像文件 : " + param3);
                    txt = "开始一个新的录像文件。。";
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_ONE_RECORDER_FILE_FINISHED:
                    Log.i(TAG, "已生成一个录像文件 : " + param3);
                    txt = "已生成一个录像文件。。";
                    break;

                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_SEND_DELAY:
                    Log.i(TAG, "发送时延: " + param1 + " 帧数:" + param2);
                    txt = "收到发送时延..";
                    break;

                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_CAPTURE_IMAGE:
                    Log.i(TAG, "快照: " + param1 + " 路径：" + param3);

                    if (param1 == 0) {
                        txt = "截取快照成功。.";
                    } else {
                        txt = "截取快照失败。.";
                    }
                    break;
            }

            String str = "当前回调状态：" + txt;

            Log.i(TAG, str);

        }
    }

    class ButtonStartListener implements View.OnClickListener {
        public void onClick(View v) {
            if (isPushing || isRecording) {
                return;
            }

            if (isStart) {
                stop();
                btnRecoderMgr.setEnabled(true);


                return;
            }

            isStart = true;
            btnStartStop.setText("开启");
            Log.i(TAG, "onClick start..");

            if (libPublisher != null) {
                {
                    publishURL = baseURL;
                    Log.i(TAG, "start, generate random url:" + publishURL);

                }

                printText = "URL:" + publishURL;

                Log.i(TAG, printText);
                ConfigRecorderFuntion(false);

                Log.i(TAG, "videoWidth: " + videoWidth + " videoHight: " + videoHight + " pushType:" + pushType);

                int audio_opt = 1;
                int video_opt = 1;

                if (pushType == 1) {
                    video_opt = 0;
                } else if (pushType == 2) {
                    audio_opt = 0;
                }

                libPublisher.SmartPublisherInit(myContext, audio_opt, video_opt, videoWidth, videoHight);

                if (is_hardware_encoder) {
                    int hwHWKbps = setHardwareEncoderKbps(videoWidth, videoHight);

                    Log.i(TAG, "hwHWKbps: " + hwHWKbps);

                    int isSupportHWEncoder = libPublisher.SetSmartPublisherVideoHWEncoder(hwHWKbps);

                    if (isSupportHWEncoder == 0) {
                        Log.i(TAG, "Great, it supports hardware encoder!");
                    }
                }

                libPublisher.SetSmartPublisherEventCallback(new EventHande());


                // set AAC encoder
                libPublisher.SmartPublisherSetAudioCodecType(1);


                // set Speex encoder
//			    	libPublisher.SmartPublisherSetAudioCodecType(2);
//			    	libPublisher.SmartPublisherSetSpeexEncoderQuality(8);


                libPublisher.SmartPublisherSetNoiseSuppression(1);//开启噪音抑制
                libPublisher.SmartPublisherSetMirror(0);//是否开启镜像
                libPublisher.SmartPublisherSetAGC(1);//开启agc功能

                //libPublisher.SmartPublisherSetClippingMode(0);

                libPublisher.SmartPublisherSetSWVideoEncoderProfile(sw_video_encoder_profile);

                libPublisher.SmartPublisherSetSWVideoEncoderSpeed(sw_video_encoder_speed);

                libPublisher.SmartPublisherSaveImageFlag(1);

                libPublisher.SetRtmpPublishingType(0);


                //libPublisher.SmartPublisherSetGopInterval(40);

                //libPublisher.SmartPublisherSetFPS(15);

                //libPublisher.SmartPublisherSetSWVideoBitRate(600, 1200);
                // IF not set url or url is empty, it will not publish stream
                // if ( libPublisher.SmartPublisherSetURL("") != 0 )


                btnRecoderMgr.setEnabled(false);
                BaseServiceData message = getBaseServiceDate();
                message.setMsgCom(MyEnum.CommandType.COMMAND_BEGIN_PUSH.ordinal());
                message.setMsgType(MyEnum.MessageType.MESSAGE_PHONEMSG.ordinal());
                Gson gson = new Gson();
                String s = gson.toJson(message) + "\n";
                MyApplication.getmClientAction().sendData(s);


            }

            if (pushType == 0 || pushType == 1) {
                CheckInitAudioRecorder();    //enable pure video publisher..
            }
        }
    }

    public BaseServiceData getBaseServiceDate() {
        BaseServiceData message = new BaseServiceData();
        return message;
    }

    private void ConfigControlEnable(boolean isEnable) {
        btnRecoderMgr.setEnabled(isEnable);

    }

    private void InitAndSetConfig() {
        Log.i(TAG, "videoWidth: " + videoWidth + " videoHight: " + videoHight
                + " pushType:" + pushType);

        int audio_opt = 1;
        int video_opt = 1;

        if (pushType == 1) {
            video_opt = 0;
        } else if (pushType == 2) {
            audio_opt = 0;
        }

        libPublisher.SmartPublisherInit(myContext, audio_opt, video_opt,
                videoWidth, videoHight);

        if (is_hardware_encoder) {
            int hwHWKbps = setHardwareEncoderKbps(videoWidth, videoHight);

            Log.i(TAG, "hwHWKbps: " + hwHWKbps);

            int isSupportHWEncoder = libPublisher
                    .SetSmartPublisherVideoHWEncoder(hwHWKbps);

            if (isSupportHWEncoder == 0) {
                Log.i(TAG, "Great, it supports hardware encoder!");
            }
        }

        libPublisher.SetSmartPublisherEventCallback(new EventHande());

        libPublisher.SmartPublisherSetAudioCodecType(1);

        // set Speex encoder
//            libPublisher.SmartPublisherSetAudioCodecType(2);
//            libPublisher.SmartPublisherSetSpeexEncoderQuality(8);


        libPublisher.SmartPublisherSetNoiseSuppression(1);//开启噪音抑制
        libPublisher.SmartPublisherSetMute(0);//取消静音
        libPublisher.SmartPublisherSetAGC(1);

        // libPublisher.SmartPublisherSetClippingMode(0);

        libPublisher.SmartPublisherSetSWVideoEncoderProfile(sw_video_encoder_profile);

        libPublisher.SmartPublisherSetSWVideoEncoderSpeed(sw_video_encoder_speed);

        // libPublisher.SetRtmpPublishingType(0);

        // libPublisher.SmartPublisherSetGopInterval(40);

        // libPublisher.SmartPublisherSetFPS(15);

        // libPublisher.SmartPublisherSetSWVideoBitRate(600, 1200);

        libPublisher.SmartPublisherSaveImageFlag(1);
    }

    class ButtonStartRecorderListener implements View.OnClickListener {
        public void onClick(View v) {
            if (isStart) {
                return;
            }

            if (isRecording) {
                stopRecorder();

                if (!isPushing) {
                    ConfigControlEnable(true);
                }

                btnStartRecorder.setText(" 录像");

                isRecording = false;

                return;
            }


            Log.i(TAG, "onClick start recorder..");

            if (libPublisher == null)
                return;

            isRecording = true;

            if (!isPushing) {
                InitAndSetConfig();
            }
            ConfigRecorderFuntion(true);

            int startRet = libPublisher.SmartPublisherStartRecorder();
            if (startRet != 0) {
                isRecording = false;

                Log.e(TAG, "Failed to start recorder.");
                return;
            }

            if (!isPushing) {
                if (pushType == 0 || pushType == 1) {
                    CheckInitAudioRecorder();    //enable pure video publisher..
                }
            }

            if (!isPushing) {
                ConfigControlEnable(false);
            }

            btnStartRecorder.setText(" 停止录像");
        }
    }

    class ButtonCaptureImageListener implements View.OnClickListener {
        @SuppressLint("SimpleDateFormat")
        public void onClick(View v) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "dn_" + timeStamp;    //创建以时间命名的文件名称

            String imagePath = imageSavePath + "/" + imageFileName + ".png";

            Log.i(TAG, "imagePath:" + imagePath);

            libPublisher.SmartPublisherSaveCurImage(imagePath);
        }
    }

    private void stop() {
        Log.i(TAG, "onClick stop..");
        StopPublish();
        isStart = false;
        btnStartStop.setText("停止");
    }

    private void stopPush() {
        if (!isRecording) {
            if (audioRecord_ != null) {
                Log.i(TAG, "stopPush, call audioRecord_.StopRecording..");
                audioRecord_.StopRecording();
                audioRecord_ = null;
            }
        }

        if (libPublisher != null) {
            libPublisher.SmartPublisherStopPublisher();
        }
    }

    private void stopRecorder() {
        if (!isPushing) {
            if (audioRecord_ != null) {
                Log.i(TAG, "stopRecorder, call audioRecord_.StopRecording..");
                audioRecord_.StopRecording();
                audioRecord_ = null;
            }
        }

        if (libPublisher != null) {
            libPublisher.SmartPublisherStopRecorder();
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "activity destory!");

        if (isStart) {
            isStart = false;
            StopPublish();
            Log.i(TAG, "onDestroy StopPublish");
        }

        if (isPushing || isRecording) {
            if (audioRecord_ != null) {
                Log.i(TAG, "surfaceDestroyed, call StopRecording..");
                audioRecord_.StopRecording();
                audioRecord_ = null;
            }

            stopPush();
            stopRecorder();

            isPushing = false;
            isRecording = false;
        }

        super.onDestroy();
        finish();
        System.exit(0);
    }

    private void SetCameraFPS(Camera.Parameters parameters) {
        if (parameters == null)
            return;

        int[] findRange = null;

        int defFPS = 20 * 1000;

        List<int[]> fpsList = parameters.getSupportedPreviewFpsRange();
        if (fpsList != null && fpsList.size() > 0) {
            for (int i = 0; i < fpsList.size(); ++i) {
                int[] range = fpsList.get(i);
                if (range != null
                        && Camera.Parameters.PREVIEW_FPS_MIN_INDEX < range.length
                        && Camera.Parameters.PREVIEW_FPS_MAX_INDEX < range.length) {
                    Log.i(TAG, "Camera index:" + i + " support min fps:" + range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]);

                    Log.i(TAG, "Camera index:" + i + " support max fps:" + range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);

                    if (findRange == null) {
                        if (defFPS <= range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]) {
                            findRange = range;

                            Log.i(TAG, "Camera found appropriate fps, min fps:" + range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
                                    + " ,max fps:" + range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
                        }
                    }
                }
            }
        }

        if (findRange != null) {
            parameters.setPreviewFpsRange(findRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX], findRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
        }
    }

    /*it will call when surfaceChanged*/
    private void initCamera(SurfaceHolder holder) {
        Log.i(TAG, "initCamera..");

        if (mPreviewRunning)
            mCamera.stopPreview();

        Camera.Parameters parameters;
        try {
            parameters = mCamera.getParameters();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        parameters.setPreviewSize(videoWidth, videoHight);
        parameters.setPictureFormat(PixelFormat.JPEG);
        parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);

        SetCameraFPS(parameters);

        setCameraDisplayOrientation(this, curCameraIndex, mCamera);

        mCamera.setParameters(parameters);

        int bufferSize = (((videoWidth | 0xf) + 1) * videoHight * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat())) / 8;

        mCamera.addCallbackBuffer(new byte[bufferSize]);

        mCamera.setPreviewCallbackWithBuffer(this);
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (Exception ex) {
            // TODO Auto-generated catch block
            if (null != mCamera) {
                mCamera.release();
                mCamera = null;
            }
            ex.printStackTrace();
        }
        mCamera.startPreview();
        mCamera.autoFocus(myAutoFocusCallback);
        mPreviewRunning = true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated..");
        try {

            int CammeraIndex = findBackCamera();
            Log.i(TAG, "BackCamera: " + CammeraIndex);

            if (CammeraIndex == -1) {
                CammeraIndex = findFrontCamera();
                currentCameraType = FRONT;
                imgSwitchCamera.setEnabled(false);
                if (CammeraIndex == -1) {
                    Log.i(TAG, "NO camera!!");
                    return;
                }
            } else {
                currentCameraType = BACK;
            }

            if (mCamera == null) {
                mCamera = openCamera(currentCameraType);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged..");
        initCamera(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        Log.i(TAG, "Surface Destroyed");
    }

    public void onConfigurationChanged(Configuration newConfig) {
        try {
            super.onConfigurationChanged(newConfig);
            Log.i(TAG, "onConfigurationChanged, start:" + isStart);
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (!isStart && !isPushing && !isRecording) {
                    currentOrigentation = LANDSCAPE;
                }
            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (!isStart && !isPushing && !isRecording) {
                    currentOrigentation = PORTRAIT;
                }
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        frameCount++;
        if (frameCount % 3000 == 0) {
            Log.i("OnPre", "gc+");
            System.gc();
            Log.i("OnPre", "gc-");
        }

        if (data == null) {
            Camera.Parameters params = camera.getParameters();
            Camera.Size size = params.getPreviewSize();
            int bufferSize = (((size.width | 0x1f) + 1) * size.height * ImageFormat.getBitsPerPixel(params.getPreviewFormat())) / 8;
            camera.addCallbackBuffer(new byte[bufferSize]);
        } else {
            if (isStart || isPushing || isRecording) {
                libPublisher.SmartPublisherOnCaptureVideoData(data, data.length, currentCameraType, currentOrigentation);
            }

            camera.addCallbackBuffer(data);
        }
    }

    @SuppressLint("NewApi")
    private Camera openCamera(int type) {
        int frontIndex = -1;
        int backIndex = -1;
        int cameraCount = Camera.getNumberOfCameras();
        Log.i(TAG, "cameraCount: " + cameraCount);

        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int cameraIndex = 0; cameraIndex < cameraCount; cameraIndex++) {
            Camera.getCameraInfo(cameraIndex, info);

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                frontIndex = cameraIndex;
            } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                backIndex = cameraIndex;
            }
        }

        currentCameraType = type;
        if (type == FRONT && frontIndex != -1) {
            curCameraIndex = frontIndex;
            return Camera.open(frontIndex);
        } else if (type == BACK && backIndex != -1) {
            curCameraIndex = backIndex;
            return Camera.open(backIndex);
        }
        return null;
    }

    private void switchCamera() throws IOException {
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        if (currentCameraType == FRONT) {
            mCamera = openCamera(BACK);
        } else if (currentCameraType == BACK) {
            mCamera = openCamera(FRONT);
        }

        initCamera(mSurfaceHolder);
    }

    private void StopPublish() {
        BaseServiceData stoppush = getBaseServiceDate();
        stoppush.setMsgType(MyEnum.MessageType.MESSAGE_PHONEMSG.ordinal());
        stoppush.setMsgCom(MyEnum.CommandType.COMMAND_END_PUSH.ordinal());
        Gson gson = new Gson();
        String s = gson.toJson(stoppush + "\n");
        MyApplication.getmClientAction().sendData(s);

        if (audioRecord_ != null) {
            Log.i(TAG, "surfaceDestroyed, call StopRecording..");
            audioRecord_.StopRecording();
            audioRecord_ = null;
        }

        if (libPublisher != null) {
            libPublisher.SmartPublisherStop();
        }
    }

    //Check if it has front camera
    private int findFrontCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return camIdx;
            }
        }
        return -1;
    }

    //Check if it has back camera
    private int findBackCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return camIdx;
            }
        }
        return -1;
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        Log.i(TAG, "curDegree: " + result);

        camera.setDisplayOrientation(result);
    }

    private int setHardwareEncoderKbps(int width, int height) {
        int hwEncoderKpbs = 0;

        switch (width) {
            case 176:
                hwEncoderKpbs = 300;
                break;
            case 320:
                hwEncoderKpbs = 500;
                break;
            case 640:
                hwEncoderKpbs = 1000;
                break;
            case 1280:
                hwEncoderKpbs = 1700;
                break;
            default:
                hwEncoderKpbs = 1000;
        }

        return hwEncoderKpbs;
    }

    /**
     * 根据目录创建文件夹
     *
     * @param context
     * @param cacheDir
     * @return
     */
    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        //判断sd卡正常挂载并且拥有权限的时候创建文件
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
            Log.i(TAG, "appCacheDir: " + appCacheDir);
        }
        if (appCacheDir == null || !appCacheDir.exists() && !appCacheDir.mkdirs()) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    /**
     * 检查是否有权限
     *
     * @param context
     * @return
     */
    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        return perm == 0;
    }
    //对绘图的自定义的view的相关的设置
    private static void setSketchpadView(SketchpadView sketchpadView) {
        sketchpadView.setStrokeType(SketchpadView.STROKE_RECT);
        sketchpadView.setStrokeColor(Color.RED);
    }
    //对socket返回的数据进行处理的方法
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 99)
    public void onPaint(final InformationEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Object object = event.getObject();
                    switch (event.getWhat()) {
                        //视频标绘
                        case EventConfig.POINT:
                            Point point = (Point) object;
                            handlePoint(point);
                            handleClear();
                            break;
                        //闪光灯的处理
                        case EventConfig.FlASH:
                            servicedata = (int) object;
                            handleFlash(mCamera);
                            break;
                        //焦距变化处理逻辑
                        case EventConfig.FOCUSING_DOWN:
                            servicedata = (int) object;
                            handleFocusingUp(servicedata, mCamera);
                            break;
                        case EventConfig.FOCUSING_UP:
                            servicedata = (int) object;
                            handleFocusingDown(servicedata, mCamera);
                            break;
                        case EventConfig.VIDEO_DISCUSS:
                            //拉取视频的url
                            String discussUrl = (String) object;
                            handlePlayer(discussUrl, player);
                            break;
                        case EventConfig.BEGIN_PUSH:
                            String message = (String) object;
                            handlePush(message);
                            break;
                    }
                } catch (Exception e) {
                    LogUtils.e(TAG, "handle board event fail");
                }

            }
        });
    }
    //清除标绘的图形
    private void handleClear() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSketchpadView.clearAllStrokes();
                            }
                        });

                    }
                });

            }
        }, 3000);
    }
    //判断服务端是不是可以进行视频推流
    private void handlePush(String message) {
        Toast.makeText(myContext, message, Toast.LENGTH_SHORT).show();
        if (libPublisher.SmartPublisherSetURL(publishURL) != 0) {

            Log.e(TAG, "Failed to set publish stream URL..");
            Toast.makeText(myContext, "推流失败", Toast.LENGTH_SHORT).show();
        }
        int isStarted = libPublisher.SmartPublisherStart();
        if (isStarted != 0) {
            Log.e(TAG, "Failed to publish stream..");
        } else {
            Toast.makeText(myContext, "推流失败", Toast.LENGTH_SHORT).show();
        }
    }
    //减小焦距
    private void handleFocusingDown(int message, Camera mCamera) {
        progress = progress - 10;
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setZoom(progress);
        mCamera.setParameters(parameters);
    }
    //播放拉取的视频源
    private void handlePlayer(String discussUrl, PlayerManager player) {
        BaseServiceData startpull = getBaseServiceDate();
        startpull.setMsgType(MyEnum.CommandType.COMMAND_BEGIN_PULL.ordinal());
        startpull.setMsgCom(MyEnum.MessageType.MESSAGE_PHONEMSG.ordinal());
        Gson gson = new Gson();
        String s = gson.toJson(startpull + "\n");
        MyApplication.getmClientAction().sendData(s);
        player.play(discussUrl);
    }
    //增加焦距
    private void handleFocusingUp(int message, Camera mCamera) {
        progress = progress + 10;
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setZoom(message);
        mCamera.setParameters(parameters);
    }
    //调节闪光灯的处理
    private void handleFlash(Camera mCamera) {
        if (openId == 0) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
            openId = 1;
        } else {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            openId = 0;
        }
    }
    //视频标绘的处理
    private void handlePoint(Point point) {
        //获取android手机的屏幕的宽度和高度
//        WindowManager wm = this.getWindowManager();
//        int width = wm.getDefaultDisplay().getWidth();
//        int height = wm.getDefaultDisplay().getHeight();
        int width = mSurfaceView.getWidth();
        int height = mSurfaceView.getHeight();
        double onePointx = point.getX1();
        double onePointy = point.getY1();
        double twoPointx = point.getX2();
        double twoPointy = point.getY2();
        double xxPoint = 2 * (width * onePointx);
        double xxxPoint = 640 - xxPoint;
        double xxxxPoint = 640 - xxxPoint;
        int x1 = new Double(xxxxPoint).intValue();
//        int x1 = (new Double(width * onePointx)).intValue();
        int y1 = (new Double(height * onePointy)).intValue();
        double xxPoint2 = width * twoPointx * 2;
        double xxxPoint2 = 640 - xxPoint2;
        double xxxxPoint2 = 640 - xxxPoint2;
        int x2 = (new Double(xxxxPoint2)).intValue();
//        int x2 = (new Double(width * twoPointx)).intValue();
        int y2 = (new Double(height * twoPointy)).intValue();
        mSketchpadView.action_down(x1, y1);
        mSketchpadView.action_up(x2, y2);
    }
    //activity重新回到栈顶运行的时候回调的方法
    @Override
    protected void onResume() {
        super.onResume();
        //EventBus注册
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }
    //activity处于暂停的时候的回调的方法
    @Override
    protected void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
    //视频调阅的功能的实现
    private class MySeeVideoListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //此处添加发起视频调阅的请求获取正在推流的地址进行播放
//            BaseServiceData message = getBaseServiceDate();
//            message.setMsgCom(MyEnum.CommandType.COMMAND_BEGIN_PUSH.ordinal());//等待设置对应的命令
//            message.setMsgType(MyEnum.MessageType.MESSAGE_PHONEMSG.ordinal());
//            message.setPhoneId("seevideo");
//            String s = gson.toJson(message + "\n");
//            MyApplication.getmClientAction().sendData(s);
        }
    }
}

