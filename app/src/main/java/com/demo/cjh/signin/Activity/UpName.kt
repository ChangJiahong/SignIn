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
import kotlinx.android.synthetic.main.activity_up_name.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

class UpName : AppCompatActivity() {


    val TAG = "UpName"


    var flag = false
    val sp = App.app.sp


    override fun onStart() {
        super.onStart()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_up_name)



        name.setText(App.app.user.name)

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
        when(item.itemId){
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


       // 修改姓名逻辑

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
