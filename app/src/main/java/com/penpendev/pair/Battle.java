package com.penpendev.pair;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import static com.penpendev.pair.MainActivity.MemberManagementFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class Battle extends Fragment implements BattleDialogFlagment.OnButtonClickListener {

    ArrayList<ArrayList<MemberManagement.MemberList[]>> battleTime = new ArrayList<>();//1,2,3試合目とbattleMemberを保存する
    ArrayList<ArrayList<MemberManagement.MemberList>> breakTime = new ArrayList<>();//1,2,3試合目とbreaklistを保存する
    ArrayList<MemberManagement.MemberList[]> battleMember = new ArrayList<>();//どのコートにどのメンバが試合に入っているか
    SharedPreferences sp;//設定から読み込むための変数

    ArrayList<MemberManagement.MemberList> entryList = new ArrayList<MemberManagement.MemberList>();
    ArrayList<MemberManagement.MemberList> breakList = new ArrayList<MemberManagement.MemberList>();

    RecyclerView mRecyclerView;
    BattleAdapter battleAdapter;
    int currentbattle;
    ActionBar actionbar;

    //定期実行の変数
    static Handler handler;
    static Runnable runnable;

    //コンストラクタ
    public Battle() {
    }

    //インスタンスの作成
    public static Battle newInstance(String breaktime, String battletime) {
        Battle f = new Battle();
        Gson gson = new Gson();
        if (battletime != null || breaktime != null) {
            Type listType;
            listType=new TypeToken<ArrayList<ArrayList<MemberManagement.MemberList[]>>>(){}.getType();
            f.battleTime = gson.fromJson(battletime, listType);
            listType=new TypeToken<ArrayList<ArrayList<MemberManagement.MemberList>>>(){}.getType();
            f.breakTime = gson.fromJson(breaktime, listType);
        }
        return f;
    }

    @Override
    public void onPositiveClick() {
    }

    @Override
    public void onNegativeClick() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_battle, container, false);
        return v;
    }

    //メンバーリストとidを渡して、そのIDを持つメンバーを返す
    public MemberManagement.MemberList memberidToGetMember(int id, ArrayList<MemberManagement.MemberList> m) {
        for (MemberManagement.MemberList mm : m) {
            if (mm.id == id) return mm;
        }
        return null;
    }

    //フラグメントが離れたら開始ボタンを定期的に消す処理をキャンセル
    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    //フラグメントが再スタートしたら開始ボタンを定期的に消す処理を再スタート
    @Override
    public void onStart() {
        handler.postDelayed(runnable, 5000);
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //ボタンの非表示
        ((Button) getActivity().findViewById(R.id.button_battle_cancel)).setVisibility(View.GONE);
        ((Button) getActivity().findViewById(R.id.button_battle_shuffle)).setVisibility(View.GONE);
        ((Button) getActivity().findViewById(R.id.button_battle_start)).setVisibility(View.GONE);

        //開始ボタンを定期的に消す処理
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                FloatingActionButton fa = (FloatingActionButton) getActivity().findViewById(R.id.battle_start);
                fa.hide();
                handler.postDelayed(this, 4000);
            }
        };

        //アクションバーの設定
        actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionbar.setCustomView(R.layout.titlebar_battle);
        ((TextView) actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_title)).setText("-----");
        //((TextView) actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_subtitle)).setVisibility(View.GONE);
        actionbar.setDisplayShowCustomEnabled(true);

        //戻るボタン、進むボタンのリスナーを登録
        actionbar.getCustomView().findViewById(R.id.button_next).setOnClickListener(buttonNextClickListener);
        actionbar.getCustomView().findViewById(R.id.button_prev).setOnClickListener(buttonPrevClickListener);


        // RecyclerViewを定義
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.battle_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                getActivity(),
                Integer.parseInt(sp.getString("battle_colums_key", getResources().getString(R.string.battle_colums_default)))
        ));

        buttonDisplay(-1);

        //フローティングボタンを押すとバトルをスタートする
        view.findViewById(R.id.battle_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creatEntryList(true);//休憩の人は除外

                //Toast.makeText(getActivity(),String.valueOf(entryList.contains(entryList.get(0).level)),Toast.LENGTH_SHORT).show();
                if (2 <= entryList.size()) {//参加者数が2人以上の場合のみ実行

                    //FAボタンを定期的に消す処理を削除
                    handler.removeCallbacks(runnable);

                    //ボタンの表示
                    ((Button) getActivity().findViewById(R.id.button_battle_cancel)).setVisibility(View.VISIBLE);
                    ((Button) getActivity().findViewById(R.id.button_battle_start)).setVisibility(View.VISIBLE);
                    //ボタンの非表示
                    ((ImageButton) getActivity().findViewById(R.id.button_prev)).setVisibility(View.GONE);
                    ((ImageButton) getActivity().findViewById(R.id.button_next)).setVisibility(View.GONE);

                    //ボトムバーの非表示
                    BottomNavigationView ba = getActivity().findViewById(R.id.nav_view);
                    ba.setVisibility(View.GONE);

                    //アクションバーの設定
                    ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                    ((TextView) actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_title)).setText((battleTime.size() + 1) + " 試合目（仮）");


                    battleMember.clear();
                    breakList.clear();

                    //メンバーのソート
                    membershuffle();

                    //battlememberを作成
                    creatBattleMember();

                    creatEntryList(false);//参加者リストの作成（休憩の人も含む）

                    //休憩リストの作成（参加者リストーコートに入っている人＝休憩する人）
                    for (MemberManagement.MemberList m : entryList) {
                        boolean f = true;
                        loop1:
                        for (MemberManagement.MemberList[] mm : battleMember) {
                            for (MemberManagement.MemberList mmm : mm) {
                                if (mmm.id == m.id) {
                                    f = false;
                                    break loop1;
                                }
                            }
                        }
                        if (f) breakList.add(m);
                    }


                    //試合内容の表示
                    battleAdapter = new BattleAdapter(getActivity(), mRecyclerView, battleMember, breakList);
                    mRecyclerView.setAdapter(battleAdapter);
                    getActivity().findViewById(R.id.battle_list).setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(getActivity(), R.string.notenouth_entrymember, Toast.LENGTH_SHORT).show();
                }
            }


        });


        //確定ボタンを押すとバトルをスタートする
        view.findViewById(R.id.button_battle_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //参加者リストの作成（休憩の人を除外する）
                creatEntryList(true);

                //休憩履歴に追加
                breakTime.add(new ArrayList<MemberManagement.MemberList>());
                breakTime.get(breakTime.size() - 1).addAll(breakList);//ディープコピー

                //試合履歴に追加
                battleTime.add(new ArrayList<MemberManagement.MemberList[]>());
                battleTime.get(battleTime.size() - 1).addAll(battleMember);//ディープコピー

                //参加した人のバトル回数をプラスにする
                for (MemberManagement.MemberList[] a : battleMember) {
                    for (MemberManagement.MemberList b : a) {
                        for (MemberManagement.MemberList m : MemberManagementFragment.memberList) {
                            if (b.id == m.id) {
                                m.entryTimes++;
                                break;
                            }
                        }
                    }
                }

                buttonDisplay(battleTime.size());

                //ボタンの非表示
                ((Button) getActivity().findViewById(R.id.button_battle_cancel)).setVisibility(View.GONE);
                ((Button) getActivity().findViewById(R.id.button_battle_shuffle)).setVisibility(View.GONE);
                ((Button) getActivity().findViewById(R.id.button_battle_start)).setVisibility(View.GONE);

                //ボトムバーの表示
                ((BottomNavigationView) getActivity().findViewById(R.id.nav_view)).setVisibility(View.VISIBLE);

                //FAボタンを定期的に消す処理を開始
                handler.postDelayed(runnable, 5000);
            }
        });
        //キャンセルボタンを押すとバトルをキャンセルする
        view.findViewById(R.id.button_battle_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ボタンの非表示
                ((Button) getActivity().findViewById(R.id.button_battle_cancel)).setVisibility(View.GONE);
                ((Button) getActivity().findViewById(R.id.button_battle_shuffle)).setVisibility(View.GONE);
                ((Button) getActivity().findViewById(R.id.button_battle_start)).setVisibility(View.GONE);

                //FAボタンを定期的に消す処理を開始
                handler.postDelayed(runnable, 5000);

                //ボトムバーの表示
                BottomNavigationView ba = getActivity().findViewById(R.id.nav_view);
                ba.setVisibility(View.VISIBLE);

                buttonDisplay(battleTime.size());
            }
        });
    }

    public void creatBattleMember() {
        //メンバーの総数をゲット
        int countMember = getCountMember();

        //ペアをランダムに入れるための変数
        ArrayList<Integer> battleMemberCount = new ArrayList<>();
        for (int i = 0; i < battleMember.size(); i++) {
            battleMemberCount.add(i);
        }
        Collections.shuffle(battleMemberCount);//シャッフル


        //コートに入れるペアをコートに入れる
        int enterMemberCount = 0;
        ArrayList<Integer> enterpair = new ArrayList<>();//入れたペア
        for (MemberManagement.MemberList m : entryList) {//参加者リストのループ
            if (enterMemberCount >= countMember) break;
            if (!enterpair.contains(m.id)) {
                boolean ff=true;//これがtrueになる＝コートに入れられたペアということ。なのでenterMemberCountをインクリメントしない
                loop1:
                for (Integer[] a : MemberManagement.MemberList.pair) {//ペアリストのループ＝a
                    for (Integer b : a) {//ペアとして設定されている一人ひとり＝b
                        if (m.id == b) {//ペアとして設定されている参加者か
                            for (int i = 0; i < battleMemberCount.size(); i++) {//ペアが入れるコートがある c.length:コートに入れる人数 a.length:設定されているペアの人数
                                int haireru = 0;
                                for (int d = 1; d < battleMember.get(battleMemberCount.get(i)).length; d = d + 2) {//右側のコートに入れるか
                                    if (battleMember.get(battleMemberCount.get(i))[d] == null)haireru++;
                                }
                                if (haireru >= a.length) {//右側のコートに入れるようなら
                                    haireru = 0;
                                    for (int f = 1; f < battleMember.get(battleMemberCount.get(i)).length; f = f + 2) {//右側のコート
                                        if (battleMember.get(battleMemberCount.get(i))[f] == null) {
                                            battleMember.get(battleMemberCount.get(i))[f] = memberidToGetMember(a[haireru], entryList);
                                            enterpair.add(a[haireru]);
                                            enterMemberCount++;
                                            haireru++;
                                            ff=false;
                                            if(haireru==a.length)break;
                                        }
                                    }
                                    Collections.shuffle(battleMemberCount);
                                    break loop1;
                                } else {//右側のコートで入れなかったら左側のコートでペアが入れるか
                                    haireru = 0;
                                    for (int f = 0; f < battleMember.get(battleMemberCount.get(i)).length; f = f + 2) {//左側のコートに空きがあるか
                                        if (battleMember.get(battleMemberCount.get(i))[f] == null)haireru++;
                                    }
                                    if (haireru >= a.length) {//左側のコートに入れるようなら
                                        haireru = 0;
                                        for (int f = 0; f < battleMember.get(battleMemberCount.get(i)).length; f = f + 2) {//左側のコート
                                            if (battleMember.get(battleMemberCount.get(i))[f] == null) {
                                                battleMember.get(battleMemberCount.get(i))[f] = memberidToGetMember(a[haireru], entryList);
                                                enterpair.add(a[haireru]);
                                                enterMemberCount++;
                                                haireru++;
                                                ff=false;
                                                if(haireru==a.length)break;
                                            }
                                        }
                                        Collections.shuffle(battleMemberCount);
                                        break loop1;
                                    }
                                }
                            }
                        }
                    }
                }
                if(ff)enterMemberCount++;
            }
        }

        //参加者リストの中の入れなかったペアをすべて消す
        for (int i = entryList.size() - 1; i >= 0; i--) {
            loop2:
            for (Integer[] re : MemberManagement.MemberList.pair) {
                for (Integer ree : re) {
                    if (ree == entryList.get(i).id) {
                        entryList.remove(i);
                        break loop2;
                    }
                }
            }
        }

        //残りコートに入れる人数以上の参加者がいる場合消す 10 5 5、ただし消す対象のヤツの参加回数が参加対象のヤツと同じ参加回数の場合は消さない）
        if (entryList.size() > countMember - enterpair.size()) {
            for (int i = entryList.size() - 1; i > countMember - enterpair.size(); i--) {
                if (entryList.get(i).entryTimes > entryList.get(countMember - enterpair.size()).entryTimes) {
                    entryList.remove(i);
                }
            }
        }

        if (sp.getString("battle_method",getString(R.string.battle_colums_default)).equals("2")) {//「レベルを均等」パターン
            //ペアがうまっているコートをpairenterに入れる
            if (enterpair.size() > 0) {
                int qq = 0;
                ArrayList<Integer> pairEnterCoat = new ArrayList<>();//ペアが入ってるコートの順番//そのコートのレベル、あと何人必要か
                for (MemberManagement.MemberList[] m : battleMember) {//ペアが放り込まれているか探す　放り込まれていたらadd
                    for (int i = 0; i < m.length; i++) {
                        if (m[i] != null) {
                            pairEnterCoat.add(qq);
                            break;
                        }
                    }
                    qq++;
                }

                //コートに埋まっているペアに対して、ペアと同じ強さの対戦相手を放り込む
                if (pairEnterCoat.size() > 0) {//ペアが放り込まれていたら実行
                    for (int cc : pairEnterCoat) {
                        int level = 0;
                        for (int i = 0; i < battleMember.get(cc).length; i++) {//そのコートのレベルをはかる
                            if (battleMember.get(cc)[i] != null) {
                                level = battleMember.get(cc)[i].level;
                                break;
                            }
                        }
                        for (int i = 0; i < battleMember.get(cc).length; i++) {
                            boolean f = true;
                            if (battleMember.get(cc)[i] == null) {
                                for (int j = 0; j < entryList.size(); j++) {
                                    if (entryList.get(j).level == level) {
                                        battleMember.get(cc)[i] = new MemberManagement.MemberList(entryList.get(j));
                                        f = false;
                                        entryList.remove(j);
                                        break;
                                    }
                                }
                                if (f) {//1人も同じレベルのやつがいなかったら･･･
                                    if (!sp.getBoolean("notenough_entry_hidden", getResources().getBoolean(R.bool.notenough_entry_hidden))) {//全員揃わないようならコートの中を全部消す
                                        for (int j = 0; j < battleMember.get(cc).length; j++) {
                                            battleMember.get(cc)[j]=null;
                                        }
                                        break;
                                    }else{
                                        battleMember.get(cc)[i] = new MemberManagement.MemberList("---", 1, Gender.OTHER);//「---」を入れる
                                    }

                                }
                            }
                        }
                    }
                }
            }

            //battleメンバーを埋める
            int[] targetLevel1={1,2,3,4,5};
            int currentLevel=-1;
            for (MemberManagement.MemberList[] m : battleMember) {
                if (m[0] == null) {//中身が全部nullならうめる(この時点で全部埋まっているか、全部nullかのどちらかなので)
                    for(int i:targetLevel1){
                        int count=0;
                        if (entryList.size() != 0) {//entrylistが空じゃなかったら
                            for (MemberManagement.MemberList mm : entryList) {
                                if (mm.level == i) count++;
                            }

                            if(count >= m.length) {
                                currentLevel=i;
                                break;
                            }else{
                                currentLevel=-1;
                            }
                        }
                    }
                    for (int i = 0; i < m.length; i++) {//1つのコートをループ
                        if(currentLevel==-1){
                            m[i] = new MemberManagement.MemberList("---", 1, Gender.OTHER);
                        }else{
                            for (MemberManagement.MemberList mm : entryList) {
                                if (mm.level == currentLevel) {
                                    m[i] = new MemberManagement.MemberList(mm);
                                    entryList.remove(mm);
                                    break;
                                }
                            }
                        }
                    }
                }

            }

        } else if (sp.getString("battle_method",getString(R.string.battle_colums_default)).equals("1")) {//レベル毎
            ArrayList<ArrayList<MemberManagement.MemberList[]>> battleMembertest = new ArrayList<>();//1,2,3試合目とbattleMemberを保存する
            final int CALC_COUNT = 10;//何回繰り返すか
            float[] power = new float[CALC_COUNT];//強さのかたより　0に近いほど強さのバランスが均等
            Arrays.fill(power, 0);

            ArrayList<MemberManagement.MemberList> entryListback = new ArrayList<>();
            for (int hh = 0; hh < CALC_COUNT; hh++) {

                //entrylistが毎回消えてしまうのでバックアップを撮っておいて毎回復元する
                if (entryListback.isEmpty()) {
                    entryListback.addAll(entryList);//バックアップ
                } else {
                    entryList.clear();
                    entryList.addAll(entryListback);//復元
                }

                //メンバーのソート(entrylist)
                membershuffle();

                //ペアのみを入れたパターンをディープコピー（battleMemberの中身を battleMembertestにぶち込む
                ArrayList<MemberManagement.MemberList[]> aaaa = new ArrayList<>();//仮変数
                for (MemberManagement.MemberList[] m : battleMember) {
                    MemberManagement.MemberList[] asd = new MemberManagement.MemberList[m.length];//仮変数
                    for (int i = 0; i < m.length; i++) {
                        if (m[i] != null)
                            asd[i] = new MemberManagement.MemberList(m[i]);//Memberlistのコンストラクタに実装してるやつ
                    }
                    aaaa.add(asd);
                }
                battleMembertest.add(aaaa);


                //battleメンバーを埋める
                for (MemberManagement.MemberList[] m : battleMembertest.get(hh)) {
                    for (int i = 0; i < m.length; i++) {
                        if (m[i] == null) {
                            if (entryList.size() != 0) {//空じゃなかったら
                                m[i] = new MemberManagement.MemberList(entryList.get(0));
                                entryList.remove(0);
                            } else {//空だったら
                                m[i] = new MemberManagement.MemberList("---", 1, Gender.OTHER);
                            }
                        }
                    }
                }
                //「全員をコートに入れる」にチェックが無い場合、「---」がいるコートはすべて「---」に書き換える
                if (!sp.getBoolean("notenough_entry_hidden", getResources().getBoolean(R.bool.notenough_entry_hidden))) {
                    for (MemberManagement.MemberList[] m : battleMembertest.get(hh)) {
                        for (int i = 0; i < m.length; i++) {
                            if (m[i].name == "---") {
                                for (int j = 0; j < m.length; j++) {
                                    m[j] = new MemberManagement.MemberList("---", 1, Gender.OTHER);
                                }
                                break;
                            }
                        }
                    }
                }

                //各コートの強さを測定して、強さのばらつきをpowerに入れる
                for (MemberManagement.MemberList[] m : battleMembertest.get(hh)) {
                    float leftPower = 0;//左コートの総強さ
                    float rightPower = 0;//右コートの総強さ

                    int count = 0;
                    for (int i = 0; i < m.length; i = i + 2) {
                        leftPower += m[i].level;
                        count++;
                    }
                    leftPower = leftPower / count;

                    count = 0;
                    for (int i = 1; i < m.length; i = i + 2) {
                        rightPower += m[i].level;
                        count++;
                    }
                    rightPower = rightPower / count;

                    power[hh] += Math.max(rightPower, leftPower) - Math.min(rightPower, leftPower);
                }
            }

            //強さのばらつきが一番小さかった組み合わせを探す
            float min = power[0];
            int mini = 0;
            for (int i = 1; i < power.length; i++) {
                if (power[i] < min) {
                    min = power[i];
                    mini = i;
                }
            }
            power = null;//解放


            //強さのばらつきが一番小さかった組み合わせを入れる
            battleMember = battleMembertest.get(mini);
            battleMembertest = null;//解放
        }else{//完全ランダム
            //メンバーのソート(entrylist)
            membershuffle();

            //battleメンバーを埋める
            for (MemberManagement.MemberList[] m : battleMember) {
                for (int i = 0; i < m.length; i++) {
                    if (m[i] == null) {
                        if (entryList.size() != 0) {//空じゃなかったら
                            m[i] = new MemberManagement.MemberList(entryList.get(0));
                            entryList.remove(0);
                        } else {//空だったら
                            m[i] = new MemberManagement.MemberList("---", 1, Gender.OTHER);
                        }
                    }
                }
            }
            //「全員をコートに入れる」にチェックが無い場合、「---」がいるコートはすべて「---」に書き換える
            if (!sp.getBoolean("notenough_entry_hidden", getResources().getBoolean(R.bool.notenough_entry_hidden))) {
                for (MemberManagement.MemberList[] m : battleMember) {
                    for (int i = 0; i < m.length; i++) {
                        if (m[i].name == "---") {
                            for (int j = 0; j < m.length; j++) {
                                m[j] = new MemberManagement.MemberList("---", 1, Gender.OTHER);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    //参加者リストの作成（f==trueの場合、休憩の人を除外する）
    public void creatEntryList(boolean f) {

        entryList = new ArrayList<MemberManagement.MemberList>();
        if(f) {//休憩の人は除外する
            for (MemberManagement.MemberList memberList : MemberManagementFragment.memberList) {
                if (memberList.entry == true && memberList.forcedBreak == false)
                    entryList.add(memberList);
            }
        }else{//休憩の人も含む
            for (MemberManagement.MemberList memberList : MemberManagementFragment.memberList) {
                if (memberList.entry == true)
                    entryList.add(memberList);
            }
        }
    }

    //battleMemberの人数を設定し、コートの総人数を返す
    public int getCountMember() {
        //コートの数、中に入る人数を初期化する
        final int COAT_NUMBER = Integer.parseInt(//コート数
                sp.getString(
                        "coat_numbers_key",
                        getResources().getString(R.string.coat_numbers_defaultValue)
                )
        );
        for (int i = 0; i < COAT_NUMBER; i++) {
            final int COAT_PEOPLE = Integer.parseInt(//各コートに入る人数
                    sp.getString(
                            "coat" + i,
                            "4"
                    )
            );
            battleMember.add(new MemberManagement.MemberList[COAT_PEOPLE]);//初期化
        }

        //コートに入れる総数
        int countMember = 0;
        for (MemberManagement.MemberList[] c : battleMember) {
            countMember = countMember + c.length;
        }
        return countMember;
    }

    //次へボタンと戻るボタンの表示を切り替えたり、タイトルバーの文字を変えたりする関数
    public void buttonDisplay(final int current) {

        //最初は非表示
        ((ImageButton) getActivity().findViewById(R.id.button_next)).setVisibility(View.GONE);
        ((ImageButton) getActivity().findViewById(R.id.button_prev)).setVisibility(View.GONE);

        if (battleTime.size() > 0) {//試合履歴が0じゃなかった場合
            if (current == -1) {//フラグメントが作成された直後は常に最新の試合を提供する
                currentbattle = battleTime.size();
            } else {//それ以外の場合は指定された試合を提供する
                currentbattle = current;
            }

            //前ボタン、次ボタンの表示・非表示
            if (battleTime.size() > 1) {
                if (currentbattle == 1) {//1試合目が選択されている場合
                    ((ImageButton) getActivity().findViewById(R.id.button_next)).setVisibility(View.VISIBLE);//次ボタンのみ表示
                } else if (currentbattle == battleTime.size()) {//最後の試合が選択されている場合
                    ((ImageButton) getActivity().findViewById(R.id.button_prev)).setVisibility(View.VISIBLE);//前ボタンのみ表示
                } else {//それ以外の場合
                    ((ImageButton) getActivity().findViewById(R.id.button_next)).setVisibility(View.VISIBLE);//どっちも表示
                    ((ImageButton) getActivity().findViewById(R.id.button_prev)).setVisibility(View.VISIBLE);
                }
            }


            //試合内容の表示
            battleAdapter = new BattleAdapter(getActivity(), mRecyclerView, battleTime.get(currentbattle - 1), breakTime.get(currentbattle - 1)) {
                int coatPosition_1st = -1;
                int coatNumber_1st = -1;
                int maxCoat;
                @Override
                void onItemClick(View view, int coatPosition_2st, int coatNumber_2st) {
                    maxCoat = battleTime.get(currentbattle - 1).size();//コート数（これを使って休憩コートを判断する）
                    if (coatPosition_1st == -1) {//最初にタップされたとき
                        coatPosition_1st = coatPosition_2st;
                        coatNumber_1st = coatNumber_2st;
                        ((TextView) actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_title)).setText(R.string.replace_member);
                        view.setBackgroundColor(Color.parseColor("#88ffff77"));
                    } else {//2回目にタップされたとき
                        //先に選択した方が試合、後が休憩の場合
                        if (maxCoat > coatNumber_1st && maxCoat == coatNumber_2st) {

                            for (MemberManagement.MemberList m : MemberManagementFragment.memberList) { //参加回数を変更する
                                if (m.id == battleTime.get(currentbattle - 1).get(coatNumber_1st)[coatPosition_1st].id)
                                    m.entryTimes--;
                                if (m.id == breakTime.get(currentbattle - 1).get(coatPosition_2st).id)
                                    m.entryTimes++;
                            }
                            MemberManagement.MemberList tmp =
                                    new MemberManagement.MemberList(battleTime.get(currentbattle - 1).get(coatNumber_1st)[coatPosition_1st]);
                            battleTime.get(currentbattle - 1).get(coatNumber_1st)[coatPosition_1st] =
                                    new MemberManagement.MemberList(breakTime.get(currentbattle - 1).get(coatPosition_2st));
                            breakTime.get(currentbattle - 1).set(coatPosition_2st,
                                    new MemberManagement.MemberList(tmp));

                            notifyItemChanged(coatNumber_1st);
                            notifyItemChanged(coatNumber_2st);
                        }
                        //先に選択した方が休憩、後が試合の場合
                        if (maxCoat == coatNumber_1st && maxCoat > coatNumber_2st) {

                            for (MemberManagement.MemberList m : MemberManagementFragment.memberList) {//参加回数を変更する
                                if (m.id == battleTime.get(currentbattle - 1).get(coatNumber_2st)[coatPosition_2st].id)
                                    m.entryTimes--;
                                if (m.id == breakTime.get(currentbattle - 1).get(coatPosition_1st).id)
                                    m.entryTimes++;
                            }

                            MemberManagement.MemberList tmp =
                                    new MemberManagement.MemberList(breakTime.get(currentbattle - 1).get(coatPosition_1st));
                            breakTime.get(currentbattle - 1).set(coatPosition_1st,
                                    new MemberManagement.MemberList(battleTime.get(currentbattle - 1).get(coatNumber_2st)[coatPosition_2st]));
                            battleTime.get(currentbattle - 1).get(coatNumber_2st)[coatPosition_2st] =
                                    new MemberManagement.MemberList(tmp);

                            notifyItemChanged(coatNumber_1st);
                            notifyItemChanged(coatNumber_2st);
                        }
                        //どちらも休憩の場合
                        if (maxCoat == coatNumber_1st && maxCoat == coatNumber_2st) {
                            MemberManagement.MemberList tmp = new MemberManagement.MemberList(breakTime.get(currentbattle - 1).get(coatPosition_1st));
                            breakTime.get(currentbattle - 1).set(coatPosition_1st, breakTime.get(currentbattle - 1).get(coatPosition_2st));
                            breakTime.get(currentbattle - 1).set(coatPosition_2st, tmp);
                            notifyItemChanged(coatNumber_1st);
                        }
                        //どちらも試合の場合
                        if (maxCoat > coatNumber_1st && maxCoat > coatNumber_2st) {
                            MemberManagement.MemberList tmp = new MemberManagement.MemberList(battleTime.get(currentbattle - 1).get(coatNumber_1st)[coatPosition_1st]);
                            battleTime.get(currentbattle - 1).get(coatNumber_1st)[coatPosition_1st] =
                                    battleTime.get(currentbattle - 1).get(coatNumber_2st)[coatPosition_2st];
                            battleTime.get(currentbattle - 1).get(coatNumber_2st)[coatPosition_2st] = tmp;
                            notifyItemChanged(coatNumber_1st);
                            notifyItemChanged(coatNumber_2st);
                        }
                        coatPosition_1st = -1;
                        coatNumber_1st = -1;
                        buttonDisplay(currentbattle);
                    }

                }
            };
            mRecyclerView.setAdapter(battleAdapter);

            //タイトルバーの更新
            TextView a = ((AppCompatActivity) getActivity()).getSupportActionBar().getCustomView().findViewById(R.id.textView_battle_titlebar_title);
            a.setText(getString(R.string.battle_titlebar,currentbattle,battleTime.size()));
            //アクションバーの設定
           // ((TextView) actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_subtitle)).setVisibility(View.GONE);

        } else {//試合履歴が0の場合
            //アクションバーの設定
            ((TextView) actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_title)).setText("-----");
           // ((TextView) actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_subtitle)).setVisibility(View.GONE);
            getActivity().findViewById(R.id.battle_list).setVisibility(View.GONE);
        }
    }

    //前へ戻るボタンが押されたとき
    View.OnClickListener buttonPrevClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonDisplay(currentbattle - 1);
        }
    };

    //次へボタンが押されたとき
    View.OnClickListener buttonNextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonDisplay(currentbattle + 1);
        }
    };

    //試合履歴をリセットする
    public void resetBattleTime() {
        battleTime.clear();
        breakTime.clear();
    }


    //メンバーのシャッフル
    public void membershuffle() {
        //メンバーをランダムシャッフル
        Collections.shuffle(entryList);

        //メンバーのソート（参加回数順）
        if (!sp.getBoolean("entryTimes_disregard", getResources().getBoolean(R.bool.entryTimes_disregard))) {//チェックが入っていない場合のみ、参加回数を考慮する
            Collections.sort(entryList, new Comparator<MemberManagement.MemberList>() {
                @Override
                public int compare(MemberManagement.MemberList o, MemberManagement.MemberList o1) {
                    return o.entryTimes - o1.entryTimes;
                }
            });
        }

        //メンバーのソート（絶対参加のメンバーを先頭にする）
        Collections.sort(entryList, new Comparator<MemberManagement.MemberList>() {
            @Override
            public int compare(MemberManagement.MemberList o, MemberManagement.MemberList o1) {
                if ((o.forcedEntry && o1.forcedEntry) || (!o.forcedEntry && !o1.forcedEntry)) {
                    return 0;//同じ場合
                } else if (o.forcedEntry && !o1.forcedEntry) {
                    return -1;//左右？のやつを優先する
                } else if (!o.forcedEntry && o1.forcedEntry) {
                    return 1;//左右？のやつを優先する
                } else {
                    Toast.makeText(getActivity(), "絶対参加のソートでエラー（デバッグ用）", Toast.LENGTH_SHORT).show();
                    return 100;//エラー
                }

            }
        });
    }

}

