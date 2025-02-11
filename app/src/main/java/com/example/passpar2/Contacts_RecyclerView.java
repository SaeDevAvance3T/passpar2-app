package com.example.passpar2;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Contacts_RecyclerView extends RecyclerView.Adapter<Contacts_RecyclerView.ClientViewHolder> {

    private List<String> contacts;
    private List<Integer> idContacts;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public Contacts_RecyclerView(List<String> contacts,List<Integer> idContacts, OnItemClickListener listener) {
        this.contacts = contacts;
        this.idContacts = idContacts;
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
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(position));

        holder.deleteButton.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setTitle("Supprimer le client")
                .setMessage("Êtes-vous sûr de vouloir supprimer ce client ?")
                .setPositiveButton("Oui", (dialog, which) -> listener.onDeleteClick(position))
                .setNegativeButton("Non", null)
                .show());

        Integer idcontact = idContacts.get(position);

        holder.editButton.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setTitle("Modifier un client")
                .setMessage("Êtes-vous sûr de vouloir modifier ce client avec l'id : " + idcontact.toString())
                .setPositiveButton("Oui", (dialog, which) -> listener.onDeleteClick(position))
                .setNegativeButton("Non", null)
                .show());

        // Gestion du clic pour modifier un client
        holder.editButton.setOnClickListener(v -> {
            // Lancez l'activité de modification avec les données du client
            Intent intent = new Intent(v.getContext(), EditContact.class);
            intent.putExtra("idContact", idcontact.toString());
            // Vous pouvez ajouter d'autres données spécifiques si nécessaire
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        ImageButton deleteButton;
        ImageButton editButton;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contactName);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.modify_button);
        }
    }
}