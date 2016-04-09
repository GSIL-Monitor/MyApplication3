package com.example.cxy.myapplication;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapListActivity extends AppCompatActivity {
    ListView listView;
    SimpleAdapter adapter;

    private List<Map<String, Object>> mapList;
    private Map<String, Object> map;
    private int[] imageids = { R.mipmap.map01,
    R.mipmap.map02,R.mipmap.map03,R.mipmap.map04,R.mipmap.map05, R.mipmap.map06};
    private int[] image={R.id.imageView};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_list);
        listView=(ListView) this.findViewById(R.id.MyListView);
        mapList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < imageids.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", imageids[i]);

            mapList.add(map);
        }

        adapter = new SimpleAdapter(this, mapList,
                R.layout.listview_item, new String[]{"image"},image);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);

    }

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            ListView lv = (ListView)parent;
            HashMap<String,Object> imageMap = (HashMap<String,Object>)lv.getItemAtPosition(position);//SimpleAdapter返回Map
            try {
                int imageId=(int)imageMap.get("image");
                if (GameView.isFinish){
                    GameView.imageId=imageId;

                }

                //Toast.makeText(getApplicationContext(), imageId, Toast.LENGTH_SHORT).show();
                finish();

            } catch (Resources.NotFoundException e) {
                Toast.makeText(getApplicationContext(), "出错了", Toast.LENGTH_SHORT).show();
            }
        }
    };


}
