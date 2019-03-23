package com.demo.cjh.signin.Activity

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bin.david.form.utils.DensityUtils
import com.demo.cjh.signin.R
import kotlinx.android.synthetic.main.activity_add_type.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import com.bumptech.glide.Glide
import com.demo.cjh.signin.pojo.Result
import com.demo.cjh.signin.pojo.Type
import com.demo.cjh.signin.service.ITypeService
import com.demo.cjh.signin.service.impl.TypeServiceImpl
import com.demo.cjh.signin.util.*
import com.google.gson.Gson
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import org.jetbrains.anko.sdk25.coroutines.onItemLongClick
import java.io.File
import java.lang.Exception


/**
 * 创建记录类别页面
 */
class AddTypeActivity : AppCompatActivity() {

    val TAG = AddTypeActivity::class.java.name

    private var type: Type = Type()

    /**
     * 临时图片路径
     */
    private lateinit var imgUrl: String
    private lateinit var bitmap: Bitmap

    private var status = ArrayList<String>()

    private lateinit var adp: ArrayAdapter<String>

    private lateinit var classId: String

    private lateinit var className: String

    private lateinit var typeId: String

    private lateinit var typeService: ITypeService

    private var isCreate = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_type)

        init()

    }

    private fun init() {

        classId = intent.getStringExtra("classId")
        className = intent.getStringExtra("className")

        typeId = intent.getStringExtra("typeId")?:""

        typeService = TypeServiceImpl(this)

        if (!typeId.isNullOrEmpty()){
            isCreate = false

            doService {
                noTransaction
                read
                run {
                    type = typeService.getTypeById(typeId)?:Type()
                    status.addAll(typeService.getKeysByTypeId(typeId))
                }
                success {
                    // 更新组件
                    initData()

                }
            }.start()
        }else{
            // 加载默认
            initData()
        }

        initView()


    }

    fun initData(){
        type.classId = classId
        mTitle.setText(type.title)

        // 本地存在
        if (!type.img.isNullOrEmpty()) {
            imgUrl = type.img

            val f = File(imgUrl)
            Glide.with(this@AddTypeActivity).load(f).into(mImg)

        }else{
            imgUrl = ""
            bitmap = drowText("默")
            mImg.setImageBitmap(bitmap)
        }

        status.add("添加")

        // 更新页面值
        updateStatus()

        if (!isCreate){
            mStatus.isEnabled = true
        }
        mSwitch.isChecked = type.dialog
    }

    @SuppressLint("ResourceAsColor")
    private fun initView() {

        imgUrl = ""
        mTitle.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                type.title = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().contains(" ")) {
                    var str = s.toString().split(" ")
                    var str1 = ""
                    for (i in str) {
                        str1 += i
                    }
                    mTitle.setText(str1)
                    mTitle.setSelection(start)
                }

                if (imgUrl.isNullOrEmpty()) {
                    if (!s.isNullOrEmpty()) {
                        bitmap = drowText(s!![0].toString())
                    } else {
                        bitmap = drowText("默")
                    }
                    mImg.setImageBitmap(bitmap)
                }

            }

        })

        mIcon.setOnClickListener {
            PhotoUtil.selectPictureFromAlbum(this@AddTypeActivity)
        }

        adp = ArrayAdapter(this@AddTypeActivity,R.layout.simple_text_item,R.id.text,status)

        mStatus.setOnClickListener {
            if (!isCreate){
                toast("不允许编辑")
                return@setOnClickListener
            }
            alert{
                title = "添加列值："
                customView {
                    linearLayout{
                        padding = DensityUtils.dp2px(this@AddTypeActivity,10f)
                        listView {
                            divider = ColorDrawable(resources.getColor(R.color.write))
                            dividerHeight = DensityUtils.dp2px(this@AddTypeActivity,5f)
                            adapter = adp
                            onItemClick { p0, p1, p2, p3 ->

                                dialogShow(p2)
                            }
                            onItemLongClick(returnValue = true) { p0, p1, p2, p3 ->
                                if (status.size-1 == p2){
                                    return@onItemLongClick
                                }
                                alert("是否删除") {
                                    positiveButton("删除"){
                                        status.removeAt(p2)
                                        adp.notifyDataSetChanged()
                                    }
                                    negativeButton("取消"){
                                        it.dismiss()
                                    }
                                }.show()
                                true
                            }
                        }.lparams(matchParent, matchParent)
                    }
                }
                positiveButton("确定"){
                    updateStatus()
                    it.dismiss()
                }
                isCancelable = false
            }.show()

        }

        mCheck.setOnClickListener {
            type.dialog = !type.dialog
            Log.v("isDialog","${type.dialog}")
            mSwitch.isChecked = type.dialog
        }
        mSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            type.dialog = isChecked
        }

        /**
         * 保存
         */
        mSave.setOnClickListener {

            if (type.title.isNullOrEmpty() || status.size <=1){
                toast("信息不完整！")
            }else if (status.size <=2) {
                toast("最少设置2个列值！")
            }else{
                if (imgUrl.isEmpty()){
                    // 没有设置图片
                    imgUrl = "$imageDirectoryPath/${getOnlyID()}.png"
                    Log.d(TAG,"imgUrl  $imgUrl")
                    bitmap.saveToPNG(imgUrl)
                }

                load.visibility = View.VISIBLE
                mSave.isEnabled = false


                // 开始创建类型
                // type服务
                doService {
                    run {

                        type.img = imgUrl
                        // 上传之后 存入返回的http图片路径
                        if (isCreate) {
                            // 创建
                            type.cId = ""+typeService.createType(classId = classId, title = type.title, img = type.img, isDialog = type.dialog, status = status.dropLast(1).toTypedArray())
                        }else{
                            // 更新操作
                            typeService.updateType(type)
                        }

                        // 先创建type

                    }
                    success {
                        load.visibility = View.GONE
                        mSave.isEnabled = true
                        finish()
                    }
                    error {e->
                        toast("操作失败，稍后再试")
                        load.visibility = View.GONE
                        mSave.isEnabled = true
                    }
                }.start()

            }
        }
    }

    private fun updateStatus() {
        staValues.text = status.dropLast(1).joinToString(prefix = "<", postfix = ">", separator = ">,<")
    }

    private fun dialogShow(index: Int) {

        val isUpdate = index < status.size-1

        alert {
            title = if (isUpdate) "修改" else "输入"
            var cous: EditText? = null
            customView {
                cous = editText {
                    hint = "请输入列值"
                    addTextChangedListener(RecordSaveMsg.NoSpace(this))
                }
            }
            cous?.setText(if (isUpdate) status[index] else null)
            positiveButton(if(isUpdate) "保存" else "添加") {
                if (isUpdate){
                    // 修改
                    val str = cous?.text.toString()
                    if (str.isEmpty()) {
                        cous?.error = "不能为空"
                        cous?.requestFocus()
                    }
                    status[index] = str
                    adp.notifyDataSetChanged()
                }else {
                    // 插入
                    if (status.size >= 7) {
                        toast("最多设置6个列值！")
                    }else {
                        val str = cous?.text.toString()
                        if (str.isEmpty()) {
                            cous?.error = "不能为空"
                            cous?.requestFocus()
                            return@positiveButton
                        }
                        status.removeAt(status.size - 1)
                        val da = status.clone() as ArrayList<String>
                        status.clear()
                        status.addAll(da)
                        status.add(str)
                        status.add("添加")
                        adp.notifyDataSetChanged()
                    }
                }
            }
            negativeButton("取消") {
                it.dismiss()
            }
        }.show()
    }

    /**
     * 调用相册
     */
    fun startImg() {
        val intent =  Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"//
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent,1)
    }

    fun drowText(str: String): Bitmap{
        val bitmap = Bitmap.createBitmap(64,64,Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        val p = Paint()
        p.textSize = DensityUtils.dp2px(this,15f).toFloat()
        p.isFilterBitmap = true
        p.isDither = true
        p.setARGB(255,255,255,255)
        p.textAlign = Paint.Align.CENTER
        val fontMetrics = p.fontMetrics
        val top = fontMetrics.top//为基线到字体上边框的距离,即上图中的top
        val bottom = fontMetrics.bottom//为基线到字体下边框的距离,即上图中的bottom
        c.drawRGB(23,171,227)

        val baseLineY = (bitmap.height/2 - top / 2 - bottom / 2)//基线中间点的y轴计算公式

        c.drawText(str,bitmap.width/2f,baseLineY,p)
        c.save(Canvas.ALL_SAVE_FLAG)
        c.restore()
        return bitmap
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (data == null){
                return
            }
            when(requestCode){
                PhotoUtil.PHOTOZOOM ->{
                    // 相册回调
                    val uri = data.data
//                    imgUrl = FileUtil.getFilePath(this, uri)
//                    val f = File(imgUrl)
//                    if (f.exists()){
//                        Glide.with(this).load(f).into(mImg)
//                    }
                    imgUrl = PhotoUtil.getPath(this)
                    val p = Uri.fromFile(File(imgUrl))
                    // 开始剪裁
                    PhotoUtil.startPhotoZoom(this@AddTypeActivity,uri,PhotoUtil.PICTURE_HEIGHT,PhotoUtil.PICTURE_WIDTH,p)
                }

                PhotoUtil.PHOTORESOULT ->{
                    val f = File(imgUrl)
                    if (f.exists()){
                        Glide.with(this).load(f).into(mImg)
                    }
                }
            }
        }
    }
}
