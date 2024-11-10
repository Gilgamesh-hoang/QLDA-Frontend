package com.example.truyenapp.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenapp.R;
import com.example.truyenapp.api.BookAPI;
import com.example.truyenapp.api.CommentAPI;
import com.example.truyenapp.api.RetrofitClient;
import com.example.truyenapp.paging.PagingScrollListener;
import com.example.truyenapp.response.APIResponse;
import com.example.truyenapp.response.BookResponse;
import com.example.truyenapp.response.CommentResponse;
import com.example.truyenapp.response.DataListResponse;
import com.example.truyenapp.utils.Format;
import com.example.truyenapp.view.adapter.CommentAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFragment extends Fragment {
    private View view;
    private TextView rating, totalView, totalComment, description;
    private RecyclerView rcv;
    private int comicId;
    private BookAPI bookAPI;
    private CommentAPI commentAPI;
    private LinearLayoutManager linearLayoutManager;
    private List<CommentResponse> comments;
    private CommentAdapter adapter;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private Integer currentPage = 1;
    private final Integer PAGE_SIZE = 5;
    private int totalPage;

    public DetailFragment(int comicId) {
        this.comicId = comicId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_comic_detail, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        bookAPI = RetrofitClient.getInstance(getContext()).create(BookAPI.class);
        commentAPI = RetrofitClient.getInstance(getContext()).create(CommentAPI.class);
        getDetail(comicId);
        getComment();
    }

    private void init() {
        this.rating = view.findViewById(R.id.tv_danhgiact);
        this.totalView = view.findViewById(R.id.tv_tongluotxem);
        this.totalComment = view.findViewById(R.id.tv_tongbinhluan);
        this.description = view.findViewById(R.id.tv_motatruyen);
        this.rcv = view.findViewById(R.id.rcv_binhluan);
        this.linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        this.rcv.setLayoutManager(linearLayoutManager);
        this.comments = new ArrayList<>();
        this.adapter = new CommentAdapter(getContext(), comments);
        this.rcv.setAdapter(adapter);
        rcv.addOnScrollListener(new PagingScrollListener(this.linearLayoutManager) {
            @Override
            public void loadMoreItem() {
                isLoading = true;
                currentPage += 1;
//                getComment();
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

    public void getTotalComment(int comicId) {
        bookAPI.getAllComment(comicId).enqueue(new Callback<APIResponse<Integer>>() {
            @Override
            public void onResponse(Call<APIResponse<Integer>> call, Response<APIResponse<Integer>> response) {
                if (response.isSuccessful()) {
                    APIResponse<Integer> apiResponse = response.body();
                    if (apiResponse != null) {
                        totalComment.setText(String.valueOf(apiResponse.getResult()));
                    }
                } else {
                    Log.println(Log.ERROR, "API", "Error");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Integer>> call, Throwable t) {
                Log.d("DetailFragment", "onFailure: " + t.getMessage());
            }
        });
    }

    public void getDetail(int comicId) {
        bookAPI.getDescriptionBook(comicId).enqueue(new Callback<APIResponse<BookResponse>>() {
            @Override
            public void onResponse(Call<APIResponse<BookResponse>> call, Response<APIResponse<BookResponse>> response) {
                if (response.isSuccessful()) {
                    APIResponse<BookResponse> apiResponse = response.body();
                    if (apiResponse != null) {
                        BookResponse bookResponse = apiResponse.getResult();
                        double rate = bookResponse.getRating();
                        rating.setText(String.valueOf(Format.roundNumber(rate)));
                        totalView.setText(String.valueOf(bookResponse.getView()));
                        description.setText(bookResponse.getDescription());
//                        getTotalComment(comicId);
                    }
                } else {
                    Log.println(Log.ERROR, "API", "Error");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<BookResponse>> call, Throwable t) {
                Log.d("DetailFragment", "onFailure: " + t.getMessage());
            }
        });
    }

    private double formatRating(double rating) {
        return (double) Math.round(rating * 100) / 100;
    }

    private void setFirstData(List<CommentResponse> list) {
        this.comments.addAll(list);
        adapter.setData(this.comments);
        if (currentPage < totalPage) {
            adapter.addFooterLoading();
        } else {
            isLastPage = true;
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private void loadNextPage(List<CommentResponse> list) {
        adapter.removeFooterLoading();
        this.comments.addAll(list);
        adapter.notifyDataSetChanged();
        this.isLoading = false;
        if (currentPage < totalPage) {
            adapter.addFooterLoading();
        } else {
            isLastPage = true;
        }
    }

    public void getComment() {
        commentAPI.getComments("BY_BOOK",comicId, currentPage, PAGE_SIZE).enqueue(new Callback<APIResponse<DataListResponse<CommentResponse>>>() {
            @Override
            public void onResponse(Call<APIResponse<DataListResponse<CommentResponse>>> call, Response<APIResponse<DataListResponse<CommentResponse>>> response) {
                APIResponse<DataListResponse<CommentResponse>> data = response.body();
                if (data == null || data.getResult() == null || data.getResult().getData() == null) {
                    return;
                }
                if (data.getCode() == 400)
                    return;
                List<CommentResponse> listTemp = new ArrayList<>();
                currentPage = data.getResult().getCurrentPage();
                totalPage = data.getResult().getTotalPages();
                listTemp.addAll(data.getResult().getData());
                if (currentPage == 1) {
                    setFirstData(listTemp);
                } else {
                    loadNextPage(listTemp);
                }
            }

            @Override
            public void onFailure(Call<APIResponse<DataListResponse<CommentResponse>>> call, Throwable throwable) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.setData(this.comments);
    }
}