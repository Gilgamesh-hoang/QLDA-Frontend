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
import com.example.truyenapp.admin.BookDetailManagement;
import com.example.truyenapp.response.BookResponse;

import java.util.List;

public class BookManagementAdapter extends RecyclerView.Adapter<BookManagementAdapter.BookManagementViewHolder> {
    private Context context;
    private List<BookResponse> list;

    public BookManagementAdapter(Context context, List<BookResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BookManagementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcv_book_management, parent, false);
        return new BookManagementViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BookManagementViewHolder holder, int position) {
        BookResponse book = list.get(position);
        if (book == null) {
            return;
        }

        holder.bookId.setText("" + book.getId());
        holder.bookName.setText(book.getName());
        holder.bookManagementLayout.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), BookDetailManagement.class);
            intent.putExtra("bookId", book.getId());
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

    public static class BookManagementViewHolder extends RecyclerView.ViewHolder {
        private TextView bookId, bookName;
        private LinearLayout bookManagementLayout;

        public BookManagementViewHolder(@NonNull View itemView) {
            super(itemView);

            bookId = itemView.findViewById(R.id.tv_idqlthongke);
            bookName = itemView.findViewById(R.id.tv_qltktentruyen);
            bookManagementLayout = itemView.findViewById(R.id.ll_rcv_qlthongke);

        }
    }
}
