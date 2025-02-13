package com.example.passpar2;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Clients_RecyclerView extends RecyclerView.Adapter<Clients_RecyclerView.ClientViewHolder> {

    private List<String> clients;

    private List<Integer> idCustomers;
    private OnItemClickListener listener;

    private Clients_afficher clientsAfficher;

    private ActivityResultLauncher<Intent> lanceurAdapter;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public Clients_RecyclerView(List<String> clients, List<Integer> idCustomers, OnItemClickListener listener, ActivityResultLauncher<Intent> lanceurAdapter, Clients_afficher clientsAfficher) {
        this.clients = clients;
        this.idCustomers = idCustomers;
        this.listener = listener;
        this.lanceurAdapter = lanceurAdapter;
        this.clientsAfficher = clientsAfficher;
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

        Integer idCustomer = idCustomers.get(position);

        holder.deleteButton.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setTitle("Supprimer le client")
                .setMessage("Êtes-vous sûr de vouloir supprimer ce client ?")
                .setPositiveButton("Oui", (dialog, which) -> clientsAfficher.deleteCustomer(idCustomer.toString()))
                .setNegativeButton("Non", null)
                .show());

        // Gestion du clic pour modifier un client
        holder.editButton.setOnClickListener(v -> {
            // Lancez l'activité de modification avec les données du client
            Intent intent = new Intent(v.getContext(), DetailClient.class);
            intent.putExtra("idCustomer", idCustomer.toString());
            // Lancer l'activité avec le lanceur
            lanceurAdapter.launch(intent);
        });
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
