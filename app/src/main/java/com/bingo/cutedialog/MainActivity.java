package com.bingo.cutedialog;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bingo.cutealert.CuteAlertDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void baseDialog(View view){
        new CuteAlertDialog(this)
                .setTitleText("你是一只鱼")
                .setContentText("水里的空气 是你小心眼和坏脾气")
                .show();
    }
    public void successDialog(View view){
        new CuteAlertDialog(this,CuteAlertDialog.SUCCESS_TYPE)
                .setTitleText("哈哈")
                .setContentText("我们在一起啦！")
                .show();
    }
    public void failDialog(View view){
        new CuteAlertDialog(this,CuteAlertDialog.ERROR_TYPE)
                .setTitleText("哄我")
                .setContentText("你个笨蛋...")
                .show();
    }
    public void warnDialog(View view){
        new CuteAlertDialog(this,CuteAlertDialog.WARNING_TYPE)
                .setTitleText("你好丑")
                .setContentText("都是我不好，抱抱!")
                .setConfirmText("白眼")
                .setConfirmClickListener(new CuteAlertDialog.OnCuteClickListener() {
                    @Override
                    public void onClick(CuteAlertDialog cuteAlertDialog) {
                        cuteAlertDialog.setTitleText("告诉你个秘密")
                                .setContentText("你刚好丑成了我喜欢的样子。")
                                .setConfirmText("莫名感动")
                                .setConfirmClickListener(null)
                                .changeAlertType(CuteAlertDialog.SUCCESS_TYPE);
                    }
                }).show();
    }
    public void warningCancelDialog(View view){
        new CuteAlertDialog(this, CuteAlertDialog.WARNING_TYPE)
                .setTitleText("确定吗?")
                .setContentText("问你最后一个问题")
                .setCancelText("爱过")
                .setConfirmText("回头")
                .showCancelButton(true)
                .setCancelClickListener(new CuteAlertDialog.OnCuteClickListener() {
                    @Override
                    public void onClick(CuteAlertDialog sDialog) {
                        // reuse previous dialog instance, keep widget user state, reset them if you need
                        sDialog.setTitleText("呜呜!")
                                .setContentText("一生所爱!")
                                .setConfirmText("see you again")
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(null)
                                .changeAlertType(CuteAlertDialog.ERROR_TYPE);
                    }
                })
                .setConfirmClickListener(new CuteAlertDialog.OnCuteClickListener() {
                    @Override
                    public void onClick(CuteAlertDialog sDialog) {
                        sDialog.setTitleText("咬一口!")
                                .setContentText("给你个甜甜的微笑 :)")
                                .setConfirmText("爱你")
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(null)
                                .changeAlertType(CuteAlertDialog.SUCCESS_TYPE);
                    }
                })
                .show();
    }
    public void customDialog(View view){
        new CuteAlertDialog(this, CuteAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("宝宝")
                .setContentText("你是最棒哒...")
                .setCustomImage(R.drawable.custom_img)
                .show();
    }

    private int i = -1;
    public void progressDialog(View view){
        final CuteAlertDialog pDialog =new CuteAlertDialog(this,CuteAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("思念变成海...");
        pDialog.show();
        new CountDownTimer(500 * 7, 500) {
            public void onTick(long millisUntilFinished) {
                // you can change the progress bar color by ProgressHelper every 800 millis
                i++;
                switch (i){
                    case 0:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.blue_btn_bg_color));
                        break;
                    case 1:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_deep_teal_50));
                        break;
                    case 2:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.success_stroke_color));
                        break;
                    case 3:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_deep_teal_20));
                        break;
                    case 4:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.material_blue_grey_80));
                        break;
                    case 5:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.warning_stroke_color));
                        break;
                    case 6:
                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.success_stroke_color));
                        break;
                }
            }

            public void onFinish() {
                i = -1;
                pDialog.setTitleText("好久不见!")
                        .setConfirmText("你也是")
                        .changeAlertType(CuteAlertDialog.SUCCESS_TYPE);
            }
        }.start();
    }
}
