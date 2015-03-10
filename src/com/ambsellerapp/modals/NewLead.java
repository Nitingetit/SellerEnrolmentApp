package com.ambsellerapp.modals;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class NewLead implements Serializable{
String business_name;
String mobile_number;
String pin_code;
String state;
String city;
String address;
String remarks;
String date;
String time;
String status;
String id;
String assigndUserId;
String leadSource;
String leadNumber;

public String getLeadNumber() {
	return leadNumber;
}
public void setLeadNumber(String leadNumber) {
	this.leadNumber = leadNumber;
}
public String getLeadSource() {
	return leadSource;
}
public void setLeadSource(String leadSource) {
	this.leadSource = leadSource;
}
public String getAssigndUserId() {
	return assigndUserId;
}
public void setAssigndUserId(String assigndUserId) {
	this.assigndUserId = assigndUserId;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public String getBusiness_name() {
	return business_name;
}
public void setBusiness_name(String business_name) {
	this.business_name = business_name;
}
public String getMobile_number() {
	return mobile_number;
}
public void setMobile_number(String mobile_number) {
	this.mobile_number = mobile_number;
}
public String getPin_code() {
	return pin_code;
}
public void setPin_code(String pin_code) {
	this.pin_code = pin_code;
}
public String getState() {
	return state;
}
public void setState(String state) {
	this.state = state;
}
public String getCity() {
	return city;
}
public void setCity(String city) {
	this.city = city;
}
public String getAddress() {
	return address;
}
public void setAddress(String address) {
	this.address = address;
}
public String getRemarks() {
	return remarks;
}
public void setRemarks(String remarks) {
	this.remarks = remarks;
}
public String getDate() {
	return date;
}
public void setDate(String date) {
	date=date.replace("Date : ", "");
	//date = new SimpleDateFormat("yyyy-MM-dd").format(date);
	this.date = date;
}
public String getTime() {
	return time;
}
public void setTime(String time) {
	time=time.replace("Time : ", "");
	//time=new SimpleDateFormat("HH:mm:ss").format(time);
	this.time = time;
}


}
