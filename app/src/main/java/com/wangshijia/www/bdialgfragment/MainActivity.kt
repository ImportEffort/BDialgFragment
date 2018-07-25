package com.wangshijia.www.bdialgfragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
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
                .addAction("取消", ContextCompat.getColor(this, R.color.selector_dialog_netive),
                        R.drawable.selector_confirm_dialog, NDialogFragment.ActionListener { dialogFragment ->
                    dialogFragment.dismiss()
                })
                .addAction("确定", ContextCompat.getColor(this, R.color.C9), R.drawable.shape_button_red, NDialogFragment.ActionListener { dialogFragment ->
                    dialogFragment.dismiss()
                })
                .setActionWidth(80)
                .setActionHeight(40)
                .show()
    }

}
