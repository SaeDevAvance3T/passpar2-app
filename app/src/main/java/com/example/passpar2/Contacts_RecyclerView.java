package com.example.passpar2;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Contacts_RecyclerView extends RecyclerView.Adapter<Contacts_RecyclerView.ClientViewHolder> {

    private final DetailClient detailClient;
    private List<String> contacts;
    private List<Integer> idContacts;
    private OnItemClickListener listener;

    private ActivityResultLauncher<Intent> lanceurAdapter;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public Contacts_RecyclerView(List<String> contacts,List<Integer> idContacts, OnItemClickListener listener, ActivityResultLauncher<Intent> lanceurAdapter, DetailClient detailClient) {
        this.contacts = contacts;
        this.idContacts = idContacts;
        this.listener = listener;
        this.lanceurAdapter = lanceurAdapter;
        this.detailClient = detailClient;
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

        Integer idcontact = idContacts.get(position);

        holder.deleteButton.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setTitle("Supprimer le client")
                .setMessage("Êtes-vous sûr de vouloir supprimer ce client ?")
                .setPositiveButton("Oui", (dialog, which) -> detailClient.deleteContact(idcontact.toString()))
                .setNegativeButton("Non", null)
                .show());

        // Gestion du clic pour modifier un client
        holder.editButton.setOnClickListener(v -> {
            // Lancez l'activité de modification avec les données du client
            Intent intent = new Intent(v.getContext(), EditContact.class);
            intent.putExtra("idContact", idcontact.toString());
            // Lancer l'activité avec le lanceur
            lanceurAdapter.launch(intent);
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