package com.applozic.livestreamingdemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.api.ApplozicMqttService;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.conversation.ApplozicConversation;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.broadcast.AlEventManager;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.exception.ApplozicException;
import com.applozic.mobicomkit.listners.AlLoginHandler;
import com.applozic.mobicomkit.listners.ApplozicUIListener;
import com.applozic.mobicomkit.listners.MessageListHandler;
import com.applozic.mobicommons.people.channel.Channel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ApplozicUIListener {


    Button launchButton;

    public static String App_ID = "applozic-sample-app";
    List<Message> messageList =  new ArrayList<Message>();
    RecyclerView listView =null;
    ApplozicMessageAdapter adapter =null;
    public static final Integer CHANNEL_KEY = 24049394;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginUser(this);
        listView = findViewById(R.id.chat_list_view);
    }


    public void loginUser(Context context) {

        Applozic.init(context, App_ID);
        Applozic.connectPublish(context);
        AlEventManager.getInstance().registerUIListener("listener2", this);

        User user = new User();
        user.setUserId("akLiveStreamingDemo");
        user.setDisplayName( "Live Streaming Demo");
        user.setAuthenticationTypeId(User.AuthenticationType.APPLOZIC.getValue());
        user.setPassword("123456");

        Applozic.connectUser(context, user, new AlLoginHandler() {
            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {

                Toast.makeText(MainActivity.this, "Login SucessFul",Toast.LENGTH_LONG).show();
                Applozic.connectPublish(context);
                Channel channel = ChannelService.getInstance(context).getChannel(CHANNEL_KEY);
                Applozic.subscribeToTyping(getApplicationContext(), channel , null);
                loadList();

            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                Toast.makeText(MainActivity.this, "Login Failed",Toast.LENGTH_LONG).show();
            }
        });

    }

    public void loadList(){

        Long createdAtTime = new Date().getTime();

        ApplozicConversation.getMessageListForChannel(getBaseContext(), CHANNEL_KEY, createdAtTime, new MessageListHandler() {
            @Override
            public void onResult(List<Message> messageListForChannel, ApplozicException e) {
                if(e == null){

                    Toast.makeText(MainActivity.this, "List Loaded",Toast.LENGTH_LONG).show();
                    messageList = messageListForChannel;
                    adapter= new ApplozicMessageAdapter(getBaseContext(),messageList);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }else{
                    //error in fetching messages
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Applozic.disconnectPublish(MainActivity.this);

    }

    //Applozic Event listeners

    @Override
    public void onMessageSent(Message message) {
        Log.i("MainActivity", "onMessageSent: " +message );

    }

    @Override
    public void onMessageReceived(Message message) {

        Log.i("MainActivity", "onMessageReceived: " +message );
        messageList.add(message);
        adapter.notifyDataSetChanged();
        listView.setEnabled(false);
        listView.smoothScrollToPosition(messageList.size()-1);



    }

    @Override
    public void onLoadMore(boolean loadMore) {

    }

    @Override
    public void onMessageSync(Message message, String key) {

    }

    @Override
    public void onMessageDeleted(String messageKey, String userId) {

    }

    @Override
    public void onMessageDelivered(Message message, String userId) {

    }

    @Override
    public void onAllMessagesDelivered(String userId) {

    }

    @Override
    public void onAllMessagesRead(String userId) {

    }

    @Override
    public void onConversationDeleted(String userId, Integer channelKey, String response) {

    }

    @Override
    public void onUpdateTypingStatus(String userId, String isTyping) {

    }

    @Override
    public void onUpdateLastSeen(String userId) {

    }

    @Override
    public void onMqttDisconnected() {

    }

    @Override
    public void onMqttConnected() {

    }

    @Override
    public void onUserOnline() {

    }

    @Override
    public void onUserOffline() {

    }

    @Override
    public void onChannelUpdated() {

    }

    @Override
    public void onConversationRead(String userId, boolean isGroup) {

    }

    @Override
    public void onUserDetailUpdated(String userId) {

    }

    @Override
    public void onMessageMetadataUpdated(String keyString) {

    }

    @Override
    public void onUserMute(boolean mute, String userId) {

    }
}
