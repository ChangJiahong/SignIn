package com.demo.cjh.signin.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.demo.cjh.signin.R
import kotlinx.android.synthetic.main.activity_register.*

/**
 * 注册页面
 */
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        register.setOnClickListener {
            var uname = name.text.toString()
            var uid = account.text.toString()
            var upassword1 = password.text.toString()
            var upassword2 = apassword.text.toString()

            if(uname.isNullOrEmpty()){
                name.error = "昵称不为空"
                name.requestFocus()
            }else if(uid.isNullOrEmpty()){
                account.error = "手机号不为空"
                account.requestFocus()
            }else if(upassword1.isNullOrEmpty()){
                password.error = "密码不为空"
                password.requestFocus()
            }else if(upassword2.isNullOrEmpty()){
                apassword.error = "密码不为空"
                apassword.requestFocus()
            }else if(upassword1 != upassword2){
                apassword.error = "两次密码不不相同"
                apassword.requestFocus()
            }else{
                if(upassword1.length in 8..16){

                    if(upassword1.toCharArray().any { it in 'a'..'z' || it in 'A'..'Z'  }) {


                        // 注册逻辑

                    }else{
                        password.error = "新密码不能为纯数字"
                        password.requestFocus()
                    }
                }else{
                    password.error = "新密码8-16位数之间"
                    password.requestFocus()
                }
            }
        }

    }


}
