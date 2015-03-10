package com.ambsellerapp.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ambsellerapp.modals.NewSeller;

public class DatabaseHandler extends SQLiteOpenHelper 
{
	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "ambsellerapp.db";

	// Contacts table name
	private static final String TABLE_NEW_SELLER = "seller";

	// Contacts Table Columns names
	private static final String KEY_ID = "_id";
	private static final String KEY_BUSSINESS_NAME = "bussiness_name";
	private static final String KEY_BENEFICIARY_NAME = "beneficiary_name";
	private static final String KEY_MOBILE_NO = "mobile_number";

	private static final String KEY_EMAIL_ADDRESS = "email_address";
	private static final String KEY_SHIPPING_ADDRESS = "shipping_address";
	private static final String KEY_BILLING_ADDRESS = "billing_address";
	private static final String KEY_SHIPPING_PINCODE = "shipping_pincode";
	private static final String KEY_BILLING_PINCODE = "billing_pincode";

	private static final String KEY_SHIPPING_CITY= "shipping_city";
	private static final String KEY_BILLING_CITY= "billing_city";
	private static final String KEY_SHIPPING_STATE= "shipping_state";
	private static final String KEY_BILLING_STATE= "billing_state";

	private static final String KEY_VERIFIED_STATUS= "key_verified";
	private static final String KEY_BANK_NAME= "bank_name";
	private static final String KEY_IFSC_CODE= "ifsc_code";
	private static final String KEY_ACCOUNT_NO= "account_no";
	private static final String KEY_BRANCH= "branch";
	private static final String KEY_TOP_UP_AMOUNT= "topup_amount";
	private static final String KEY_LOCATION= "location";
	private static final String KEY_SYNC_STATUS= "sync_status";
	private static final String KEY_LATITUDE= "latitude";
	private static final String KEY_LONGITUDE= "longitude";
	private static final String KEY_RD_ID= "rd_id";
	private static final String KEY_SELLER_ID= "seller_id";
	private static final String KEY_SYNC_PAYMENT_STATUS= "payment_sync_status";
	private static final String KEY_IS_NO_DUPLICATE= "number_duplicate";
	private static final String KEY_MOBILE_NUMBER_VERIFIED= "mobile_verified";
	private static final String KEY_CST_TIN_NUMBER= "cst_tin_no";
	private static final String KEY_CRM_SELLER_ID= "crm_seller_id";
	public static SQLiteDatabase db;
	private static DatabaseHandler instance;
	private Context mcontxt;

	public DatabaseHandler(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mcontxt = context;
		db=getWritableDatabase();

		//		instance = this;
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		String CREATE_NEW_SELLLER_TABLE =

				"CREATE TABLE IF NOT EXISTS  "
						+ TABLE_NEW_SELLER
						+ "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," + KEY_BUSSINESS_NAME
						+ " VARCHAR ," + KEY_BENEFICIARY_NAME + " VARCHAR,"	+ KEY_MOBILE_NO + " VARCHAR,"
						+ KEY_SHIPPING_PINCODE	+ " VARCHAR," + KEY_BILLING_PINCODE	+ " VARCHAR," + KEY_EMAIL_ADDRESS + " VARCHAR,"	
						+ KEY_VERIFIED_STATUS	+ " INTEGER," + KEY_BANK_NAME	+ " VARCHAR," + KEY_IFSC_CODE + " VARCHAR,"	+ KEY_ACCOUNT_NO
						+ " VARCHAR," + KEY_BRANCH	+ " VARCHAR,"	+ KEY_TOP_UP_AMOUNT	+ " INTEGER," + KEY_LOCATION
						+ " VARCHAR,"	+ KEY_BILLING_ADDRESS + " VARCHAR," + KEY_SHIPPING_ADDRESS + " VARCHAR," + KEY_SHIPPING_CITY	
						+ " VARCHAR," +	KEY_BILLING_CITY	+ " VARCHAR," + KEY_SYNC_STATUS
						+ " INTEGER," + KEY_SYNC_PAYMENT_STATUS+ " INTEGER,"	+ KEY_SHIPPING_STATE	+ " VARCHAR,"
						+ KEY_BILLING_STATE	+ " VARCHAR," +KEY_LATITUDE + " VARCHAR," + KEY_LONGITUDE	+ " VARCHAR,"
						+ KEY_RD_ID + " VARCHAR," + KEY_CST_TIN_NUMBER + " VARCHAR," +KEY_IS_NO_DUPLICATE + " INTEGER,"
						+ KEY_MOBILE_NUMBER_VERIFIED	+ " VARCHAR," + KEY_CRM_SELLER_ID	+ " VARCHAR," + KEY_SELLER_ID	+ " VARCHAR )";

		System.out.println("---Value of creation query is--"+CREATE_NEW_SELLLER_TABLE);

		db.execSQL(CREATE_NEW_SELLLER_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEW_SELLER);
		// Create tables again
		onCreate(db);
		db.close();
	}
	/**
	 * Method for getting the instance of the DatabaseHandler 
	 * @return
	 */
	public static synchronized DatabaseHandler getInstance(Context ctx) 
	{
		if (instance == null && ctx != null) 
		{
			new DatabaseHandler(ctx);
		}
		return instance;
	}
	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new Seller
	public long addNewSeller(NewSeller newSeller) 
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_BUSSINESS_NAME, newSeller.getBussiness_name()); // Seller Bussiness Name
		//        values.put(KEY_CONTACT_NAME, newSeller.getContact_name()); // contact name
		values.put(KEY_MOBILE_NO, newSeller.getMobile_no()); // seller mobile name
		values.put(KEY_EMAIL_ADDRESS, newSeller.getEmail_id()); // seller email address
		values.put(KEY_SHIPPING_ADDRESS, newSeller.getShipping_address()); // seller email address
		values.put(KEY_BILLING_ADDRESS, newSeller.getBilling_address()); // seller email address
		
		values.put(KEY_SHIPPING_PINCODE, newSeller.getShipping_pin_code()); // seller pincode name
		values.put(KEY_BILLING_PINCODE, newSeller.getBilling_pin_code()); // seller pincode name
		values.put(KEY_SHIPPING_CITY, newSeller.getShipping_city()); // seller location
		values.put(KEY_BILLING_CITY, newSeller.getBilling_city()); // seller location
		values.put(KEY_SHIPPING_STATE, newSeller.getShipping_state()); // seller location
		values.put(KEY_BILLING_STATE, newSeller.getBilling_state()); // seller location
		values.put(KEY_VERIFIED_STATUS, newSeller.getVerfication_status()); // seller bank name


		values.put(KEY_BANK_NAME, newSeller.getBank_name()); // seller bank name
		values.put(KEY_IFSC_CODE, newSeller.getIfsc_code()); // seller bank IFSC code
		System.out.println("====Inside insert Accountno  value in Database=="+newSeller.getAccount_no());
		values.put(KEY_ACCOUNT_NO, newSeller.getAccount_no()); // seller account no
		values.put(KEY_BRANCH, newSeller.getBranch()); // seller bank branch
		values.put(KEY_TOP_UP_AMOUNT, newSeller.getTopup_amount()); // seller top up amount

		values.put(KEY_LOCATION, newSeller.getLocation()); // seller location


		values.put(KEY_BENEFICIARY_NAME, newSeller.getBeneficaryName()); // seller location
		values.put(KEY_SYNC_STATUS, newSeller.getSync_status()); // seller location
		values.put(KEY_SYNC_PAYMENT_STATUS, newSeller.getPayment_status()); // seller payment status
		values.put(KEY_LATITUDE, newSeller.getLatitude()); // seller location
		values.put(KEY_LONGITUDE, newSeller.getLongitude()); // seller location
		values.put(KEY_MOBILE_NUMBER_VERIFIED, newSeller.getMobile_verified()); // seller location
		values.put(KEY_IS_NO_DUPLICATE, newSeller.getIs_no_duplicate()); // seller location
		values.put(KEY_CST_TIN_NUMBER, newSeller.getCst_tin_number()); // seller location
		values.put(KEY_CRM_SELLER_ID, newSeller.getCrm_seller_id()); // seller location
		
		//        values.put(KEY_ID, newSeller.getId()); // seller location


		// Inserting Row
		long id=0;
		try 
		{
			id = db.insert(TABLE_NEW_SELLER, null, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close(); // Closing database connection
		return id;
	}

	// Getting single contact
	//    NewSeller getSeller(int id) {
	//        SQLiteDatabase db = this.getReadableDatabase();
	// 
	//        Cursor cursor = db.query(TABLE_NEW_SELLER, new String[] { KEY_ID,
	//                KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
	//                new String[] { String.valueOf(id) }, null, null, null, null);
	//        if (cursor != null)
	//            cursor.moveToFirst();
	// 
	//        NewSeller contact = new Contact(Integer.parseInt(cursor.getString(0)),
	//                cursor.getString(1), cursor.getString(2));
	//        // return contact
	//        return contact;
	//    }
	//     
	// Getting All Sellers
	public List<NewSeller> getAllSellers() {
		List<NewSeller> contactList = new ArrayList<NewSeller>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NEW_SELLER;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				NewSeller contact = new NewSeller();
				contact.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));

				contact.setBussiness_name(cursor.getString(cursor.getColumnIndex("bussiness_name")));
				contact.setMobile_no(cursor.getString(cursor.getColumnIndex("mobile_number")));
				contact.setEmail_id(cursor.getString(cursor.getColumnIndex("email_address")));
				contact.setBilling_address(cursor.getString(cursor.getColumnIndex(KEY_BILLING_ADDRESS)));
				contact.setShipping_address(cursor.getString(cursor.getColumnIndex(KEY_SHIPPING_ADDRESS)));
				contact.setShipping_pin_code(cursor.getString(cursor.getColumnIndex(KEY_SHIPPING_PINCODE)));
				contact.setBilling_pin_code(cursor.getString(cursor.getColumnIndex(KEY_BILLING_PINCODE)));
				contact.setShipping_city(cursor.getString(cursor.getColumnIndex(KEY_SHIPPING_CITY)));
				contact.setBilling_city(cursor.getString(cursor.getColumnIndex(KEY_BILLING_CITY)));
				contact.setShipping_state(cursor.getString(cursor.getColumnIndex(KEY_SHIPPING_STATE)));
				contact.setBilling_state(cursor.getString(cursor.getColumnIndex(KEY_BILLING_STATE)));
				contact.setVerfication_status(cursor.getInt(cursor.getColumnIndex("key_verified")));
				contact.setBank_name(cursor.getString(cursor.getColumnIndex("bank_name")));
				contact.setIfsc_code(cursor.getString(cursor.getColumnIndex("ifsc_code")));
				contact.setTopup_amount(cursor.getInt(cursor.getColumnIndex(KEY_TOP_UP_AMOUNT)));
				contact.setAccount_no(cursor.getString(cursor.getColumnIndex("account_no")));
				contact.setBranch(cursor.getString(cursor.getColumnIndex("branch")));
				contact.setLatitude(cursor.getString(cursor.getColumnIndex("latitude")));
				contact.setLongitude(cursor.getString(cursor.getColumnIndex("longitude")));
				contact.setLocation(cursor.getString(cursor.getColumnIndex("location")));
				contact.setBeneficaryName(cursor.getString(cursor.getColumnIndex("beneficiary_name")));
				contact.setMobile_verified(cursor.getString(cursor.getColumnIndex("mobile_verified")));
				contact.setCst_tin_number(cursor.getString(cursor.getColumnIndex(KEY_CST_TIN_NUMBER)));
				contact.setCrm_seller_id(cursor.getString(cursor.getColumnIndex(KEY_CRM_SELLER_ID)));

				// Adding contact to list
				contactList.add(contact);
			} while (cursor.moveToNext());
		}

		System.out.println("----value of All details---"+Arrays.toString(contactList.toArray()));
		// return contact list
		db.close();
		return contactList;
	}

	// Getting Individual Seller
	public NewSeller getSellers(int id) {
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NEW_SELLER + " where "+ KEY_ID + "=" + id;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		NewSeller newSeller=null;
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				newSeller = new NewSeller();
				//                newSeller.setId(cursor.getString(0));
				//                contact.setBussiness_name(Name(cursor.getString(1));
				//                contact.setPhoneNumber(cursor.getString(2));
				// Adding contact to list
				//                singleSeller.add(contact);
			} while (cursor.moveToNext());
		}
		db.close();
		// return contact list
		return newSeller;
	}

	// Getting All not Synced Server
	public List<NewSeller> getNotSyncedSellers() {
		List<NewSeller> contactList = new ArrayList<NewSeller>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NEW_SELLER +" where  ("+KEY_SYNC_PAYMENT_STATUS +"=0  and "+KEY_SYNC_STATUS +"=0 and "+KEY_IS_NO_DUPLICATE +"=0 )";
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list TODO
		if (cursor.getCount()!=0 && cursor.moveToFirst()) {
			do {
				NewSeller contact = new NewSeller();
				contact.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
				//				contact.setId(cursor.getString(Integer.parseInt(cursor.getString(0))

				contact.setBussiness_name(cursor.getString(cursor.getColumnIndex("bussiness_name")));
				contact.setMobile_no(cursor.getString(cursor.getColumnIndex("mobile_number")));
				contact.setEmail_id(cursor.getString(cursor.getColumnIndex("email_address")));
				contact.setBilling_address(cursor.getString(cursor.getColumnIndex(KEY_BILLING_ADDRESS)));
				contact.setShipping_address(cursor.getString(cursor.getColumnIndex(KEY_SHIPPING_ADDRESS)));
				contact.setShipping_pin_code(cursor.getString(cursor.getColumnIndex(KEY_SHIPPING_PINCODE)));
				contact.setBilling_pin_code(cursor.getString(cursor.getColumnIndex(KEY_BILLING_PINCODE)));
				contact.setShipping_city(cursor.getString(cursor.getColumnIndex(KEY_SHIPPING_CITY)));
				contact.setBilling_city(cursor.getString(cursor.getColumnIndex(KEY_BILLING_CITY)));
				contact.setShipping_state(cursor.getString(cursor.getColumnIndex(KEY_SHIPPING_STATE)));
				contact.setBilling_state(cursor.getString(cursor.getColumnIndex(KEY_BILLING_STATE)));
				contact.setVerfication_status(cursor.getInt(cursor.getColumnIndex("key_verified")));
				contact.setBank_name(cursor.getString(cursor.getColumnIndex("bank_name")));
				contact.setIfsc_code(cursor.getString(cursor.getColumnIndex("ifsc_code")));
				contact.setTopup_amount(cursor.getInt(cursor.getColumnIndex(KEY_TOP_UP_AMOUNT)));
				contact.setAccount_no(cursor.getString(cursor.getColumnIndex("account_no")));
				contact.setBranch(cursor.getString(cursor.getColumnIndex("branch")));
				contact.setLatitude(cursor.getString(cursor.getColumnIndex("latitude")));
				contact.setLongitude(cursor.getString(cursor.getColumnIndex("longitude")));
				contact.setLocation(cursor.getString(cursor.getColumnIndex("location")));
				contact.setBeneficaryName(cursor.getString(cursor.getColumnIndex("beneficiary_name")));
				contact.setSync_status(cursor.getInt(cursor.getColumnIndex("sync_status")));
				contact.setRdId(cursor.getString(cursor.getColumnIndex(KEY_RD_ID)));
				contact.setSeller_Id(cursor.getString(cursor.getColumnIndex(KEY_SELLER_ID)));
				contact.setMobile_verified(cursor.getString(cursor.getColumnIndex(KEY_MOBILE_NUMBER_VERIFIED)));
				contact.setCst_tin_number(cursor.getString(cursor.getColumnIndex(KEY_CST_TIN_NUMBER)));
				contact.setCrm_seller_id(cursor.getString(cursor.getColumnIndex(KEY_CRM_SELLER_ID)));
				// Adding contact to list
				contactList.add(contact);
			} while (cursor.moveToNext());
		}
		System.out.println("----Value of contact list size is--"+contactList.size());
		cursor.close();
		db.close();
		// return contact list
		return contactList;
	}

	// Updating single Seller Payment status
	public void updateSellerSyncStatus(int id) 
	{
		SQLiteDatabase db = this.getWritableDatabase();

		String sql="update "+ TABLE_NEW_SELLER + 
				" set "+ KEY_SYNC_STATUS  + " = 1 where "+KEY_ID+"="+id +";";
		System.out.println("----updateSellerPaymentStatus Query is --"+sql);
		db.execSQL(sql);
		db.close();
	}

	// Updating single Seller Payment status
	public void updateSellerPaymentStatus(int id) 
	{
		SQLiteDatabase db = this.getWritableDatabase();

		String sql="update "+ TABLE_NEW_SELLER + 
				" set "+ KEY_SYNC_PAYMENT_STATUS  + " = 1 where "+KEY_ID+"="+id +";";
		System.out.println("----updateSellerPaymentStatus Query is --"+sql);
		db.execSQL(sql);
		db.close();
	}

	// Updating single Seller Status
	public void updateSellerStatus(int id,String rDId,String sellerFinalId) 
	{
		SQLiteDatabase db = this.getWritableDatabase();

		String sql="update "+ TABLE_NEW_SELLER +
				//				" set "+ KEY_SYNC_STATUS  + " = 1 where "+KEY_ID+"="+id +";";
				" set "+ KEY_SYNC_STATUS  + " = 1, "+  KEY_RD_ID+"='rDId', "+ KEY_SELLER_ID+"='sellerFinalId' where "+KEY_ID+"="+id +";";
		System.out.println("----updateSellerStatus Query is --"+sql);
		db.execSQL(sql);
		db.close();
		// updating row
		//        return db.update(TABLE_NEW_SELLER, null, KEY_ID + " = ?",
		//                new String[] { String.valueOf(contact.getID()) });

	}


	// Updating single Seller Payment status
	public void updateSellerIsNumberDuplicateStatus(int id) 
	{
		SQLiteDatabase db = this.getWritableDatabase();

		String sql="update "+ TABLE_NEW_SELLER + 
				" set "+ KEY_IS_NO_DUPLICATE  + " = 1 where "+KEY_ID+"="+id +";";
		db.execSQL(sql);
		db.close();
	}
	// 
	//    // Deleting single contact
	//    public void deleteContact(Contact contact) 
	//    {
	//        SQLiteDatabase db = this.getWritableDatabase();
	//        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
	//                new String[] { String.valueOf(contact.getID()) });
	//        db.close();
	//    }
	// 
	// 
	//    // Getting contacts Count
	//    public int getContactsCount()
	//    {
	//        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
	//        SQLiteDatabase db = this.getReadableDatabase();
	//        Cursor cursor = db.rawQuery(countQuery, null);
	//        cursor.close();
	// 
	//        // return count
	//        return cursor.getCount();
	//    }

	public void insertSellerData(String crm_seller_id,
			String crm_seller_name, String crm_seller_contact,
			String crm_seller_email, String crm_seller_pincode, String type,
			String crm_seller_update, String crm_updated_date_time,
			String created, String updated, int status) {

		ContentValues values = new ContentValues();
		values.put("crm_seller_id", crm_seller_id);
		values.put("crm_seller_name", crm_seller_name);
		values.put("crm_seller_contact", crm_seller_contact);
		values.put("crm_seller_email", crm_seller_email);
		values.put("crm_seller_pincode", crm_seller_pincode);
		values.put("type", type);
		values.put("crm_seller_update", crm_seller_update);
		values.put("crm_updated_date_time", crm_updated_date_time);
		values.put("created", created);
		values.put("updated", updated);
		values.put("status", status);
		try {

			//			String selectQuery = "select _id, crm_seller_id from seller where crm_seller_id="+crm_seller_id;
			//			db.execSQL(selectQuery);
			//			Cursor cursor = db.rawQuery(selectQuery, null);
			//			// Log.d("DBHelper", "@Awd::Cursor Length: " + cursor.getCount());
			//			while (cursor.moveToNext()) {
			//			}
			db.insert("seller", null, values);

		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			if (db != null)
				db.close();
		}

	}

}