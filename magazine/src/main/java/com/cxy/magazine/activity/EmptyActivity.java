package com.cxy.magazine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import com.cxy.magazine.R;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmptyActivity extends Activity {

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;

    @BindView(R.id.emptyView)
    QMUIEmptyView mEmptyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        String topbarTitle=intent.getStringExtra("topbarTitle");
        String title=intent.getStringExtra("title");
        String message=intent.getStringExtra("message");

        //设置topbar
        mTopBar.setTitle(topbarTitle);

        //设置emptyview
        mEmptyView.show(title,message);
    }

}
