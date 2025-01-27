package com.example.passpar2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Contacts_RecyclerView extends RecyclerView.Adapter<Contacts_RecyclerView.ClientViewHolder> {

    private List<String> contacts;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public Contacts_RecyclerView(List<String> clients, OnItemClickListener listener) {
        this.contacts = clients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_liste, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        String client = contacts.get(position);
        holder.contactName.setText(client);
        //holder.modifyButton.setOnClickListener(v -> listener.onModifyClick(position));
        //holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(position));

        holder.deleteButton.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setTitle("Supprimer le client")
                .setMessage("Êtes-vous sûr de vouloir supprimer ce client ?")
                .setPositiveButton("Oui", (dialog, which) -> listener.onDeleteClick(position))
                .setNegativeButton("Non", null)
                .show());
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        ImageButton deleteButton;

        ImageButton modifyButton;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contactName);
            modifyButton = itemView.findViewById(R.id.modify_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}