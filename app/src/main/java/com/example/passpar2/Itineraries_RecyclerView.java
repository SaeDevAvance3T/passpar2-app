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

public class Itineraries_RecyclerView extends RecyclerView.Adapter<Itineraries_RecyclerView.ItinerariesViewHolder> {

    private final Itineraries_afficher itinerariesAfficher;
    private List<String> itineraries;
    private List<String> idItineraries;
    private OnItemClickListener listener;

    private ActivityResultLauncher<Intent> lanceurAdapter;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public Itineraries_RecyclerView(List<String> itineraries, List<String> idItineraries, OnItemClickListener listener, ActivityResultLauncher<Intent> lanceurAdapter, Itineraries_afficher itinerariesAfficher) {
        this.itineraries = itineraries;
        this.idItineraries = idItineraries;
        this.listener = listener;
        this.lanceurAdapter = lanceurAdapter;
        this.itinerariesAfficher = itinerariesAfficher;
    }

    @NonNull
    @Override
    public ItinerariesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itineraries_liste, parent, false);
        return new ItinerariesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItinerariesViewHolder holder, int position) {
        String itinerarie = itineraries.get(position);
        holder.itinerarieName.setText(itinerarie);

        String idItinerarie = idItineraries.get(position);

        holder.deleteButton.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setTitle("Supprimer l'itineraire")
                .setMessage("Êtes-vous sûr de vouloir supprimer cet itineraire ?")
                .setPositiveButton("Oui", (dialog, which) -> itinerariesAfficher.deleteItenarie(idItinerarie))
                .setNegativeButton("Non", null)
                .show());

        // Gestion du clic pour supprimer un itineraire
        holder.editButton.setOnClickListener(v -> {
            // Lancez l'activité de modification avec les données du client
            Intent intent = new Intent(v.getContext(), EditRoute.class);
            intent.putExtra("itineraryId", idItinerarie);
            // Lancer l'activité avec le lanceur
            lanceurAdapter.launch(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itineraries.size();
    }

    public static class ItinerariesViewHolder extends RecyclerView.ViewHolder {
        TextView itinerarieName;
        ImageButton deleteButton;
        ImageButton editButton;

        public ItinerariesViewHolder(@NonNull View itemView) {
            super(itemView);
            itinerarieName = itemView.findViewById(R.id.itinerarie_name);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.modify_button);
        }
    }
}