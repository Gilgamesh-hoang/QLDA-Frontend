package com.example.truyenapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.truyenapp.R;
import com.example.truyenapp.constraints.BundleConstraint;
import com.example.truyenapp.view.adapter.FragmentAdapterDetail;
import com.google.android.material.tabs.TabLayout;

public class DetailComicActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 pager2;
    private FragmentAdapterDetail adapter;
    private ImageView imgComic;
    private String linkImage;
    private int idComic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_comic);
        Intent intent = getIntent();
        idComic = intent.getIntExtra(BundleConstraint.ID_COMIC, 0);
        linkImage = intent.getStringExtra(BundleConstraint.LINK_IMG);
        init();
        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new FragmentAdapterDetail(fragmentManager, getLifecycle());
        pager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Chi tiết"));
        tabLayout.addTab(tabLayout.newTab().setText("Chapter"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        adapter.setIdComic(idComic);
    }

    private void init() {
        tabLayout = findViewById(R.id.tab_layout);
        pager2 = findViewById(R.id.view_pager2);
        imgComic = findViewById(R.id.img_truyen);
        Glide.with(this).load(linkImage).into(imgComic);

    }
}
