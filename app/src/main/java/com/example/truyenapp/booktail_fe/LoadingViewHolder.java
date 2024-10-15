package com.example.truyenapp.booktail_fe;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.truyenapp.R;
import lombok.Getter;
@Getter
public class LoadingViewHolder extends RecyclerView.ViewHolder {
    private ProgressBar progressBar;
    public LoadingViewHolder(@NonNull View itemView) {
        super(itemView);
        this.progressBar = itemView.findViewById(R.id.progress_bar);
    }
}