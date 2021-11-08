package com.penpendev.pair;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.penpendev.pair.MainActivity.MemberManagementFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemberManagement extends Fragment {

    SimpleAdapter mAdapter;
    SectionedGridRecyclerViewAdapter mSectionedAdapter;
    public ArrayList<MemberList> memberList = new ArrayList<MemberList>();
    RecyclerView mRecyclerView;
    SharedPreferences sp;//設定から読み込むための変数

    //メンバーのクラス
    public static class MemberList {
        static int idyou = -1;//一意のIDを付けるようの変数
        static ArrayList<Integer[]> pair=new ArrayList<Integer[]>();//設定していた場合、この人たちと必ずペアになる
        String name;
        int level;
        Gender gender;
        boolean entry;
        boolean forcedEntry=false;
        boolean forcedBreak=false;
        int id;

        //ここの変数はEntryクラスで使う
        int entryTimes;//総対戦回数
        Map<String, Integer> entryTimesOpponentsName;//対戦した相手の名前と回数
        Map<String, Integer> entryTimesPartnersName;//味方だった人の名前と回数
        List<Integer> notpair;//設定していた場合、この人とペアにはならない


        //  コンストラクタ
        public MemberList(String name, int level, Gender gender) {
            this.name = name;
            this.level = level;
            this.gender = gender;
            this.entry = false;
            this.id = this.idyou + 1;
            idyou++;

            this.entryTimes = 0;
            this.entryTimesOpponentsName = new HashMap<>();
            this.entryTimesPartnersName= new HashMap<>();
            this.notpair = new ArrayList<>();
        }
        //ディープコピーするためのコンストラクタ
        public MemberList(MemberList m){
            this.name = m.name;
            this.level = m.level;
            this.gender = m.gender;
            this.entry = m.entry;
            this.forcedEntry=m.forcedEntry;
            this.forcedBreak=m.forcedBreak;
            this.id = m.id;

            this.entryTimes=m.entryTimes;
            this.entryTimesOpponentsName=m.entryTimesOpponentsName;//これディープコピー？？
            this.entryTimesPartnersName=m.entryTimesPartnersName;//これディープコピー？？
            this.notpair=m.notpair;//これディープコピー？？
        }
    }

    //インスタンスの作成メソッド
    public static MemberManagement newInstance(String member,String pair,int idyou) {
        MemberManagement f = new MemberManagement();

        Gson gson = new Gson();
        if(member==null){


            f.memberList.add(new MemberList(GetApplication.getInstance().getApplicationContext().getString(R.string.member_intro1), 3, Gender.WOMAN));
            f.memberList.add(new MemberList(GetApplication.getInstance().getApplicationContext().getString(R.string.member_intro2), 1, Gender.MAN));
/*            f.memberList.add(new MemberList("あ1", 1, Gender.MAN));
            f.memberList.add(new MemberList("い1", 1, Gender.MAN));
            f.memberList.add(new MemberList("う1", 1, Gender.MAN));
            f.memberList.add(new MemberList("え1", 1, Gender.MAN));
            f.memberList.add(new MemberList("お1", 1, Gender.WOMAN));
            f.memberList.add(new MemberList("か2", 2, Gender.WOMAN));
            f.memberList.add(new MemberList("き2", 2, Gender.MAN));
            f.memberList.add(new MemberList("く2", 2, Gender.MAN));
            f.memberList.add(new MemberList("け2", 2, Gender.MAN));
            f.memberList.add(new MemberList("こ2", 2, Gender.MAN));
            f.memberList.add(new MemberList("さ3", 3, Gender.WOMAN));
            f.memberList.add(new MemberList("し3", 3, Gender.WOMAN));
            f.memberList.add(new MemberList("す3", 3, Gender.MAN));
            f.memberList.add(new MemberList("せ3", 3, Gender.MAN));
            f.memberList.add(new MemberList("そ3", 3, Gender.MAN));
            f.memberList.add(new MemberList("た4", 4, Gender.WOMAN));
            f.memberList.add(new MemberList("ち4", 4, Gender.WOMAN));
            f.memberList.add(new MemberList("つ4", 4, Gender.MAN));
            f.memberList.add(new MemberList("て4", 4, Gender.WOMAN));
            f.memberList.add(new MemberList("と4", 4, Gender.WOMAN));*/
/*            for (MemberList a : f.memberList) {
                a.entry = true;
            }*/
/*            f.memberList.add(new MemberList("やまだ", 1, Gender.MAN));
            f.memberList.add(new MemberList("たなか", 2, Gender.WOMAN));
            f.memberList.add(new MemberList("やまなか", 2, Gender.WOMAN));
            f.memberList.add(new MemberList("あいかわ", 3, Gender.MAN));
            f.memberList.add(new MemberList("うちだ", 3, Gender.WOMAN));
            f.memberList.add(new MemberList("あいだ", 3, Gender.WOMAN));
            f.memberList.add(new MemberList("いぐち", 1, Gender.MAN));
            f.memberList.add(new MemberList("いいやま", 5, Gender.WOMAN));
            f.memberList.add(new MemberList("えがわ", 2, Gender.WOMAN));
            f.memberList.add(new MemberList("おのだ", 1, Gender.MAN));
            f.memberList.add(new MemberList("かきうち", 3, Gender.WOMAN));
            f.memberList.add(new MemberList("わだ", 3, Gender.WOMAN));*/
        }else{
            f.memberList= gson.fromJson(member, new TypeToken<ArrayList<MemberList>>(){}.getType());
            MemberList.pair=gson.fromJson(pair, new TypeToken< ArrayList<Integer[]>>(){}.getType());
            MemberList.idyou=idyou;
        }
        return f;
    }

    //コンストラクタ　空にしておく必要あり
    public MemberManagement() {
    }

    @Override
    @CheckResult
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //第1引数の内容を第2引数にぶち込む。第3引数はfalse。
        return inflater.inflate(R.layout.fragment_member_management, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //アクションバーの設定
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionbar.setCustomView(R.layout.titlebar);
        TextView textview = actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_title);
        textview.setText(R.string.entry1);
        TextView textview1 = actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_subtitle);
        textview1.setText(getResources().getQuantityString(R.plurals.actionbar_people,memberList.size(),memberList.size()));

        actionbar.setDisplayShowCustomEnabled(true);

        // RecyclerViewを定義
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                getActivity(),
                Integer.parseInt(sp.getString("colum_entry_member",getResources().getString(R.string.colum_entry_member_default)
        ))));

        // SimpleAdapterを定義
        mAdapter = new SimpleAdapter(getActivity(), memberList, true) {
            //項目がロングクリックされたらメンバー情報を参照
            @Override
            void onLongItemClick(View view, int position) {
                int posi = mSectionedAdapter.sectionedPositionToPosition(position);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new MemberAdd(memberList.get(posi), posi))
                        .addToBackStack(null)
                        .commit();
            }

            //項目がクリックされたら参加・非参加を切り替え
            @Override
            void onItemClick(View view, int position) {//position：section込みで何番目か　posi：section抜きで何番目か
                int posi = mSectionedAdapter.sectionedPositionToPosition(position);
                if (memberList.get(posi).entry == false) {//非参加だったら参加にする
                    memberList.get(posi).entry = true;
                } else {//参加だったら非参加にする
                    memberList.get(posi).entry=false;

                    //参加者から外したときにペアが設定されていた場合はそのペアを解消する
                    hazureru:
                    for (MemberManagement.MemberList m : memberList) {
                        for (int i = 0; i < MemberList.pair.size(); i++) {
                            for (Integer aa : MemberList.pair.get(i)) {
                                if (memberList.get(posi).id == aa) {
                                    MemberList.pair.remove(i);
                                    break hazureru;
                                }
                            }
                        }
                    }
                }
                notifyItemChanged(position);
            }
        };

        List<SectionedGridRecyclerViewAdapter.Section> sections = sectionAddNameSort(memberList);//名前順でセクションを追加する

        //dummy用の配列
        SectionedGridRecyclerViewAdapter.Section[] dummy = new SectionedGridRecyclerViewAdapter.Section[sections.size()];

        mSectionedAdapter = new SectionedGridRecyclerViewAdapter(getActivity(), mRecyclerView, mAdapter);

        //(sections.toArray(dummy)はsectionsというリストをdummyという配列に変換するという意味
        mSectionedAdapter.setSections(sections.toArray(dummy));

        //RecyclerViewにアダプターセット
        mRecyclerView.setAdapter(mSectionedAdapter);

        //フローティングボタンを押すと登録画面を表示する
        view.findViewById(R.id.MemberAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new MemberAdd())
                        .addToBackStack(null)
                        .commit();
            }
        });


        //スクロールしたらフローティングアクションボタンを隠す
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.MemberAdd);
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown()) fab.hide();
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) fab.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }


    //メンバーを新規追加、更新
    public void memberUpdate(MemberManagement.MemberList memberList, int position) {
        if (position == -1) {//新規追加
            this.memberList.add(new MemberList(memberList.name, memberList.level, memberList.gender));
        } else {//更新
            this.memberList.set(position, memberList);
        }
    }

    //メンバーの削除
    public void memberUpdate(int position) {
        this.memberList.remove(position);
    }

    //メンバーの削除
    public void entrytimeReset() {
        for(MemberList m:memberList){
            m.entryTimes=0;
        }
    }


    //セクションの追加
    public List<SectionedGridRecyclerViewAdapter.Section> sectionAddNameSort(List<MemberList> memberList) {
        List<SectionedGridRecyclerViewAdapter.Section> sections = new ArrayList<SectionedGridRecyclerViewAdapter.Section>();
        Map<String, Character[]> sectionChar = new ArrayMap<String, Character[]>();
        Map<String, Integer> sectionInt = new ArrayMap<String, Integer>();

        //リストのソート（名前順）
        Collections.sort(memberList, new Comparator<MemberList>() {
            @Override
            public int compare(MemberList o, MemberList o1) {
                return o.name.compareTo(o1.name);
            }
        });

        sectionChar.put("▶あ行", new Character[]{'あ', 'い', 'う', 'え', 'お','ぁ', 'ぃ', 'ぅ', 'ぇ', 'ぉ','ア', 'イ', 'ウ', 'エ', 'オ'});
        sectionChar.put("▶か行", new Character[]{'か', 'き', 'く', 'け', 'こ','カ', 'キ', 'ク', 'ケ', 'コ'});
        sectionChar.put("▶さ行", new Character[]{'さ', 'し', 'す', 'せ', 'そ','サ', 'シ', 'ス', 'セ', 'ソ'});
        sectionChar.put("▶た行", new Character[]{'た', 'ち', 'つ', 'て', 'と','タ', 'チ', 'ツ', 'テ', 'ト'});
        sectionChar.put("▶な行", new Character[]{'な', 'に', 'ぬ', 'ね', 'の','ナ', 'ニ', 'ヌ', 'ネ', 'ノ'});
        sectionChar.put("▶は行", new Character[]{'は', 'ひ', 'ふ', 'へ', 'ほ','ハ', 'ヒ', 'フ', 'ヘ', 'ホ'});
        sectionChar.put("▶ま行", new Character[]{'ま', 'み', 'む', 'め', 'も','マ', 'ミ', 'ム', 'メ', 'モ'});
        sectionChar.put("▶や行", new Character[]{'や', 'ゆ', 'よ','ヤ', 'ユ', 'ヨ'});
        sectionChar.put("▶ら行", new Character[]{'ら', 'り', 'る', 'れ', 'ろ','ラ', 'リ', 'ル', 'レ', 'ロ'});
        sectionChar.put("▶わ行", new Character[]{'わ', 'を', 'ん','ワ', 'ヲ', 'ン'});
        sectionChar.put("▶A-G", new Character[]{'A', 'B', 'C', 'D', 'E', 'F', 'G'});
        sectionChar.put("▶H-N", new Character[]{'H', 'I', 'J', 'K', 'L', 'M', 'N'});
        sectionChar.put("▶O-U", new Character[]{'O', 'P', 'Q', 'R', 'S', 'T', 'U'});
        sectionChar.put("▶V-Z", new Character[]{'V', 'W', 'X', 'Y', 'Z'});
        sectionChar.put("▶0-9", new Character[]{'0', '1', '2', '3', '4','5','6','7','8','9'});
        sectionChar.put("▶"+getResources().getString(R.string.symbol), new Character[]{'!', '"', '#', '$', '%','&','(',')','*','+',',','-','.','/',':',';','<','=','>','?','!','@','|','{','}'});


        //メンバーリストの上から順番にsectionCharの値に一致する名前がないか調べ、一致する前があったらその名前の上にsectionCharのキー名でsectionを作る
        for (ArrayMap.Entry<String, Character[]> cha : sectionChar.entrySet()) {
            int j = 0;
            int k = 0;
            for (Character a : cha.getValue()) {
                for (int i = 0; i < memberList.size(); i++) {//メンバーリストのループ
                    if (a.equals(Character.toUpperCase((memberList.get(i).name.charAt(0))))) {
                        j++;
                        if (j == 1) k = i;
                    }
                }
            }
            if (j != 0)sections.add(new SectionedGridRecyclerViewAdapter.Section(k, cha.getKey() + " : "  + getResources().getQuantityString(R.plurals.sectionbar_people,j,j)));
        }
        return sections;
    }
}


