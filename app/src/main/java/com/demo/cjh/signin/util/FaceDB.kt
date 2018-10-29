package com.demo.cjh.signin.util

import android.util.Log
import com.arcsoft.facerecognition.AFR_FSDKEngine
import com.arcsoft.facerecognition.AFR_FSDKError
import com.arcsoft.facerecognition.AFR_FSDKFace
import com.arcsoft.facerecognition.AFR_FSDKVersion
import com.demo.cjh.signin.App
import com.demo.cjh.signin.obj.FaceRegist
import java.io.*
import java.util.ArrayList

/**
 * Created by CJH
 * on 2018/8/2
 */
class FaceDB {
    val TAG = "FaceDB"

    companion object {
        //sdk 1.0版
//        val appid = "4kX79nVx1cRBR4d7a4Rf2LrXAuDgkQ5qsjwETW29rvjz"
//        val ft_key = "6YEhMtjkFyvA56ZrhzPgqau9qT13JUsFyCRR9m6jXg1o"
//        val fd_key = "6YEhMtjkFyvA56ZrhzPgqauGzrGAnUBmR9uBa1tyUrvp"
//        val fr_key = "6YEhMtjkFyvA56ZrhzPgqauQAFXNm3n1eYY1vpULdEdc"
//        val age_key = "6YEhMtjkFyvA56ZrhzPgqav1yFqEaJXzw5LT1bof84q5"
//        val gender_key = "6YEhMtjkFyvA56ZrhzPgqav98f6RgY1BSzPgckmGdGyU"

        // sdk 1.2版 支持安卓8.1系统
        val appid = "4kX79nVx1cRBR4d7a4Rf2LrXAuDgkQ5qsjwETW29rvjz"
        val ft_key = "6YEhMtjkFyvA56ZrhzPgqau9qT199RzTkQaqsSSZZkTu"
        val fd_key = "6YEhMtjkFyvA56ZrhzPgqauGzrGGgmJ4H4sxsnpM8x75"
        val fr_key = "6YEhMtjkFyvA56ZrhzPgqaumeTJxMuGCjHDkjNqo6638"
        val age_key = "6YEhMtjkFyvA56ZrhzPgqav1yFqKGxy1Wgx7i5J689Lq"
        val gender_key = "6YEhMtjkFyvA56ZrhzPgqav98f6SaJnoA25siB1TdPJS"
    }

    public var mRegister = ArrayList<FaceRegist>()
    private var mFREngine: AFR_FSDKEngine = AFR_FSDKEngine()
    private var mFRVersion: AFR_FSDKVersion = AFR_FSDKVersion()
    internal var mUpgrade: Boolean = false


    init {
        val error = mFREngine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key)
        if (error.code != AFR_FSDKError.MOK) {
            Log.e(TAG, "AFR_FSDK_InitialEngine fail! error code :" + error.code)
        } else {
            mFREngine.AFR_FSDK_GetVersion(mFRVersion)
            Log.d(TAG, "AFR_FSDK_GetVersion=" + mFRVersion.toString())
        }
    }

    fun destroy() {
            mFREngine.AFR_FSDK_UninitialEngine()

    }


    fun loadFaces(classId: String): Boolean {

        mRegister.addAll(App.app!!.db!!.queryFaces(classId))
        Log.v(TAG,"加载人脸库完成")
        return true
    }

    fun addFace(classId: String,stuId: String, name: String,face: AFR_FSDKFace,no: Int) {

        val frface = FaceRegist(stuId,name)
        when(no){
            1 -> frface.face1 = face
            2 -> frface.face2 = face
            3 -> frface.face3 = face
        }
        mRegister.add(frface)
        App.app!!.db!!.insertFace(classId,stuId, face, no)
    }

    fun delete(name: String): Boolean {
        try {
            //check if already registered.
            var find = false
            for (frface in mRegister) {
                if (frface.name.equals(name)) {

                    mRegister.remove(frface)
                    find = true
                    break
                }
            }

            if (find) {

            }
            return find
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return false
    }

    fun upgrade(): Boolean {
        return false
    }
}