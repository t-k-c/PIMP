package pi.novobyte.com.pimp;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;
import data.GetRequestManager;

import static commondata.CommonData.COMFORTAA_FONT_PATH;
import static commondata.ServerData.SIGNUP_LINK;

public class SignUpActivity extends AppCompatActivity {
EditText usernametv,nametv,passwordtv,emailtv;
CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        TextView tv1 = (TextView) findViewById(R.id.signuptext);
        tv1.setTypeface(Typeface.createFromAsset(getAssets(),COMFORTAA_FONT_PATH));
        nametv =  (EditText) findViewById(R.id.name);
        usernametv =  (EditText) findViewById(R.id.username);
        emailtv =  (EditText) findViewById(R.id.email);
        passwordtv =  (EditText) findViewById(R.id.password);
        checkBox = (CheckBox)findViewById(R.id.accountype);
    }
    public void goToLoginActivity(View v){
        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
        finish();
    }
    public void signUp(View v){
        String name  = nametv.getText().toString();
        String email  = emailtv.getText().toString();
        String username  = usernametv.getText().toString();
        String password  = passwordtv.getText().toString();
        boolean isPartner = checkBox.isChecked();
        Log.i("pimp","IS PARTNER"+isPartner+"");
        boolean canContinue = true;
        if(name.isEmpty()){
            nametv.setError("The name should not be empty");
            canContinue = false;
        }
        if(email.isEmpty()){
            emailtv.setError("The email should not be empty");
            canContinue = false;
        } if(username.isEmpty()){
            usernametv.setError("The username should not be empty");
            canContinue = false;
        } if(password.isEmpty()){
            passwordtv.setError("The password should not be empty");
            canContinue = false;
        }

        if(canContinue){
            Uri.Builder builder =  new Uri.Builder();
            builder.appendQueryParameter("email",email);
            builder.appendQueryParameter("username",username);
            builder.appendQueryParameter("name",name);
            builder.appendQueryParameter("password",password);
            builder.appendQueryParameter("type",isPartner ? "1" : "0" );
            String params =  builder.build().toString();
            final String fullpath = SIGNUP_LINK+params;
            Log.i("pimp",fullpath);
            final SweetAlertDialog sweetAlertDialog  = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.setTitle("Signing Up");
            sweetAlertDialog.setContentText("Please wait while we create an account for you");
            sweetAlertDialog.showCancelButton(false);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String resp = GetRequestManager.getResponse(fullpath);
                    Log.i("pimp",resp);

                        // the resp is the user id in case of positive signup or a negative attribute
                        try{ //errors from the server will not always be integers :)
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int response = Integer.parseInt(resp);
                                    if(response<0){
                                        sweetAlertDialog.setCancelable(true);
                                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                        sweetAlertDialog.setTitle("Error occurred..");
                                        switch (response){
                                            case -2: sweetAlertDialog.setContentText("Sorry, that didnt work! Email already in use"); break;
                                            default: sweetAlertDialog.setContentText("Sorry, that didnt work! The problem is not from yous");
                                        }
                                        sweetAlertDialog.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.cancel();
                                            }
                                        });
                                    }else{
                                        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                        sweetAlertDialog.setTitle("Success!");
                                        sweetAlertDialog.setContentText("Welcome to pimp! Glad to have you onboard :) ");
                                        sweetAlertDialog.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                               startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                                               finish();
                                            }
                                        });
                                    }
                                }
                            });
                        }catch(NumberFormatException e){
                            if(GetRequestManager.checkIfErrorFromMessage(resp)){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SignUpActivity.this, "An error occurred:"+GetRequestManager.decodeErrorMessage(resp), Toast.LENGTH_LONG).show();

                                    }
                                });

                            }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignUpActivity.this, "An error occurred: Unreachable response from server", Toast.LENGTH_LONG).show();

                                }
                            });
                            }
                        }

                }
            }).start();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
//        Blurry.with(this).radius(5).sampling(2).animate(1500).onto((ViewGroup) findViewById(R.id.tt));
        super.onWindowFocusChanged(hasFocus);
    }
}
