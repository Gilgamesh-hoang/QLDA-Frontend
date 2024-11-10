package com.example.truyenapp.view.adapter.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenapp.R;
import com.example.truyenapp.response.ChapterContentRespone;

import java.util.List;

public class ChapterContentAdapter extends RecyclerView.Adapter<ChapterContentAdapter.ChapterContentHolder> {
    private Context context;
    private List<ChapterContentRespone> list;

    public ChapterContentAdapter(Context context, List<ChapterContentRespone> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChapterContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcv_book_management, parent, false);
        return new ChapterContentHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChapterContentHolder holder, int position) {
        ChapterContentRespone content = list.get(position);
        if (content == null) {
            return;
        }

        holder.idText.setText("" + content.getId());
        holder.content.setText(content.getLinkImage());
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class ChapterContentHolder extends RecyclerView.ViewHolder {
        private TextView idText, content;

        public ChapterContentHolder(@NonNull View itemView) {
            super(itemView);
            idText = itemView.findViewById(R.id.tv_idqlthongke);
            content = itemView.findViewById(R.id.tv_qltktentruyen);
        }
    }
}
