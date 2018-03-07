package com.iconodigital.corcholibre;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProductDetails extends AppCompatActivity {



    TextView tv_quantity;
    Button btn_increase,btn_decrease,btn_add_to_list;
    Product prod;
    boolean activity_opened_from_list;
    int list_id;
    ImageView iv_alert;
    ImageView iv_expand;
    TextView total_tv;
    Drawable default_drawable;
    FrameLayout container_frame;
    boolean image_zoomed;
    float total = 0F;
    float actual_price = 0F;
    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;
    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        tv_quantity=(TextView)findViewById(R.id.quantity);

        activity_opened_from_list=getIntent().getBooleanExtra("activity_opened_from_list",false);
        container_frame=(FrameLayout)findViewById(R.id.content_product_details);




//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        mShortAnimationDuration = this.getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            default_drawable = this.getResources().getDrawable(R.drawable.ic_groceries, this.getApplicationContext().getTheme());
        }
        else {
            default_drawable = this.getResources().getDrawable(R.drawable.ic_groceries);
        }


        prod = (Product) getIntent().getSerializableExtra("Product");
        //quantity=prod.getQuantity();
        tv_quantity.setText(prod.getQuantity()+"");
        ImageView prod_image=(ImageView) findViewById(R.id.prod_image);
        TextView prod_name=(TextView)findViewById(R.id.prod_name);
        total_tv=(TextView)findViewById(R.id.total);
        TextView prod_desc=(TextView)findViewById(R.id.prod_desc);
        TextView prod_price=(TextView)findViewById(R.id.prod_price);
        TextView prod_price_cut=(TextView)findViewById(R.id.prod_price_cut);
        btn_increase=(Button)findViewById(R.id.btn_increase);
        btn_decrease=(Button)findViewById(R.id.btn_decrease);
        btn_add_to_list=(Button)findViewById(R.id.btn_add_to_list);
        btn_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_quantity(true);
            }
        });
        btn_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_quantity(false);
            }
        });
        btn_add_to_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activity_opened_from_list){
                    int list_id=ProductDetails.this.getIntent().getIntExtra("list_id",-1);
                    if(list_id!=-1){
                        DatabaseHandler db_handler=new DatabaseHandler(ProductDetails.this);
                        db_handler.updateProduct(prod,list_id);

                        Intent resultIntent = new Intent();
                        // TODO Add extras or a data URI to this intent as appropriate.
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }

                }
                else{
                    /*applicationClass.selected_product_ids_arr.add(prod.getProduct_id());
                    applicationClass.selected_prod_map.put(prod.getProduct_id(),prod);
                    Snackbar.make(view, "Product added to selection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    finish();*/
                    applicationClass.single_selected_prod=prod;
                    Intent add_to_list_intent=new Intent(ProductDetails.this,Add_to_list.class);
                    add_to_list_intent.putExtra("single_product",true);
                    startActivity(add_to_list_intent);
                    finish();

                }

            }
        });


        prod_name.setText(prod.product_name);
        prod_desc.setText(prod.product_desc);
        prod_price_cut.setPaintFlags(0);
        if (prod.offer_price==null|| prod.offer_price.equals("")) {

            prod_price.setText(applicationClass.CURRENCY_SYMBOL+prod.price);
            actual_price=getPriceFromString(prod.price);


        }
        else{
            actual_price=getPriceFromString(prod.offer_price);

            prod_price.setText(applicationClass.CURRENCY_SYMBOL+prod.offer_price);
            prod_price_cut.setText(prod.price);
            prod_price_cut.setPaintFlags(prod_price_cut.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        total=actual_price*prod.getQuantity();
        total_tv.setText("Total: "+applicationClass.CURRENCY_SYMBOL+String.format("%.2f",total));
        String url = prod.img_url;
        new ImageLoaderHelper().init(this).load_image_universal(prod_image,url,default_drawable);

        iv_alert=(ImageView)findViewById(R.id.iv_alert);
        iv_expand=(ImageView)findViewById(R.id.expanded_image);
        prod_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(view, prod.product_id);
            }
        });

        /*
        if(
                prod.getOffer_price().trim().length()>0||
                prod.getOffer_conditions().trim().length()>0||
                prod.getOffer_title().trim().length()>0
                ){
            */
            iv_alert.setVisibility(View.VISIBLE);
            iv_alert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(ProductDetails.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Agregar notificación");
                    builder.setMessage("¿Desea agregar este producto a la lista de notificaciones?");
                    builder.setPositiveButton("DE ACUERDO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // what ever you want to do with No option.
                            DatabaseHandler db_handler=new DatabaseHandler(ProductDetails.this);
                            int alert_list_id=db_handler.getAlertListId();
                            ArrayList<Product> temp_prodLIst = new ArrayList<>();
                            temp_prodLIst.add(prod);
                            db_handler.addProducts(temp_prodLIst,alert_list_id);

                        }
                    });
                    builder.setNegativeButton("Cancelar", null);
                    builder.show();






                }
            });


        //}
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void change_quantity(boolean increase){
        if(increase){
            prod.setQuantity(prod.getQuantity()+1);
            tv_quantity.setText(""+prod.getQuantity());


        }
        else{
            if(prod.getQuantity()>1){
                prod.setQuantity(prod.getQuantity()-1);
                tv_quantity.setText(""+prod.getQuantity());
            }
        }
        total=actual_price*prod.getQuantity();
        total_tv.setText("Total: S/. "+String.format("%.2f",total));

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



    private void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) iv_expand;


        String url = prod.img_url;
        new ImageLoaderHelper().init(ProductDetails.this).load_image_universal(expandedImageView,url,default_drawable);


//        load_profile_image(expandedImageView, imageResId);
//        expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);

        container_frame.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }


}
