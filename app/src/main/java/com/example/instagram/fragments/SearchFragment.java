package com.example.instagram.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.R;
import com.example.instagram.presenters.SearchPresenter;
import com.example.instagram.data.User;

import java.util.ArrayList;

public class SearchFragment extends BaseFragment implements SearchPresenter.connectionError {


    private SearchPresenter presenter;
    private RecyclerView recyclerView;
    private static ArrayList<User> users = new ArrayList<>();
    private Toolbar toolbar;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        presenter = new SearchPresenter(this);
        recyclerView = v.findViewById(R.id.search_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new UserListAdapter());
        toolbar = v.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(null);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_toolbar, menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search_toolbar_action).getActionView().findViewById(R.id.search_view);

        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.onActionViewExpanded();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                presenter.findUsers(s, new SearchPresenter.onUsersFound() {
                    @Override
                    public void onSuccess(ArrayList<User> result) {
                        updateList(result);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                presenter.findUsers(s, new SearchPresenter.onUsersFound() {
                    @Override
                    public void onSuccess(ArrayList<User> result) {
                        updateList(result);
                    }
                });
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateList(ArrayList<User> list) {
        users = list;
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        String profileId;
        TextView username, fullname;
        ImageView photo;

        public UserHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.user_list_item, parent, false));
            username = itemView.findViewById(R.id.content);
            fullname = itemView.findViewById(R.id.fullname_label);
            photo = itemView.findViewById(R.id.user_photo);
            itemView.setOnClickListener(this);
        }

        public void Bind(User user) {
            profileId = user.getUser_id();
            username.setText(user.getUsername());
            if (user.getFullname().length() > 0) {
                fullname.setVisibility(View.VISIBLE);
                fullname.setText(user.getFullname());
            } else {
                fullname.setVisibility(View.GONE);
            }
            Glide.with(getActivity())
                .load(user.getImage())
                .apply(new RequestOptions()
                .placeholder(R.color.colorGrey)
                .error(R.drawable.default_avatar))
                .into(photo);
        }

        @Override
        public void onClick(View view) {
            addFragment(ProfileFragment.newInstance(profileId));
        }
    }

    public class UserListAdapter extends RecyclerView.Adapter<UserHolder> {

        @NonNull
        @Override
        public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UserHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull UserHolder holder, int position) {
            User user = users.get(position);
            holder.Bind(user);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }

    @Override
    public void showConnectionError() {
        Toast.makeText(getActivity(), R.string.error_connection, Toast.LENGTH_SHORT).show();
    }
}
