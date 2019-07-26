package com.applozic.livestreamingdemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.listners.AlLoginHandler;

public class MainActivity extends AppCompatActivity {


    Button launchButton;

    public static String App_ID = "applozic-sample-app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginUser(this);
        String[] mobileArray = {"Android to sahi hai but ","IPhone na ho payega ","WindowsMobile sach me ","Blackberry",
                "WebOS sahi hai but sahi hai but sahi hai but","Ubuntu sahi hai but sahi hai but","Windows7 sahi hai but sahi hai butsahi hai but","Max OS X sahi hai but sahi hai but"};


        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.message_list_textview, mobileArray);

        ListView listView = (ListView) findViewById(R.id.chat_list_view);
        listView.setAdapter(adapter);
        listView.setEnabled(false);
    }


    public void loginUser(Context context) {

        Applozic.init(context, App_ID);

        User user = new User();
        user.setUserId("akLiveStreamingDemo");
        user.setDisplayName( "Live Streaming Demo");
        user.setAuthenticationTypeId(User.AuthenticationType.APPLOZIC.getValue());
        user.setPassword("123456");

        Applozic.connectUser(context, user, new AlLoginHandler() {
            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {

                Toast.makeText(MainActivity.this, "Login SucessFul",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                Toast.makeText(MainActivity.this, "Login Failed",Toast.LENGTH_LONG).show();
            }
        });

    }


}
