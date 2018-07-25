package com.wangshijia.www.bdialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.v4.app.FragmentActivity;
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

public abstract class BaseDialogBuilder<T extends BaseDialogBuilder> {

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
    protected FragmentActivity context;

    protected View contentView;

    protected int height = -1;
    protected int width = -1;
    private boolean showTopCloseImage = false;
    private boolean showActionBtn = true;
    /**
     * 底部按钮大小
     */
    private int actionWidth = -2;
    private int actionHeight = -2;
    private int actionGravity = Gravity.END;


    /**
     * 内容区域LinearLayout 布局方向 ContentOrientation 可选
     */
    private int contentOrientation = VERTICAL;

    private ArrayList<Button> actions = new ArrayList<>();
    private boolean cancelable = true;
    private boolean canceledOnTouchOutside = true;

    public BaseDialogBuilder(FragmentActivity activity) {
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
        //
        beforeCreateAction();
        //底部按钮区域
        onCreateAction(actionArea);

        dialogFragment.setContentView(rootView);

        if (width != -1) {
            dialogFragment.setMaxWidth(width);
        }

        if (height != -1) {
            dialogFragment.setMaxHeight(height);
        }

        dialogFragment.setCancelable(cancelable);
        dialogFragment.setCanceledOnTouchOutside(canceledOnTouchOutside);
        return dialogFragment;
    }

    /**
     * 创建 点击按钮的前置操作，可以用于子类 Builder 生成 默认 action
     */
    protected void beforeCreateAction() {

    }


    private void onCreateAction(LinearLayout actionArea) {

        for (int i = 0; i < actions.size(); i++) {
            if (i > 0 && i < actions.size()) {
                actionArea.addView(createActionContainerSpace(context));
            }

            Button action = actions.get(i);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (actionWidth == -2) {
                params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                params.weight = 1;
            } else {
                actionArea.setGravity(actionGravity);
                params.width = DensityUtils.dip2px(context, actionWidth);
            }

            if (actionHeight != -2) {
                params.height = DensityUtils.dip2px(context, actionHeight);
            } else {
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
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

    public T addAction(String text, int width, int height, @ColorRes int textColor, @DrawableRes int backgroundRes, NDialogFragment.ActionListener listener) {
        Drawable drawable = context.getDrawable(backgroundRes);
        ColorStateList colorStateList = context.getResources().getColorStateList(textColor);
        addAction(text, width, height, colorStateList, drawable, listener);
        return (T) this;
    }

    public T addAction(String text, @ColorRes int textColor, @DrawableRes int backgroundRes, NDialogFragment.ActionListener listener) {
        addAction(text, actionWidth, actionHeight, textColor, backgroundRes, listener);
        return (T) this;
    }



    public T addAction(String text, ColorStateList colorStateList, Drawable drawabls, NDialogFragment.ActionListener listener) {
        addAction(text, actionWidth, actionHeight, colorStateList, drawabls, listener);
        return (T) this;
    }

    private T addAction(String text, int width, int height, ColorStateList colorStateList, Drawable drawable, NDialogFragment.ActionListener listener) {
        Button button = new Button(context);
        button.setBackground(drawable);
        button.setTextColor(colorStateList);
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
        return (T) this;
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
    public T setContentView(View contentView) {
        this.contentView = contentView;
        return (T) this;
    }

    public T setMaxHeight(int height) {
        int pxHeight = UIDisplayHelper.dp2px(context, height);
        int contentAreaMaxHeight = getContentAreaMaxHeight();
        if (pxHeight > contentAreaMaxHeight) {
            pxHeight = contentAreaMaxHeight;
        }
        this.height = pxHeight;
        return (T) this;
    }

    public T setMaxWidth(int width) {
        int dpWidth = UIDisplayHelper.dp2px(context, width);
        int screenWidth = UIDisplayHelper.getScreenWidth(context);
        if (dpWidth > screenWidth) {
            width = UIDisplayHelper.getScreenWidth(context);
        }
        this.width = UIDisplayHelper.dp2px(context, width);
        return (T) this;
    }

    public T setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return (T) this;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public T setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.canceledOnTouchOutside = canceledOnTouchOutside;
        return (T) this;
    }

    public boolean isCanceledOnTouchOutside() {
        return canceledOnTouchOutside;
    }

    /**
     * @param actionHeight dp 值
     * @return T
     */
    public T setActionHeight(int actionHeight) {
        this.actionHeight = actionHeight;
        return (T) this;
    }

    /**
     * @param actionWidth dp 值
     * @return T
     */
    public T setActionWidth(int actionWidth) {
        this.actionWidth = actionWidth;
        return (T) this;
    }

    public T setActionGravity(@ActionGravity int actionGravity) {
        this.actionGravity = actionGravity;
        return (T) this;
    }


    public T setContentOrientation(@ContentOrientation int contentOrientation) {
        this.contentOrientation = contentOrientation;
        return (T) this;
    }

    public int getContentOrientation() {
        return contentOrientation;
    }
}
