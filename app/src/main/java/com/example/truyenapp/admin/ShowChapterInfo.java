package com.example.truyenapp.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.truyenapp.R;
import com.example.truyenapp.api.BookAPI;
import com.example.truyenapp.api.ChapterAPI;
import com.example.truyenapp.api.RetrofitClient;
import com.example.truyenapp.response.BookResponse;
import com.example.truyenapp.response.ChapterResponse;
import com.example.truyenapp.view.adapter.admin.ChapterContentAdapter;

import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowChapterInfo extends AppCompatActivity implements View.OnClickListener {
    ImageView thumbnail;
    TextView idText, ratingText, viewText, dateText, bookNameText, chapterNameText;
    int chapterId;
    private RecyclerView rcv;
    private ChapterContentAdapter adapter;
    private BookAPI bookAPI;
    private ChapterAPI chapterAPI;
    private BookResponse bookResponse;
    private ChapterResponse chapterResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chapter_info);
        bookAPI = RetrofitClient.getInstance(this).create(BookAPI.class);
        chapterAPI = RetrofitClient.getInstance(this).create(ChapterAPI.class);

        init();
        Intent intent = getIntent();
        chapterId = intent.getIntExtra("chapterId", 1);
        getDataFromAPI();
        setOnClickListener();
    }

    private void setOnClickListener() {

    }

    private void init() {
        thumbnail = findViewById(R.id.img_qlc);
        idText = findViewById(R.id.tv_qlc_id);
        rcv = findViewById(R.id.rcv_quanlynoidungchapter);
        ratingText = findViewById(R.id.tv_qlc_danhgia);
        viewText = findViewById(R.id.tv_qlc_luotxem);
        dateText = findViewById(R.id.tv_qlc_ngaydang);
        bookNameText = findViewById(R.id.tv_qlc_tentruyen);
        chapterNameText = findViewById(R.id.edt_qlc_tenchapter);
    }

    @Override
    public void onClick(View view) {
    }

    private void getDataFromAPI() {
        // Create two CompletableFuture objects, one for each API call
        CompletableFuture<ChapterResponse> futureChapter = getChapterById();
        CompletableFuture<BookResponse> futureBook = getBook();

        // Use CompletableFuture.allOf() to create a new CompletableFuture that is completed when both of the given CompletableFutures complete
        CompletableFuture.allOf(futureChapter, futureBook).thenRun(() -> {
            // When both API calls are done, get the results
            bookResponse = futureBook.join();
            chapterResponse = futureChapter.join();

            // Update the UI on the main thread
            runOnUiThread(() -> {
                // Set the data in the UI
                setData();
                // Set up the RecyclerView
                chapterContentRecycle();
            });
        }).exceptionally(ex -> {
            // If either API call fails, handle the exception
            // Update the UI on the main thread
            runOnUiThread(() -> {
                // Show a toast message with the error
                Toast.makeText(ShowChapterInfo.this, "Failed to load data: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            });
            return null;
        });
    }

    private synchronized CompletableFuture<ChapterResponse> getChapterById() {
        CompletableFuture<ChapterResponse> future = new CompletableFuture<>();
        chapterAPI.getChapter(chapterId).enqueue(new Callback<ChapterResponse>() {
            @Override
            public void onResponse(Call<ChapterResponse> call, Response<ChapterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    future.complete(response.body());
                } else {
                    Log.e("ChapterId", "Chapter API response failed: " + response.message());
                    Toast.makeText(ShowChapterInfo.this, "Failed to get chapter: " + response.message(), Toast.LENGTH_SHORT).show();
                    future.completeExceptionally(new Exception("Failed to get chapter: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ChapterResponse> call, Throwable t) {
                Log.e("ChapterId", "Chapter API request failed", t);
                Toast.makeText(ShowChapterInfo.this, "Failed to get chapter", Toast.LENGTH_SHORT).show();
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    private synchronized CompletableFuture<BookResponse> getBook() {
        CompletableFuture<BookResponse> future = new CompletableFuture<>();
        bookAPI.getBookByChapterId(chapterId).enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    future.complete(response.body());
                } else {
                    Log.e("ChapterId", "Book API response failed: " + response.message());
                    Toast.makeText(ShowChapterInfo.this, "Failed to get book: " + response.message(), Toast.LENGTH_SHORT).show();
                    future.completeExceptionally(new Exception("Failed to get book: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                Log.e("ChapterId", "Book API request failed", t);
                Toast.makeText(ShowChapterInfo.this, "Failed to get book", Toast.LENGTH_SHORT).show();
                future.completeExceptionally(t);
            }
        });
        return future;
    }


    private void chapterContentRecycle() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv.setLayoutManager(linearLayoutManager);

        adapter = new ChapterContentAdapter(this, chapterResponse.getChapterContent());
        rcv.setAdapter(adapter);
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        Glide.with(this).load(bookResponse.getThumbnail()).into(thumbnail);
        idText.setText("" + chapterResponse.getId());
        bookNameText.setText(bookResponse.getName());
        chapterNameText.setText(chapterResponse.getName());
        ratingText.setText("" + chapterResponse.getRating());
        viewText.setText("" + chapterResponse.getView());
        dateText.setText(chapterResponse.getPublishDate().toString());
    }


}
