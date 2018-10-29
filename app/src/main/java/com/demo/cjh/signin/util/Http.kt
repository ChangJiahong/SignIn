package com.demo.cjh.signin.util

import android.util.Log
import java.io.*
import java.net.*
import com.demo.cjh.signin.App
import java.util.*
import com.demo.cjh.signin.obj.FaceData


/**
 * Created by CJH
 * on 2018/9/1
 */
object Http {

    val TAG = "Http.class"

    val IP = App.app!!.ip!!

    val loginUrl = "$IP/signin/login"

    val registerUrl = "$IP/signin/register"

    val setImgUrl = "$IP/signin/setImg"

    val setName = "$IP/signin/setName"

    val setPwd = "$IP/signin/setPwd"

    val backUp = "$IP/signin/backUp"

    val setFace = "$IP/signin/setFace"

    val reStore = "$IP/signin/reStore"

    fun login_by_pwd(userid: String,pwd: String) : String{
        var url = loginUrl
        var content = "telnum=Tel&pwd=Pwd".replace("Tel",userid)
                .replace("Pwd",pwd)
        return Http_Post(url,content)
    }

    fun login_by_token(userid: String,userToken: String) : String{
        var url = loginUrl
        var content = "telnum=Tel&usertoken=UserToken".replace("Tel",userid)
                .replace("UserToken",userToken)
        return Http_Post(url,content)
    }

    fun register(userid: String,name: String,pwd: String): String{
        val url = "$registerUrl?telnum=$userid&name=$name&pwd=$pwd"
        return URL(url).readText()
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

            var br = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
            var sb = StringBuffer()
            br.forEachLine{
                sb.append(it)
            }
            result = sb.toString()
            Log.v("http","data "+result!!)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            Log.d("http", "错误4")
        } catch (e: ProtocolException) {
            e.printStackTrace()
            Log.d("http", "错误1")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            Log.d("http", "错误2")
        } catch (e: KotlinNullPointerException){
            e.printStackTrace()
        }
        return result!!
    }


    private  val BOUNDARY =  UUID.randomUUID().toString() // 边界标识 随机生成

    fun upImg(imgpath: String): String{
        // 上传服务器
        val sp = App.app!!.sp!!

        Log.v(TAG,"uid="+sp.getString("userid",""))
        val params = mapOf(Pair("telnum",sp.getString("userid","")), Pair("usertoken",sp.getString("userToken","")))

        val url = setImgUrl

        val file = File(imgpath)

        return uploadImage(params,"image",file,file.name,url)
    }

    private  val PREFIX = "--";
    private  val LINE_END = "\r\n";


    /**

     *

     * @param params

     * 传递的普通参数

     * @param uploadFile

     * 需要上传的文件

     * @param fileFormName

     * 需要上传文件表单中的名字

     * @param newFileName

     * 上传的文件名称，不填写将为uploadFile的名称

     * @param urlStr

     * 上传的服务器的路径

     * @throws IOException

     */
    fun uploadImage(params: Map<String,String>, fileFormName: String = "image", uploadFile: File, newFileName: String? = uploadFile.name,urlStr: String) : String{


        val sb = StringBuilder()

        /**

         * 普通的表单数据

         */

        for (key in params.keys) {

            sb.append("--$BOUNDARY\r\n")

            sb.append("Content-Disposition: form-data; name=\"" + key + "\""

                    + "\r\n")

            sb.append("\r\n")

            sb.append(params[key] + "\r\n")

        }

        /**

         * 上传文件的头

         */

        sb.append("--$BOUNDARY\r\n")

        sb.append("Content-Disposition: form-data; name=\"$fileFormName\"; filename=\"$newFileName\"\r\n")

        sb.append("Content-Type: image/jpeg" + "\r\n")// 如果服务器端有文件类型的校验，必须明确指定ContentType

        sb.append("\r\n")


        val headerInfo = sb.toString().toByteArray(charset("UTF-8"))

        val endInfo = "\r\n--$BOUNDARY--\r\n".toByteArray(charset("UTF-8"))

        Log.v(TAG,sb.toString())
        //println(sb.toString())

        val url = URL(urlStr)

        val conn = url.openConnection() as HttpURLConnection

        conn.requestMethod = "POST"

        conn.setRequestProperty("Content-Type","multipart/form-data; boundary=$BOUNDARY")

        //conn.setRequestProperty("Content-Length", (headerInfo.size + uploadFile.length()).toString() + endInfo.size)

        conn.doOutput = true


        val out = DataOutputStream(conn.outputStream)

        val inp = FileInputStream(uploadFile)

        out.write(headerInfo)


        val buf = ByteArray(1024)

        var len: Int = 0

        while (inp.read(buf).also { len = it } != -1)
            out.write(buf, 0, len)

        out.write(endInfo)

        inp.close()

        out.close()
        var result = ""

        if (conn.responseCode === 200) {

            Log.v(TAG,"上传成功")
            //println("上传成功")


            var br = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
            var sbr = StringBuffer()
            br.forEachLine{
                sbr.append(it)
            }
            result = sbr.toString()
            println(result)
        }


        return result
    }

    fun upName(name: String): String{
        var url = setName+"?telnum=${App.app!!.user.id}&usertoken=${App.app!!.user.userToken}"
        return Http_Post(url,"name=$name")
    }

    fun upPwd(pwd: String): String{
        var url = setPwd+"?telnum=${App.app!!.user.id}&usertoken=${App.app!!.user.userToken}&pwd=$pwd"
        return URL(url).readText()
    }


    fun backUp(signInData: String): String {
        var url = backUp+"?telnum=${App.app!!.user.id}&usertoken=${App.app!!.user.userToken}"
        return Http_Post(url,"signInData=$signInData")
    }

    fun reStore(): String{
        var url = reStore+"?telnum=${App.app!!.user.id}&usertoken=${App.app!!.user.userToken}"
        return URL(url).readText()
    }
    fun setFace(faces: ArrayList<FaceData>): String{
        for(face in faces) {
            var data1 = face.face1
            var data2 = face.face2
            var data3 = face.face3

            var urlStr = setFace + "?telnum=${App.app!!.user.id}&usertoken=${App.app!!.user.userToken}&classid=${face.classId}" +
                    "&stuid=${face.stuId}"

            if(data1 != null){
                urlStr += "&len1=${data1.size}"
            }
            if(data2 != null){
                urlStr += "&len2=${data2.size}"
            }
            if(data3 != null){
                urlStr += "&len3=${data3.size}"
            }

            var data : ByteArray? = null

            if(data1 != null){
                data = data1
                if(data2 != null){
                    data = ArrayUtil.MergerArray(data,data2)
                    if(data3 != null){
                        data = ArrayUtil.MergerArray(data,data3)
                    }
                }
            }

            if(data != null){


                val url = URL(urlStr)

                val conn = url.openConnection() as HttpURLConnection

                conn.requestMethod = "POST"

                conn.setRequestProperty("Content-Type","multipart/form-data; boundary=$BOUNDARY")

                //conn.setRequestProperty("Content-Length", (headerInfo.size + uploadFile.length()).toString() + endInfo.size)

                conn.doOutput = true


                val out = DataOutputStream(conn.outputStream)

                val inp = ByteArrayInputStream(data)


                val buf = ByteArray(1024)

                var len: Int = 0

                while (inp.read(buf).also { len = it } != -1)
                    out.write(buf, 0, len)


                inp.close()

                out.close()
                var result = ""

                if (conn.responseCode === 200) {

                    Log.v(TAG,"上传成功")
                    //println("上传成功")


                    var br = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                    var sbr = StringBuffer()
                    br.forEachLine{
                        sbr.append(it)
                    }
                    result = sbr.toString()
                    println(result)
                }

                Log.v(TAG,result)
            }


        }

        return ""
    }

}