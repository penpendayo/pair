package com.penpendev.pair;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.penpendev.pair.MainActivity.BattleFragment;
import static com.penpendev.pair.MainActivity.PACKAGE_NAME;

public class BattleAdapter extends RecyclerView.Adapter<BattleAdapter.ViewHolder> {

    private final Context mContext;
    ArrayList<MemberManagement.MemberList[]> battleMember;
    ArrayList<MemberManagement.MemberList> breakMember;
    RecyclerView recyclerView;
    SharedPreferences sp;//設定から読み込むための変数
    int textsize;

    //コンストラクタ
    public BattleAdapter(Context context,
                         RecyclerView RV, ArrayList<MemberManagement.MemberList[]> battleMember,
                         ArrayList<MemberManagement.MemberList> breakMember) {
        this.mContext = context;
        this.battleMember = battleMember;
        this.recyclerView=RV;
        this.breakMember = breakMember;
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        this.textsize=Integer.parseInt(sp.getString("battle_textsize",mContext.getResources().getString(R.string.battle_textsize_default)));


        //何カラム分使うか。例えばreturn1なら1つのビューで1カラム分。return 3なら1つのビューで3カラム分使う
        final GridLayoutManager layoutManager = (GridLayoutManager) (recyclerView.getLayoutManager());
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
            @Override
            public int getSpanSize ( int position){
                return BattleFragment.battleAdapter.battleMember.size()==position
                        ? layoutManager.getSpanCount()
                        : 1 ;
            }
        });
    }

    //2つのアダプターの抽象アダプター
    abstract static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View view) {
            super(view);
        }

        public static ViewHolder create(View v, int viewType) {
            return viewType == R.layout.battle_list
                    ? new BattleViewHolder(v)
                    :new BreakViewHolder(v);
        }
    }


    //ViewHolderの定義1つ目（試合を表示するやつ）
    public static class BattleViewHolder extends ViewHolder {
        public final TextView coat;
        public final LinearLayout LL_left;
        public final LinearLayout LL_right;

        public BattleViewHolder(View view) {
            super(view);
            coat = (TextView) view.findViewById(R.id.coat);
            LL_left=(LinearLayout) view.findViewById(R.id.linearLayout_battle).findViewById(R.id.LL_left);
            LL_right=(LinearLayout) view.findViewById(R.id.linearLayout_battle).findViewById(R.id.LL_right);
        }
    }

    //ViewHolderの定義2つ目（休憩メンバーを表示するやつ）
    public static class BreakViewHolder extends ViewHolder {
        public final LinearLayout LL;
        public final ConstraintLayout CL;
        public BreakViewHolder(View view) {
            super(view);
            LL=(LinearLayout)view.findViewById(R.id.linearLayout_break);
            CL=(ConstraintLayout)view.findViewById(R.id.CL);
        }
    }

    //pxからdpに変換する関数
    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    /**
     * dpからpixelへの変換
     * @param dp
     * @return float pixel
     */
    public float convertDp2Px(float dp){
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        return dp * metrics.density;
    }


    //一つ一つのビューを返す
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        final ViewHolder holder;
        switch (viewType){
            case R.layout.battle_break:
                view = LayoutInflater.from(mContext).inflate(R.layout.battle_break, parent, false);
                holder = new BreakViewHolder(view);
                return holder;
            case R.layout.battle_list:
                view = LayoutInflater.from(mContext).inflate(R.layout.battle_list, parent, false);
                holder = new BattleViewHolder(view);
                return holder;
        }
        //例外処理
        view = LayoutInflater.from(mContext).inflate(R.layout.battle_list, parent, false);
        holder = new BattleViewHolder(view);
        return  holder;
    }

    void onLongItemClick(View view, int position) {
        //アダプタのインスタンスを作る際
        //このメソッドをオーバーライドして
        //クリックイベントの処理を設定する
    }

    void onItemClick(View view, int coatPosition,int coatNumber) {
        //アダプタのインスタンスを作る際
        //このメソッドをオーバーライドして
        //クリックイベントの処理を設定する
    }

    //onCreateViewHolderで返された一つ一つのビューに対してデータを入れていく
    public void onBindViewHolder(ViewHolder holder, final int position) {
        int coatPosition=0;

        switch (holder.getItemViewType()){
            case R.layout.battle_break:
               // ((TextView)((BreakViewHolder)holder).CL.findViewById(R.id.coat)).setTextSize(50);

                ((BreakViewHolder)holder).LL.removeAllViews();
                //LinearLayout.MarginLayoutParams ML=new LinearLayout.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                //ML.setMargins(0,(int)convertDp2Px(80),0,(int)convertDp2Px(80));


                if(breakMember.size()==0){//休憩メンバーが0人だったら非表示にする
                    ((BreakViewHolder)holder).CL.setVisibility(View.GONE);
                }else{
                    ((BreakViewHolder)holder).CL.setVisibility(View.VISIBLE);
                    for(MemberManagement.MemberList m:breakMember){
                        final int coatPosition1=coatPosition;
                        TextView tview=new TextView(mContext);
                        tview.setText(m.name);
                        //tview.setLayoutParams(ML);
                        tview.setPadding(0,(int)convertDp2Px(10),0,(int)convertDp2Px(10));
                        tview.setGravity(Gravity.CENTER);
                        tview.setTextSize(textsize);
                        tview.setTypeface(Typeface.DEFAULT_BOLD);
                        tview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onItemClick(v,coatPosition1,position);
                            }
                        });
                        coatPosition++;

                        switch (m.gender) {
                            case MAN:
                                tview.setTextColor(Color.parseColor("#0000f0"));
                                break;
                            case WOMAN:
                                tview.setTextColor(Color.parseColor("#f00000"));
                                break;
                            case OTHER:
                                tview.setTextColor(Color.parseColor("#000000"));
                                break;
                        }
                        ((BreakViewHolder)holder).LL.addView(tview);
                    }
                }
                break;
            case R.layout.battle_list:
                ((BattleViewHolder)holder).coat.setText(mContext.getString(R.string.coat)+ String.valueOf(position + 1));
                ((BattleViewHolder)holder).LL_left.removeAllViews();
                ((BattleViewHolder)holder).LL_right.removeAllViews();

                LinearLayout.MarginLayoutParams ML=new LinearLayout.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                ML.setMargins(8,(int)convertDp2Px(8),8,(int)convertDp2Px(8));

                //final ColorStateList stateList = new ColorStateList(new int[][]{{}}, new int[]{Color.parseColor("#33ffffbb")});

                int i = 0;
                for (MemberManagement.MemberList a : battleMember.get(position)) {
                    TextView tview=new TextView(mContext);
                   // tview.setLayoutParams(ML);
                    //tview.setBackgroundTintList(stateList);
                    tview.setText(a.name);
                    tview.setTextSize(textsize);
                    tview.setPadding(0,(int)convertDp2Px(10),0,(int)convertDp2Px(10));
                    tview.setGravity(Gravity.CENTER);
                    tview.setTypeface(Typeface.DEFAULT_BOLD);
                    final int aa=i;
                    tview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemClick(v,aa,position);
                        }
                    });
                    switch (a.gender) {
                        case MAN:
                            tview.setTextColor(Color.parseColor("#0000f0"));
                            break;
                        case WOMAN:
                            tview.setTextColor(Color.parseColor("#f00000"));
                            break;
                        case OTHER:
                            tview.setTextColor(Color.parseColor("#000000"));
                            break;
                    }

                    switch (i%2){
                        case 0:
                            ((BattleViewHolder)holder).LL_left.addView(tview);
                            break;
                        case 1:
                            ((BattleViewHolder)holder).LL_right.addView(tview);
                            break;
                    }

                    i++;
                }

                break;
        }
    }


    @Override
    public int getItemCount() {
        return battleMember.size()+1;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        if(battleMember.size()==position){
            return R.layout.battle_break;
        }else{
            return R.layout.battle_list;
        }

    }
}
