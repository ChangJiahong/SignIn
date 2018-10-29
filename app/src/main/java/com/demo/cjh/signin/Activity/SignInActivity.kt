package com.demo.cjh.signin.Activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.demo.cjh.signin.App
import com.demo.cjh.signin.R
import com.demo.cjh.signin.obj.StudentInfo
import com.demo.cjh.signin.util.SpeakUtil
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.io.Serializable

class SignInActivity : AppCompatActivity(), View.OnClickListener {

    val TAG = "SignIn"

    /**
     * 当前数据索引
     */
    var index = 0
    /**
     * 当前数据对象
     */
    var item: StudentInfo? = null
    /**
     * 数据集合
     */
    var data: List<StudentInfo>? = null
    /**
     * 是否修改内容
     */
    var flag: Boolean = false

    /**
     * 自动显示下一个
     */
    var nextFlag: Boolean = false

    var speakUtil: SpeakUtil? = null

    val speakFalg = App.app!!.dsp!!.getBoolean("voice_is",true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        var i = intent.getIntExtra("code",0)
        if(i == 1){
            nextFlag = true
        }
        index  = intent.getIntExtra("position",0)

        data = intent.getSerializableExtra("data") as List<StudentInfo>

        show(index)

        init()
    }

    private fun init() {
        last.setOnClickListener {

            showLast()

        }

        next.setOnClickListener {

            showNext()

        }

        dao.setOnClickListener(this)

        chiDao.setOnClickListener(this)

        kuangKe.setOnClickListener(this)

        shiJia.setOnClickListener(this)

        bingJia.setOnClickListener(this)

        say_btn.setOnClickListener{
            speack()
        }

        initialButtons(false)

        doAsync {
            speakUtil = SpeakUtil.getInstance(this@SignInActivity)
            if(speakUtil!!.synthesizer == null) {

                if(!speakUtil!!.initialTts()){
                    // 加载初始化失败
                    runOnUiThread {
                        toast("鉴权失败，请检查网络稍后重试！")
                    }

                }else{
                    runOnUiThread {
                        initialButtons(true)
                        if(nextFlag) {
                            speack("开始点名:  ")
                        }
                        speack()
                    }
                }

            }

        }

    }

    override fun onClick(v: View?) {

        flag = true
        var type = check(v!!)
        data!![index].type = type


        if(nextFlag) {
            if (index >= data!!.size - 1) {
                toast("点完了")
                speack("点名结束")
            } else {
                progress.visibility = View.VISIBLE
                doAsync {
                    Thread.sleep(500)
                    runOnUiThread {
                        showNext()
                        progress.visibility = View.GONE
                    }
                }
            }
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK && flag){

            val intent = Intent()
            intent.putExtra("data",data as Serializable)
            setResult(Activity.RESULT_OK,intent)
            finish()

            return true

        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 根据控件选择
     */
    private fun check(v : View): String{

        unCheckAll()

        when(v.id){
            R.id.dao -> {
                dao.backgroundResource = R.drawable.hua
                return "出勤"
            }
            R.id.chiDao -> {
                chiDao.backgroundResource = R.drawable.hua
                return "迟到"
            }
            R.id.kuangKe -> {
                kuangKe.backgroundResource = R.drawable.hua
                return "旷课"
            }
            R.id.shiJia -> {
                shiJia.backgroundResource = R.drawable.hua
                return "事假"
            }
            R.id.bingJia -> {
                bingJia.backgroundResource = R.drawable.hua
                return "病假"
            }
        }
        return ""
    }

    /**
     * 根据type选择
     */
    private fun check(v: String?) {

        unCheckAll()

        when(v!!){
            "dao" -> dao.backgroundResource = R.drawable.hua
            "chiDao" -> chiDao.backgroundResource = R.drawable.hua
            "kuangKe" -> kuangKe.backgroundResource = R.drawable.hua
            "shiJia" -> shiJia.backgroundResource = R.drawable.hua
            "bingJia" -> bingJia.backgroundResource = R.drawable.hua
            else -> return
        }
    }

    /**
     * 全部不选
     */
    private fun unCheckAll(){
        dao.backgroundResource = R.drawable.hua1
        chiDao.backgroundResource = R.drawable.hua1
        kuangKe.backgroundResource = R.drawable.hua1
        shiJia.backgroundResource = R.drawable.hua1
        bingJia.backgroundResource = R.drawable.hua1
    }

    /**
     * 显示上一个
     */
    private fun showLast() {
        if(index<=0){
            toast("已经是第一个了")
        }else {
            index--
            show(index)
        }
        speack()

    }


    /**
     * 显示下一个
     */
    private fun showNext() {
        if(index >= data!!.size-1){
            toast("已经是最后一个了")
        }else {
            index++
            show(index)
        }
        speack()

    }


    /**
     * 刷新页面
     */
    private fun show(index : Int){
        item = data!![index]
        id.text = item!!.stuId
        name.text = item!!.name
        check(item!!.type)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sin_in,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val intent = Intent()
        intent.putExtra("data",data as Serializable)
        setResult(Activity.RESULT_OK,intent)
        finish()
        return super.onOptionsItemSelected(item)
    }

    fun speack(){
        if(!name.text.isEmpty()) {
            speack(name.text.toString())
        }
    }
    fun speack(text: String){
        if(speakFalg) {
            speakUtil!!.speak(text)
        }
    }

    private fun initialButtons(flag: Boolean) {

        dao.isEnabled = flag // 先禁用按钮，等待引擎初始化后打开。
        chiDao.isEnabled = flag
        kuangKe.isEnabled = flag
        shiJia.isEnabled = flag
        bingJia.isEnabled = flag
        last.isEnabled = flag
        next.isEnabled = flag
        say_btn.isEnabled = flag
    }

    override fun onDestroy() {
        super.onDestroy()
        if(speakUtil != null) {
            speakUtil!!.release()
            speakUtil = null
            Log.d("My","Sign释放")
        }
    }
}
