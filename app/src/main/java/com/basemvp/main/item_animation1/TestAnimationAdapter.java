package com.basemvp.main.item_animation1;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import jp.wasabeef.recyclerview.adapters.AnimationAdapter;

public class TestAnimationAdapter extends AnimationAdapter {

    public TestAnimationAdapter(RecyclerView.Adapter adapter) {
        super(adapter);
    }

    @Override
    protected Animator[] getAnimators(View view) {
        view.setPivotX(1000);
        view.setPivotY(1000);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 320, 360f);
        return new ObjectAnimator[]{rotation};
    }
}