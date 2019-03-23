package com.demo.cjh.signin.service.impl

import android.content.Context
import com.demo.cjh.signin.dao.IRecordDao
import com.demo.cjh.signin.dao.ITypeDao
import com.demo.cjh.signin.dao.ITypeKeyDao
import com.demo.cjh.signin.dao.impl.RecordDaoImpl
import com.demo.cjh.signin.dao.impl.TypeDaoImpl
import com.demo.cjh.signin.dao.impl.TypeKeyDaoImpl
import com.demo.cjh.signin.pojo.Type
import com.demo.cjh.signin.pojo.TypeKey
import com.demo.cjh.signin.service.ITypeService
import org.jetbrains.anko.collections.forEachWithIndex

import java.util.ArrayList

/**
 * Created by CJH
 * on 2018/11/24
 */
class TypeServiceImpl(private val context: Context ) : ITypeService {



    private val typeDao: ITypeDao = TypeDaoImpl(context)
    private val typeKeyDao: ITypeKeyDao = TypeKeyDaoImpl(context)
    private val recordDao: IRecordDao = RecordDaoImpl(context)

    /**
     * 获取 记录类型 枚举值
     */
    override fun getKeysByTypeId(typeId: String): ArrayList<String> {
        return typeKeyDao.getKeysByTypeId(typeId)
    }

    /**
     * 获取单个type
     */
    override fun getTypeById(typeId: String): Type? {
        return typeDao.getById(typeId)
    }

    /**
     * 获取 记录类型列表
     */
    override fun getTypesByClassId(classId: String): ArrayList<Type> {
        return typeDao.getTypesByClassId(classId)
    }

    /**
     * 创建 记录 类型
     */
    override fun createType(classId: String, title: String, img: String, isDialog: Boolean,status: Array<String>): Int {
        val type = Type()
        type.classId = classId
        type.title = title
        type.img = img
        type.dialog = isDialog

        val typeId = typeDao.insert(type).toString()

        status.forEachWithIndex { i, s ->
            typeKeyDao.insert(TypeKey(typeId = typeId,typeKey = s,weight = i))
        }
        return typeId.toInt()
    }

    /**
     * 修改
     */
    override fun updateType(type: Type): Int{
        return typeDao.update(type)
    }

    /**
     * 删除byId
     */
    override fun deleteById(typeId: String,classId: String): Int {
        // 删除type， 删除关于type的所有记录
        // record表中，所有typeid的记录
        recordDao.deleteByTypeIdAndClassId(typeId = typeId,classId = classId)
        typeDao.deleteById(typeId)

        return typeKeyDao.deleteById(typeId)
    }

}
