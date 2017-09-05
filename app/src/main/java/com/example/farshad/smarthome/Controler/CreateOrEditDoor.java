package com.example.farshad.smarthome.Controler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farshad.smarthome.Model.DoorModel;
import com.example.farshad.smarthome.Model.UserModel;
import com.example.farshad.smarthome.R;
import com.example.farshad.smarthome.Services.DoorModelProvider;
import com.example.farshad.smarthome.Services.DoorModelService;
import com.example.farshad.smarthome.Utility.DoorConstants;
import com.example.farshad.smarthome.Utility.UserConstants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrEditDoor extends AppCompatActivity {
    private int mActionToDo = DoorConstants.NEW_DOOR;
    private String mDoorIdInEditMode;
    private DoorModelService dMService;
    private DoorModel mExDoor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_edit_door);


        //findViewById(R.id.name).setText(mExDoor.getName());

        //get argument and check is edit modeD
        Bundle args = getIntent().getExtras();
        if (args != null) {
            mActionToDo = args.getInt(DoorConstants.ACTION_TO_DO_KEY, DoorConstants.NEW_DOOR);
            if (mActionToDo == DoorConstants.EDIT_DOOR) {
                mDoorIdInEditMode = args.getString(DoorConstants.DOOR_ID_KEY, "");
            }

        }


        //first create new instant of DoorModelProvider
        DoorModelProvider provider = new DoorModelProvider();
        //get the DoorModelProvider interface to call API routes
        dMService = provider.getDService();

        if (mActionToDo == DoorConstants.EDIT_DOOR && !mDoorIdInEditMode.equals("")) {
            Call<DoorModel> call = dMService.getDoorById(mDoorIdInEditMode);
            call.enqueue(new Callback<DoorModel>() {
                @Override
                public void onResponse(Call<DoorModel> call, Response<DoorModel> response) {
                    if (response.isSuccessful()) {
                        mExDoor = response.body();
                        setToEditViewContent();
                    }else if (response.code() == 500) {
                        Toast.makeText(getBaseContext(), "Internal Server Error ", Toast.LENGTH_LONG).show();
                        //return;
                    }
                }

                @Override
                public void onFailure(Call<DoorModel> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mActionToDo == DoorConstants.EDIT_DOOR) {
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
            Call<Boolean> call = dMService.addNewDoor(getFromEditViewContent());
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
            Call<DoorModel> call = dMService.updateDoorById(Integer.parseInt(mDoorIdInEditMode), getFromEditViewContent());
            call.enqueue(new Callback<DoorModel>() {
                @Override
                public void onResponse(Call<DoorModel> call, Response<DoorModel> response) {
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
                public void onFailure(Call<DoorModel> call, Throwable t) {

                }
            });

        } else if (id == android.R.id.home) {
            //back to main activity
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private void setToEditViewContent() {
        //findViewById(R.id.edit_or_create_door).
        TextView door_id = (TextView) findViewById(R.id.door_pin);
        TextView door_image_id = (TextView) findViewById(R.id.door_image_id);
        Spinner family_id = (Spinner) findViewById(R.id.family_id);
        EditText door_name = (EditText) findViewById(R.id.door_name);
        EditText door_description = (EditText) findViewById(R.id.door_description);
        door_id.setText(mExDoor.getDoorId());
        //door_image_id.setText(mExDoor.getD().toString());
        //family_id.setText(mExDoor.get());
        door_name.setText(mExDoor.getDoorName());
        door_description.setText(mExDoor.getDoorDes().toString());
    }

    private DoorModel getFromEditViewContent() {

        DoorModel dm = new DoorModel();
        TextView door_id = (TextView) findViewById(R.id.door_pin);
        TextView door_image_id = (TextView) findViewById(R.id.door_image_id);
        Spinner family_id = (Spinner) findViewById(R.id.family_id);
        EditText door_name = (EditText) findViewById(R.id.door_name);
        EditText door_description = (EditText) findViewById(R.id.door_description);
        if (!isEmpty(door_id) && !isEmpty(door_name) && !isEmpty(door_image_id) ) {
            dm.setDoorName(door_id.getText().toString());
            //dm.setDoorId(door_image_id.getText().toString());
            //dm.setFamilyId(familyId.getText().toString());
            dm.setDoorName(door_name.getText().toString());
            dm.setDoorDes(door_description.getText().toString());
            return dm;
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
