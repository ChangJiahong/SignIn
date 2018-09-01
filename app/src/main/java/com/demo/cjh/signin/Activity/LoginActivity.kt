package com.demo.cjh.signin.Activity

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.demo.cjh.signin.R
import com.demo.cjh.signin.util.Http
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    val TAG = "LoginActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
    }

    private fun init() {

        login.setOnClickListener{
            var _account  = account.text.toString()
            var _password = password.text.toString()

            var focusView : View? = null
            var cancel = false

            if (!TextUtils.isEmpty(_password) && _password.length > 6) {
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
                doAsync {
                    var jsonString = Http.login(_account,_password)
                    var jsonObject = JSONObject(jsonString)
                    var status = jsonObject.getInt("status")
                    when(status){
                        1 ->{
                            // 登陆成功
                            toast("登陆成功")
                            var data = jsonObject.getString("data")
                            finish()
                        }
                        0 ->{
                            // 登陆失败
                            var msg = jsonObject.getString("message")
                            toast(msg)
                        }
                    }
                    runOnUiThread {
                        showProgress(false)
                    }
                }

//                thread(start = true){
//                    Thread.sleep(3000)
//                    // 登录处理
//                    if(_account == "18855486127"  && _password == "123456"){
//                        startActivity<MainActivity>()
//                    }else{
//                        toast("用户名或密码错误")
//                    }
//                    runOnUiThread {
//                        showProgress(false)
//                    }
//                }

                val loginTask = LoginTask()
                loginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, _account,_password)


            }
        }

    }

    public fun showProgress(show : Boolean){
        progressView.visibility = if(show) View.VISIBLE else View.GONE
    }
    private fun isMobileNO(mobiles: String): Boolean {
        val p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$")
        val m = p.matcher(mobiles)
        return m.matches()
    }

    inner class LoginTask : AsyncTask<String, Void, Boolean>() {
        override fun doInBackground(vararg params: String?): Boolean {
            Thread.sleep(3000)
            Log.v(TAG,params.first()!!)
            if(params[0] == "18855486127"  && params[1] == "123456"){
                return true
            }
            return false
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            showProgress(false)
            if(result!!){
                startActivity<MainActivity>()
                finish()
            }else{
                toast("用户名或密码错误")
            }
        }

    }
}
