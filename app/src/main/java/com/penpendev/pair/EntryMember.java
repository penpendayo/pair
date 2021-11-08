package com.penpendev.pair;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.penpendev.pair.MainActivity.MemberManagementFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class EntryMember extends Fragment {
    SharedPreferences sp;//設定から読み込むための変数
    RecyclerView mRecyclerView;
    ArrayList<MemberManagement.MemberList> entryList;
    SimpleAdapter mAdapter;
    SectionedGridRecyclerViewAdapter mSectionedAdapter;
    ArrayList<Integer> pairflg=new ArrayList<Integer>();//この値によってペアを組むかどうか決まる
    int haitapairflg = -100;//この値によって排他ペアを組むかどうか決まる

    //コンストラクタ
    public EntryMember() {
    }

    //キャンセルボタンが押されたとき
    View.OnClickListener button_cancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            kyoutuu();
        }
    };

    //ペアを登録ボタンが押されたとき
    View.OnClickListener button_regi = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MemberManagement.MemberList.pair.add(pairflg.toArray(new Integer[pairflg.size()]));
            kyoutuu();
        }
    };
    public void kyoutuu(){
        for (Integer a : pairflg) {
            for (int j = 0; j < entryList.size(); j++) {
                if (entryList.get(j).id == a)
                    mAdapter.notifyItemChanged(mSectionedAdapter.positionToSectionedPosition(j));//登録したペアを更新
            }
        }

        pairflg.clear();
        actionBarSetting();

        //登録ボタン、キャンセルボタンの非表示
        ((Button)getActivity().findViewById(R.id.button_entry_cancel)).setVisibility(View.GONE);
        ((Button)getActivity().findViewById(R.id.button_entry_regi)).setVisibility(View.GONE);

        //ボトムバーの表示
        BottomNavigationView ba =getActivity().findViewById(R.id.nav_view);
        ba.setVisibility(View.VISIBLE);

    }

    //インスタンスの作成
    public static EntryMember newInstance(){
        EntryMember f = new EntryMember();

        //参加者リストの作成
        f.entryList = new ArrayList<MemberManagement.MemberList>();
        for (MemberManagement.MemberList memberList : MemberManagementFragment.memberList) {
            if (memberList.entry == true) f.entryList.add(memberList);
        }
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entry_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //登録ボタン、キャンセルボタンの非表示
        ((Button)getActivity().findViewById(R.id.button_entry_cancel)).setVisibility(View.GONE);
        ((Button)getActivity().findViewById(R.id.button_entry_regi)).setVisibility(View.GONE);

        //参加者リストの更新
        entryList = new ArrayList<MemberManagement.MemberList>();
        for (MemberManagement.MemberList memberList : MemberManagementFragment.memberList) {
            if (memberList.entry == true) entryList.add(memberList);
        }

        //RecyclerViewの設定
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list1);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                getActivity(),
                Integer.parseInt(sp.getString("colum_entry_member",getResources().getString(R.string.colum_entry_member_default)
                ))));

        //アクションバーの設定
        actionBarSetting();

        //ボタンのリスナーの設定
        view.findViewById(R.id.button_entry_cancel).setOnClickListener(button_cancel);
        view.findViewById(R.id.button_entry_regi).setOnClickListener(button_regi);

        List<SectionedGridRecyclerViewAdapter.Section> sections = sectionAddLevelSort(entryList);

        // SimpleAdapterを定義
        mAdapter = new SimpleAdapter(getActivity(), entryList, false) {
            //項目がロングクリックされたら
            @Override
            void onLongItemClick(View view, int position) {
            }

            //項目がクリックされたら
            @Override
            void onItemClick(View view, int position) {//position：section込みで何番目か　posi：section抜きで何番目か//simpleAdapterのpositionは元々section抜きの値

                final int posi = mSectionedAdapter.sectionedPositionToPosition(position);
                final int posi1 = position;

                //ポップアップメニューの初期化
                PopupMenu popup = new PopupMenu(getActivity(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.entry_menu, popup.getMenu());

                //ペアリストに選択したIDがあったらtrue
                boolean f1=false;
                for(Integer[] a:MemberManagement.MemberList.pair){
                    for(int b:a){
                        if(b==entryList.get(posi).id)f1=true;
                    }
                }

                //ポップアップメニューの削除
                popup.getMenu().removeItem(f1
                        ? R.id.pair_entry//ペアが設定されていたら
                        : R.id.pair_entry_cancel);//ペアが設定されていなかったら

                //ポップアップメニューの削除
                popup.getMenu().removeItem(entryList.get(posi).forcedEntry
                        ? R.id.entry_definitely//強制参加になっていたら強制参加のほうを消す
                        : R.id.entry_definitely_cancel);//強制参加になっていなかったら強制参加解除のほうを消す

                //ポップアップメニューの削除
                popup.getMenu().removeItem(entryList.get(posi).forcedBreak
                        ? R.id.entry_break//強制参加になっていたら強制参加のほうを消す
                        : R.id.entry_break_cancel);//強制参加になっていなかったら強制参加解除のほうを消す

                if (pairflg.size() == 0) {//「ペアを設定する」が選択されていないか
                    popup.show();

                    //ポップアップが選択されたあとの処理
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.pair_entry://ペアの設定
                                    pairflg.add(entryList.get(posi).id);//ペアの追加

                                    //アクションバーの切り替え
                                    ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                                    actionbar.setCustomView(R.layout.titlebar);
                                    TextView textview = actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_title);
                                    textview.setText(R.string.message2);
                                    TextView textview1 = actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_subtitle);
                                    textview1.setText(entryList.get(posi).name);
                                    actionbar.setDisplayShowCustomEnabled(true);

                                    //ボトムバーの非表示
                                    BottomNavigationView ba =getActivity().findViewById(R.id.nav_view);
                                    ba.setVisibility(View.GONE);

                                    //登録ボタン、キャンセルボタンの表示
                                    ((Button)getActivity().findViewById(R.id.button_entry_cancel)).setVisibility(View.VISIBLE);
                                    ((Button)getActivity().findViewById(R.id.button_entry_regi)).setVisibility(View.VISIBLE);

                                    notifyItemChanged(posi1);
                                    break;
                                case R.id.pair_entry_cancel://ペアの解除
                                    Integer[] c;
                                    for(int i=0;i<MemberManagement.MemberList.pair.size();i++){//設定されているペアだけ繰り返し
                                        for(int b:MemberManagement.MemberList.pair.get(i)){
                                            if(b==entryList.get(posi).id) {
                                                c=MemberManagement.MemberList.pair.get(i);
                                                MemberManagement.MemberList.pair.remove(i);//ペアをを削除
                                                for(Integer a:c){
                                                    for(int j=0;j<entryList.size();j++){
                                                        if(entryList.get(j).id==a)  notifyItemChanged(mSectionedAdapter.positionToSectionedPosition(j));//削除したペアを更新
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case R.id.entry_decrement://参加回数を1減らす
                                    for(MemberManagement.MemberList m:MemberManagementFragment.memberList){
                                        if(entryList.get(posi).id==m.id){
                                            m.entryTimes--;
                                            notifyItemChanged(posi1);
                                        }
                                    }
                                    break;
                                case R.id.entry_increment://参加回数を1増やす
                                    for(MemberManagement.MemberList m:MemberManagementFragment.memberList){
                                        if(entryList.get(posi).id==m.id){
                                            m.entryTimes++;
                                            notifyItemChanged(posi1);
                                        }
                                    }
                                    break;
                                case R.id.entry_definitely://強制参加にする
                                    for(MemberManagement.MemberList m:MemberManagementFragment.memberList){
                                        if(entryList.get(posi).id==m.id){
                                            m.forcedEntry=true;
                                            m.forcedBreak=false;
                                            notifyItemChanged(posi1);
                                        }
                                    }
                                    break;
                                case R.id.entry_break://休憩にする
                                    for(MemberManagement.MemberList m:MemberManagementFragment.memberList){
                                        if(entryList.get(posi).id==m.id){
                                            m.forcedBreak=true;
                                            m.forcedEntry=false;
                                            notifyItemChanged(posi1);
                                        }
                                    }
                                    break;
                                case R.id.entry_break_cancel://休憩を解除する
                                    for(MemberManagement.MemberList m:MemberManagementFragment.memberList){
                                        if(entryList.get(posi).id==m.id){
                                            m.forcedBreak=false;
                                            notifyItemChanged(posi1);
                                        }
                                    }
                                    break;
                                case R.id.entry_definitely_cancel://強制参加を解除する
                                    for(MemberManagement.MemberList m:MemberManagementFragment.memberList){
                                        if(entryList.get(posi).id==m.id){
                                            m.forcedEntry=false;
                                            notifyItemChanged(posi1);
                                        }
                                    }
                                    break;
                            }
                            return false;
                        }

                    });
                } else {//ペアが設定されている場合　
                    //ペアリストに選択したIDがあったらtrue
                    boolean f=false;
                    for(Integer[] a:MemberManagement.MemberList.pair){
                        for(int b:a){
                            if(b==entryList.get(posi).id)f=true;
                        }
                    }

                    if (pairflg.contains(entryList.get(posi).id) || f) {//本人やすでに選択されている人、またはすでにペアが設定されている人が選択された場合
                        Toast.makeText(getActivity(), R.string.message1, Toast.LENGTH_SHORT).show();
                    } else {
                        pairflg.add(entryList.get(posi).id);

                        //アクションバーの設定
                        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                        actionbar.getCustomView();
                        TextView textview1 = actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_subtitle);
                        textview1.setText(textview1.getText()+","+entryList.get(posi).name);
                        actionbar.setDisplayShowCustomEnabled(true);

                        //登録ボタンを押せるようにする
                        getActivity().findViewById(R.id.button_entry_regi).setEnabled(true);

                        notifyItemChanged(posi1);
                    }
                }
            }
        };

        //dummy用の配列
        SectionedGridRecyclerViewAdapter.Section[] dummy = new SectionedGridRecyclerViewAdapter.Section[sections.size()];

        mSectionedAdapter = new SectionedGridRecyclerViewAdapter(getActivity(), mRecyclerView, mAdapter);

        //(sections.toArray(dummy)はsectionsというリストをdummyという配列に変換するという意味
        mSectionedAdapter.setSections(sections.toArray(dummy));

        //RecyclerViewにアダプターセット
        mRecyclerView.setAdapter(mSectionedAdapter);


    }

    //デフォルトのアクションバーの設定
    public void actionBarSetting(){
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionbar.setCustomView(R.layout.titlebar);
        TextView textview = actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_title);
        textview.setText(R.string.member1);
        TextView textview1 = actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_subtitle);
        textview1.setText(getResources().getQuantityString(R.plurals.actionbar_people,entryList.size(),entryList.size()));
        actionbar.setDisplayShowCustomEnabled(true);
    }

    //フラグメントが離れたら
    @Override
    public void onPause() {
        super.onPause();
        pairflg.clear();

    }

    //セクションの追加
    public List<SectionedGridRecyclerViewAdapter.Section> sectionAddLevelSort(List<MemberManagement.MemberList> memberList) {
        List<SectionedGridRecyclerViewAdapter.Section> sections = new ArrayList<SectionedGridRecyclerViewAdapter.Section>();
        Map<String, Integer> sectionInt = new ArrayMap<String, Integer>();

        //リストのソート（レベル順）
        Collections.sort(memberList, new Comparator<MemberManagement.MemberList>() {
            @Override
            public int compare(MemberManagement.MemberList o, MemberManagement.MemberList o1) {
                return o.level - o1.level;
            }
        });

        sectionInt.put("▶" + getString(R.string.memberadd_beginner), 1);
        sectionInt.put("▶"+getString(R.string.memberadd_elementary), 2);
        sectionInt.put("▶"+getString(R.string.memberadd_intermediate), 3);
        sectionInt.put("▶"+getString(R.string.memberadd_advanced), 4);
        sectionInt.put("▶"+getString(R.string.memberadd_pro), 5);

        //メンバーリストの上から順番にsectionCharの値に一致する名前がないか調べ、一致する前があったらその名前の上にsectionCharのキー名でsectionを作る
        for (Map.Entry<String, Integer> a : sectionInt.entrySet()) {
            int j = 0;
            int k = 0;
            for (int i = 0; i < memberList.size(); i++) {
                if (a.getValue().equals((memberList.get(i).level))) {
                    j++;
                    if (j == 1) k = i;
                }
            }
            if (j != 0)sections.add(new SectionedGridRecyclerViewAdapter.Section(k, a.getKey() + " : "  + getResources().getQuantityString(R.plurals.sectionbar_people,j,j)));
        }
        return sections;
    }

}
