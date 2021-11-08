package com.penpendev.pair;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.penpendev.pair.MainActivity.EntryMemberFragment;
import static com.penpendev.pair.MainActivity.MemberManagementFragment;

/**
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */

//GridViewなビューを返す
public class SimpleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private int mCurrentItemId = 0;
    private ArrayList<MemberManagement.MemberList> memberList;
    private boolean flg;//true:メンバー　false：参加者

    //コンストラクタ
    public SimpleAdapter(Context context, ArrayList<MemberManagement.MemberList> memberList, boolean flg) {
        this.memberList = memberList;
        this.mContext = context;
        this.flg = flg;//true:メンバー　false：参加者
    }

    //ViewHolderの定義
    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView name;
        public final TextView level;
        public final TextView entryCount;
        public final TextView pair;
        public final TextView forced;
        public final ImageView person;
        public final ImageView battleIcon;
        public final ImageView entryIcon;
        public final ConstraintLayout viewGroup;

        public SimpleViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.text_name);
            level = (TextView) view.findViewById(R.id.text_level);
            viewGroup = (ConstraintLayout) view.findViewById(R.id.text_container);
            person = (ImageView) view.findViewById(R.id.imageView3);
            entryCount = (TextView) view.findViewById(R.id.textView100);
            battleIcon = (ImageView) view.findViewById(R.id.imageView_battle);
            entryIcon= (ImageView) view.findViewById(R.id.imageView_entry);
            pair= (TextView) view.findViewById(R.id.textView_pair);
            forced= (TextView) view.findViewById(R.id.textView_forced);

            pair.setVisibility(View.GONE);
            forced.setVisibility(View.GONE);
        }
    }

    //一つ一つのビューを返す
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.list_items, parent, false);

        //ViewHolderを生成
        final SimpleViewHolder holder = new SimpleViewHolder(view);

        //メンバーから呼ばれた場合
        if (flg) {
            holder.battleIcon.setVisibility(View.INVISIBLE);
            holder.entryCount.setVisibility(View.INVISIBLE);
            holder.entryCount.setTypeface(null, Typeface.BOLD);

        }else{//参加者から呼ばれた場合
            holder.entryIcon.setVisibility(View.GONE);
        }

        //ロングクリックイベントを登録
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                //処理はonItemClick()に丸投げ
                onLongItemClick(v, position);
                return true;
            }
        });

        //クリックイベントを登録
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                //処理はonItemClick()に丸投げ
                onItemClick(v, position);
            }
        });
        return holder;
    }

    void onLongItemClick(View view, int position) {
        //アダプタのインスタンスを作る際
        //このメソッドをオーバーライドして
        //クリックイベントの処理を設定する
    }

    void onItemClick(View view, int position) {
        //アダプタのインスタンスを作る際
        //このメソッドをオーバーライドして
        //クリックイベントの処理を設定する
    }


    //onCreateViewHolderで返された一つ一つのビューに対してデータを入れていく
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ((SimpleViewHolder) holder).name.setText(memberList.get(position).name);


        //レベル別に応じて色変え
        switch (memberList.get(position).level) {
            case 1:
                ((SimpleViewHolder) holder).level.setText(R.string.memberadd_beginner);
                ((SimpleViewHolder) holder).level.setBackgroundColor(mContext.getResources().getColor(R.color.beginner));
                break;
            case 2:
                ((SimpleViewHolder) holder).level.setText(R.string.memberadd_elementary);
                ((SimpleViewHolder) holder).level.setBackgroundColor(mContext.getResources().getColor(R.color.elementary));
                break;
            case 3:
                ((SimpleViewHolder) holder).level.setText(R.string.memberadd_intermediate);
                ((SimpleViewHolder) holder).level.setBackgroundColor(mContext.getResources().getColor(R.color.intermediate));
                // drawable.setStroke(2,Color.parseColor("#660000"));
                break;
            case 4:
                ((SimpleViewHolder) holder).level.setText(R.string.memberadd_advanced);
                ((SimpleViewHolder) holder).level.setBackgroundColor(mContext.getResources().getColor(R.color.advanced));

                break;
            case 5:
                ((SimpleViewHolder) holder).level.setText(R.string.memberadd_pro);
                ((SimpleViewHolder) holder).level.setBackgroundColor(mContext.getResources().getColor(R.color.pro));

                break;
        }

        //男なら青、女なら赤
        switch (memberList.get(position).gender) {
            case MAN:
                ((SimpleViewHolder) holder).person.setColorFilter(Color.BLUE);
//                ((SimpleViewHolder) holder).name.setTextColor(Color.parseColor("#0000f0"));
                break;
            case WOMAN:
                ((SimpleViewHolder) holder).person.setColorFilter(Color.RED);
//                ((SimpleViewHolder) holder).name.setTextColor(Color.parseColor("#f00000"));
                break;
            case OTHER:
                ((SimpleViewHolder) holder).person.setColorFilter(Color.BLACK);
                break;
        }

        GradientDrawable drawable;
        drawable=(GradientDrawable) ((SimpleViewHolder) holder).viewGroup.getBackground().getCurrent();
        if (flg) {//メンバーの場合のみ実行

            //参加？非参加？
            if (memberList.get(position).entry) {//参加
                drawable.setColor(mContext.getResources().getColor(R.color.entrycolor));
                ((SimpleViewHolder) holder).entryCount.setVisibility(View.VISIBLE);
                ((SimpleViewHolder) holder).entryCount.setText(R.string.entrynow);
                ((SimpleViewHolder) holder).entryIcon.setVisibility(View.VISIBLE);//手のアイコンを表示
            } else {//非参加
                //drawable.setStroke(3,Color.parseColor("#ff0000"));
                drawable.setColor(Color.WHITE);
                ((SimpleViewHolder) holder).entryCount.setVisibility(View.INVISIBLE);
                ((SimpleViewHolder) holder).entryIcon.setVisibility(View.GONE);//手のアイコンを非表示
            }
        } else {//参加者のみ実行
            //drawable.setColor(Color.WHITE);
            if(EntryMemberFragment.pairflg.size()==0){
                drawable.setColor(Color.WHITE);
            }else{
                for(int a:EntryMemberFragment.pairflg){
                    if(memberList.get(position).id==a) {
                        //((SimpleViewHolder) holder).viewGroup.setBackgroundColor(mContext.getResources().getColor(R.color.elementary));
                        drawable.setColor(mContext.getResources().getColor(R.color.entrycolor));
                        break;
                    }else{
                        drawable.setColor(Color.WHITE);
                    }
                }
            }



            //レベルを非表示
            ((SimpleViewHolder) holder).level.setVisibility(View.INVISIBLE);

            ((SimpleViewHolder) holder).pair.setVisibility(View.GONE);
            for (int i = 0; i < MemberManagement.MemberList.pair.size(); i++) {//ペアの数だけ繰り返し
                for (int b : MemberManagement.MemberList.pair.get(i)) {//一つ一つのペアの中身を繰り返し
                    if (b == memberList.get(position).id) {
                        ((SimpleViewHolder) holder).pair.setText("ペア" + String.valueOf(i + 1) + "@x" + String.valueOf(MemberManagement.MemberList.pair.get(i).length));//自分に色を設定する
                        ((SimpleViewHolder) holder).pair.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
           /* //ペアの設定
            if (memberList.get(position).pair.size() != 0) {//ペアが設定されているか確認する
                ((SimpleViewHolder) holder).pair.setText("ペア"+memberList.get(position).pair.keyAt(0));//自分に色を設定する

                ((SimpleViewHolder) holder).pair.setVisibility(View.VISIBLE);
            }else{
                ((SimpleViewHolder) holder).pair.setVisibility(View.GONE);
            }*/

            //参加回数
            ((SimpleViewHolder) holder).entryCount.setText("x" + String.valueOf(memberList.get(position).entryTimes));

            if (memberList.get(position).forcedEntry == true) {
                ((SimpleViewHolder) holder).forced.setText("絶対参加");
                ((SimpleViewHolder) holder).forced.setVisibility(View.VISIBLE);
            }else if(memberList.get(position).forcedBreak == true){
                ((SimpleViewHolder) holder).forced.setText("休憩");
                ((SimpleViewHolder) holder).forced.setVisibility(View.VISIBLE);
            }else{
                ((SimpleViewHolder) holder).forced.setVisibility(View.GONE);
            }

        }
    }


    @Override
    public int getItemCount() {
        return memberList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}