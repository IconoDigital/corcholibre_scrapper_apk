package com.iconodigital.corcholibre;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sourav on 22-Nov-16.
 */

public class Myfile_RecyclerView_Fragment extends Fragment {

    String col_name;
    String filter;
    String type;
    private List<Product> productList_all = new ArrayList<>();
    private List<Product> productList_tottas = new ArrayList<>();
    private List<Product> productList_plazavia = new ArrayList<>();

    private RecyclerView recyclerView;
    private Myfile_RecyclerView_Adapter mAdapter;
    private RelativeLayout rlTottus;
    public Myfile_RecyclerView_Fragment() {
        // Required empty public constructor
    }

    public Myfile_RecyclerView_Fragment newInstance (String col_name, String filter, String type) {
        // Required empty public constructor
        Myfile_RecyclerView_Fragment t = new Myfile_RecyclerView_Fragment();
        t.col_name = col_name;
        t.filter = filter;
        t.type = type;

        return t;


    }

    public Myfile_RecyclerView_Fragment newInstance (String type) {
        // Required empty public constructor
        Myfile_RecyclerView_Fragment t = new Myfile_RecyclerView_Fragment();
        t.type = type;

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

        rlTottus = (RelativeLayout) inflater.inflate(R.layout.fragment_recyclerview_my_file, container, false);
//        recyclerView = (RecyclerView) rlTottus.findViewById(R.id.recycler_view_list);


        /**************************************************RecyclerView Settings**************************************************/
//        if (type == applicationClass.TYPE_ALL){
//
//            mAdapter = new Myfile_RecyclerView_Adapter(productList_all,mAdapter,this.getContext(),type);
//
//        }else if (type == applicationClass.TYPE_TOTTAS){
//
//            mAdapter = new Myfile_RecyclerView_Adapter(productList_tottas,mAdapter,this.getContext(),type);
//
//        }else if (type == applicationClass.TYPE_PLAZAVIA){
//
//            mAdapter = new Myfile_RecyclerView_Adapter(productList_plazavia,mAdapter,this.getContext(),type);
//
//        }else {
//
//        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(super.getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);



        if (type == applicationClass.TYPE_ALL){

//            async_show_products show_products = new async_show_products(1,150,productList_all,mAdapter);
//            show_products.execute();

        }else if (type == applicationClass.TYPE_TOTTAS){

            async_filter_products show_products = new async_filter_products(col_name,filter,1,150,productList_tottas,mAdapter,this.getContext());
            show_products.execute();

        }else if (type == applicationClass.TYPE_PLAZAVIA){

            async_filter_products show_products = new async_filter_products(col_name,filter,1,150,productList_plazavia,mAdapter,this.getContext());
            show_products.execute();

        }else {

        }




        return  rlTottus;

    }






}

