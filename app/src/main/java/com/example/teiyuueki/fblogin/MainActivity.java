package com.example.teiyuueki.fblogin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;


public class MainActivity extends FragmentActivity {


    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Button postButton;
    private boolean login_flag;
    private EditText postText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //初始化FacebookSdk，記得要放第一行，不然setContentView會出錯

        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //宣告callback Manager

        callbackManager = CallbackManager.Factory.create();

        //找到login button

        info = (TextView) findViewById(R.id.info);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        postButton = (Button) findViewById(R.id.post_button);
        postText = (EditText) findViewById(R.id.post_text);

        //幫loginButton增加callback function

        //這邊為了方便 直接寫成inner class

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                info.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                );
                Log.d("Token", loginResult.getAccessToken().getToken());
                login_flag = true;
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
                login_flag = false;
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed." + e);
                login_flag = false;
            }
        });

        if (login_flag == true || false) {
            LoginManager.getInstance().logInWithPublishPermissions((Activity) this, Arrays.asList("publish_actions"));
        }

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // クリック時の処理
                if (postText.getText().length() != 0) {
                    Log.e("postText", String.valueOf(postText.getText()));
                    Bundle params = new Bundle();
                    params.putString("message", String.valueOf(postText.getText()));
                /* make the API call */
                    new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/me/feed",
                            params,
                            HttpMethod.POST,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                 /* handle the result */
                                    Log.i("fb", "Feeds :" + response.getJSONObject());
                                    Toast.makeText(
                                            getApplicationContext(),
                                            response.getJSONObject()
                                                    + "",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                    ).executeAsync();
                }else{
                    Log.e("Alert", "Alert is called");
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    // アラートダイアログのタイトルを設定します
                    alertDialogBuilder.setTitle("テキスト入力");
                    // アラートダイアログのメッセージを設定します
                    alertDialogBuilder.setMessage("テキストを入力してください");

                    alertDialogBuilder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }

                Log.e("Token", String.valueOf(AccessToken.getCurrentAccessToken()));
            }/*else{
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                    // アラートダイアログのタイトルを設定します
                    alertDialogBuilder.setTitle("ログイン");
                    // アラートダイアログのメッセージを設定します
                    alertDialogBuilder.setMessage("ログインしてください");

                    alertDialogBuilder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                }
            }*/
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
