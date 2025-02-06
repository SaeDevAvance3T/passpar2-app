package com.example.passpar2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CheckboxAdapter extends BaseAdapter {

    private Context context;
    private HashMap<Integer, String> items;
    private Set<Integer> selectedKeys = new HashSet<>(); // Stocke les clés cochées
    private CheckboxSelectionListener selectionListener;

    public CheckboxAdapter(Context context, HashMap<Integer, String> items, CheckboxSelectionListener selectionListener) {
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
        int key = (int) items.keySet().toArray()[position];
        checkBox.setText(items.get(key));

        // Rétablir l'état précédent si l'utilisateur fait défiler la liste
        checkBox.setChecked(selectedKeys.contains(key));

        // Gérer le clic sur la Checkbox
        checkBox.setOnClickListener(v -> {
            if (checkBox.isChecked()) {
                selectedKeys.add(key);
            } else {
                selectedKeys.remove(key);
            }

            // Envoyer la mise à jour à l’activité principale
            if (selectionListener != null) {
                selectionListener.onCheckboxSelectionChanged(selectedKeys);
            }
        });

        return convertView;
    }
}