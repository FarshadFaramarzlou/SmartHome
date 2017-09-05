package com.example.farshad.smarthome.Controler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.farshad.smarthome.Adapter.UserRecyclerAdapter;
import com.example.farshad.smarthome.Model.UserModel;
import com.example.farshad.smarthome.R;
import com.example.farshad.smarthome.Services.UserModelProvider;
import com.example.farshad.smarthome.Services.UserModelService;
import com.example.farshad.smarthome.Utility.AppPreferenceTools;
import com.example.farshad.smarthome.Utility.UserConstants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//import static com.example.farshad.smarthome.R.id.btnOpenDoor;


public class UserActivity extends AppCompatActivity {

    private RecyclerView userRecylerView;
    private UserRecyclerAdapter userAdapter;
    private AppPreferenceTools mAppPreferenceTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        mAppPreferenceTools = new AppPreferenceTools(this);
        if (mAppPreferenceTools.isAuthorized())
            setUpUserRecyclerView();
    }

    private void setUpUserRecyclerView() {

        userRecylerView = (RecyclerView) findViewById(R.id.userRecylerView);
        userAdapter = new UserRecyclerAdapter(this, new UserRecyclerAdapter.UserEventHandler() {
            @Override
            public void onEditUser(String userId, int position) {

                Intent editUserIntent = new Intent(getBaseContext(), CreateOrEditUser.class);
                editUserIntent.putExtra(UserConstants.ACTION_TO_DO_KEY, UserConstants.EDIT_USER);
                editUserIntent.putExtra(UserConstants.USER_ID_KEY, userId);
                startActivityForResult(editUserIntent, UserConstants.CREATE_OR_EDIT_USER_REQUEST_CODE);
            }

            @Override
            public void onDeletUser(String userId, final int position) {
                UserModelProvider userProvider = new UserModelProvider();
                //Call api route
                UserModelService userService = userProvider.getUService();
                Call<Boolean> call = userService.deleteUserById(userId);

                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if(response.isSuccessful()){
                            userAdapter.userList.remove(position);
                            userAdapter.notifyItemRemoved(position);
                            userAdapter.notifyItemRangeChanged(position, userAdapter.userList.size());
                        } else if (response.code() == 406) {
                            Toast.makeText(getBaseContext(), "Can not Delete ", Toast.LENGTH_SHORT).show();
                            //return;
                    }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onAddUser() {

            }

        });
        userRecylerView.setAdapter(userAdapter);


        LinearLayoutManager mlinearLayoutManagerVertical = new LinearLayoutManager(this);
        mlinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        userRecylerView.setLayoutManager(mlinearLayoutManagerVertical);

        userRecylerView.setItemAnimator(new DefaultItemAnimator());

        getUsersFromServer();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_user) {
            Intent editUserIntent = new Intent(getBaseContext(), CreateOrEditUser.class);
            editUserIntent.putExtra(UserConstants.ACTION_TO_DO_KEY, UserConstants.NEW_USER);
            startActivityForResult(editUserIntent, UserConstants.CREATE_OR_EDIT_USER_REQUEST_CODE);
        } else if (id == android.R.id.home) {
            //back to main activity
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUsersFromServer() {
        //int[] images = getImages();

        UserModelProvider userProvider = new UserModelProvider();
        //Call api route
        UserModelService userService = userProvider.getUService();
        Call<List<UserModel>> call = userService.getUsers(Integer.parseInt(mAppPreferenceTools.getUserType()), Integer.parseInt(mAppPreferenceTools.getFamilyId()));

        call.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                if (response.isSuccessful()) {
                    userAdapter.updateUserAdapterData(response.body());
                    userAdapter.notifyDataSetChanged();
                } else if (response.code() == 500) {
                    Toast.makeText(getBaseContext(), "Intarnal Server Error", Toast.LENGTH_SHORT).show();
                    //return;
                }
            }

            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {
                //Toast.makeText(getBaseContext(), "R F", Toast.LENGTH_SHORT);

            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == UserConstants.CREATE_OR_EDIT_USER_REQUEST_CODE && resultCode == RESULT_OK) {
            getUsersFromServer();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
