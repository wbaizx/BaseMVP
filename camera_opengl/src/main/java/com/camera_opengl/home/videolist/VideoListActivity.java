package com.camera_opengl.home.videolist;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.base.common.base.BaseActivity;
import com.base.common.util.FileUtil;
import com.base.common.util.RouterUtilKt;
import com.camera_opengl.R;
import com.camera_opengl.home.play.PlayActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;

import java.io.File;
import java.util.ArrayList;

public class VideoListActivity extends BaseActivity {

    private VideoListAdapter videoListAdapter = new VideoListAdapter();

    @Override
    protected int getContentView() {
        return R.layout.activity_video_list;
    }

    @Override
    protected void initView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(videoListAdapter);

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoListAdapter.deleteFile();
            }
        });

        findViewById(R.id.allSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoListAdapter.selectAll();
            }
        });

        videoListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (videoListAdapter.isSelectMode()) {
                    videoListAdapter.select(position);
                } else {
                    Intent intent = new Intent(VideoListActivity.this, PlayActivity.class);
                    intent.putExtra("path", videoListAdapter.getData().get(position).getFile().getAbsolutePath());
                    RouterUtilKt.launchActivity(VideoListActivity.this, intent, 1);
                }
            }
        });

        videoListAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                videoListAdapter.setSelectMode(true);
                findViewById(R.id.allSelect).setVisibility(View.VISIBLE);
                findViewById(R.id.delete).setVisibility(View.VISIBLE);

                videoListAdapter.select(position);
                return true;
            }
        });
    }

    @Override
    protected void initData() {
        File file = new File(FileUtil.INSTANCE.getDiskFilePath("VIDEO"));
        File[] files = file.listFiles();
        if (files != null) {
            ArrayList<VideoListAdapter.FileBean> list = new ArrayList<>();
            for (File f : files) {
                list.add(new VideoListAdapter.FileBean(f));
            }
            videoListAdapter.setList(list);
        }
    }
}
