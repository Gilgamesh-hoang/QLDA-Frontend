package com.example.truyenapp.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.truyenapp.R;
import com.example.truyenapp.api.CommentAPI;
import com.example.truyenapp.api.RetrofitClient;
import com.example.truyenapp.constraints.BundleConstraint;
import com.example.truyenapp.enums.CommentState;
import com.example.truyenapp.mapper.CommentMapper;
import com.example.truyenapp.model.Comment;
import com.example.truyenapp.response.APIResponse;
import com.example.truyenapp.response.CommentResponse;
import com.example.truyenapp.utils.Format;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentDetailManagementActivity extends AppCompatActivity {
    Integer commentId;
    Comment comment;
    ImageView img;
    TextView tv_id, tv_email, tv_noidung, tv_ngaydang, tv_trangthai, tv_tentruyen, tv_tenchapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail_management);

        init();
        this.commentId = getIntent().getIntExtra(BundleConstraint.ID_COMMENT, 0);
        callAPI();
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        Glide.with(this).load(comment.getThumbnail()).into(img);
        tv_tentruyen.setText(comment.getBookName());
        tv_tenchapter.setText(comment.getChapterName());
        tv_id.setText(comment.getId() + "");
        tv_email.setText(comment.getEmail());
        tv_noidung.setText(comment.getContent());
        tv_ngaydang.setText(Format.formatDate(comment.getCreatedAt().toString(), "yyyy-MM-dd", "dd/MM/yyyy"));
        int state = comment.getState();
        if (state == CommentState.SHOW.getState()) {
            tv_trangthai.setText("Hoạt động");
        } else {
            tv_trangthai.setText("Bị khóa");
        }
    }

    private void init() {
        img = findViewById(R.id.image_comment_detail_management);
        tv_email = findViewById(R.id.tv_comment_detail_management_email);
        tv_id = findViewById(R.id.tv_comment_detail_management_id);
        tv_noidung = findViewById(R.id.tv_comment_detail_management_content);
        tv_ngaydang = findViewById(R.id.tv_comment_detail_management_publishDay);
        tv_trangthai = findViewById(R.id.tv_comment_detail_management_state);
        tv_tentruyen = findViewById(R.id.tv_comment_detail_management_commic);
        tv_tenchapter = findViewById(R.id.tv_comment_detail_management_chapter);
    }

    public void callAPI() {
        RetrofitClient.getInstance(this).create(CommentAPI.class).getCommentDetail(commentId).enqueue(new Callback<APIResponse<CommentResponse>>() {
            @Override
            public void onResponse(Call<APIResponse<CommentResponse>> call, Response<APIResponse<CommentResponse>> response) {
                APIResponse<CommentResponse> apiResponse = response.body();
                if (apiResponse == null || apiResponse.getCode() == 400) {
                    return;
                }
                comment = CommentMapper.INSTANCE.commentResponseToComment(apiResponse.getResult());
                setData();
            }

            @Override
            public void onFailure(Call<APIResponse<CommentResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });


    }
}
