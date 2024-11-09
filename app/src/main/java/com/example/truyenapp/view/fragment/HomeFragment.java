package com.example.truyenapp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyenapp.R;
import com.example.truyenapp.api.RetrofitClient;
import com.example.truyenapp.api.SearchAPI;
import com.example.truyenapp.api.UserAPI;
import com.example.truyenapp.mapper.BookMapper;
import com.example.truyenapp.model.Comic;
import com.example.truyenapp.model.JWTToken;
import com.example.truyenapp.response.APIResponse;
import com.example.truyenapp.response.BookResponse;
import com.example.truyenapp.response.DataListResponse;
import com.example.truyenapp.response.UserResponse;
import com.example.truyenapp.utils.AuthenticationManager;
import com.example.truyenapp.utils.DialogHelper;
import com.example.truyenapp.utils.SharedPreferencesHelper;
import com.example.truyenapp.utils.SystemConstant;
import com.example.truyenapp.view.activity.CategoryActivity;
import com.example.truyenapp.view.activity.SearchActivity;
import com.example.truyenapp.view.activity.SignInActivity;
import com.example.truyenapp.view.adapter.ComicSliderAdapter;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    Toolbar toolbar;
    ViewFlipper viewFlipper;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    View headerLayout, view;
    Button loginBtn;
    //    Button logoutBtn;
    Menu menu;
    MenuItem mn_it_chucnangquantri;
    TextView tv_TimKemHome, tv_xephang, tv_theloai, tv_usernamehome, tv_diemthuong, tv_diemdanh;
    String username, email;
    private UserResponse userResponse;

    private List<Comic> newComic = new ArrayList<>();
    private List<Comic> topComic = new ArrayList<>();
    private List<Comic> comicFullChapter = new ArrayList<>();
    private RecyclerView rv, rv2, rv3;
    private ComicSliderAdapter _rv, rv_2, rv_3;
    private UserAPI userAPI;

    private DialogHelper dialogHelper;

    private boolean isLoggedIn;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAPI = RetrofitClient.getInstance(getContext()).create(UserAPI.class);

    }

    /**
     * This method is used to fetch user information
     * author: Hoang
     */

    private void getUserInfo() {
        // Call the getUserInfo method from the UserAPI interface
        JWTToken jwtToken = SharedPreferencesHelper.getObject(getContext(), SystemConstant.JWT_TOKEN, JWTToken.class);
        if (jwtToken == null) {
            return;
        }
        userAPI.getUserInfo(jwtToken.getToken()).enqueue(new Callback<UserResponse>() {
            // This method is called when the server response is received
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                // Get the UserResponse object from the response
                UserResponse user = response.body();
                // Check if the user object is not null
                if (user != null) {
                    // Assign the user object to the userResponse variable
                    userResponse = user;
                    // Set the email of the user in the TextView tv_emailhome
                    username = user.getUsername();
                    tv_usernamehome.setText(username);
                    loginBtn.setVisibility(View.GONE);
                    verifyUserRole();
                }
            }

            // This method is called when the request could not be executed due to cancellation, a connectivity problem or a timeout
            @Override
            public void onFailure(Call<UserResponse> call, Throwable throwable) {
                // Log the error message
                Log.e("TAG", "Login failed: " + throwable.getMessage());
                // Show a toast message indicating that an error occurred
                Toast.makeText(getActivity(), "Lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void verifyUserRole() {
        if (userResponse != null) {
            mn_it_chucnangquantri.setVisible(SystemConstant.ROLE_ADMIN.equals(userResponse.getRole()));
        } else {
            mn_it_chucnangquantri.setVisible(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        getUserInfo();
        init();
        Intent i = getActivity().getIntent();
        email = i.getStringExtra("email");
        tv_usernamehome.setText(email);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);

        rv.setLayoutManager(linearLayoutManager);
        rv2.setLayoutManager(linearLayoutManager2);
        rv3.setLayoutManager(linearLayoutManager3);

        _rv = new ComicSliderAdapter(getActivity(), email);
        rv_2 = new ComicSliderAdapter(getActivity(), email);
        rv_3 = new ComicSliderAdapter(getActivity(), email);

        rv.setAdapter(_rv);
        rv2.setAdapter(rv_2);
        rv3.setAdapter(rv_3);

        getNewComic();
        getTopComic();
//        getFullComic();

        setEventActionBar();
        setEventViewFlipper();
        setOnClickListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.isLoggedIn = AuthenticationManager.isLoggedIn(SharedPreferencesHelper.getObject(getActivity().getApplicationContext(), SystemConstant.JWT_TOKEN, JWTToken.class));
    }

    private void init() {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.vf);
        navigationView = (NavigationView) view.findViewById(R.id.nvv);
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drlo);
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drlo);
        headerLayout = navigationView.inflateHeaderView(R.layout.fragment_header);
        loginBtn = (Button) headerLayout.findViewById(R.id.home_login_btn);


        tv_TimKemHome = (TextView) view.findViewById(R.id.tv_TimKiemHome);
        tv_xephang = (TextView) view.findViewById(R.id.tv_xephang);
        tv_theloai = (TextView) view.findViewById(R.id.tv_category);
        tv_diemthuong = view.findViewById(R.id.btn_to_redeem_reward);
        tv_diemdanh = view.findViewById(R.id.btn_attendance_home_fragment);
        rv = view.findViewById(R.id.rv);
        rv2 = view.findViewById(R.id.rv2);
        rv3 = view.findViewById(R.id.rv3);
        menu = navigationView.getMenu();
//        mn_it_chucnangquantri = menu.findItem(R.id.it_chucnangquantri);
        tv_usernamehome = headerLayout.findViewById(R.id.tv_usernamehome);

        this.dialogHelper = new DialogHelper(this.getContext());
    }

    private void setOnClickListener() {
        loginBtn.setOnClickListener(this);
        tv_TimKemHome.setOnClickListener(this);
        tv_xephang.setOnClickListener(this);
        tv_theloai.setOnClickListener(this);
        tv_diemthuong.setOnClickListener(this);
        tv_diemdanh.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_login_btn:
                Intent dialog_box = new Intent(getActivity(), SignInActivity.class);
                startActivity(dialog_box);
                getActivity().finish();
                break;
            default:
                break;
            case R.id.tv_TimKiemHome:
                Intent dialog_box1 = new Intent(getActivity(), SearchActivity.class);
                startActivity(dialog_box1);
                break;

            case R.id.tv_category:
                Intent dialog_box3 = new Intent(getActivity(), CategoryActivity.class);
                startActivity(dialog_box3);
                break;
        }
    }

    private void setEventViewFlipper() {
        List<String> arrGTSP = new ArrayList<>();
        arrGTSP.add("https://m.media-amazon.com/images/M/MV5BMjYxZjFkNTEtZjYzNC00MTdhLThiM2ItYmRiOWJiYTJkYzMxXkEyXkFqcGdeQXVyNDQxNjcxNQ@@._V1_FMjpg_UX1000_.jpg");
        arrGTSP.add("https://www.mangageko.com/media/manga_covers/36284.jpg");
        arrGTSP.add("https://upload.wikimedia.org/wikipedia/en/5/52/Y%C5%8Dkoso_Jitsuryoku_Shij%C5%8D_Shugi_no_Ky%C5%8Dshitsu_e%2C_Volume_1.jpg");
        arrGTSP.add("https://upload.wikimedia.org/wikipedia/vi/a/a7/That_Time_I_Got_Reincarnated_as_a_Slime_anime_official_poster.jpg");
        arrGTSP.add("https://i7.xem-truyen.com/manga/14/14743/uhazq28.thumb_500x.jpg");

        for (int i = 0; i < arrGTSP.size(); i++) {
            ImageView imageView = new ImageView(((AppCompatActivity) getActivity()).getApplicationContext());
            Picasso.with(((AppCompatActivity) getActivity()).getApplicationContext()).load(arrGTSP.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation anim_slide_in = AnimationUtils.loadAnimation(((AppCompatActivity) getActivity()).getApplicationContext(), R.anim.slide_in_right);
        Animation anim_slide_out = AnimationUtils.loadAnimation(((AppCompatActivity) getActivity()).getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(anim_slide_in);
        viewFlipper.setOutAnimation(anim_slide_out);
    }

    private void setEventActionBar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.nav_d_op, R.string.nav_d_cl);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
//            case R.id.it_quanlytaikhoan:
//                Intent dialog_box = new Intent(getActivity(), ManagementAccount.class);
//                startActivity(dialog_box);
//                break;
//            case R.id.it_quanlytruyen:
//                Intent dialog_box1 = new Intent(getActivity(), BookManagement.class);
//                startActivity(dialog_box1);
//                break;
//            case R.id.it_quanlybinhluan:
//                Intent dialog_box2 = new Intent(getActivity(), CommentManagementActivity.class);
//                startActivity(dialog_box2);
//                break;
//            case R.id.it_quanlythongke:
//                Intent dialog_box3 = new Intent(getActivity(), StatisticalManagement.class);
//                startActivity(dialog_box3);
//                break;
//            case R.id.it_xephang:
//                Intent dialog_box4 = new Intent(getActivity(), RankActivity.class);
//                startActivity(dialog_box4);
//                break;
//            case R.id.it_theloai:
//                Intent dialog_box5 = new Intent(getActivity(), CategoryActivity.class);
//                startActivity(dialog_box5);
//                break;
        }
        return true;
    }

    public void getNewComic() {
        SearchAPI response = RetrofitClient.getInstance(getContext()).create(SearchAPI.class);
        response.getNewComic(5).enqueue(new Callback<APIResponse<DataListResponse<BookResponse>>>() {
            @Override
            public void onResponse(Call<APIResponse<DataListResponse<BookResponse>>> call, Response<APIResponse<DataListResponse<BookResponse>>> response) {
                APIResponse<DataListResponse<BookResponse>> data = response.body();
                assert data != null;
                if (data.getCode() == 400 ) {
                    return;
                }
                for (BookResponse bookResponse : data.getResult().getData()) {
                    Comic comic = BookMapper.INSTANCE.bookResponseToStory(bookResponse);
                    newComic.add(comic);
                }
                _rv.setData(newComic);
            }

            @Override
            public void onFailure(Call<APIResponse<DataListResponse<BookResponse>>> call, Throwable throwable) {
                Log.e("TAG", "Can not get new comic: " + throwable.getMessage());
//                dialogHelper.showDialog("Lỗi, vui lòng thử lại").show();
            }
        });
    }

    public void getTopComic() {
        SearchAPI response = RetrofitClient.getInstance(getContext()).create(SearchAPI.class);
        response.getTopComic("rating", 5).enqueue(new Callback<APIResponse<DataListResponse<BookResponse>>>() {
            @Override
            public void onResponse(Call<APIResponse<DataListResponse<BookResponse>>> call, Response<APIResponse<DataListResponse<BookResponse>>> response) {
                APIResponse<DataListResponse<BookResponse>> data = response.body();
                assert data != null;
                if(data.getCode() == 400){
                    return;
                }
                for (BookResponse bookResponse : data.getResult().getData()) {
                    Comic comic = BookMapper.INSTANCE.bookResponseToStory(bookResponse);
                    topComic.add(comic);
                }
                rv_2.setData(topComic);
            }

            @Override
            public void onFailure(Call<APIResponse<DataListResponse<BookResponse>>> call, Throwable throwable) {
                Log.e("TAG", "Can not get top comic: " + throwable.getMessage());
//                dialogHelper.showDialog("Lỗi, vui lòng thử lại").show();
            }
        });
    }

//    public void getFullComic() {
//        SearchAPI response = RetrofitClient.getInstance(getContext()).create(SearchAPI.class);
//        response.getFullComic("", "desc", 5).enqueue(new Callback<APIResponse<DataListResponse<BookResponse>>>() {
//            @Override
//            public void onResponse(Call<APIResponse<DataListResponse<BookResponse>>> call, Response<APIResponse<DataListResponse<BookResponse>>> response) {
//
//                Log.d("API", response.toString());
//                APIResponse<DataListResponse<BookResponse>> data = response.body();
//                assert data != null;
//                if (data.getCode() == 400) {
//                    return;
//                }
//                for (BookResponse bookResponse : data.getResult().getData()) {
//                    Comic classifyComic = BookMapper.INSTANCE.bookResponseToStory(bookResponse);
//                    comicFullChapter.add(classifyComic);
//                }
//                rv_3.setData(comicFullChapter);
//            }
//
//            @Override
//            public void onFailure(Call<APIResponse<DataListResponse<BookResponse>>> call, Throwable throwable) {
//                Log.e("TAG", "Can not get comic full: " + throwable.getMessage());
////                dialogHelper.showDialog("Lỗi, vui lòng thử lại").show();
//            }
//        });
//    }

}