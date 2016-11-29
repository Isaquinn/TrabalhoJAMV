package com.example.isaquearaujo.trabalhojamv;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cadastro extends AppCompatActivity implements View.OnClickListener {
    private EditText nome, email, senha, data, cor;
    private AutoCompleteTextView sexo;
    private Button Registrar;
    private TextView Entrar;
    private ProgressDialog progress;
    private FirebaseAuth firebaseAuth;
    private Firebase principal;
    private Firebase users;
    public  static  String emailsplit;
    private ImageView jamvc;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Firebase.setAndroidContext(this);
        firebaseAuth = FirebaseAuth.getInstance();
        principal = new Firebase("https://trabalhojamv.firebaseio.com/");
        users = principal.child("users");
        jamvc = (ImageView)findViewById(R.id.jamvc);
        nome = (EditText)findViewById(R.id.nome);
        email = (EditText)findViewById(R.id.email);
        senha = (EditText)findViewById(R.id.passwordcadastro);
        data = (EditText)findViewById(R.id.datadenascimentocadastro);
        data.setInputType(InputType.TYPE_NULL);
        data.requestFocus();
        setDateTimeField();
        sexo = (AutoCompleteTextView) findViewById(R.id.sexocadastro);
        cor = (EditText)findViewById(R.id.corcadastro);
        final String[] COUNTRIES = new String[] {
                "Masculino", "Feminino"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        sexo.setAdapter(adapter);
        Registrar = (Button)findViewById(R.id.Registrarnovousuario);
        Entrar = (TextView)findViewById(R.id.entrar);
        Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validar(email.getText().toString().trim()) == true && !senha.getText().toString().trim().equals(""))
                {
                    if(isOnline() == true) {
                        registerUser();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Não é possível cadastrar, você está sem conexão com a internet", Toast.LENGTH_LONG).show();
                    }
                }
                 if(email.getText().toString().trim().equals("") && senha.getText().toString().trim().equals("") && nome.getText().toString().trim().equals("") && cor.getText().toString().trim().equals("") && sexo.getText().toString().trim().equals("") && data.getText().toString().trim().equals(""))
                {
                    Toast.makeText(Cadastro.this, "Por favor preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(validar(email.getText().toString().trim()) == false && !senha.getText().toString().trim().equals(""))
                {
                    email.setText("");
                    Toast.makeText(Cadastro.this, "Por favor insira um email valido!", Toast.LENGTH_SHORT).show();
                    return;
                }
                 if(!email.getText().toString().trim().equals("") || senha.getText().toString().trim().equals(""))
                {
                    Toast.makeText(Cadastro.this, "Por favor insira a senha!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        Entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Cadastro.this, Login.class);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_left);
                Intent intent2 = new Intent(Cadastro.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Exit me", true);
                startActivity(intent);
                finish();
            }
        });
    }
    private void setDateTimeField() {
        data.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                data.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
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
    public boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo() != null &&
                manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    private  void registerUser()
    {
        final String textoemail = email.getText().toString().trim();
        final String textosenha = senha.getText().toString().trim();
        if(TextUtils.isEmpty(textoemail))
        {
            Toast.makeText(this, "Por favor insira o Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(textosenha))
        {
            Toast.makeText(this, "Por favor insira a senha", Toast.LENGTH_SHORT).show();
            return;
        }
        progress = new ProgressDialog(Cadastro.this,R.style.full_screen_dialog) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.custom_progressdialog);
                getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
            }
        };
        progress.show();
        firebaseAuth.createUserWithEmailAndPassword(textoemail,textosenha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Map<String, String> userData = new HashMap<String, String>();
                    userData.put("Nome", nome.getText().toString().trim());
                    userData.put("Email", email.getText().toString().trim());
                    userData.put("DataDeNascimento", data.getText().toString().trim());
                    userData.put("Sexo", sexo.getText().toString().trim());
                    userData.put("Cor",cor.getText().toString().trim());
                    String nomesalvar = email.getText().toString().trim();
                    nomesalvar.replace('.', ',');
                    emailsplit = nomesalvar.replace('.', ',');
                    users = principal.child("users").child(emailsplit);
                    users.setValue(userData);
                    progress.dismiss();
                    Toast.makeText(Cadastro.this,"Registro realizado com Sucesso", Toast.LENGTH_SHORT).show();
                    jamvc.setImageResource(R.drawable.feliz);
                    email.setText("");
                    senha.setText("");
                    nome.setText("");
                    sexo.setText("");
                    data.setText("");
                    cor.setText("");
                }
                else
                {
                    progress.dismiss();
                    Toast.makeText(Cadastro.this,"Não foi possivel realizar o cadastro", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void onBackPressed()  {
        Intent intent = new Intent(Cadastro.this, Login.class );
        startActivityForResult(intent, 0);
        overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_left);
        Intent intent2 = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Exit me", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.datadenascimentocadastro)
        {
            fromDatePickerDialog.show();
        }

    }
}
