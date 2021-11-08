package com.penpendev.pair;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.logging.Handler;

import static com.penpendev.pair.Battle.runnable;
import static com.penpendev.pair.Battle.handler;

public class MyFrameLayout123 extends ConstraintLayout {

    public MyFrameLayout123(Context context){
        super(context);
    }
    public MyFrameLayout123(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFrameLayout123(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        // do what you need to with the event, and then...
        Log.d("kore","dispatchTouchEventでshow");

        FloatingActionButton fa=(FloatingActionButton)findViewById(R.id.battle_start);
        fa.show();

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,5000);

        return super.dispatchTouchEvent(e);
    }

}