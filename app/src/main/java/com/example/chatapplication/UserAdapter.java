package com.example.chatapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    List<String> userList;
    String userName;
    MainActivity mContext;

    FirebaseDatabase database;
    DatabaseReference reference;

    public UserAdapter(List<String> userList, String userName,MainActivity mContext) {
        this.userList = userList;
        this.userName = userName;
        this.mContext = mContext;

        database = FirebaseDatabase.getInstance("https://chatapplication-7858d-default-rtdb.asia-southeast1.firebasedatabase.app");
        reference = database.getReference();
    }

    public UserAdapter(List<String> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            reference.child("Users").child(userList.get(position)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String otherName = snapshot.child("userName").getValue().toString();
                    String imageURL = snapshot.child("image").getValue().toString();
                    holder.textViewUser.setText(otherName);

                    if(imageURL.equals("null")){
                        holder.imageViewUsers.setImageResource(R.drawable.profile);
                    }
                    else{
                        Picasso.get().load(imageURL).into(holder.imageViewUsers);
                    }
                    holder.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext,MyChatActivity.class);
                            intent.putExtra("userName",userName);
                            intent.putExtra("otherName",otherName);
                            mContext.startActivity(intent);

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewUser;
        private CircleImageView imageViewUsers;
        private CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewUser = itemView.findViewById(R.id.textViewUser);
            imageViewUsers = itemView.findViewById(R.id.imageViewUsersProfile);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

}
