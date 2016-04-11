package com.forthcode.feedbackapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.forthcode.feedbackapp.Database.MyDb;
import com.forthcode.feedbackapp.R;

public class MainActivity extends AppCompatActivity {

    private EditText mPasswordView, mEmailView;
    private SharedPreferences mySharedpref;
    String userName;
    MyDb db;
    String pwd, uName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mySharedpref = PreferenceManager.getDefaultSharedPreferences(this);
        userName = mySharedpref.getString("uName", "0");
        db=new MyDb(this);
        if(userName.equals("0")){

        }else{
            Intent intent=new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }



        setContentView(R.layout.activity_login);

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uName=mEmailView.getText().toString();
                pwd=mPasswordView.getText().toString();

                if(uName.equals("keyur@dnanetworks.com")||uName.equals("ritesh@dnanetworks.com")||uName.equals("ranjit@dnanetworks.com")||uName.equals("rohit@dnanetworks.com")){
                    if(userName.equals(uName)){
                        db.open();
                        db.deleteUser();
                        db.close();
                    }

                if(pwd.equals("Dna@123")){
                    SharedPreferences.Editor et=mySharedpref.edit();
                    et.putString("uName", uName);
                    et.commit();

                    Intent intent=new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(MainActivity.this, "Please enter a valid password", Toast.LENGTH_LONG).show();
                }

            }else{
                    Toast.makeText(MainActivity.this,"Please enter a valid user name", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}