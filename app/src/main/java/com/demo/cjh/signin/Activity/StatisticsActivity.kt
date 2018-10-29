package com.demo.cjh.signin.Activity

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TabHost
import android.widget.TextView
import com.demo.cjh.signin.Fragment.*
import com.demo.cjh.signin.R
import kotlinx.android.synthetic.main.activity_statistics.*
import org.jetbrains.anko.find
import org.jetbrains.anko.padding
import org.jetbrains.anko.textColorResource


/**
 * 统计功能
 */
class StatisticsActivity : AppCompatActivity() {

    var TAG = "StatisticsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        init()
    }

    private fun init() {
        title = "统计"


        var bundle = Bundle()
        bundle.putString("tag",TAG)
        tabhost.setup(this,supportFragmentManager,R.id.realtabcontent)

        tabhost.addTab(getTabView(R.string.sclass), SClassFragment::class.java,bundle)
        tabhost.addTab(getTabView(R.string.sstu), SStuFragment::class.java,bundle)
        tabhost.tabWidget.showDividers = LinearLayout.SHOW_DIVIDER_NONE
        // 初始化按钮
        check(0)
        tabhost.tabWidget.getChildTabViewAt(0).setOnClickListener { check(0) }
        tabhost.tabWidget.getChildTabViewAt(1).setOnClickListener { check(1) }



    }

    fun getTabView(textId: Int): TabHost.TabSpec{
        var text = resources.getString(textId)
        var tab_item = layoutInflater.inflate(R.layout.tab_item_layout,null)
        var item_text = tab_item.find<TextView>(R.id.tab_text)
        var item_img = tab_item.find<ImageView>(R.id.tab_img)
        item_img.visibility = View.GONE
        item_text.setPadding(0,20,0,20)
        item_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,22f)
        item_text.text = text
        var spec = tabhost.newTabSpec(text).setIndicator(tab_item)
        return spec
    }

    fun check(po: Int){
        tabhost.currentTab = po
        for(i in (0..(tabhost.tabWidget.childCount-1))){
            var text = tabhost.tabWidget.getChildAt(i).find<TextView>(R.id.tab_text)
            //var img = tabhost.tabWidget.getChildAt(i).find<ImageView>(R.id.tab_img)

            if(po == i){
                text.setTextColor(Color.parseColor("#007FFF"))
            }else{
                text.textColorResource = R.color.defult
            }


        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sclass,menu)
        return true
    }
}
