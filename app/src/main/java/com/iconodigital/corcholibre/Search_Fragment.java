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
import com.android.volley.toolbox.StringRequest;
import com.iconodigital.corcholibre.networking.UrlConstants;
import com.iconodigital.corcholibre.networking.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


interface RequestCompletedListener {
    public void OnCategoryListFetchedListener(List<String> list);

    public void OnProductListFetchedListener(String category_id, List<Product> productList, boolean resetSources);
}

/**
 * Created by sourav on 02-Dec-16.
 */

public class Search_Fragment extends Fragment implements RequestCompletedListener {


    public static String[] columns = new String[]{"_id", "NAME", "DESC", "BRAND"};
    public List<Product> productList = new ArrayList<>();
    RelativeLayout rlSearch;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    List<String> category_id_List = new ArrayList<>();
    Map<String, String> category_name_Map = new HashMap<>();
    List<String> store_id_List = new ArrayList<>();
    Map<String, String> store_name_Map = new HashMap<>();
    List<String> listStores = new ArrayList<>();

    Context ctx;
    Search_RecyclerView_Adapter recyclerView_adapter;
    String current_categoryId = "0";
    SearchFeedResultsAdapter mSearchViewAdapter;
    int[] tv_id = {R.id.txt_cat_type1, R.id.txt_cat_type2, R.id.txt_cat_type3, R.id.txt_cat_type4, R.id.txt_cat_type5};
    List<Product> list = new ArrayList<>();
    private List<SearchRecyclerViewAdapter_List> searchRecyclerViewAdapterLists = new ArrayList<>();
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private android.widget.SearchView search;
    private String current_searchItem = "";

    public Search_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle(getResources().getString(R.string.search_products2));
        ctx = getContext();

        /** Clearing All previous selections **/
        applicationClass.selected_product_ids_arr.clear();
        applicationClass.selected_prod_map.clear();
        /**************************************************Taking UI Component Reference**************************************************/

        rlSearch = (RelativeLayout) inflater.inflate(R.layout.fragment_search, container, false);
        /*linearLayout_cat_source = rlSearch.findViewById(R.id.linearLayout_cat_source);
        viewPager = (ViewPager) rlSearch.findViewById(R.id.viewpager);
        toolbar = (Toolbar) rlSearch.findViewById(R.id.toolbar_search);*/

        return rlSearch;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //selected_product_ids_arr=new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) rlSearch.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_to_list_intent = new Intent(getActivity(), Add_to_list.class);
                //add_to_list_intent.putExtra("product_map",selected_prod_map);
                if (applicationClass.selected_prod_map.size() > 0)
                    startActivity(add_to_list_intent);
                else
                    Toast.makeText(getContext(), "No products selected ", Toast.LENGTH_SHORT).show();
            }
        });
        fetchCategories();
    }

    private void fetchCategories() {
        final ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        rlSearch.addView(progressBar, params);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlConstants.WS_SEARCH_PRODUCTS_CATEGORY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);  //To show ProgressBar
                String status = null;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    status = jsonObject.getString("status");
                    if (status.equalsIgnoreCase("1"))
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    else {
                        JSONArray user_info = jsonObject.getJSONArray("user_info");

                        category_id_List.clear();
                        category_name_Map.clear();
                        category_id_List.add("0");
                        category_name_Map.put("0", "All");

                        JSONObject object;
                        String category_id;
                        String category_name;
                        for (int i = 0; i < user_info.length(); i++) {
                            object = (JSONObject) user_info.get(i);
                            category_id = object.getString("category_id");
                            category_name = object.getString("category_name");

                            category_id_List.add(category_id);
                            category_name_Map.put(category_id, category_name);
                        }
                        if (category_id_List.size() > 1) {
                            setupViewPager();
                            setupTabLayout();
                            setupSearchBar();
                            fetchProducts();
                            loadSourcesButtons(productList);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Response: " + response, Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Server didn't respond.MSG:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                if (progressBar.isShown())
                    progressBar.setVisibility(View.GONE);
            }
        });
        VolleyUtils.getInstance(getContext()).addToRequestQueue(stringRequest);
    }



    private void setupViewPager() {
        viewPager = (ViewPager) rlSearch.findViewById(R.id.viewpager);
        viewPagerAdapter = new Search_Fragment.ViewPagerAdapter(getChildFragmentManager());
        List<Product> productListLocal = new ArrayList<>();
        String category_name = "null";
        for (String id :
                category_id_List) {
            category_name = category_name_Map.get(id);

            recyclerView_adapter = new Search_RecyclerView_Adapter(productListLocal, viewPager.getContext(), category_name);
            viewPagerAdapter.addFragment(new Search_RecyclerView_Fragment()
                    .newInstance(current_searchItem, productListLocal, recyclerView_adapter), category_name);
            searchRecyclerViewAdapterLists.add(new SearchRecyclerViewAdapter_List(
                    Integer.parseInt(id), recyclerView_adapter));
        }
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void setupTabLayout() {
        tabLayout = (TabLayout) rlSearch.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        if (tabLayout.getTabCount() <= 3)
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        else
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                current_categoryId = category_id_List.get(tab.getPosition());
                filterProductsByCategoryIdAndSearchText(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void filterProductsByCategoryIdAndSearchText(boolean showToast) {
        List<Product> filteredProductList = new ArrayList<>();
        int category_id = Integer.parseInt(current_categoryId);
        if (category_id == 0) {
            for (Product product : productList) {
                if (searchPassed(product))
                    filteredProductList.add(product);
            }

        } else {
            for (Product product : productList) {
                if (category_id == product.category_id && searchPassed(product))
                    filteredProductList.add(product);
            }
        }
        /*for (Product product : productList) {
            if (searchPassed(product) && (category_id == 0 || category_id == product.category_id))
                filteredProductList.add(product);
        }*/

        /*SearchRecyclerViewAdapter_List adapterList = searchRecyclerViewAdapterLists.get(category_id);
        Search_RecyclerView_Adapter adapter = adapterList.getSearchRecyclerViewAdapter();
        adapter.update_productList_searches(filteredProductList);*/
        List<Product> list = new ArrayList<>();
        list.clear();
        list.addAll(filteredProductList);
        loadSourcesButtons(list);
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
        search = (android.widget.SearchView) rlSearch.findViewById(R.id.searchView_search);
        search.setQueryHint("Search Products");
        int id = search.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = search.findViewById(id);
        textView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/BebasNeue_Regular.ttf"));
        //*** setOnQueryTextFocusChangeListener ***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                current_searchItem = query;
                filterProductsByCategoryIdAndSearchText(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (current_searchItem.length() > newText.length()) {
                    List<Product> productListLocal = new ArrayList<>();
                    productListLocal.addAll(productList);
                    int category_id = Integer.parseInt(current_categoryId);
                    SearchRecyclerViewAdapter_List adapterList = searchRecyclerViewAdapterLists.get(category_id);
                    Search_RecyclerView_Adapter adapter = adapterList.getSearchRecyclerViewAdapter();
                    adapter.update_productList_searches(productListLocal);
                }
                current_searchItem = newText;
                filterProductsByCategoryIdAndSearchText(false);

                return false;
            }
        });
    }

    private void fetchProducts() {
        /*JSONArray jsonArray = null;
        String str = "[\n" +
                "    {\n" +
                "      \"product_id\": \"265\",\n" +
                "      \"product_name\": \"Vino Tinto Las Moras Reserva Malbec Botella 750 ml\",\n" +
                "      \"brand_name\": \"Las Moras\",\n" +
                "      \"price\": \"59.90\",\n" +
                "      \"offer_price\": \"38.90\",\n" +
                "      \"source_card_price\": null,\n" +
                "      \"store\": \"wong\",\n" +
                "      \"valid_until\": \"0000-00-00\",\n" +
                "      \"offer_title\": \"\",\n" +
                "      \"product_desc\": \"\",\n" +
                "      \"category_id\": \"1\",\n" +
                "      \"img_url\": \"http:\\/\\/52.3.144.26\\/scraper\\/product_images\\/265.jpg\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"product_id\": \"295\",\n" +
                "      \"product_name\": \"Vino Tinto Alegoria Gran Reserva Malbec N C 750 ml\",\n" +
                "      \"brand_name\": \"Navarro Correas\",\n" +
                "      \"price\": \"128.50\",\n" +
                "      \"offer_price\": \"77.10\",\n" +
                "      \"source_card_price\": null,\n" +
                "      \"store\": \"wong\",\n" +
                "      \"valid_until\": \"0000-00-00\",\n" +
                "      \"offer_title\": \"\",\n" +
                "      \"product_desc\": \"\",\n" +
                "      \"category_id\": \"1\",\n" +
                "      \"img_url\": \"http:\\/\\/52.3.144.26\\/scraper\\/product_images\\/295.jpg\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"product_id\": \"296\",\n" +
                "      \"product_name\": \"Vino Tinto Navarro Correas Coleccion Privada Cabernet Sauvignon Botella 750 ml\",\n" +
                "      \"brand_name\": \"Navarro Correas\",\n" +
                "      \"price\": \"61.90\",\n" +
                "      \"offer_price\": \"41.90\",\n" +
                "      \"source_card_price\": null,\n" +
                "      \"store\": \"wong\",\n" +
                "      \"valid_until\": \"0000-00-00\",\n" +
                "      \"offer_title\": \"\",\n" +
                "      \"product_desc\": \"\",\n" +
                "      \"category_id\": \"1\",\n" +
                "      \"img_url\": \"http:\\/\\/52.3.144.26\\/scraper\\/product_images\\/296.jpg\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"product_id\": \"297\",\n" +
                "      \"product_name\": \"Vino Tinto Navarro Correas Coleccion Privada Malbec Botella 750 ml\",\n" +
                "      \"brand_name\": \"Navarro Correas\",\n" +
                "      \"price\": \"61.90\",\n" +
                "      \"offer_price\": \"41.90\",\n" +
                "      \"source_card_price\": null,\n" +
                "      \"store\": \"wong\",\n" +
                "      \"valid_until\": \"0000-00-00\",\n" +
                "      \"offer_title\": \"\",\n" +
                "      \"product_desc\": \"\",\n" +
                "      \"category_id\": \"1\",\n" +
                "      \"img_url\": \"http:\\/\\/52.3.144.26\\/scraper\\/product_images\\/297.jpg\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"product_id\": \"373\",\n" +
                "      \"product_name\": \"Vino Tinto Trivento Golden Reserva Malbec Botella 750 ml\",\n" +
                "      \"brand_name\": \"Trivento\",\n" +
                "      \"price\": \"99.90\",\n" +
                "      \"offer_price\": \"64.93\",\n" +
                "      \"source_card_price\": null,\n" +
                "      \"store\": \"wong\",\n" +
                "      \"valid_until\": \"0000-00-00\",\n" +
                "      \"offer_title\": \"\",\n" +
                "      \"product_desc\": \"\",\n" +
                "      \"category_id\": \"1\",\n" +
                "      \"img_url\": \"http:\\/\\/52.3.144.26\\/scraper\\/product_images\\/373.jpg\"\n" +
                "    }]";
        try {
            jsonArray = new JSONArray(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
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
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        int count = productList.size();
        Toast.makeText(getContext(), "Number of products: " + count, Toast.LENGTH_SHORT).show();
        List<Product> productListLocal = new ArrayList<>();
        productListLocal.addAll(productList);
        SearchRecyclerViewAdapter_List adapterList = searchRecyclerViewAdapterLists.get(0);
        Search_RecyclerView_Adapter adapter = adapterList.getSearchRecyclerViewAdapter();
        adapter.update_productList_searches(productListLocal);
*/


        final ProgressBar progressBar = rlSearch.findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, UrlConstants.GET_PRODUCT, null,
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
                                String store;//
                                int category_id;//
                                String valid_until;//
                                String img_url;//
                                Product product = null;
                                productList.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    productObj = jsonArray.getJSONObject(i);
                                    product_id = Integer.parseInt(productObj.getString("product_id"));
                                    product_name = productObj.getString("product_name");
                                    product_desc = productObj.getString("product_desc");
                                    brand_name = productObj.getString("brand_name");
                                    price = productObj.getString("price");
                                    offer_price = productObj.getString("offer_price");
                                    offer_title = productObj.getString("offer_title");
                                    store = productObj.getString("store");
                                    category_id = Integer.parseInt(productObj.getString("category_id"));
                                    valid_until = productObj.getString("valid_until");
                                    img_url = productObj.getString("img_url");

                                    product = new Product(product_id, product_name, product_desc, brand_name, price, offer_price, offer_title, store, category_id, valid_until, img_url);
                                    productList.add(product);
                                }
                                int count = productList.size();
                                try {
                                    Toast.makeText(getContext(), "Number of products: " + count, Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                }
                                List<Product> productListLocal = new ArrayList<>();
                                productListLocal.addAll(productList);
                                loadSourcesButtons(productListLocal);
/*
                                SearchRecyclerViewAdapter_List adapterList = searchRecyclerViewAdapterLists.get(0);
                                Search_RecyclerView_Adapter adapter = adapterList.getSearchRecyclerViewAdapter();
                                adapter.update_productList_searches(productListLocal);
*/
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

    private void loadSourcesButtons(List<Product> filteredProductList) {

        //-------------------------------------Num of stores-----------//
        List<String> listStores = new ArrayList<>();
        String store;
        for (Product product : filteredProductList) {
            store = product.getStore() + " ";
            if (!listStores.contains(store))
                listStores.add(store);
        }


        //----------------show store buttons-----------------//

        for (int i = 0; i < tv_id.length; i++) {
            TextView tv = (TextView) rlSearch.findViewById(tv_id[i]);
            tv.setVisibility(View.GONE);
        }
        TextView tv;
        int i = 0;
        try {
            for (String str : listStores) {
                tv = rlSearch.findViewById(tv_id[i++]);
                tv.setText(str + "(x)");
                tv.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //-------------
        List<Product> productListFinal = new ArrayList<>();
        productListFinal.addAll(filteredProductList);
        /*SearchRecyclerViewAdapter_List adapterList = searchRecyclerViewAdapterLists.get(Integer.parseInt(current_categoryId));
        Search_RecyclerView_Adapter adapter = adapterList.getSearchRecyclerViewAdapter();
        adapter.update_productList_searches(productListFinal);*/

        refreshSourcesButtons(productListFinal, null);

    }

    private void refreshSourcesButtons(List<Product> productListFinal, TextView v) {
        list.clear();
        list.addAll(productListFinal);
        String str1 = "", str2 = "";
        for (Product product : productListFinal) {
            str1 = product.getStore() + " (x)";
            if (v != null) {
                str2 = v.getText().toString();
                if (str1.equalsIgnoreCase(str2))
                    list.remove(product);
            }
        }
        SearchRecyclerViewAdapter_List adapterList = searchRecyclerViewAdapterLists.get(Integer.parseInt(current_categoryId));
        Search_RecyclerView_Adapter adapter = adapterList.getSearchRecyclerViewAdapter();
        adapter.update_productList_searches(list);
        //list.addAll(productListFinal);
        TextView tv;
        //setOnCLickListeners
        for (int i = 0; i < tv_id.length; i++) {
            tv = (TextView) rlSearch.findViewById(tv_id[i]);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                    List<Product> lst = new ArrayList<>();
                    lst.addAll(list);
                    refreshSourcesButtons(lst, (TextView) v);
                }
            });
        }
    }

    public void show_searchView() {
        Toolbar toolbar_search = rlSearch.findViewById(R.id.toolbar_search);
        if (toolbar_search.getVisibility() == View.VISIBLE)
            toolbar_search.setVisibility(View.GONE);
        else
            toolbar_search.setVisibility(View.VISIBLE);
    }


    @Override
    public void OnCategoryListFetchedListener(List<String> list) {

    }

    @Override
    public void OnProductListFetchedListener(String category_id, List<Product> productList, boolean resetSources) {

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

    private class SearchRecyclerViewAdapter_List {
        int id;
        private Search_RecyclerView_Adapter searchRecyclerViewAdapter;

        public SearchRecyclerViewAdapter_List(int id, Search_RecyclerView_Adapter searchRecyclerViewAdapter) {
            this.id = id;
            this.searchRecyclerViewAdapter = searchRecyclerViewAdapter;
        }

        public Search_RecyclerView_Adapter getSearchRecyclerViewAdapter() {
            return searchRecyclerViewAdapter;
        }
    }
}