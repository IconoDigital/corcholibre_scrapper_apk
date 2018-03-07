package com.iconodigital.corcholibre;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by sourav on 24-Nov-16.
 */

public class My_File_Fragment extends Fragment {



    RecyclerView recyclerView;
    ArrayList<product_list> productList;
    Myfile_RecyclerView_Adapter mAdapter;
    LinearLayoutManager mLayoutManager;
    FloatingActionButton fab;
    DatabaseHandler db_handler;
    private FrameLayout flMyFile;
    private ImageView iv_expand;











    public My_File_Fragment() {
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
                .setActionBarTitle(getResources().getString(R.string.my_list));





        /**************************************************Taking UI Component Reference**************************************************/

        flMyFile = (FrameLayout) inflater.inflate(R.layout.fragment_recyclerview_my_file, container, false);
        recyclerView = (RecyclerView) flMyFile.findViewById(R.id.recycler_view_ofertas);
        iv_expand = (ImageView) flMyFile.findViewById(R.id.expanded_image);
        fab = (FloatingActionButton) flMyFile.findViewById(R.id.fab);



        final DatabaseHandler db_handler=new DatabaseHandler(this.getContext());
        productList=db_handler.getLists();
        if(productList.size()==0){
            Toast.makeText(getContext(),"Crea tu primera lista aqui",Toast.LENGTH_SHORT);
        }


        /**************************************************RecyclerView Settings**************************************************/
        mAdapter = new Myfile_RecyclerView_Adapter(productList,this.getActivity());
        mLayoutManager = new LinearLayoutManager(super.getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);







        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext(),R.style.AppCompatAlertDialogStyle);
                LayoutInflater alert_inflater = (LayoutInflater)  getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout alertbox_content = (LinearLayout) alert_inflater.inflate(R.layout.alertbox_notes, null);
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
                        productList.clear();
                        int i=0;
                        while(i<product_lists_new.size()){
                            productList.add(product_lists_new.get(i));
                            i++;
                        }

                        mAdapter.notifyDataSetChanged();


                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();



            }
        });



        return flMyFile;
    }

   public void refreshList(){
       productList.clear();
       DatabaseHandler db_handler=new DatabaseHandler(this.getContext());
       productList.addAll(db_handler.getLists());
       mAdapter.notifyDataSetChanged();
   }


}
