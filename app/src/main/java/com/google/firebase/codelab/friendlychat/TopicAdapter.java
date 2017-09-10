package com.google.firebase.codelab.friendlychat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.MyViewHolder> {

    private List<TopicHolder> topicList;
    TopicsActivity topicsActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView topicTextView;
        CardView cardView;


        public MyViewHolder(View view, TopicsActivity topicsActivity) {
            super(view);
            topicTextView = (TextView) view.findViewById(R.id.topicTextView);
            cardView = (CardView) view.findViewById(R.id.cardView);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
           // Toast.makeText(topicsActivity, topicTextView.getText(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(topicsActivity, MainActivity.class);
            intent.putExtra("topic", topicTextView.getText());
            topicsActivity.startActivity(intent);
        }
    }


    public TopicAdapter(List<TopicHolder> topicList, Context c ){
        this.topicList = topicList;
        this.topicsActivity = (TopicsActivity) c;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topics, parent, false);

        return new MyViewHolder(itemView, topicsActivity);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TopicHolder topicHolder = topicList.get(position);
        holder.topicTextView.setText(topicHolder.getTopic());
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }
}