package com.naufalhadi.barindicatorsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.naufalhadi.barindicator.BarIndicator
import com.naufalhadi.barindicatorsample.utils.ViewPagerAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewPager2 = findViewById<ViewPager2>(R.id.iv_view_pager)
        val barIndicator = findViewById<BarIndicator>(R.id.barIndicator)

        viewPager2.adapter = ViewPagerAdapter()
        barIndicator.setViewPager(viewPager2)
    }
}