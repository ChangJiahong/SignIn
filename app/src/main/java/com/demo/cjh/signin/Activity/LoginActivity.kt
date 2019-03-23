package com.demo.cjh.signin.Activity

import android.app.Activity
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.demo.cjh.signin.App
import com.demo.cjh.signin.R
import com.demo.cjh.signin.util.Http
import com.demo.cjh.signin.util.HttpHelper
import com.demo.cjh.signin.util.doHttp
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    val TAG = "LoginActivity"


    var _account  = ""
    var _password = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
    }

    private fun init() {

        val sp = App.app.sp

        _account  = sp.getString("userid","")
        _password = sp.getString("pwd","")

        account.setText(_account)
        password.setText(_password)


        login.setOnClickListener{
            if(currentFocus !=null)
            {
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(currentFocus
                                .getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
            }

            _account  = account.text.toString()
            _password = password.text.toString()

            var focusView : View? = null
            var cancel = false

            if (!TextUtils.isEmpty(_password) && _password.length < 8) {
                password.error = "密码太短了"
                focusView = password
                cancel = true
            }

            if(TextUtils.isEmpty(account.text)){
                account.error = "邮箱不为空"
                focusView = account
                cancel = true
            }else if(TextUtils.isEmpty(password.text)){
                password.error = "密码不为空"
                focusView = password
                cancel = true
            }else if(!isEmail(_account)){
                account.error = "不是有效的邮箱账号"
                focusView = account
                cancel = true
            }

            if(cancel){
                focusView!!.requestFocus()
            }else{

                showProgress(true)

                doHttp {
                    url = HttpHelper.login
                    params {
                        "username"-_account
                        "password"-_password
                    }
                    success { status, msg, data ->
                        //
                        when(status){
                            200 ->{
                                toast("登陆成功")

                                val js = data as LinkedTreeMap<String, String>
                                val name = data["name"]
                                val userToken = data["token"]
                                val imgUrl = data["img"]

                                sp.edit().apply {
                                    putString("userId", _account)
                                    putString("name", name)
                                    putString("token", userToken)
                                    putString("img", imgUrl)
                                    putString("pwd", _password)
                                    putBoolean("isLogin", true)
                                    apply()
                                }
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                            else ->{
                                // 登陆失败
                                toast(msg)
                                sp.edit().apply {
                                    putBoolean("isLogin", false)
                                    apply()
                                }
                            }
                        }
                    }
                    error {
                        runOnUiThread {
                            toast("服务器错误，错误码：$it")
                        }
                    }
                }.start()
                //val loginTask = LoginTask()
               // loginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, _account,_password)


            }
        }

    }

    fun register(v: View){
        startActivity<RegisterActivity>()
    }

    public fun showProgress(show : Boolean){
        progressView.visibility = if(show) View.VISIBLE else View.GONE
    }
    private fun isMobileNO(mobiles: String): Boolean {
        val p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$")
        val m = p.matcher(mobiles)
        return m.matches()
    }


    fun isEmail(string: String?): Boolean {
        if (string == null)
            return false
        val regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"
        val p: Pattern
        val m: Matcher
        p = Pattern.compile(regEx1)
        m = p.matcher(string)
        return m.matches()
    }

}
