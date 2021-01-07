package com.arnab.chatymeety;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> mMessageList;
    private String curUid;
    private String thumbnail;

    public MessageAdapter(List<Message> mMessageList,String curUid,String thumbnail) {
        this.curUid=curUid;
        this.mMessageList = mMessageList;
        this.thumbnail=thumbnail;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_msg_layout,parent,false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message=mMessageList.get(position);
        holder.setIsRecyclable(false);


        if(curUid.equals(message.getFrom())){
            holder.main.setGravity(Gravity.END);
            holder.head.setVisibility(View.GONE);
            /*RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams)holder.messageText.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            holder.messageText.setLayoutParams(layoutParams);*/
            holder.messageText.setBackgroundResource(R.drawable.chat_background_own);
            holder.messageText.setTextColor(Color.BLACK);
            holder.messageText.setText(message.getMessage());
        }
        else{
            Picasso.get().load(thumbnail).into(holder.head);
            /*RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams)holder.messageText.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
            //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            holder.messageText.setLayoutParams(layoutParams);*/
            holder.messageText.setBackgroundResource(R.drawable.chat_background_other);
            holder.messageText.setTextColor(Color.WHITE);
            holder.messageText.setText(message.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public ImageView head;
        public RelativeLayout main;

        public MessageViewHolder(View view) {
            super(view);
            messageText = view.findViewById(R.id.msg_text);
            main=view.findViewById(R.id.singlemsg);
            head = view.findViewById(R.id.msg_head);
        }
    }
}
