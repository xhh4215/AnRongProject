/*
 * SmartPublisherJni.java
 * SmartPublisherJni
 * 
 * Github: https://github.com/daniulive/SmarterStreaming
 * 
 * Created by DaniuLive on 2015/09/20.
 * Copyright © 2014~2016 DaniuLive. All rights reserved.
 */

package com.daniulive.smartpublisher;


import com.eventhandle.SmartEventCallback;

import java.nio.ByteBuffer;

public class SmartPublisherJni {
	
	public static class WATERMARK {
	   	public static final int WATERMARK_FONTSIZE_MEDIUM 			= 0;
	   	public static final int WATERMARK_FONTSIZE_SMALL 			= 1;
	   	public static final int WATERMARK_FONTSIZE_BIG	 			= 2;
	   	
	   	public static final int WATERMARK_POSITION_TOPLEFT 			= 0;
	   	public static final int WATERMARK_POSITION_TOPRIGHT			= 1;
	   	public static final int WATERMARK_POSITION_BOTTOMLEFT		= 2;
	   	public static final int WATERMARK_POSITION_BOTTOMRIGHT 		= 3;
	}
	
	/**
	 * Initialized publisher. 初始化sdk
	 *
	 * @param ctx: get by this.getApplicationContext()
	 *  Context
	 * @param audio_opt: if with 0: it does not publish audio; if with 1, it publish audio; if with 2, it publish external encoded audio, only support aac.
	 * 
	 * @param video_opt: if with 0: it does not publish video; if with 1, it publish video; if with 2, it publish external encoded video, only support h264, data:0000000167....
	 * 
	 * @param width: capture width; height: capture height.
	 *  图片的宽高信息
	 * <pre>This function must be called firstly.</pre>
	 *
	 * @return {0} if successful
	 */
    public native int SmartPublisherInit(Object ctx, int audio_opt, int video_opt,  int width, int height);
    
	 /**
	  * Set callback event
	  * 
	  * @param callback function
	  * 
	 * @return {0} if successful
	  */
    public native int SetSmartPublisherEventCallback(SmartEventCallback callback);
    
	 /**
	  * Set Video HW Encoder, if support HW encoder, it will return 0
	  *  检测是否支持硬编码，如果返回 0， 则支持，否则自动采用软编码;
	  * @param kbps: the kbps of different resolution(25 fps).
	  * 
	  * @return {0} if successful
	  */
   public native int SetSmartPublisherVideoHWEncoder(int kbps);
    
    /**
     * Set Text water-mark
	 *  设置文字水印
     * 
     * @param fontSize: it should be "MEDIUM", "SMALL", "BIG"
     * 
     * @param waterPostion: it should be "TOPLEFT", "TOPRIGHT", "BOTTOMLEFT", "BOTTOMRIGHT".
     * 
     * @param xPading, yPading: the distance of the original picture.
     * 
     * <pre> The interface is only used for setting font water-mark when publishing stream. </pre>  
     * 
     * @return {0} if successful
     */
    public native int SmartPublisherSetTextWatermark(String waterText, int isAppendTime, int fontSize, int waterPostion, int xPading, int yPading);
    
    
    /**
     * Set Text water-mark font file name
	 *
     * @param fontFileName:  font full file name,  e.g: /system/fonts/DroidSansFallback.ttf
	 *
	 * @return {0} if successful
     */
    public native int SmartPublisherSetTextWatermarkFontFileName(String fontFileName);
	
    /**
     * Set picture water-mark
     *  设置图片水印
     * @param picPath: the picture working path, e.g: /sdcard/logo.png
     * 
     * @param waterPostion: it should be "TOPLEFT", "TOPRIGHT", "BOTTOMLEFT", "BOTTOMRIGHT".
     * 
     * @param picWidth, picHeight: picture width & height
     * 
     * @param xPading, yPading: the distance of the original picture.
     * 
     * <pre> The interface is only used for setting picture(logo) water-mark when publishing stream, with "*.png" format </pre>  
     * 
     * @return {0} if successful
     */
    public native int SmartPublisherSetPictureWatermark(String picPath, int waterPostion, int picWidth, int picHeight, int xPading, int yPading);
    
    /**
     * Set gop interval.
     * 设置推送端 GOP 间隔，一般建议在帧率 的 1~3 倍，如不设置，用底层计算的默认值;
     * <pre>please set before SmartPublisherStart while after SmartPublisherInit.</pre>
     *
     * gopInterval: encode I frame interval, the value always > 0
     *
     * @return {0} if successful
     */
    public native int SmartPublisherSetGopInterval(int gopInterval);
    
    /**
     * Set software encode video bit-rate.
     * 设置 software encode video bit-rate，最大码流一般是平均码流的 2 倍，如不设置，用底层计算的默认值;
     * <pre>please set before SmartPublisherStart while after SmartPublisherInit.</pre>
     *
     * avgBitRate: average encode bit-rate(kbps)
     * 
     * maxBitRate: max encode bit-rate(kbps)
     *
     * @return {0} if successful
     */
    public native int SmartPublisherSetSWVideoBitRate(int avgBitRate, int maxBitRate);
    
    /**
     * Set fps.
     * 设置 fps，如不设置，用底层计算的默认值
     * <pre>please set before SmartPublisherStart while after SmartPublisherInit.</pre>
     *
     * fps: the fps of video, range with (1,25).
     *
     * @return {0} if successful
     */
    public native int SmartPublisherSetFPS(int fps);
    
	/**
     * Set software video encoder profile.
     * 设置软编码模式下的 video encoder profile，默认 baseline profile;
     * <pre>please set before SmartPublisherStart while after SmartPublisherInit.</pre>
     *
     * profile: the software video encoder profile, range with (1,3).
     * 
     * 1: baseline profile
     * 2: main profile
     * 3: high profile
     *
     * @return {0} if successful
     */
    public native int SmartPublisherSetSWVideoEncoderProfile(int profile);
    
    
    /**
     * 
     * Set software video encoder speed.
     * 设置软编码编码速度，设置 范围(1,6)，1 最快，6 最慢，默认是 6;
     * <pre>please set before SmartPublisherStart while after SmartPublisherInit.</pre>
     * 
     * @param speed: range with(1, 6), the default speed is 6. 
     * 
     * if with 1, CPU is lowest.
     * if with 6, CPU is highest.
     * 
     * @return {0} if successful
     */
    public native int SmartPublisherSetSWVideoEncoderSpeed(int speed);
	
     /**
     * Set Clipping Mode: 设置裁剪模式(仅用于640*480分辨率, 裁剪主要用于移动端宽高适配)
     *
     * <pre>please set before SmartPublisherStart while after SmartPublisherInit.</pre>
     *
     * @param mode: 0: 非裁剪模式 1:裁剪模式(如不设置, 默认裁剪模式)
     *
     * @return {0} if successful
     */
    public native int SmartPublisherSetClippingMode(int mode);
	
    /**
     * Set audio encoder type
     *  设置编码类型，默认 AAC 编码，type 设置为 2 时，启用 speex 编码(码流更低);
     * @param type: if with 1:AAC, if with 2: SPEEX
     * 
     * @return {0} if successful
     */
    public native int SmartPublisherSetAudioCodecType(int type);
    
    /**
     * Set speex encoder quality
     * 设置 speex 编码质量，数值 越大，质量越高，范围(0,10)，默认 8;
     * @param quality: range with (0, 10), default value is 8
     * 
     * @return {0} if successful
     */
    public native int SmartPublisherSetSpeexEncoderQuality(int quality);
    
    
    /**
     * Set Audio Noise Suppression
     * 设置噪音抑制，噪音抑制开启后， 去除采集端背景杂音;
     * @param isNS: if with 1:suppress, if with 0: does not suppress
     * 
     * @return {0} if successful
     */
    public native int SmartPublisherSetNoiseSuppression(int isNS);
    
    
    /**
     * Set Audio AGC
     *  设置自动增益控制，保持声音稳定
     * @param isNS: if with 1:AGC, if with 0: does not AGC
     * 
     * @return {0} if successful
     */
    public native int SmartPublisherSetAGC(int isAGC);
    
    
    /**
     * Set Audio Echo Cancellation
     * 
     * @param isCancel: if with 1:Echo Cancellation, if with 0: does not cancel
     * @param delay: echo delay(ms), if with 0, SDK will automatically estimate the delay.
     * 
     * @return {0} if successful
     */
    public native int SmartPublisherSetEchoCancellation(int isCancel, int delay);
    
    
    /**
     * Set mute or not during publish stream
     * 设置实时静音、取消静音
     * @param isMute: if with 1:mute, if with 0: does not mute
     * 
     * @return {0} if successful
     */
    public native int SmartPublisherSetMute(int isMute);
    
    /**
     * Set mirror
     *  镜像模式: 播放端和推送端本地回显方向显 示一致;
     * @param isMirror: if with 1:mirror mode, if with 0: normal mode
     * 
     * Please note when with "mirror mode", the publisher and player with the same echo direction
     * 
     * @return {0} if successful
     */
    public native int SmartPublisherSetMirror(int isMirror);
    
    /**
     * Set if recorder the stream to local file.
     * 设置是否本地录像
     * @param isRecorder: (0: do not recorder; 1: recorder)
     * 
     * <pre> NOTE: If set isRecorder with 1: Please make sure before call SmartPublisherStartPublish(), set a valid path via SmartPublisherCreateFileDirectory(). </pre> 
     * 
     * @return {0} if successful
     */
    public native int SmartPublisherSetRecorder(int isRecorder);
    
    /**
     * Create file directory
     *  创建录像文件 目录;
     * @param path,  E.g: /sdcard/daniulive/rec
     * 
     * <pre> The interface is only used for recording the stream data to local side. </pre> 
     * 
     * @return {0} if successful
     */
    public native int SmartPublisherCreateFileDirectory(String path);
    
    /**
     * Set recorder directory.
     * 设置录像文件目录
     * @param path: the directory of recorder file.
     * 
     * <pre> NOTE: make sure the path should be existed, or else the setting failed. </pre>
     * 
     * @return {0} if successful
     */
    public native int SmartPublisherSetRecorderDirectory(String path);
    
    /**
     * Set the size of every recorded file. 
     * 设置每个录像文件的大小，比 如 100M，超过这个大小后，会自动生成下一个录像文件;
     * @param size: (MB), (5M~500M), if not in this range, set default size with 200MB.
     * 
     * @return {0} if successful
     */
    public native int SmartPublisherSetRecorderFileMaxSize(int size);
    
	 /**
	  * Set if needs to save image during publishing stream
	  * 设置是否启用快照;
	  * @param is_save_image: if with 1, it will save current image via the interface of SmartPlayerSaveImage(), if with 0: does not it
	  *
	  * @return {0} if successful
	  */
	 public native int SmartPublisherSaveImageFlag(int is_save_image);
		  
	 /**
	  * Save current image during publishing stream
	  * 推送或录像过程中，根据设置路径和文件名， 实时快照;
	  * @param imageName: image name, which including fully path, "/sdcard/daniuliveimage/daniu.png", etc.
	  *
	  * @return {0} if successful
	  */
	 public native int SmartPublisherSaveCurImage(String imageName);
    
    /**
     * Set rtmp PublishingType
     *  设置 rtmp publisher 类型，0:live，1:record。
     * @param type: 0:live, 1:record. please refer to rtmp specification Page 46
     * 
     * @return {0} if successful
     */
    public native int SetRtmpPublishingType(int type);
        
    /**
	* Set publish stream url.
	* 
	* if not set url or url is empty, it will not publish stream
	*
	* @param url: publish url.
	*
	* @return {0} if successful
	*/
    public native int SmartPublisherSetURL(String url);
    
	/**
	* Start publish stream
	*
	* @return {0} if successful
	*/
    public native int SmartPublisherStart();
	
	/**
	* Set live video data(no encoded data).
	* 传递实时采集的 video 数据;
	* @param cameraType: CAMERA_FACING_BACK with 0, CAMERA_FACING_FRONT with 1
	* 
	* @param curOrg: LANDSCAPE with 0, PORTRAIT 1
	*
	* @return {0} if successful
	*/
    public native int SmartPublisherOnCaptureVideoData(byte[] data, int len, int cameraType, int curOrg);
    
    /**
	* Set live video data(no encoded data).
	* 第三方 YUV(I420)
	* @param data: I420 data
	* 
	* @param len: I420 data length
	* 
	* @param yStride: y stride
	* 
	* @param uStride: u stride
	* 
	* @param vStride: v stride
	*
	* @return {0} if successful
	*/
    public native int SmartPublisherOnCaptureVideoI420Data(byte[] data, int len, int yStride, int uStride, int vStride);
    
    
    /**
	* Set live video data(no encoded data).
	* 第三方 RGBA 接 口;
	* @param data: RGBA data
	* 
	* @param rowStride: stride information
	* 
	* @param width: width
	* 
	* @param height: height
	*
	* @return {0} if successful
	*/
    public native int SmartPublisherOnCaptureVideoRGBAData(ByteBuffer data, int rowStride, int width, int height);
	
	/**
	 * Set live video data(no encoded data).
	 * 设置 ABGR flip vertical(垂直翻转) data;
	 * @param data: ABGR flip vertical(垂直翻转) data
	 *
	 * @param rowStride: stride information
	 *
	 * @param width: width
	 *
	 * @param height: height
	 *
	 * @return {0} if successful
	 */
	public native int SmartPublisherOnCaptureVideoABGRFlipVerticalData(ByteBuffer data, int rowStride, int width, int height);
	
	
	/**
	 * Set far end pcm data
	 *  实时传递远端 PCM 数据(可 用于互动级的回音消除处理);
	 * @param pcmdata : 16bit pcm data
	 * @param sampleRate: audio sample rate
	 * @param channel: auido channel
	 * @param per_channel_sample_number: per channel sample numbers
	 * @param is_low_latency: if with 0, it is not low_latency, if with 1, it is low_latency
	 * @return {0} if successful
	 */
	public native int SmartPublisherOnFarEndPCMData(ByteBuffer pcmdata, int sampleRate, int channel, int per_channel_sample_number, int is_low_latency);
	
	
	/**
	* Set encoded video data.
	* 第三方编 码后视频数据接口;
	* @param buffer: encoded video data
	* 
	* @param len: data length
	* 
	* @param isKeyFrame: if with key frame, please set 1, otherwise, set 0.
	* 
	* @param timeStamp: video timestamp
	*
	* @return {0} if successful
	*/
    public native int SmartPublisherOnReceivingVideoEncodedData(byte[] buffer, int len, int isKeyFrame, long timeStamp);
    

	/**
	* set audio specific configure.
	* 第三方音频参数 设置接口;
	* @param buffer: audio specific settings.
	* 
	* For example:
	* 
	* sample rate with 44100, channel: 2, profile: LC
	* 
	* audioConfig set as below:
	* 
	*	byte[] audioConfig = new byte[2];
	*	audioConfig[0] = 0x12;
	*	audioConfig[1] = 0x10;
	* 
	* @param len: buffer length
	*
	* @return {0} if successful
	*/
    public native int SmartPublisherSetAudioSpecificConfig(byte[] buffer, int len);
    
	/**
	* Set encoded audio data.
	*
	* @param data: encoded audio data
	* 第三方编码后视频数据接口;
	* @param len: data length
	* 
	* @param isKeyFrame: 1
	* 
	* @param timeStamp: audio timestamp
	*
	* @return {0} if successful
	*/
    public native int SmartPublisherOnReceivingAACData(byte[] buffer, int len, int isKeyFrame, long timeStamp);
    
	/**
	 * Stop publish stream
	 * 第三方编码后视频数
	 据接口;
	 * @return {0} if successful
	 */
    public native int SmartPublisherStop();


    /*********增加新的接口 ++ ***************/
    /* 增加新接口是为了把推送和录像分离, 老的接口依然可用(SmartPublisherStart, SmartPublisherStop), 
     * 但是不要老接口和新接口混着用，这样结果是未定义的
    */
    
    /**
	* Start publish stream 
	* SmartPublisherStartPublisher，只推流;
	* @return {0} if successful
	*/
    public native int SmartPublisherStartPublisher();
    
    /**
   	* Stop publish stream 
   	* SmartPublisherStopPublisher，关闭推流;
   	* @return {0} if successful
   	*/
    public native int SmartPublisherStopPublisher();
    
    /**
	* Start recorder
	* SmartPublisherStartRecorder，只录像;
	* @return {0} if successful
	*/
    public native int SmartPublisherStartRecorder();
    
    /**
   	* Stop recorder 
   	*SmartPublisherStopRecorder，停止录像。
   	* @return {0} if successful
   	*/
    public native int SmartPublisherStopRecorder();
    
   
    /*********增加新的接口  -- ***************/      
}
