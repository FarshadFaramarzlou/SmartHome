package com.example.farshad.smarthome.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.farshad.smarthome.Model.DoorModel;
import com.example.farshad.smarthome.R;

import java.util.Collections;
import java.util.List;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by Farshad on 7/11/2017.
 */

public class DoorRecyclerAdapter extends RecyclerView.Adapter<DoorRecyclerAdapter.DoorViewHolder> {

    public List<DoorModel> doorList = Collections.emptyList();
    private LayoutInflater doorInflater;
    private DoorEventHandler mDoorEventHandler;

    public DoorRecyclerAdapter(Context context/*,ArrayList<DoorModel> Data*/,DoorEventHandler doorEventHandler) {
        //this.doorList = Data;
        this.doorInflater = LayoutInflater.from(context);
    }

    public void updateDoorAdapterData(List<DoorModel> data){
        this.doorList = data;
    }
    @Override
    public DoorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG,"onCreateViewHolder");
        View view = doorInflater.inflate(R.layout.list_view_door,parent,false);
        DoorViewHolder doorHolder = new DoorViewHolder(view);
        return doorHolder;
    }

    @Override
    public void onBindViewHolder(DoorViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder " + position);

        DoorModel currentDoor = doorList.get(position);
        holder.setData(currentDoor,position);
        holder.setListeners();
    }

    @Override
    public int getItemCount() {

        return doorList.size();
    }

    public void removeDoor(int position){

        if (mDoorEventHandler != null) {
            mDoorEventHandler.onDeletDoor(String.valueOf(doorList.get(position).getDoorId()), position);
        }
       /* doorList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,doorList.size());*/
    }

    public void addDoor(int position,DoorModel door){
        doorList.add(position,door);
        notifyItemInserted(position);
        notifyItemRangeChanged(position,doorList.size());
    }

    public void editDoor(int position, DoorModel door) {
        if (mDoorEventHandler != null) {
            mDoorEventHandler.onEditDoor(String.valueOf(doorList.get(position).getDoorId()), position);
            doorList.add(position, door);
            notifyItemInserted(position);
            notifyItemRangeChanged(position, doorList.size());
        }

    }

    class DoorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        ImageView imgThumb, imgAdd, imgDelete;
        int position;
        DoorModel currentDoor;

        public DoorViewHolder(View itemView) {
            super(itemView);
            title       = (TextView)  itemView.findViewById(R.id.doorTitle);
            imgThumb    = (ImageView) itemView.findViewById(R.id.imgRow);
            imgAdd      = (ImageView) itemView.findViewById(R.id.img_row_edit);
            imgDelete   = (ImageView) itemView.findViewById(R.id.img_row_delete);;
        }

        public void setData(DoorModel currentDoor, int position) {
            this.title.setText(currentDoor.getDoorName());
            this.imgThumb.setImageResource(currentDoor.getDoorImageID());
            this.position=position;
            this.currentDoor=currentDoor;
        }

        public void setListeners(){
            imgDelete.setOnClickListener(DoorViewHolder.this);
            imgAdd.setOnClickListener(DoorViewHolder.this);
        }

        public void onClick(View v){
            Log.d(TAG,"onClick brfore operation at position: " + position + "size: " + doorList.size());
            switch (v.getId()){
                case R.id.img_row_delete:
                    removeDoor(position);
                    break;
                case R.id.img_row_edit:
                    editDoor(position,currentDoor);
                    break;
            }
            Log.d(TAG,"onClick after operatin - size " + doorList.size());
        }
    }

    public interface DoorEventHandler {

        void onEditDoor(String doorId, int position);

        void onDeletDoor(String doorId, int position);

        void onAddDoor();

    }
}
