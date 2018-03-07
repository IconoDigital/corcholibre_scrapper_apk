package com.iconodigital.corcholibre;

import java.io.Serializable;

/**
 * Created by sourav on 23-Nov-16.
 */

public class Product implements Serializable {


    int product_id;//
    String product_name;//
    String product_desc;//
    String brand_name;//
    String unit;
    String price;//
    String offer_price;//
    String source_card_price;//
    String offer_title;//
    String offer_conditions;
    String store;//
    int category_id;//
    String valid_until;//
    String img_url;//
    int quantity;
    String category_keyword;
    String quantity_keyword;

    public Product() {
    }

    public Product(int product_id, String product_name, String product_desc, String brand_name, String unit, String price, String offer_price, String offer_title, String offer_conditions, String store, int category_id, String valid_until, String img_url) {

        this.product_id = product_id;
        this.product_name = product_name;
        this.product_desc = product_desc;
        this.brand_name = brand_name;
        this.unit = unit;
        this.price = price;
        this.offer_price = offer_price;
        this.offer_title = offer_title;
        this.offer_conditions = offer_conditions;
        this.store = store;
        this.category_id = category_id;
        this.valid_until = valid_until;
        this.img_url = img_url;

        /**by default quantity will be 1**/
        this.quantity = 1;

    }

    public Product(int product_id, String product_name, String product_desc,
                   String brand_name, String unit, String price, String offer_price,
                   String offer_title, String offer_conditions, String store, int category_id,
                   String valid_until, String img_url, String category_keyword, String quantity_keyword) {

        this.product_id = product_id;
        this.product_name = product_name;
        this.product_desc = product_desc;
        this.brand_name = brand_name;
        this.unit = unit;
        this.price = price;
        this.offer_price = offer_price;
        this.offer_title = offer_title;
        this.offer_conditions = offer_conditions;
        this.store = store;
        this.category_id = category_id;
        this.valid_until = valid_until;
        this.img_url = img_url;

        /**by default quantity will be 1**/
        this.quantity = 1;
        this.category_keyword = category_keyword;
        this.quantity_keyword = quantity_keyword;

    }

    public Product(int product_id, String product_name, String product_desc, String brand_name, String price, String offer_price, String offer_title, String store, int category_id, String valid_until, String img_url) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_desc = product_desc;
        this.brand_name = brand_name;
        this.price = price;
        this.offer_price = offer_price;
        this.offer_title = offer_title;
        this.store = store;
        this.category_id = category_id;
        this.valid_until = valid_until;
        this.img_url = img_url;
    }

    public String getCategory_keyword() {
        return category_keyword;
    }

    public void setCategory_keyword(String category_keyword) {
        this.category_keyword = category_keyword;
    }

    public String getQuantity_keyword() {
        return quantity_keyword;
    }

    public void setQuantity_keyword(String quantity_keyword) {
        this.quantity_keyword = quantity_keyword;
    }

    /****************************************GETTER METHODS***********************************/

    public int getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public String getUnit() {
        return unit;
    }

    public String getPrice() {
        return price;
    }

    public String getOffer_price() {
        return offer_price;
    }

    public String getOffer_title() {
        return offer_title;
    }

    public String getOffer_conditions() {
        return offer_conditions;
    }

    public String getStore() {
        return store;
    }

    public int getCategory_id() {
        return category_id;
    }

    public String getValid_until() {
        return valid_until;
    }

    public String getImg_Url() {
        return img_url;
    }

    public int getQuantity() {
        if (quantity < 1)
            return 1;
        return quantity;
    }


    /******************************SETTER METHODS*********************************/
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    //
//    public String getProductName() {
//        return product_name;
//    }
//
//    public String getProductDesc() {
//        return product_desc;
//    }
//    public String getBrandName() {
//        return brand_name;
//    }
//    public String getPrice() {
//        return price;
//    }
//    public String getOfferPrice() {
//        return offer_price;
//    }
//    public String getTimeStamp() {
//        return timestamp;
//    }
//    public String getOfferTitle() {
//        return offer_title;
//    }
//

    /*
    @Override
    public String toString() {
        return "Product{" +
                "product_id=" + product_id +
                ", product_name='" + product_name + '\'' +
                ", product_desc='" + product_desc + '\'' +
                ", brand_name='" + brand_name + '\'' +
                ", unit='" + unit + '\'' +
                ", price='" + price + '\'' +
                ", offer_price='" + offer_price + '\'' +
                ", offer_title='" + offer_title + '\'' +
                ", offer_conditions='" + offer_conditions + '\'' +
                ", store='" + store + '\'' +
                ", category_id=" + category_id +
                ", valid_until='" + valid_until + '\'' +
                ", img_url='" + img_url + '\'' +
                '}';
    }
*/
}
