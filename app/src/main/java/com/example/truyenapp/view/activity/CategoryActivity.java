package com.example.truyenapp.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.truyenapp.R;
import com.example.truyenapp.api.RetrofitClient;
import com.example.truyenapp.api.SearchAPI;
import com.example.truyenapp.response.APIResponse;
import com.example.truyenapp.response.CategoryResponse;
import com.example.truyenapp.view.adapter.CategoryViewModel;
import com.example.truyenapp.view.adapter.FragmentAdapterCategory;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {
    CategoryViewModel categoryViewModel;
    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentAdapterCategory adapter;
    ArrayAdapter<String> categoryAdapter;
    String category;
    Map<Integer, String> mapCategory;
    AutoCompleteTextView autoCompleteTextView;
    public Integer categoryId;
    private final String[] TAB_TEXT = {"Mới nhất", "Đánh giá ", "Lượt Xem"};
    public static Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_category);
        init();
        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new FragmentAdapterCategory(fragmentManager, getLifecycle());
        pager2.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, pager2,
                (tab, position) -> tab.setText(TAB_TEXT[position])
        ).attach();

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

        autoCompleteTextView.setAdapter(categoryAdapter);
        initCategory();
        handleViewPager2();
    }

    private void init() {
        this.tabLayout = findViewById(R.id.tab_layout_category);
        this.pager2 = findViewById(R.id.view_pager2_category);
        this.autoCompleteTextView = findViewById(R.id.auto_complete_category);
        categoryAdapter = new ArrayAdapter(this, R.layout.list_item);
        mapCategory = new HashMap<>();
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                handleSearch();
            }
        });
    }

    public void handleSearch() {
        this.category = autoCompleteTextView.getText().toString();
        categoryId = getCategory();
        categoryViewModel.setCategoryId(categoryId);
    }

    private Integer getCategory() {
        String category = autoCompleteTextView.getText().toString();
        return mapCategory.entrySet().stream().filter(entry -> entry.getValue().equals(category)).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public void handleViewPager2() {
        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
                currentFragment = adapter.createFragment(position);
            }
        });
    }

    private void initCategory() {
        SearchAPI response = RetrofitClient.getInstance(this).create(SearchAPI.class);
        response.getCategory().enqueue(new Callback<APIResponse<List<CategoryResponse>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<CategoryResponse>>> call, Response<APIResponse<List<CategoryResponse>>> response) {
                APIResponse<List<CategoryResponse>> data = response.body();
                for (CategoryResponse category : data.getResult()) {
                    mapCategory.put(category.getId(), category.getName());
                }
                mapCategory.put(0, "Tất cả");
                categoryAdapter.addAll(mapCategory.values());
                categoryAdapter.notifyDataSetChanged();
                autoCompleteTextView.setText(mapCategory.get(0), false);
            }

            @Override
            public void onFailure(Call<APIResponse<List<CategoryResponse>>> call, Throwable throwable) {
                Log.d("SearchActivity", "onFailure: " + throwable.getMessage());
            }
        });
    }

}