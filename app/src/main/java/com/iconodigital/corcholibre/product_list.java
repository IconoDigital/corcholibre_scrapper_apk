package com.iconodigital.corcholibre;

import java.util.ArrayList;

/**
 * Created by ARIJIT on 12/6/2016.
 */

public class product_list {
    int list_id;
    String list_name,date;
    ArrayList<Product> productArrayList;

    public product_list(int list_id, String list_name){
        this.list_id=list_id;
        this.list_name=list_name;
    }

    public product_list(int list_id, String list_name,String date){
        this.list_id=list_id;
        this.list_name=list_name;
        this.date=date;
    }

    public product_list(int list_id, String list_name, ArrayList<Product> productArrayList) {
        this.list_id=list_id;
        this.list_name=list_name;
        this.productArrayList=productArrayList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getList_id() {
        return list_id;
    }

    public void setList_id(int list_id) {
        this.list_id = list_id;
    }

    public ArrayList<Product> getProductArrayList() {
        return productArrayList;
    }

    public void setProductArrayList(ArrayList<Product> productArrayList) {
        this.productArrayList = productArrayList;
    }


    public String getList_name() {
        return list_name;
    }

    public void setList_name(String list_name) {
        this.list_name = list_name;
    }
}
