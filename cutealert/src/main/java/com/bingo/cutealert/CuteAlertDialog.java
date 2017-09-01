package com.bingo.cutealert;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * ================================
 * 作   者:  Administrator
 * 邮   箱:   zhang-cb@foxmail.com
 * 创建时间:    2017/8/30 14:05
 * 版   本: 1.0
 * 描   述:
 * =================================
 */

public class CuteAlertDialog extends Dialog implements View.OnClickListener{
    private View mDialogView;
    private AnimationSet mModalInAnim;
    private AnimationSet mModalOutAnim;
//    private Animation mOverlayOutAnim;
    private TextView mTitleTextView;
    private TextView mContentTextView;
    private String mTitleText;
    private String mContentText;
    private boolean mShowCancel;
    private boolean mShowConfirm;
    private boolean mShowContent;
    private String mCancelText;
    private String mConfirmText;
    private int mAlertType;
    private FrameLayout mErrorFrame;
    private FrameLayout mSuccessFrame;
    private FrameLayout mProgressFrame;
    private SuccessView mSuccessTip;
    private ImageView mErrorX;
    private View mSuccessLeftMask;
    private View mSuccessRightMask;
    private Drawable mCustomImgDrawable;
    private ImageView mCustomImage;
    private Button mConfirmButton;
    private Button mCancelButton;
    private ProgressHelper mProgressHelper;
    private FrameLayout mWarningFrame;
    private OnCuteClickListener mCancelClickListener;
    private OnCuteClickListener mConfirmClickListener;
    private boolean mCloseFromCancel;

    public static final int NORMAL_TYPE = 0;
    public static final int ERROR_TYPE = 1;
    public static final int SUCCESS_TYPE = 2;
    public static final int WARNING_TYPE = 3;
    public static final int CUSTOM_IMAGE_TYPE = 4;
    public static final int PROGRESS_TYPE = 5;
    private static final long DISMISS_DELAYED = 800;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissWithAnimation();
        }
    };

    private Animation.AnimationListener dialogOutAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.sendEmptyMessageDelayed(0, DISMISS_DELAYED);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };


    public static interface OnCuteClickListener{
        public void onClick(CuteAlertDialog cuteAlertDialog);
    }

    public CuteAlertDialog(Context context) {
        this(context,NORMAL_TYPE);
    }
    public CuteAlertDialog(Context context,int alertType){
        super(context,R.style.alert_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        mProgressHelper = new ProgressHelper(context);
        mAlertType = alertType;
        mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.modal_in);

        mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.modal_out);
        mModalOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialogView.setVisibility(View.GONE);
                mDialogView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCloseFromCancel) {
                            CuteAlertDialog.super.cancel();
                        } else {
                            CuteAlertDialog.super.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        // dialog overlay fade out
//        mOverlayOutAnim = new Animation() {
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                WindowManager.LayoutParams wlp = getWindow().getAttributes();
//                wlp.alpha = 1 - interpolatedTime;
//                getWindow().setAttributes(wlp);
//            }
//        };
//        mOverlayOutAnim.setDuration(150);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cute_alert_dialog);

        mDialogView=getWindow().getDecorView().findViewById(android.R.id.content);
        mTitleTextView=findViewById(R.id.title_text);
        mContentTextView=findViewById(R.id.content_text);
        mErrorFrame=findViewById(R.id.error_frame);
        mErrorX=mErrorFrame.findViewById(R.id.error_x);
        mSuccessFrame=findViewById(R.id.success_frame);
        mProgressFrame = findViewById(R.id.progress_dialog);
        mSuccessTip = mSuccessFrame.findViewById(R.id.success_tip_view);
        mSuccessLeftMask = mSuccessFrame.findViewById(R.id.mask_left);
        mSuccessRightMask = mSuccessFrame.findViewById(R.id.mask_right);
        mCustomImage = findViewById(R.id.custom_image);
        mWarningFrame = findViewById(R.id.warning_frame);
        mConfirmButton = findViewById(R.id.confirm_button);
        mCancelButton = findViewById(R.id.cancel_button);
        mProgressHelper.setProgressWheel((ProgressWheel)findViewById(R.id.progressWheel));

        mConfirmButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        setTitleText(mTitleText);
        setContentText(mContentText);
        setCancelText(mCancelText);
        setConfirmText(mConfirmText);
        changeAlertType(mAlertType, true);

    }

    private void restore () {
        mCustomImage.setVisibility(View.GONE);
        mErrorFrame.setVisibility(View.GONE);
        mSuccessFrame.setVisibility(View.GONE);
        mWarningFrame.setVisibility(View.GONE);
        mProgressFrame.setVisibility(View.GONE);
        mConfirmButton.setVisibility(View.VISIBLE);
        mConfirmButton.setBackgroundResource(R.drawable.blue_button_background);
    }


    private void changeAlertType(int alertType, boolean fromCreate) {
        mAlertType = alertType;
        // call after created views
        if (mDialogView != null) {
            if (!fromCreate) {
                // restore all of views state before switching alert type
                restore();
            }
            switch (mAlertType) {
                case NORMAL_TYPE:
                    mModalInAnim.setAnimationListener(dialogOutAnimationListener);
                    break;
                case ERROR_TYPE:
                    mErrorFrame.setVisibility(View.VISIBLE);
                    //对进入动画进行监听
                    mModalInAnim.setAnimationListener(dialogOutAnimationListener);
                    break;
                case SUCCESS_TYPE:
                    mSuccessFrame.setVisibility(View.VISIBLE);
                    //对进入动画进行监听
                    mModalInAnim.setAnimationListener(dialogOutAnimationListener);
                    break;
                case WARNING_TYPE:
                    mConfirmButton.setBackgroundResource(R.drawable.red_button_background);
                    mWarningFrame.setVisibility(View.VISIBLE);
                    break;
                case CUSTOM_IMAGE_TYPE:
                    mModalInAnim.setAnimationListener(dialogOutAnimationListener);
                    setCustomImage(mCustomImgDrawable);
                    break;
                case PROGRESS_TYPE:
                    mProgressFrame.setVisibility(View.VISIBLE);
                    mConfirmButton.setVisibility(View.GONE);
                    break;
            }
        }
    }

    public int getAlerType () {
        return mAlertType;
    }

    public void changeAlertType(int alertType) {
        changeAlertType(alertType, false);
    }


    public String getTitleText () {
        return mTitleText;
    }

    public CuteAlertDialog setTitleText (String text) {
        mTitleText = text;
        if (mTitleTextView != null && mTitleText != null) {
            mTitleTextView.setText(mTitleText);
        }
        return this;
    }

    public CuteAlertDialog setCustomImage (Drawable drawable) {
        mCustomImgDrawable = drawable;
        if (mCustomImage != null && mCustomImgDrawable != null) {
            mCustomImage.setVisibility(View.VISIBLE);
            mCustomImage.setImageDrawable(mCustomImgDrawable);
        }
        return this;
    }

    public CuteAlertDialog setCustomImage (int resourceId) {
        return setCustomImage(getContext().getResources().getDrawable(resourceId));
    }

    public String getContentText () {
        return mContentText;
    }

    public CuteAlertDialog setContentText (String text) {
        mContentText = text;
        if (mContentTextView != null && mContentText != null) {
            showContentText(true);
            mContentTextView.setText(mContentText);
        }
        return this;
    }

    public boolean isShowCancelButton () {
        return mShowCancel;
    }

    public boolean isShowConfirmButton(){
        return mShowConfirm;
    }

    public CuteAlertDialog showCancelButton (boolean isShow) {
        mShowCancel = isShow;
        if (mCancelButton != null) {
            mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public CuteAlertDialog showConfirmButton(boolean isShow){
        mShowConfirm=isShow;
        if (mConfirmButton != null){
            mConfirmButton.setVisibility(mShowConfirm ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public boolean isShowContentText () {
        return mShowContent;
    }

    public CuteAlertDialog showContentText (boolean isShow) {
        mShowContent = isShow;
        if (mContentTextView != null) {
            mContentTextView.setVisibility(mShowContent ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public String getCancelText () {
        return mCancelText;
    }

    public CuteAlertDialog setCancelText (String text) {
        mCancelText = text;
        if (mCancelButton != null && mCancelText != null) {
            showCancelButton(true);
            mCancelButton.setText(mCancelText);
        }
        return this;
    }

    public String getConfirmText () {
        return mConfirmText;
    }

    public CuteAlertDialog setConfirmText (String text) {
        mConfirmText = text;
        if (mConfirmButton != null && mConfirmText != null) {
            showConfirmButton(true);
            mConfirmButton.setText(mConfirmText);
        }
        return this;
    }

    public CuteAlertDialog setCancelClickListener (OnCuteClickListener listener) {
        mCancelClickListener = listener;
        return this;
    }

    public CuteAlertDialog setConfirmClickListener (OnCuteClickListener listener) {
        mConfirmClickListener = listener;
        return this;
    }

    protected void onStart() {
        mDialogView.startAnimation(mModalInAnim);
//        playAnimation();
    }

    /**
     * The real Dialog.cancel() will be invoked async-ly after the animation finishes.
     */
    @Override
    public void cancel() {
        dismissWithAnimation(true);
    }

    /**
     * The real Dialog.dismiss() will be invoked async-ly after the animation finishes.
     */
    public void dismissWithAnimation() {
        dismissWithAnimation(false);
    }

    private void dismissWithAnimation(boolean fromCancel) {
        mCloseFromCancel = fromCancel;
//        mConfirmButton.startAnimation(mOverlayOutAnim);
        mDialogView.startAnimation(mModalOutAnim);
//        mDialogView.startAnimation(mOverlayOutAnim);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel_button) {
            if (mCancelClickListener != null) {
                mCancelClickListener.onClick(CuteAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        } else if (v.getId() == R.id.confirm_button) {
            if (mConfirmClickListener != null) {
                mConfirmClickListener.onClick(CuteAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        }
    }

    public ProgressHelper getProgressHelper () {
        return mProgressHelper;
    }
}
