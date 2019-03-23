package com.demo.cjh.signin.pojo;

/**
 * Created by CJH
 * on 2018/11/24
 */
public class Type extends Client{
    /**
     * 类型名
     */
    private String title = "" ;
    /**
     * 图片路径
     */
    private String img = "" ;

    /**
     * 是否使用弹窗
     */
    public Integer isDialog = 0 ;

    /**
     * 班级编号
     */
    private String classId = "" ;

    public Type(){}

    public Type(String cid,String title,String img,String classId){
        this.setCId(cid);
        this.title = title;
        this.img = img;
        this.classId = classId;
    }
    public Type(String title,String img,String classId,Boolean isDialog){
        this.title = title;
        this.img = img;
        this.classId = classId;
        setDialog(isDialog);
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Boolean getDialog(){
        return isDialog == 1;
    }
    public void setDialog(Boolean dialog) {
        isDialog = dialog?1:0;
    }
}
