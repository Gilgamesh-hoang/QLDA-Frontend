package com.example.truyenapp.view.adapter.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenapp.R;
import com.example.truyenapp.admin.ShowChapterInfo;
import com.example.truyenapp.response.ChapterResponse;

import java.util.List;

public class ChapterManagementAdapter extends RecyclerView.Adapter<ChapterManagementAdapter.ChapterManagementHolder> {
    private Context context;
    private List<ChapterResponse> list;

    public ChapterManagementAdapter(Context context, List<ChapterResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChapterManagementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcv_book_management, parent, false);
        return new ChapterManagementHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChapterManagementHolder holder, int position) {
        ChapterResponse chapter = list.get(position);
        if (chapter == null) {
            return;
        }

        holder.tv_idqlthongke.setText("" + chapter.getId());
        holder.tv_qltktentruyen.setText(chapter.getName());
        holder.ll_rcv_qlthongke.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), ShowChapterInfo.class);
            intent.putExtra("chapterId", chapter.getId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class ChapterManagementHolder extends RecyclerView.ViewHolder {
        private TextView tv_idqlthongke, tv_qltktentruyen;
        private LinearLayout ll_rcv_qlthongke;

        public ChapterManagementHolder(@NonNull View itemView) {
            super(itemView);
            tv_idqlthongke = itemView.findViewById(R.id.tv_idqlthongke);
            tv_qltktentruyen = itemView.findViewById(R.id.tv_qltktentruyen);
            ll_rcv_qlthongke = itemView.findViewById(R.id.ll_rcv_qlthongke);
        }
    }
}
