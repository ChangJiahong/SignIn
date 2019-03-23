package com.demo.cjh.signin.Activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.demo.cjh.signin.R
import com.demo.cjh.signin.pojo.Type
import com.demo.cjh.signin.service.ITypeService
import com.demo.cjh.signin.service.impl.TypeServiceImpl
import com.demo.cjh.signin.util.doService
import kotlinx.android.synthetic.main.activity_select.*
import org.jetbrains.anko.*
import java.io.File

/**
 * 功能选择页面
 */
class SelectActivity : AppCompatActivity(){

    val TAG = SelectActivity::class.java.name

    /**
     * 班级编号
     */
    private lateinit var classId: String
    /**
     * 班级名
     */
    private lateinit var className: String

    /**
     * 类型服务
     */
    lateinit var typeService: ITypeService

    /**
     * 类型数据
     */
    lateinit var types: ArrayList<Type>

    /**
     * gridList adapter实现
     */
    lateinit var adapter: GridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        classId = intent.getStringExtra("classId")
        className = intent.getStringExtra("className")

        title = className

        service_init()

        view_init()

        init_data()

    }

    /**
     * 加载数据
     */
    private fun init_data() {
        doAsync {
            types = typeService.getTypesByClassId(classId)
            // 添加一个新建按钮
            types.add(Type("-1", "新建", "+", classId))

            uiThread {
                // 更新页面
                adapter = GridAdapter(this@SelectActivity, types)
                gridView.adapter = adapter

            }
        }
    }

    /**
     * 初始化化控件
     */
    private fun view_init() {
        registerForContextMenu(gridView)
        gridView.setOnItemClickListener { parent, view, position, id ->
            val item = types[position]

            when (position) {
                types.size - 1 -> {
                    Log.d(TAG, "添加type")
                    startActivity<AddTypeActivity>(
                            "classId" to classId,
                            "className" to className
                    )

                }
                else -> {
                    //
                    startActivity<OldListActivity>(
                            "classId" to classId,
                            "className" to className,
                            "typeName" to item.title,
                            "typeId" to item.cId)
                }
            }
        }
    }

    /**
     * 加载数据库
     * 初始化服务
     */
    private fun service_init() {
        typeService = TypeServiceImpl(this)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.type,menu)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {

        val menuInfo = item!!.menuInfo as AdapterView.AdapterContextMenuInfo
        if (menuInfo.position >= types.size-1){
            return super.onContextItemSelected(item)
        }
        val ite = types[menuInfo.position]
        when(item.itemId){
            R.id.edit ->{
                // 编辑
                startActivity<AddTypeActivity>(
                        "classId" to classId,
                        "className" to className,
                        "typeId" to ite.cId
                )
            }
            R.id.delete ->{
                // 删除
                alert("删除后会丢失所以关于该类型的记录，确定删除吗","提示") {
                    positiveButton("确定"){
                        doService {
                            run {
                                typeService.deleteById(ite.cId,ite.classId)
                            }
                            success {
                                types.removeAt(menuInfo.position)
                                adapter.notifyDataSetChanged()
                            }
                        }.start()
                    }
                    negativeButton("再想想"){

                    }
                }.show()
            }
        }

        return super.onContextItemSelected(item)
    }

    override fun onRestart() {
        super.onRestart()
        init_data()
    }

    class GridAdapter(var context: Context,var data: ArrayList<Type>) : BaseAdapter(){

        var inflater: LayoutInflater = LayoutInflater.from(context)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var holder: Holder
            var v : View
            if(convertView == null){
                v = inflater.inflate(R.layout.type_item,null)
                holder = Holder(v)
                v.tag = holder
            }else{
                v = convertView
                holder = v.tag as Holder
            }
            val item = data[position]
            holder.typeName.text = item.title
            when(item.img){
                "" ->{

                }
                "1" ->{
                    holder.typeImg.setImageResource(R.drawable.kaoqin)
                }
                "2" ->{
                    holder.typeImg.setImageResource(R.drawable.jilu)
                }
                "3" ->{
                    holder.typeImg.setImageResource(R.drawable.shiyan)
                }
                "+" ->{
                    holder.typeImg.setImageResource(R.drawable.add_type)
                }
                else ->{

                    val f = File(item.img)
                    Glide.with(context).load(f).into(holder.typeImg)

                }
            }

            return v
        }


        override fun getItem(position: Int): Type {
           return data[position]
        }


        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return data.size
        }

        class Holder(v: View){
            val typeName = v.find<TextView>(R.id.type_name)
            val typeImg = v.find<ImageView>(R.id.type_img)
        }

    }
}
