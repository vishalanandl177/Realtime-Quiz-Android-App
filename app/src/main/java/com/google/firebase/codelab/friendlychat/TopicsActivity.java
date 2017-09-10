package com.google.firebase.codelab.friendlychat;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TopicsActivity extends AppCompatActivity {


    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        public TextView topicTextView;
        View view;

        public TopicViewHolder(View v) {
            super(v);
            topicTextView = (TextView) itemView.findViewById(R.id.topicTextView);
            this.view = v;
        }
    }

    private RecyclerView topicsRecyclerView;
    TopicAdapter topicAdapter;
    private List<TopicHolder> topicList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        topicsRecyclerView= (RecyclerView) findViewById(R.id.topicRecyclerView);

      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        topicAdapter = new TopicAdapter(topicList, TopicsActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        topicsRecyclerView.setLayoutManager(mLayoutManager);
        topicsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        topicsRecyclerView.setAdapter(topicAdapter);

        prepareMovieData();
        topicsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private void prepareMovieData() {
        TopicHolder holder = new TopicHolder("Java");
        topicList.add(holder);
        holder = new TopicHolder("C++");
        topicList.add(holder);
        holder = new TopicHolder("HTML");
        topicList.add(holder);
        holder = new TopicHolder("PHP");
        topicList.add(holder);
        holder = new TopicHolder("C");
        topicList.add(holder);

        topicAdapter.notifyDataSetChanged();
    }

}
