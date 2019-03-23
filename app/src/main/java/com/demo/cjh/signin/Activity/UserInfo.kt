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
import com.demo.cjh.signin.util.*
import com.demo.cjh.signin.util.PhotoUtil.IMAGE_UNSPECIFIED
import kotlinx.android.synthetic.main.activity_user_info.*
import org.jetbrains.anko.startActivity
import java.io.File



class UserInfo : AppCompatActivity() , View.OnClickListener{

    val sp = App.app.sp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        title = "个人信息"


        init()

    }

    private fun init() {
        toux.setOnClickListener(this)
        userid.setOnClickListener(this)
        uname.setOnClickListener(this)
        pwd.setOnClickListener(this)
        exit.setOnClickListener(this)

        val imgUrl = sp.getString("img","")
        val uid = sp.getString("userId","")
        val uname = sp.getString("name","")
        Log.d("img",imgUrl)


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
                    putString("img","")
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

                    imgpath = PhotoUtil.getPath(this)// 生成一个地址用于存放剪辑后的图片

                    if (TextUtils.isEmpty(imgpath)) {
                        Log.e("Login", "随机生成的用于存放剪辑后的图片的地址失败")
                        return
                    }
                    val imageUri = Uri.fromFile(File(imgpath))
                    PhotoUtil.startPhotoZoom(this@UserInfo, uri, PhotoUtil.PICTURE_HEIGHT, PhotoUtil.PICTURE_WIDTH, imageUri)
                }


                3 -> {
                    // 裁剪图片处理结果

                    // 上传图片

                }

                2 ->{
                    // 更改用户名
                    name.text = App.app.user.name
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
