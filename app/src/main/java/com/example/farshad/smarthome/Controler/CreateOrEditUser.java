package com.example.farshad.smarthome.Controler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farshad.smarthome.Model.UserModel;
import com.example.farshad.smarthome.R;
import com.example.farshad.smarthome.Services.UserModelProvider;
import com.example.farshad.smarthome.Services.UserModelService;
import com.example.farshad.smarthome.Utility.UserConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrEditUser extends AppCompatActivity {
    private int mActionToDo = UserConstants.NEW_USER;
    private String mUserIdInEditMode;
    private UserModelService uMService;
    private UserModel mExUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_edit_user);


        //findViewById(R.id.name).setText(mExUser.getName());

        //get argument and check is edit mode
        Bundle args = getIntent().getExtras();
        if (args != null) {
            mActionToDo = args.getInt(UserConstants.ACTION_TO_DO_KEY, UserConstants.NEW_USER);
            if (mActionToDo == UserConstants.EDIT_USER) {
                mUserIdInEditMode = args.getString(UserConstants.USER_ID_KEY, "");
            }

        }


        //first create new instant of UserModelProvider
        UserModelProvider provider = new UserModelProvider();
        //get the UserModelProvider interface to call API routes
        uMService = provider.getUService();

        if (mActionToDo == UserConstants.EDIT_USER && !mUserIdInEditMode.equals("")) {
            Call<UserModel> call = uMService.getUserById(mUserIdInEditMode);
            call.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                    if (response.isSuccessful()) {
                        mExUser = response.body();
                        setToEditViewContent();
                    }else if (response.code() == 500) {
                        Toast.makeText(getBaseContext(), "Internal Server Error ", Toast.LENGTH_LONG).show();
                        //return;
                    }
                }

                @Override
                public void onFailure(Call<UserModel> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mActionToDo == UserConstants.EDIT_USER) {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_add, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Call<Boolean> call = uMService.addNewUser(getFromEditViewContent());
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Successfully Added", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    }else if (response.code() == 500) {
                        Toast.makeText(getBaseContext(), "Internal Server Error ", Toast.LENGTH_SHORT).show();
                        //return;
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {

                }
            });

        } else if (id == R.id.action_edit) {
            Call<UserModel> call = uMService.updateUserById(Integer.parseInt(mUserIdInEditMode), getFromEditViewContent());
            call.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Successfully updated", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    }else if(response.code() == 500) {
                        Toast.makeText(getBaseContext(), "Internal Server Error ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                @Override
                public void onFailure(Call<UserModel> call, Throwable t) {

                }
            });

        } else if (id == android.R.id.home) {
            //back to main activity
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private void setToEditViewContent() {
        //findViewById(R.id.edit_or_create_user).
        TextView name = (TextView) findViewById(R.id.name);
        TextView lastname = (TextView) findViewById(R.id.lastname);
        TextView username = (TextView) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);
        EditText password2 = (EditText) findViewById(R.id.confirm_password);
        name.setText(mExUser.getName().toString());
        lastname.setText(mExUser.getLastname().toString());
        username.setText(mExUser.getUsername().toString());
        password.setText(mExUser.getPassword().toString());
        password2.setText(mExUser.getPassword().toString());
    }

    private UserModel getFromEditViewContent() {

        UserModel um = new UserModel();
        TextView name = (TextView) findViewById(R.id.name);
        TextView lastname = (TextView) findViewById(R.id.lastname);
        TextView username = (TextView) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);
        EditText password2 = (EditText) findViewById(R.id.confirm_password);
        if (!isEmpty(name) && !isEmpty(lastname) && !isEmpty(username) && isPasswordMatch(password, password2)) {
            um.setName(name.getText().toString());
            um.setLastname(lastname.getText().toString());
            um.setUsername(username.getText().toString());
            um.setPassword(password.getText().toString());
            return um;
        }else{
            return null;}

    }

    private boolean isEmpty(TextView tmp) {
        if (TextUtils.isEmpty(tmp.getText().toString())) {
            tmp.setError(getString(R.string.error_field_required));
            return true;
        }
        return false;
    }

    private boolean isPasswordMatch(EditText pswrd, EditText pswrd2) {
        if (TextUtils.isEmpty(pswrd.getText().toString()) && isPasswordValid(pswrd.getText().toString()))
            pswrd.setError(getString(R.string.error_invalid_password));
        if (TextUtils.isEmpty(pswrd.getText().toString()) && isPasswordValid(pswrd.getText().toString()))
            pswrd2.setError(getString(R.string.error_invalid_password));
        if (!pswrd.getText().toString().equals(pswrd2.getText().toString()))
            pswrd2.setError(getString(R.string.error_invalid_not_matched));
        return true;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


}
