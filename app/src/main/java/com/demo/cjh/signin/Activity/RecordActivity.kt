package com.demo.cjh.signin.Activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bin.david.form.data.format.count.ICountFormat
import com.bin.david.form.data.format.draw.ImageResDrawFormat
import com.bin.david.form.data.format.draw.TextDrawFormat
import com.bin.david.form.data.table.ArrayTableData
import com.bin.david.form.utils.DensityUtils
import com.demo.cjh.signin.App
import com.demo.cjh.signin.R
import com.demo.cjh.signin.pojo.StuInfo
import com.demo.cjh.signin.pojo.Type
import com.demo.cjh.signin.service.IRecordService
import com.demo.cjh.signin.service.IStuService
import com.demo.cjh.signin.service.ITypeService
import com.demo.cjh.signin.service.impl.RecordServiceImpl
import com.demo.cjh.signin.service.impl.StuServiceImpl
import com.demo.cjh.signin.service.impl.TypeServiceImpl
import com.demo.cjh.signin.util.TransactionManager
import com.demo.cjh.signin.util.doService
import kotlinx.android.synthetic.main.activity_record.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import com.demo.cjh.signin.util.FaceDB


/**
 * 考勤，记录页面
 */
class RecordActivity : AppCompatActivity() {

    val TAG = RecordActivity::class.java.name

    companion object {
        /**
         * 显示
         */
        const val ACTION_SHOW = 1
        /**
         * 创建
         */
        const val ACTION_CREATE = 0
    }

    /**
     * 班级编号
     */
    private lateinit var classId: String
    /**
     * 记录类型名
     */
    private lateinit var typeName: String

    private lateinit var typeId: String

    private lateinit var className: String

    private lateinit var titleN: String

    /**
     * 操作状态
     * 0 -> 新建
     * 1 -> 查看
     */
    private var action = ACTION_CREATE //

    /**
     * 返回次数
     */
    private var backCount = 0

    private var stus = ArrayList<StuInfo>()

    /**
     * 学生行信息对象
     * 数组对象
     * 通过查表获取
     */
    private lateinit var stuData: Array<Array<String>>

    /**
     * 标题
     */
    private lateinit var titles: Array<String>

    /**
     * 统计行
     */
    private lateinit var counts: Array<String>

    /**
     * 状态选项
     */
    private lateinit var status: Array<String>

    private lateinit var type: Type

    private lateinit var tableData: ArrayTableData<String>

    private lateinit var stuService: IStuService

    private lateinit var typeService: ITypeService

    private lateinit var recordService: IRecordService

    private lateinit var faceDB: FaceDB

    /**
     * 修改标志
     */
    private var upFlag = false

    /**
     * 是否通过刷脸操作
     */
    private var byFace = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)


        classId = intent.getStringExtra("classId")
        className = intent.getStringExtra("className")
        typeName = intent.getStringExtra("typeName")
        typeId = intent.getStringExtra("typeId")
        action = intent.getIntExtra("action",0)

        titleN = if (action == ACTION_SHOW){
            intent.getStringExtra("title")
        }else{
            "$className-$typeName"
        }

        // 设置标题
        title = titleN

        init()

    }

    private fun init() {

        sInit()
        // 加载数据
        dataInit()

    }



    /**
     * 初始化服务
     */
    private fun sInit(){
        stuService = StuServiceImpl(this)
        recordService = RecordServiceImpl(this)
        typeService = TypeServiceImpl(this)
//        val fa = App.getFaceDB()
//        if (fa != null){
//            faceDB = fa
//        }else{
//            faceDB = FaceDB(this)
//            App.setFaceDB(faceDB)
//        }

        faceDB = FaceDB(this)

    }

    /**
     * 加载数据 绘制界面
     */
    private fun dataInit() {
        doAsync {

            // 加载数据

            type = typeService.getTypeById(typeId)?:Type()

            stus.clear()
            /**
             * 如果是查看历史信息，初始化状态信息
             */
            if (action == ACTION_SHOW){
                stus.addAll(recordService.getStusByClassIdAndTypeIdAndTitle(classId = classId,typeId = typeId,title = titleN))
            }else {
                // 创建数据
                // 加载学生数据
                stus.addAll(stuService.getStuInfosByClassId(classId))
                // 加载学生人脸数据
                faceDB.loadFaces(classId)
            }
            Log.v(TAG,"stus size: ${stus.size}")

            status = typeService.getKeysByTypeId(typeId).toTypedArray()
            val tit = arrayListOf<String>("学号","姓名")
            tit.addAll(status)
            titles = tit.toTypedArray()

            // 从数据源 分析统计各个列值
            statistical()

            /**
             * 行列转换 并初始化数据
             */
            stuData = ArrayTableData.transformColumnArray(stuListToArray())


            printl(stuData)

            uiThread {

                /**
                 * 绘制界面
                 */
                init_view()
            }

        }
    }

    /**
     * 统计列值
     */
    private fun statistical() {
        counts = Array(titles.size){"0"}
        counts[0] = "合计："
        counts[1] = "${stus.size}人"
        for(stu in stus){
            if(stu.status != "-1"){
                counts[stu.status.toInt()+2] = (counts[stu.status.toInt()+2].toInt()+1).toString()
            }
        }
    }

    /**
     * copy 保证stuData的内存唯一性，
     */
    fun copy(data:  Array<Array<String>>){
        for(i in 0 until data.size){
            for(j in 0 until data[i].size){
                stuData[i][j] = data[i][j]
            }
        }
        printl(data)
    }

    /**
     * 对象集合转矩阵数组
     */
    private fun stuListToArray(): Array<Array<String>> {
        val data = Array(stus.size) { it -> Array(titles.size) { "0" } }
        for (i in 0 until data.size) {
            val stu = Array(titles.size) { "0" }
            stu[0] = stus[i].stuId
            stu[1] = stus[i].stuName

            // 设置标号
            if(stus[i].status != "-1") {
                stu[stus[i].status.toInt() + 2] = "1"
            }
            data[i] = stu
        }
        printl(data)
        return data
    }

    /**
     * 打印矩阵
     */
    fun printl(data: Array<Array<String>>){

        var str = "[${data.size}],[${data[0].size}]\n"
        for(i in 0 until data.size){
            for(j in 0 until data[i].size){
                str += data[i][j]+" * "
            }
            str += "\n"
        }
        Log.v(TAG,str)
    }

    /**
     * 矩阵转对象集合
     */
    private fun arrayToStuList(){
        for(i in 0 until stus.size){
            for(j in 0 until status.size){
                if(stuData[j+2][i] ==  "1") {
                    // 等于1的下标 就是状态值
                    stus[i].status = j.toString()
                }
            }

        }
    }

    /**
     * 绘制界面
     */
    private fun init_view() {
        // 设置数据源
        tableData = ArrayTableData.create(className, titles, stuData, TextDrawFormat<String>())

        val size = DensityUtils.dp2px(this@RecordActivity, 15f) //指定图标大小

        val images = object : ImageResDrawFormat<String>(size, size) {
            override fun getResourceID(t: String, value: String?, position: Int): Int {
                if (t == "1") {
                    return R.drawable.hua
                }
                return 0
            }

            override fun getContext(): Context {
                return this@RecordActivity
            }

        }

        /**
         * 遍历列，设置图标
         */
        tableData.arrayColumns[0].isAutoCount = true
        tableData.arrayColumns[0].countFormat = Icount { counts[0] }
        tableData.arrayColumns[1].isAutoCount = false
        tableData.arrayColumns[1].countFormat = Icount { counts[1] }

        var i = 2
        tableData.arrayColumns.drop(2).forEach {
            val item = it
            item.drawFormat = images
            item.isAutoCount = true
            val num = counts[i++]
            item.countFormat = Icount{ num }
        }

        tableData.isShowCount = true

        // 行点击事件
        tableData.setOnRowClickListener { column, t, col, row ->
            // 数据格式转换
            arrayToStuList()
            if (type.dialog){
                /**
                 * 使用dialog 选择器
                 */

                val item = stus[row]
                val index = item.status
                var dialog = AlertDialog.Builder(this@RecordActivity)
                        .setTitle("${item.stuName}同学$typeName:")
                        .setSingleChoiceItems(titles.drop(2).toTypedArray(),item.status.toInt()){dialog, which ->

                            item.status = which.toString()

                            if(index != item.status ){
                                // 表示修改
                                upFlag = true
                            }
                            refresh()
                            dialog.dismiss()
                        }.show()

            }else {
                /**
                 * 使用页面选择器
                 */
                val intent = Intent(this@RecordActivity, SignInActivity::class.java)
                intent.putExtra("action", 0)
                intent.putExtra("position", row)
                intent.putExtra("data", stus)
                intent.putExtra("typeId", typeId)
                startActivityForResult(intent, 0)
            }
        }

        table.tableData = tableData
        val wm1 = this@RecordActivity.windowManager
        val outMetrics = DisplayMetrics()
        wm1.defaultDisplay.getMetrics(outMetrics)
        val width1 = outMetrics.widthPixels
        table.config.minTableWidth = width1
        table.setZoom(true,2f,0.5f)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK && upFlag ) {

            arrayToStuList()

            if (action == ACTION_CREATE) {
                // 保存操作

                if (stus.any { it.status == "-1" }) {
                    // 有未点名的
                    //toast("检测到未点名")

                    var til = titles[2]
                    if (type.img  == "1" ){
                        if (byFace){
                            til = titles[7]
                        }
                    }

                    alert("检测到有未填人员,退出将默认为${til}，是否退出", "提示") {
                        positiveButton("确认") {
                            // 默认stus出勤
                            stus.forEach {
                                if (it.status == "-1") {
                                    it.status = if (byFace) "5" else "0"
                                }
                            }
                            // 刷新
//                            copy(ArrayTableData.transformColumnArray(stuListToArray()))
//                            table.notifyDataChanged()
                            refresh()

                            val intent = Intent(this@RecordActivity, RecordSaveMsg::class.java)
                            // 启动回调
                            startActivityForResult(intent, 1)

                        }
                        negativeButton("取消") {

                        }
                        neutralPressed("退出") {
                            finish()
                        }
                        isCancelable = false
                    }.show()

                } else {
                    alert("尚未保存，是否退出") {
                        positiveButton("保存"){
                            val intent = Intent(this@RecordActivity, RecordSaveMsg::class.java)
                            // 启动回调
                            startActivityForResult(intent, 1)
                        }
                        neutralPressed("退出"){
                            finish()
                        }
                        negativeButton("取消"){

                        }
                    }.show()


                }

                // 点名完成
                // 保存  输入学科  节数  备注  标题

                return false
            }else if (action == ACTION_SHOW){
                // 修改了，保存
                alert(message = "检测到你修改了信息，是否保存",title = "提示") {
                    positiveButton("确定"){
                        doService {
                            run {
                                recordService.update(stus)
                            }
                            success {
                                finish()
                            }
                        }.start()
                    }
                    negativeButton("取消"){
                        finish()
                    }
                }.show()

                return false
            }

        }

        return super.onKeyDown(keyCode, event)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(data == null) {
                return
            }
            when(requestCode){
                0 ->{
                    Log.v(TAG,"手动更改回调")
                    stus.clear()
                    val da = data.getSerializableExtra("data") as ArrayList<StuInfo>
                    stus.addAll(da)
                    // 数据格式转换

                    // 刷新
                    refresh()

                    //table.notifyDataChanged()

                    // 修改标志 置true
                    upFlag = true
                }
                1 ->{
                    // 保存回调
                    val sTitle = data.getStringExtra("title")
                    val sSubjectId = data.getStringExtra("subjectId")
                    val sTime = data.getStringExtra("time")
                    val sInfo = data.getStringExtra("info")

                    Log.d(TAG,"回调数据：$sTitle $sSubjectId $sTime $sInfo")
                    /**
                     * 保存信息
                     */
                    TransactionManager(this).run {
                       recordService.save(title = sTitle,info = sInfo,subjectId = sSubjectId,sTime = sTime,typeId = typeId,stus = stus)
                    }.success {
                        // 成功返回
                        finish()
                    }.start()
                }
                2 ->{
                    // 人脸检测回调
                    upFlag = true
                    // 通过刷脸考勤
                    byFace = true

                    val da = data.getStringArrayListExtra("stuIds")
                    da.forEach {
                        Log.v(TAG,"da: $it")
                    }
                    stus.forEach {
                        var its = it
                        Log.v(TAG," | " +its.stuId+" "+its.status)
                        if(da.any { its.stuId == it }){
                            // 存在
                            its.status = "0"
                            Log.v(TAG,"${its.stuName} 存在")
                        }
                        Log.v(TAG,its.stuId+" "+its.status)
                    }

                    refresh()
                }
            }
        }

    }

    private fun refresh() {
        // 计算列值
        statistical()
        // 格式转化
        copy(ArrayTableData.transformColumnArray(stuListToArray()))
        // 重绘view
        init_view()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (action == ACTION_CREATE && type.img == "1") {
            menuInflater.inflate(R.menu.dian_ming, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.dm ->{
                // 手动点名
                arrayToStuList()
                val intent = Intent(this@RecordActivity,SignInActivity::class.java)
                intent.putExtra("action",1)
                intent.putExtra("position",0)
                intent.putExtra("data",stus)
                intent.putExtra("typeId",typeId)
                startActivityForResult(intent,0)
            }
            R.id.facedm ->{
                // 自动点名 人脸识别

                // 判断是否存在人脸
                if(faceDB.mRegister.isEmpty()) {
                    // 显示为空
                    alert("暂无学生人脸数据，请先录入") {
                        positiveButton("确定"){
                            it.dismiss()
                        }
                        isCancelable = false
                    }.show()
                }else{
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                        if (ContextCompat.checkSelfPermission(this,
                                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){ //表示未授权时
                            //进行授权
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),1)
                        }else{
                            //调用方法
                            startCamera()
                        }
                    }else{
                        startCamera()
                    }



                }

            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 调用相机页面
     */
    private fun startCamera() {
        AlertDialog.Builder(this@RecordActivity)
                .setTitle("请选择相机")
                .setItems(arrayOf("后置相机", "前置相机")) { dialog, which ->

                    val it = Intent(this@RecordActivity, SignInByFace::class.java)
                    it.putExtra("Camera", which)
                    App.setRegister(faceDB.mRegister)
                    startActivityForResult(it, 2)
                }
                .show()
    }


    /**
     * 授权回调
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            1 ->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){ //同意权限申请
                    startCamera()
                }else { //拒绝权限申请
                    Toast.makeText(this,"权限被拒绝了", Toast.LENGTH_SHORT).show()
                    //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),1)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        faceDB.mRegister.clear()
        faceDB.destroy()
    }

    public class Icount<T>(var setCountS:(count: Long) -> String) : ICountFormat<T, Long> {
        var cou = 0L

        override fun count(t: T?) {
            Log.d("COUNT","count: $t")
            when(t){
                is String ->{
                    cou += 1
                }
            }
        }

        override fun getCount(): Long {
            return cou
        }

        override fun getCountString(): String {
            return setCountS(cou)
        }

        override fun clearCount() {
            cou = 0
        }


    }
}
