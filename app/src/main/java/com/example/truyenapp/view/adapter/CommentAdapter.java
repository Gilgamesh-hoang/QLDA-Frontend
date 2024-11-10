package com.example.truyenapp.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.truyenapp.R;
import com.example.truyenapp.paging.PagingAdapter;
import com.example.truyenapp.response.CommentResponse;
import com.example.truyenapp.utils.Format;

import java.util.List;

public class CommentAdapter extends PagingAdapter<CommentResponse, CommentAdapter.CommentViewHolder> {

    public CommentAdapter(Context context, List<CommentResponse> list) {
        super(context, list);
        setItemRcv(R.layout.item_rcv_comment);
    }

    public void setData(List<CommentResponse> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    protected CommentViewHolder createItemViewHolder(View view) {
        return new CommentViewHolder(view);
    }

    @Override
    protected void bindData(CommentViewHolder holder, CommentResponse comment) {
        if (comment == null) {
            return;
        }
        String link = comment.getUser().getAvatar();
        String email = comment.getUser().getEmail();
        String nameChapter = comment.getChapterName();
        if (link!= null) {
            Glide.with(this.context).load(link).into(holder.avatar);
        }
        holder.name.setText(email);
        holder.content.setText(comment.getContent());
        holder.date.setText(Format.formatDate(comment.getCreatedAt().toString(), "yyyy-MM-dd", "dd-MM-yyyy"));
        holder.chapter.setText(nameChapter);
    }


    class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView name, content, date, chapter;
        private ImageView avatar;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.tv_item_comment_content);
            name = itemView.findViewById(R.id.tv_item_comment_name);
            date = itemView.findViewById(R.id.tv_item_comment_date);
            chapter = itemView.findViewById(R.id.tv_item_comment_chapter);
            avatar = itemView.findViewById(R.id.image_item_comment_avatar);
        }
    }


}
