package com.demo.cjh.signin.Activity

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.demo.cjh.signin.App
import com.demo.cjh.signin.R
import com.demo.cjh.signin.util.Http.upName
import com.demo.cjh.signin.util.getreslut
import kotlinx.android.synthetic.main.activity_up_name.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.json.JSONObject

class UpName : AppCompatActivity() {


    val TAG = "UpName"


    var flag = false
    val sp = App.app!!.sp!!


    override fun onStart() {
        super.onStart()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_up_name)



        name.setText(App.app!!.user.name)

        name.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                flag = true
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Log.v("UpName","menu onClick ${item!!.itemId}")
        when(item!!.itemId){
            R.id.save ->{
                if(flag) {
                    save()
                }else{
                    finish()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun save(){
        if(currentFocus !=null)
        {
            (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(currentFocus
                            .windowToken,
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }

        if(name.text.isNullOrEmpty()){
            alert{
                title = "提示"
                message = "没有输入昵称，请重新填写"
                positiveButton("是") {

                }
            }
            return
        }
        progressView.visibility = View.VISIBLE

        doAsync {
            var resultString = upName(name.text.toString())

            uiThread {
                progressView.visibility = View.GONE
                var result = getreslut(resultString)
                if(result.status == 1) {
                    toast("修改成功")

                    val jsonObject = JSONObject(result.data)

                    sp.edit().apply{
                        putString("name",jsonObject.getString("name"))
                        putString("userToken",jsonObject.getString("userToken"))
                        apply()
                    }

                    setResult(Activity.RESULT_OK)
                    finish()
                }else{
                    toast(result.msg)
                }
            }
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK && flag){

            alert {
                title = "提示"
                message = "检测到你已修改名称，是否保存"
                positiveButton("是") {

                    save()


                }
                negativeButton("否"){
                    finish()
                }

                }.show()

            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}
