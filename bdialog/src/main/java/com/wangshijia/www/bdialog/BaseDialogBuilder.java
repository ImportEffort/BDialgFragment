package com.wangshijia.www.bdialog;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wangshijia.www.bdialog.util.DensityUtils;
import com.wangshijia.www.bdialog.util.UIDisplayHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public abstract class BaseDialogBuilder {


    public void setShowActionBtn(boolean showActionBtn) {
        this.showActionBtn = showActionBtn;
    }

    @IntDef({LEFT, RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionGravity {
    }

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ContentOrientation {
    }

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private NDialogFragment dialogFragment;
    protected AppCompatActivity context;

    protected View contentView;

    protected int height = -1;
    protected int width = -1;
    private boolean showTopCloseImage = false;
    private boolean showActionBtn = true;
    /**
     * 底部按钮大小
     */
    private int actionWidth = -1;
    private int actionHeight = -1;
    private int actionGravity = Gravity.END;

    /**
     * 内容区域LinearLayout 布局方向 ContentOrientation 可选
     */
    private int contentOrientation = VERTICAL;

    private ArrayList<Button> actions = new ArrayList<>();
    private boolean cancelable = true;
    private boolean canceledOnTouchOutside = true;

    public BaseDialogBuilder(AppCompatActivity activity) {
        this.context = activity;
    }

    protected int getContentAreaMaxHeight() {
        if (height == -1) {
            // 屏幕高度的0.85 - 预估的 title 和 action 高度
            return (int) (UIDisplayHelper.getScreenHeight(context) * 0.85) - UIDisplayHelper.dp2px(context, 100);
        }
        return height;
    }

    public NDialogFragment create() {
        dialogFragment = new NDialogFragment();

        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_ndialog_fragment, null);

        TextView titleView = rootView.findViewById(R.id.dialogTitle);
        LinearLayout contentArea = rootView.findViewById(R.id.contentArea);

        LinearLayout actionArea = rootView.findViewById(R.id.actionArea);
        actionArea.setVisibility(showActionBtn ? View.VISIBLE : View.GONE);

        ImageView closeImageBtn = rootView.findViewById(R.id.closeImageBtn);
        closeImageBtn.setVisibility(showTopCloseImage ? View.VISIBLE : View.GONE);
        closeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.dismiss();
            }
        });

        //标题区域
        onCreateTitle(titleView);
        //内容区域
        onCreateContent(contentArea);
        //底部按钮区域
        onCreateAction(actionArea);

        dialogFragment.setContentView(rootView);

        if (width != -1) {
            dialogFragment.setMaxWidth(width);
        }

        if (height != -1) {
            dialogFragment.setMaxHeight(height);
        } else {
        }
        dialogFragment.setCancelable(cancelable);
        dialogFragment.setCanceledOnTouchOutside(canceledOnTouchOutside);
        return dialogFragment;
    }


    private void onCreateAction(LinearLayout actionArea) {

        for (int i = 0; i < actions.size(); i++) {
            if (i > 0 && i < actions.size()) {
                actionArea.addView(createActionContainerSpace(context));
            }

            Button action = actions.get(i);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (actionWidth == -1) {
                params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                params.weight = 1;
            } else {
                actionArea.setGravity(actionGravity);
                params.width = DensityUtils.dip2px(context, actionWidth);
                if (actionHeight != -1) {
                    params.height = DensityUtils.dip2px(context, actionHeight);
                } else {
                    params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                }
            }

            action.setLayoutParams(params);
            actionArea.addView(action);
        }
    }

    private View createActionContainerSpace(Context context) {
        View space = new View(context);
        LinearLayout.LayoutParams spaceLp = new LinearLayout.LayoutParams(DensityUtils.dip2px(context, 20), 0);
        space.setLayoutParams(spaceLp);
        return space;
    }

    public BaseDialogBuilder addAction(String text, int width, int height, @ColorInt int textColor, @DrawableRes int layoutRes, NDialogFragment.ActionListener listener) {
        Button button = new Button(context);
        button.setBackgroundResource(layoutRes);
        button.setTextColor(textColor);
        button.setText(text);

        this.actionWidth = width;
        this.actionHeight = height;

        button.setEnabled(true);
        button.setClickable(true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(dialogFragment);
            }
        });
        actions.add(button);
        return this;
    }

    public BaseDialogBuilder addAction(String text, @ColorInt int textColor, @DrawableRes int backgroundRes, NDialogFragment.ActionListener listener) {
        addAction(text, 40, -2, textColor, backgroundRes, listener);
        return this;
    }

    public void show() {
        NDialogFragment nDialogFragment = create();
        nDialogFragment.show(context);
    }

    /**
     * 创建 title 部分
     *
     * @param titleView
     */
    protected abstract void onCreateTitle(TextView titleView);

    protected abstract void onCreateContent(LinearLayout contentArea);

    /**
     * 自定义文本内容部分，因为有的文本内容可能样式复杂
     *
     * @param contentView
     * @return
     */
    public BaseDialogBuilder setContentView(View contentView) {
        this.contentView = contentView;
        return this;
    }

    public BaseDialogBuilder setMaxHeight(int height) {
        int pxHeight = UIDisplayHelper.dp2px(context, height);
        int contentAreaMaxHeight = getContentAreaMaxHeight();
        if (pxHeight > contentAreaMaxHeight) {
            pxHeight = contentAreaMaxHeight;
        }
        this.height = pxHeight;
        return this;
    }

    public BaseDialogBuilder setMaxWidth(int width) {
        int dpWidth = UIDisplayHelper.dp2px(context, width);
        int screenWidth = UIDisplayHelper.getScreenWidth(context);
        if (dpWidth > screenWidth) {
            width = UIDisplayHelper.getScreenWidth(context);
        }
        this.width = UIDisplayHelper.dp2px(context, width);
        return this;
    }

    public BaseDialogBuilder setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public BaseDialogBuilder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.canceledOnTouchOutside = canceledOnTouchOutside;
        return this;
    }

    public boolean isCanceledOnTouchOutside() {
        return canceledOnTouchOutside;
    }

    /**
     * @param actionHeight dp 值
     * @return BaseDialogBuilder
     */
    public BaseDialogBuilder setActionHeight(int actionHeight) {
        this.actionHeight = actionHeight;
        return this;
    }

    /**
     * @param actionWidth dp 值
     * @return BaseDialogBuilder
     */
    public BaseDialogBuilder setActionWidth(int actionWidth) {
        this.actionWidth = actionWidth;
        return this;
    }

    public BaseDialogBuilder setActionGravity(@ActionGravity int actionGravity) {
        this.actionGravity = actionGravity;
        return this;
    }


    public BaseDialogBuilder setContentOrientation(@ContentOrientation int contentOrientation) {
        this.contentOrientation = contentOrientation;
        return this;
    }

    public int getContentOrientation() {
        return contentOrientation;
    }
}
