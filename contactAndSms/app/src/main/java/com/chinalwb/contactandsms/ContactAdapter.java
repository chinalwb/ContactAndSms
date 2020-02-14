package com.chinalwb.contactandsms;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<ContactModel> contactList;

    public ContactAdapter(List<ContactModel> contactList) {
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactModel contactModel = contactList.get(position);
        CardView cardView = holder.cardView;
        ImageView contactImageView = cardView.findViewById(R.id.contact_icon);
        TextView contactNameView = cardView.findViewById(R.id.contact_name);
        if (!TextUtils.isEmpty(contactModel.getPhoto_uri())) {
            contactImageView.setImageURI(Uri.parse(contactModel.getPhoto_uri()));
        }
        contactNameView.setText(
                contactModel.getFirst_name() + " " + contactModel.getLast_name()
                + " --> Sent!"
        );
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }
}
