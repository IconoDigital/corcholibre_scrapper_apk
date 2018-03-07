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
 * Created by sourav on 23-Nov-16.
 */

public class Ofertas_RecyclerView_Fragment extends Fragment {
    ArrayList<String> selected_product_ids_arr;
     RecyclerView recyclerView;
     Ofertas_RecyclerView_Adapter mAdapter;
     FrameLayout flPlazavia;
     ImageView iv_expand;
    String col_name;
    String filter;
    String type;
    LinearLayoutManager mLayoutManager;
    private List<Product> productList = new ArrayList<>();
    //HashMap<Integer,Product> selected_prod_map;


    public Ofertas_RecyclerView_Fragment() {
        // Required empty public constructor
    }


    public Ofertas_RecyclerView_Fragment newInstance (String col_name, String filter,
                                                      String type,
                                                      List<Product> productList,


                                                      Ofertas_RecyclerView_Adapter mAdapter
    ) {
        // Required empty public constructor
        Ofertas_RecyclerView_Fragment p = new Ofertas_RecyclerView_Fragment();
        p.col_name = col_name;
        p.filter = filter;
        p.type = type;
        p.selected_product_ids_arr=selected_product_ids_arr;

        p.productList=productList;
        p.mAdapter=mAdapter;


        return p;


    }

    public Ofertas_RecyclerView_Fragment newInstance (String type,
                                                      List<Product> productList,
                                                      Ofertas_RecyclerView_Adapter mAdapter
                                                       ) {
        // Required empty public constructor
        Ofertas_RecyclerView_Fragment p = new Ofertas_RecyclerView_Fragment();
        p.type = type;
        p.selected_product_ids_arr=selected_product_ids_arr;
        p.mAdapter=mAdapter;
        p.productList=productList;



        return p;

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

        flPlazavia = (FrameLayout) inflater.inflate(R.layout.fragment_recyclerview_ofertas, container, false);
        recyclerView = (RecyclerView) flPlazavia.findViewById(R.id.recycler_view_ofertas);
        iv_expand = (ImageView) flPlazavia.findViewById(R.id.expanded_image);


        /**************************************************RecyclerView Settings**************************************************/

        mAdapter.setFlPlazavia(flPlazavia);
        mAdapter.setIv_expand(iv_expand);


        //mAdapter = new Ofertas_RecyclerView_Adapter(productList,mAdapter,this.getContext(),type,this,iv_expand,flPlazavia);

/*        if (type == applicationClass.TYPE_ALL){

            mAdapter = new Ofertas_RecyclerView_Adapter(productList,mAdapter,this.getContext(),type,this,iv_expand,flPlazavia);

        }else if (type == applicationClass.TYPE_TOTTAS){

            mAdapter = new Ofertas_RecyclerView_Adapter(productList_tottas,mAdapter,this.getContext(),type,this,iv_expand,flPlazavia);

        }else if (type == applicationClass.TYPE_PLAZAVIA){

            mAdapter = new Ofertas_RecyclerView_Adapter(productList_plazavia,mAdapter,this.getContext(),type,this,iv_expand,flPlazavia);

        }else {

        }

*/



        mLayoutManager = new LinearLayoutManager(super.getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        async_filter_products show_products = new async_filter_products(col_name,filter,1,150, productList,mAdapter,this.getContext());
        show_products.execute();

       /*
        if (type == applicationClass.TYPE_ALL){

            async_show_products show_products = new async_show_products(1,150,productList,mAdapter);
            show_products.execute();

        }else if (type == applicationClass.TYPE_TOTTAS){

            async_filter_products show_products = new async_filter_products(col_name,filter,1,150,productList_tottas,mAdapter);
            show_products.execute();

        }else if (type == applicationClass.TYPE_PLAZAVIA){

            async_filter_products show_products = new async_filter_products(col_name,filter,1,150,productList_plazavia,mAdapter);
            show_products.execute();

        }else {

        }
        */


        return flPlazavia;

    }
    /*
    public void refresh_recycler_item(int position,String tab){
//        int start_cell= mLayoutManager.findFirstVisibleItemPosition();
//        int visible_cell_count=mLayoutManager.findLastVisibleItemPosition()-start_cell;
//        mAdapter.notifyItemRangeChanged(start_cell,visible_cell_count);
        parent_frag_ref.refresh_recycler_item(position,tab);


    }

    public void refresh_item_if_present(Product prod,String type_from,String type_to){
        if (type == applicationClass.TYPE_ALL){

            if(productList.contains(prod)){
                mAdapter.notifyItemChanged(productList.indexOf(prod));
            }

        }else if (type == applicationClass.TYPE_TOTTAS){

            if(productList_tottas.contains(prod)){
                mAdapter.notifyItemChanged(productList_tottas.indexOf(prod));
            }

        }else if (type == applicationClass.TYPE_PLAZAVIA){

            if(productList_plazavia.contains(prod)){
                mAdapter.notifyItemChanged(productList_plazavia.indexOf(prod));
            }

        }else {

        }
    }

    */






}
