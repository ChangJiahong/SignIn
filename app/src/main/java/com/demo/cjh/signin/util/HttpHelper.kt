package com.demo.cjh.signin.util

import android.content.Context
import android.util.Log
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import com.demo.cjh.signin.App
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import com.demo.cjh.signin.pojo.Result
import com.google.gson.Gson
import java.io.*
import java.util.*
import java.util.concurrent.TimeoutException
import kotlin.collections.ArrayList




/**
 * 网络请求帮助类
 * Created by CJH
 * on 2018/12/20
 * @date 2018/12/20
 * @author CJH
 */
class HttpHelper{

    companion object {
        var IP = ""

        val login
        get() = "$IP/login"

        val downTables
        get() = "$IP/downTables"

        val upTables
        get() = "$IP/upTables"

        val upTypeImg
        get() = "$IP/file/upTypeImg"

        val upUserImg
        get() = "$IP/file/upUserImg"

        val alterName
        get() = "$IP/alterName"

        val alterPwd
        get() = "$IP/alterPwd"

        val upFaceFiles
        get()= "$IP/file/upFaceFiles"

    }

    var requestMethod = "POST"

    var readTimeout = 5000

    var connectTimeout = 5000

    var doInput = true

    var doOutput = true

    var type = "application/x-www-form-urlencoded"

    var token = App.app.sp.getString("token"," s")

    private var _params: MutableMap<String,String> = HashMap<String,String>()

    lateinit var files: ArrayList<String>

    private val pairs = fun (map: MutableMap<String,String>, makePairs: RequestPairs.() -> Unit){
        val requestPair = RequestPairs()
        requestPair.makePairs()
        map.putAll(requestPair.pairs)
    }

    var content = ""

    fun params(makePairs: RequestPairs.() -> Unit) = pairs(_params,makePairs)

    var url = ""

    /**
     * 参数字符串
     */
    private val _content:String
    get() {
        return  if (_params.isNotEmpty()) _params.toQueryString() else content
    }


    private lateinit var su: (status: Int,msg: String,data: Any?) -> Unit

    private lateinit var er: (code: Int) -> Unit

    private var be = {

    }

    fun before(b: () -> Unit): HttpHelper{
        be = b
        return this
    }

    fun success(s: (status: Int,msg: String,data: Any?) -> Unit): HttpHelper {
        su = s
        return this
    }

    fun error(e: (code: Int) -> Unit): HttpHelper{
        er = e
        Log.d("http","加载error")
        return this
    }

    fun start() {

        doAsync {
            Log.v("http","开启请求 url= $url")
            be()
            var json: String = ""
            try {
                json = post()
            } catch (e: Exception){
                uiThread {
                    er(6)
                    Log.d("http", "错误6")
                }
                return@doAsync
            }
            Log.d("http result",json)
            uiThread {
                Log.v("http","请求结束")
                if (json.isNotEmpty()){
                    val result = Gson().fromJson(json, Result::class.java)  //getreslut(json)

                    su(result.status, result.msg, result.data)

                }
            }
        }

    }

    fun upload() {
        doAsync {
            be()
            val json = uploadFile()
            uiThread {
                Log.v("http","请求结束")
                if (json.isNotEmpty()){
                    val result = Gson().fromJson(json, Result::class.java)  //getreslut(json)

                    su(result.status, result.msg, result.data)

                }
            }
        }
    }


    private fun post(): String {
        val conn = createConn()

        if (_content.isNotEmpty()) {
            val out = conn.outputStream
            out.write(_content.toByteArray())
            out.flush()
            out.close()
        }

        if(conn.responseCode == 200) {
            val br = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
            val sb = StringBuffer()
            br.forEachLine {
                sb.append(it)
            }
            return sb.toString()
        }else{
            throw java.lang.Exception("请求失败")
        }

            //Log.v("http","data "+result)

        return ""
    }

    private fun createConn(): HttpURLConnection{
        val httpUrl = URL(url)
        val conn = httpUrl.openConnection() as HttpURLConnection
        conn.requestMethod = this.requestMethod
        conn.readTimeout = this.readTimeout
        conn.connectTimeout = this.connectTimeout
        conn.doInput = this.doInput
        conn.doOutput = this.doOutput

        conn.setRequestProperty("Authorization", "Bearer $token")
        conn.setRequestProperty("Content-Type",type)

        return conn
    }


    fun uploadFile(url: String = this.url,files: ArrayList<String> = this.files,params: MutableMap<String,String> = this._params): String {

        if (url.isNotEmpty()){
            this.url = url
        }

        val filePaths = files
        val BOUNDARY = UUID.randomUUID().toString() //边界标识 随机生成
        val PREFIX = "--"
        val LINE_END = "\r\n"
        val CONTENT_TYPE = "multipart/form-data" //内容类型
        try {
            val conn = createConn()
            conn.setRequestProperty("Charset", "UTF-8")
            //设置编码
            conn.setRequestProperty("connection", "keep-alive")
            conn.setRequestProperty("Content-Type", "$CONTENT_TYPE;boundary=$BOUNDARY")
            //     if(files.size()!= 0) {
            /** * 当文件不为空，把文件包装并且上传  */
            val outputSteam = conn.outputStream
            val dos = DataOutputStream(outputSteam)

            /**
             * 普通的表单数据
             */
            val sbr = StringBuffer()
            for (key in params.keys) {

                sbr.append("--$BOUNDARY\r\n")

                sbr.append("Content-Disposition: form-data; name=\"$key\"\r\n")

                sbr.append("\r\n")

                sbr.append(params[key] + "\r\n")

            }
            dos.write(sbr.toString().toByteArray())

            for (i in filePaths.indices) {
                val file = File(filePaths[i])
                val sb = StringBuffer()
                sb.append(PREFIX)
                sb.append(BOUNDARY)
                sb.append(LINE_END)
                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.name + "\"" + LINE_END)
                //sb.append("Content-Type: application/octet-stream; charset="+ "UTF-8" +LINE_END);

                sb.append(LINE_END)
                dos.write(sb.toString().toByteArray())
                val fs = FileInputStream(file)
                val bytes = ByteArray(1024)
                var len = -1
                while (fs.read(bytes).also { len = it } != -1) {
                    dos.write(bytes, 0, len)
                }

                dos.write(LINE_END.toByteArray())
                fs.close()

            }
            val end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).toByteArray()
            dos.write(end_data)
            dos.flush()
            /**
             * 获取响应码 200=成功
             * 当响应成功，获取响应的流
             */
            val res = conn.responseCode
            //  Log.e(TAG, "response code:"+res);
            if (res == 200) {
                //
                val br = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                val sb = StringBuffer()
                br.forEachLine {
                    sb.append(it)
                }
                return sb.toString()
            }

        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TimeoutException){
            e.printStackTrace()
        }

        return ""

    }


    class RequestPairs{
        var pairs: MutableMap<String, String> = HashMap()
        operator fun String.minus(value: String) {
            pairs.put(this, value)
        }
    }

    private fun <K, V> Map<K, V>.toQueryString(): String = this.map { "${it.key}=${it.value}" }.joinToString("&")
}

    fun Context.doHttp(dos: HttpHelper.() -> Unit): HttpHelper{
        return HttpHelper().apply{
        dos()
        }
        }