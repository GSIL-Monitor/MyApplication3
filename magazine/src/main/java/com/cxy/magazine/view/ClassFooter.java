package com.cxy.magazine.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.cxy.magazine.R;

public class ClassFooter extends RelativeLayout {
    public ClassFooter(Context context) {
        super(context);
        init(context);
    }

    public ClassFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ClassFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {

        inflate(context,  R.layout.footer_class, this);
    }
}
