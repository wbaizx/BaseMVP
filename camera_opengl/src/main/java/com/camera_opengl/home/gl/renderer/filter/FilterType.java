package com.camera_opengl.home.gl.renderer.filter;

import com.camera_opengl.R;

import java.util.ArrayList;

public enum FilterType {
    NONE("无", "none", R.mipmap.filter_no, 0),
    BEAUTY("美颜", "beauty", R.mipmap.filter_beauty_white, R.drawable.lut_white),
    WHITENING("美白", "whitening", R.mipmap.filter_beauty_white, R.drawable.lut_white),
    ROMANTIC("浪漫", "romantic", R.mipmap.filter_romantic, R.drawable.lut_romantic),
    FRESH("清新", "fresh", R.mipmap.filter_fresh, R.drawable.lut_fresh),
    AESTHETICISM("唯美", "aestheticism", R.mipmap.filter_aestheticism, R.drawable.lut_aestheticism),
    PINK("粉嫩", "pink", R.mipmap.filter_pink, R.drawable.lut_pink),
    NOSTALGIA("怀旧", "nostalgia", R.mipmap.filter_nostalgia, R.drawable.lut_nostalgia),
    BLUES("蓝调", "blues", R.mipmap.filter_blues, R.drawable.lut_bluse),
    COOL("清凉", "cool", R.mipmap.filter_cool, R.drawable.lut_cool),
    JAPANESE("日系", "japanese", R.mipmap.filter_japanese, R.drawable.lut_japanese),
    GREY("灰色", "grey", R.mipmap.filter_no, 0),
    LIGHT("提亮", "light", R.mipmap.filter_no, 0),
    ;

    private String name;
    private String id;
    private int png;
    private int lut;

    FilterType(String name, String id, int png, int lut) {
        this.name = name;
        this.id = id;
        this.png = png;
        this.lut = lut;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getPng() {
        return png;
    }

    public int getLut() {
        return lut;
    }

    public static ArrayList<FilterType> getList() {
        ArrayList<FilterType> list = new ArrayList<>();
        list.add(NONE);
        list.add(BEAUTY);
        list.add(WHITENING);
        list.add(ROMANTIC);
        list.add(FRESH);
        list.add(AESTHETICISM);
        list.add(PINK);
        list.add(NOSTALGIA);
        list.add(BLUES);
        list.add(COOL);
        list.add(JAPANESE);
        list.add(GREY);
        list.add(LIGHT);
        return list;
    }

    public static BaseFilter getFilter(FilterType type) {
        if (type == NONE) {
            return new NoneFilter(type);
        } else if (type == GREY) {
            return new GreyFilter(type);
        } else if (type == LIGHT) {
            return new LightFilter(type);
        } else {
            return new LutFilter(type);
        }
    }
}
