package com.example.passpar2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CheckboxAdapterEdit extends BaseAdapter {
    private Context context;
    private Map<Integer, String> enterpriseValues; // Liste des entreprises (ID -> Nom)
    private Set<Integer> selectedCustomerIds; // Liste des customerId à cocher
    private LayoutInflater inflater;
    private List<Integer> keys; // Liste des IDs pour un accès facile
    private CheckboxSelectionListener selectionListener; // Interface pour notifier les changements

    public CheckboxAdapterEdit(Context context, Map<Integer, String> enterpriseValues, Set<Integer> selectedCustomerIds, CheckboxSelectionListener selectionListener) {
        this.context = context;
        this.enterpriseValues = enterpriseValues;
        this.selectedCustomerIds = selectedCustomerIds;
        this.inflater = LayoutInflater.from(context);
        this.keys = new ArrayList<>(enterpriseValues.keySet()); // Extraire les clés pour un accès facile
        this.selectionListener = selectionListener;
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
            convertView = inflater.inflate(R.layout.list_item_enterprise, parent, false);
            holder = new ViewHolder();
            holder.checkBox = convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int id = keys.get(position); // Récupère l'ID de l'entreprise
        String name = enterpriseValues.get(id);

        holder.checkBox.setText(name);
        holder.checkBox.setChecked(selectedCustomerIds.contains(id)); // Cocher si présent dans l'itinéraire

        // Gérer le clic sur la case à cocher
        holder.checkBox.setOnClickListener(v -> {
            if (holder.checkBox.isChecked()) {
                selectedCustomerIds.add(id); // Ajouter à la sélection
            } else {
                selectedCustomerIds.remove(id); // Retirer de la sélection
            }

            // Notifier l'activité principale
            if (selectionListener != null) {
                selectionListener.onCheckboxSelectionChanged(selectedCustomerIds);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        CheckBox checkBox;
    }
}
