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
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.net.SocketTimeoutException
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

        val sp = App.app!!.sp!!

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
                account.error = "手机号不为空"
                focusView = account
                cancel = true
            }else if(TextUtils.isEmpty(password.text)){
                password.error = "密码不为空"
                focusView = password
                cancel = true
            }else if(!isMobileNO(_account)){
                account.error = "手机号不正确"
                focusView = account
                cancel = true
            }

            if(cancel){
                focusView!!.requestFocus()
            }else{

                showProgress(true)

                val loginTask = LoginTask()
                loginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, _account,_password)


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

    inner class LoginTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            Thread.sleep(3000)
            Log.v(TAG,params.first()!!)

            try {

                var jsonString = Http.login_by_pwd(userid = params[0]!!,pwd = params[1]!!)
                return jsonString
            } catch (e: SocketTimeoutException){
                return "timeOut"
            }


        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            showProgress(false)

            Log.v(TAG, result!!)
            if (result.equals("timeOut")) {
                // 服务器响应超时
                toast("服务器响应超时")

            } else {
                var jsonObject = JSONObject(result!!)
                var status = jsonObject.getInt("status")
                when (status) {
                    1 -> {
                        // 登陆成功
                        toast("登陆成功")
                        var data = jsonObject.getString("data")
                        var jsonObject = JSONObject(data)
                        var name = jsonObject.getString("name")
                        var userToken = jsonObject.getString("userToken")
                        var imgUrl = jsonObject.getString("imgUrl")
                        var sp = App.app!!.sp!!
                        sp.edit().apply {
                            putString("userid", _account)
                            putString("name", name)
                            putString("userToken", userToken)
                            putString("imgUrl", imgUrl)
                            putString("pwd", _password)
                            putBoolean("isLogin", true)
                            apply()
                        }
                        setResult(Activity.RESULT_OK)
                        finish()

                    }
                    0 -> {
                        // 登陆失败
                        var msg = jsonObject.getString("message")
                        toast(msg)
                        var sp = App.app!!.sp!!
                        sp.edit().apply {
                            putBoolean("isLogin", false)
                            apply()
                        }
                    }
                }

            }
        }

    }
}
