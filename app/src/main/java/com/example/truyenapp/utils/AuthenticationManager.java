package com.example.truyenapp.utils;

import android.util.Log;

import com.example.truyenapp.api.UserAPI;
import com.example.truyenapp.model.JWTToken;
import com.example.truyenapp.response.UserResponse;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationManager {

    public static boolean isLoggedIn(JWTToken token) {
        if (token == null)
            return false;
        return new Timestamp(System.currentTimeMillis()).before(new Timestamp(token.getExpiredTime()));
    }

    public static boolean isAdmin(JWTToken token, UserAPI userAPI) {
        // Check if the user is logged in
        if (!isLoggedIn(token))
            return false;

        // Create an AtomicBoolean to store the admin status
        AtomicBoolean isAdmin = new AtomicBoolean(false);

        // Call the checkAdmin method, which returns a CompletableFuture
        CompletableFuture<Boolean> userFuture = checkAdmin(token, userAPI);

        // When the CompletableFuture completes, set the value of isAdmin
        userFuture.thenAccept(isAdmin::set);

        // Return the value of isAdmin
        return isAdmin.get();
    }

    private static CompletableFuture<Boolean> checkAdmin(JWTToken token, UserAPI userAPI) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        userAPI.getUserInfo(token.getToken()).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse user = response.body();
                if (user != null) {
                    future.complete(user.getRole().equals(SystemConstant.ROLE_ADMIN));
                } else {
                    future.completeExceptionally(new Throwable("User is null"));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable throwable) {
                Log.e("TAG", throwable.getMessage());
                future.completeExceptionally(throwable);
            }
        });
        return future;
    }
}
