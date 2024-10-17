package com.example.truyenapp.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenapp.R;
import com.example.truyenapp.constraints.BundleConstraint;
import com.example.truyenapp.model.JWTToken;
import com.example.truyenapp.response.ChapterResponse;
import com.example.truyenapp.utils.AuthenticationManager;
import com.example.truyenapp.utils.Format;
import com.example.truyenapp.utils.SharedPreferencesHelper;
import com.example.truyenapp.utils.SystemConstant;
import com.example.truyenapp.view.activity.ReadChapterActivity;

import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
    private Context context;
    private List<ChapterResponse> list;
    private boolean isLogin;

    public ChapterAdapter(Context context, List<ChapterResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcv_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        ChapterResponse chapter = list.get(position);
        if (chapter == null) {
            return;
        }
        if (chapter.getView() == null){
            holder.view.setText("Lượt xem: " + 0);
        } else {
            holder.view.setText("Lượt xem: " + chapter.getView());
        }
        holder.textViewChapter.setText(chapter.getName());
        holder.ngayDang.setText("Ngày đăng: " + Format.formatDate(chapter.getPublishDate().toString(), "yyyy-MM-dd", "dd-MM-yyyy"));
        holder.chapterItem.setOnClickListener(view -> {
            this.isLogin = AuthenticationManager.isLoggedIn(SharedPreferencesHelper.getObject(context, SystemConstant.JWT_TOKEN, JWTToken.class));
            if (isLogin) {
                Intent intent = new Intent(holder.itemView.getContext(), ReadChapterActivity.class);
                intent.putExtra(BundleConstraint.QUANTITY, list.size());
                intent.putExtra(BundleConstraint.POSITION, position);
                intent.putStringArrayListExtra(BundleConstraint.LIST_CHAPTER_NAME, getChapterName());
                intent.putIntegerArrayListExtra(BundleConstraint.LIST_CHAPTER_ID, getChapterId());
                intent.putExtra(BundleConstraint.ID_CHAPTER, getChapterId().get(position));
                holder.itemView.getContext().startActivity(intent);
            } else {
                Toast.makeText(this.context, "Vui lòng đăng nhập để xem nội dung truyện!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class ChapterViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewChapter, ngayDang, view;
        private LinearLayout chapterItem;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewChapter = itemView.findViewById(R.id.tv_chapter);
            ngayDang = itemView.findViewById(R.id.tv_ngaydang);
            view = itemView.findViewById(R.id.tv_luotxem);
            chapterItem = itemView.findViewById(R.id.ll_rcv_chapter);
        }
    }

    public ArrayList<String> getChapterName() {
        ArrayList<String> listChapterName = list.stream().collect(ArrayList::new, (list, chapter) -> list.add(chapter.getName()), ArrayList::addAll);
        return listChapterName;
    }

    private ArrayList<Integer> getChapterId() {
        ArrayList<Integer> listChapterId = list.stream().collect(ArrayList::new, (list, chapter) -> list.add(chapter.getId()), ArrayList::addAll);
        return listChapterId;
    }
}
