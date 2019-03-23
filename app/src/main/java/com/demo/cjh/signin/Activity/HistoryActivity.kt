package com.demo.cjh.signin.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.demo.cjh.signin.R
import com.demo.cjh.signin.pojo.HistoryItem
import com.demo.cjh.signin.util.database
import org.jetbrains.anko.doAsync

class HistoryActivity : AppCompatActivity() {


    val data = ArrayList<HistoryItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        init()
    }

    private fun init() {

        doAsync {
            data.clear()
            data.addAll(database.queryHisoryList())
        }
    }
}
