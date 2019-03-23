package com.demo.cjh.signin.pojo;

import java.io.Serializable;
import java.util.Date;

public class Stu extends Client {

    /**
     * 班级编号
     */
    private String classId;

    /**
     * 学生编号
     */
    private String stuId;

    /**
     * 学生姓名
     */
    private String stuName;

    /**
     * 脸数据信息1
     */
    private String stuFace1;

    /**
     * 脸数据信息2
     */
    private String stuFace2;

    /**
     * 脸数据信息3
     */
    private String stuFace3;

    /**
     * 创建时间
     */
    private String createTime;

    public Stu() { }

    public Stu(String classId, String stuId) {
        this.classId = classId;
        this.stuId = stuId;
    }
    public Stu(String classId, String stuId,String stuName) {
        this.classId = classId;
        this.stuId = stuId;
        this.stuName = stuName;
    }

    public StuInfo toStuInfo(){
        return new StuInfo(this);
    }


    /**
     * 获取班级编号
     *
     * @return class_id - 班级编号
     */
    public String getClassId() {
        return classId;
    }

    /**
     * 设置班级编号
     *
     * @param classId 班级编号
     */
    public void setClassId(String classId) {
        this.classId = classId == null ? null : classId.trim();
    }

    /**
     * 获取学生编号
     *
     * @return stu_id - 学生编号
     */
    public String getStuId() {
        return stuId;
    }

    /**
     * 设置学生编号
     *
     * @param stuId 学生编号
     */
    public void setStuId(String stuId) {
        this.stuId = stuId == null ? null : stuId.trim();
    }

    /**
     * 获取学生姓名
     *
     * @return stu_name - 学生姓名
     */
    public String getStuName() {
        return stuName;
    }

    /**
     * 设置学生姓名
     *
     * @param stuName 学生姓名
     */
    public void setStuName(String stuName) {
        this.stuName = stuName == null ? null : stuName.trim();
    }

    /**
     * 获取脸数据信息1
     *
     * @return stu_face1 - 脸数据信息1
     */
    public String getStuFace1() {
        return stuFace1;
    }

    /**
     * 设置脸数据信息1
     *
     * @param stuFace1 脸数据信息1
     */
    public void setStuFace1(String stuFace1) {
        this.stuFace1 = stuFace1 == null ? null : stuFace1.trim();
    }

    /**
     * 获取脸数据信息2
     *
     * @return stu_face2 - 脸数据信息2
     */
    public String getStuFace2() {
        return stuFace2;
    }

    /**
     * 设置脸数据信息2
     *
     * @param stuFace2 脸数据信息2
     */
    public void setStuFace2(String stuFace2) {
        this.stuFace2 = stuFace2 == null ? null : stuFace2.trim();
    }

    /**
     * 获取脸数据信息3
     *
     * @return stu_face3 - 脸数据信息3
     */
    public String getStuFace3() {
        return stuFace3;
    }

    /**
     * 设置脸数据信息3
     *
     * @param stuFace3 脸数据信息3
     */
    public void setStuFace3(String stuFace3) {
        this.stuFace3 = stuFace3 == null ? null : stuFace3.trim();
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}