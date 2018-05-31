package com.demo.cjh.signin

import java.io.Serializable

class StudentInfo : Serializable {

    constructor(){}

    constructor(id : String, name : String,  type : String, classId: String){
        this.id = id
        this.name = name
        this.type = type
        this.classId = classId
    }


    var id: String? = null
    var name: String? = null
    var type: String? = null
    var classId: String? = null
}
