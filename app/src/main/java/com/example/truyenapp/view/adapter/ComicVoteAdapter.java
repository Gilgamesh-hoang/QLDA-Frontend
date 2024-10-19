package com.example.truyenapp.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.truyenapp.R;
import com.example.truyenapp.constraints.BundleConstraint;
import com.example.truyenapp.paging.PagingAdapter;
import com.example.truyenapp.response.BookResponse;
import com.example.truyenapp.utils.Format;
//import com.example.truyenapp.view.activity.DetailComicActivity;

import java.util.List;

public class ComicVoteAdapter extends PagingAdapter<BookResponse, ComicVoteAdapter.VoteViewHolder> {
    public ComicVoteAdapter(Context context, List<BookResponse> list) {
        super(context, list);
    }

    @Override
    protected VoteViewHolder createItemViewHolder(View view) {
        return new VoteViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindData(VoteViewHolder holder, BookResponse comic) {
        if (comic == null) {
            return;
        }
        Log.d("date", comic.getPublishDate() + "");
        String publishDate = Format.formatDate(comic.getPublishDate().toString(), "yyyy-MM-dd", "dd-MM-yyyy");
        Glide.with(this.context).load(comic.getThumbnail()).into(holder.imgCommic);
        holder.nameCommic.setText(comic.getName());
        holder.info.setText("Đánh giá: " + Format.roundNumber(comic.getRating()));
        holder.dateCommic.setText("Ngày đăng: " + publishDate);
        holder.detailCommicView.setOnClickListener(view -> {
//            Intent intent = new Intent(holder.itemView.getContext(), DetailComicActivity.class);
//            intent.putExtra(BundleConstraint.ID_COMIC, comic.getId());
//            intent.putExtra(BundleConstraint.LINK_IMG, comic.getThumbnail());
//            holder.itemView.getContext().startActivity(intent);
        });
    }

    public class VoteViewHolder extends RecyclerView.ViewHolder {
        private View detailCommicView;
        private ImageView imgCommic;
        private TextView nameCommic;
        private TextView dateCommic;
        private TextView info;

        public VoteViewHolder(View view) {
            super(view);
            this.imgCommic = view.findViewById(R.id.item_rcv_thumnail);
            this.nameCommic = view.findViewById(R.id.item_rcv_name_commic);
            this.info = view.findViewById(R.id.item_rcv_info_commic);
            this.dateCommic = view.findViewById(R.id.item_rcv_date_commic);
            this.detailCommicView = view.findViewById(R.id.item_detail_commic);
        }
    }
}
