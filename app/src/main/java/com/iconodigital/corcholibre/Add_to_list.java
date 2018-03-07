package com.iconodigital.corcholibre;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class Add_to_list extends AppCompatActivity {
    List_Recycler_Adapter mAdapter;
    DatabaseHandler db_handler;
    ArrayList<String> list_name_list;
    ArrayList<product_list> product_lists;
    ArrayList<Product> productArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Toast.makeText(this,"Click on List to add Items or click yellow button to add a new List",Toast.LENGTH_LONG).show();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                AlertDialog.Builder alert = new AlertDialog.Builder(Add_to_list.this,R.style.AppCompatAlertDialogStyle);
                //final EditText edittext = new EditText(getActivity());
                //final EditText edittext2 = new EditText(getActivity());

                LayoutInflater alert_inflater = (LayoutInflater) Add_to_list.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                LinearLayout alertbox_content = (LinearLayout) alert_inflater.inflate(R.layout.alertbox_notes, null);
                //final EditText offer= (EditText) alertbox_content.findViewById(R.id.edttxt_number);
                final EditText notes = (EditText) alertbox_content.findViewById(R.id.edttxt_notes);



                alert.setMessage("Introduzca el nombre de la lista ");
                alert.setTitle("Agregar nueva lista");

                alert.setView(alertbox_content);

                alert.setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //What ever you want to do with the value

                        //String offer_str = offer.getText().toString();
                        String notes_str = notes.getText().toString();
                        db_handler.addList(notes_str);
                        ArrayList<product_list>product_lists_new=db_handler.getLists();
                        product_lists.clear();
                        int i=0;
                        while(i<product_lists_new.size()){
                            product_lists.add(product_lists_new.get(i));
                            i++;
                        }

                        mAdapter.notifyDataSetChanged();


                    }
                });

                alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();

            }
        });
        db_handler=new DatabaseHandler(this);
         product_lists=db_handler.getLists();
        //list_name_list=new ArrayList<>(product_list_map.values());
        //HashMap<Integer,Product> prod_map=(HashMap<Integer,Product>) getIntent().getSerializableExtra("product_map");
        productArrayList=new ArrayList<>();
        if(Add_to_list.this.getIntent().getBooleanExtra("single_product",false)){
            productArrayList.add(applicationClass.single_selected_prod);
        }
        else{
            productArrayList.addAll(applicationClass.selected_prod_map.values());
        }


        mAdapter=new List_Recycler_Adapter(product_lists,productArrayList,this);


        RecyclerView list_recycler=(RecyclerView)findViewById(R.id.list_recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(Add_to_list.this);
        list_recycler.setLayoutManager(mLayoutManager);
        list_recycler.setItemAnimator(new DefaultItemAnimator());
        list_recycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        list_recycler.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}
