package com.demo.cjh.signin.Activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.bin.david.form.utils.DensityUtils
import com.demo.cjh.signin.Adapter.StuSignInListAdapter
import com.demo.cjh.signin.R
import com.demo.cjh.signin.pojo.OldListItem
import com.demo.cjh.signin.pojo.StuInfo
import com.demo.cjh.signin.service.IRecordService
import com.demo.cjh.signin.service.IStuService
import com.demo.cjh.signin.service.ITypeService
import com.demo.cjh.signin.service.impl.RecordServiceImpl
import com.demo.cjh.signin.service.impl.StuServiceImpl
import com.demo.cjh.signin.service.impl.TypeServiceImpl
import com.demo.cjh.signin.util.*
import kotlinx.android.synthetic.main.activity_old_list.*
import kotlinx.android.synthetic.main.stu_list_item.view.*
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.jetbrains.anko.*
import org.jetbrains.anko.collections.forEachWithIndex
import java.io.File

/**
 * 历史记录页面
 */
class OldListActivity : AppCompatActivity() {

    private val TAG = OldListActivity::class.java.name

    /**
     * 日期比较
     */
    val compByDate: Comparator<OldListItem> = Comparator { o1, o2 ->
        o2.time.parseDate().compareTo(o1.time.parseDate())
    }

    /**
     * 班级编号
     */
    private lateinit var classId: String

    /**
     * 班级名
     */
    private lateinit var className: String

    /**
     * 类型名
     */
    private lateinit var typeName: String

    /**
     * 类型编号
     */
    private lateinit var typeId: String

    /**
     * 学生服务
     */
    private lateinit var stuService: IStuService

    /**
     *
     */
    private lateinit var recordService: IRecordService

    private lateinit var typeService: ITypeService

    private lateinit var addStuByHand: Button

    private lateinit var addStuByFile: Button

    private lateinit var adapter: StuSignInListAdapter

    private val data = ArrayList<OldListItem>()

    private val stus = ArrayList<StuInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_old_list)

        classId = intent.getStringExtra("classId")
        className = intent.getStringExtra("className")
        typeName = intent.getStringExtra("typeName")
        typeId = intent.getStringExtra("typeId")

        title = "$className-$typeName"

        init()
    }

    fun init(){
        /**
         * 初始化服务
         */
        _init()

        /**
         * 添加按钮初始化化
         */
        add_init()

        /**
         * 显示页面初始化
         */
        listView_init()

        // 显示空
        showEmpty(false)

        /**
         * 加载数据
         */
        init_data()


    }

    /**
     * 加载数据
     */
    private fun init_data() {
        doAsync {
            stus.clear()
            stus.addAll(stuService.getStuInfosByClassId(classId))
            if (stus.isEmpty()){
                uiThread {
                    // 显示空，没有学生数据
                    showEmpty(true)
                }
            }else{
                data.clear()
                data.addAll(recordService.getListByClassIdAndTypeId(classId,typeId))
                // 排序
                data.sortWith(compByDate)

                uiThread {
                    // 刷新页面
                    adapter.notifyDataSetChanged()
                }
            }

        }
    }


    /**
     * 显示空页面
     */
    private fun showEmpty(show: Boolean) {
        empty_view.visibility = if (show) View.VISIBLE else View.GONE
        view.visibility = if (show) View.GONE else View.VISIBLE
    }

    /**
     * listView 控件初始化
     */
    private fun listView_init() {
        /**
         * 配置头view
         */
        var vHead = View.inflate(this@OldListActivity, R.layout.stu_sign_in_list_item, null)
        val text = vHead.find<TextView>(R.id.title)
        vHead.find<TextView>(R.id.time).visibility = View.GONE
        vHead.find<TextView>(R.id.info).visibility = View.GONE
        vHead.find<TextView>(R.id.stime).text = ""
        vHead.find<TextView>(R.id.subject).text = ""
        text.text = "新建"


        /**
         * 添加头view
         */
        oldList.addHeaderView(vHead)

        /**
         * 初始化适配器
         */
        adapter = StuSignInListAdapter(data,this@OldListActivity)
        oldList.adapter = adapter

        /**
         * item list点击事件
         */
        oldList.setOnItemClickListener { parent, view, position, id ->

            when(position){
                0 ->{
                    startActivity<RecordActivity>(
                            "classId" to classId,
                            "className" to className,
                            "typeName" to typeName,
                            "typeId" to typeId,
                            "action" to RecordActivity.ACTION_CREATE)
                }
                else ->{
                    startActivity<RecordActivity>(
                            "classId" to classId,
                            "className" to className,
                            "typeName" to typeName,
                            "typeId" to typeId,
                            "title" to data[position-1].title,
                            "action" to RecordActivity.ACTION_SHOW)
                }
            }
        }
    }

    /**
     * 添加学生信息 的设置初始化
     */
    fun add_init(){

        addStuByHand = empty_view.find(R.id.byHand)
        addStuByFile = empty_view.find(R.id.byFile)
        /**
         * 手动添加学生
         */
        addStuByHand.setOnClickListener {
            startActivity<AddStuActivity>(
                    "classId" to classId,
                    "className" to className,
                    "action" to "0")
        }
        /**
         * 文件添加
         */
        addStuByFile.setOnClickListener {

            val intent =  Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/vnd.ms-excel"//Excel类型限制
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent,1)
        }
    }


    /**
     * 初始化服务
     */
    fun _init(){
        stuService = StuServiceImpl(this)
        recordService = RecordServiceImpl(this)
        typeService = TypeServiceImpl(this)
    }


    override fun onRestart() {
        super.onRestart()
        showEmpty(false)
        init_data()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.stu_list,menu)
        return super.onCreateOptionsMenu(menu)
    }



    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.out ->{
                if(data.size <=0) {
                    toast("暂无签到数据，不能导出！")
                    return true
                }

                alert {
                    title = "请选择需要导出的记录："
                    val checks = ArrayList<CheckBox>()
                    customView {
                        verticalLayout {
                            scrollView {
                                verticalLayout {
                                    padding = DensityUtils.dp2px(this@OldListActivity,10f)
                                    for (i in 0 until data.size){
                                        val check = checkBox(data[i].title){
                                            isChecked = true
                                            textSize = DensityUtils.dp2px(this@OldListActivity,12f).toFloat()
                                        }
                                        checks.add(check)
                                    }
                                }
                            }

                        }

                    }

                    positiveButton("确定"){
                        val da = ArrayList<OldListItem>()
                        data.forEachWithIndex {i ,s->
                            if (checks[i].isChecked){
                                da.add(s)
                            }
                        }
                        var fileName = className+System.currentTimeMillis()
                        alert {
                            title = "文件名："
                            lateinit var editT: EditText
                            this.customView{
                                verticalLayout {
                                    editT = editText(fileName){
                                        hint = "文件名"
                                    }
                                }
                            }

                            positiveButton("是"){
                                fileName = editT.text.toString()
                                var filePath = fileDirectoryPath+"/"+fileName+"."+ FileUtil.XLS
                                var newFile = File(filePath)
                                if(newFile.exists()){
                                    toast("文件名重复！！导出失败")
                                }else {
                                    export(fileName,da)
                                }
                            }
                            negativeButton("否"){

                            }
                        }.show()
                    }
                    negativeButton("取消"){

                    }
                }.show()



            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            // 文件系统返回 文件路径
            if(requestCode == 1){
                val uri = data!!.data
                var path = ""
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                    path = FileUtil.getPath(this, uri) ?:""
                } else {//4.4以下下系统调用方法
                    path = FileUtil.getRealPathFromURI(this, uri) ?:""
                }

                Log.d(TAG,path)

                /**
                 * 启动导入页面
                 */
                startActivity<TableActivity>(
                        "classId" to classId,
                        "className" to className,
                        "path" to path)
            }
        }
    }

    private fun export(fileName: String,data: ArrayList<OldListItem>) {

        // 表头，根据表头插入数据
        var flag = false
        var mProgressDialog = ProgressDialog(this)
        mProgressDialog.setCancelable(true)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL) // 设置水平进度条
        mProgressDialog.setTitle("请稍后")
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消") { dialogInterface: DialogInterface, i: Int ->
            flag = true
            toast("导出失败")
        }
        mProgressDialog.max = 100
        mProgressDialog.setMessage("正在努力导出中...")
        mProgressDialog.show()
        doAsync {

            var filePath = "$fileDirectoryPath/$fileName.${FileUtil.XLS}"
            var newFile = File(filePath)
            if (!newFile.exists()) {
                newFile.createNewFile()
            }
            var workbook = HSSFWorkbook()
            var sheet = workbook.createSheet("sheet1")
            // 头行
            var headRow = sheet.createRow(0)

            var cell = headRow.createCell(0)
            cell.setCellValue("学号")
            cell = headRow.createCell(1)
            cell.setCellValue("姓名")

            var rowCount = stus.size + 1
            var cellCount = data.size + 2

            data.reverse()
            /**
             * 创建第一行的列
             */
            for (i in 0..(data.size - 1)) {
                cell = headRow.createCell(i + 2)
                cell.setCellValue(data[i].title)
            }

            /**
             * 创建stuData学生的行
             */
            for (i in 1..(rowCount - 1)) {
                var row = sheet.createRow(i)
                var cell = row.createCell(0)
                cell.setCellValue(stus[i - 1].stuId)
                cell = row.createCell(1)
                cell.setCellValue(stus[i - 1].stuName)
            }

            /**
             * 建立数据对象
             * 查询表数据
             * 插入
             */

           // var stuSign = database.query_gpadata_by_classId(classId)

            val keys = typeService.getKeysByTypeId(typeId)
            for (i in 1..(rowCount - 1)) {
                // 行标
                // 当前行
                var r = sheet.getRow(i)
                // 学号
                var stuId = r.getCell(0).getValue()

                val stus = recordService.getValuesByClassIdAndTypeIdAndTitleAndStuId(classId = classId,typeId = typeId,stuId = stuId)

                for (j in 2..(cellCount - 1)) {
                    // 列标
                    // 创建当前列
                    var c = r.createCell(j)

                    // 标题
                    var title = headRow.getCell(j).getValue()

                    c.setCellValue(keys[stus[title]!!.toInt()])

                }
                if (flag)
                    break
                mProgressDialog.incrementProgressBy((i/rowCount)*100)
            }

            workbook.write(newFile)
            workbook.close()

            runOnUiThread {
                toast("导出成功")
                mProgressDialog.dismiss()
            }

        }

    }
}
