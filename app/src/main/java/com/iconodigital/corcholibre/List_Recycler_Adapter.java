package com.iconodigital.corcholibre;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ARIJIT on 12/5/2016.
 */

public class List_Recycler_Adapter extends RecyclerView.Adapter<List_Recycler_Adapter.MyViewHolder> {
    ArrayList<product_list> list_name_list;
    ArrayList<Product> productArrayList;
    Context context;

    public List_Recycler_Adapter(ArrayList<product_list> list_name_list, ArrayList<Product> productArrayList,
                                 Context context) {
        this.list_name_list=list_name_list;
        this.context=context;
        this.productArrayList=productArrayList;
    }

    @Override
    public List_Recycler_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_recycler_cell, parent, false);


        return new List_Recycler_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(List_Recycler_Adapter.MyViewHolder holder, final int position) {

    holder.list_name_tv.setText(list_name_list.get(position).list_name);
    holder.list_date_tv.setText(list_name_list.get(position).date);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Agregar Producto");
                builder.setMessage("Quieres agregar  este producto a la lista \"List name\"");
                builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                        DatabaseHandler db_handler=new DatabaseHandler(context);
                        db_handler.addProducts(productArrayList,list_name_list.get(position).list_id);
                        AlertDialog.Builder builderinner =
                                new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                        builderinner.setTitle("Ir a la lista");
                        builderinner.setMessage("Do you want to go to the list or back to search");
                        builderinner.setPositiveButton("Ir a la lista",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                                Intent list_intent= new Intent(context,ListItemsActivity.class);
                                list_intent.putExtra("list_id",list_name_list.get(position).list_id);
                                list_intent.putExtra("list_name",list_name_list.get(position).list_name);
                                context.startActivity(list_intent);
                                //applicationClass.selected_prod_map.clear();
                                //applicationClass.selected_product_ids_arr.clear();
                                ((Add_to_list)context).finish();
                            }

                        });
                        builderinner.setNegativeButton("seguir buscando", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                                ((Add_to_list)context).finish();
                            }
                        });
                        builderinner.show();

                    }
                });
                builder.setNegativeButton("Cancelar", null);
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list_name_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView list_name_tv;
        TextView list_date_tv;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            list_name_tv = (TextView) view.findViewById(R.id.list_name);
            list_date_tv = (TextView) view.findViewById(R.id.date);
            cardView = (CardView) view.findViewById(R.id.cardView);

        }
    }

}
