package com.example.truyenapp.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenapp.R;
import com.example.truyenapp.api.RetrofitClient;
import com.example.truyenapp.api.SearchAPI;
import com.example.truyenapp.paging.PagingScrollListener;
import com.example.truyenapp.response.APIResponse;
import com.example.truyenapp.response.BookResponse;
import com.example.truyenapp.response.DataListResponse;
import com.example.truyenapp.view.adapter.CategoryViewModel;
import com.example.truyenapp.view.adapter.ComicViewAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComicViewFragment extends Fragment  {
    CategoryViewModel categoryViewModel;
    private View view;
    private RecyclerView rcv;
    private ComicViewAdapter adapter;
    private List<BookResponse> listComic = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private Integer categoryId;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int totalPage;
    private int currentPage = 1;
    private final int PAGE_SIZE = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rcv_linear, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        getData();
        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
        categoryViewModel.getCategoryId().observe(getViewLifecycleOwner(), this::update);
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

    private void update(Integer integer) {
        this.categoryId = integer;
        currentPage = 1;
        totalPage = 0;
        this.adapter.removeFooterLoading();
        this.adapter.clearData();
        this.isLoading = false;
        this.isLastPage = false;
        this.getData();
    }

    private void init() {
        this.rcv = view.findViewById(R.id.rcv_comic_card);
        this.adapter = new ComicViewAdapter(getActivity(), listComic);
        this.linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        this.rcv.setLayoutManager(linearLayoutManager);
        this.rcv.setAdapter(adapter);
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

    //    call api lấy dữ liệu danh sách truyện theo lượt xem
    public void getData() {
        SearchAPI response = RetrofitClient.getInstance(getContext()).create(SearchAPI.class);
        if (categoryId == null) categoryId = 0;
        Call<APIResponse<DataListResponse<BookResponse>>> call = response.rank("view", categoryId, currentPage, PAGE_SIZE);
        call.enqueue(new Callback<APIResponse<DataListResponse<BookResponse>>>() {
            @Override
            public void onResponse(Call<APIResponse<DataListResponse<BookResponse>>> call, Response<APIResponse<DataListResponse<BookResponse>>> response) {
                APIResponse<DataListResponse<BookResponse>> data = response.body();

                if (data == null || data.getResult() == null || data.getResult().getData() == null) {
                    return;
                }

                if (data.getCode() == 400)
                    return;
                List<BookResponse> listTemp = data.getResult().getData();
                currentPage = data.getResult().getCurrentPage();
                totalPage = data.getResult().getTotalPages();


                if (currentPage == 1) {
                    setFirstData(listTemp);
                } else {
                    loadNextPage(listTemp);
                }
            }

            @Override
            public void onFailure(Call<APIResponse<DataListResponse<BookResponse>>> call, Throwable throwable) {
                Toast.makeText(getActivity(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("comic view", "onResume: ");
    }

}