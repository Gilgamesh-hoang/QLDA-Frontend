package com.example.truyenapp.view.adapter.admin;

import android.content.Context;
import android.view.View;

import com.example.truyenapp.R;
import com.example.truyenapp.model.Comment;
import com.example.truyenapp.paging.PagingAdapter;
import com.example.truyenapp.view.viewHolder.CommentManagerViewHolder;

import java.util.List;

public class CommentManagerAdapter extends PagingAdapter<Comment, CommentManagerViewHolder> {
    public CommentManagerAdapter(Context context, List<Comment> list) {
        super(context, list);
        setItemRcv(R.layout.item_rcv_comment_management);
    }

    public void setData(List<Comment> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    protected CommentManagerViewHolder createItemViewHolder(View view) {
        return new CommentManagerViewHolder(view);
    }

    @Override
    protected void bindData(CommentManagerViewHolder holder, Comment comment) {
        if (comment == null) {
            return;
        }
        holder.setData(comment);
        holder.handleOnCLick();
    }
}
