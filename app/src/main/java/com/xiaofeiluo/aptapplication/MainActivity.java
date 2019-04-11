package com.xiaofeiluo.aptapplication;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaofeiluo.luocache.LuoCache;


public class MainActivity extends AppCompatActivity {

    private EditText account;
    private EditText name;
    private EditText age;
    private EditText city;
    private EditText passworld;
    private Button save;
    private Button get;
    private Button clear;
    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LuoCache.init(getApplication());
        account = findViewById(R.id.account);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        city = findViewById(R.id.city);
        passworld = findViewById(R.id.passworld);
        save = findViewById(R.id.save);
        get = findViewById(R.id.get);
        clear = findViewById(R.id.clear);
        info = findViewById(R.id.info);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
            }
        });
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getData() {
        String name = UserManager.getInstance().getName();
        Account account = UserManager.getInstance().getAccount();
        int age = UserManager.getInstance().getAge();
        String city = UserManager.getInstance().getCity();
        info.setText(name+"--"+age+"--"+city+"--"+account.getPassWorld()+"--"+account.getAccount());
    }

    private void clearData() {
        UserManager.getInstance().removeName();
        UserManager.getInstance().removeAge();
        UserManager.getInstance().removeAccount();
        UserManager.getInstance().removeName();
        UserManager.getInstance().removeCity();
    }

    private void saveData() {
        UserManager.getInstance().setName(name.getText().toString());
        UserManager.getInstance().setAge(Integer.parseInt(age.getText().toString()));
        UserManager.getInstance().setCity(city.getText().toString());
        Account account = new Account();
        account.setAccount(this.account.getText().toString());
        account.setPassWorld(passworld.getText().toString());
        UserManager.getInstance().setAccount(account);
    }
}
