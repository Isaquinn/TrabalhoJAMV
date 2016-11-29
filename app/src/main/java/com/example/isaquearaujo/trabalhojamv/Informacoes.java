package com.example.isaquearaujo.trabalhojamv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.drive.query.internal.LogicalFilter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

public class Informacoes extends AppCompatActivity {
    private TextView nomeinfo, emailinfo, datainfo, sexoinfo, corinfo;
    private FirebaseAuth firebaseAuth;
    private Firebase principal;
    private Firebase users;
    public  static String emailsplitado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacoes);
        nomeinfo = (TextView) findViewById(R.id.nomeinfo);
        nomeinfo.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        emailinfo = (TextView) findViewById(R.id.emailinfo);
        emailinfo.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        datainfo = (TextView) findViewById(R.id.datainfo);
        datainfo.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        sexoinfo = (TextView) findViewById(R.id.sexoinfo);
        sexoinfo.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        corinfo = (TextView) findViewById(R.id.corinfo);
        corinfo.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        firebaseAuth = FirebaseAuth.getInstance();
        principal = new Firebase("https://trabalhojamv.firebaseio.com/");
        users = principal.child("users");
        String emailsplit = Login.email.getText().toString().trim();
        emailsplit.replace(".", ",");
        emailsplitado = emailsplit.replace("." , ",");
        users = principal.child("users").child(emailsplitado);
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,String> mapa = dataSnapshot.getValue(Map.class);
                String nome = mapa.get("Nome");
                String email = mapa.get("Email");
                String data = mapa.get("DataDeNascimento");
                String sexo = mapa.get("Sexo");
                String cor = mapa.get("Cor");
                nomeinfo.setText("Nome:"+ nome);
                emailinfo.setText("E-mail:" + email);
                datainfo.setText("Data:"+ data);
                sexoinfo.setText("Sexo:" + sexo);
                corinfo.setText("Cor Preferida:"+ cor);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void onBackPressed()  {
        Intent intent = new Intent(Informacoes.this, Login.class );
        startActivityForResult(intent, 0);
        overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_left);
        Intent intent2 = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Exit me", true);
        startActivity(intent);
        finish();
    }
}
