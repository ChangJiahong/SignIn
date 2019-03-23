package com.demo.cjh.signin.pojo;

import java.util.List;

/**
 * @author CJH
 * on 2018/12/17
 */

public class Tables {

    private String uId;

    private List<Classes> classesList;

    private List<Record> recordList;

    private List<Stu> stuList;

    private List<Subject> subjectList;

    private List<Type> typeList;

    private List<TypeKey> typeKeyList;

    private List<FaceRecord> faceRecordList;


    public Tables() {
    }

    public Tables(String uId) {
        this.uId = uId;
    }

    public List<Classes> getClassesList() {
        return classesList;
    }

    public void setClassesList(List<Classes> classesList) {
        this.classesList = classesList;
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }

    public List<Stu> getStuList() {
        return stuList;
    }

    public void setStuList(List<Stu> stuList) {
        this.stuList = stuList;
    }

    public List<Subject> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }

    public List<Type> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<Type> typeList) {
        this.typeList = typeList;
    }

    public List<TypeKey> getTypeKeyList() {
        return typeKeyList;
    }

    public void setTypeKeyList(List<TypeKey> typeKeyList) {
        this.typeKeyList = typeKeyList;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public List<FaceRecord> getFaceRecordList() {
        return faceRecordList;
    }

    public void setFaceRecordList(List<FaceRecord> faceRecordList) {
        this.faceRecordList = faceRecordList;
    }
}
