package com.iconodigital.corcholibre;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by sourav on 06-Dec-16.
 */

public class ListItemsActivity extends AppCompatActivity {


    static final int CHANGE_PRODUCT_QUANTITY = 1;
    RecyclerView recyclerView;
    ListItems_RecyclerView_Adapter mAdapter;
    LinearLayoutManager mLayoutManager;
    ArrayList<Product> productItemAll;
    ArrayList<Product> productItemPlazavea;
    ArrayList<Product> productItemTottas;
    ArrayList<Product> productItemBest;
    TextView tv_total_regular,tv_total_offer,tv_savings;
    LinearLayout total_Section_ll;
    View total_section_divider;
    TabLayout tabLayout;
    List_Fragment list_fragmentAll,list_fragmentPlazavea,list_fragmentTottas,list_fragmentBest;
    FloatingActionButton fab;
    DatabaseHandler db_handler;
    ViewPager viewPager;
    private int list_id;
    private String ListName;
    private ImageView iv_expand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_recyclerview_ofertas);
        setContentView(R.layout.activity_list_items);

        //Toolbar toolbar=(Toolbar) getSupportActionBar();
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        /**************************************************Taking UI Component Reference**************************************************/
        list_id=this.getIntent().getIntExtra("list_id",-1);
        ListName=this.getIntent().getStringExtra("list_name");
        toolbar.setTitle(ListName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);







        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if(list_id==-1){
            /**close activity**/
            finish();
        }

        db_handler=new DatabaseHandler(this);

        productItemAll=new ArrayList<>();
        productItemTottas=new ArrayList<>();
        productItemPlazavea=new ArrayList<>();
        fetch_products_from_db(list_id);
        segregate_products();
        productItemBest=new ArrayList<>();
        generateSavingsList();
        setupViewPager(viewPager);



        /**************************************************RecyclerView Settings**************************************************/


    }

    public void fetch_products_from_db(int list_id){
        ArrayList<Product> temp_list = db_handler.getProducts(list_id);
        productItemAll.clear();
        for (Product prod : temp_list) {
            productItemAll.add(prod);
        }
    }





    @Override
    public void onResume() {
        super.onResume();

//        ActionBar actionBar = this.getSupportActionBar();
//        actionBar.setTitle(ListName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CHANGE_PRODUCT_QUANTITY) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                //reloading products from db
                /*DatabaseHandler db_handler=new DatabaseHandler(this);
                productItemAll.clear();
                productItemAll.addAll(db_handler.getProducts(list_id));
                fetch_products_from_db(list_id);
                segregate_products();
                //mAdapter.notifyDataSetChanged();*/
                updateTotalTVs();

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    public HashMap<String, Float> calculate_total(ArrayList<Product> productItemList) {
        float total_regular=0F;
        float total_offer=0F;
        float savings;


        for (Product prod : productItemList) {

            if(!prod.getPrice().equals("")){
                total_regular+=getPriceFromString(prod.getPrice());
            }
            if(!prod.getOffer_price().equals("")){
                total_offer+=getPriceFromString(prod.getOffer_price());
            }
            else{
                total_offer+=getPriceFromString(prod.getPrice());
            }

        }
        savings=total_regular-total_offer;
        HashMap<String,Float> total_map=new HashMap<>();
        total_map.put("total_regular",total_regular);
        total_map.put("total_offer",total_offer);
        total_map.put("savings",savings);

        return total_map;
    }

    public void updateTotalTVs(){
        fetch_products_from_db(list_id);
        segregate_products();
        generateSavingsList();
       list_fragmentPlazavea.updateTotalTVs();
       list_fragmentTottas.updateTotalTVs();
       list_fragmentBest.updateTotalTVs();
       list_fragmentAll.updateTotalTVs();

    }

    public float getPriceFromString(String s){
        Pattern p = Pattern.compile("(?<!(?:\\d|\\.))\\d+\\.\\d{2}(?!\\.)");
        Matcher m = p.matcher(s);
        float price=0F;
        if (m.find()){
            String res = m.toMatchResult().group(0);
            price=Float.parseFloat(res);
        }
        return price;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //        fragmentManager.beginTransaction().replace(R.id.content_frame, HomeFragment.newInstance(mAdapter,camera,params)).commit();

        list_fragmentAll=new List_Fragment().newInstance(productItemAll,list_id,ListItemsActivity.this);
        list_fragmentTottas=new List_Fragment().newInstance(productItemTottas,list_id,ListItemsActivity.this);
        list_fragmentBest=new List_Fragment().newInstance(productItemBest,list_id,ListItemsActivity.this);
        list_fragmentPlazavea=new List_Fragment().newInstance(productItemPlazavea,list_id,ListItemsActivity.this);

        adapter.addFragment(list_fragmentAll, "Todos");
        adapter.addFragment(list_fragmentBest, "Best Savings");
        adapter.addFragment(list_fragmentTottas,"Tottas");
        adapter.addFragment(list_fragmentPlazavea,"Plazavea");
        viewPager.setAdapter(adapter);
    }

    public void segregate_products(){
        productItemTottas.clear();
        productItemPlazavea.clear();

        for(int i=0;i<productItemAll.size();i++){
            Product prod = productItemAll.get(i);
            if (prod.store.equals(applicationClass.TYPE_TOTTAS) ||
                    prod.store.equals(applicationClass.TYPE_TOTTAS_SMALL)) {
                productItemTottas.add(prod);
            }
            else{
                productItemPlazavea.add(prod);
            }
        }
    }

    public void generateSavingsList(){
        HashMap<String, ArrayList<Product>> category_product_list_map = new HashMap<>();
        productItemBest.clear();

        for (Product prod : productItemAll) {
            Log.d("Quantity keyword :",""+prod.getQuantity_keyword()+" Category keyword"+prod.getCategory_keyword());
            String category_keyword=prod.getCategory_keyword();
            if(!category_keyword.equalsIgnoreCase("")&&!category_keyword.equalsIgnoreCase("null")&& category_keyword!=null){
                if(category_product_list_map.containsKey(category_keyword.toLowerCase())){
                    ArrayList<Product> prod_list = category_product_list_map.get(category_keyword.toLowerCase());
                    prod_list.add(prod);
                }
                else{
                    ArrayList<Product> prod_list = new ArrayList<>();
                    prod_list.add(prod);
                    category_product_list_map.put(category_keyword.toLowerCase(),prod_list);
                }
            }
            else{
                //No category keyword present
                productItemBest.add(prod);
            }

        }

        /**
         * Iterating through category_product_list map to get the best savings Product
         */
        Iterator It =category_product_list_map.entrySet().iterator();
        while(It.hasNext()){
            Map.Entry pair = (Map.Entry)It.next();
            ArrayList<Product> similar_prod_list = (ArrayList<Product>) pair.getValue();
            float min_price=Float.MAX_VALUE;
            int pos=0;
            int selected_pos=-1;
            for (Product prod : similar_prod_list) {

                if(prod.getQuantity_keyword()!=null){
                 float quantity=Float.parseFloat(prod.getQuantity_keyword());

                    if(!prod.getOffer_price().equalsIgnoreCase("")){
                        float price=getPriceFromString(prod.getOffer_price());

                        if((price/quantity)<min_price){
                            selected_pos=pos;
                            min_price=price/quantity;
                        }
                    }
                    else if(!prod.getPrice().equalsIgnoreCase("")){
                        float price=getPriceFromString(prod.getPrice());
                        if((price/quantity)<min_price){
                            selected_pos=pos;
                            min_price=price/quantity;
                        }
                    }
                }

                pos++;
            }
            if(selected_pos!=-1)
                productItemBest.add(similar_prod_list.get(selected_pos));
        }



    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }





}








