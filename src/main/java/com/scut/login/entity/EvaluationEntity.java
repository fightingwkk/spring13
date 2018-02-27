package com.scut.login.entity;



/**
 * @author WANGKK
 
 create table evaluation (
id int not null auto_increment,
 wechat_id varchar(300) not null,
 phone varchar(20) not null,
 datetime timestamp not null default CURRENT_TIMESTAMP,
 content varchar(300) not null,
 profession varchar(5),
 attitude varchar(5),
 speed varchar(5),
 isread tinyint default 0,
 grade varchar(5),
anonymity tinyint default 0,
delete_status int not null default 0,
 primary key(id)
 )engine=INNODB default charset=utf8;
 */
public class EvaluationEntity {

	private int id;
	private String wechat_id;//患者
	private String phone;//医生
	private String datetime;//评价时间
	private String content;//评价内容
	private String profession;//专业程度
	private String attitude;//回复态度
	private String speed;//回复速度
	private int  isread;//是否阅读
	private String grade;//总评分
	private int anonymity;//匿名
	private int delete_status;//删除
	private String head_pic;
	private String name;
	private String sex;
	private int age;


	public EvaluationEntity() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDelete_status() {
		return delete_status;
	}

	public void setDelete_status(int delete_status) {
		this.delete_status = delete_status;
	}

	public String getWechat_id() {
		return wechat_id;
	}
	public void setWechat_id(String wechat_id) {
		this.wechat_id = wechat_id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getAttitude() {
		return attitude;
	}
	public void setAttitude(String attitude) {
		this.attitude = attitude;
	}
	public String getSpeed() { 
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public int getIsread() {
		return isread;
	}

	public void setIsread(int isread) {
		this.isread = isread;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public int getAnonymity() {
		return anonymity;
	}

	public void setAnonymity(int anonymity) {
		this.anonymity = anonymity;
	}

	public String getHead_pic() {
		return head_pic;
	}

	public void setHead_pic(String head_pic) {
		this.head_pic = head_pic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
