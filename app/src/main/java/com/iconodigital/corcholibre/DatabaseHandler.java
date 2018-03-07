package com.iconodigital.corcholibre;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ARIJIT on 12/5/2016.
 */

public class DatabaseHandler extends SQLiteOpenHelper {


    // Logcat tag
    private static final String LOG = DatabaseHandler.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "com.com.iconodigital.com.iconodigital.com.iconodigital.com.iconodigital.com.iconodigital.corcholibre";


    // Table Names
    private static final String TABLE_LIST_PORDUCTS = "list_products";
    private static final String TABLE_LISTS = "lists";

    // Common column names
    private static final String KEY_PRODUCT_ID = "product_id";
    private static final String KEY_PRODUCT_ID_WEB = "product_id_web";
    private static final String KEY_LIST_ID = "list_id";
    private static final String KEY_LIST_NAME = "list_name";
    private static final String KEY_LIST_CREATE_DATE = "list_create_date";
    private static final String KEY_PRODUCT = "Product";


    /*---------------CREATING PRODUCT LIST TABLE--------------------*/
    private static final String CREATE_TABLE_LIST_PRODUCTS = "CREATE TABLE " + TABLE_LIST_PORDUCTS
            + "(" +
            KEY_PRODUCT_ID + " INTEGER PRIMARY KEY," +
            KEY_LIST_ID + " INTEGER ," +
            KEY_PRODUCT_ID_WEB + " INTEGER ," +
            KEY_PRODUCT + " BLOB"+
            ")";

    /*---------------CREATING PRODUCT LIST TABLE--------------------*/
    private static final String CREATE_TABLE_LIST = "CREATE TABLE " + TABLE_LISTS
            + "(" +
            KEY_LIST_ID + " INTEGER PRIMARY KEY," +
            KEY_LIST_NAME+ " VARCHAR,"+
            KEY_LIST_CREATE_DATE+ " VARCHAR"+
            ")";

    private  static final String ADD_ALERT_LIST="INSERT INTO "+TABLE_LISTS+" ("+KEY_LIST_NAME+") values ('"+applicationClass.ALERT_LIST+"')";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        DateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();


        calendar.setTimeInMillis(milliSeconds);

        return formatter.format(calendar.getTime());
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LIST_PRODUCTS);
        db.execSQL(CREATE_TABLE_LIST);
        db.execSQL(ADD_ALERT_LIST);



    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_PORDUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);

        // Create tables again
        onCreate(db);
    }

    /*----------------------------------USER CREATE FUNCTIONS---------------------------------------*/
    long addList(String list_name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values=new ContentValues();
           /**/
        Calendar calendar = Calendar.getInstance();




        values.put(KEY_LIST_NAME, list_name);
        values.put(KEY_LIST_CREATE_DATE, calendar.getTimeInMillis());



        long list_id=db.insert(TABLE_LISTS, null, values);
        db.close();
        return list_id;
    }

    void addProduct(Product prod, long list_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //ArrayList<Person> persons  = new ArrayList<>();
        Gson gson = new Gson();
        ContentValues values=new ContentValues();
           /**/

        values.put(KEY_PRODUCT_ID_WEB, prod.product_id);
        values.put(KEY_LIST_ID, list_id);
        values.put(KEY_PRODUCT,gson.toJson(prod).getBytes());
        db.insert(TABLE_LIST_PORDUCTS, null, values);
        db.close();

    }

    void addProducts(ArrayList<Product> prod_list, long list_id) {
        int i=0;
        while(i<prod_list.size()){
            if(checkIfProductInList(prod_list.get(i).product_id,list_id))
                updateProduct(prod_list.get(i),list_id);
            else
                addProduct(prod_list.get(i),list_id);
            i++;
        }
    }

    int updateProduct(Product prod, long list_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson gson = new Gson();
        ContentValues values = new ContentValues();
        values.put(KEY_PRODUCT,gson.toJson(prod).getBytes());


        // updating row
        int res=db.update(TABLE_LIST_PORDUCTS,values,KEY_PRODUCT_ID_WEB+" = ? AND "+KEY_LIST_ID+" = ?",
                new String[]{String.valueOf(prod.getProduct_id()),String.valueOf(list_id)});
        /*int res= db.update(TABLE_LISTS, values, KEY_LIST_ID + " = ?",
                new String[] { String.valueOf(list_id) });*/
        //cursor.close();
        db.close();
        return res;
    }

    boolean checkIfProductInList(int prod_id,long list_id){
        SQLiteDatabase db=this.getReadableDatabase();
        String check_product_sql="SELECT "+KEY_PRODUCT+" FROM "+TABLE_LIST_PORDUCTS+" WHERE "+
                KEY_PRODUCT_ID_WEB+" = "+prod_id+" AND "+
                KEY_LIST_ID+" = "+list_id;
        Cursor csr=db.rawQuery(check_product_sql,null);
        if(csr.moveToFirst()){
           return true;
        }
        return  false;
    }

    /*----------------------------------GET Lists FUNCTIONS--------------------------------------*/
    // Getting single property
    public ArrayList<product_list> getLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<product_list> product_lists=new ArrayList<>();
        String get_user_sql="SELECT * FROM "+TABLE_LISTS+" WHERE "+KEY_LIST_NAME+" != '"+applicationClass.ALERT_LIST+"'";
        Cursor csr=db.rawQuery(get_user_sql,null);

        if(csr.moveToFirst()){
            do {


                product_lists.add(new product_list(csr.getInt(0), csr.getString(1),
                        getDate(Long.parseLong(csr.getString(2)),applicationClass.DATE_FORMAT_RSS_2)));

            }while (csr.moveToNext());

        }
        csr.close();
        db.close();
        return product_lists;
    }

/*----------------------------------GET PRODUCT FUNCTIONS--------------------------------------*/

    int getAlertListId(){
        SQLiteDatabase db = this.getReadableDatabase();

        String get_alert_list_id_sql="SELECT * FROM "+TABLE_LISTS+
                " WHERE "+KEY_LIST_NAME+" = '"+applicationClass.ALERT_LIST+"'";
        int alert_list_id=-1;
        Cursor csr=db.rawQuery(get_alert_list_id_sql,null);

        if(csr.moveToFirst()){
            do {


                alert_list_id=csr.getInt(0);

            }while (csr.moveToNext());

        }
        csr.close();
        db.close();
        return alert_list_id;
    }

    public ArrayList<Product> getProducts(int list_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Product> product_list = new ArrayList<>();
        String get_user_sql="SELECT * FROM "+TABLE_LIST_PORDUCTS+" WHERE "+KEY_LIST_ID+" = "+list_id;
        Cursor csr=db.rawQuery(get_user_sql,null);
        if(csr.moveToFirst()){
            do {
                byte[] blob = csr.getBlob(3);
                String json = new String(blob);
                Gson gson = new Gson();
                Product prod = gson.fromJson(json, Product.class);

                product_list.add(prod);

            }while (csr.moveToNext());

        }
        csr.close();
        db.close();
        return product_list;
    }

    /*----------------------------------UPDATE USER FUNCTIONS--------------------------------------*/
    // Updating single document
    public int updateListName(int list_id,String list_name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LIST_NAME, list_name);


        // updating row
        int res= db.update(TABLE_LISTS, values, KEY_LIST_ID + " = ?",
                new String[] { String.valueOf(list_id) });
        //cursor.close();
        db.close();
        return res;
    }

    public void deleteList(int list_id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_LISTS, KEY_LIST_ID + " = ?",
                new String[]{String.valueOf(list_id)});

        db.delete(TABLE_LIST_PORDUCTS,KEY_LIST_ID +" = ?",
                new String[]{String.valueOf(list_id)});
        db.close();
    }

     /*----------------------------------COUNT FROM TABLE FUNCTIONS--------------------------------------*/

    /*----------------------------------DELETE FROM TABLE FUNCTIONS--------------------------------------*/
    // Deleting single document
    public void deleteFromTable(String table_name,String KEY_ID,int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(table_name, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // Getting document Count
    public int getCountFromTable(String table_name) {
        String countQuery = "SELECT  * FROM " + table_name;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count=cursor.getCount();
        cursor.close();
        db.close();
        // return count
        return count;
    }




}
