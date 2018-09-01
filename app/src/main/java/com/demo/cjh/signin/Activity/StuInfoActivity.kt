package com.demo.cjh.signin.Activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.arcsoft.facedetection.AFD_FSDKEngine
import com.arcsoft.facedetection.AFD_FSDKError
import com.arcsoft.facedetection.AFD_FSDKFace
import com.arcsoft.facedetection.AFD_FSDKVersion
import com.arcsoft.facerecognition.AFR_FSDKEngine
import com.arcsoft.facerecognition.AFR_FSDKError
import com.arcsoft.facerecognition.AFR_FSDKFace
import com.arcsoft.facerecognition.AFR_FSDKVersion
import com.demo.cjh.signin.App
import com.demo.cjh.signin.FileUtil
import com.demo.cjh.signin.R
import com.demo.cjh.signin.util.FaceDB
import com.demo.cjh.signin.util.NV21_to_bitmap
import com.demo.cjh.signin.util.database
import com.guo.android_extend.image.ImageConverter
import com.guo.android_extend.java.ExtByteArrayOutputStream
import kotlinx.android.synthetic.main.activity_stu_info.*
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*

class StuInfoActivity : AppCompatActivity(), View.OnClickListener {

    val TAG = "StuInfoActivity"

    private val REQUEST_CODE_IMAGE_CAMERA = 1  // 相机
    private val REQUEST_CODE_IMAGE_OP = 2  // 相册
    private val REQUEST_CODE_OP = 3 //

    var classId: String = ""
    var className: String = ""
    var stuId: String = ""
    var name: String = ""

    var faceNO = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_info)

        init()
    }

    private fun init() {
        classId = intent.getStringExtra("classId")
        className = intent.getStringExtra("name")
        stuId = intent.getStringExtra("stuId")
        name = intent.getStringExtra("name")

        mStuId.text = stuId
        mName.text = name
        mClassName.text = className

        face1.setOnClickListener(this)
        face2.setOnClickListener(this)
        face3.setOnClickListener(this)

        doAsync {
            var stu = database.queryFaces(classId, stuId)
            runOnUiThread {
                if (stu!!.face1 != null){
                    face1.setImageResource(R.drawable.ren)
                }
                if (stu.face2 != null){
                    face2.setImageResource(R.drawable.ren)
                }
                if (stu.face3 != null){
                    face3.setImageResource(R.drawable.ren)
                }
            }
        }






    }


    override fun onClick(v: View?) {

        when(v!!.id){
            R.id.face1 ->{
                // 人脸注册
                alertDialog()
                faceNO = 1
            }
            R.id.face2 ->{
                alertDialog()
                faceNO = 2
            }
            R.id.face3 ->{
                alertDialog()
                faceNO = 3
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_CODE_IMAGE_CAMERA ->{
                    // 相机注册
                    val mPath = App.getCaptureImage()
                    Log.d("TAG", "Path = $requestCode")
                    var path = ""
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                        path = FileUtil.getPath(this, mPath) ?:""
                    } else {//4.4以下下系统调用方法
                        path = FileUtil.getRealPathFromURI(this, mPath) ?:""
                    }


                    if(insertFace(path)) {
                        when (faceNO) {
                            1 -> face1.setImageResource(R.drawable.ren)
                            2 -> face2.setImageResource(R.drawable.ren)
                            3 -> face3.setImageResource(R.drawable.ren)
                        }
                    }





                }
                REQUEST_CODE_IMAGE_OP ->{
                    // 相册注册
                    val uri = data!!.data
                    var path = ""
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                        path = FileUtil.getPath(this, uri) ?:""
                    } else {//4.4以下下系统调用方法
                        path = FileUtil.getRealPathFromURI(this, uri) ?:""
                    }

                    if(insertFace(path)) {
                        when (faceNO) {
                            1 -> face1.setImageResource(R.drawable.ren)
                            2 -> face2.setImageResource(R.drawable.ren)
                            3 -> face3.setImageResource(R.drawable.ren)
                        }
                    }

                }

            }
        }
    }

    fun alertDialog(){
        AlertDialog.Builder(this@StuInfoActivity)
                .setTitle("请选择注册方式")
                .setItems(arrayOf("相册", "相机")) { dialog, which ->
                    when (which) {
                        1 -> {
                            // 打开相机
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                                if (ContextCompat.checkSelfPermission(this,
                                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){ //表示未授权时
                                    //进行授权
                                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),1)
                                }else{
                                    //调用方法
                                    startCamera()
                                }
                            }else{
                                startCamera()
                            }

                        }
                        0 -> {
                            // 打开相册
                            startImg()

                        }
                    }
                }.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            1 ->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){ //同意权限申请
                    startCamera()
                }else { //拒绝权限申请
                    Toast.makeText(this,"权限被拒绝了",Toast.LENGTH_SHORT).show()
                    //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),1)
                }
            }
        }
    }
    fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val values = ContentValues(1)
        // 设置图片保存路径 ，默认在Pictures
        values.put(MediaStore.Images.Media.DATA, FileUtil.imageDirectory+"/"+ SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())+".jpg")
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        App.setCaptureImage(uri)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_CODE_IMAGE_CAMERA)
    }

    fun startImg() {
        val intent =  Intent(Intent.ACTION_GET_CONTENT)
        //intent.setType(“image/*”);//选择图片
        //intent.setType(“audio/*”); //选择音频
        //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
        //intent.setType(“video/*;image/*”);//同时选择视频和图片
        intent.type = "image/*"//
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent,REQUEST_CODE_IMAGE_OP)
    }



    fun insertFace(path: String): Boolean{
        var flag: Boolean? = null
        val bmp = FileUtil.decodeImage(path)

        // 把图形格式转化为软虹SDK使用的图像格式NV21
        val data = ByteArray(bmp!!.getWidth() * bmp.getHeight() * 3 / 2)
        val convert = ImageConverter()
        convert.initial(bmp.getWidth(), bmp.getHeight(), ImageConverter.CP_PAF_NV21)
        if (convert.convert(bmp, data)) {
            Log.d(TAG, "convert ok!")
        }
        convert.destroy()

        val FD_engine = AFD_FSDKEngine()
        val FD_version = AFD_FSDKVersion()

        // 用来存放检测到的人脸信息列表
        val FD_result = ArrayList<AFD_FSDKFace>()

        // 初始化人脸检测引擎
        var FD_error = FD_engine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5)

        if (FD_error.code != AFD_FSDKError.MOK) {
            Toast.makeText(this@StuInfoActivity, "FD初始化失败，错误码：" + FD_error.code, Toast.LENGTH_SHORT).show()
            return false
        }

        // 输入的data数据为NV21格式，人脸检测返回结果保存在FD_result中
        FD_error = FD_engine.AFD_FSDK_StillImageFaceDetection(data, bmp.getWidth(), bmp.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, FD_result)


        if (!FD_result.isEmpty()) {

            // 检测人脸特征信息
            val FR_version1 = AFR_FSDKVersion()
            val FR_engine1 = AFR_FSDKEngine()

            // 存放人脸特征信息
            val FR_result1 = AFR_FSDKFace()

            // 初始化
            var FR_error1 = FR_engine1.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key)

            if (FR_error1.code != AFR_FSDKError.MOK) {
                Toast.makeText(this@StuInfoActivity, "FR初始化失败，错误码：" + FD_error.code, Toast.LENGTH_SHORT).show()
                return false
            }

            // 检测人脸特征
            FR_error1 = FR_engine1.AFR_FSDK_ExtractFRFeature(data, bmp.getWidth(), bmp.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, Rect(FD_result[0].rect), FD_result[0].degree, FR_result1)

            if (FR_error1.code != AFR_FSDKError.MOK) {
                Toast.makeText(this@StuInfoActivity, "人脸特征无法检测，请换一张图片", Toast.LENGTH_SHORT).show()
                return false
            } else {

                val mAFR_FSDKFace = FR_result1.clone() // 复制

                //  裁剪
                val width = FD_result[0].rect.width()
                val height = FD_result[0].rect.height()
                val face_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                val face_canvas = Canvas(face_bitmap)
                face_canvas.drawBitmap(bmp, FD_result[0].rect, Rect(0, 0, width, height), null)

                // 显示
                //face1.setImageBitmap(face_bitmap)


                // 添加人脸特征信息到脸库
                App.mFaceDB.addFace(classId,stuId,name,mAFR_FSDKFace,faceNO)

            }
            // 销毁
            FR_error1 = FR_engine1.AFR_FSDK_UninitialEngine()
            Log.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + FR_error1.getCode())

        } else {
            Toast.makeText(this@StuInfoActivity, "未检测到人脸", Toast.LENGTH_SHORT).show()
            return false
        }

        FD_error = FD_engine.AFD_FSDK_UninitialFaceEngine()
        Log.d(TAG, "AFD_FSDK_UninitialFaceEngine =" + FD_error.getCode())

        return true
    }
}
