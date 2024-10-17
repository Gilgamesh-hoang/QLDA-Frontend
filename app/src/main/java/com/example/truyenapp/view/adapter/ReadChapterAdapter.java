package com.example.truyenapp.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.truyenapp.R;
import com.example.truyenapp.response.ChapterContentRespone;

import java.util.List;

public class ReadChapterAdapter extends RecyclerView.Adapter<ReadChapterAdapter.ReadChapterViewHolder> {

    private List<ChapterContentRespone> list;
    private Context context;

    public ReadChapterAdapter(List<ChapterContentRespone> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ReadChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcv_docchapter,parent,false);
        return new ReadChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadChapterViewHolder holder, int position) {
        ChapterContentRespone contentOfChapter =list.get(position);
        if(contentOfChapter ==null){
            return;
        }

        Glide.with(this.context).load(contentOfChapter.getLinkImage()).into(holder.img_docchapter);
    }

    @Override
    public int getItemCount() {
        if(list!=null){
            return list.size();
        }
        return 0;
    }

    public class ReadChapterViewHolder extends RecyclerView.ViewHolder{
        private ImageView img_docchapter;

        public ReadChapterViewHolder(@NonNull View itemView) {
            super(itemView);

            img_docchapter=itemView.findViewById(R.id.img_docchapter);
        }
    }
}
