package com.demo.cjh.signin.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import com.demo.cjh.signin.R
import com.demo.cjh.signin.pojo.Stu
import com.demo.cjh.signin.service.IClassesService
import com.demo.cjh.signin.service.IStuService
import com.demo.cjh.signin.service.impl.ClassesServiceImpl
import com.demo.cjh.signin.service.impl.StuServiceImpl
import com.demo.cjh.signin.util.MyDatabaseOpenHelper
import kotlinx.android.synthetic.main.activity_stu_table.*
import org.jetbrains.anko.*

/**
 * 手动添加学生信息页面
 */
class AddStuActivity : AppCompatActivity() {

    val TAG = "AddStuActivity"

    private lateinit var stuAdapter: StuAdapter

    private lateinit var stus: ArrayList<Stu>

    private lateinit var classId: String

    private lateinit var className: String

    private lateinit var stuService: IStuService
    private lateinit var classesService: IClassesService

    private lateinit var readDb: SQLiteDatabase
    private lateinit var writeDb: SQLiteDatabase

    private lateinit var action: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_table)

        classId = intent.getStringExtra("classId")
        className = intent.getStringExtra("className")?:"SignIn"
        action = intent.getStringExtra("action")
        stus = intent.getSerializableExtra("stuData") as ArrayList<Stu>? ?: ArrayList<Stu>()


        title = className


        init()
    }

    private fun init() {

        db_init()

        stuAdapter = StuAdapter(this,stus)

        stuList.adapter = stuAdapter

        /**
         * item 点击事件
         */
        stuList.setOnItemClickListener { parent, view, position, id ->
            var item = stus[position]
            showDialog(item.stuId,item.stuName,position)
        }

    }

    /**
     * 数据库设置并初始化服务
     */
    fun db_init(){
        val db = MyDatabaseOpenHelper.getInstence(this)
        readDb = db.readableDatabase
        writeDb = db.writableDatabase

        stuService = StuServiceImpl(this)
        classesService = ClassesServiceImpl(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.add ->{
                // 添加
                showDialog("","",stus.size)

            }
            R.id.ok ->{
                // 完成
                //
                if(action == Intent.ACTION_VIEW ){
                    // 外部文件打开，没有classId
                    // 创建班级

                    var view = LayoutInflater.from(this).inflate(R.layout.table_dialog, null)
                    var inputName = view.find<EditText>(R.id.inputName)
                    var inputInfo = view.find<EditText>(R.id.inputInfo)

                    alert("请输入班级信息：") {
                        this.customView = view
                        positiveButton("确定"){
                            var className = inputName.text.toString()
                            var info = inputInfo.text.toString()
                            var institute = ""// 二级学院
                            var speciality = ""// 学校
                            if(inputName.text.toString().isEmpty()){
                                toast("班级名不能为空！")
                            }else {
                                doAsync {
                                    // mvc  调用服务层对象，
                                    val classes = classesService.saveClasses(className,info,institute,speciality)

                                    uiThread {
                                        if (classes != null) {
                                            // 创建成功 设置班级id
                                            stus.forEach {
                                                it.classId = classes.classId
                                            }
                                            toast("创建班级成功！")
                                            /**
                                             * 保存学生数据
                                             */
                                            save()
                                        } else {
                                            toast("创建班级失败")
                                        }
                                    }


                                }
                            }
                        }
                        negativeButton("取消"){

                        }
                        isCancelable = false
                    }.show()
                }else{
                    /**
                     * 直接保存
                     */

                    save()
                }


            }

        }

        return super.onOptionsItemSelected(item)
    }

    private fun save() {
        doAsync {
            var re = stuService.save(stus)
            uiThread {
                if (re > 0) {
                    toast("保存成功")
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    toast("保存失败！")
                }
            }
        }
    }


    /**
     * 显示 消息
     */
    fun showDialog(id: String,name: String,index: Int){
        var v = LayoutInflater.from(this).inflate(R.layout.add_dialog, null)
        var inputName = v.find<EditText>(R.id.inputName)
        var inputId = v.find<EditText>(R.id.inputId)
        inputId.setText(id)
        inputName.setText(name)

        alert {
            title = "请输入学生信息名称："
            customView = v

            positiveButton("确定"){
                if(inputId.text.isEmpty()){
                    inputId.error = "学号不为空！"
                    inputId.requestFocus()
                }else if (inputName.text.isEmpty()){
                    inputName.error = "姓名不为空"
                    inputName.requestFocus()
                }else {
                    var stuId = inputId.text.toString()
                    var stuName = inputName.text.toString()
                    if(index >= stus.size) {
                        // 插入信息
                        // 如果没有和id相同的返回true
                        if (stus.none { it.stuId == stuId }) {
                            stus.add(Stu(classId,stuId, stuName))
                            stuAdapter.notifyDataSetChanged()
                        } else {
                            toast("插入失败，学号重复")
                        }
                    }else if(index < stus.size) {
                        /**
                         * 修改信息 更改
                         */
                        if(stuId == id && stuName == name){
                            // 没有更改 跳过
                            return@positiveButton
                        }
                        if (stus.none { it.stuId == stuId } || stuId == id) {

                            stus[index].stuId = stuId
                            stus[index].stuName = stuName
                            // 更新
                            stuAdapter.notifyDataSetChanged()
                        } else {
                            toast("修改失败，学号重复")
                        }


                    }
                }
            }
            negativeButton("取消"){

            }
        }.show()

    }


    /**
     * Stu List Adapter
     */
    class StuAdapter(val context: Context,val data : ArrayList<Stu>) :BaseAdapter(){

        init {
            data.sortBy {
                it.stuId
            }

        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var v: View
            var holder: ViewHolder
            if(convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.activity_table_item, null)
                holder = ViewHolder(v)
                v.tag = holder
            }else{
                v = convertView
                holder = v.tag as ViewHolder
            }

            val item = data[position]

            holder.idView.text = (position+1).toString()
            holder.stu_id.text = item.stuId
            holder.name.text = item.stuName

            return v
        }

        override fun getItem(position: Int): Stu {
            return data[position]
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
            var idView = view.find<TextView>(R.id.id)
            var stu_id = view.find<TextView>(R.id.stu_id)
            var name = view.find<TextView>(R.id.name)
        }

    }

}
