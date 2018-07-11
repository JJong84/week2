package com.example.q.week2;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

public class Tab2 extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab2);
        PageTwo firstFragment = new PageTwo();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.tab2_frag, firstFragment).commit();
    }
}
