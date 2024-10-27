package com.example.truyenapp.view.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenapp.R;
import com.example.truyenapp.admin.CommentDetailManagementActivity;
import com.example.truyenapp.api.CommentAPI;
import com.example.truyenapp.api.RetrofitClient;
import com.example.truyenapp.constraints.BundleConstraint;
import com.example.truyenapp.enums.CommentState;
import com.example.truyenapp.model.Comment;
import com.example.truyenapp.request.CommentChangeRequest;
import com.example.truyenapp.response.APIResponse;
import com.example.truyenapp.utils.DialogEvent;
import com.example.truyenapp.utils.DialogHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentManagerViewHolder extends RecyclerView.ViewHolder implements DialogEvent {
    Context context;
    private TextView id, state, name, chapter;
    private Button btnAction;
    private Comment comment;
    private CommentState stateCanChange;
    private DialogHelper dialogHelper;

    public CommentManagerViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        id = itemView.findViewById(R.id.tv_comment_manager_id);
        state = itemView.findViewById(R.id.tv_comment_manager_state);
        name = itemView.findViewById(R.id.tv_comment_manager_name);
        chapter = itemView.findViewById(R.id.tv_comment_manager_chapter);
        btnAction = itemView.findViewById(R.id.btn_comment_manager_action);
        dialogHelper = new DialogHelper(itemView.getContext(), this);
    }

    public void setData(Comment comment) {
        Log.d("Comment", comment.toString());
        if (comment == null) {
            return;
        }
        this.comment = comment;
        id.setText("" + comment.getId());
        name.setText(comment.getBookName());
        String chapterNumber = comment.getChapterName().replace("Chapter", "").trim();
        chapter.setText(chapterNumber);
        btnAction.setTextColor(Color.WHITE);
        updateState();
    }

    public void handleOnCLick() {
        this.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, CommentDetailManagementActivity.class);
            intent.putExtra(BundleConstraint.ID_COMMENT, comment.getId());
            context.startActivity(intent);
        });
        this.btnAction.setOnClickListener(view -> {
            dialogHelper.showDialogCommentState(stateCanChange).show();
        });
    }

    private void changeState() {
        CommentChangeRequest request = new CommentChangeRequest(this.comment.getId(), stateCanChange.getState());
        Call<APIResponse> call = RetrofitClient.getInstance(itemView.getContext()).create(CommentAPI.class).changeState(request);
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                APIResponse apiResponse = response.body();
                if (apiResponse.getCode() == 200) {
                    if (stateCanChange == CommentState.HIDE)
                        comment.setState(CommentState.HIDE.getState());
                    else comment.setState(CommentState.SHOW.getState());
                    updateState();
                    dialogHelper.showDialogChangeStateCommentSuccess().show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    @Override
    public void onPositiveClick() {
        changeState();
    }

    @Override
    public void onNegativeClick() {

    }

    @Override
    public void onCancel() {

    }

    private void updateState() {
        if (this.comment.getState() == CommentState.HIDE.getState()) {
            state.setText("Bị khóa");
            btnAction.setText("Mở khóa");
            btnAction.setBackgroundColor(Color.GREEN);
            stateCanChange = CommentState.SHOW;
        } else {
            state.setText("Hoạt động");
            btnAction.setText("Khóa");
            btnAction.setBackgroundColor(Color.RED);
            stateCanChange = CommentState.HIDE;
        }
    }
}
