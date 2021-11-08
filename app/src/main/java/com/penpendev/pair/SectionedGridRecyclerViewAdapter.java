package com.penpendev.pair;



import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class SectionedGridRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private static final int SECTION_TYPE = 0;

    private boolean mValid = true;
    private RecyclerView.Adapter mBaseAdapter;
    private SparseArray<Section> mSections = new SparseArray<Section>();
    private RecyclerView mRecyclerView;


    //コンストラクタ 第1:コンテキスト、第2:リサイクラービューのインスタンス、第3:SimpleAdapterのインスタンス、第4:メンバーリスト
    public SectionedGridRecyclerViewAdapter(Context context, RecyclerView recyclerView, RecyclerView.Adapter baseAdapter) {

        mContext = context;
        mBaseAdapter = baseAdapter;//SimpleAdaperのインスタンス
        mRecyclerView = recyclerView;


        //Gridviewのリストが変更されたら何らかの処理を実行するリスナーをセット
        mBaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyDataSetChanged();
            }
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });

        //何カラム分使うか。例えばreturn1なら1つのビューで1カラム分。return 3なら1つのビューで3カラム分使う
        final GridLayoutManager layoutManager = (GridLayoutManager)(mRecyclerView.getLayoutManager());
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (isSectionHeaderPosition(position))? layoutManager.getSpanCount() :1;
            }
        });
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public SectionViewHolder(View view,int mTextResourceid) {
            super(view);
            title = (TextView) view.findViewById(mTextResourceid);
        }
    }

    //一つ一つのビューを返す
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int typeView) {

        if (typeView == SECTION_TYPE) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.section, parent, false);
            return new SectionViewHolder(view,R.id.section_text);
        }else{
            return mBaseAdapter.onCreateViewHolder(parent, typeView -1);
        }
    }
    //onCreateViewHolderで返された一つ一つのビューに対してデータを入れていく
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder sectionViewHolder, int position) {

        if (isSectionHeaderPosition(position)) {
            String headerTitle=String.valueOf(mSections.get(position).title);
            ((SectionViewHolder)sectionViewHolder).title.setText(headerTitle);

            if((headerTitle.contains(mContext.getResources().getString(R.string.memberadd_beginner)))){
                ((SectionViewHolder) sectionViewHolder).title.setBackgroundColor(mContext.getResources().getColor(R.color.beginner));
            }
            if((headerTitle.contains(mContext.getResources().getString(R.string.memberadd_elementary)))){
                ((SectionViewHolder) sectionViewHolder).title.setBackgroundColor(mContext.getResources().getColor(R.color.elementary));
            }
            if((headerTitle.contains(mContext.getResources().getString(R.string.memberadd_intermediate)))){
                ((SectionViewHolder) sectionViewHolder).title.setBackgroundColor(mContext.getResources().getColor(R.color.intermediate));
            }
            if((headerTitle.contains(mContext.getResources().getString(R.string.memberadd_advanced)))){
                ((SectionViewHolder) sectionViewHolder).title.setBackgroundColor(mContext.getResources().getColor(R.color.advanced));
            }
            if((headerTitle.contains(mContext.getResources().getString(R.string.memberadd_pro)))){
                ((SectionViewHolder) sectionViewHolder).title.setBackgroundColor(mContext.getResources().getColor(R.color.pro));
            }


        }else{
            mBaseAdapter.onBindViewHolder(sectionViewHolder,sectionedPositionToPosition(position));
        }
    }

    //positionがヘッダー位置だったらSECTION_TYPEを返す。そうじゃなかったら
    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? SECTION_TYPE
                : mBaseAdapter.getItemViewType(sectionedPositionToPosition(position)) +1 ;
    }


    public static class Section {
        int firstPosition;
        int sectionedPosition;
        CharSequence title;

        public Section(int firstPosition, CharSequence title) {
            this.firstPosition = firstPosition;
            this.title = title;
        }

        public CharSequence getTitle() {
            return title;
        }
    }


    public void setSections(Section[] sections) {
        mSections.clear();


        //たぶんCollection.sortよりArrays.sortのほうが速度が早いからわざわざ変換してるのかも
        Arrays.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section o, Section o1) {
                return (o.firstPosition == o1.firstPosition)
                        ? 0
                        : ((o.firstPosition < o1.firstPosition) ? -1 : 1);
            }
        });

        int offset = 0; // offset positions for the headers we're adding        追加するヘッダーのオフセット位置
        for (Section section : sections) {
            section.sectionedPosition = section.firstPosition + offset;
            mSections.append(section.sectionedPosition, section);
            ++offset;
        }

        notifyDataSetChanged();
    }

    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).firstPosition > position) {
                break;
            }
            ++offset;
        }
        return position + offset;
    }

    //
    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).sectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    //positionがヘッダー位置だったらtrueを返す
    public boolean isSectionHeaderPosition(int position) {
        return mSections.get(position) != null;
    }

    //そのビューのID的なものを返すメソッドらしい。このメソッドの戻り値がリスナーなどの仮引数として使われるっぽい
    //http://outofmem.hatenablog.com/entry/2014/10/29/040510
    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - mSections.indexOfKey(position)
                : mBaseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    //ビューが何個あるか返すメソッド
    @Override
    public int getItemCount() {
        return (mValid ? mBaseAdapter.getItemCount() + mSections.size() : 0);
    }

}