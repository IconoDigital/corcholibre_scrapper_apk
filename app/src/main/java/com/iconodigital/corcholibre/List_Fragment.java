package com.iconodigital.corcholibre;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by ARIJIT on 17-Jan-17.
 */

public class List_Fragment extends Fragment {

    LinearLayout outer_ll;
    RecyclerView recyclerView;
    ListItems_RecyclerView_Adapter mAdapter;
    LinearLayoutManager mLayoutManager;
    ArrayList<Product> productItemList;
    TextView tv_total_regular,tv_total_offer,tv_savings;
    LinearLayout total_Section_ll;
    View total_section_divider;
    ListItemsActivity parentActivity;
    private FrameLayout flListItems;
    private RecyclerView rlListItems;
    private int list_id;
    private String ListName;
    private ImageView iv_expand;

    public List_Fragment newInstance(ArrayList<Product> productItemList, int list_id, ListItemsActivity parentActivity) {
        List_Fragment lf=new List_Fragment();
        lf.productItemList=productItemList;
        lf.parentActivity=parentActivity;
        lf.list_id=list_id;
        return lf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        outer_ll = (LinearLayout) inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = (RecyclerView) outer_ll.findViewById(R.id.recycler_view_list);



        /**************************************************Taking UI Component Reference**************************************************/

        flListItems=(FrameLayout) outer_ll.findViewById(R.id.container);
        rlListItems=(RecyclerView)outer_ll.findViewById(R.id.recycler_view_ofertas);
        iv_expand=(ImageView)outer_ll.findViewById(R.id.expanded_image);

        tv_total_regular=(TextView) outer_ll.findViewById(R.id.total_regular);
        tv_total_offer=(TextView) outer_ll.findViewById(R.id.total_offer);
        tv_savings=(TextView) outer_ll.findViewById(R.id.savings);

        total_Section_ll=(LinearLayout)outer_ll.findViewById(R.id.total_section_ll);
        total_section_divider=outer_ll.findViewById(R.id.total_section_divider);









        /**************************************************RecyclerView Settings**************************************************/
        mAdapter = new ListItems_RecyclerView_Adapter(productItemList,parentActivity,iv_expand,flListItems,list_id);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(super.getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        updateTotalTVs();

        return outer_ll;

    }

    public HashMap<String, Float> calculate_total(ArrayList<Product> productItemList) {
        float total_regular=0F;
        float total_offer=0F;
        float savings;


        for (Product prod : productItemList) {

            if(!prod.getPrice().equals("")){
                total_regular+=getPriceFromString(prod.getPrice())*prod.getQuantity();
            }
            if(!prod.getOffer_price().equals("")){
                total_offer+=getPriceFromString(prod.getOffer_price())*prod.getQuantity();
            }
            else{
                total_offer+=getPriceFromString(prod.getPrice())*prod.getQuantity();
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
        if(tv_total_regular!=null)
        tv_total_regular.setText(String.format("%.2f", total_map.get("total_regular")));
        if(tv_total_offer!=null)
        tv_total_offer.setText(String.format("%.2f", total_map.get("total_offer")));
        if(tv_savings!=null)
        tv_savings.setText(String.format("%.2f", total_map.get("savings")));

        if(mAdapter!=null)
            mAdapter.notifyDataSetChanged();

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


}
