package com.demo.cjh.signin.util

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by CJH
 * on 2018/12/5
 */
class TransactionManager(private val context: Context) {
    private val TAG = TransactionManager::class.java.name

    /**
     * 是否开启事务
     * 默认开启
     */
    private var isTransaction = true

    /**
     * 是否是写操作
     * 默认是写操作
     */
    private var isWrite = true

    /**
     * 设置不开启事务
     */
    val noTransaction = {
        isTransaction = false
        this
    }()

    /**
     * 设置是读操作
     */
    val read = {
        isWrite = false
        this
    }()

    /**
     * 设置是写操作
     */
    val write = {
        isWrite = true
        this
    }()

    /**
     * 执行错误
     */
    private var er: (e: Exception) -> Unit = {
        Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
    }

    /**
     * 执行成功
     */
    private var su = {
        Toast.makeText(context, "成功", Toast.LENGTH_SHORT).show()
    }

    /**
     * 执行过程
     */
    private lateinit var ru: () -> Unit

    fun error(f: (e: Exception) -> Unit): TransactionManager {
        er = f
        return this
    }

    fun success(s: () -> Unit): TransactionManager {
        su = s
        return this
    }


    fun run(r: () -> Unit): TransactionManager {
        ru = r
        return this
    }

    constructor(context: Context, isTransaction: Boolean, isWrite: Boolean, er: (e: Exception) -> Unit, su: () -> Unit, ru: () -> Unit) : this(context) {
        this.isTransaction = isTransaction
        this.isWrite = isWrite
        this.er = er
        this.su = su
        this.ru = ru
    }

    /**
     * 开始
     */
    /**
     * 通过设置builder模式 开启事务
     */
    fun start() {
        doAsync {
            // 创建数据库对象
            var db: SQLiteDatabase = if (isWrite) {
                MyDatabaseOpenHelper.writeDb(context)
            } else {
                MyDatabaseOpenHelper.readDb(context)
            }

            if (isTransaction) {
                db.beginTransaction()
            }
            // 开启事务
            try {
                // 数据库操作
                ru()
                if (isTransaction) {
                    // 设置事务成功
                    db.setTransactionSuccessful()
                }

                uiThread {
                    // 成功回调
                    su()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                uiThread {
                    // 失败回调
                    er(e)
                }
            } finally {
                if (isTransaction) {
                    // 提交事务
                    db.endTransaction()
                }
                db.close()
            }
        }

    }

    /**
     * 通过设置DoTransactionListener 监听器 开启事务
     */
    fun start(doTransactionListener: DoTransactionListener) {

        doAsync {

            // 创建数据库对象
            var db: SQLiteDatabase = if (isWrite) {
                MyDatabaseOpenHelper.writeDb(context)
            } else {
                MyDatabaseOpenHelper.readDb(context)
            }

            if (isTransaction) {
                db.beginTransaction()
            }
            // 开启事务
            try {
                // 数据库操作
                doTransactionListener.run()
                if (isTransaction) {
                    // 设置事务成功
                    db.setTransactionSuccessful()
                }

                uiThread {
                    // 成功回调
                    doTransactionListener.success()
                }

            } catch (e: SQLException) {
                e.printStackTrace()
                uiThread {
                    // 失败回调
                    doTransactionListener.error(e)
                }
            } finally {
                if (isTransaction) {
                    // 提交事务
                    db.endTransaction()
                }
                db.close()
            }
        }
    }

    interface DoTransactionListener {
        /**
         * 数据库操作
         */
        fun run()

        /**
         * 失败回调
         */
        fun error(e: SQLException)

        /**
         * 成功回调
         */
        fun success()
    }
}

fun Context.doService(dos: TransactionManager.() -> Unit): TransactionManager{
    return TransactionManager(this).apply{
        dos()
    }
}