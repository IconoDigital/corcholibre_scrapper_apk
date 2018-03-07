package com.iconodigital.corcholibre;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ARIJIT on 27-Jan-17.
 */

public class AlertList_RecylcerView_adapter extends RecyclerView.Adapter<AlertList_RecylcerView_adapter.MyViewHolder> {

    static final int CHANGE_PRODUCT_QUANTITY = 1;
    Drawable default_drawable;
    Alert_list_activity context;
    FrameLayout flPlazavia;
    int list_id;
    private ArrayList<Product> productItemList;
    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;
    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;
    private ImageView iv_expand;


    public AlertList_RecylcerView_adapter(ArrayList<Product> productItemList, Alert_list_activity context, ImageView iv_expand, FrameLayout flPlazavia, int list_id) {
        this.productItemList = productItemList;
        this.iv_expand = iv_expand;
        this.context = context;
        this.flPlazavia=flPlazavia;
        this.list_id=list_id;
    }

    @Override
    public AlertList_RecylcerView_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_cell_list_item, parent, false);
        return new AlertList_RecylcerView_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AlertList_RecylcerView_adapter.MyViewHolder holder, final int position) {


        final Product items = productItemList.get(position);

        String url = applicationClass.WS_IMAGE_URL_BIG + productItemList.get(position).product_id + applicationClass.WS_JPG;
        new ImageLoaderHelper().init(context).load_image_universal(holder.productImg,url,default_drawable);
        if (items.store.equals("tottus")) {

            holder.categoryImg.setImageResource(R.drawable.ic_tottus_logo);
        }else {

            holder.categoryImg.setImageResource(R.drawable.ic_plazavea_logo);
        }


        holder.name.setText(items.product_name.trim());
        holder.description.setText(items.product_desc.trim());
        holder.price.setText(applicationClass.CURRENCY_SYMBOL+items.price.trim());
        if (items.offer_price==null|| items.offer_price.equals("")){


            holder.validTill.setVisibility(View.GONE);
            holder.offer.setVisibility(View.GONE);


            holder.offerPrice.setVisibility(View.GONE);

            //new ImageLoaderHelper().init(context).load_image_universal(holder.categoryImg,url,default_drawable);


        }else {

            if(
                    items.offer_price.trim().equals("")&&
                            items.valid_until.trim().equals("")&&
                            items.offer_title.trim().equals("")
                    ){
                holder.validTill.setVisibility(View.GONE);
                holder.offer.setVisibility(View.GONE);
                holder.offerPrice.setVisibility(View.GONE);
            }
            else{
                if(items.valid_until.equals("0000-00-00")){
                    holder.validTill.setVisibility(View.GONE);
                }
                else{
                    holder.validTill.setText("Fecha Venc "+items.valid_until);
                }

                if(items.offer_title.trim().equals("")){
                    holder.offer.setText("ofertas");
                }
                else{
                    holder.offer.setText(items.offer_title);
                }

                holder.offerPrice.setText(applicationClass.CURRENCY_SYMBOL+items.offer_price);
                if(!items.offer_price.trim().equals(""))
                    holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


            }

        }



        holder.parent_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent product_details=new Intent(context,ProductDetails.class);
                product_details.putExtra("Product", items);
                product_details.putExtra("activity_opened_from_list",true);
                product_details.putExtra("list_id",list_id);
                context.startActivityForResult(product_details,CHANGE_PRODUCT_QUANTITY);
            }
        });


        holder.deleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHandler db_handler=new DatabaseHandler(context);
                db_handler.deleteFromTable("list_products","product_id_web",items.product_id);
                productItemList.remove(position);
                AlertList_RecylcerView_adapter.this.notifyItemRemoved(position);
                context.updateTotalTVs();


            }
        });

        holder.productImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                zoomImageFromThumb(holder.productImg, productItemList.get(position).product_id);

            }
        });



    }

    @Override
    public int getItemCount() {
        return productItemList.size();
    }

    private void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) iv_expand;


        String url = applicationClass.WS_IMAGE_URL_BIG + imageResId + applicationClass.WS_JPG;
        new ImageLoaderHelper().init(context).load_image_universal(expandedImageView, url, default_drawable);


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

        flPlazavia.getGlobalVisibleRect(finalBounds, globalOffset);
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView description, name, brand, price, offerPrice, validTill, offer;
        ImageView productImg, categoryImg;
        ImageView deleteList;
        LinearLayout parent_ll;
        Context context;


        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tvofertasName);
            parent_ll = (LinearLayout) view;
            description = (TextView) view.findViewById(R.id.tvofertasDescription);

            price = (TextView) view.findViewById(R.id.tvofertasPrice);
            offerPrice = (TextView) view.findViewById(R.id.tvofertasOfferPrice);
            validTill = (TextView) view.findViewById(R.id.tvofertasValidTill);
            offer = (TextView) view.findViewById(R.id.tvofertasOffer);

            productImg = (ImageView) view.findViewById(R.id.ivofertasProduct);
            categoryImg = (ImageView) view.findViewById(R.id.ivofertasCategory);
            deleteList = (ImageView) view.findViewById(R.id.delete_product);


        }
    }

}
