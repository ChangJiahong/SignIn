package com.demo.cjh.signin.pojo

/**
 * Created by CJH
 * on 2018/11/21
 */
object SqlString{
    /**
     * Create Table If Not Exists stu(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            class_id CHAR(20) not null,
            stu_id CHAR(20) not null,
            stu_name VARCHAR(20) not null,
            stu_face1 VARCHAR(255),
            stu_face2 VARCHAR(255),
            stu_face3 VARCHAR(255),
            create_time TEXT,

            status INTEGER not null,
            anchor INTEGER not NULL
            )
     */

    val stu = "Create Table If Not Exists stu(" +
                    "        id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "        class_id CHAR(20) not null," +
                    "        stu_id CHAR(20) not null," +
                    "        stu_name VARCHAR(20) not null," +
                    "        stu_face1 VARCHAR(255)," +
                    "        stu_face2 VARCHAR(255)," +
                    "        stu_face3 VARCHAR(255)," +
                    "        create_time TEXT," +
                    "        status INTEGER not null," +
                    "        anchor INTEGER not NULL" +
                    "        )"

    /**
     * Create Table If Not Exists classes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            class_id CHAR(20) not null,
            class_name VARCHAR(20) not null,
            info VARCHAR(255) ,
            institute VARCHAR(50),
            speciality VARCHAR(50),
            create_time TEXT,

            status INTEGER not null,
            anchor INTEGER not NULL
            )
     */

    val classes = "Create Table If Not Exists classes (" +
                        "     id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "     class_id CHAR(20) not null," +
                        "     class_name VARCHAR(20) not null," +
                        "     info VARCHAR(255) ," +
                        "     institute VARCHAR(50)," +
                        "     speciality VARCHAR(50)," +
                        "     create_time TEXT," +
                        "     status INTEGER not null," +
                        "     anchor INTEGER not NULL" +
                        "     )"

    /**
     * Create Table If Not Exists record(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            class_id CHAR(20) not null,
            stu_id CHAR(20) not null,
            subject_id VARCHAR(50) ,
            stime VARCHAR(20),
            statu VARCHAR(20) not null,
            type_id CHAR(20) not null,
            info VARCHAR(255) ,
            title VARCHAR(255) not null,
            record_id VARCHAR(255) not null,
            create_time TEXT,

            status INTEGER not null,
            anchor INTEGER not NULL
            )
     */

    // record_id 作为某次记录的唯一表示

    val record = "Create Table If Not Exists record(" +
                        "     id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "     class_id CHAR(20) not null," +
                        "     stu_id CHAR(20) not null," +
                        "     subject_id VARCHAR(50) ," +
                        "     stime VARCHAR(20)," +
                        "     statu VARCHAR(20) not null," +
                        "     type_id CHAR(20) not null," +
                        "     info VARCHAR(255) ," +
                        "     title VARCHAR(255) not null," +
                        "     record_id VARCHAR(255) not null," +
                        "     create_time TEXT," +

                        "     status INTEGER not null," +
                        "     anchor INTEGER not NULL" +
                        "     )"

    /**
     * Create Table If Not Exists type(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            type CHAR(20) not null,
            img VARCHAR(200),
            class_id CHAR(20) not null,
            is_dialog INTEGER not null,

            status INTEGER not null,
            anchor INTEGER not NULL
            )
     */

    val type = "Create Table If Not Exists type(" +
                        "     id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "     title CHAR(20) not null," +
                        "     img VARCHAR(200)," +
                        "     class_id CHAR(20) not null," +
                        "     is_dialog INTEGER not null," +

                        "     status INTEGER not null," +
                        "     anchor INTEGER not NULL" +
                        "     )"

    val typeInit = "INSERT INTO type(id,title,img,class_id,is_dialog) VALUES(1,'课堂考勤','1','000',0),(2,'课堂表现','2','000',1),(3,'实验表现','3','000',1) "

    val typeKeyInit = "INSERT INTO type_key(type_id,type_key,weight) VALUES(1,'出勤',0),(1,'事假',1),(1,'病假',2),(1,'迟到',3),(1,'早退',4)," +
            "(1,'旷课',5),(2,'优',0),(2,'良',1),(2,'中',2),(2,'差',3)," +
            "(3,'优',0),(3,'良',1),(3,'中',2),(3,'差',3)"


    /**
     * Create Table If Not Exists subject(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            subject CHAR(20) not null,

            status INTEGER not null,
            anchor INTEGER not NULL
            )
     */

    val subject = "Create Table If Not Exists subject(" +
                        "     id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "     subject CHAR(20) not null," +
                        "     status INTEGER not null," +
                        "     anchor INTEGER not NULL" +
                        "     )"

    /**
     * 类型的枚举值
     * Create Table If Not Exists type_key(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            type_id INTEGER NOT NULL,
            type_key VARCHAR(20) NOT NULL,
            weight INTEGER Not Null,

            status INTEGER not null,
            anchor INTEGER not NULL
            )
     */
    val typeKey = "Create Table If Not Exists type_key(" +
                        "     id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        "     type_id INTEGER NOT NULL," +
                        "     type_key VARCHAR(20) NOT NULL," +
                        "     weight INTEGER Not Null," +

                        "     status INTEGER not null," +
                        "     anchor INTEGER not NULL" +
                        "     )"

    /**
     * Create Table If Not Exists facesData(
         id INTEGER PRIMARY KEY AUTOINCREMENT,  // id
         sId INTEGER NOT NULL,   // 学生id
         path VARCHAR(200) NOT NULL, // 数据路径
         create_time TEXT,  // 创建时间

         status INTEGER not null,
         anchor INTEGER not NULL
         )
     */
    val facesData = "Create Table If Not Exists facesData(" +
                        "     id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        "     s_id INTEGER NOT NULL, "+
                        "     path VARCHAR(200) NOT NULL," +
                        "     create_time TEXT," +

                        "     status INTEGER not null," +
                        "     anchor INTEGER not NULL" +
                        "     )"
}