package com.demo.cjh.signin.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.Camera
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import com.arcsoft.ageestimation.ASAE_FSDKFace
import com.arcsoft.facerecognition.AFR_FSDKEngine
import com.arcsoft.facerecognition.AFR_FSDKFace
import com.arcsoft.facerecognition.AFR_FSDKMatching
import com.arcsoft.facerecognition.AFR_FSDKVersion
import com.arcsoft.facetracking.AFT_FSDKEngine
import com.arcsoft.facetracking.AFT_FSDKFace
import com.arcsoft.facetracking.AFT_FSDKVersion
import com.arcsoft.genderestimation.ASGE_FSDKFace
import com.demo.cjh.signin.App
import com.demo.cjh.signin.R
import com.demo.cjh.signin.pojo.FaceRegist
import com.demo.cjh.signin.util.FaceDB
import com.guo.android_extend.java.AbsLoop
import com.guo.android_extend.java.ExtByteArrayOutputStream
import com.guo.android_extend.tools.CameraHelper
import com.guo.android_extend.widget.CameraFrameData
import com.guo.android_extend.widget.CameraGLSurfaceView
import com.guo.android_extend.widget.CameraSurfaceView
import kotlinx.android.synthetic.main.activity_sign_in_by_face.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import java.io.IOException
import java.util.ArrayList

/**
 * 人脸检测界面
 */
class SignInByFace : AppCompatActivity() , CameraSurfaceView.OnCameraListener, Camera.AutoFocusCallback , View.OnTouchListener {

    val TAG = "SignInByFace"


    private var mWidth: Int = 0
    private var mHeight:Int = 0
    private var mFormat:Int = 0
    private var mCamera: Camera? = null

    /**
     * 人脸追踪
     */
    internal var version = AFT_FSDKVersion()
    internal var engine = AFT_FSDKEngine()

    /**
     * 追踪到人脸的数据
     */
    internal var result: MutableList<AFT_FSDKFace> = ArrayList()
    internal var results: MutableList<AFT_FSDKFace> = ArrayList()

    internal var mCameraID: Int = 0
    internal var mCameraRotate: Int = 0
    internal var mCameraMirror: Boolean = false
    internal var mImageNV21: ByteArray? = null

    private var camera_switch: ImageButton? = null
    private lateinit var faceAdapter: FaceAdapter
    private val stus = ArrayList<String>()
    private val stuIds = ArrayList<String>()

    internal lateinit var mFRAbsLoop: FRAbsLoop
    // 当前检测脸
    internal lateinit var mAFT_FSDKFace: AFT_FSDKFace

    internal inner class FRAbsLoop() : AbsLoop() {

        /**
         * AFR 人脸识别
         */
        var version = AFR_FSDKVersion()
        // 人脸识别功能
        var engine = AFR_FSDKEngine()
        // 保存人脸信息
        var mResult = AFR_FSDKFace()
        var mResgist: ArrayList<FaceRegist> = App.getRegister()
        var face1: MutableList<ASAE_FSDKFace> = ArrayList()
        var face2: MutableList<ASGE_FSDKFace> = ArrayList()

        override fun setup() {
            var error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key)
            Log.d(TAG, "AFR_FSDK_InitialEngine = " + error.code)
            error = engine.AFR_FSDK_GetVersion(version)
            Log.d(TAG, "FR=" + version.toString() + "," + error.code) //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
        }

        override fun loop() {
            synchronized(this) {

                if (mResgist.isEmpty()){
                    alert {
                        message = "没有搜索到脸库"
                        positiveButton("确定"){
                            finish()
                        }
                        isCancelable = false
                    }.show()
                }

                if (mImageNV21 != null) {

                    for (mAFT_FSDKFace in results) {
                        val time = System.currentTimeMillis()

                        var error = engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21, mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree(), mResult)
                        Log.d(TAG, "AFR_FSDK_ExtractFRFeature cost :" + (System.currentTimeMillis() - time) + "ms")
                        Log.d(TAG, "Face=" + mResult.featureData[0] + "," + mResult.featureData[1] + "," + mResult.featureData[2] + "," + error.code)
                        val score = AFR_FSDKMatching()
                        var max = 0.0f
                        var name: String? = null
                        var id: String? = null
                        var flag = false
                        for (fr in mResgist) {

                            if(fr.face1 != null) {
                                error = engine.AFR_FSDK_FacePairMatching(mResult, fr.face1, score)
                                Log.d(TAG, "Score:" + score.score + ", AFR_FSDK_FacePairMatching=" + error.code)
                                if (max < score.score) {
                                    max = score.score
                                    name = fr.name
                                    id = fr.stuId
                                }
                            }

                            if(fr.face2 != null) {
                                error = engine.AFR_FSDK_FacePairMatching(mResult, fr.face2, score)
                                Log.d(TAG, "Score:" + score.score + ", AFR_FSDK_FacePairMatching=" + error.code)
                                if (max < score.score) {
                                    max = score.score
                                    name = fr.name
                                    id = fr.stuId
                                }
                            }

                            if(fr.face3 != null) {
                                error = engine.AFR_FSDK_FacePairMatching(mResult, fr.face3, score)
                                Log.d(TAG, "Score:" + score.score + ", AFR_FSDK_FacePairMatching=" + error.code)
                                if (max < score.score) {
                                    max = score.score
                                    name = fr.name
                                    id = fr.stuId
                                    Log.v(TAG, name)
                                }
                            }


                            if (stuIds.any { it == id }) {
                                // 如果存在
                                flag = true
                                Log.v(TAG,name+"存在")
                            }
                        }

                        //age & gender
                        face1.clear()
                        face2.clear()
                        if (flag) {

                            // 跳过此张脸
                            continue
                        }
                        //                face1.add(new ASAE_FSDKFace(mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree()));
                        //                face2.add(new ASGE_FSDKFace(mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree()));
                        //                ASAE_FSDKError error1 = mAgeEngine.ASAE_FSDK_AgeEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face1, ages);
                        //                ASGE_FSDKError error2 = mGenderEngine.ASGE_FSDK_GenderEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face2, genders);
                        //                Log.d(TAG, "ASAE_FSDK_AgeEstimation_Image:" + error1.getCode() + ",ASGE_FSDK_GenderEstimation_Image:" + error2.getCode());
                        //                Log.d(TAG, "age:" + ages.get(0).getAge() + ",gender:" + genders.get(0).getGender());
                        //                final String age = ages.get(0).getAge() == 0 ? "年龄未知" : ages.get(0).getAge() + "岁";
                        //                final String gender = genders.get(0).getGender() == -1 ? "性别未知" : (genders.get(0).getGender() == 0 ? "男" : "女");

                        //crop
                        val data = mImageNV21
                        val yuv = YuvImage(data, ImageFormat.NV21, mWidth, mHeight, null)
                        val ops = ExtByteArrayOutputStream()
                        yuv.compressToJpeg(mAFT_FSDKFace!!.getRect(), 80, ops)
                        val bmp = BitmapFactory.decodeByteArray(ops.byteArray, 0, ops.byteArray.size)
                        try {
                            ops.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        Log.d(TAG, "fit Score:$max")
                        if (max > 0.6f) {
                            //fr success.
                            val max_score = max
                            Log.d(TAG, "fit Score:$max, NAME:$name")
                            val mNameShow = name
                            val mId = id

                            runOnUiThread {
                                stus.add(mNameShow!!)
                                stuIds.add(mId!!)
                                Toast.makeText(applicationContext, "识别成功", Toast.LENGTH_SHORT).show()
                                faceAdapter.notifyDataSetChanged()
                                if(stuIds.size == mResgist.size){
                                    alert {
                                        message = "已录入脸库的学生全部签到完成"
                                        positiveButton("确定"){
                                            val intent = Intent()
                                            intent.putExtra("stuIds",stuIds)
                                            setResult(Activity.RESULT_OK,intent)
                                            Log.v(TAG,"回传数据："+stuIds.size)
                                            finish()
                                        }
                                    }.show()
                                }
                            }
                        } else {
                            val mNameShow = "未识别"
                            runOnUiThread { Toast.makeText(applicationContext, "未识别", Toast.LENGTH_SHORT).show() }

                        }
                    }

                    // 清空
                    mImageNV21 = null
                }
            }

        }

        override fun over() {
            val error = engine.AFR_FSDK_UninitialEngine()
            Log.d(TAG, "AFR_FSDK_UninitialEngine : " + error.code)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_by_face)

        mCameraID = if (intent.getIntExtra("Camera", 0) == 0) Camera.CameraInfo.CAMERA_FACING_BACK else Camera.CameraInfo.CAMERA_FACING_FRONT
        mCameraRotate = if (intent.getIntExtra("Camera", 0) == 0) 0 else 0
        mCameraMirror = intent.getIntExtra("Camera", 0) != 0

        //通过Resources获取
        val dm = resources.displayMetrics
        mWidth = dm.widthPixels
        mHeight = dm.heightPixels
//        mWidth = 1280;
//        mHeight = 960;
        mFormat = ImageFormat.NV21

        setContentView(R.layout.activity_sign_in_by_face)

        mGLSurfaceView.setOnTouchListener(this)
        mGLSurfaceView.setAspectRatio(0.0)
//        mGLSurfaceView.setAspectRatio(mWidth, mHeight);

        mSurfaceView.setOnCameraListener(this)
        mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, mCameraMirror, mCameraRotate)
        mSurfaceView.debug_print_fps(true, false)

        val headView = layoutInflater.inflate(R.layout.item, null)
        val textView = headView.findViewById(R.id.text) as TextView
        textView.setTextColor(Color.RED)
        mListView.addHeaderView(headView)
        faceAdapter = FaceAdapter(this, stus)
        mListView.setAdapter(faceAdapter)

        // 初始化人脸追踪
        var err = engine.AFT_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5)
        Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.code)
        err = engine.AFT_FSDK_GetVersion(version)
        Log.d(TAG, "AFT_FSDK_GetVersion:" + version.toString() + "," + err.code)

        mFRAbsLoop = FRAbsLoop()
        mFRAbsLoop.start()

    }


    override fun onDestroy() {
        super.onDestroy()
        mFRAbsLoop.shutdown()
        val err = engine.AFT_FSDK_UninitialFaceEngine()
        Log.d(TAG, "AFT_FSDK_UninitialFaceEngine =" + err.code)

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK ){
            alert {
                message = "是否退出"
                positiveButton("是"){
                    val intent = Intent()
                    intent.putExtra("stuIds",stuIds)
                    setResult(Activity.RESULT_OK,intent)
                    Log.v(TAG,"回传数据："+stuIds.size)
                    finish()
                }
                negativeButton("否"){

                }
            }.show()
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
    override fun startPreviewLater(): Boolean {
        return false
    }

    override fun setupChanged(format: Int, width: Int, height: Int) {

    }

    override fun onBeforeRender(data: CameraFrameData?) {

    }

    override fun onPreview(data: ByteArray?, width: Int, height: Int, format: Int, timestamp: Long): Any {
        // 检测输入的图像中存在的人脸，输出结果和初始化时设置的参数有密切关系,输出到result
        // byte[] data 输入的图像数据
        // int width 图像宽度
        // int height 图像高度
        // int format 图像格式
        // List<AFT_FSDKFace> list 检测到的人脸会 add 到此 list.注意 AFT_FSDKFace 对象引擎内部重复使用,
        // 如需保存,请 clone一份 AFD_FSDKFace 对象或另外保存
        val err = engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, result)
        Log.d(TAG, "AFT_FSDK_FaceFeatureDetect =" + err.code)
        Log.d(TAG, "Face=" + result.size)
        for (face in result) {
            Log.d(TAG, "Face:result" + face.toString())
        }
        if (mImageNV21 == null) {
            if (!result.isEmpty()) {
                results.clear()
                for(face in result){
                    results.add(face.clone())
                }
                mAFT_FSDKFace = result[0].clone()

                Log.v(TAG,"face: "+results.size)
                mImageNV21 = data!!.clone()
            } else {
                //mHandler.postDelayed(hide, 3000);
            }
        }
        //copy rects
        val rects = arrayOfNulls<Rect>(result.size)
        for (i in result.indices) {
            rects[i] = Rect(result[i].rect)
        }
        //clear result.
        result.clear()
        //return the rects for render.
        return rects
    }

    override fun setupCamera(): Camera {
        mCamera = Camera.open(mCameraID)
        try {
            val parameters = mCamera!!.parameters
            parameters.setPreviewSize(mWidth, mHeight)
            parameters.previewFormat = mFormat

            for (size in parameters.supportedPreviewSizes) {
                Log.d(TAG, "SIZE:" + size.width + "x" + size.height)
            }
            for (format in parameters.supportedPreviewFormats) {
                Log.d(TAG, "FORMAT:" + format!!)
            }

            val fps = parameters.supportedPreviewFpsRange
            for (count in fps) {
                Log.d(TAG, "T:")
                for (data in count) {
                    Log.d(TAG, "V=$data")
                }
            }
            //parameters.setPreviewFpsRange(15000, 30000);
            //parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
            //parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            //parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
            //parmeters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            //parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
            //parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
            mCamera!!.setParameters(parameters)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (mCamera != null) {
            mWidth = mCamera!!.parameters.previewSize.width
            mHeight = mCamera!!.parameters.previewSize.height
        }
        return mCamera!!
    }

    override fun onAfterRender(data: CameraFrameData?) {
        // 绘制人脸框
        mGLSurfaceView.getGLES2Render().draw_rect(data!!.getParams() as Array<Rect>, Color.GREEN, 2)
    }

    override fun onAutoFocus(success: Boolean, camera: Camera?) {
        if (success) {
            Log.d(TAG, "Camera Focus SUCCESS!")
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        CameraHelper.touchFocus(mCamera, event, v, this)
        return false
    }

    internal inner class FaceAdapter(context: Context, data: List<String>) : BaseAdapter() {

        private var inflater: LayoutInflater? = null
        private var data: List<String>? = null

        init {
            inflater = LayoutInflater.from(context)
            this.data = data
        }

        override fun getCount(): Int {
            return data!!.size
        }

        override fun getItem(position: Int): Any {
            return data!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val holder: Holder
            var v: View? = null
            if (convertView == null) {
                v = inflater!!.inflate(R.layout.item, null)
                holder = Holder(v)
                //holder.name = v!!.findViewById(R.id.text) as TextView
                v.tag = holder
            } else {
                v = convertView
                holder = v.tag as Holder
            }
            holder.name.text = data!![position]



            return v!!
        }

        internal inner class Holder(val v: View) {
            var name = v.find<TextView>(R.id.text)

        }
    }

}
