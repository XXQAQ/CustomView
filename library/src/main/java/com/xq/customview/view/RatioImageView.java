package com.xq.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.xq.customview.R;

public class RatioImageView extends ImageView {

    /**
     <declare-styleable name="RatioImageView">
     <!--宽度是否根据src图片的比例来测量（高度已知）-->
     <attr name="is_width_fix_drawable_size_ratio" format="boolean"/>
     <!--高度是否根据src图片的比例来测量（宽度已知）-->
     <attr name="is_height_fix_drawable_size_ratio" format="boolean"/>

     <!-- 高度设置，参考宽度，如0.5 , 表示 高度＝宽度×０.5 -->
     <attr name="height_to_width_ratio" format="float"/>
     <!-- 宽度设置，参考高度，如0.5 , 表示 宽度＝高度×０.5 -->
     <attr name="width_to_height_ratio" format="float"/>
     </declare-styleable>
     */

    /* 优先级从大到小：
     mIsWidthFitDrawableSizeRatio mIsHeightFitDrawableSizeRatio
     mWidthRatio mHeightRatio
     即如果设置了mIsWidthFitDrawableSizeRatio为true，则优先级较低的三个值不生效 */

    private float mDrawableSizeRatio = -1f; // src图片(前景图)的宽高比例
    // 根据前景图宽高比例测量View,防止图片缩放变形
    private boolean mIsWidthFitDrawableSizeRatio; // 宽度是否根据src图片(前景图)的比例来测量（高度已知）
    private boolean mIsHeightFitDrawableSizeRatio; // 高度是否根据src图片(前景图)的比例来测量（宽度已知）
    // 宽高比例
    private float mWidthRatio = -1; // 宽度 = 高度*mWidthRatio
    private float mHeightRatio = -1; // 高度 = 宽度*mHeightRatio

    public RatioImageView(Context context) {
        this(context, null);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr); // 虽然此处会调用setImageDrawable，但此时成员变量还未被正确初始化
        init(attrs);
        // 一定要有此代码
        if (getDrawable() != null) {
            mDrawableSizeRatio = 1f * getDrawable().getIntrinsicWidth()
                    / getDrawable().getIntrinsicHeight();
        }
    }

    /**
     * 初始化变量
     */
    private void init(AttributeSet attrs) {

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.RatioImageView);
        mIsWidthFitDrawableSizeRatio = a.getBoolean(R.styleable.RatioImageView_is_width_fix_drawable_size_ratio,
                mIsWidthFitDrawableSizeRatio);
        mIsHeightFitDrawableSizeRatio = a.getBoolean(R.styleable.RatioImageView_is_height_fix_drawable_size_ratio,
                mIsHeightFitDrawableSizeRatio);
        mHeightRatio = a.getFloat(
                R.styleable.RatioImageView_height_to_width_ratio, mHeightRatio);
        mWidthRatio = a.getFloat(
                R.styleable.RatioImageView_width_to_height_ratio, mWidthRatio);
        a.recycle();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (getDrawable() != null) {
            mDrawableSizeRatio = 1f * getDrawable().getIntrinsicWidth()
                    / getDrawable().getIntrinsicHeight();
            if (mDrawableSizeRatio > 0
                    && (mIsWidthFitDrawableSizeRatio || mIsHeightFitDrawableSizeRatio)) {
                requestLayout();
            }
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (getDrawable() != null) {
            mDrawableSizeRatio = 1f * getDrawable().getIntrinsicWidth()
                    / getDrawable().getIntrinsicHeight();
            if (mDrawableSizeRatio > 0
                    && (mIsWidthFitDrawableSizeRatio || mIsHeightFitDrawableSizeRatio)) {
                requestLayout();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 优先级从大到小：
        // mIsWidthFitDrawableSizeRatio mIsHeightFitDrawableSizeRatio
        // mWidthRatio mHeightRatio
        if (mDrawableSizeRatio > 0) {
            // 根据前景图宽高比例来测量view的大小
            if (mIsWidthFitDrawableSizeRatio) {
                mWidthRatio = mDrawableSizeRatio;
            } else if (mIsHeightFitDrawableSizeRatio) {
                mHeightRatio = 1 / mDrawableSizeRatio;
            }
        }

        if (mHeightRatio > 0 && mWidthRatio > 0) {
            throw new RuntimeException("高度和宽度不能同时设置百分比！！");
        }

        if (mWidthRatio > 0) { // 高度已知，根据比例，设置宽度
            int height = MeasureSpec.getSize(heightMeasureSpec);
            super.onMeasure(MeasureSpec.makeMeasureSpec(
                    (int) (height * mWidthRatio), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        } else if (mHeightRatio > 0) { // 宽度已知，根据比例，设置高度
            int width = MeasureSpec.getSize(widthMeasureSpec);
            super.onMeasure(MeasureSpec.makeMeasureSpec(width,
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    (int) (width * mHeightRatio), MeasureSpec.EXACTLY));
        } else { // 系统默认测量
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
