package com.example.farshad.smarthome.Controler;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.farshad.smarthome.Adapter.DoorRecyclerAdapter;
import com.example.farshad.smarthome.Model.DoorModel;
import com.example.farshad.smarthome.R;
import com.example.farshad.smarthome.Services.DoorModelProvider;
import com.example.farshad.smarthome.Services.DoorModelService;
import com.example.farshad.smarthome.Services.UserModelProvider;
import com.example.farshad.smarthome.Services.UserModelService;
import com.example.farshad.smarthome.Utility.AppPreferenceTools;
import com.example.farshad.smarthome.Utility.DoorConstants;
import com.example.farshad.smarthome.Utility.UserConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import static com.example.farshad.smarthome.R.id.btnOpenDoor;


public class DoorLocks extends AppCompatActivity {

    private RecyclerView doorRecylerView;
    private DoorRecyclerAdapter doorAdapter;
    private AppPreferenceTools mAppPreferenceTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_locks);
        setUpDoorRecyclerView();


       /* Button b1 = (Button)findViewById(R.id.btnOpenDoor);
        b1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                MyClientTask myClientTask=null;
                if(myClientTask==null) {
                    myClientTask = new MyClientTask("2.187.150.247", 5912,
                            "/ON");
                }
                myClientTask.execute();

            }

        });*/
    }


    private void setUpDoorRecyclerView() {

        doorRecylerView = (RecyclerView) findViewById(R.id.doorRecylerView);
        doorAdapter = new DoorRecyclerAdapter(this, new DoorRecyclerAdapter.DoorEventHandler() {
            //for Edit button

            @Override
            public void onEditDoor(String doorId, int position) {
                Intent editDoorIntent = new Intent(getBaseContext(), CreateOrEditDoor.class);
                editDoorIntent.putExtra(DoorConstants.ACTION_TO_DO_KEY, DoorConstants.EDIT_DOOR);
                editDoorIntent.putExtra(DoorConstants.DOOR_ID_KEY, doorId);
                startActivityForResult(editDoorIntent, DoorConstants.CREATE_OR_EDIT_DOOR_REQUEST_CODE);
            }

            @Override
            public void onDeletDoor(String doorId, final int position) {
                DoorModelProvider doorProvider = new DoorModelProvider();
                //Call api route
                DoorModelService doorService = doorProvider.getDService();
                Call<Boolean> call = doorService.deleteDoorById(doorId);

                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful()) {
                            doorAdapter.doorList.remove(position);
                            doorAdapter.notifyItemRemoved(position);
                            doorAdapter.notifyItemRangeChanged(position, doorAdapter.doorList.size());
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
            public void onAddDoor() {

            }


        });
        doorRecylerView.setAdapter(doorAdapter);


        LinearLayoutManager mlinearLayoutManagerVertical = new LinearLayoutManager(this);
        mlinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        doorRecylerView.setLayoutManager(mlinearLayoutManagerVertical);

        doorRecylerView.setItemAnimator(new DefaultItemAnimator());

        getDoorsFromServer();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent editDoorIntent = new Intent(getBaseContext(), CreateOrEditDoor.class);
            editDoorIntent.putExtra(DoorConstants.ACTION_TO_DO_KEY, DoorConstants.NEW_DOOR);
            startActivityForResult(editDoorIntent, DoorConstants.CREATE_OR_EDIT_DOOR_REQUEST_CODE);
        } else if (id == android.R.id.home) {
            //back to main activity
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDoorsFromServer() {
        //int[] images = getImages();

        DoorModelProvider doorProvider = new DoorModelProvider();
        //Call api route
        DoorModelService doorService = doorProvider.getDService();

        Call<List<DoorModel>> call = doorService.getDoors();

        call.enqueue(new Callback<List<DoorModel>>() {
            @Override
            public void onResponse(Call<List<DoorModel>> call, Response<List<DoorModel>> response) {
                if (response.isSuccessful()) {
                    doorAdapter.updateDoorAdapterData(response.body());
                    doorAdapter.notifyDataSetChanged();
                } else if (response.code() == 500) {
                    Toast.makeText(getBaseContext(), "Internal Server Error", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<List<DoorModel>> call, Throwable t) {
                //Toast.makeText(getBaseContext(), "R F", Toast.LENGTH_SHORT);

            }

        });

    }
/*
    public class MyClientTask extends AsyncTask<Void, Void, Void> {


        String dstAddress;
        int dstPort;
        String response = "";
        String msgToServer;

        MyClientTask(String addr, int port, String msgTo) {
            dstAddress = addr;
            dstPort = port;
            msgToServer = msgTo;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                if (msgToServer != null) {
                    dataOutputStream.writeBytes(msgToServer);

                }


            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            textResponse.setText(response);
            super.onPostExecute(result);
        }
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == DoorConstants.CREATE_OR_EDIT_DOOR_REQUEST_CODE && resultCode == RESULT_OK) {
            getDoorsFromServer();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
