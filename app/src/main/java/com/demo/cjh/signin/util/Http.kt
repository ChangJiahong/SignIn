package com.demo.cjh.signin.util

import android.util.Log
import java.net.URL
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException


/**
 * Created by CJH
 * on 2018/9/1
 */
object Http {

    fun login(userid: String,pwd: String) : String{
        var url = "localhost:8080/signin/register"
        var content = "telnum=Tel&pwd=Pwd".replace("Tel",userid)
                                                            .replace("Pwd",pwd)
        return Http_Post(url,content)
    }

    /**
     * http请求
     * @param url 请求地址
     * @param content 发送的数据
     * @return
     */
    fun Http_Post(url: String, content: String): String {
        var result: String? = null
        try {
            val httpUrl = URL(url)
            val conn = httpUrl.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.readTimeout = 5000
            conn.connectTimeout = 5000
            conn.doInput = true
            conn.doOutput = true
            val out = conn.outputStream
            out.write(content.toByteArray())

            val br = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
            val sb = StringBuffer()
            var line = ""
            do  {
                line = br.readLine()
                sb.append(line)
            }while (line != null)
            result = sb.toString()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            Log.d("http", "错误4")
        } catch (e: ProtocolException) {
            e.printStackTrace()
            Log.d("http", "错误1")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            Log.d("http", "错误2")
        }

        return result!!
    }
}