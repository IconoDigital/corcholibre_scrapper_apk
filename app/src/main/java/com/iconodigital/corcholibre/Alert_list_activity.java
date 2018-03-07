package com.iconodigital.corcholibre;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by ARIJIT on 27-Jan-17.
 */

public class Alert_list_activity extends AppCompatActivity {


    static final int CHANGE_PRODUCT_QUANTITY = 1;
    RecyclerView recyclerView;
    AlertList_RecylcerView_adapter mAdapter;
    LinearLayoutManager mLayoutManager;
    ArrayList<Product> productItemList;
    TextView tv_total_regular,tv_total_offer,tv_savings;
    LinearLayout total_Section_ll;
    View total_section_divider;
    FloatingActionButton fab;
    DatabaseHandler db_handler;
    private FrameLayout flListItems;
    private RecyclerView rlListItems;
    private int list_id;
    private String ListName;
    private ImageView iv_expand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_recyclerview_ofertas);
        setContentView(R.layout.activity_alert_list_items);

        //Toolbar toolbar=(Toolbar) getSupportActionBar();
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);




        /**************************************************Taking UI Component Reference**************************************************/

        flListItems=(FrameLayout) findViewById(R.id.container);
        rlListItems=(RecyclerView)findViewById(R.id.recycler_view_ofertas);
        iv_expand=(ImageView)findViewById(R.id.expanded_image);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view_ofertas);
        tv_total_regular=(TextView) findViewById(R.id.total_regular);
        tv_total_offer=(TextView) findViewById(R.id.total_offer);
        tv_savings=(TextView) findViewById(R.id.savings);

        total_Section_ll=(LinearLayout)findViewById(R.id.total_section_ll);
        total_section_divider=findViewById(R.id.total_section_divider);

        list_id=this.getIntent().getIntExtra("list_id",-1);
        ListName=this.getIntent().getStringExtra("list_name");
        toolbar.setTitle(ListName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(list_id==-1){
            /**close activity**/
            finish();
        }

        final DatabaseHandler db_handler=new DatabaseHandler(getBaseContext());

        productItemList=db_handler.getProducts(list_id);
        if(productItemList.size()==0){
            Toast.makeText(Alert_list_activity.this,"Agrega tus alertas por producto",Toast.LENGTH_SHORT);
        }

        /**************************************************RecyclerView Settings**************************************************/
        mAdapter = new AlertList_RecylcerView_adapter(productItemList,this,iv_expand,flListItems,list_id);
        mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        if(db_handler.getAlertListId()==list_id){
            total_section_divider.setVisibility(View.GONE);
            total_Section_ll.setVisibility(View.GONE);

        }
        updateTotalTVs();
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
                DatabaseHandler db_handler=new DatabaseHandler(this);
                productItemList.clear();
                productItemList.addAll(db_handler.getProducts(list_id));
                mAdapter.notifyDataSetChanged();
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
        HashMap<String,Float> total_map=calculate_total(productItemList);
        tv_total_regular.setText(String.format("%.2f", total_map.get("total_regular")));
        tv_total_offer.setText(String.format("%.2f", total_map.get("total_offer")));
        tv_savings.setText(String.format("%.2f", total_map.get("savings")));

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









