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
import android.widget.RadioButton
import com.demo.cjh.signin.App
import com.demo.cjh.signin.R
import com.demo.cjh.signin.listener.MessageListener
import com.demo.cjh.signin.pojo.StuInfo
import com.demo.cjh.signin.service.ITypeService
import com.demo.cjh.signin.service.impl.TypeServiceImpl
import com.demo.cjh.signin.util.SpeakUtil
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.*
import org.jetbrains.anko.collections.forEachByIndex

class SignInActivity : AppCompatActivity(), View.OnClickListener {

    val TAG = "SignIn"

    /**
     * 当前数据索引
     */
    var index = 0
    /**
     * 当前数据对象
     */
    lateinit var item: StuInfo
    /**
     * 数据集合
     */
    lateinit var data: ArrayList<StuInfo>
    /**
     * 是否修改内容
     */
    var flag: Boolean = false

    /**
     * 自动显示下一个
     */
    var nextFlag: Boolean = false

    lateinit var speakUtil: SpeakUtil

    lateinit var typeId: String

    val speakFalg = App.app.dsp.getBoolean("voice_is",true)

    /**
     * 状态选项
     */
    private lateinit var status: Array<String>

    private lateinit var typeService: ITypeService

    private lateinit var btns: Array<RadioButton>

    private var pflag = false

    // 加载btn完成
    private var overView = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // 默认0
        // 1 自动显示下一个
        var i = intent.getIntExtra("action",0)
        if(i == 1){
            nextFlag = true
        }
        index  = intent.getIntExtra("position",0)

        data = intent.getSerializableExtra("data") as ArrayList<StuInfo>

        typeId = intent.getStringExtra("typeId")


        init()
    }

    private fun init() {

        db_init()

        init_view()


        doAsync {

            while (!overView)

            speakUtil = SpeakUtil.getInstance(this@SignInActivity)
            if(speakUtil.synthesizer == null) {

                if(!speakUtil.initialTts(msg)){
                    // 加载初始化失败
                    runOnUiThread {
                        toast("鉴权失败，请检查网络稍后重试！")
                    }

                }else{
                    runOnUiThread {
                        if(nextFlag) {
                            speack("开始点名:  ")
                        }
                        speack()
                        initialButtons(true)
                    }
                }

            }

        }

    }

    /**
     * 数据库设置并初始化服务
     */
    fun db_init(){
        typeService = TypeServiceImpl(this)
    }

    private fun init_view() {

        last.setOnClickListener(this)

        next.setOnClickListener(this)

        say_btn.setOnClickListener(this)


        doAsync {
            status = typeService.getKeysByTypeId(typeId).toTypedArray()
            uiThread {
                btns = Array(status.size){
                   RadioButton(this@SignInActivity)
                }
                for((i,v) in status.withIndex()) {
                    var view = btns[i]
                    view.text = v
                    view.textSize = 30f
                    view.id = i
                    radioGroup.addView(view)
                }

                radioGroup.setHorizontalSpace(15)
                radioGroup.setVerticalSpace(15)
                radioGroup.setOnCheckedChangeListener { group, checkedId ->
                    if(pflag){
                        // 非人为更改 返回
                        return@setOnCheckedChangeListener
                    }
                    btns.forEachIndexed { i, radioButton ->
                        if (radioButton.id == checkedId){
                            // 设置 选中的值
                            item.status = i.toString()
                        }
                    }
                    // 修改标志
                    flag = true
                    if(nextFlag) {
                        if (index >= data.size - 1) {
                            toast("点完了")
                            speack("点名结束")
                        } else {
//                            progress.visibility = View.VISIBLE
//                            doAsync {
//                                Thread.sleep(500)
//                                runOnUiThread {
//                                    showNext()
//                                    progress.visibility = View.GONE
//                                }
//                            }
                            showNext()
                        }
                    }
                }

                /**
                 * 显示界面
                 */
                show(index)

                overView = true
                initialButtons(false)



            }


        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.last -> showLast()
            R.id.next -> showNext()
            R.id.say_btn -> speack()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK && flag){

            val intent = Intent()
            intent.putExtra("data",data)
            setResult(Activity.RESULT_OK,intent)
            Log.v(TAG,"更改退出")
            finish()

            return true

        }
        Log.v(TAG,"未更改退出")
        return super.onKeyDown(keyCode, event)
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
        if(index >= data.size-1){
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
        item = data[index]
        id.text = item.stuId
        name.text = item.stuName
        pflag = true
        if(item.status != "-1") {
            btns[item.status.toInt()].isChecked = true
        }else{
            radioGroup.clearCheck()
        }
        pflag = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sin_in,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val intent = Intent()
        intent.putExtra("data",data)
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
            speakUtil.speak(text)
        }
    }

    val msg = object : MessageListener(){

        override fun onSynthesizeStart(utteranceId: String?) {
            super.onSynthesizeStart(utteranceId)
            runOnUiThread {
                progress.visibility = View.VISIBLE
            }
        }
        override fun onSynthesizeFinish(utteranceId: String?) {
            super.onSynthesizeFinish(utteranceId)
            runOnUiThread {
                progress.visibility = View.GONE
            }
        }

        override fun onSpeechStart(utteranceId: String?) {
            super.onSpeechStart(utteranceId)
            runOnUiThread {
                initialButtons(false)
            }

        }

        override fun onSpeechFinish(utteranceId: String?) {
            super.onSpeechFinish(utteranceId)
            runOnUiThread {
                initialButtons(true)
            }

        }
    }
    private fun initialButtons(flag: Boolean) {

        btns.forEachByIndex {
            it.isEnabled = flag
        }
        last.isEnabled = flag
        next.isEnabled = flag
        say_btn.isEnabled = flag
    }

    override fun onDestroy() {
        super.onDestroy()
        if(speakUtil != null) {
            speakUtil.release()
            //speakUtil = null
            Log.d("My","Sign释放")
        }
    }
}
