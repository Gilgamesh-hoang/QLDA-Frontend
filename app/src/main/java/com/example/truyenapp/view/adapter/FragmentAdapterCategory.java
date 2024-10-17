package com.example.truyenapp.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.truyenapp.view.fragment.ComicNewFragment;
import com.example.truyenapp.view.fragment.ComicViewFragment;
import com.example.truyenapp.view.fragment.ComicVoteFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapterCategory extends FragmentStateAdapter {
    ComicVoteFragment comicVoteFragment = new ComicVoteFragment();
    ComicViewFragment comicViewFragment = new ComicViewFragment();
    ComicNewFragment comicNewFragment = new ComicNewFragment();

    private final List<Fragment> fragmentList = new ArrayList<>();

    public FragmentAdapterCategory(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        fragmentList.add(comicNewFragment);
        fragmentList.add(comicVoteFragment);
        fragmentList.add(comicViewFragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return comicVoteFragment;
            case 2:
                return comicViewFragment;
        }
        return comicNewFragment;
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public Fragment getFragment(int position) {
        return fragmentList.get(position);
    }
}
