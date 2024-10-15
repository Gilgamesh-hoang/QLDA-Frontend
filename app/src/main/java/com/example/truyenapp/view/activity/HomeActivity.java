package com.example.truyenapp.view.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.truyenapp.R;
import com.example.truyenapp.api.RetrofitClient;
import com.example.truyenapp.api.UserAPI;
import com.example.truyenapp.model.JWTToken;
import com.example.truyenapp.response.APIResponse;
import com.example.truyenapp.utils.AuthenticationManager;
import com.example.truyenapp.utils.DialogEvent;
import com.example.truyenapp.utils.DialogHelper;
import com.example.truyenapp.utils.SharedPreferencesHelper;
import com.example.truyenapp.utils.SystemConstant;
import com.example.truyenapp.view.fragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import lombok.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity {

    String email;
    int numberNotification;
    BottomNavigationView bottomNavigationView;


//    private void initNavigateBottom() {
//        bottomNavigationView = findViewById(R.id.bottom_nav);
//        bottomNavigationView.show(1, true);
//        bottomNavigationView.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home));
//        bottomNavigationView.add(new MeowBottomNavigation.Model(2, R.drawable.ic_noti));
//        bottomNavigationView.add(new MeowBottomNavigation.Model(4, R.drawable.ic_baseline_account));
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

//        initNavigateBottom();
////        getNumberNotifications();
//        if (numberNotification != 0) {
//            bottomNavigationView.setCount(2, numberNotification + "");
//        }

        handleEventNav();
    }

    /**
     * Handle event navigation
     * author: Hoang
     * status: done
     */
    private void handleEventNav() {
        boolean isLoggedIn = AuthenticationManager.isLoggedIn(SharedPreferencesHelper.getObject(getApplicationContext(), SystemConstant.JWT_TOKEN, JWTToken.class));
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.home:
                        selectedFragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, selectedFragment).commit();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
        bottomNavigationView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!isLoggedIn) {
                    showDialogLogin().show();
                }
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
    }

    public AlertDialog.Builder showDialogLogin() {
        DialogHelper dialogHelper = new DialogHelper(this, new DialogEvent() {
            @Override
            public void onPositiveClick() {
            }

            @Override
            public void onNegativeClick() {
                defaultIdNav();
            }

            @Override
            public void onCancel() {
                defaultIdNav();
            }
        });
        return dialogHelper.showDialogLogin();
    }

    private void defaultIdNav() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, new HomeFragment())
                .commit();
    }
}