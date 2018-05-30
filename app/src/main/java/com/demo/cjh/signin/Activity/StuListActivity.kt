package com.demo.cjh.signin.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.demo.cjh.signin.R
import com.demo.cjh.signin.StudentInfo
import kotlinx.android.synthetic.main.activity_stu_list.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.find
import java.io.Serializable

class StuListActivity : AppCompatActivity() {

    val TAG = "StuListActivity"

    private val REQUEST_REGION_PICK = 1

    val data = ArrayList<StudentInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_list)

        init()
    }

    private fun init() {

        class_name.text = intent.getStringExtra("id")


        for(i in (1..60)){
            data.add(StudentInfo(i.toString(),"庄三".plus(i),"dao"))

        }

        stu_list.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        stu_list.adapter = StuAdapter(data){position ->

                val intent = Intent(this@StuListActivity,SignInActivity::class.java)
                intent.putExtra("code",0)
                intent.putExtra("position",position)
                intent.putExtra("data",data)

                startActivityForResult(intent,REQUEST_REGION_PICK)

            //startActivity(intent)
        }

        stu_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy > 0){
                    Log.v(TAG,"向下")

                }else if(dy <0){
                    Log.v(TAG,"向上")

                }
            }
        })

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            alert {
                title = "提示"
                message = "是否保存"
                positiveButton("是"){
                    val intent = Intent()
                    intent.putExtra("data",data as Serializable)
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                }
                negativeButton("否"){

                }
            }.show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.stu_list,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val intent = Intent(this@StuListActivity,SignInActivity::class.java)

        intent.putExtra("code",1)
        intent.putExtra("id",0)
        intent.putExtra("data",data)

        startActivityForResult(intent,REQUEST_REGION_PICK)
        //startActivity(intent)
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, datas: Intent?) {
        super.onActivityResult(requestCode, resultCode, datas)

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_REGION_PICK){
                if(datas != null) {
                    data.clear()
                    var da = datas.getSerializableExtra("data")!! as List<StudentInfo>
                    for(item in da){
                        data.add(item)
                    }
                    stu_list.adapter.notifyDataSetChanged()
                }
            }
        }
    }

    class StuAdapter(val mItems : ArrayList<StudentInfo>,internal val didSelectedAtPos:(idx : Int) -> Unit) : RecyclerView.Adapter<StuAdapter.ViewHolder>(){

        internal var mContext : Context? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            mContext = parent.context
            return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.stu_list_item,parent,false))
        }

        override fun getItemCount(): Int {
            return mItems.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            fun bind(model: StudentInfo){

                holder.idView.text = (position+1).toString()
                holder.stu_id.text = model.id
                holder.name.text = model.name
                Log.v("StuAdapter",position.toString()+model.name+" "+model.type)
                unCheckAll(holder)
                when(model.type){
                    "dao" -> holder.dao.backgroundResource = R.drawable.hua2
                    "shiJia" -> holder.shiJia.backgroundResource = R.drawable.hua2
                    "bingJia" -> holder.bingJia.backgroundResource = R.drawable.hua2
                    "chiDao" -> holder.chiDao.backgroundResource = R.drawable.hua2
                    "kuangKe" -> holder.kuangKe.backgroundResource = R.drawable.hua2
                }

                with(holder.container){
                    setOnClickListener {
                        didSelectedAtPos(position)
                    }
                }


            }
            val item = mItems[position]
            bind(item)
        }

        /**
         * 全部不选
         */
        private fun unCheckAll(holder: ViewHolder ){
            holder.dao.backgroundResource = 0
            holder.chiDao.backgroundResource = 0
            holder.kuangKe.backgroundResource = 0
            holder.shiJia.backgroundResource = 0
            holder.bingJia.backgroundResource = 0
        }

        class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
            var container = view.find<LinearLayout>(R.id.stu_list_item)
            var idView = view.find<TextView>(R.id.id)
            var stu_id = view.find<TextView>(R.id.stu_id)
            var name = view.find<TextView>(R.id.name)
            var dao = view.find<ImageView>(R.id.dao)
            var shiJia = view.find<ImageView>(R.id.shiJia)
            var bingJia = view.find<ImageView>(R.id.bingJia)
            var chiDao = view.find<ImageView>(R.id.chiDao)
            var kuangKe = view.find<ImageView>(R.id.kuangKe)
        }

    }

    class Track {
        var id : String? = null
        var name : String? = null
        var type : String? = null
        constructor(id : String,name: String,type: String){
            this.id = id
            this.name = name
            this.type = type
        }
    }
}
