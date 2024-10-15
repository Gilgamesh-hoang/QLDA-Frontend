package com.example.truyenapp.booktail_fe;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.truyenapp.R;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class PagingAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected static final int TYPE_ITEM = 0;
    protected static final int TYPE_LOADING = 1;
    protected boolean isLoading;
    protected Context context;
    protected List<T> list;
    @Setter
    protected int itemRcv = R.layout.item_rcv_rank;
    protected final int ITEM_LOADING = R.layout.item_rcv_loading;
    public PagingAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
    }
    @Override
    public int getItemViewType(int position) {
        if (list != null && position == list.size() - 1 && isLoading)
            return TYPE_LOADING;
        return TYPE_ITEM;
    }
    public void addFooterLoading() {
        isLoading = true;
        list.add(null);
    }
    public void removeFooterLoading() {
        isLoading = false;
        int position = list.size() - 1;
        try {
            T t = list.get(position);
            if (t == null) {
                list.remove(position);
                notifyItemRemoved(position);
            }
        } catch (Exception ignored) {
        }
    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (TYPE_ITEM == viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(itemRcv, parent, false);
            return createItemViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(ITEM_LOADING, parent, false);
            return createLoadingViewHolder(view);
        }
    }
    protected abstract VH createItemViewHolder(View view);
    protected VH createLoadingViewHolder(View view) {
        return (VH) new LoadingViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Log.d("TAG", "onBindViewHolder: " + " " + list.size());
        if (holder.getItemViewType() == TYPE_ITEM) {
            bindData(holder, list.get(position));
        } else {
            ((LoadingViewHolder) holder).getProgressBar().setIndeterminate(true);
        }
    }
    protected abstract void bindData(VH holder, T object);
    @Override
    public int getItemCount() {
        if (list != null)
            return list.size();
        return 0;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void clearData() {
        list.clear();
        this.notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<T> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }
}
