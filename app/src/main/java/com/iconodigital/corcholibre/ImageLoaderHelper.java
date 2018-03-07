package com.iconodigital.corcholibre;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by ARIJIT on 12/4/2016.
 */

public class ImageLoaderHelper {
    Context context;
    public ImageLoaderHelper init(Context context) {
        ImageLoaderHelper imageLoaderHelper=new ImageLoaderHelper();
        imageLoaderHelper.context=context;
        return imageLoaderHelper;
    }

    public void load_image_universal(final ImageView Iv, final String url, final Drawable fall_back_image) {

        //final String url = applicationClass.WS_IMAGE_URL_BIG + product_id + applicationClass.WS_JPG;


        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(fall_back_image)
                //.showImageOnFail(default_drawable)
                .showImageOnLoading(fall_back_image).build();

//initialize image view


//download and display image from url


        imageLoader.displayImage(url, Iv, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                                        FailReason failReason) {
                // TODO Auto-generated method stub
                // This will handle 404 and it will catch null exception
                // do here what you want to do
                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisk(true).resetViewBeforeLoading(true)
                        .showImageForEmptyUri(fall_back_image)
                        .showImageOnFail(fall_back_image)
                        .showImageOnLoading(fall_back_image).build();

                imageLoader.displayImage(String.valueOf(R.drawable.ic_groceries), Iv);
                Log.e("Image Load Error","Could not load image");

            }

            @Override
            public void onLoadingComplete(String imageUri,
                                          View view, Bitmap loadedImage) {
                // TODO Auto-generated method stub
                if (Iv == null)
                    return;

                Iv.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri,
                                           View view) {
            }

        });
    }

}
