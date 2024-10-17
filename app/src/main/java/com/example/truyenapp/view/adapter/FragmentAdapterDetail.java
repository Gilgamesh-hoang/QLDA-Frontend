package com.example.truyenapp.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

//import com.example.truyenapp.view.fragment.ChapterFragment;
import com.example.truyenapp.view.fragment.DetailFragment;

public class FragmentAdapterDetail extends FragmentStateAdapter {
    private int idComic;
    public FragmentAdapterDetail(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle){
        super(fragmentManager,lifecycle);
    }

    public void setIdComic(int idComic) {
        this.idComic = idComic;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 1:
//                return new ChapterFragment(idComic);
        }
        return new DetailFragment(idComic);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
