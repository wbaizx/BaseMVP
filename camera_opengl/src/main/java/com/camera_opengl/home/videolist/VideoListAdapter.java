package com.camera_opengl.home.videolist;

import com.base.common.util.imageload.LoadImage;
import com.camera_opengl.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Iterator;

public class VideoListAdapter extends BaseQuickAdapter<VideoListAdapter.FileBean, BaseViewHolder> {
    private static final String TAG = "VideoListAdapter";

    private boolean selectMode = false;
    private boolean allSelect = false;
    private int selectNum = 0;

    public boolean isSelectMode() {
        return selectMode;
    }

    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
    }

    public VideoListAdapter() {
        super(R.layout.video_list_item);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, FileBean bean) {
        holder.setGone(R.id.videoItemSelect, !bean.isSelect())
                .setText(R.id.videoItemTitle, bean.file.getName());

        LoadImage.INSTANCE.load(bean.file, holder.getView(R.id.videoItemImg));
    }

    public void select(int position) {
        FileBean bean = getData().get(position);
        bean.setSelect(!bean.isSelect());
        if (bean.isSelect()) {
            selectNum++;
        } else {
            selectNum--;
        }
        checkIsAllSelect();
        notifyItemChanged(position);
    }

    public void selectAll() {
        allSelect = !allSelect;
        for (VideoListAdapter.FileBean bean : getData()) {
            bean.setSelect(allSelect);
        }
        if (allSelect) {
            selectNum = getData().size();
        } else {
            selectNum = 0;
        }
        notifyDataSetChanged();
    }

    private void checkIsAllSelect() {
        allSelect = selectNum == getData().size();
    }

    public void deleteFile() {
        Iterator<FileBean> iterator = getData().iterator();
        while (iterator.hasNext()) {
            FileBean bean = iterator.next();
            if (bean.isSelect()) {
                bean.file.delete();
                iterator.remove();
            }
        }
        notifyDataSetChanged();
    }

    public static class FileBean {
        private boolean isSelect = false;

        private File file;

        public File getFile() {
            return file;
        }

        public FileBean(File file) {
            this.file = file;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }
    }
}
