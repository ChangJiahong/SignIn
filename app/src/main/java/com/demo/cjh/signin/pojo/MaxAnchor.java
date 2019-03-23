package com.demo.cjh.signin.pojo;

/**
 * @author CJH
 * on 2018/12/19
 */

public class MaxAnchor {

    private String uId;

    private Long classesMaxAnchor;

    private Long recordMaxAnchor;

    private Long stuMaxAnchor;

    private Long subjectMaxAnchor;

    private Long typeMaxAnchor;

    private Long typeKeyMaxAnchor;

    public MaxAnchor(String uId) {
        this.uId = uId;
    }

    public Long getClassesMaxAnchor() {
        return classesMaxAnchor;
    }

    public void setClassesMaxAnchor(Long classesMaxAnchor) {
        this.classesMaxAnchor = classesMaxAnchor;
    }

    public Long getRecordMaxAnchor() {
        return recordMaxAnchor;
    }

    public void setRecordMaxAnchor(Long recordMaxAnchor) {
        this.recordMaxAnchor = recordMaxAnchor;
    }

    public Long getStuMaxAnchor() {
        return stuMaxAnchor;
    }

    public void setStuMaxAnchor(Long stuMaxAnchor) {
        this.stuMaxAnchor = stuMaxAnchor;
    }

    public Long getSubjectMaxAnchor() {
        return subjectMaxAnchor;
    }

    public void setSubjectMaxAnchor(Long subjectMaxAnchor) {
        this.subjectMaxAnchor = subjectMaxAnchor;
    }

    public Long getTypeMaxAnchor() {
        return typeMaxAnchor;
    }

    public void setTypeMaxAnchor(Long typeMaxAnchor) {
        this.typeMaxAnchor = typeMaxAnchor;
    }

    public Long getTypeKeyMaxAnchor() {
        return typeKeyMaxAnchor;
    }

    public void setTypeKeyMaxAnchor(Long typeKeyMaxAnchor) {
        this.typeKeyMaxAnchor = typeKeyMaxAnchor;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
