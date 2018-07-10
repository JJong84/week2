package com.example.q.week2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import Contact.Person;

public class ContactAdd extends Activity {

    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        LinearLayout lil = findViewById(R.id.lil);

        lil.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                hideKeyboard();
            }
        });
    }

    //확인 버튼 클릭
    public void mOnOk(View v) {
        EditText addName = findViewById(R.id.addName);
        EditText addPhone = findViewById(R.id.addPhone);
        EditText addEmail = findViewById(R.id.addEmail);
        EditText addad = findViewById(R.id.addad);

        if(addName.getText() == null){
            Toast.makeText(getApplicationContext(), "Please fill the name.", Toast.LENGTH_SHORT).show();
            return;
        }

        Person add = new Person();

        add.setName(addName.getText().toString());
        add.setPhone(addPhone.getText().toString());
        add.setEmail(addEmail.getText().toString());
        add.setAddress(addad.getText().toString());

        hideKeyboard();

        firstTabHelper.contactHelper.addContact(add, this);
        setResult(0);
        finish();
    }

    public void mOnCancel(View v){
        hideKeyboard();
        Toast.makeText(getApplicationContext(), "Cancelled.", Toast.LENGTH_SHORT).show();
        setResult(0);
        finish();
    }

    private void hideKeyboard() {
        EditText addName = findViewById(R.id.addName);
        EditText addPhone = findViewById(R.id.addPhone);
        EditText addEmail = findViewById(R.id.addEmail);
        EditText addad = findViewById(R.id.addad);

        imm.hideSoftInputFromWindow(addName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(addPhone.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(addEmail.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(addad.getWindowToken(), 0);
    }
}
