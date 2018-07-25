package com.wangshijia.www.bdialog;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

/**
 * height is wrapContent but limited by maxHeight
 * <p>
 * Created by cgspine on 2017/12/21.
 */

public class WrapContentScrollView extends NestedScrollView {
    private int mMaxHeight = Integer.MAX_VALUE >> 2;

    public WrapContentScrollView(Context context) {
        super(context);
    }

    public WrapContentScrollView(Context context, int maxHeight) {
        super(context);
        mMaxHeight = maxHeight;
    }

    public WrapContentScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapContentScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMaxHeight(int maxHeight) {
        if (mMaxHeight != maxHeight) {
            mMaxHeight = maxHeight;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(mMaxHeight,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
