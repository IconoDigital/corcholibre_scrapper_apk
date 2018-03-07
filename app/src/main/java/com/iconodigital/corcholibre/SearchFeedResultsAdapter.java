package com.iconodigital.corcholibre;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by ARIJIT on 12/8/2016.
 */

 class SearchFeedResultsAdapter extends SimpleCursorAdapter {
    private static final String tag=SearchFeedResultsAdapter.class.getName();
    private Context context=null;
    public SearchFeedResultsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context=context;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tv_name=(TextView)view.findViewById(R.id.name);
        TextView tv_brand=(TextView)view.findViewById(R.id.brand);
        tv_name.setText(cursor.getString(1));
        tv_brand.setText(cursor.getString(3));



    }
}