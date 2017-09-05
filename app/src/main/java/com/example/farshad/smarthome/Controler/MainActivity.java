package com.example.farshad.smarthome.Controler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farshad.smarthome.Adapter.UserRecyclerAdapter;
import com.example.farshad.smarthome.Model.UserModel;
import com.example.farshad.smarthome.R;
import com.example.farshad.smarthome.Services.UserModelProvider;
import com.example.farshad.smarthome.Services.UserModelService;
import com.example.farshad.smarthome.TApplication;
import com.example.farshad.smarthome.Utility.AndroidUtilities;
import com.example.farshad.smarthome.Utility.AppPreferenceTools;
import com.example.farshad.smarthome.Utility.UserConstants;
import com.example.farshad.smarthome.Utility.CropCircleTransformation;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private UserRecyclerAdapter mAdapter;
    // private Service mTService;
    private AppPreferenceTools mAppPreferenceTools;
    private TextView mTxDisplayName;
    private ProgressDialog mProgressDialog;
    private UserModelService mTService;
    private ImageView mImImageProfile;
    private PermissionEventListener mPermissionEventListener;
    private ImageView mImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAppPreferenceTools = new AppPreferenceTools(this);

        if (mAppPreferenceTools.isAuthorized()) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
/*
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
*/
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            mTxDisplayName = (TextView) findViewById(R.id.tx_display_name);
            //mImImageProfile = (ImageView) toolbar.findViewById(R.id.im_image_profile);
            ImageButton mbtnLocks = (ImageButton) findViewById(R.id.imgBtnDoor);
            mbtnLocks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent doorLayout = new Intent(MainActivity.this, DoorLocks.class);
                    startActivity(doorLayout);
                }
            });

        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == UserConstants.CREATE_OR_EDIT_USER_REQUEST_CODE && resultCode == RESULT_OK) {
            //getTweetsFromServer();
        } else if (requestCode == UserConstants.UPDATE_USER_PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            //update the value of name in toolbar and re create the tweets Adapter
            mTxDisplayName.setText(mAppPreferenceTools.getUserName());
            mAdapter.notifyDataSetChanged();
        } else if (requestCode == UserConstants.GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                Uri selectedImageUri = data.getData();
                String extractUriFrom = selectedImageUri.toString();
                //check is from google photos or google drive
                if (extractUriFrom.contains("com.google.android.apps.photos.contentprovider") || extractUriFrom.contains("com.google.android.apps.docs.storage")) {
                    final int chunkSize = 1024;  // We'll read in one kB at a time
                    byte[] imageData = new byte[chunkSize];
                    File imageFile = AndroidUtilities.generateImagePath();
                    InputStream in = null;
                    OutputStream out = null;
                    mProgressDialog.setMessage("Loading ...");
                    mProgressDialog.show();
                    try {
                        in = getContentResolver().openInputStream(selectedImageUri);
                        out = new FileOutputStream(imageFile);
                        int bytesRead;
                        while ((bytesRead = in.read(imageData)) > 0) {
                            out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)));
                        }
                        uploadImageProfile(imageFile.getAbsolutePath());
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
//                        navigateToPhotoCropActivity();
                    } catch (Exception ex) {
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        Toast.makeText(getBaseContext(), "can not get this image :|", Toast.LENGTH_SHORT).show();
                        Log.e("Something went wrong.", ex.toString());
                    } finally {
                        if (in != null) {
                            in.close();
                        }
                        if (out != null) {
                            out.close();
                        }
                        if (mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                    }
                } else {
                    uploadImageProfile(AndroidUtilities.getPath(selectedImageUri));
                }
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), "something wrong :|", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * upload image profile and is success load it into ImageView
     *
     * @param imagePath
     */
    private void uploadImageProfile(String imagePath) {
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            //create request body
            Map<String, RequestBody> map = new HashMap<>();
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
            map.put("photo\"; filename=\"" + imageFile.getName() + "\"", requestBody);
            //make call
            Call<UserModel> call = mTService.uploadUserProfileImage(mAppPreferenceTools.getAccessToken(), map);
            call.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                    if (response.isSuccessful()) {
                        //save the user model to pref
                        mAppPreferenceTools.saveUserModel(response.body());
                        //load new image
                        Picasso.with(getBaseContext()).load(mAppPreferenceTools.getImageProfileUrl())
                                .transform(new CropCircleTransformation())
                                .into(mImImageProfile);
                        //reload the adapter since the user image profile changed
                        mAdapter.notifyDataSetChanged();
                    } else {
                        //ErrorModel errorModel = ErrorUtils.parseError(response);
                        //Toast.makeText(getBaseContext(), "Error type is " + errorModel.type + " , description " + errorModel.description, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserModel> call, Throwable t) {
                    //occur when fail to deserialize || no network connection || server unavailable
                    Toast.makeText(getBaseContext(), "Fail it >> " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getBaseContext(), "can not upload file since the file is not exist :|", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingLayout = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingLayout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_edit_profile) {
            // Handle the Edit Profile action
            Intent editUserIntent = new Intent(getBaseContext(), CreateOrEditUser.class);
            editUserIntent.putExtra(UserConstants.ACTION_TO_DO_KEY, UserConstants.EDIT_USER);
            editUserIntent.putExtra(UserConstants.USER_ID_KEY, mAppPreferenceTools.getUserId());
            startActivityForResult(editUserIntent, UserConstants.CREATE_OR_EDIT_USER_REQUEST_CODE);
        } else if (id == R.id.nav_app_users) {
            Intent userLayout = new Intent(MainActivity.this, UserActivity.class);
            startActivity(userLayout);

        } else if (id == R.id.nav_recent_activity) {

        } else if (id == R.id.nav_about_dev) {

        } else if (id == R.id.nav_about_app) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_log_out) {
//send request to server to terminate this application
            UserModelProvider userProvider = new UserModelProvider();
            //Call api route
            UserModelService userService = userProvider.getUService();
            Call<Boolean> call = userService.terminateApp();
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    //remove all authentication information such as accessToken and others
                    mAppPreferenceTools.removeAllPrefs();
                    //navigate to sign in activity
                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                    //finish this
                    finish();
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {

                }
            });

        }
        /*else if (id == R.id.imgProfile) {

        }*/

/*
mImages=(ImageView) findViewById(R.id.imgProfile);
        mImages.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //open gallery to select single picture as image profile
                //before that check runtime permission
                if (checkRunTimePermissionIsGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    startGalleryIntent();
                } else {
                    //request write external permission for open camera intent
                    requestRunTimePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, UserConstants.FOR_OPEN_GALLERY_REQUEST_WRITE_EXTERNAL_STORAGE_PER, new PermissionEventListener() {
                        @Override
                        public void onGranted(int requestCode, String[] permissions) {
                            startGalleryIntent();
                        }

                        @Override
                        public void onFailure(int requestCode, String[] permissions) {
                            Toast.makeText(getBaseContext(), "Can not pick photo without this permission", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * start gallery intent to pick photo
     */
    private void startGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), UserConstants.GALLERY_REQUEST_CODE);
    }

    private void requestRunTimePermission(String permissionType, int requestCode, PermissionEventListener permissionEventListener) {
        ActivityCompat.requestPermissions(this, new String[]{permissionType}, requestCode);
        mPermissionEventListener = permissionEventListener;
    }

    private boolean checkRunTimePermissionIsGranted(String permissionType) {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(TApplication.applicationContext, permissionType);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionEventListener != null) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionEventListener.onGranted(requestCode, permissions);
            } else {
                mPermissionEventListener.onFailure(requestCode, permissions);
            }
        }

    }

    public interface PermissionEventListener {
        void onGranted(int requestCode, String[] permissions);

        void onFailure(int requestCode, String[] permissions);
    }

}

