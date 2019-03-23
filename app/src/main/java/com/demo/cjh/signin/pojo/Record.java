package com.demo.cjh.signin.pojo;

import java.util.Date;

public class Record extends Client{

    /**
     * 班级id
     */
    private String classId;

    /**
     * 学生id
     */
    private String stuId;

    /**
     * 学科id
     */
    private String subjectId;

    /**
     * 上课时间
     */
    private String stime;

    /**
     * 状态信息{优良中差、ABCD、出勤|请假。。。}
     */
    private String statu;

    /**
     * 记录类型{考勤、课堂表现、平时成绩。。。}
     */
    private String typeId;

    /**
     * 记录信息
     */
    private String info;

    /**
     * 记录标题|默认是时间
     */
    private String title;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 记录识别码
     */
    private String recordId;

    /**
     * 获取班级id
     *
     * @return class_id - 班级id
     */
    public String getClassId() {
        return classId;
    }

    /**
     * 设置班级id
     *
     * @param classId 班级id
     */
    public void setClassId(String classId) {
        this.classId = classId == null ? null : classId.trim();
    }

    /**
     * 获取学生id
     *
     * @return stu_id - 学生id
     */
    public String getStuId() {
        return stuId;
    }

    /**
     * 设置学生id
     *
     * @param stuId 学生id
     */
    public void setStuId(String stuId) {
        this.stuId = stuId == null ? null : stuId.trim();
    }

    /**
     * 获取学科id
     *
     * @return subject_id - 学科id
     */
    public String getSubjectId() {
        return subjectId;
    }

    /**
     * 设置学科id
     *
     * @param subjectId 学科id
     */
    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId == null ? null : subjectId.trim();
    }

    /**
     * 获取上课时间
     *
     * @return stime - 上课时间
     */
    public String getStime() {
        return stime;
    }

    /**
     * 设置上课时间
     *
     * @param stime 上课时间
     */
    public void setStime(String stime) {
        this.stime = stime;
    }

    /**
     * 获取状态信息{优良中差、ABCD、出勤|请假。。。}
     *
     * @return status - 状态信息{优良中差、ABCD、出勤|请假。。。}
     */
    public String getStatu() {
        return statu;
    }

    /**
     * 设置状态信息{优良中差、ABCD、出勤|请假。。。}
     *
     * @param status 状态信息{优良中差、ABCD、出勤|请假。。。}
     */
    public void setStatu(String status) {
        this.statu = status == null ? null : status.trim();
    }

    /**
     * 获取记录类型{考勤、课堂表现、平时成绩。。。}
     *
     * @return type - 记录类型{考勤、课堂表现、平时成绩。。。}
     */
    public String getTypeId() {
        return typeId;
    }

    /**
     * 设置记录类型{考勤、课堂表现、平时成绩。。。}
     *
     * @param typeId 记录类型{考勤、课堂表现、平时成绩。。。}
     */
    public void setTypeId(String typeId) {
        this.typeId = typeId == null ? null : typeId.trim();
    }

    /**
     * 获取记录信息
     *
     * @return info - 记录信息
     */
    public String getInfo() {
        return info;
    }

    /**
     * 设置记录信息
     *
     * @param info 记录信息
     */
    public void setInfo(String info) {
        this.info = info == null ? null : info.trim();
    }

    /**
     * 获取记录标题|默认是时间
     *
     * @return title - 记录标题|默认是时间
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置记录标题|默认是时间
     *
     * @param title 记录标题|默认是时间
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
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

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
}