package com.example.truyenapp.admin;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.truyenapp.R;
import com.example.truyenapp.api.BookAPI;
import com.example.truyenapp.api.CategoryAPI;
import com.example.truyenapp.api.ChapterAPI;
import com.example.truyenapp.api.RetrofitClient;
import com.example.truyenapp.api.UserAPI;
import com.example.truyenapp.model.JWTToken;
import com.example.truyenapp.request.BookRequest;
import com.example.truyenapp.response.APIResponse;
import com.example.truyenapp.response.BookResponse;
import com.example.truyenapp.response.CategoryResponse;
import com.example.truyenapp.response.ChapterResponse;
import com.example.truyenapp.utils.AuthenticationManager;
import com.example.truyenapp.utils.SharedPreferencesHelper;
import com.example.truyenapp.utils.StatusMapUtil;
import com.example.truyenapp.utils.SystemConstant;
import com.example.truyenapp.utils.UploadImage;
import com.example.truyenapp.view.adapter.admin.ChapterManagementAdapter;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailManagement extends AppCompatActivity implements View.OnClickListener {

    private ImageView thumbnail;
    private TextView bookId, dialogCategory, dialogStatus;
    private EditText bookAuthor, bookDescription, bookName;
    private Button editBtn, saveBtn, cancelEditBtn, deleteStoryBtn, thumbnailBtn;
    private int id;
    private RecyclerView rcv;
    private ChapterManagementAdapter adapter;
    private BookAPI bookAPI;
    private UserAPI userAPI;
    private CategoryAPI categoryAPI;
    private ChapterAPI chapterAPI;
    private BookResponse bookResponse;
    private LinearLayout editThumbnailComponent;
    private String[] categories;
    private static final int PICK_IMAGE_REQUEST = 1;
    private List<String> selectedCategories;
    private boolean[] checkedItems;
    private final String[] statuses = StatusMapUtil.getStatusMap().values().toArray(new String[0]) ;

    private String selectedStatus = null;
    private int selectedStatusIndex = -1;
    private Uri imageUri;
    private ProgressBar progressBar;
    private FrameLayout progressContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail_management);
        connectAPI();

        JWTToken token = SharedPreferencesHelper.getObject(this, SystemConstant.JWT_TOKEN, JWTToken.class);
        if (AuthenticationManager.isAdmin(token, userAPI)) {
            Toast.makeText(this, "Bạn không có quyền truy cập", Toast.LENGTH_SHORT).show();
            finish();
        }

        init();
        Intent intent = getIntent();
        id = intent.getIntExtra("bookId", 1);
        setEnable(false);
        setOnClickListener();
        getBook();
    }

    private void connectAPI() {
        userAPI = RetrofitClient.getInstance(this).create(UserAPI.class);
        bookAPI = RetrofitClient.getInstance(this).create(BookAPI.class);
        chapterAPI = RetrofitClient.getInstance(this).create(ChapterAPI.class);
        categoryAPI = RetrofitClient.getInstance(this).create(CategoryAPI.class);
    }

    // This method is used to fetch a book's details from the server
    private void getBook() {
        // Call the getBook method from the bookAPI interface, passing the book's id
        bookAPI.getBook(id).enqueue(new Callback<BookResponse>() {
            // This method is called when the server responds to our request
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                // Check if the response is successful
                if (response.isSuccessful()) {
                    // If the response is successful, store the book's details in the bookResponse object
                    bookResponse = response.body();

                    getCategories();
                    // Set the data in the UI
                    setData();
                    // Set up the RecyclerView for the chapters
                    chapterRecycleView();
                } else {
                    // If the response is not successful, show a toast message indicating that the book's details could not be fetched
                    Toast.makeText(BookDetailManagement.this, "Failed to get book", Toast.LENGTH_SHORT).show();
                }
            }

            // This method is called when the request to the server fails
            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                // Show a toast message indicating that the request failed
                Toast.makeText(BookDetailManagement.this, "Failed to get book", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // This method is used to fetch all categories from the server
    private void getCategories() {
        // Call the getAll method from the categoryAPI interface
        // This method is asynchronous and uses Retrofit's enqueue method to send the request
        categoryAPI.getAll().enqueue(new Callback<List<CategoryResponse>>() {
            // This method is called when the server responds to our request
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                // Check if the response is successful and the response body is not empty
                if (response.isSuccessful() && !response.body().isEmpty()) {
                    // Initialize the categories array with the size of the response body
                    categories = new String[response.body().size()];
                    // Initialize the checkedItems array with the same size as the categories array
                    checkedItems = new boolean[categories.length];
                    // Get the list of categories from the bookResponse object
                    selectedCategories = bookResponse.getCategoryNames();

                    // Loop through the response body
                    for (int i = 0; i < response.body().size(); i++) {
                        // Get the name of each category
                        String category = response.body().get(i).getName();
                        // Store the category name in the categories array
                        categories[i] = category;
                        // If the selected categories contain the current category, mark the item as checked
                        if (selectedCategories.contains(category)) {
                            checkedItems[i] = true;
                        }
                    }
                }
            }

            // This method is called when the request to the server fails
            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                // Log the error message
                Log.e("Error", t.getMessage());
            }
        });
    }

    private void showDialogCategories() {
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

    // This method is used to show a dialog for selecting the status of a book
    private void showDialogStatuses() {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the list of items to display in the dialog. The items are the statuses.
        // The selectedStatusIndex represents the currently selected status.
        // The listener is called when an item is clicked.
        builder.setSingleChoiceItems(statuses, selectedStatusIndex, (dialog, which) -> {
            // Store the selected status
            selectedStatus = statuses[which];
            // Update the selected status index
            selectedStatusIndex = which;
        });

        // Set the positive (OK) button and its click listener
        builder.setPositiveButton("OK", (dialog, which) -> {
            // If a status is selected, set the text of the dialogStatus TextView to the selected status
            if (selectedStatus != null) {
                dialogStatus.setText(selectedStatus);
            }
        });

        // Set the negative (Cancel) button and its click listener
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Do nothing when the Cancel button is clicked
        });

        // Show the dialog
        builder.show();
    }


    // This method is used to set up the RecyclerView for displaying chapters of a book
    private void chapterRecycleView() {
        // Create a new LinearLayoutManager
        // This will position the items in your RecyclerView vertically
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        // Set the RecyclerView's layout manager to the LinearLayoutManager we just created
        rcv.setLayoutManager(linearLayoutManager);

        // Call the getChaptersByBook method from the chapterAPI interface, passing the book's id
        // This method is asynchronous and uses Retrofit's enqueue method to send the request
        chapterAPI.getChaptersByBook(id).enqueue(new Callback<List<ChapterResponse>>() {
            // This method is called when the server responds to our request
            @Override
            public void onResponse(Call<List<ChapterResponse>> call, Response<List<ChapterResponse>> response) {
                // Check if the response is successful
                if (response.isSuccessful()) {
                    // If the response is successful, store the list of chapters in the list object
                    List<ChapterResponse> list = response.body();

                    // Create a new QLChapterAdapter with the list of chapters
                    // This adapter will be used to display each chapter in the RecyclerView
                    adapter = new ChapterManagementAdapter(BookDetailManagement.this, list);

                    // Set the RecyclerView's adapter to the QLChapterAdapter we just created
                    rcv.setAdapter(adapter);
                } else {
                    // If the response is not successful, show a toast message indicating that the chapters could not be fetched
                    Toast.makeText(BookDetailManagement.this, "Failed to get chapters", Toast.LENGTH_SHORT).show();
                }
            }

            // This method is called when the request to the server fails
            @Override
            public void onFailure(Call<List<ChapterResponse>> call, Throwable t) {
                // Show a toast message indicating that the request failed
                Toast.makeText(BookDetailManagement.this, "Failed to get chapters", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData() {
        Glide.with(this).load(bookResponse.getThumbnail()).into(thumbnail);
        bookName.setText(bookResponse.getName());
        bookAuthor.setText(bookResponse.getAuthor());
        bookDescription.setText(bookResponse.getDescription());
        bookId.setText(String.valueOf(bookResponse.getId()));

        selectedStatus = StatusMapUtil.getValue(bookResponse.getStatus());
        dialogStatus.setText(selectedStatus);
        // Cập nhật chỉ mục đã chọn dựa trên giá trị của selectedStatus
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(selectedStatus)) {
                selectedStatusIndex = i;
                break;
            }
        }

        StringBuilder categories = new StringBuilder();
        for (String category : bookResponse.getCategoryNames()) {
            categories.append(category).append(", ");
        }
        categories.deleteCharAt(categories.length() - 2);
        dialogCategory.setText(categories.toString());
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
            thumbnail.setImageURI(imageUri);
        }
    }

    private void setOnClickListener() {
        editBtn.setOnClickListener(this);
        cancelEditBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        deleteStoryBtn.setOnClickListener(this);
        thumbnailBtn.setOnClickListener(this);
        dialogCategory.setOnClickListener(this);
        dialogStatus.setOnClickListener(this);
    }

    private void init() {
        thumbnail = findViewById(R.id.img_qlt);
        thumbnailBtn = findViewById(R.id.selectThumbnailBtn);
        editBtn = findViewById(R.id.bt_chinhsuatruyen);
        bookId = findViewById(R.id.tv_qlt_id);
        bookAuthor = findViewById(R.id.edt_qlt_tacgia);
        bookDescription = findViewById(R.id.edt_qlt_mota);
        dialogCategory = findViewById(R.id.edt_qlt_theloai);
        dialogStatus = findViewById(R.id.edt_qlt_trangthai);
        bookName = findViewById(R.id.edt_qlt_tentruyen);
        saveBtn = findViewById(R.id.bt_xacnhantruyen);
        cancelEditBtn = findViewById(R.id.bt_huychinhsuatruyen);
        rcv = findViewById(R.id.rcv_quanlychapter);
        deleteStoryBtn = findViewById(R.id.bt_deleteStory);
        editThumbnailComponent = findViewById(R.id.edit_thumbnail_component);
        progressBar = findViewById(R.id.progress_spin_kit);
        progressContainer = findViewById(R.id.progress_container);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_chinhsuatruyen:
                setEnable(true);
                break;
            case R.id.bt_deleteStory:
                showDeleteConfirmationDialog();
                break;
            case R.id.bt_huychinhsuatruyen:
                setEnable(false);
                reload();
                break;
            case R.id.bt_xacnhantruyen:
                handleUpdateBook();
                break;
            case R.id.edt_qlt_theloai:
                showDialogCategories();
                break;
            case R.id.edt_qlt_trangthai:
                showDialogStatuses();
                break;
            case R.id.selectThumbnailBtn:
                openImagePicker();
                break;
        }
    }

    // This method is used to handle the update of a book's details
    private void handleUpdateBook() {
        // Get the book's name, author, and description from the corresponding EditText fields
        // Trim the strings to remove any leading or trailing white space
        String name = bookName.getText().toString().trim();
        String author = bookAuthor.getText().toString().trim();
        String description = bookDescription.getText().toString().trim();

        // Check if the name, author, or description is empty
        // If any of them is empty, show a toast message asking the user to enter the missing information
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên truyện", Toast.LENGTH_SHORT).show();
        } else if (author.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tác giả", Toast.LENGTH_SHORT).show();
        } else if (description.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
        } else if (selectedCategories.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập thể loại", Toast.LENGTH_SHORT).show();
        } else if (selectedStatus == null) {
            Toast.makeText(this, "Vui lòng trạng thái", Toast.LENGTH_SHORT).show();
        } else {
            // If all the required information is provided, create a new BookRequest object
            // Set the book's id, name, author, description, categories, and status in the BookRequest object
            BookRequest bookRequest = BookRequest.builder()
                    .id(bookResponse.getId())
                    .name(name)
                    .author(author)
                    .description(description)
                    .categoryNames(selectedCategories)
                    .status(StatusMapUtil.getKey(selectedStatus))
                    .build();

            // Show a progress bar while the book's details are being updated
            showProgressBar(new Wave());

            // Check if a new image has been selected for the book
            if (imageUri != null) {
                // If a new image has been selected, upload the image and update the book's details
                performActionsAfterUpload(bookRequest);
            } else {
                // If no new image has been selected, use the current image and update the book's details
                bookRequest.setThumbnail(bookResponse.getThumbnail());
                handleAddBook(bookRequest);
            }
        }
    }

    // This method is used to perform actions after an image has been uploaded
    private void performActionsAfterUpload(BookRequest bookRequest) {
        // Call the uploadImageToFirebase method from the UploadImage class
        // This method uploads the image to Firebase and returns a CompletableFuture that will be completed with the download URL of the image
        UploadImage.uploadImageToFirebase(this, this.imageUri).thenAccept(downloadUrl -> {
            // When the CompletableFuture is completed, set the thumbnail of the bookRequest to the download URL
            bookRequest.setThumbnail(downloadUrl);
            // Call the handleAddBook method to add the book to the database
            handleAddBook(bookRequest);
        }).exceptionally(throwable -> {
            // If the CompletableFuture is completed exceptionally (an error occurred), log the error and show a toast message
            Log.e("TAG", "Failed to upload image or get URL", throwable);
            Toast.makeText(this, "Failed to upload image or get URL: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            // Cancel the progress bar
            cancelProgressBar();
            return null;
        });
    }

    private void handleAddBook(BookRequest bookRequest) {
        bookAPI.updateBook(bookRequest).enqueue(new Callback<APIResponse<Void>>() {
            @Override
            public void onResponse(Call<APIResponse<Void>> call, Response<APIResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BookDetailManagement.this, "Sửa thông tin thành công", Toast.LENGTH_SHORT).show();
                    reload();
                } else {
                        Toast.makeText(BookDetailManagement.this, "Không thể thay đổi thong tin", Toast.LENGTH_SHORT).show();
                }
                cancelProgressBar();
            }

            @Override
            public void onFailure(Call<APIResponse<Void>> call, Throwable t) {
                Toast.makeText(BookDetailManagement.this, "Không thể thay đổi thong tin", Toast.LENGTH_SHORT).show();
                Log.e("Error", t.getMessage());
                cancelProgressBar();
            }
        });
    }

    private void showProgressBar(Sprite style) {
        progressBar.setIndeterminateDrawable(style);
        progressContainer.setVisibility(View.VISIBLE);
    }

    private void cancelProgressBar() {
        progressContainer.setVisibility(View.GONE);
    }

    // This method is used to delete a book
    private void handleDeleteStory() {
        // Call the deleteBook method from the bookAPI interface, passing the book's id
        bookAPI.deleteBook(bookResponse.getId()).enqueue(new Callback<APIResponse<Void>>() {
            // This method is called when the server responds to our request
            @Override
            public void onResponse(Call<APIResponse<Void>> call, Response<APIResponse<Void>> response) {
                // Check if the response is successful
                if (response.isSuccessful()) {
                    // If the response is successful, show a toast message indicating that the book was deleted
                    Toast.makeText(BookDetailManagement.this, "Đã xóa truyện", Toast.LENGTH_SHORT).show();
                    // Create a new Intent to start the BookManagement activity
                    Intent intent = new Intent(BookDetailManagement.this, BookManagement.class);
                    // Start the BookManagement activity
                    startActivity(intent);
                } else {
                    // If the response is not successful, show a toast message indicating that the book could not be deleted
                    Toast.makeText(BookDetailManagement.this, "Failed to delete book", Toast.LENGTH_SHORT).show();
                }
            }

            // This method is called when the request to the server fails
            @Override
            public void onFailure(Call<APIResponse<Void>> call, Throwable t) {
                // Show a toast message indicating that the request failed
                Toast.makeText(BookDetailManagement.this, "Failed to delete book", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // This method is used to show a confirmation dialog when the user attempts to delete a book
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the message to be displayed in the dialog
        builder.setMessage("Bạn có chắc muốn xóa cuốn sách này?");

        // Set the positive (Yes) button and its click listener
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // When the Yes button is clicked, call the handleDeleteStory method to delete the book
                handleDeleteStory();
            }
        });

        // Set the negative (No) button and its click listener
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // When the No button is clicked, close the dialog
                dialog.dismiss();
            }
        });

        // Create the AlertDialog using the builder
        AlertDialog alertDialog = builder.create();

        // Show the dialog
        alertDialog.show();
    }


    private void setEnable(boolean isEnable) {
        if (isEnable) {
            dialogStatus.setEnabled(true);
            bookName.setEnabled(true);
            bookDescription.setEnabled(true);
            dialogCategory.setEnabled(true);
            bookAuthor.setEnabled(true);
            editBtn.setVisibility(View.GONE);
            cancelEditBtn.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.VISIBLE);
            deleteStoryBtn.setVisibility(View.VISIBLE);
            editThumbnailComponent.setVisibility(View.VISIBLE);
        } else {
            dialogStatus.setEnabled(false);
            bookName.setEnabled(false);
            bookDescription.setEnabled(false);
            dialogCategory.setEnabled(false);
            bookAuthor.setEnabled(false);
            editBtn.setVisibility(View.VISIBLE);
            cancelEditBtn.setVisibility(View.GONE);
            saveBtn.setVisibility(View.GONE);
            deleteStoryBtn.setVisibility(View.GONE);
            editThumbnailComponent.setVisibility(View.GONE);
        }
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
