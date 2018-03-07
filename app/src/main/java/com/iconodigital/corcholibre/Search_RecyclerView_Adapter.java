package com.iconodigital.corcholibre;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sourav on 02-Dec-16.
 */

public class Search_RecyclerView_Adapter extends RecyclerView.Adapter<Search_RecyclerView_Adapter.MyViewHolder> {

    Drawable default_drawable;
    Context context;
    String type;
    FrameLayout flPlazavia;
    private List<Product> productList_searches;
    private ImageView iv_expand;
    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;
    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;


    public Search_RecyclerView_Adapter(List<Product> productList_searches, Context context,
                                       String type) {
        this.productList_searches = productList_searches;

        this.context = context;
        this.type = type;

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = context.getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            default_drawable = context.getResources().getDrawable(R.drawable.bottle_loader, context.getApplicationContext().getTheme());
        } else {
            default_drawable = context.getResources().getDrawable(R.drawable.bottle_loader);
        }
    }

    public void setIv_expand(ImageView iv_expand) {
        this.iv_expand = iv_expand;
    }

    public void setFlPlazavia(FrameLayout flPlazavia) {
        this.flPlazavia = flPlazavia;
    }

    @Override
    public Search_RecyclerView_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_cell_ofertas, parent, false);


        return new Search_RecyclerView_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final Search_RecyclerView_Adapter.MyViewHolder holder, final int position) {


        final Product item = productList_searches.get(position);

        String url = item.img_url;
        new ImageLoaderHelper().init(context).load_image_universal(holder.productImg, url, default_drawable);
        if (item.store.equals("tottus")) {
            holder.categoryImg.setImageResource(R.drawable.ic_tottus_logo);
        } else if (item.store.equals("wong")) {
            holder.categoryImg.setImageResource(R.drawable.ic_wong_logo);
        } else if (item.store.equals("plazavea")) {
            holder.categoryImg.setImageResource(R.drawable.ic_plazavea_logo);
        } else {
            holder.categoryImg.setImageResource(R.drawable.bottle_loader);
        }


        holder.name.setText(item.product_name.trim());
        holder.description.setText(item.product_desc.trim());
        holder.price.setText(applicationClass.CURRENCY_SYMBOL + item.price.trim());
        holder.price.setPaintFlags(0);

        if (item.offer_price == null || item.offer_price.equals("")) {
            holder.validTill.setVisibility(View.GONE);
            holder.offer.setVisibility(View.GONE);
            holder.offerPrice.setVisibility(View.GONE);
            //new ImageLoaderHelper().init(context).load_image_universal(holder.categoryImg,url,default_drawable);
        } else {
            if (item.offer_price.trim().equals("") &&
                    item.valid_until.trim().equals("") && item.offer_title.trim().equals("")
                    ) {
                holder.validTill.setVisibility(View.GONE);
                holder.offer.setVisibility(View.GONE);
                holder.offerPrice.setVisibility(View.GONE);
            } else {
                if (item.valid_until.equals("0000-00-00")) {
                    holder.validTill.setVisibility(View.GONE);
                } else {
                    holder.validTill.setText("Fecha Venc " + item.valid_until);
                }

                if (item.offer_title.equals("")) {
                    holder.offer.setText("oferta");
                } else {
                    holder.offer.setText(item.offer_title);
                }

                holder.offer.setVisibility(View.VISIBLE);
                holder.offerPrice.setText(applicationClass.CURRENCY_SYMBOL + item.offer_price.trim());
                if (item.offer_price.trim().equals("0.00")) {
                    holder.offer.setVisibility(View.GONE);
                } else if (!item.offer_price.trim().equals("0.00") && !item.price.trim().equals(""))
                    holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

        }

//for version 2
/*
        holder.parent_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent product_details = new Intent(context, ProductDetails.class);
                product_details.putExtra("Product", productList_searches.get(position));
                //product_details.putExtra("selected_product_ids_arr",selected_product_ids_arr);
                context.startActivity(product_details);
            }
        });
*/

        if (applicationClass.selected_product_ids_arr.contains(productList_searches.get(position).product_id)) {
            holder.checkBox.setChecked(true);
        } else
            holder.checkBox.setChecked(false);


        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int product_id = productList_searches.get(position).product_id;
                if (((CheckBox) view).isChecked()) {
                    applicationClass.selected_product_ids_arr.add(product_id);
                    applicationClass.selected_prod_map.put(product_id, productList_searches.get(position));
                } else {
                    applicationClass.selected_product_ids_arr.remove(new Integer(product_id));
                    applicationClass.selected_prod_map.remove(product_id);
                }
                // current_frag.refresh_recycler_item(position,type);
            }
        });

        holder.productImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                zoomImageFromThumb(holder.productImg, productList_searches.get(position).product_id, item.img_url);

            }
        });

    }

    @Override
    public int getItemCount() {
        return productList_searches.size();
    }

    private void zoomImageFromThumb(final View thumbView, int imageResId, String img_url) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) iv_expand;

        new ImageLoaderHelper().init(context).load_image_universal(expandedImageView, img_url, default_drawable);


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

    public void update_productList_searches(List<Product> productList_searches) {

        List<Product> productList_temp = new ArrayList<>();
        productList_temp.addAll(productList_searches);
        try {
            this.productList_searches.clear();
            this.productList_searches.addAll(productList_temp);
            this.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView description, name, brand, price, offerPrice, validTill, offer;
        public ImageView productImg, categoryImg;
        LinearLayout parent_ll;
        CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tvofertasName);
            description = (TextView) view.findViewById(R.id.tvofertasDescription);
            parent_ll = (LinearLayout) view;

            price = (TextView) view.findViewById(R.id.tvofertasPrice);
            offerPrice = (TextView) view.findViewById(R.id.tvofertasOfferPrice);
            validTill = (TextView) view.findViewById(R.id.tvofertasValidTill);
            offer = (TextView) view.findViewById(R.id.tvofertasOffer);

            productImg = (ImageView) view.findViewById(R.id.ivofertasProduct);
            categoryImg = (ImageView) view.findViewById(R.id.ivofertasCategory);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        }
    }

}