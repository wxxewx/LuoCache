package com.xiaofeiluo.luocachedemo;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaofeiluo.luocache.LuoCache;
import com.xiaofeiluo.luocache.R;
import com.xiaofeiluo.luocache.strategy.SpStrategy;



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
        LuoCache.init(getApplication(), SpStrategy.class);
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
        info.setText(name + "--" + age + "--" + city + "--" + (account != null ? account.getPassWorld() : "null") + "--" + (account != null ? account.getAccount() : "null"));
    }

    private void clearData() {
        UserManager.getInstance().clear();
    }

    private void saveData() {
        String name = this.name.getText().toString();
        String age = this.age.getText().toString();
        String city = this.city.getText().toString();
        String account = this.account.getText().toString();
        String passworld = this.passworld.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(age)) {
            Toast.makeText(this, "请输入年龄", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(city)) {
            Toast.makeText(this, "请输入城市", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(passworld)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        UserManager.getInstance().setName(name);
        UserManager.getInstance().setAge(Integer.parseInt(age));
        UserManager.getInstance().setCity(city);
        Account accountData = new Account();
        accountData.setAccount(account);
        accountData.setPassWorld(passworld);
        UserManager.getInstance().setAccount(accountData);
    }
}
