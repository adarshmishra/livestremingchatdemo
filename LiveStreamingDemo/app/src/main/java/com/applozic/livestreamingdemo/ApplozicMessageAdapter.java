package com.applozic.livestreamingdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;

import java.util.ArrayList;
import java.util.List;

public class ApplozicMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ONE_TO_ONE_CHAT = 1;
    private static final int GROUP_CHAT = 2;
    private LayoutInflater inflater;
    private Context mContext;
    private List<Message> messageList = new ArrayList<>();
    private MessageDatabaseService messageDatabaseService;


    public ApplozicMessageAdapter(Context mContext, List<Message> listOfMessages) {
        this.mContext = mContext;
        this.messageDatabaseService = new MessageDatabaseService(mContext);
        inflater = LayoutInflater.from(mContext);
        this.messageList = listOfMessages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.message_list_textview, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    /**
     * This method displays conversation header in rows displaying conversation name, image and last message.
     * It also handles onclick event on the message row opening detailed conversation with that particular thread.
     *
     * @param mholder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mholder, int position) {

        final Message currentMessage = messageList.get(position);
        MyViewHolder holder = (MyViewHolder) mholder;
        holder.message.setText(currentMessage.getMessage());
        //holder.message.setText(new String(Character.toChars(Integer.parseInt("1F44F", 16))));

    }

    public int getItemCount() {
        return messageList.size();
    }

    /**
     * This is a overriden method which is used to identify a particular row if it is a group conversation or 1-to-1 conversation.
     *
     * @param position Used to get hold of a particular row in the view.
     * @return integer value corresponding to conversation type
     * 1 : if 1-to-1 conversation
     * 2 : if Group conversation
     */
    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getGroupId() == null) {
            return ONE_TO_ONE_CHAT;//This is a 1-to-1 message.
        } else {
            return GROUP_CHAT;//This is a Group message.
        }
    }

    /**
     * View Holder for coversation rows. It uses message_row layout.
     */
    class MyViewHolder extends RecyclerView.ViewHolder {


        TextView message;

        public MyViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.text_message_incoming);
        }
    }
}

