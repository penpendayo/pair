package com.penpendev.pair;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class BattleDialogFlagment extends DialogFragment {



    public interface OnButtonClickListener {
        public void onPositiveClick();
        public void onNegativeClick();
    }



    public static BattleDialogFlagment newInstance(Fragment fragment) {
        BattleDialogFlagment dialog = new BattleDialogFlagment();
        dialog.setTargetFragment(fragment, 0);
        return dialog;
    }

    // ダイアログが生成された時に呼ばれるメソッド ※必須
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        // 表示する文章設定
        dialogBuilder.setMessage("試合を開始しますか？");

        // OKボタン作成
        dialogBuilder.setPositiveButton("はい", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                OnButtonClickListener listener;
                try {
                    listener = (OnButtonClickListener) BattleDialogFlagment.this.getTargetFragment();
                } catch (ClassCastException e) {
                    // リスナーなし
                    return;
                }

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        listener.onPositiveClick();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        listener.onNegativeClick();
                        break;
                }
            }
        });

        // NGボタン作成
        dialogBuilder.setNegativeButton("いいえ", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // dialogBulderを返す
        return dialogBuilder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //ダイアログの位置
        //getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

        View v = inflater.inflate(R.layout.fragment_member_add, container, false);


        //ダイアログの色
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#eeffffff")));
        //return super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }
}