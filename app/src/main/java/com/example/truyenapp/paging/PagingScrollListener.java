package com.example.truyenapp.paging;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PagingScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager layoutManager;

    public PagingScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

//        Đếm số lượng item đang được hiển thị trên màn hình
        int visibleItemCount = layoutManager.getChildCount();
//        Đếm tổng số item trong page
        int totalItemCount = layoutManager.getItemCount();
//        Vị trí position của item đầu tiên được hiển thị
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

//        Log.d("PagingScrollListener", "onScrolled: " + visibleItemCount + " " + totalItemCount + " " + firstVisibleItemPosition);
//        Log.d("PagingScrollListener", "onScrolled: " + isLoading() + " " + isLastPage());
//        Nếu trang đang load hoặc đã là trang cuối cùng thì không load thêm
        if (isLoading() || isLastPage()) return;
//        Nếu vị trs item đầu tiên hiển thị >0 và tổng số item hiển thị + vị trí item đầu tiên = tổng số item trong page thì load thêm item
        if (firstVisibleItemPosition >= 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
//            Log.d("PagingScrollListener", "onScrolled: load more item");
            this.loadMoreItem();
        }
    }

    public abstract void loadMoreItem();

    public abstract boolean isLoading();

    public abstract boolean isLastPage();
}
