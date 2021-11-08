package com.penpendev.pair;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import static com.penpendev.pair.MainActivity.BattleFragment;
import static com.penpendev.pair.MainActivity.MemberManagementFragment;


public class SettingsFragment extends PreferenceFragmentCompat {

    int currentcoatCount;
    int nextcoatCount;
    SharedPreferences sp;
    ListPreference countingPreference;

    //リスナー解除
    @Override
    public void onPause() {
        super.onPause();
        sp.unregisterOnSharedPreferenceChangeListener(listener);
    }

    //リスナーセット
    @Override
    public void onResume() {
        super.onResume();
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
      /*
        PreferenceCategory category = new PreferenceCategory(getPreferenceScreen().getContext());
                category.setTitle("test");
        getPreferenceScreen().addPreference(category);


        PreferenceScreen restore = getPreferenceManager().createPreferenceScreen(getPreferenceScreen().getContext());
        restore.setTitle("リストア");
        restore.setOnPreferenceClickListener(asd);
        category.addPreference(restore); //categoryにPreferenceScreenを追加*/

        countingPreference = (ListPreference) findPreference("battle_method");
        countingPreference.setSummaryProvider(new Preference.SummaryProvider<ListPreference>() {
            @Override
            public CharSequence provideSummary(ListPreference preference) {
                String text = (String)preference.getValue();
                if (TextUtils.isEmpty(text)){
                    return "Not set";
                }else{
                    //return text.substring(0,text.indexOf("\n"));
                    if(text.equals("1")){
                        return getString(R.string.battle_method1).substring(0,getString(R.string.battle_method1).indexOf("\n"));
                    }else if(text.equals("2")){
                        return getString(R.string.battle_method2).substring(0,getString(R.string.battle_method2).indexOf("\n"));
                    }else if(text.equals("3")) {
                        return getString(R.string.battle_method3).substring(0,getString(R.string.battle_method3).indexOf("\n"));
                    }else{
                        return "test";
                    }
                }
            }
        });



      //リスナーのセット
        findPreference("reset_entrytimes").setOnPreferenceClickListener(reset);
        findPreference("reset_battletime").setOnPreferenceClickListener(reset);

        currentcoatCount = Integer.parseInt(sp.getString("coat_numbers_key", "10"));

        for (int i = 0; i < currentcoatCount; i++) {
            ListPreference lp=  new ListPreference(getPreferenceScreen().getContext());
            lp.setEntries(R.array.coat_people);
            lp.setEntryValues(R.array.coat_people);
            lp.setKey("coat" + i);
            lp.setTitle("コート" + (i + 1) + "の人数");
            lp.setValue("4");
            lp.setDefaultValue("4");
            lp.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
            //getPreferenceScreen().addPreference(lp);
            ((PreferenceCategory)getPreferenceScreen().findPreference("battlesetting")).addPreference(lp);
        }

        //アクションバーの設定
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionbar.setCustomView(R.layout.titlebar);
        TextView textview = actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_title);
        textview.setText("設定");
        TextView textview1 = actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_subtitle);
        textview1.setVisibility(View.GONE);
        actionbar.setDisplayShowCustomEnabled(true);
    }


    //リスナー
    Preference.OnPreferenceClickListener reset=new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {

            CustomDialogFlagment dialog = new CustomDialogFlagment();
            Bundle bd=new Bundle();
            switch (preference.getKey()){
                case "reset_battletime":
                    bd.putString("reset_witch",getResources().getString(R.string.reset_battletime));
                    break;
                case "reset_entrytimes":
                    bd.putString("reset_witch",getResources().getString(R.string.reset_entrytime));
                    break;
            }
            dialog.setArguments(bd);
            // 表示  getFagmentManager()は固定、sampleは識別タグ
            dialog.show(getFragmentManager(), "sample");
            return false;
        }
    };

    //なにかの設定が変更されたら呼び出されるリスナー
    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            switch (key) {
                case "coat_numbers_key"://コート数が変更されたら

                    nextcoatCount=Integer.parseInt(prefs.getString("coat_numbers_key", "10"));

                    if (nextcoatCount > currentcoatCount) {//コート数が増えた場合→追加する
                        for (int i = currentcoatCount; i < nextcoatCount; i++) {
                            ListPreference lp = new ListPreference(getPreferenceScreen().getContext());
                            lp.setEntries(R.array.coat_people);
                            lp.setEntryValues(R.array.coat_people);
                            lp.setKey("coat"+i);
                            lp.setTitle("コート" + (i + 1) + "の人数");
                            lp.setDefaultValue("4");
                            lp.setValue("4");

                            lp.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
                            //getPreferenceScreen().addPreference(lp);
                            ((PreferenceCategory)getPreferenceScreen().findPreference("battlesetting")).addPreference(lp);
                        }
                        currentcoatCount=nextcoatCount;
                    } else if (nextcoatCount< currentcoatCount) {//コート数が減った場合→削除する
                        for (int i = currentcoatCount; i > nextcoatCount; i--) {
                            //getPreferenceScreen().removePreference(getPreferenceScreen().findPreference("coat"+(i-1)));
                            ((PreferenceCategory)getPreferenceScreen().findPreference("battlesetting")).removePreference(getPreferenceScreen().findPreference("coat"+(i-1)));
                        }
                        currentcoatCount=nextcoatCount;
                    }

                    break;
            }

        }
    };

    static public class CustomDialogFlagment extends DialogFragment {

        // ダイアログが生成された時に呼ばれるメソッド ※必須
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {


            // ダイアログ生成  AlertDialogのBuilderクラスを指定してインスタンス化します
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

            // 表示する文章設定
            dialogBuilder.setMessage(getArguments().getString("reset_witch"));

            // OKボタン作成
            dialogBuilder.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(getArguments().getString("reset_witch")==getResources().getString(R.string.reset_entrytime)){
                        MemberManagementFragment.entrytimeReset();
                    }else{
                        BattleFragment.resetBattleTime();

                    }

                }
            });

            // NGボタン作成
            dialogBuilder.setNegativeButton(R.string.no,new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            // dialogBulderを返す
            return dialogBuilder.create();
        }
    }
}
