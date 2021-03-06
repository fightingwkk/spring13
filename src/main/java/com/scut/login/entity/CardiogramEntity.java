package com.scut.login.entity;
/**
 #心电图表
 create table cardiogram(
 id int auto_increment,
 wechat_id varchar(300) not null,
 cardiogram varchar(300) not null,
 date date not null,
 remark varchar(300) not null,
 primary key(id),
 )engine=INNODB default charset=utf8;
 */
public class CardiogramEntity {
    private int id;
    private String wechat_id;//患者微信号id
    private String cardiogram;//心电图URL
    private String date;//测量日期
    private String remark;//备注

    public CardiogramEntity() {
    }

    public CardiogramEntity(int id, String wechat_id, String cardiogram, String date, String remark) {
        this.id = id;
        this.wechat_id = wechat_id;
        this.cardiogram = cardiogram;
        this.date = date;
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWechat_id() {
        return wechat_id;
    }

    public void setWechat_id(String wechat_id) {
        this.wechat_id = wechat_id;
    }

    public String getCardiogram() {
        return cardiogram;
    }

    public void setCardiogram(String cardiogram) {
        this.cardiogram = cardiogram;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
