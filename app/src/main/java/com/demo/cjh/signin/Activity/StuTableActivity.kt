package com.demo.cjh.signin.Activity

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bin.david.form.data.CellInfo
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.format.bg.IBackgroundFormat
import com.bin.david.form.data.format.bg.ICellBackgroundFormat
import com.bin.david.form.data.table.TableData
import com.demo.cjh.signin.R
import com.demo.cjh.signin.StudentInfo
import kotlinx.android.synthetic.main.activity_stu_table.*
import org.jetbrains.anko.toast


class StuTableActivity : AppCompatActivity() {

    val TAG = "StuTableActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_table)

        init()
    }

    private fun init() {

        val intent = intent

        var list = ArrayList<StudentInfo>()
        for(i in (0..60)){
            list.add(StudentInfo())
            list[i].id = i.toString()
            list[i].name = "张三".plus(i)
        }

        var column1 : Column<String> = Column("学号","id")
        var column2 : Column<String> = Column("姓名","name")
        var column3 : Column<String> = Column("出勤","chuQin")
        var column4 : Column<String> = Column("病假","bingJia")
        var column5 : Column<String> = Column("事假","shiJia")
        var column6 : Column<String> = Column("旷课","kuangKe")

        var tableData : TableData<StudentInfo> = TableData(intent.getStringExtra("id"),list,column1,column2,column3,column4,column5,column6)
        //val stu_table = findViewById<SmartTable<StudentInfo>>(R.id.stu_table)


        stu_table.tableData = tableData
        stu_table.setZoom(true) // 设置缩放
        stu_table.config.contentCellBackgroundFormat = object : ICellBackgroundFormat<CellInfo<*>> {
            override fun drawBackground(canvas: Canvas, rect: Rect, cellInfo: CellInfo<*>, paint: Paint) {
                if (cellInfo.row % 2 == 0 && cellInfo.col % 2 != 0) {
                    paint.color = Color.parseColor("#c5cecc")
                    canvas.drawRect(rect, paint)
                }else if(cellInfo.row % 2 != 0 && cellInfo.col % 2 == 0) {
                    paint.color = Color.parseColor("#c5cecc")
                    canvas.drawRect(rect, paint)
                }
            }

            override fun getTextColor(cellInfo: CellInfo<*>): Int {
                return 0
            }
        }



    }
}
