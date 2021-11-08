package com.penpendev.pair;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.jar.Attributes;


public class MainActivity extends AppCompatActivity {
    public static String PACKAGE_NAME;//パッケージの名前はアクティビティでしか取得できないから？
    private static Context mContext;
    //フラグメントの定義
    public static MemberManagement MemberManagementFragment;
    public static EntryMember EntryMemberFragment;
    public static Battle BattleFragment;
    ScaleGestureDetector scaleGesture;//ピンチアウト、ピンチインを検出するためのオブジェクトの定義
    BottomNavigationView navView;
    SharedPreferences sp;//設定を読み込むためのオブジェクトの定義


    /**
     * ピンチイン/アウトのListener
     */
    private ScaleGestureDetector.SimpleOnScaleGestureListener scaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        // イベントの開始（２点タッチされたタイミングで発生）
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return super.onScaleBegin(detector);

        }

        // イベントの開始（指を離すと発生）
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            //ボトムバーの非表示・表示を設定によって切り替える（ピンチイン）
            if (sp.getBoolean("bottommenu_display_key"
                    , getResources().getBoolean(R.bool.bottommenu_display_default))) {
                if (detector.getScaleFactor() < 1) {
                    if (navView.getVisibility() == View.GONE) {
                        navView.setVisibility(View.VISIBLE);
                    } else {
                        navView.setVisibility(View.GONE);
                    }
                }
            }else{//offになっている場合でも表示だけはさせる
                navView.setVisibility(View.VISIBLE);
            }

            //ツールバーの非表示・表示を設定によって切り替える（ピンチアウト）
            if (sp.getBoolean("toolbar_display_key"
                    , getResources().getBoolean(R.bool.toolbar_display_default))) {
                if (detector.getScaleFactor() > 1) {
                    if (getSupportActionBar().isShowing()) {
                        getSupportActionBar().hide();
                    } else {
                        getSupportActionBar().show();
                    }
                }
            }else{//offになっている場合でも表示だけはさせる
                getSupportActionBar().show();
            }
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return super.onScale(detector);
        }
    };


    //ボトムナビゲーションの設定
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.title_member:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, MemberManagementFragment)
                            .commit();
                    return true;
                case R.id.title_participant:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, EntryMemberFragment)
                            .commit();
                    return true;
                case R.id.title_match:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, BattleFragment)
                            .commit();
                    return true;
                case R.id.title_setting:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, new SettingsFragment())
                            .commit();
                    return true;
            }
            return false;
        }

    };

    public static Context getContext(){
        return mContext;
    }
    //アプリが停止しそうになったらデータをすべて保存しておく
    @Override
    protected void onPause() {
        super.onPause();
        Gson gson = new Gson();
        String json;

        json=gson.toJson(MemberManagementFragment.memberList);
        sp.edit().putString("memberlist",json).commit();

        json=gson.toJson(MemberManagement.MemberList.pair);
        sp.edit().putString("pair",json).commit();

        sp.edit().putInt("idyou",MemberManagement.MemberList.idyou).commit();

        json=gson.toJson(BattleFragment.breakTime);
        sp.edit().putString("breaktime",json).commit();

        json=gson.toJson(BattleFragment.battleTime);
        sp.edit().putString("battletime",json).commit();

    }

    //タップされたら呼び出される（onTouchEventはActivity自体のタップイベント）
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        scaleGesture.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        setContentView(R.layout.activity_main);
        PACKAGE_NAME = getApplicationContext().getPackageName();

        // ScaleGestureDetectorを生成
        scaleGesture = new ScaleGestureDetector(this, scaleGestureListener);

        //設定を読み込むためのオブジェクト
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        String memberlist,breaktime,battletime,pair;
        int idyou;

        memberlist=sp.getString("memberlist", null);
        breaktime=sp.getString("breaktime", null);
        battletime=sp.getString("battletime", null);
        pair=sp.getString("pair", null);
        idyou=sp.getInt("idyou",-1);

        MemberManagementFragment= MemberManagement.newInstance(memberlist,pair,idyou);
        EntryMemberFragment = EntryMember.newInstance();
        BattleFragment = Battle.newInstance(breaktime,battletime);

//        viewPager = (ViewPager) findViewById(R.id.viewpager);
//        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(viewPagerAdapter);

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

/*        String url = "http://www.google.com";
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        // Verify that the intent will resolve to an activity
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Here we use an intent without a Chooser unlike the next example
            startActivity(intent);
        }*/


        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, MemberManagementFragment)
                    .commit();
        }


    }
}





