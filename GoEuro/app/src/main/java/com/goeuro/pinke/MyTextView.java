package com.goeuro.pinke;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by admin on 09-Aug-15.
 */
public class MyTextView extends TextView{
    public MyTextView(Context paramContext)
    {
        this(paramContext, null);
    }

    public MyTextView(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);

        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Semibold.ttf"));
    }

}


