package com.camera_opengl.videolist;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.base.common.base.BaseActivity;
import com.camera_opengl.R;

//RouteString.VIDEO_LIST
//RouteString.isNeedLogin
@Route(path = "/camera/video_list", name = "组件化camera 视频列表查看页", extras = -1)
public class VideoListActivity extends BaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_video_list;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}
