package com.camera_opengl.home.camera;

import android.util.Size;

import java.util.Comparator;

/**
 * 比较最接近的宽高
 */
public class CompareSize implements Comparator<Size> {
    private int expectWidth;
    private int expectHeight;

    public CompareSize(int expectWidth, int expectHeight) {
        this.expectWidth = expectWidth;
        this.expectHeight = expectHeight;
    }

    @Override
    public int compare(Size lhs, Size rhs) {
        if (Math.abs(lhs.getWidth() - expectWidth) > Math.abs(rhs.getWidth() - expectWidth)) {
            return 1;
        } else if (Math.abs(lhs.getWidth() - expectWidth) == Math.abs(rhs.getWidth() - expectWidth)) {
            if (lhs.getWidth() > rhs.getWidth()) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return -1;
        }
    }
}
