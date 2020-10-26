package com.camera_opengl.home;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.base.common.util.imageload.LoadImage;
import com.camera_opengl.R;
import com.camera_opengl.home.gl.renderer.filter.FilterType;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FilterDialog {
    private BottomSheetDialog bottomSheetDialog;
    private DialogInterface.OnDismissListener onDismissListener;
    private OnItemClickListener onItemClickListener;

    public void init(Context context) {
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(context);
            RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(context).inflate(R.layout.bottom_filter_view, null);
            bottomSheetDialog.setContentView(recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

            BaseQuickAdapter<FilterType, BaseViewHolder> adapter = new BaseQuickAdapter<FilterType, BaseViewHolder>(R.layout.bottom_filter_item) {
                @Override
                protected void convert(@NotNull BaseViewHolder holder, FilterType type) {
                    LoadImage.INSTANCE.load(type.getPng(), holder.getView(R.id.filterItemImg));
                    holder.setText(R.id.name, type.getName());
                }
            };
            ArrayList<FilterType> filterTypes = FilterType.getList();
            adapter.setList(filterTypes);
            adapter.setOnItemClickListener(new com.chad.library.adapter.base.listener.OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    onItemClickListener.onItemClick(filterTypes.get(position));
                }
            });
            recyclerView.setAdapter(adapter);

            bottomSheetDialog.setOnDismissListener(onDismissListener);
        }

        Window window = bottomSheetDialog.getWindow();
        if (window != null) {
            window.setDimAmount(0f);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public void show() {
        bottomSheetDialog.show();
    }

    public interface OnItemClickListener {
        void onItemClick(FilterType type);
    }
}
