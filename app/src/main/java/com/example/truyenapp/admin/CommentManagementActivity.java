package com.example.truyenapp.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenapp.R;
import com.example.truyenapp.api.CommentAPI;
import com.example.truyenapp.api.RetrofitClient;
import com.example.truyenapp.mapper.CommentMapper;
import com.example.truyenapp.model.Comment;
import com.example.truyenapp.paging.PagingScrollListener;
import com.example.truyenapp.response.APIResponse;
import com.example.truyenapp.response.CommentResponse;
import com.example.truyenapp.response.DataListResponse;
import com.example.truyenapp.view.adapter.admin.CommentManagerAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentManagementActivity extends AppCompatActivity {
    private RecyclerView rcv;
    private CommentManagerAdapter adapter;
    private final int PAGE_SIZE = 15;
    private int currentPage = 1;
    private int totalPage;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    LinearLayoutManager linearLayoutManager;
    List<Comment> listComment;
    CommentAPI commentAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_management);

        init();
        getData();
    }

    private void init() {
        rcv = findViewById(R.id.rcv_comment_management);
        this.commentAPI = RetrofitClient.getInstance(this).create(CommentAPI.class);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv.setLayoutManager(linearLayoutManager);
        this.listComment = new ArrayList<>();
        adapter = new CommentManagerAdapter(this, listComment);
        rcv.setAdapter(adapter);
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

    private void setFirstData(List<Comment> list) {
        this.listComment.addAll(list);
        adapter.setData(this.listComment);
        if (currentPage < totalPage) {
            adapter.addFooterLoading();
        } else {
            isLastPage = true;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadNextPage(List<Comment> list) {
        adapter.removeFooterLoading();
        this.listComment.addAll(list);
        adapter.notifyDataSetChanged();
        this.isLoading = false;
        if (currentPage < totalPage) {
            adapter.addFooterLoading();
        } else {
            isLastPage = true;
        }
    }

    private void getData() {
        commentAPI.getComments(currentPage, PAGE_SIZE).enqueue(new Callback<APIResponse<DataListResponse<CommentResponse>>>() {
            @Override
            public void onResponse(Call<APIResponse<DataListResponse<CommentResponse>>> call, Response<APIResponse<DataListResponse<CommentResponse>>> response) {
                APIResponse<DataListResponse<CommentResponse>> data = response.body();

                if (data == null || data.getResult() == null || data.getResult().getData() == null) {
                    return;
                }

                if (data.getCode() == 400)
                    return;

                List<Comment> listTemp = new ArrayList<>();
                currentPage = data.getResult().getCurrentPage();
                totalPage = data.getResult().getTotalPages();
                for (CommentResponse commentResponse : data.getResult().getData()) {
                    Comment comment = CommentMapper.INSTANCE.commentResponseToComment(commentResponse);
                    listTemp.add(comment);
                }
                if (currentPage == 1) {
                    setFirstData(listTemp);
                } else {
                    loadNextPage(listTemp);
                }
            }

            @Override
            public void onFailure(Call<APIResponse<DataListResponse<CommentResponse>>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
