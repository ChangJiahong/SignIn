package com.demo.cjh.signin.Activity

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.demo.cjh.signin.App
import com.demo.cjh.signin.R
import com.demo.cjh.signin.util.HttpHelper
import com.demo.cjh.signin.util.doHttp
import kotlinx.android.synthetic.main.activity_up_pwd.*
import org.jetbrains.anko.toast

class UpPwd : AppCompatActivity() {

    val TAG = "UpPwd"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_up_pwd)

        title = "设置密码"


    }

    fun save(){
        if(getCurrentFocus()!=null)
        {
            (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(getCurrentFocus()
                            .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }

        Log.v(TAG,"pwd ="+App.app.user.pwd+"  ppp = "+oldPwd.text)
        if(oldPwd.text.toString() == App.app.user.pwd){
            if(newPwd.text.toString() == aPwd.text.toString()){
                if(newPwd.text.length in 8..16){

                    if(newPwd.text.toString().toCharArray().any { it in 'a'..'z' || it in 'A'..'Z'  }) {
                        if(newPwd.text.toString() != oldPwd.text.toString()) {
                            progressView.visibility = View.VISIBLE

                            doHttp {
                                url = HttpHelper.alterPwd
                                params {
                                    "pwd"-newPwd.text.toString()
                                }
                                success { status, msg, data ->
                                    progressView.visibility = View.GONE
                                    if (status == 200){
                                        toast("修改成功")
                                        val sp = App.app.sp
                                        sp.edit().apply{
                                            putString("pwd",newPwd.text.toString())
                                            apply()
                                        }
                                        finish()
                                    }else{
                                        toast(msg)
                                    }
                                }
                                error {
                                    progressView.visibility = View.GONE
                                    toast("修改失败$it")
                                }
                            }.start()

                        }else{
                            aPwd.error = "新密码不能与原密码相同"
                            aPwd.requestFocus()
                        }
                    }else{
                        aPwd.error = "新密码不能为纯数字"
                        aPwd.requestFocus()
                    }
                }else{
                    aPwd.error = "新密码8-16位数之间"
                    aPwd.requestFocus()
                }

            }else{
                aPwd.error = "两次密码不相同"
                aPwd.requestFocus()
            }
        }else{
            oldPwd.error = "原密码不正确"
            oldPwd.requestFocus()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.save ->{

                save()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
