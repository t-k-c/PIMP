package pi.novobyte.com.pimp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.pedant.SweetAlert.SweetAlertDialog;
import commondata.CookieData;
import commondata.ServerData;
import data.GetRequestManager;

import static commondata.CommonData.COMFORTAA_FONT_PATH;
import static commondata.CookieData.userLoggedIn;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //checks user logged in state
        if(userLoggedIn(this)){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
        //end of condition for user logged in state

        setContentView(R.layout.activity_login);
        TextView tv1 = (TextView) findViewById(R.id.logintext);
        tv1.setTypeface(Typeface.createFromAsset(getAssets(),COMFORTAA_FONT_PATH));
    }

    public void goToSignUpActivity(View v){
        startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
        finish();
    }

    public void login(View v){
        EditText emailText = (EditText) findViewById(R.id.email);
        EditText passwordText = (EditText) findViewById(R.id.password);
        String email = emailText.getText().toString();
        String password =  passwordText.getText().toString();
        if(email.isEmpty()){
            emailText.setError("Field is empty");
        }
        if(password.isEmpty()){
            passwordText.setError("Field is empty");
        }

        if(!password.isEmpty() && !email.isEmpty()){
            final String urlheader = ServerData.LOGIN_LINK;

                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter("email",email);
                builder.appendQueryParameter("password",password);
                final String params = builder.build().toString();
       /*         final String params = "?"+ URLEncoder.encode("email","UTF-8") +"="
                        +URLEncoder.encode(email,"UTF-8")+
                        "&"+URLEncoder.encode("password","UTF-8")+"="+
                        URLEncoder.encode(password,"UTF-8");*/
            SweetAlertDialog sw = new SweetAlertDialog(this);
                    log(urlheader+params,sw);



        }
    }
    public void log(final String url,final  SweetAlertDialog sweetAlertDialog){
//        Toast.makeText(this, thumbnail, Toast.LENGTH_SHORT).show();
        sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Authentifying");
        sweetAlertDialog.setContentText("Please wait...");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String resp = GetRequestManager.getResponse(url);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(LoginActivity.this, resp, Toast.LENGTH_SHORT).show();
                        Log.d("PIMP","Server: "+resp);

                    }
                });
                try{
                    if(Integer.parseInt(resp) > 0 ){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                sweetAlertDialog.setContentText("Authentication verified");
                                sweetAlertDialog.setTitle("Success");
                                sweetAlertDialog.setConfirmButton("Continue", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                        CookieData.setUserId(Integer.parseInt(resp),LoginActivity.this);
                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                        finish();
                                    }
                                });
                                sweetAlertDialog.showCancelButton(false);
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                               switch (Integer.parseInt(resp)){
                                   case -3 :   sweetAlertDialog.setContentText("Account Blocked!"); break;
                                   case -2 :   sweetAlertDialog.setContentText("Unknown email!"); break;
                                   case -1 :   sweetAlertDialog.setContentText("Incorrect pasword!"); break;
                                   default :   sweetAlertDialog.setContentText("Error occurred");
                               }
                                sweetAlertDialog.setTitle("Authentication failed");
                                sweetAlertDialog.setConfirmButton("Retry", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                        log(url,sweetAlertDialog);
                                    }
                                });
                                sweetAlertDialog.setCancelButton("Back", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                });
                            }
                        });
                    }
                }catch(NumberFormatException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            sweetAlertDialog.setContentText("An error occurred");
                            sweetAlertDialog.setTitle("Failure");
                            sweetAlertDialog.setCancelButton("Skip", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplicationContext().startActivity(i);
                                    finish();
                                }
                            });
                            sweetAlertDialog.setConfirmButton("Retry", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            });
                        }
                    });
                }

            }
        }).start();
    }
}
