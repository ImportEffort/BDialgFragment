package com.wangshijia.www.bdialog;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wangshijia.www.bdialog.util.DensityUtils;
import com.wangshijia.www.bdialog.util.UIDisplayHelper;

public class NDialogFragment extends DialogFragment {


    private boolean canceledOnTouchOutside;

    public NDialogFragment() {
    }

    private View contentView;

    private int maxWidth = -1;
    private int maxHeight = -1;

    public void setContentView(View view) {
        this.contentView = view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        if (maxWidth == -1) {
            maxWidth = (int) (UIDisplayHelper.getScreenWidth(getContext()) * 0.8f);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置宽度为屏宽、靠近屏幕底部。
        final Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.color.dialog_transparent);
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.width = maxWidth;
            if (maxHeight != -1) {
                wlp.height = maxHeight;
            } else {
                wlp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
        }
    }

    public NDialogFragment show(@NonNull AppCompatActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        ft.commit();
        show(fragmentManager, "dialog");
        fragmentManager.executePendingTransactions();
        return this;
    }


    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setMaxHeight(int height) {
        this.maxHeight = height;
    }

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.canceledOnTouchOutside = canceledOnTouchOutside;
    }


    public static class MessageDialogBuilder extends BaseDialogBuilder {

        /**
         * 标题默认文字大小
         */
        private int contentTextSize = 14;
        private int contentTextColor;
        private int titleTextSize = 18;
        private String contentText;
        private String title;

        private int contentTextGravity = Gravity.LEFT;

        protected ActionListener positiveListener;


        public MessageDialogBuilder(AppCompatActivity activity) {
            super(activity);
            init(activity);
        }

        private void init(AppCompatActivity activity) {
            contentTextColor = ContextCompat.getColor(activity, R.color.dialog_content);
        }

        @Override
        protected void onCreateTitle(TextView titleView) {
            if (hasTitle()) {
                titleView.setText(title);
                titleView.setTextSize(titleTextSize);
                titleView.setVisibility(View.VISIBLE);
            } else {
                titleView.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onCreateContent(LinearLayout contentArea) {
            if (contentView == null) {
                contentView = generalNormalView();
            }
            WrapContentScrollView wrapContentScrollView = new WrapContentScrollView(context);
            wrapContentScrollView.setVerticalScrollBarEnabled(false);
            wrapContentScrollView.setMaxHeight(getContentAreaMaxHeight());
            wrapContentScrollView.addView(contentView);
            contentArea.addView(wrapContentScrollView);
        }

        private TextView generalNormalView() {
            TextView view = new TextView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
            view.setTextColor(ContextCompat.getColor(context, R.color.dialog_content));
            view.setTextSize(contentTextSize);
            view.setTextColor(contentTextColor);
            view.setGravity(contentTextGravity);

            if (!TextUtils.isEmpty(contentText)) {
                view.setText(contentText);
            }
            return view;
        }

        public MessageDialogBuilder setContentTextColor(@ColorRes int contentTextColor) {
            this.contentTextColor = ContextCompat.getColor(context, contentTextColor);
            return this;
        }

        private boolean hasTitle() {
            return title != null && !title.isEmpty();
        }

        public MessageDialogBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public MessageDialogBuilder setContentText(String text) {
            this.contentText = text;
            return this;
        }

        public MessageDialogBuilder setContentTextSize(int contentTextSize) {
            this.contentTextSize = contentTextSize;
            return this;
        }


        public MessageDialogBuilder setTitleTextSize(int titleTextSize) {
            this.titleTextSize = titleTextSize;
            return this;
        }


        public MessageDialogBuilder setContentTextGravity(int contentTextGravity) {
            this.contentTextGravity = contentTextGravity;
            return this;
        }

        public MessageDialogBuilder addConfirmBtnClickListener(ActionListener confirmClick){
            this.positiveListener = confirmClick;
            return this;
        }
    }

    public static class ProgressBarDialogBuilder extends BaseDialogBuilder {
        private int contentTextSize = 14;

        public ProgressBarDialogBuilder(AppCompatActivity activity) {
            super(activity);
            init();
        }

        private void init() {
            setShowActionBtn(false);
        }

        @Override
        protected void onCreateTitle(TextView titleView) {

        }

        @Override
        protected void onCreateContent(LinearLayout contentArea) {
            int contentOrientation = getContentOrientation();
            contentArea.setOrientation(contentOrientation == BaseDialogBuilder.VERTICAL ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
            createProgressBar(contentArea);
            createText(contentArea);
        }

        private void createText(LinearLayout contentArea) {
            TextView textView = new TextView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (getContentOrientation() == BaseDialogBuilder.HORIZONTAL) {
                params.setMargins(DensityUtils.dip2px(context, 15), 0, 0, 0);
                params.gravity = Gravity.CENTER_VERTICAL;
            } else {
                params.setMargins(0, DensityUtils.dip2px(context, 15), 0, 0);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                setMaxWidth(180);
                setMaxHeight(180);
            }
            textView.setTextSize(contentTextSize);
            textView.setText("加载中...");
            textView.setLayoutParams(params);
            contentArea.addView(textView);
        }

        private void createProgressBar(LinearLayout contentArea) {
            ProgressBar progressBar = new ProgressBar(context);
            contentArea.addView(progressBar);
        }
    }

    public static class ConfirmDialogBuilder extends MessageDialogBuilder {

        public ConfirmDialogBuilder(AppCompatActivity activity) {
            super(activity);
            setActionHeight(40);
        }

        @Override
        protected void beforeCreateAction() {
            super.beforeCreateAction();
            int[][] states = new int[2][];
            states[0] = new int[]{android.R.attr.state_pressed};
            states[1] = new int[]{};
            int[] negativeColors = new int[]{0xfff5f5f5, 0xff8997A5};
            ColorStateList negativeColorList = new ColorStateList(states, negativeColors);
            Drawable negativeDrawable = context.getDrawable(R.drawable.selector_confirm_negative);
            addAction("取消", negativeColorList, negativeDrawable, new ActionListener() {
                @Override
                public void onClick(NDialogFragment dialogFragment) {
                    dialogFragment.dismiss();
                }
            });
            int[] positiveColors = new int[]{0xffffffff, 0xffffffff};
            ColorStateList positiveColorList = new ColorStateList(states, positiveColors);
            Drawable positiveDrawable = context.getDrawable(R.drawable.selector_confirm_positive);
            addAction("确认", positiveColorList, positiveDrawable, new ActionListener() {
                @Override
                public void onClick(NDialogFragment dialogFragment) {
                    if (positiveListener != null) {
                        positiveListener.onClick(dialogFragment);
                    }
                }
            });
        }
    }

    public interface ActionListener {
        void onClick(NDialogFragment dialogFragment);
    }
}
