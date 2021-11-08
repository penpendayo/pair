package com.penpendev.pair;


import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import static com.penpendev.pair.MainActivity.MemberManagementFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class MemberAdd extends Fragment {
    private EditText editTextName;
    private RadioGroup radioGroupGender;
    private RadioGroup radioGroupLevel;
    private MemberManagement.MemberList memberList;

    private int position = -1;

    //フラグメントのコンストラクタに引数を指定するのはNGらしいけどもう治すのが面倒くさいのでこのままにする
    //メンバーを変更するときのコンストラクタ
    public  MemberAdd( MemberManagement.MemberList memberList,int position) {
        this.memberList=memberList;
        this.position=position;

    }

    //メンバーを新規追加するときのコンストラクタ
    public MemberAdd() {
        memberList=new MemberManagement.MemberList("",0,Gender.OTHER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //アクションバーの設定
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionbar.setCustomView(R.layout.titlebar);

        TextView textview = actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_title);
        if(this.position==-1){
            textview.setText(R.string.memberadd1);
        }else{
            textview.setText(R.string.memberadd2);
        }

        TextView textview1 = actionbar.getCustomView().findViewById(R.id.textView_battle_titlebar_subtitle);
        textview1.setVisibility(View.GONE);
        actionbar.setDisplayShowCustomEnabled(true);

        return inflater.inflate(R.layout.fragment_member_add, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //ビューのIDを取得
        editTextName = (EditText)view.findViewById(R.id.editText);
        radioGroupGender = (RadioGroup)view.findViewById(R.id.radioGroup_gender);
        radioGroupLevel = (RadioGroup)view.findViewById(R.id.radioGroup_level);

        //ボタンが押されたときのリスナーを設定しておく
        view.findViewById(R.id.button_entry_add).setOnClickListener(button1ClickListener);
        view.findViewById(R.id.button_cancel_entry).setOnClickListener(button2ClickListener);
        view.findViewById(R.id.button_delete).setOnClickListener(button3ClickListener);

        //変更の場合、名前などを最初から代入しておく
        if(memberList.name!="") {
            ((Button)view.findViewById(R.id.button_delete)).setVisibility(View.VISIBLE);
            ((Button)view.findViewById(R.id.button_entry_add)).setText(R.string.memberadd_update);
            editTextName.setText(memberList.name);
            switch (memberList.level){
                case 1:
                    radioGroupLevel.check(R.id.radioButton_beginner);
                    break;
                case 2:
                    radioGroupLevel.check(R.id.radioButton_elementary);
                    break;
                case 3:
                    radioGroupLevel.check(R.id.radioButton_intermediate);
                    break;
                case 4:
                    radioGroupLevel.check(R.id.radioButton_advanced);
                    break;
                case 5:
                    radioGroupLevel.check(R.id.radioButton_pro);
                    break;
            }

            switch (memberList.gender) {
                case MAN:
                    radioGroupGender.check(R.id.radioButton_MAN);
                    break;
                case WOMAN:
                    radioGroupGender.check(R.id.radioButton_WOMAN);
                    break;
                case OTHER:
                    radioGroupGender.check(R.id.radioButton_OTHER);
                    break;
            }
        }
    }
    //削除ボタンが押されたとき
    View.OnClickListener button3ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MemberManagementFragment.memberUpdate(position);
            getFragmentManager().popBackStack();//戻る
        }

    };

    //キャンセルボタンが押されたとき
    View.OnClickListener button2ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getFragmentManager().popBackStack();//戻る
        }
    };

    //登録ボタンが押されたとき
    View.OnClickListener button1ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            StringBuilder sb = new StringBuilder();
            if(editTextName.getText().toString().isEmpty()) {
                sb.append(getString(R.string.memberadd_name)).append("\n");
            }
            if(radioGroupGender.getCheckedRadioButtonId()==-1) {
                sb.append(getString(R.string.memberadd_gender)).append("\n");
            }
            if(radioGroupLevel.getCheckedRadioButtonId()==-1) {
                sb.append(getString(R.string.memberadd_level));
            }
            if(!sb.toString().isEmpty()){
                new AlertDialog.Builder(getActivity())//非推奨だけど直すのが面倒なのでそのままにする
                        .setTitle(R.string.memberadd_miss)
                        .setMessage(sb.toString())
                        .setPositiveButton("OK", null)
                        .show();
            }else{//ここに登録の内容

                //ラジオグループの中の何番目がチェックされているか
                RadioButton radioButtonGender = (RadioButton) getActivity().findViewById(radioGroupGender.getCheckedRadioButtonId());
                int radioGroupGenderIndex=radioGroupGender.indexOfChild(radioButtonGender);

                RadioButton radioButtonLevel = (RadioButton) getActivity().findViewById(radioGroupLevel.getCheckedRadioButtonId());
                int radioGroupLevelIndex=radioGroupLevel.indexOfChild(radioButtonLevel);

                //名前の登録
                memberList.name= editTextName.getText().toString();

                //レベルの登録
                switch (radioGroupLevelIndex){
                    case 0://入門
                        memberList.level=1;
                        break;
                    case 1://初級
                        memberList.level=2;
                        break;
                    case 2://中級
                        memberList.level=3;
                        break;
                    case 3://上級
                        memberList.level=4;
                        break;
                    case 4://プロ
                        memberList.level=5;
                        break;
                }

                //ジェンダーの登録
                switch (radioGroupGenderIndex){
                    case 0://男
                        memberList.gender=Gender.MAN;
                        break;
                    case 1://女
                        memberList.gender=Gender.WOMAN;
                        break;
                    case 2://その他
                        memberList.gender=Gender.OTHER;
                        break;
                }

                //positionが-1だったら新規追加
                MemberManagementFragment.memberUpdate(memberList, position);
                getFragmentManager().popBackStack();
            }
        }
    };




}
