package com.example.Blockchain_App.Adapters;
import static com.example.Blockchain_App.Blockchain.Blockchain_user_registration.FLAT_NO;
import static com.example.Blockchain_App.Blockchain.Blockchain_user_registration.mAuth;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Blockchain_App.Blockchain.Blockchain_user_registration;
import com.example.Blockchain_App.Core.ImagesActivity;
import com.example.Blockchain_App.Model.Request;
import com.example.Blockchain_App.Network.NetworkClient;
import com.example.Blockchain_App.Network.UploadApis;
import com.example.Blockchain_App.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Request> mRequests;
    private OnItemClickListener mListener;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public ImageAdapter(Context context, List<Request> uploads) {
        mContext = context;
        mRequests = uploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card, parent, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Request requestCurrent = mRequests.get(position);
        holder.textViewName.setText(requestCurrent.getName());
        Picasso.get()
                .load(requestCurrent.getUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView textViewName;
        public ImageView imageView;
        public Button acceptBtn;
        public Button declineBtn;

        public ImageViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.example_name);
            imageView = itemView.findViewById(R.id.imagev);
            acceptBtn = itemView.findViewById(R.id.grant_button);
            declineBtn = itemView.findViewById(R.id.decline_button);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(acceptBtn.getRootView(), "TODO: Backend integration", BaseTransientBottomBar.LENGTH_SHORT).show();
                    Toast.makeText(acceptBtn.getContext(), "Accept Clicked", Toast.LENGTH_SHORT).show();
                    updateFire(true);
                    postBack(true);
                    acceptBtn.setEnabled(false);
                    declineBtn.setEnabled(false);

                }
            });

            declineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(acceptBtn.getRootView(), "TODO: Backend integration", BaseTransientBottomBar.LENGTH_SHORT).show();
                    Toast.makeText(acceptBtn.getContext(), "Decline Clicked", Toast.LENGTH_SHORT).show();
                    updateFire(false);
                    postBack(false);
                    acceptBtn.setEnabled(false);
                    declineBtn.setEnabled(false);
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem doWhatever = menu.add(Menu.NONE, 1, 1, "Do whatever");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
                            mListener.onWhatEverClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    private void updateFire(boolean b) {
        mDatabase.child(FLAT_NO).child("Requests").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                Request request = task.getResult().getValue(Request.class);
                if (request != null) {
                    Log.d("Firebase Fetch", String.valueOf(request.toMap()));
                    Log.d("Firebase Auth", String.valueOf(mAuth.getCurrentUser().getEmail()));
                    if(b){
                        request.addApproval(mAuth.getCurrentUser().getEmail());
                        mDatabase.child(FLAT_NO).child("Requests").setValue(request,
                                (error, ref) -> Toast.makeText(mContext.getApplicationContext(),
                                "Request Approved", Toast.LENGTH_SHORT).show());
                    }
                    else{
                        request.addDenial(mAuth.getCurrentUser().getEmail());
                        mDatabase.child(FLAT_NO).child("Requests").setValue(request,
                                (error, ref) -> Toast.makeText(mContext.getApplicationContext(),
                                "Request Denied", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    private void postBack(boolean i) {
        mAuth = FirebaseAuth.getInstance();
        // get retrofit instance
        Retrofit retrofit = NetworkClient.getRetrofit();
        // form the request body for image
        // form user requestbody of type  plain text
        String UserName = (mAuth.getCurrentUser().getDisplayName().isEmpty())?"NULL":mAuth.getCurrentUser().getEmail();
        RequestBody username = RequestBody.create(MediaType.parse("text/plain"), UserName);

        RequestBody reqID = RequestBody.create(MediaType.parse("text/plain"), (mRequests.get(0).toMap().get("ReqID")));
        Log.d("requestbody", mRequests.get(0).toMap().get("ReqID"));
        RequestBody response = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf((i)?"accept".toUpperCase():"reject".toUpperCase()));

        UploadApis uploadApis = retrofit.create(UploadApis.class);
        // make the network API call PARAM : reqID and username
        Call call = uploadApis.Response(username, reqID, response);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Toast.makeText(mContext,""+response.message(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(mContext,""+ t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onWhatEverClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
