package com.demo.cjh.signin.util

import android.content.Context
import android.util.Log
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.TtsMode
import com.demo.cjh.signin.App
import com.demo.cjh.signin.control.InitConfig
import com.demo.cjh.signin.control.MySyntherizer
import com.demo.cjh.signin.listener.MessageListener
import java.io.IOException
import java.lang.Exception
import java.util.HashMap

/**
 * Created by CJH
 * on 2018/7/15
 */

class SpeakUtil(var context: Context){
    val TAG = "SpeakUtil"

    // ================== 初始化参数设置开始 ==========================
    /**
     * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     * 本demo的包名是com.baidu.tts.sample，定义在build.gradle中。
     */
    protected var appId = "11517350"

    protected var appKey = "qHGMZlGLKzolML0SqKUD7GeY"

    protected var secretKey = "BbZQnEnP3G3y2Oj4vejoa0edLhk2u1tR "

    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    protected var ttsMode = TtsMode.MIX

    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
    protected var offlineVoice = App.app!!.dsp!!.getString("voice_type",OfflineResource.VOICE_MALE)

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================

    // 主控制类，所有合成控制方法从这个类开始
    public var synthesizer: MySyntherizer? = null


    companion object {
        private var speakVoiceUtil: SpeakUtil? = null
        protected var offlineVoice = OfflineResource.VOICE_MALE


        fun getInstance(context: Context): SpeakUtil {
            if (speakVoiceUtil == null) {
                synchronized(SpeakUtil::class.java) {
                    if (speakVoiceUtil == null) {
                        speakVoiceUtil = SpeakUtil(context)
                    }
                }
            }
            return speakVoiceUtil!!
        }
    }


    /**
     * 初始化引擎，需要的参数均在InitConfig类里
     *
     *
     * DEMO中提供了3个SpeechSynthesizerListener的实现
     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
     */
    public fun initialTts(msg: MessageListener = MessageListener()): Boolean {
        LoggerProxy.printable(true) // 日志打印在logcat中
        // 设置初始化参数

        Log.d("My",offlineVoice)
        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
        val listener = msg

        val params = getParams()


        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
        val initConfig = InitConfig(appId, appKey, secretKey, ttsMode, params, listener)

        // 如果您集成中出错，请将下面一段代码放在和demo中相同的位置，并复制InitConfig 和 AutoCheck到您的项目中
        // 上线时请删除AutoCheck的调用

        try {
            synthesizer = MySyntherizer(context, initConfig) // 此处可以改为MySyntherizer 了解调用过程

        }catch (e: Exception){

            return false
        }
        return true
    }

    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    protected fun getParams(): Map<String, String> {
        val params = HashMap<String, String>()
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params[SpeechSynthesizer.PARAM_SPEAKER] = when(offlineVoice){
            "M" -> "1"
            "F" -> "0"
            "X" -> "3"
            "Y" -> "4"
            else -> {
                "2"
            }
        }

        // 设置合成的音量，0-9 ，默认 5
        params[SpeechSynthesizer.PARAM_VOLUME] = App.app!!.dsp!!.getString("voice_volume","5")
        // 设置合成的语速，0-9 ，默认 5
        params[SpeechSynthesizer.PARAM_SPEED] = App.app!!.dsp!!.getString("voice_speed","5")
        // 设置合成的语调，0-9 ，默认 5
        params[SpeechSynthesizer.PARAM_PITCH] = App.app!!.dsp!!.getString("voice_pitch","5")

        params[SpeechSynthesizer.PARAM_MIX_MODE] = when(App.app!!.dsp!!.getString("mix_mode","MIX_MODE_DEFAULT")){
            "MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI" -> SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI
            "MIX_MODE_HIGH_SPEED_NETWORK" -> SpeechSynthesizer.MIX_MODE_HIGH_SPEED_NETWORK
            "MIX_MODE_HIGH_SPEED_SYNTHESIZE" -> SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE
            else -> {
                SpeechSynthesizer.MIX_MODE_DEFAULT
            }
        }



        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        val offlineResource = createOfflineResource(offlineVoice)
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params[SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE] = offlineResource.textFilename
        params[SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE] = offlineResource.modelFilename
        return params
    }

    protected fun createOfflineResource(voiceType: String): OfflineResource {
        var offlineResource: OfflineResource? = null
        try {
            offlineResource = OfflineResource(context, voiceType)
        } catch (e: IOException) {
            // IO 错误自行处理
            e.printStackTrace()
            // toPrint("【error】:copy files from assets failed." + e.message)
        }

        return offlineResource!!
    }

    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    public fun speak(text: String) {
        // mShowText.setText("")
        // 需要合成的文本text的长度不能超过1024个GBK字节。
        if(text.isEmpty())
            return
        // 合成前可以修改参数：
        // Map<String, String> params = getParams();
        // synthesizer.setParams(params);
        val result = synthesizer!!.speak(text)
        checkResult(result, "speak")

    }

    private fun checkResult(result: Int, method: String) {
        if (result != 0) {
            Log.v(TAG,"error code :$result method:$method, 错误码文档:http://yuyin.baidu.com/docs/tts/122 ")
        }
    }

    // 释放资源
    public fun release(){
        Log.d("My","ssss")
        if(this.synthesizer != null) {
            this.synthesizer!!.release()
            this.synthesizer = null
            speakVoiceUtil = null
            Log.d("My","Speak释放")
        }

    }


}