package com.naufalhadi.barindicator

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class BarIndicator: LinearLayout {

    lateinit var lpViewPager: ViewPager2
    private var indicatorMargin = dpToPx(0F)
    private var indicatorWidth = 0
    private var indicatorHeight = dpToPx(4F)
    private var indicatorCornerRadius: Int = -1
    private var indicatorBackgroundResId = R.drawable.bg_indicator_active
    private var indicatorUnselectedBackgroundResId = R.drawable.bg_indicator_inactive
    private var indicatorBackgroundColorResId: Int = R.color.black
    private var indicatorUnselectedBackgroundColorResId: Int = R.color.white
    private var lastPosition = -1
    private var isBarClickable: Boolean = true

    private val internalPageChangeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {}

        override fun onPageSelected(position: Int) {
            if (lpViewPager.adapter == null || lpViewPager.adapter?.itemCount ?: 0 <= 0) return
            if (indicatorUnselectedBackgroundColorResId != R.color.white) setIndicatorColor(indicatorUnselectedBackgroundResId, indicatorUnselectedBackgroundColorResId)
            if (lastPosition >= 0) getChildAt(lastPosition)?.apply {
                setBackgroundResource(indicatorUnselectedBackgroundResId)
                setBackgroundCornerRadius(this)
            }
            getChildAt(position)?.apply {
                setBackgroundResource(indicatorBackgroundResId)
                setBackgroundCornerRadius(this)
            }
            lastPosition = position
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private val dataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            val newCount = lpViewPager.adapter?.itemCount ?: 0
            val currentCount = childCount
            lastPosition = when {
                newCount == currentCount -> return
                lastPosition < newCount -> lpViewPager.currentItem
                else -> -1
            }
            createIndicator()
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.BarIndicator, 0, 0)
            .apply {
                try {
                    indicatorHeight = getDimensionPixelSize(R.styleable.BarIndicator_barHeight, indicatorHeight)
                    indicatorMargin = getDimensionPixelSize(R.styleable.BarIndicator_barSpacing, indicatorMargin)
                    indicatorCornerRadius = getDimensionPixelSize(R.styleable.BarIndicator_indicatorCornerRadius, indicatorCornerRadius)
                    indicatorBackgroundResId = getResourceId(R.styleable.BarIndicator_barBackgroundActive, indicatorBackgroundResId)
                    indicatorUnselectedBackgroundResId = getResourceId(R.styleable.BarIndicator_barBackgroundInactive, indicatorUnselectedBackgroundResId)
                    indicatorBackgroundColorResId = getResourceId(R.styleable.BarIndicator_activeColor , indicatorBackgroundColorResId)
                    indicatorUnselectedBackgroundColorResId = getResourceId(R.styleable.BarIndicator_inactiveColor , indicatorUnselectedBackgroundColorResId)
                    isBarClickable = getBoolean(R.styleable.BarIndicator_isBarClickable, isBarClickable)
                } finally {
                    recycle()
                }
            }
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
    }

    fun setViewPager(viewPager: ViewPager2) {
        if (viewPager.adapter == null) {
            throw IllegalStateException(
                "You have to set an adapter to the view pager before " +
                        "initializing the stepper carousel bar !"
            )
        }
        this.lpViewPager = viewPager
        if (lpViewPager.adapter != null) {
            lastPosition = -1
            createIndicator()
            lpViewPager.unregisterOnPageChangeCallback(internalPageChangeListener)
            lpViewPager.registerOnPageChangeCallback(internalPageChangeListener)
            internalPageChangeListener.onPageSelected(lpViewPager.currentItem)
            lpViewPager.adapter!!.registerAdapterDataObserver(dataObserver)
        }
    }

    private fun createIndicator() {
        removeAllViews()
        val count = lpViewPager.adapter?.itemCount ?: 0
        if (count <= 0) return

        val currentItem = lpViewPager.currentItem
        for (i in 0 until  count) {
            if (currentItem == i) {
                if (indicatorBackgroundColorResId != R.color.black) setIndicatorColor(indicatorBackgroundResId, indicatorBackgroundColorResId)
                addIndicator(indicatorBackgroundResId, i, count)
            }
            else {
                if (indicatorUnselectedBackgroundColorResId != R.color.white) setIndicatorColor(indicatorUnselectedBackgroundResId, indicatorUnselectedBackgroundColorResId)
                addIndicator(indicatorUnselectedBackgroundResId, i, count)
            }
        }
    }


    private fun addIndicator(@DrawableRes backgroundDrawableId: Int, index: Int, maxIndicator: Int) {
        val indicator = View(context)
        indicator.setBackgroundResource(backgroundDrawableId)
        setBackgroundCornerRadius(indicator)
        if (isBarClickable) {
            indicator.setOnClickListener {
                lpViewPager.setCurrentItem(index, true)
            }
        }
        addView(indicator, indicatorWidth, indicatorHeight)
        val lp = indicator.layoutParams as LayoutParams
        lp.weight = 1F
        if (index+1 != maxIndicator) lp.rightMargin = indicatorMargin
        indicator.layoutParams = lp
    }

    private fun dpToPx(dpValue: Float): Int =
        (dpValue * resources.displayMetrics.density + 0.5f).toInt()

    private fun setIndicatorColor(backgroundDrawable: Int, colorId: Int) {
        val unwrappedDrawable = AppCompatResources.getDrawable(context, backgroundDrawable)
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, ResourcesCompat.getColor(resources, colorId, null))
    }

    private fun setBackgroundCornerRadius(view: View) {
        val backGroundDrawable = view.background.mutate() as GradientDrawable
        if (indicatorCornerRadius != -1) backGroundDrawable.cornerRadius = indicatorCornerRadius.toFloat()
    }
}