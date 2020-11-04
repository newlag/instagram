package com.example.instagram.fragments;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.example.instagram.activities.MainActivity;

public abstract class BaseFragment extends Fragment {
    public void addFragment(Fragment fragment) {
        ((MainActivity) getActivity()).addFragment(fragment);
    }

    public void setSupportActionBar(Toolbar toolbar) {
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
    }
}
