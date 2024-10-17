package com.example.truyenapp.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.truyenapp.R;
import com.example.truyenapp.constraints.BundleConstraint;
import com.example.truyenapp.paging.PagingAdapter;
import com.example.truyenapp.response.BookResponse;
import com.example.truyenapp.utils.Format;
//import com.example.truyenapp.view.activity.DetailComicActivity;

import java.util.List;

public class ComicNewAdapter extends PagingAdapter<BookResponse, ComicNewAdapter.NewHolder> {

    public ComicNewAdapter(Context context, List<BookResponse> list) {
        super(context, list);
    }

    @Override
    protected NewHolder createItemViewHolder(View view) {
        return new NewHolder(view);
    }

    @Override
    protected void bindData(NewHolder holder, BookResponse comic) {
        if (comic == null) {
            return;
        }
        if (comic.getThumbnail() != null)
            Glide.with(this.context).load(comic.getThumbnail()).into(holder.imgComic);
        holder.nameComic.setText(comic.getName());
        holder.dateComic.setText("Ngày đăng: " + Format.formatDate(comic.getPublishDate().toString(), "yyyy-MM-dd", "dd-MM-yyyy"));
        holder.info.setVisibility(View.GONE);
        holder.detailComicView.setOnClickListener(view -> {
//            Intent intent = new Intent(holder.itemView.getContext(), DetailComicActivity.class);
//            intent.putExtra(BundleConstraint.ID_COMIC, comic.getId());
//            intent.putExtra(BundleConstraint.LINK_IMG, comic.getThumbnail());
//            holder.itemView.getContext().startActivity(intent);
        });
    }

    public class NewHolder extends RecyclerView.ViewHolder {
        private View detailComicView;
        private ImageView imgComic;
        private TextView nameComic;
        private TextView dateComic;
        private TextView info;

        public NewHolder(@NonNull View view) {
            super(view);
            this.imgComic = view.findViewById(R.id.item_rcv_thumnail);
            this.nameComic = view.findViewById(R.id.item_rcv_name_commic);
            this.dateComic = view.findViewById(R.id.item_rcv_date_commic);
            this.detailComicView = view.findViewById(R.id.item_detail_commic);
            this.info = view.findViewById(R.id.item_rcv_info_commic);

        }
    }

}
