package com.example.socialmedia.UI.Activity;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Control.NotificationManager;
import com.example.socialmedia.Control.SharedPreferencesManager;
import com.example.socialmedia.Data.Firebase.RealtimeDatabase.NotificationRepository;
import com.example.socialmedia.Model.Notification;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Activity.Chat.ChatActivity;
import com.example.socialmedia.UI.MainView;
import com.example.socialmedia.UI.RecyclerView.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        User user= SharedPreferencesManager.getUser(getApplicationContext());
        RecyclerView recyclerView=findViewById(R.id.recyclerViewNotification);
        recyclerView.setPaddingRelative(9,9,9,9);
        List<Notification> list=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));



        NotificationManager notificationManager=new NotificationManager();
        notificationManager.getNotification(user.getId(), new NotificationRepository.GetNotificationCallback() {
            @Override
            public void getNotificationSuccess(List<Notification> notifications) {
                list.addAll(notifications);
                recyclerView.setAdapter(new NotificationAdapter(NotificationActivity.this,list));

            }

            @Override
            public void getNotificationFailure(Exception e) {

            }
        });


        ImageView back=findViewById(R.id.backNotification);
        ImageView home=findViewById(R.id.homeNotificationActivity);
        ImageView chat=findViewById(R.id.chatIconNotificationActivity);

        View.OnClickListener activityIconListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.homeNotificationActivity){
                    Intent intent=new Intent(getBaseContext(), MainView.class);
                    startActivity(intent);
                }else if(v.getId()==R.id.chatIconNotificationActivity){
                    Intent intent=new Intent(getBaseContext(), ChatActivity.class);
                    startActivity(intent);
                }else if(v.getId()==R.id.videoIconNotificationActivity){

                }else if(v.getId()==R.id.backNotification){
                    finish();
                }
            }
        };
        back.setOnClickListener(activityIconListener);
        home.setOnClickListener(activityIconListener);
        chat.setOnClickListener(activityIconListener);

    }
}
