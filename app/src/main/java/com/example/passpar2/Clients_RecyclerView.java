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

public class Clients_RecyclerView extends RecyclerView.Adapter<Clients_RecyclerView.ClientViewHolder> {

    private List<String> clients;
    private List<Integer> idClients;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public Clients_RecyclerView(List<String> clients,List<Integer> idClients, OnItemClickListener listener) {
        this.clients = clients;
        this.idClients = idClients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clients_liste, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        String client = clients.get(position);
        holder.clientName.setText(client);
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(position));

        holder.deleteButton.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setTitle("Supprimer le client")
                .setMessage("Êtes-vous sûr de vouloir supprimer ce client ?")
                .setPositiveButton("Oui", (dialog, which) -> listener.onDeleteClick(position))
                .setNegativeButton("Non", null)
                .show());

        Integer idclient = idClients.get(position);

        holder.editButton.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setTitle("Modifier un client")
                .setMessage("Êtes-vous sûr de vouloir modifier ce client avec l'id : " + idclient.toString())
                .setPositiveButton("Oui", (dialog, which) -> listener.onDeleteClick(position))
                .setNegativeButton("Non", null)
                .show());
        // Gestion du clic pour modifier un client
        //holder.editButton.setOnClickListener(v -> {
        //    // Lancez l'activité de modification avec les données du client
        //    Intent intent = new Intent(v.getContext(), Clients_edit.class);
        //    intent.putExtra("id", idclient);
        //    // Vous pouvez ajouter d'autres données spécifiques si nécessaire
        //    v.getContext().startActivity(intent);
        //});
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    public static class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView clientName;
        ImageButton deleteButton;
        ImageButton editButton;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            clientName = itemView.findViewById(R.id.client_name);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.modify_button);
        }
    }
}
