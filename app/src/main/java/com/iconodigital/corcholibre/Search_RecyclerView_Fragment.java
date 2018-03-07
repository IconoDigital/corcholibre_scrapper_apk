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

import java.util.ArrayList;
import java.util.List;



/**
 * Created by sourav on 02-Dec-16.
 */

public class Search_RecyclerView_Fragment extends Fragment {

    FrameLayout flPlazavia;
    ImageView iv_expand;
    String type;
    private List<Product> productList = new ArrayList<>();
    private List<Product> productList_tottas = new ArrayList<>();
    private List<Product> productList_plazavia = new ArrayList<>();
    private List<Product> productList_searches = new ArrayList<>();

    private RecyclerView recyclerView;
    private Search_RecyclerView_Adapter mAdapter;
    private FrameLayout flSearch;
    public Search_RecyclerView_Fragment() {
        // Required empty public constructor
    }

    public Search_RecyclerView_Fragment newInstance ( String type) {
        // Required empty public constructor
        Search_RecyclerView_Fragment t = new Search_RecyclerView_Fragment();


        t.type = type;


        return t;


    }

    public Search_RecyclerView_Fragment newInstance(String type, List<Product> productList, Search_RecyclerView_Adapter mAdapter) {
        // Required empty public constructor
        Search_RecyclerView_Fragment t = new Search_RecyclerView_Fragment();


        t.type = type;
        t.productList=productList;
        t.mAdapter=mAdapter;


        return t;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_recyclerview_my_file, container, false);

        /**************************************************Taking UI Component Reference**************************************************/

        flSearch = (FrameLayout) inflater.inflate(R.layout.fragment_recyclerview_ofertas, container, false);
       recyclerView = (RecyclerView) flSearch.findViewById(R.id.recycler_view_ofertas);


        /**************************************************RecyclerView Settings**************************************************/
        /*if (type == applicationClass.TYPE_ALL){

            mAdapter = new Search_RecyclerView_Adapter(productList_all,mAdapter,this.getContext(),type);

        }else if (type == applicationClass.TYPE_TOTTAS){

            mAdapter = new Search_RecyclerView_Adapter(productList_tottas,mAdapter,this.getContext(),type);

        }else if (type == applicationClass.TYPE_PLAZAVIA){

            mAdapter = new Search_RecyclerView_Adapter(productList_plazavia,mAdapter,this.getContext(),type);

        }else {

        }*/
        //mAdapter = new Search_RecyclerView_Adapter(productList,this.getContext(),type);



        iv_expand = (ImageView) flSearch.findViewById(R.id.expanded_image);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(super.getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        mAdapter.setFlPlazavia(flSearch);
        mAdapter.setIv_expand(iv_expand);



//        if (type == applicationClass.TYPE_ALL){
//
//
//            async_search_products search_products = new async_search_products(filter,1,150,productList_all,mAdapter);
//            search_products.execute();
//
//        }else if (type == applicationClass.TYPE_TOTTAS){
//
//            async_search_products search_products = new async_search_products(filter,1,150,productList_tottas,mAdapter);
//            search_products.execute();
//
//        }else if (type == applicationClass.TYPE_PLAZAVIA){
//
//            async_search_products search_products = new async_search_products(filter,1,150,productList_plazavia,mAdapter);
//            search_products.execute();
//
//        }else {
//
//        }




        return flSearch;

    }






}