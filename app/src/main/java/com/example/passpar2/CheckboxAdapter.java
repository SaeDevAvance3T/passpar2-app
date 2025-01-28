package com.example.passpar2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.example.passpar2.CallbackListener;

import java.util.List;

public class CheckboxAdapter extends BaseAdapter {

    private Context context;
    private List<String> items; // Liste des données
    private CallbackListener callbackListener; // Interface de communication

    public CheckboxAdapter(Context context, List<String> items, CallbackListener callbackListener) {
        this.context = context;
        this.items = items;
        this.callbackListener = callbackListener;
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

        // Configurer l'état initial si nécessaire
        checkBox.setChecked(false);

        // Définir un écouteur sur la Checkbox
        checkBox.setOnClickListener(v -> {
            boolean isChecked = checkBox.isChecked();
            String content = items.get(position);

            // Transmettre l'information via le callback
            if (callbackListener != null) {
                callbackListener.onCheckboxClicked(content, isChecked);
            }
        });

        return convertView;
    }
}
