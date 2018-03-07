package com.iconodigital.corcholibre;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sourav on 23-Nov-16.
 */

public class Myfile_RecyclerView_Adapter extends RecyclerView.Adapter<Myfile_RecyclerView_Adapter.MyViewHolder> {

    Context context;
    private ArrayList<product_list> productList;


    public Myfile_RecyclerView_Adapter(ArrayList<product_list> productList,Context context) {
        this.productList = productList;
        this.context=context;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_cell_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {


        final product_list items = productList.get(position);
        holder.list_name.setText(items.list_name);
        holder.list_date.setText(items.date);

        holder.deleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder =
                        new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Delete List");
                builder.setMessage("Do you want delete this list and the products in it?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                        DatabaseHandler db_handler=new DatabaseHandler(context);
                        db_handler.deleteList(items.list_id);
                        ArrayList<product_list> productList_temp=db_handler.getLists();
                        productList.clear();
                        productList.addAll(productList_temp);
                        Myfile_RecyclerView_Adapter.this.notifyItemRemoved(position);

                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();


            }
        });


        holder.editList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(context,R.style.AppCompatAlertDialogStyle);
                //final EditText edittext = new EditText(getActivity());
                //final EditText edittext2 = new EditText(getActivity());

                LayoutInflater alert_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                LinearLayout alertbox_content = (LinearLayout) alert_inflater.inflate(R.layout.alertbox_notes, null);
                //final EditText offer= (EditText) alertbox_content.findViewById(R.id.edttxt_number);
                final EditText notes = (EditText) alertbox_content.findViewById(R.id.edttxt_notes);

                notes.setText(items.getList_name());
                notes.setHint("List Name");

                alert.setMessage("Edit List Name ");
                alert.setTitle("Edit");

                alert.setView(alertbox_content);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //What ever you want to do with the value

                        DatabaseHandler db_handler=new DatabaseHandler(context);
                        db_handler.updateListName(items.getList_id(),notes.getText().toString());

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
        holder.shareList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(context,R.style.AppCompatAlertDialogStyle);
                //final EditText edittext = new EditText(getActivity());
                //final EditText edittext2 = new EditText(getActivity());

                LayoutInflater alert_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                LinearLayout alertbox_content = (LinearLayout) alert_inflater.inflate(R.layout.alertbox_notes, null);
                //final EditText offer= (EditText) alertbox_content.findViewById(R.id.edttxt_number);
                final EditText notes = (EditText) alertbox_content.findViewById(R.id.edttxt_notes);

                notes.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                notes.setHint("Email id");

                alert.setMessage("Enter the email of the person you want to share this list with.");
                alert.setTitle("Share this list");

                alert.setView(alertbox_content);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if(isValidEmail(notes.getText())) {


                            /**
                             * call webservice to share the list.
                             */
                            try {

                                //What ever you want to do with the value
                                DatabaseHandler db_handler = new DatabaseHandler(context);
                                ArrayList<Product> share_list = db_handler.getProducts(items.getList_id());
                                Gson gson = new Gson();
                                byte share_list_bytes[] = gson.toJson(share_list).getBytes();
                                JSONArray product_arr_json = new JSONArray();
                                for (Product prod : share_list) {
                                    JSONObject prod_obj = new JSONObject();
                                    prod_obj.put("product_id", prod.product_id);
                                    prod_obj.put("quantity", prod.quantity);

                                    product_arr_json.put(prod_obj);
                                }
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                                String sender_name = preferences.getString("Name", null);
                                String sender_email = preferences.getString("Email", null);
                                async_share_list shareListRequest = new async_share_list(notes.getText().toString(), share_list_bytes, product_arr_json
                                        , items.getList_name(), sender_name, sender_email, context);
                                shareListRequest.execute();


                            } catch (JSONException e) {

                            }




                        /*DatabaseHandler db_handler=new DatabaseHandler(context);
                        db_handler.updateListName(items.getList_id(),notes.getText().toString());*/
                        }
                        else{
                            Toast.makeText(context,"Enter a valid email address",Toast.LENGTH_SHORT).show();
                        }

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

        holder.parent_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent list_intent=new Intent(context,ListItemsActivity.class);
                list_intent.putExtra("list_id",productList.get(position).getList_id());
                list_intent.putExtra("list_name",productList.get(position).getList_name());
                context.startActivity(list_intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView list_name;
        public TextView list_date;
        public ImageView editList, deleteList, shareList;
        LinearLayout parent_ll;


        public MyViewHolder(View view) {
            super(view);
            parent_ll = (LinearLayout) view;
            list_name = (TextView) view.findViewById(R.id.list_name);
            list_date = (TextView) view.findViewById(R.id.date);

            editList = (ImageView) view.findViewById(R.id.editlist);
            deleteList = (ImageView) view.findViewById(R.id.deletelist);
            shareList = (ImageView) view.findViewById(R.id.sharelist);

        }
    }

}



