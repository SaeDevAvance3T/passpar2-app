package com.example.passpar2;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseSelection_RecyclerView extends RecyclerView.Adapter<CourseSelection_RecyclerView.ItinerariesViewHolder> {

    private final List<String> itineraries;
    private final List<String> idItineraries;
    private final List<String> idCourses;
    private final ActivityResultLauncher<Intent> lanceurAdapter;

    public CourseSelection_RecyclerView(List<String> itineraries, List<String> idItineraries, List<String> idCourses, ActivityResultLauncher<Intent> lanceurAdapter) {
        this.itineraries = itineraries;
        this.idItineraries = idItineraries;
        this.idCourses = idCourses;
        this.lanceurAdapter = lanceurAdapter;
    }

    @NonNull
    @Override
    public ItinerariesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itinerary_selection_recycler_view, parent, false);
        return new ItinerariesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItinerariesViewHolder holder, int position) {
        String itinerarie = itineraries.get(position);
        String idItinerarie = idItineraries.get(position);
        String idCourse = idCourses.get(position);

        holder.itinerarieName.setText(itinerarie);

        // Gestion du clic pour afficher les détails de l'itinéraire
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ItineraryDetailActivity.class);
            intent.putExtra("itineraryId", idItinerarie);
            intent.putExtra("courseId",idCourse);
            lanceurAdapter.launch(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itineraries.size();
    }

    public static class ItinerariesViewHolder extends RecyclerView.ViewHolder {
        TextView itinerarieName;

        public ItinerariesViewHolder(@NonNull View itemView) {
            super(itemView);
            itinerarieName = itemView.findViewById(R.id.itinerarie_name);
        }
    }
}