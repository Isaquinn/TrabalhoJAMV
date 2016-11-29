package com.example.isaquearaujo.trabalhojamv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.fitness.data.Application;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    public static EditText email, senha;
    private Button Logar;
    private TextView Cadastrar;
    private ProgressDialog progress;
    private FirebaseAuth firebaseAuth;
    private Firebase principal;
    private ImageView jamvl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if( getIntent().getBooleanExtra("Exit me", false)){
            finish();
            return; // add this to prevent from doing unnecessary stuffs
        }
        Firebase.setAndroidContext(this);
        firebaseAuth = FirebaseAuth.getInstance();
        principal = new Firebase("https://trabalhojamv.firebaseio.com/");
        jamvl = (ImageView)findViewById(R.id.jamvl);
        email = (EditText)findViewById(R.id.login);
        senha = (EditText)findViewById(R.id.password);
        Logar = (Button)findViewById(R.id.LogIn);
        Cadastrar = (TextView)findViewById(R.id.naoexisteconta);
        Logar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validar(email.getText().toString().trim()) == true && !senha.getText().toString().trim().equals(""))
                {
                    if(isOnline() == true) {
                        LoginUser();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Não é possível entrar, você está sem conexão com a internet", Toast.LENGTH_LONG).show();
                    }
                }
                else if(email.getText().toString().trim().equals("") && senha.getText().toString().trim().equals(""))
                {
                    Toast.makeText(Login.this, "Por favor preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(validar(email.getText().toString().trim()) == false && !senha.getText().toString().trim().equals(""))
                {
                    email.setText("");
                    Toast.makeText(Login.this, "Por favor insira um email valido!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!email.getText().toString().trim().equals("") || senha.getText().toString().trim().equals(""))
                {
                    Toast.makeText(Login.this, "Por favor insira a senha!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        Cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Cadastro.class);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_right);
            }
        });
    }
    public static boolean validar(String email)
    {
        boolean isEmailIdValid = false;
        if (email != null && email.length() > 0) {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                isEmailIdValid = true;
            }
        }
        return isEmailIdValid;
    }
    public boolean isOnline()
    {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo()
                .isConnectedOrConnecting();
    }
    private  void LoginUser()
    {
        String textoemail = email.getText().toString().trim();
        String textosenha = senha.getText().toString().trim();
        if(TextUtils.isEmpty(textoemail))
        {
            Toast.makeText(this, "Please enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(textosenha))
        {
            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();
            return;
        }
        progress = new ProgressDialog(Login.this,R.style.full_screen_dialog) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.custom_progressdialog2);
                getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
            }
        };
        progress.show();
        firebaseAuth.signInWithEmailAndPassword(textoemail,textosenha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    /*Toast.makeText(NewLogin.this,"Login Succesefuly", Toast.LENGTH_SHORT).show();
                    Intent iinent= new Intent(NewLogin.this,CustomAvatar.class);
                    startActivity(iinent);*/
                    progress.dismiss();
                    Toast.makeText(Login.this,"Login efetuado com Sucesso", Toast.LENGTH_SHORT).show();
                    jamvl.setImageResource(R.drawable.feliz);
                    Intent intent = new Intent(Login.this, Informacoes.class);
                    startActivityForResult(intent, 0);

                }
                else
                {
                    Toast.makeText(Login.this,"Não foi possivel efetuar login", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }
        });

    }
    public void onBackPressed()  {
        if( getIntent().getBooleanExtra("Exit me", true)){
            System.exit(0);
            return; // add this to prevent from doing unnecessary stuffs
        }
    }
}
