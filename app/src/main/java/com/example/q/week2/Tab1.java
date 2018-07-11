package com.example.q.week2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Contact.Person;
import firstTabHelper.*;

public class Tab1 extends AppCompatActivity implements View.OnClickListener {

    private List<Person> result;
    private List<Person> fav_res;
    private List<Person> not_res;
    private ArrayList<Person> contact;
    private ArrayList<Person> fav_con;
    private ArrayList<Person> not_con;
    private ListViewAdapter ca = null;
    private ListViewAdapter cb = null;
    private AlertDialog.Builder builder;
    private URL url;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton more, upload, download, add;
    private EditText searchText;
    private ListView lv;
    private ListView fav_lv;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab1);

        Log.d("REST GET777", "dd");
        searchText = findViewById(R.id.searchText);
        lv = findViewById(R.id.lv);
        fav_lv = findViewById(R.id.lv_favorite);

        result = new ArrayList<Person>();
        getContact getcontact = new getContact();
        result = getcontact.collect(this);

        contact = new ArrayList<Person>();
        contact.addAll(result);

        fav_res = new ArrayList<Person>();
        not_res = new ArrayList<Person>();

        for (int i = 0; i < contact.size(); i++) {
            if (contact.get(i).getFavorite()) {
                fav_res.add(contact.get(i));
            } else {
                not_res.add(contact.get(i));
            }
        }

        fav_con = new ArrayList<Person>();
        fav_con.addAll(fav_res);
        not_con = new ArrayList<Person>();
        not_con.addAll(not_res);

        ca = new ListViewAdapter(fav_res, this);
        fav_lv.setAdapter(ca);

        cb = new ListViewAdapter(not_res, this);
        lv.setAdapter(cb);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = searchText.getText().toString();
                search(text);
            }
        });

        //click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Tab1.this, detailContact.class);
                intent.putExtra("contact", not_res.get(i));
                startActivityForResult(intent, 0);
            }
        });

        //click
        fav_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Tab1.this, detailContact.class);
                intent.putExtra("contact", fav_res.get(i));
                startActivityForResult(intent, 1);
            }
        });


        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        more = (FloatingActionButton) findViewById(R.id.more);
        upload = (FloatingActionButton) findViewById(R.id.upload);
        download = (FloatingActionButton) findViewById(R.id.download);
        add = (FloatingActionButton) findViewById(R.id.add);

        more.setOnClickListener(this);
        upload.setOnClickListener(this);
        download.setOnClickListener(this);
        add.setOnClickListener(this);


        mSwipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lv = findViewById(R.id.lv);
                fav_lv = findViewById(R.id.lv_favorite);

                result = new ArrayList<Person>();
                getContact getcontact = new getContact();
                result = getcontact.collect(Tab1.this);

                contact = new ArrayList<Person>();
                contact.addAll(result);

                fav_res = new ArrayList<Person>();
                not_res = new ArrayList<Person>();

                for (int i = 0; i < contact.size(); i++) {
                    if (contact.get(i).getFavorite()) {
                        fav_res.add(contact.get(i));
                    } else {
                        not_res.add(contact.get(i));
                    }
                }

                fav_con = new ArrayList<Person>();
                fav_con.addAll(fav_res);
                not_con = new ArrayList<Person>();
                not_con.addAll(not_res);

                ca = new ListViewAdapter(fav_res, Tab1.this);
                fav_lv.setAdapter(ca);

                cb = new ListViewAdapter(not_res, Tab1.this);
                lv.setAdapter(cb);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.more:
                anim();
                break;
            case R.id.upload:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("Contact List Backup")        // 제목 설정
                        .setMessage("Are you sure to upload contact list?")        // 메세지 설정
                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.d("connecttest", "1");
                                DeletePerson delete = new DeletePerson();
                                delete.deletePerson();
                                for(int i=0; i<contact.size(); i++) {
                                    PostPerson post = new PostPerson();
                                    post.postPerson(contact.get(i));
                                }
                                Toast.makeText(getApplicationContext(), "Successfully uploaded.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                builder.show();
                break;
            case R.id.download:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("Contact List Recover")        // 제목 설정
                        .setMessage("Are you sure to download contact list?")        // 메세지 설정
                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ArrayList<Person> downCont = new ArrayList<Person>();
                                GetPerson get = new GetPerson();
                                Log.d("REST GET999", "");
                                try {
                                    Log.d("REST GET999", "");
                                    for(int i=0; i<contact.size(); i++){
                                        contactHelper.deleteContact(contact.get(i), Tab1.this);
                                    }
                                    downCont = get.getPerson();
                                    for(int i=0; i<downCont.size(); i++){
                                        contactHelper.addContact(downCont.get(i), Tab1.this);
                                    }
                                    Log.d("REST14", downCont.get(0).getName());
                                    Toast.makeText(getApplicationContext(), "Successfully downloaded.", Toast.LENGTH_SHORT).show();
                                    //TODO: refresh it

                                } catch (Exception e){
                                    e.printStackTrace();
                                    Log.d("REST567", e.getMessage());
                                    Toast.makeText(getApplicationContext(), "Download failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                builder.show();
                break;
            case R.id.add:
                Intent intent = new Intent(Tab1.this, ContactAdd.class);
                startActivityForResult(intent, 0);
                break;
        }
    }

    public void anim() {
        if (isFabOpen) {
            more.setImageResource(R.drawable.more);
            upload.startAnimation(fab_close);
            download.startAnimation(fab_close);
            add.startAnimation(fab_close);
            upload.setClickable(false);
            download.setClickable(false);
            add.setClickable(false);
            isFabOpen = false;
        } else {
            more.setImageResource(R.drawable.close);
            upload.startAnimation(fab_open);
            download.startAnimation(fab_open);
            add.startAnimation(fab_open);
            upload.setClickable(true);
            download.setClickable(true);
            add.setClickable(true);
            isFabOpen = true;
        }
    }


    public void search(String text) {
        Log.d("recieve", text);
        fav_res.clear();
        not_res.clear();

        if (text.length() == 0) {
            Log.d("route1", "route1");
            fav_res.addAll(fav_con);
            not_res.addAll(not_con);
        } else {
            for (int i = 0; i < contact.size(); i++) {
                Person person = contact.get(i);
                if ((person.getName().toLowerCase().contains(text)) || soundSearcher.matchString(person.getName(), text) || (person.getPhone().contains(text)) || (person.getEmail().contains(text))) {
                    if (person.getFavorite()) {
                        fav_res.add(person);
                    } else {
                        not_res.add(person);
                    }
                }
            }
        }

        ca.notifyDataSetChanged();
        cb.notifyDataSetChanged();
    }
}