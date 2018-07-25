package com.wangshijia.www.bdialgfragment

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.Button
import com.wangshijia.www.bdialog.BaseDialogBuilder
import com.wangshijia.www.bdialog.NDialogFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.testBtn).setOnClickListener { showTestDialog() }
        findViewById<Button>(R.id.progressBtn).setOnClickListener { showProgressDialog() }
        findViewById<Button>(R.id.progressBtnV).setOnClickListener { showProgressDialogV() }
        findViewById<Button>(R.id.confirmDialog).setOnClickListener { showConfirmDialog() }
    }

    private fun showConfirmDialog() {
        NDialogFragment.ConfirmDialogBuilder(this)
                .setContentText("我是确认对话框")
                .setContentTextColor(R.color.C1)
                .setContentTextSize(17)
                .setContentTextGravity(Gravity.CENTER)
                .addConfirmBtnClickListener { dialogFragment -> dialogFragment.dismiss() }
                .show()
    }

    private fun showProgressDialogV() {
        NDialogFragment.ProgressBarDialogBuilder(this)
                .setContentOrientation(BaseDialogBuilder.VERTICAL)
                .show()
    }

    private fun showProgressDialog() {
        NDialogFragment.ProgressBarDialogBuilder(this)
                .setContentOrientation(BaseDialogBuilder.HORIZONTAL)
                .show()
    }

    private fun showTestDialog() {
        NDialogFragment.MessageDialogBuilder(this)
                .setTitle("我是标题")
                .setContentText(getString(R.string.long_text))
                .setContentTextSize(14)
                .addAction("取消", R.color.selector_dialog_netive,
                        R.drawable.selector_confirm_negative, NDialogFragment.ActionListener { dialogFragment ->
                    dialogFragment.dismiss()
                })
                .addAction("确定", R.color.C9, R.drawable.shape_button_red, NDialogFragment.ActionListener { dialogFragment ->
                    dialogFragment.dismiss()
                })
                .setActionWidth(80)
                .setActionHeight(40)
                .show()
    }

}
