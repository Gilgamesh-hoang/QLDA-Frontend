package com.example.truyenapp.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenapp.R;
import com.example.truyenapp.api.BookAPI;
import com.example.truyenapp.api.CategoryAPI;
import com.example.truyenapp.api.RetrofitClient;
import com.example.truyenapp.api.UserAPI;
import com.example.truyenapp.model.JWTToken;
import com.example.truyenapp.request.BookRequest;
import com.example.truyenapp.response.APIResponse;
import com.example.truyenapp.response.BookResponse;
import com.example.truyenapp.response.CategoryResponse;
import com.example.truyenapp.utils.AuthenticationManager;
import com.example.truyenapp.utils.SharedPreferencesHelper;
import com.example.truyenapp.utils.SystemConstant;
import com.example.truyenapp.utils.UploadImage;
import com.example.truyenapp.view.adapter.admin.BookManagementAdapter;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookManagement extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView rcv;
    private BookManagementAdapter adapter;
    private ImageView addBookImg, thumbnailImg;
    private Button addBtn, cancelBtn, thumbnailBtn;
    private EditText bookNameText, authorText, descriptionText;
    private CardView addBookCard;
    private UserAPI userAPI;
    private BookAPI bookAPI;
    private CategoryAPI categoryAPI;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    private ProgressBar progressBar;
    private FrameLayout progressContainer;
    private TextView dialogCategory;

    private String[] categories;
    private List<String> selectedCategories;
    boolean[] checkedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_management);
        connectAPI();

        JWTToken token = SharedPreferencesHelper.getObject(this, SystemConstant.JWT_TOKEN, JWTToken.class);
        if (AuthenticationManager.isAdmin(token, userAPI)) {
            Toast.makeText(this, "Bạn không có quyền truy cập", Toast.LENGTH_SHORT).show();
            finish();
        }

        init();
        setOnClickListener();
        recyclerView();
        getCategories();
        selectedCategories = new ArrayList<>();
    }

    private void connectAPI() {
        userAPI = RetrofitClient.getInstance(this).create(UserAPI.class);
        bookAPI = RetrofitClient.getInstance(this).create(BookAPI.class);
        categoryAPI = RetrofitClient.getInstance(this).create(CategoryAPI.class);
    }

    private synchronized void getCategories() {
        categoryAPI.getAll().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (response.isSuccessful() && !response.body().isEmpty()) {

                    categories = new String[response.body().size()];
                    checkedItems = new boolean[categories.length];
                    for (int i = 0; i < response.body().size(); i++) {
                        categories[i] = response.body().get(i).getName();
                    }

                } else {
                    Toast.makeText(BookManagement.this, "Không thể lấy thể loại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                Toast.makeText(BookManagement.this, "Không thể lấy thể loại", Toast.LENGTH_SHORT).show();
                Log.e("Error", t.getMessage());
            }
        });
    }

    private void showMultiSelectDialog() {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the list of items to display in the dialog. The items are the categories.
        // The checkedItems array represents whether each item is checked.
        // The listener is called when an item is clicked.
        builder.setMultiChoiceItems(categories, checkedItems, (dialog, which, isChecked) -> {
            // Get the category that was clicked
            String selectedCategory = categories[which];

            // If the item is checked
            if (isChecked) {
                // If the selected category is not already in the list of selected categories
                if (!selectedCategories.contains(selectedCategory)) {
                    // Add the selected category to the list and mark the item as checked
                    selectedCategories.add(selectedCategory);
                    checkedItems[which] = true;
                }
            } else {
                // If the item is unchecked, remove the selected category from the list and mark the item as unchecked
                selectedCategories.remove(selectedCategory);
                checkedItems[which] = false;
            }
        });

        // Set the positive (OK) button and its click listener
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Create a StringBuilder to build the string of selected categories
            StringBuilder selectedString = new StringBuilder();

            // Loop through the list of selected categories
            for (String category : selectedCategories) {
                // Append each category and a comma to the string
                selectedString.append(category).append(", ");
            }

            // If the string is not empty, remove the last comma
            if (selectedString.length() > 0)
                selectedString.deleteCharAt(selectedString.length() - 2);

            // Set the text of the dialogCategory TextView to the string of selected categories
            dialogCategory.setText(selectedString.toString());
        });

        // Set the negative (Cancel) button and its click listener
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Do nothing when the Cancel button is clicked
        });

        // Show the dialog
        builder.show();
    }

    private void recyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcv.setLayoutManager(linearLayoutManager);
        bookAPI.getAllBooks().enqueue(new Callback<List<BookResponse>>() {
            @Override
            public void onResponse(Call<List<BookResponse>> call, Response<List<BookResponse>> response) {
                if (response.isSuccessful()) {
                    adapter = new BookManagementAdapter(getApplicationContext(), response.body());
                    rcv.setAdapter(adapter);
                } else {
                    Toast.makeText(BookManagement.this, "Không thể lấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BookResponse>> call, Throwable t) {
                Toast.makeText(BookManagement.this, "Không thể lấy dữ liệu", Toast.LENGTH_SHORT).show();
                Log.e("Error", t.getMessage());
            }
        });

    }

    private void init() {
        rcv = findViewById(R.id.rcv_quanlytruyen);
        addBookImg = findViewById(R.id.addBookImg);
        thumbnailImg = findViewById(R.id.thumbnailImg);
        cancelBtn = findViewById(R.id.bt_huy_newtruyen);
        addBtn = findViewById(R.id.bt_them_newtruyen);
        bookNameText = findViewById(R.id.edt_tentruyen_newtruyen);
        authorText = findViewById(R.id.edt_tg_newtruyen);
        descriptionText = findViewById(R.id.edt_mota_newtruyen);
        thumbnailBtn = findViewById(R.id.selectThumbnailBtn);
        addBookCard = findViewById(R.id.cv_themtruyen);
        progressBar = findViewById(R.id.progress_spin_kit);
        progressContainer = findViewById(R.id.progress_container);
        dialogCategory = findViewById(R.id.dialog_category);
    }

    private void setOnClickListener() {
        addBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        addBookImg.setOnClickListener(this);
        thumbnailBtn.setOnClickListener(this);
        dialogCategory.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addBookImg:
                addBookCard.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_huy_newtruyen:
                addBookCard.setVisibility(View.GONE);
                break;
            case R.id.bt_them_newtruyen:
                addBook();
                break;
            case R.id.selectThumbnailBtn:
                openImagePicker();
                break;
            case R.id.dialog_category:
                showMultiSelectDialog();
                break;
        }
    }

    //Image upload
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // The user has successfully picked an image
            this.imageUri = data.getData();

            thumbnailImg.setImageURI(imageUri);
            thumbnailImg.setVisibility(View.VISIBLE);
        }
    }




    private void performActionsAfterUpload(BookRequest bookRequest) {
        UploadImage.uploadImageToFirebase(this, this.imageUri).thenAccept(downloadUrl -> {
            bookRequest.setThumbnail(downloadUrl);
            handleAddBook(bookRequest);
        }).exceptionally(throwable -> {
            Log.e("TAG", "Failed to upload image or get URL", throwable);
            Toast.makeText(this, "Failed to upload image or get URL: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            cancelProgressBar();
            return null;
        });
    }


    private void addBook() {
        String bookName = bookNameText.getText().toString().trim();
        String author = authorText.getText().toString().trim();
        String description = descriptionText.getText().toString().trim();

        if (bookName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên truyện", Toast.LENGTH_SHORT).show();
        } else if (author.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tác giả", Toast.LENGTH_SHORT).show();
        } else if (description.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
        } else if (selectedCategories.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập thể loại", Toast.LENGTH_SHORT).show();
        } else if (imageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
        } else {

            BookRequest bookRequest = BookRequest.builder()
                    .name(bookName)
                    .author(author)
                    .description(description)
                    .categoryNames(selectedCategories)
                    .status(SystemConstant.STATUS_UPDATING_KEY)
                    .build();

            showProgressBar(new Wave());
            performActionsAfterUpload(bookRequest);

        }
    }

    private void showProgressBar(Sprite style) {
        progressBar.setIndeterminateDrawable(style);
        progressContainer.setVisibility(View.VISIBLE);
    }

    private void cancelProgressBar() {
        progressContainer.setVisibility(View.GONE);
    }

    private void handleAddBook(BookRequest bookRequest) {
        bookAPI.addNewBook(bookRequest).enqueue(new Callback<APIResponse<Void>>() {
            @Override
            public void onResponse(Call<APIResponse<Void>> call, Response<APIResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BookManagement.this, "Thêm truyện thành công", Toast.LENGTH_SHORT).show();
                    reload();
                    recyclerView();
                } else {
                    int statusCode = response.code();
                    if (statusCode == 409) {
                        Toast.makeText(BookManagement.this, "Truyện đã tồn tại", Toast.LENGTH_SHORT).show();
                    } else if (statusCode == 500) {
                        Toast.makeText(BookManagement.this, "Lỗi server", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BookManagement.this, "Không thể thêm truyện", Toast.LENGTH_SHORT).show();
                    }

                }
                cancelProgressBar();
            }

            @Override
            public void onFailure(Call<APIResponse<Void>> call, Throwable t) {
                Toast.makeText(BookManagement.this, "Không thể thêm truyện", Toast.LENGTH_SHORT).show();
                Log.e("Error", t.getMessage());
                cancelProgressBar();
            }
        });
    }

    private void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
