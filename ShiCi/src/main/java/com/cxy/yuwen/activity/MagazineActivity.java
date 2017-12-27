package com.cxy.yuwen.activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.cxy.yuwen.R;
import com.cxy.yuwen.fragment.MagazineClassFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MagazineActivity extends BasicActivity {




    @BindView(R.id.toolbar)  Toolbar toolbar;
    @BindView(R.id.tv_title) TextView tvTitle;


    private String htmlUrl="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazine);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent=this.getIntent();
        htmlUrl=intent.getStringExtra("url");
        String title=intent.getStringExtra("title");
        tvTitle.setText(title);

        MagazineClassFragment magazineClassFragment=MagazineClassFragment.newInstance(htmlUrl);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, magazineClassFragment)
                .commit();



    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}
