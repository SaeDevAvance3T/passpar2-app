package com.example.passpar2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CheckboxAdapter extends BaseAdapter {

    private Context context;
    private List<String> items;
    private Set<Integer> selectedPositions = new HashSet<>(); // Stocke les positions cochées
    private CheckboxSelectionListener selectionListener;

    public CheckboxAdapter(Context context, List<String> items, CheckboxSelectionListener selectionListener) {
        this.context = context;
        this.items = items;
        this.selectionListener = selectionListener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_enterprise, parent, false);
        }

        CheckBox checkBox = convertView.findViewById(R.id.checkbox);
        checkBox.setText(items.get(position));

        // Rétablir l'état précédent si l'utilisateur fait défiler la liste
        checkBox.setChecked(selectedPositions.contains(position));

        // Gérer le clic sur la Checkbox
        checkBox.setOnClickListener(v -> {
            if (checkBox.isChecked()) {
                selectedPositions.add(position);
            } else {
                selectedPositions.remove(position);
            }

            // Envoyer la mise à jour à l’activité principale
            if (selectionListener != null) {
                selectionListener.onCheckboxSelectionChanged(selectedPositions);
            }
        });

        return convertView;
    }
}
