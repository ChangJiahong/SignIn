package com.demo.cjh.signin.service

/**
 * Created by CJH
 * on 2018/12/5
 */
object ServiceManager {
    private val TAG = ServiceManager::class.java.name

    private val services = HashMap<String,Any>()

    public fun registerService(key: String,value: Any){
        if(!services.containsKey(key)) {
            services[key] = value
        }
    }

    public fun getService(key: String): Any?{
        return services[key]
    }
}