package com.applozic.livestreamingdemo;

import android.content.Context;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.applozic.mobicomkit.api.conversation.MessageBuilder;
import com.applozic.mobicomkit.broadcast.AlEventManager;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.exception.ApplozicException;
import com.applozic.mobicomkit.listners.AlLoginHandler;
import com.applozic.mobicomkit.listners.ApplozicUIListener;
import com.applozic.mobicomkit.listners.MessageListHandler;
import com.applozic.mobicommons.people.channel.Channel;
import com.like.LikeButton;
import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ApplozicUIListener {


    Button launchButton;
    ImageView sendMessageView;
    LikeButton clapButton;

    EditText messageEditBox;

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
        sendMessageView = findViewById(R.id.message_send_button);
        clapButton = findViewById(R.id.clap_image);
        messageEditBox = findViewById(R.id.message_edit_text);
        setOnClickListener();

    }

    private void setOnClickListener() {
        sendMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(messageEditBox.getText().length() > 0){
                  new MessageBuilder(getApplicationContext()).setMessage(messageEditBox.getText().toString()).setGroupId(CHANNEL_KEY).send();
                  messageEditBox.setText("");
              }

            }
        });

        clapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MessageBuilder(getApplicationContext()).setMessage("LIKE").setGroupId(CHANNEL_KEY).send();
                clapButton.setLiked(true);
            }
        });

    }

    private void animateLike() {
        new ParticleSystem(MainActivity.this, 10000, R.drawable.star_pink, 20000)
                .setSpeedRange(0.2f, 0.5f)
                .oneShot(findViewById(R.id.chat_list_view), 100);
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
                    listView.setEnabled(false);
                    listView.smoothScrollToPosition(messageList.size()-1);

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
        messageList.add(message);
        adapter.notifyDataSetChanged();
        listView.setEnabled(false);
        listView.smoothScrollToPosition(messageList.size()-1);
        if(message.getMessage().equalsIgnoreCase("LIKE")){
            animateLike();
        }
    }

    @Override
    public void onMessageReceived(Message message) {

        Log.i("MainActivity", "onMessageReceived: " +message );

        if(message.getMessage().equalsIgnoreCase("LIKE")){
             animateLike();
        }
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
