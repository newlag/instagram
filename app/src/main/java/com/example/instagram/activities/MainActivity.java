package com.example.instagram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.instagram.R;
import com.example.instagram.fragments.NotificationFragment;
import com.example.instagram.fragments.SearchFragment;
import com.example.instagram.data.User;
import com.example.instagram.fragments.PostListFragment;
import com.example.instagram.fragments.ProfileFragment;
import com.example.instagram.presenters.MainPresenter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    BottomNavigationView navigation;
    public static User user;
    private Fragment homeFragment, searchFragment, addFragment, notificationFragment, profileFragment, activityFragment;

    private MainPresenter presenter = new MainPresenter();

    private HashMap<Integer, Stack<Fragment>> fragments = new HashMap<>();
    private int currentTab;
    private Fragment currentFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) { // Обработка кликов bottom navigation view
        int id = menuItem.getItemId();

        if (id == R.id.action_add) {
            startActivity(new Intent(MainActivity.this, UploadActivity.class));
            return true;
        }

        currentTab = id;
        Stack<Fragment> fragmentStack = fragments.get(id);
        if (fragmentStack == null) { // Если первый раз открываем данную вкладку
            createTab(id);
        } else {
            showFragment(fragmentStack.lastElement(), true);
        }

        return true;
    }

    private void createTab(int id) {
        Fragment fragment = null;
        switch (id) {
            case R.id.action_home:
                fragment = PostListFragment.newInstance();
            break;

            case R.id.action_search:
                fragment = SearchFragment.newInstance();
            break;

            case R.id.action_like:
                fragment = new NotificationFragment();
            break;

            case R.id.action_profile:
                fragment = ProfileFragment.newInstance(presenter.getUserId());
            break;
        }
        fragments.put(id, new Stack<Fragment>());
        fragments.get(currentTab).push(fragment);
        showFragment(fragment, false);


    }

    private void showFragment(Fragment fragment, boolean isFragmentCreated) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragment != null) {

            if (fragmentManager.findFragmentById(R.id.fragment_container) == null) { // Первый фрагмент
                fragmentManager.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
            } else if (!isFragmentCreated) { // Если фрагмент ещё не создан
                fragmentManager.beginTransaction()
                        .hide(currentFragment)
                        .add(R.id.fragment_container, fragment)
                        .commit();

            } else {
                fragmentManager.beginTransaction()
                    .hide(currentFragment)
                    .show(fragment)
                    .commit();
            }
            currentFragment = fragment;
        }
    }

    public void addFragment(Fragment fragment) {
        if (fragment != null) {
            fragments.get(currentTab).push(fragment);
            showFragment(fragment, false);
        }
    }

    private void prevFragment() {
        fragments.get(currentTab).pop(); // Выкидываем текущий фрагмент
        Fragment fragment = fragments.get(currentTab).lastElement(); // Заряжаем предыдущий
        showFragment(fragment, true);
    }

    @Override
    public void onBackPressed() {
        if (fragments.get(currentTab).size() == 1) {
            finish();
        } else {
            prevFragment();
        }
    }
}
