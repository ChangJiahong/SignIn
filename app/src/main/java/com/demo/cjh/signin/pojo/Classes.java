package com.demo.cjh.signin.pojo;

public class Classes extends Client {

    /**
     * 班级编号
     */
    private String classId;

    /**
     * 班级名
     */
    private String className;

    /**
     * 班级信息
     */
    private String info;

    /**
     * 二级学院名
     */
    private String institute;

    /**
     * 专业名
     */
    private String speciality;

    /**
     * 创建时间
     */
    private String createTime;


    public Classes(){};

    public Classes(String classId) {
        this.classId = classId;
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
     * 获取班级名
     *
     * @return class_name - 班级名
     */
    public String getClassName() {
        return className;
    }

    /**
     * 设置班级名
     *
     * @param className 班级名
     */
    public void setClassName(String className) {
        this.className = className == null ? null : className.trim();
    }

    /**
     * 获取班级信息
     *
     * @return info - 班级信息
     */
    public String getInfo() {
        return info;
    }

    /**
     * 设置班级信息
     *
     * @param info 班级信息
     */
    public void setInfo(String info) {
        this.info = info == null ? null : info.trim();
    }

    /**
     * 获取二级学院名
     *
     * @return institute - 二级学院名
     */
    public String getInstitute() {
        return institute;
    }

    /**
     * 设置二级学院名
     *
     * @param institute 二级学院名
     */
    public void setInstitute(String institute) {
        this.institute = institute == null ? null : institute.trim();
    }

    /**
     * 获取专业名
     *
     * @return speciality - 专业名
     */
    public String getSpeciality() {
        return speciality;
    }

    /**
     * 设置专业名
     *
     * @param speciality 专业名
     */
    public void setSpeciality(String speciality) {
        this.speciality = speciality == null ? null : speciality.trim();
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