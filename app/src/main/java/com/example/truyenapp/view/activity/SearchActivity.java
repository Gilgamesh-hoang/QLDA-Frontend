package com.example.truyenapp.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenapp.R;
import com.example.truyenapp.api.RetrofitClient;
import com.example.truyenapp.api.SearchAPI;
import com.example.truyenapp.paging.PagingScrollListener;
import com.example.truyenapp.response.APIResponse;
import com.example.truyenapp.response.BookResponse;
import com.example.truyenapp.response.CategoryResponse;
import com.example.truyenapp.response.DataListResponse;
import com.example.truyenapp.view.adapter.SearchAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    EditText inputSearch;
    ImageView inputSearchRecord;
    TextView notify;
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> categoryAdapter;
    Map<Integer, String> mapCategory;
    List<BookResponse> listComic;
    LinearLayoutManager linearLayoutManager;
    public String category;
    public String keyword = "";
    private Integer categoryId;
    private RecyclerView rcv;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private SearchAdapter adapter;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int totalPage;
    private int currentPage = 1;
    private final int PAGE_SIZE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init();
        categoryAdapter = new ArrayAdapter(this, R.layout.list_item);
        autoCompleteTextView.setAdapter(categoryAdapter);
        adapter = new SearchAdapter(this, listComic);
        rcv.setAdapter(adapter);
        initCategory();
//        Handle Search
        handleEvent();
    }

    private void init() {
        this.inputSearch = findViewById(R.id.edt_search);
        this.autoCompleteTextView = findViewById(R.id.auto_complete_category);
        this.notify = findViewById(R.id.activity_search_notify);
        this.rcv = findViewById(R.id.activity_rcv_commic);
        this.inputSearchRecord = findViewById(R.id.activity_search_recog);
//
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcv.setLayoutManager(linearLayoutManager);
        this.mapCategory = new HashMap<>();
        this.listComic = new ArrayList<>();
        rcv.addOnScrollListener(new PagingScrollListener(this.linearLayoutManager) {
            @Override
            public void loadMoreItem() {
                isLoading = true;
                currentPage += 1;
                getData();
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }
        });
    }

    private void setFirstData(List<BookResponse> list) {
        this.listComic.addAll(list);
        adapter.setData(this.listComic);
        if (currentPage < totalPage) {
            adapter.addFooterLoading();
        } else {
            isLastPage = true;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadNextPage(List<BookResponse> list) {
        adapter.removeFooterLoading();
        this.listComic.addAll(list);
        adapter.notifyDataSetChanged();
        this.isLoading = false;
        if (currentPage < totalPage) {
            adapter.addFooterLoading();
        } else {
            isLastPage = true;
        }
    }

    private Integer getCategory() {
        String category = autoCompleteTextView.getText().toString();
        return mapCategory.entrySet().stream().filter(entry -> entry.getValue().equals(category)).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    private String getKeyword() {
        return inputSearch.getText().toString();
    }

    //  Event
    private void handleEvent() {
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                handleSearch();
            }
        });
        inputSearch.setOnEditorActionListener((v, actionId, event) -> {
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                handleSearch();
            }
            return true;
        });
        inputSearchRecord.setOnClickListener(this);
    }

    private void showNotify(String message) {
        notify.setText(message);
        if (message.equals("")) {
            notify.setVisibility(View.GONE);
        } else {
            notify.setVisibility(View.VISIBLE);
        }
    }

    public void handleSearch() {
        this.category = autoCompleteTextView.getText().toString();
        this.categoryId = getCategory();
        this.keyword = getKeyword();
        this.resetSearch();
        this.getData();
    }

    public void resetSearch() {
        this.isLoading = false;
        this.isLastPage = false;
        this.currentPage = 1;
        this.listComic.clear();
        adapter.notifyDataSetChanged();
    }

    private void getData() {
        SearchAPI response = RetrofitClient.getInstance(this).create(SearchAPI.class);
        response.search(keyword, categoryId, currentPage, PAGE_SIZE).enqueue(new Callback<APIResponse<DataListResponse<BookResponse>>>() {
            @Override
            public void onResponse(Call<APIResponse<DataListResponse<BookResponse>>> call, Response<APIResponse<DataListResponse<BookResponse>>> response) {
                APIResponse<DataListResponse<BookResponse>> data = response.body();
                if (data == null || data.getResult() == null || data.getResult().getData() == null) {
                    showNotify("Không có truyện cần tìm!!!");
                    return;
                }

                if (data.getCode() == 400) {
                    showNotify("Không có truyện cần tìm!!!");
                    return;
                }
                List<BookResponse> listTemp = data.getResult().getData();
                currentPage = data.getResult().getCurrentPage();
                totalPage = data.getResult().getTotalPages();
                showNotify("");
                if (currentPage == 1) {
                    setFirstData(listTemp);
                } else {
                    loadNextPage(listTemp);
                }
            }

            @Override
            public void onFailure(Call<APIResponse<DataListResponse<BookResponse>>> call, Throwable throwable) {
                showNotify("Không có tải được dữ liệu!!!");
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
                categoryAdapter.add("Tất cả");
                categoryAdapter.addAll(mapCategory.values());
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<APIResponse<List<CategoryResponse>>> call, Throwable throwable) {
                Log.d("SearchActivity", "onFailure: " + throwable.getMessage());
            }
        });
    }

    //    Handle voice search
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Nói tên truyện");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            try {
                String textVoice = result.get(0);
                inputSearch.setText(textVoice);
                handleSearch();
            } catch (NullPointerException e) {
                showNotify("Không có truyện cần tìm!!!");
            }
        }
    }

}
