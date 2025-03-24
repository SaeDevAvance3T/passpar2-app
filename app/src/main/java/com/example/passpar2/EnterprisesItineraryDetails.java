package com.example.passpar2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnterprisesItineraryDetails extends BaseAdapter {
    private Context context;
    private Map<Integer, String> enterpriseValues; // Liste des entreprises (ID -> Nom)
    private LayoutInflater inflater;
    private List<Integer> keys; // Liste des IDs pour un accès facile

    public EnterprisesItineraryDetails(Context context, Map<Integer, String> enterpriseValues) {
        this.context = context;
        this.enterpriseValues = enterpriseValues;
        this.inflater = LayoutInflater.from(context);
        this.keys = new ArrayList<>(enterpriseValues.keySet()); // Extraire les clés pour un accès facile
    }

    @Override
    public int getCount() {
        return enterpriseValues.size(); // Nombre total d'entreprises
    }

    @Override
    public Object getItem(int position) {
        int key = keys.get(position);
        return enterpriseValues.get(key);
    }

    @Override
    public long getItemId(int position) {
        return keys.get(position); // Retourne l'ID de l'entreprise
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_enterprise_details, parent, false);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.enterprise_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int id = keys.get(position); // Récupère l'ID de l'entreprise
        String name = enterpriseValues.get(id);

        holder.textView.setText(name);

        return convertView;
    }

    static class ViewHolder {
        TextView textView;
    }
}
