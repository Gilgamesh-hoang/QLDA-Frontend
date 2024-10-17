package com.example.truyenapp.view.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenapp.R;
import com.example.truyenapp.api.BookAPI;
import com.example.truyenapp.api.ChapterAPI;
import com.example.truyenapp.api.RetrofitClient;
import com.example.truyenapp.constraints.BundleConstraint;
import com.example.truyenapp.response.APIResponse;
import com.example.truyenapp.response.ChapterResponse;
import com.example.truyenapp.view.adapter.ChapterAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChapterFragment extends Fragment {
    View view;
    TextView textViewChapter, dateRelease, viewChapter;
    private RecyclerView rcv;
    private ChapterAdapter rcvAdapter;
    private BookAPI bookAPI;
    private ChapterAPI chapterAPI;
    private int idComic;

    public ChapterFragment() {

    }

    public ChapterFragment(int idComic) {
        this.idComic = idComic;
    }

    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idComic = getArguments().getInt(BundleConstraint.ID_COMIC);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chapter, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rcv.setLayoutManager(linearLayoutManager);
        bookAPI = RetrofitClient.getInstance(getContext()).create(BookAPI.class);
        chapterAPI = RetrofitClient.getInstance(getContext()).create(ChapterAPI.class);
        getChapter(idComic);
    }

    private void init() {
        textViewChapter = view.findViewById(R.id.tv_chapter);
        dateRelease = view.findViewById(R.id.tv_ngaydang);
        viewChapter = view.findViewById(R.id.tv_luotxem);
        rcv = view.findViewById(R.id.rcv_chapter);
    }
    public void getViewEachChapter(int idChapter, ChapterResponse chapterResponse, Runnable callback) {
        chapterAPI.getTotalView(idChapter).enqueue(new Callback<APIResponse<Integer>>() {
            @Override
            public void onResponse(Call<APIResponse<Integer>> call, Response<APIResponse<Integer>> response) {
                if (response.isSuccessful()) {
                    Integer view = response.body().getResult();
                    chapterResponse.setView(view);
                }
                callback.run();
            }

            @Override
            public void onFailure(Call<APIResponse<Integer>> call, Throwable t) {
                Log.e("ChapterFragment", "onFailure: " + t.getMessage());
                callback.run(); // Call the callback function even if the API call fails
            }
        });
    }

    public void getChapter(int idComic) {
        bookAPI.getChaptersByBookId(idComic).enqueue(new Callback<List<ChapterResponse>>() {
            @Override
            public void onResponse(Call<List<ChapterResponse>> call, Response<List<ChapterResponse>> response) {
                if (response.isSuccessful()) {
                    List<ChapterResponse> list = response.body();
                    if (list != null && !list.isEmpty()) {
                        int totalChapters = list.size();
                        int[] counter = {0};

                        for (ChapterResponse chapterResponse : list) {
                            getViewEachChapter(chapterResponse.getId(), chapterResponse, () -> {
                                synchronized (counter) {
                                    counter[0]++;
                                    if (counter[0] == totalChapters) {
                                        rcvAdapter = new ChapterAdapter(getContext(), list);
                                        rcv.setAdapter(rcvAdapter);
                                    }
                                }
                            });
                        }
                    } else {
                        rcvAdapter = new ChapterAdapter(getContext(), list);
                        rcv.setAdapter(rcvAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ChapterResponse>> call, Throwable t) {
                Log.e("ChapterFragment", "onFailure: " + t.getMessage());
            }
        });
    }

}