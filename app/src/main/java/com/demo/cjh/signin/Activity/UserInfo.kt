package com.demo.cjh.signin.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.bumptech.glide.Glide
import com.demo.cjh.signin.App
import com.demo.cjh.signin.R
import com.demo.cjh.signin.util.Http
import com.demo.cjh.signin.util.PhotoUtil
import com.demo.cjh.signin.util.PhotoUtil.IMAGE_UNSPECIFIED
import com.demo.cjh.signin.util.getreslut
import kotlinx.android.synthetic.main.activity_table2_item.view.*
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.android.synthetic.main.list_my_item.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.File



class UserInfo : AppCompatActivity() , View.OnClickListener{

    val sp = App.app!!.sp!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        setTitle("个人信息")


        init()

    }

    private fun init() {
        toux.setOnClickListener(this)
        userid.setOnClickListener(this)
        uname.setOnClickListener(this)
        pwd.setOnClickListener(this)
        exit.setOnClickListener(this)

        val imgUrl = sp.getString("imgUrl","")
        val uid = sp.getString("userid","")
        val uname = sp.getString("name","")


        Glide.with(applicationContext).load(imgUrl).into(user_image)
        telnum.text = uid
        name.text = uname

    }


    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.toux ->{
                // 更换头像
                startImg()
            }
            R.id.userid ->{
                // 更换手机号

            }
            R.id.uname ->{
                // 修改用户名
                var intent = Intent(this, UpName::class.java)

                startActivityForResult(intent, 2)
            }
            R.id.pwd ->{
                // 更改密码
                startActivity<UpPwd>()
            }
            R.id.exit ->{
                // 退出当前账号

                sp.edit().apply{
                    putBoolean("isLogin",false)
                    putString("pwd","")
                    putString("userToken","")
                    putString("imgUrl","")
                    putString("name","")
                    apply()
                }
                setResult(Activity.RESULT_OK)
                finish()
            }

        }
    }

    // 打开相册
    fun startImg() {

        val intent = Intent(Intent.ACTION_PICK, null)
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_UNSPECIFIED)

//        val intent =  Intent(Intent.ACTION_GET_CONTENT)
//        //intent.setType(“image/*”);//选择图片
//        //intent.setType(“audio/*”); //选择音频
//        //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
//        //intent.setType(“video/*;image/*”);//同时选择视频和图片
//        intent.type = "image/*"//
//        intent.addCategory(Intent.CATEGORY_OPENABLE)

        startActivityForResult(intent,1)
    }

    var imgpath = ""

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode ){
                1 ->{
                    // 打开相册注册
                    val uri = data!!.data
//                var path = ""
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
//                    path = FileUtil.getPath(this, uri) ?:""
//                } else {//4.4以下下系统调用方法
//                    path = FileUtil.getRealPathFromURI(this, uri) ?:""
//                }

                    imgpath = PhotoUtil.getPath(this)// 生成一个地址用于存放剪辑后的图片
                    if (TextUtils.isEmpty(imgpath)) {
                        Log.e("Login", "随机生成的用于存放剪辑后的图片的地址失败")
                        return
                    }
                    val imageUri = Uri.fromFile(File(imgpath))
                    PhotoUtil.startPhotoZoom(this@UserInfo, uri, PhotoUtil.PICTURE_HEIGHT, PhotoUtil.PICTURE_WIDTH, imageUri)
                }


                3 ->{
                    // 裁剪图片处理结果

                    doAsync {
                        var resultString =  Http.upImg(imgpath)
                        var result = getreslut(resultString)
                        if(result.status == 1){
                            // 修改成功
                            // 更新imgUrl

                            val jsonObject = JSONObject(result.data)

                            sp.edit().apply{
                                putString("imgUrl",jsonObject.getString("imgUrl"))
                                putString("userToken",jsonObject.getString("userToken"))
                                apply()
                            }

                            uiThread {
                                /**
                                 * 如，根据path获取剪辑后的图片
                                 */
                                val bitmap = PhotoUtil.convertToBitmap(imgpath, PhotoUtil.PICTURE_HEIGHT, PhotoUtil.PICTURE_WIDTH)
                                if (bitmap != null) {
                                    //tv2.setText(bitmap.getHeight()+"x"+bitmap.getWidth()+"图");
                                    user_image.setImageBitmap(bitmap)
                                    //Glide.with(context).load(img).into(id_my_icon)
                                }

                                toast("修改成功")

                            }
                        }else{
                            // 不成功
                            uiThread {
                                toast(result.msg)
                            }

                        }
                    }
                }

                2 ->{
                    // 更改用户名
                    name.text = App.app!!.user.name
                }
            }


        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK ){
            setResult(Activity.RESULT_OK)
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }
}
