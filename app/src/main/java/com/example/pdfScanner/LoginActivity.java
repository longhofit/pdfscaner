package com.example.pdfScanner;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout textInputEmail,textInputPass;
    EditText email, password;
    Builder_Api_Retrofit builderApiRetrofit;
    UserInterface userInterface;
    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        textInputEmail=findViewById(R.id.textInputEmail);
        textInputPass=findViewById(R.id.textInputPassword);
        email=findViewById(R.id.editTextEmail);
        password=findViewById(R.id.editTextPassword);
        login=findViewById(R.id.cirLoginButton);
        userInterface= Builder_Api_Retrofit.getServiceUserInterface();
        //login listener
        findViewById(R.id.cirLoginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validInput())
                {
                    String inputEmail=email.getText().toString();
                    String inputPassword=password.getText().toString();
                    if(inputEmail.trim().contentEquals("test@gmail.com")&&inputPassword.trim().contentEquals("1"))
                    {
                        login.setVisibility(View.GONE);
                        goLogin();
                    }
                    else {
                        login.setVisibility(View.VISIBLE);
                        new  Builder_Api_Retrofit().showErrorInActivity(LoginActivity.this,"Login fail ");
                    }
                }
            }
        });

    }

    private boolean validInput ()
    {
        String s_email=email.getText().toString();
        String s_password=password.getText().toString();
        if (s_email.isEmpty()|s_password.isEmpty())
        {
            if(s_email.isEmpty())
            {
                textInputEmail.setError("Field can't be empty");
                textInputPass.setError(null);
            }
            else if(s_password.isEmpty())
            {
                textInputPass.setError("Field can't be empty");
                textInputEmail.setError(null);
            }
            else
            {
                textInputEmail.setError("Field can't be empty");
                textInputPass.setError("Field can't be empty");
            }
            return false;
        }
        else {
            textInputPass.setError(null);
            textInputEmail.setError(null);
            return true;
        }
    }


    void goLogin ()
    {
        /*Call<reponse_obj> call=userInterface.login(email.getText().toString(),password.getText().toString());
        call.enqueue(new Callback<reponse_obj>() {
            @Override
            public void onResponse(Call<reponse_obj> call, Response<reponse_obj> response) {
                if(response.isSuccessful())
                {
                        Toast.makeText(LoginActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    login.setVisibility(View.VISIBLE);
                    if(response.body().getSuccess().contains("1"))
                    {
                            new  Builder_Api_Retrofit().showSuccessInActivityNotClose(LoginActivity.this,"Login succesfull "+response.body().getName());
                    }
                    else
                    {
                        login.setVisibility(View.VISIBLE);
                        new  Builder_Api_Retrofit().showErrorInActivity(LoginActivity.this,"Login fail ");
                    }
                }
            }

            @Override
            public void onFailure(Call<reponse_obj> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                login.setVisibility(View.VISIBLE);
            }
        });*/
        new  Builder_Api_Retrofit().showSuccessInActivityNotClose(LoginActivity.this,"Login succesfull ");
    }
    public void onLoginClick(View View){
        startActivity(new Intent(this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }
}
