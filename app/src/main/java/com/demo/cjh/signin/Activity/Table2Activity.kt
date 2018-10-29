package com.demo.cjh.signin.Activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import com.bin.david.form.data.format.draw.TextDrawFormat
import com.bin.david.form.data.table.ArrayTableData
import com.demo.cjh.signin.*
import com.demo.cjh.signin.FileUtil
import com.demo.cjh.signin.obj.StudentInfo
import com.demo.cjh.signin.util.getDirPath
import com.demo.cjh.signin.util.getSuffex
import com.demo.cjh.signin.util.getValue
import kotlinx.android.synthetic.main.activity_table2.*
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jetbrains.anko.*
import java.io.File

/**
 * 查看数据页面 默认打开第一个日期的状态
 */
class Table2Activity : AppCompatActivity() {

    val TAG = "Table2Activity"

    /**
     * 当前打开文件对象
     */
    var file: File? = null

    /**
     * 数据对象
     */
    var contentData = ArrayList<ArrayList<String>>()

    var stuData = ArrayList<StudentInfo>()
    var titleData = ArrayList<String>()

    var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table2)
        init()

    }

    private fun init() {

        var filename = intent.getStringExtra("filepath")
        file = File(FileUtil.fileDirectory + "/"+filename)
        Log.v(TAG,"app内打开 --> "+file!!.getDirPath()+"  --> "+file!!.name)



        Log.v(TAG, "开始")

        // 分析扩展名
        when (file!!.getSuffex()) {
            FileUtil.XLS -> {
                // 如果是xls
                doAsync {
                    var data = readExcel(file!!)
                    if(data == null){
                        data = Array<Array<String>>(26,{Array(25,{""})})
                    }

                    uiThread {
                        Log.v(TAG,"show Table")
                        var tableData = ArrayTableData.create(table, "Excel表", data, TextDrawFormat<String>())

                        table.tableData = tableData


                    }



                }


                //Log.v(TAG, sheet.getRow(1).getCell(0).toString())
            }
            FileUtil.XLSX -> {
                // xlsx文档类型待解决
                showDialog("暂不支持xlsx文件格式，请期待更新！")
            }
        }
        Log.v(TAG, "关闭")


    }


    fun readExcel(file: File): Array<Array<String>>? {

        var inputStream = file.inputStream()
        var workbook = WorkbookFactory.create(inputStream)
        var sheet = workbook.getSheetAt(0)



        // 总行数
        var rowCount = sheet.physicalNumberOfRows
        // 第一行
        var tabletitle = sheet.getRow(sheet.firstRowNum)
        // 总列数
        var cellCount = tabletitle.physicalNumberOfCells

        var data = Array<Array<String>>(rowCount,{Array(cellCount,{""})})

        /**
         * 文件格式是否正确
         */
        if (tabletitle.getCell(0).getValue() == "学号" && tabletitle.getCell(1).getValue() == "姓名") {
            // 格式正确  读取数据

            // 读取数据
            for (i  in 0 until rowCount) {

                var rowData = Array<String>(sheet.getRow(i).physicalNumberOfCells,{""})

                for (j in 0 until cellCount) {
                    rowData[j] = sheet.getRow(i).getCell(j).getValue()
                }
                data[i] = rowData
            }

        }else{
            return null
        }
        var newdata = Array<Array<String>>(cellCount,{Array(rowCount,{""})})
        for (i in 0 until data.size){
            for(j in 0 until data[i].size){
                newdata[j][i] = data[i][j]
            }
        }

        return newdata
    }

    fun showDialog(msg: String){
        AlertDialog.Builder(this@Table2Activity)
                .setTitle(msg)
                .setPositiveButton("确定"){ dialogInterface: DialogInterface, i: Int ->
                    finish()
                }
                .setCancelable(false)
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.share,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.share ->{
                Log.v("TAG","正在分享文件")
                shareFile(this,file!!)

            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 調用系統方法分享文件
    public fun shareFile(context: Context, file: File) {
        if (file.exists()) {
            var share = Intent(Intent.ACTION_SEND)
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            share.type = getMimeType(file.absolutePath)//此处可发送多种文件
            share.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(share, "分享文件"))
            Log.v("TAG","分享文件")
        } else {
            toast("分享文件不存在")
        }
    }

    // 根据文件后缀名获得对应的MIME类型。
    private fun getMimeType(filePath: String) :String {
        var mmr = MediaMetadataRetriever()
        var mime = "*/*"
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath)
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
            } catch (e:IllegalStateException) {
                return mime
            } catch (e:IllegalArgumentException) {
                return mime
            } catch (e:RuntimeException) {
                return mime
            }
        }
        return mime
    }
}
