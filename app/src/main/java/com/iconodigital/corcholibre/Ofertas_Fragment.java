package com.iconodigital.corcholibre;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.iconodigital.corcholibre.networking.UrlConstants;
import com.iconodigital.corcholibre.networking.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sourav on 24-Nov-16.
 */
public class Ofertas_Fragment extends Fragment {

    public static String[] columns = new String[]{"_id", "NAME", "DESC", "BRAND"};
    public List<Product> productList = new ArrayList<>();
    RelativeLayout rlofertas;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    List<String> storeList = new ArrayList<>();
    Context ctx;
    Ofertas_RecyclerView_Adapter recyclerView_adapter;
    SearchFeedResultsAdapter mSearchViewAdapter;
    private List<OfertasRecyclerViewAdapter_List> ofertasRecyclerViewAdapterLists = new ArrayList<>();
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private android.widget.SearchView search;
    private String current_searchItem = "";
    private String current_tab = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle("ofertas");

        //selected_product_ids_arr =new ArrayList<>();

        /** Clearing All previous selections **/
        applicationClass.selected_product_ids_arr.clear();
        applicationClass.selected_prod_map.clear();


        /**************************************************Taking UI Component Reference**************************************************/

        rlofertas = (RelativeLayout) inflater.inflate(R.layout.fragment_ofertas, container, false);
        return rlofertas;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) rlofertas.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_to_list_intent = new Intent(getActivity(), Add_to_list.class);
                //add_to_list_intent.putExtra("product_map",selected_prod_map);
                startActivity(add_to_list_intent);
            }
        });

        fetchProducts();
    }

    private void fetchProducts() {
        final ProgressBar progressBar = rlofertas.findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, UrlConstants.GET_OFFER_PRODUCT, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        progressBar.setVisibility(View.GONE);
                        String status = null;
                        try {
                            status = jsonObject.getString("status");
                            if (status.equals("1")) {
                                Toast.makeText(getContext(), "Status: 1", Toast.LENGTH_SHORT).show();
                            } else {
                                JSONArray jsonArray = jsonObject.getJSONArray("product_info");
                                JSONObject productObj = null;
                                int product_id;//
                                String product_name;//
                                String product_desc;//
                                String brand_name;//
                                String price;//
                                String offer_price;//
                                String offer_title;//
                                String source_card_price;//
                                String source;//
                                int category_id;//
                                String valid_until;//
                                String img_url;//
                                Product product = null;
                                productList.clear();
                                storeList.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    productObj = jsonArray.getJSONObject(i);
                                    product_id = Integer.parseInt(productObj.getString("product_id"));
                                    product_name = productObj.getString("product_name");
                                    product_desc = productObj.getString("product_desc");
                                    brand_name = productObj.getString("brand_name");
                                    price = productObj.getString("price");
                                    offer_price = productObj.getString("offer_price");
                                    offer_title = productObj.getString("offer_title");
                                    source = productObj.getString("store");
                                    category_id = Integer.parseInt(productObj.getString("category_id"));
                                    valid_until = productObj.getString("valid_until");
                                    img_url = productObj.getString("img_url");

                                    product = new Product(product_id, product_name, product_desc, brand_name, price, offer_price, offer_title, source, category_id, valid_until, img_url);
                                    productList.add(product);

                                    if (!storeList.contains(source))
                                        storeList.add(source);
                                }

                                if (storeList.size() > 0) {
                                    storeList.add(0, "All");
                                    setupViewPager();
                                    setupTabLayout();
                                    current_tab = "All";
                                    filterProductsByStoreAndSearchText(true);
                                    setupSearchBar();
                                }

                                int count = productList.size();
                                try {
                                    Toast.makeText(getContext(), "Number of products: " + count, Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressBar.isShown())
                    progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Server didn't respond.\n" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
        };
        VolleyUtils.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void setupViewPager() {
        viewPager = (ViewPager) rlofertas.findViewById(R.id.viewpager);
        viewPagerAdapter = new Ofertas_Fragment.ViewPagerAdapter(getChildFragmentManager());
        List<Product> productListLocal = new ArrayList<>();
        for (String store_name : storeList) {
            recyclerView_adapter = new Ofertas_RecyclerView_Adapter(productListLocal, viewPager.getContext(), store_name);
            viewPagerAdapter.addFragment(new Ofertas_RecyclerView_Fragment()
                    .newInstance(current_searchItem, productListLocal, recyclerView_adapter), store_name);
            ofertasRecyclerViewAdapterLists.add(new OfertasRecyclerViewAdapter_List(recyclerView_adapter));
        }
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void setupTabLayout() {
        tabLayout = (TabLayout) rlofertas.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        if (tabLayout.getTabCount() <= 3)
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        else
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                current_tab = tab.getText().toString();
                filterProductsByStoreAndSearchText(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void filterProductsByStoreAndSearchText(boolean showToast) {
        List<Product> filteredProductList = new ArrayList<>();
        if (current_tab.equalsIgnoreCase("All")) {
            for (Product product : productList) {
                if (searchPassed(product))
                    filteredProductList.add(product);
            }

        } else {
            for (Product product :
                    productList) {
                if (current_tab.equalsIgnoreCase(product.store) && searchPassed(product))
                    filteredProductList.add(product);
            }
        }
        OfertasRecyclerViewAdapter_List adapterList = ofertasRecyclerViewAdapterLists.get(storeList.indexOf(current_tab));
        Ofertas_RecyclerView_Adapter adapter = adapterList.getOfertasRecyclerViewAdapter();
        adapter.update_productList_searches(filteredProductList);
        if (showToast)
            Toast.makeText(getContext(), "Total products: " + filteredProductList.size(), Toast.LENGTH_SHORT).show();
    }

    private boolean searchPassed(Product product) {
        if (current_searchItem.length() == 0)
            return true;
        else {
            boolean foundInBrand = product.getBrand_name().toLowerCase().contains(current_searchItem.toLowerCase());
            boolean foundInProductName = product.getProduct_name().toLowerCase().contains(current_searchItem.toLowerCase());
            boolean foundInStoreName = product.getStore().toLowerCase().contains(current_searchItem.toLowerCase());
            if (foundInBrand || foundInProductName || foundInStoreName)
                return true;
        }
        return false;
    }

    private void setupSearchBar() {
        //-----------SEARCH-----------------------//
        search = (android.widget.SearchView) rlofertas.findViewById(R.id.searchView_search);
        search.setQueryHint("Search Products");
        int id = search.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = search.findViewById(id);
        textView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/BebasNeue_Regular.ttf"));
        //*** setOnQueryTextFocusChangeListener ***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                current_searchItem = query;
                filterProductsByStoreAndSearchText(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (current_searchItem.length() > newText.length()) {
                    List<Product> productListLocal = new ArrayList<>();
                    productListLocal.addAll(productList);
                    OfertasRecyclerViewAdapter_List adapterList = ofertasRecyclerViewAdapterLists.get(storeList.indexOf(current_tab));
                    Ofertas_RecyclerView_Adapter adapter = adapterList.getOfertasRecyclerViewAdapter();
                    adapter.update_productList_searches(productListLocal);
                }
                current_searchItem = newText;
                filterProductsByStoreAndSearchText(false);

                return false;
            }
        });
    }


    public void show_searchView() {
        Toolbar toolbar_search = rlofertas.findViewById(R.id.toolbar_search);
        if (toolbar_search.getVisibility() == View.VISIBLE)
            toolbar_search.setVisibility(View.GONE);
        else
            toolbar_search.setVisibility(View.VISIBLE);
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

    private class OfertasRecyclerViewAdapter_List {
        private Ofertas_RecyclerView_Adapter ofertasRecyclerViewAdapter;

        public OfertasRecyclerViewAdapter_List(Ofertas_RecyclerView_Adapter ofertasRecyclerViewAdapter) {
            this.ofertasRecyclerViewAdapter = ofertasRecyclerViewAdapter;
        }

        public Ofertas_RecyclerView_Adapter getOfertasRecyclerViewAdapter() {
            return ofertasRecyclerViewAdapter;
        }
    }

}
