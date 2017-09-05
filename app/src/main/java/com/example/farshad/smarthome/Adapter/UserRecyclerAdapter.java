package com.example.farshad.smarthome.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.farshad.smarthome.Model.UserModel;
import com.example.farshad.smarthome.R;

import java.util.Collections;
import java.util.List;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by Farshad on 7/11/2017.
 */

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.UserViewHolder> {

    public List<UserModel> userList = Collections.emptyList();
    private LayoutInflater userInflater;
    private UserEventHandler mUserEventHandler;

    public UserRecyclerAdapter(Context context/*,ArrayList<DoorModel> Data*/, UserEventHandler userEventHandler) {
        //this.userList = Data;
        this.userInflater = LayoutInflater.from(context);
        this.mUserEventHandler = userEventHandler;
    }

    public void updateUserAdapterData(List<UserModel> data) {
        this.userList = data;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = userInflater.inflate(R.layout.list_view_user, parent, false);
        UserViewHolder userHolder = new UserViewHolder(view);
        return userHolder;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder " + position);

        UserModel currentUser = userList.get(position);
        holder.setData(currentUser, position);
        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void removeUser(int position, UserModel user) {
        if (mUserEventHandler != null) {
            mUserEventHandler.onDeletUser(String.valueOf(userList.get(position).getUserid()), position);
        }
    }

    public void addUser(int position, UserModel user) {
        userList.add(position, user);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, userList.size());
    }

    public void editUser(int position, UserModel user) {
        if (mUserEventHandler != null) {
            mUserEventHandler.onEditUser(String.valueOf(userList.get(position).getUserid()), position);
            userList.add(position, user);
            notifyItemInserted(position);
            notifyItemRangeChanged(position, userList.size());
        }

    }


    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, username;
        ImageView imgThumb, imgEdit, imgDelete;
        int position;
        UserModel currentUser;

        public UserViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.userTitle);
            username = (TextView) itemView.findViewById(R.id.username);
            imgThumb = (ImageView) itemView.findViewById(R.id.imgRow);
            imgEdit = (ImageView) itemView.findViewById(R.id.img_row_edit);
            imgDelete = (ImageView) itemView.findViewById(R.id.img_row_delete);
        }

        public void setData(UserModel currentUser, int position) {
            this.name.setText(currentUser.getName());
            this.imgThumb.setImageResource(/*currentUser.getUserid()*/0);
            this.position = position;
            this.currentUser = currentUser;
        }

        public void setListeners() {
            imgDelete.setOnClickListener(UserViewHolder.this);
            imgEdit.setOnClickListener(UserViewHolder.this);
        }

        public void onClick(View v) {
            Log.d(TAG, "onClick brfore operation at position: " + position + "size: " + userList.size());
            switch (v.getId()) {
                case R.id.img_row_delete:
                    removeUser(position, currentUser);
                    break;
                case R.id.img_row_edit:
                    editUser(position, currentUser);
                    break;
            }
            Log.d(TAG, "onClick after operatin - size " + userList.size());
        }
    }

    public interface UserEventHandler {

        void onEditUser(String userId, int position);

        void onDeletUser(String userId, int position);

        void onAddUser();

    }
}
