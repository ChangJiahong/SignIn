package com.demo.cjh.signin

import android.util.Log
import com.demo.cjh.signin.util.HttpHelper
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        var httpHelper = HttpHelper();
        httpHelper.apply {
            url = "http://cjh.pythong.top/ip/"
            requestMethod = "GET"

            success { status, msg, data ->
                if (status == 200){
                    Log.d("index","ip : ${data.toString()}")
                }
            }
            start()
        }
    }
}
