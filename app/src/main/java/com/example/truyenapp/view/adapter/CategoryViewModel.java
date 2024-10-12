package com.example.truyenapp.view.adapter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CategoryViewModel extends ViewModel {
    private final MutableLiveData<Integer> categoryId = new MutableLiveData<>();

    public void setCategoryId(Integer categoryId) {
        this.categoryId.setValue(categoryId);
    }

    public LiveData<Integer> getCategoryId() {
        return categoryId;
    }
}
