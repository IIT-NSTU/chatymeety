package com.arnab.chatymeety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private MyPagerAdapter mpagerAdapter;
    private DatabaseReference dataRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mtoolbar=findViewById(R.id.main_toolbar);
        mTabLayout=findViewById(R.id.tab);
        mViewPager=findViewById(R.id.viewpager);
        mpagerAdapter=new MyPagerAdapter(getSupportFragmentManager(),BEHAVIOR_SET_USER_VISIBLE_HINT);


        mViewPager.setAdapter(mpagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);


        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("ChatyMeety");

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            sendToAuth();
        }
        else{
            dataRef= FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid());
            dataRef.child("online").setValue(true);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(mAuth.getCurrentUser()!=null)dataRef.child("online").setValue(false);
    }

    void sendToAuth(){
        Intent sendToAuth=new Intent(getApplicationContext(),AuthActivity.class);
        startActivity(sendToAuth);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId()==R.id.main_menu_logout){
            mAuth.signOut();
            sendToAuth();
        }
        if (item.getItemId()==R.id.main_menu_account_setting){
            startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
        }

        if (item.getItemId()==R.id.main_menu_alluser){
            startActivity(new Intent(getApplicationContext(),AllUserActivity.class));
        }

        return true;
    }
}
