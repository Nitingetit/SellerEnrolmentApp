package com.ambsellerapp.modals;

import java.io.Serializable;

public class NewSeller implements Serializable
{

	int id=0;
	

	String bussiness_name="";
	String mobile_no="";
	String email_id="";
	
	String billing_address="";
	String shipping_address="";
	String billing_pin_code="";
	String billing_city="";
	String billing_state="";
	String shipping_pin_code="";
	String shipping_city="";
	String shipping_state="";
	int verfication_status=0;
	String bank_name="";
	String account_no="";
	String ifsc_code="";
	String beneficaryName="";
	String branch="";
	boolean isLead;
	String leadId;


	public boolean isLead() {
		return isLead;
	}

	public void setLead(boolean isLead) {
		this.isLead = isLead;
	}

	public String getLeadId() {
		return leadId;
	}

	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}

	int topup_amount=1;
	String location="";
	int sync_status=0;

	int payment_status=0;
	String latitude="";
	String longitude="";
	
	String rdId ="";
	String seller_Id ="";

	int is_no_duplicate=0;
	String mobile_verified="";
	String cst_tin_number="";
			
	String crm_seller_id="";
	
	


	public String getBilling_address()
	{
		return billing_address;
	}

	public void setBilling_address(String billing_address) 
	{
		this.billing_address = billing_address;
	}

	public String getShipping_address() 
	{
		return shipping_address;
	}

	public void setShipping_address(String shipping_address) 
	{
		this.shipping_address = shipping_address;
	}

	public String getBilling_pin_code() 
	{
		return billing_pin_code;
	}

	public void setBilling_pin_code(String billing_pin_code) 
	{
		this.billing_pin_code = billing_pin_code;
	}

	public String getBilling_city() 
	{
		return billing_city;
	}

	public void setBilling_city(String billing_city) 
	{
		this.billing_city = billing_city;
	}

	public String getBilling_state() 
	{
		return billing_state;
	}

	public void setBilling_state(String billing_state) 
	{
		this.billing_state = billing_state;
	}

	public String getShipping_pin_code() 
	{
		return shipping_pin_code;
	}

	public void setShipping_pin_code(String shipping_pin_code) 
	{
		this.shipping_pin_code = shipping_pin_code;
	}

	public String getShipping_city() 
	{
		return shipping_city;
	}

	public void setShipping_city(String shipping_city) 
	{
		this.shipping_city = shipping_city;
	}

	public String getShipping_state() 
	{
		return shipping_state;
	}

	public void setShipping_state(String shipping_state) 
	{
		this.shipping_state = shipping_state;
	}



	public String getCst_tin_number() {
		return cst_tin_number;
	}

	public void setCst_tin_number(String cst_tin_number) {
		this.cst_tin_number = cst_tin_number;
	}

	public String getMobile_verified()
	{
		return mobile_verified;
	}

	public void setMobile_verified(String mobile_verified) 
	{
		this.mobile_verified = mobile_verified;
	}

	public NewSeller()
	{
		this.id=id;
	}

	public String getBussiness_name() {
		return bussiness_name;
	}
	public void setBussiness_name(String bussiness_name) {
		this.bussiness_name = bussiness_name;
	}
	public String getMobile_no() {
		return mobile_no;
	}
	public void setMobile_no(String mobile_no) {
		this.mobile_no = mobile_no;
	}
	public String getEmail_id() {
		return email_id;
	}
	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}
	public int getVerfication_status() {
		return verfication_status;
	}
	public void setVerfication_status(int verfication_status) {
		this.verfication_status = verfication_status;
	}
	public String getBank_name() {
		return bank_name;
	}
	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}
	public String getAccount_no() {
		return account_no;
	}
	public void setAccount_no(String account_no) {
		this.account_no = account_no;
	}
	public String getIfsc_code() {
		return ifsc_code;
	}
	public void setIfsc_code(String ifsc_code) {
		this.ifsc_code = ifsc_code;
	}
	public String getBeneficaryName() {
		return beneficaryName;
	}
	public void setBeneficaryName(String beneficaryName) {
		this.beneficaryName = beneficaryName;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getSync_status() {
		return sync_status;
	}
	public void setSync_status(int sync_status) {
		this.sync_status = sync_status;
	}
	public String getLatitude() 
	{
		return latitude;
	}

	public void setLatitude(String latitude) 
	{
		this.latitude = latitude;
	}

	public String getLongitude() 
	{
		return longitude;
	}

	public void setLongitude(String longitude) 
	{
		this.longitude = longitude;
	}
	
	
	
	public String getRdId() {
		return rdId;
	}

	public void setRdId(String rdId) {
		this.rdId = rdId;
	}

	public String getSeller_Id() {
		return seller_Id;
	}

	public void setSeller_Id(String seller_Id) {
		this.seller_Id = seller_Id;
	}

	public int getId() 
	{
		return id;
	}
	public void setId(int id) 
	{
		this.id = id;
	}
	public int getPayment_status() 
	{
		return payment_status;
	}

	public void setPayment_status(int payment_status) 
	{
		this.payment_status = payment_status;
	}
	
	public int getTopup_amount() {
		return topup_amount;
	}

	public void setTopup_amount(int topup_amount) {
		this.topup_amount = topup_amount;
	}
	
	public int getIs_no_duplicate() 
	{
		return is_no_duplicate;
	}

	public void setIs_no_duplicate(int is_no_duplicate) 
	{
		this.is_no_duplicate = is_no_duplicate;
	}
	
	public String getCrm_seller_id()
	{
		return crm_seller_id;
	}

	public void setCrm_seller_id(String crm_seller_id) 
	{
		this.crm_seller_id = crm_seller_id;
	}
	/*public NewSeller(Parcel in)
	{
		String[] data = new String[20];
		in.readStringArray(data);
		this.bussiness_name = data[0];
		this.mobile_no = data[1];
		this.email_id = data[2]; 
		this.address= data[3];
		this.pin_code= data[4];
		this.city= data[5];
		this.state= data[6];
		this.bank_name= data[7];
		
		this.account_no=data[8];
		this.ifsc_code=data[9];
		this.beneficaryName=data[10];
		this.branch=data[11];
		this.topup_amount=data[12];
		this.latitude=data[13];
		this.longitude=data[14];
		this.location=data[15];
		this.id=data[16];
	}


	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeStringArray(new String[] { this.bussiness_name,
				this.mobile_no, this.email_id,
				this.address, this.pin_code, 
				this.city, this.state ,
				this.bank_name, this.account_no, 
				this.ifsc_code,	this.beneficaryName,
				this.branch, this.topup_amount,
				this.latitude, this.longitude, this.location, this.id});
	}

	public static final Parcelable.Creator<NewSeller> CREATOR = new Parcelable.Creator<NewSeller>() 
	{
		@Override
		public NewSeller createFromParcel(Parcel in) 
		{
			return new NewSeller(in);
		}

		@Override
		public NewSeller[] newArray(int size) 
		{
			return new NewSeller[size];
		}
	};*/
	
}
