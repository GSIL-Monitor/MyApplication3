package Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.questionsurvey.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import entity.Questionnaire;

/**
 * Created by cxy on 2018/1/5.
 */

public class SyncAdapter extends RecyclerView.Adapter<SyncAdapter.MyViewHolder> {

    private List<Questionnaire> questionnaireList;
    private Context context;

    public SyncAdapter(List<Questionnaire> questionnaireList, Context context) {
        this.questionnaireList = questionnaireList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sync, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Questionnaire questionnaire=questionnaireList.get(position);
        holder.tvTitle.setText(questionnaire.getTitle()+questionnaire.getVersion());
    }

    @Override
    public int getItemCount() {
        return questionnaireList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {

        @BindView(R.id.tv_title) TextView tvTitle;

        public MyViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this,view);

        }
    }
}
