package com.hmzl.aliouswang.yygallery;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

/**
 * Created by aliouswang on 2018/4/18.
 */

public class ImageScaleActivity extends Activity{

    public static void jump(Context context, int x, int y, int width, int height, float ratio) {
        Intent intent = new Intent(context, ImageScaleActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("x", x);
        bundle.putInt("y", y);
        bundle.putInt("width", width);
        bundle.putInt("height", height);
        bundle.putFloat("ratio", ratio);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    int x;
    int y;
    int width;
    int height;
    float ratio;
    float mTragetRatio = 1.14f;

    private ColorDrawable mColorDrawable;


    private ScaleImageView img_mask;

    private int mScreenHeight;
    private int mScreenWidth;

    private int[] mInitScreenLocation;

    private int mLeftDelta;
    private int mTopDelta;

    private volatile boolean bEnterAnimGoing;
    private volatile boolean bExitAnimGoing;


    private View mMainBackground;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_scale);

        mMainBackground = findViewById(R.id.main_background);
        mColorDrawable = new ColorDrawable(Color.BLACK);
        mMainBackground.setBackgroundDrawable(mColorDrawable);
        mColorDrawable.setAlpha(0);

        // Fade in background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mColorDrawable, "alpha",  255);
        bgAnim.setDuration(ANIM_DURATION);
        bgAnim.start();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mScreenHeight = displaymetrics.heightPixels;
        mScreenWidth = displaymetrics.widthPixels;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            x = bundle.getInt("x");
            y = bundle.getInt("y");
            width = bundle.getInt("width");
            height = bundle.getInt("height");
            ratio = bundle.getFloat("ratio");
        }

        img_mask = findViewById(R.id.img_mask);
        img_mask.setImageResource(R.drawable.zhao_2);
        img_mask.setRatio(ratio);

        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) img_mask.getLayoutParams();
        rlp.width = width;
        rlp.height = height;
        img_mask.setLayoutParams(rlp);


        if (savedInstanceState == null) {
            ViewTreeObserver observer = img_mask.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    img_mask.getViewTreeObserver().removeOnPreDrawListener(this);

                    int[] screenLocation = new int[2];
                    mInitScreenLocation = screenLocation;
                    img_mask.getLocationOnScreen(screenLocation);
                    mLeftDelta = x - screenLocation[0];
                    mTopDelta = y - screenLocation[1];

//                    calculateImageDeltas();

                    AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
                    ViewWrapper viewWrapper = new ViewWrapper(img_mask);

                    ValueAnimator animator2 = ObjectAnimator.ofInt(viewWrapper, "width", width);
                    ValueAnimator translateX = ObjectAnimator.ofFloat(img_mask, "translationX", 0, mLeftDelta);
                    ValueAnimator translateY = ObjectAnimator.ofFloat(img_mask, "translationY", 0, mTopDelta);
                    AnimatorSet translateSet = new AnimatorSet();
                    translateSet.playTogether(translateX, translateY, animator2);
                    translateSet.setDuration(0).start();

                    enterValueAnimation();

                    return true;
                }
            });

        }
    }

    public static final int ANIM_DURATION = 400;
    public void enterValueAnimation() {
        if (bEnterAnimGoing) return;
        bEnterAnimGoing = true;
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        ViewWrapper viewWrapper = new ViewWrapper(img_mask);

        ValueAnimator animator = ObjectAnimator.ofInt(viewWrapper, "width", mScreenWidth);
        ValueAnimator scaleAnimator =
                ObjectAnimator.ofFloat(viewWrapper, "ratio", ratio, mTragetRatio);
        ValueAnimator translateXAnim = ObjectAnimator.ofFloat(img_mask, "translationX", mLeftDelta, 0);
        ValueAnimator translateYAnim = ObjectAnimator.ofFloat(img_mask, "translationY", mTopDelta, 0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator, scaleAnimator, translateXAnim, translateYAnim);
        animatorSet.setInterpolator(interpolator);
        animatorSet.setStartDelay(10);
        animatorSet.setDuration(ANIM_DURATION).start();

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
//                mMaskImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
//                mViewPager.setVisibility(View.VISIBLE);
//                mMaskImageView.setVisibility(View.INVISIBLE);
                bEnterAnimGoing = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private static class ViewWrapper {
        private View mTarget;

        private float mScale = 1;

        public ViewWrapper(View target) {
            mTarget = target;
        }

        public int getWidth() {
            return mTarget.getLayoutParams().width;
        }

        public void setWidth(int width) {
            mTarget.getLayoutParams().width = width;
            mTarget.requestLayout();
        }

        public float getRatio() {
            return mScale;
        }

        public void setRatio(float scale) {
            this.mScale = scale;
            ((IRatio) mTarget).setRatio(scale);
        }

    }

    public void exitAnimation(final Runnable endAction) {
//        if (mCurrentPosition < mStartPosition ||
//                mCurrentPosition >= mEndPosition) {
//            finish();
//            return;
//        }
        if (bExitAnimGoing) return;
        bExitAnimGoing = true;

//        mRatio = ((IRatio)mMaskImageView).getRatio();
//        mLeftDelta = mNineImageDeltas.get(mCurrentPosition - mStartPosition).left - mInitScreenLocation[0];
//        mTopDelta = mNineImageDeltas.get(mCurrentPosition - mStartPosition).top - mInitScreenLocation[1];
//
//        mPageIndicatorView.setVisibility(View.GONE);
//        mMaskImageView.setVisibility(View.VISIBLE);
//        mViewPager.setVisibility(View.GONE);
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        ViewWrapper viewWrapper = new ViewWrapper(img_mask);

        ValueAnimator animator = ObjectAnimator.ofInt(viewWrapper, "width", width);
        ValueAnimator scaleAnimator =
                ObjectAnimator.ofFloat(viewWrapper, "ratio", mTragetRatio, ratio);
        ValueAnimator translateXAnim = ObjectAnimator.ofFloat(img_mask, "translationX", 0, mLeftDelta);
        ValueAnimator translateYAnim = ObjectAnimator.ofFloat(img_mask, "translationY", 0, mTopDelta);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator, scaleAnimator, translateXAnim, translateYAnim);
        animatorSet.setInterpolator(interpolator);
        animatorSet.setStartDelay(10);
        animatorSet.setDuration(ANIM_DURATION).start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                endAction.run();
                bExitAnimGoing = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });


        // Fade out background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mColorDrawable, "alpha", 0);
        bgAnim.setDuration(ANIM_DURATION);
        bgAnim.start();
    }

    protected void finishSelf() {
        exitAnimation(new Runnable() {
            public void run() {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishSelf();
    }
}
