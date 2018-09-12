package com.cxy.magazine.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.cxy.magazine.R;

/**
 * Created by cxy on 2018/8/26.
 */

public class SampleFooter extends RelativeLayout {
    public SampleFooter(Context context) {
        super(context);
        init(context);
    }

    public SampleFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SampleFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {

        inflate(context,  R.layout.layout_empty, this);
    }
}
