package com.example.passpar2;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.passpar2.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_account) {
            if (!(this instanceof AccountActivity)) {
                startActivity(new Intent(this, AccountActivity.class));
            }else {
                Toast.makeText(this, this.getString(R.string.menu_message_already_account), Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.action_path) {
            if (!(this instanceof Accueil_main)) {
                startActivity(new Intent(this, Accueil_main.class));
            }else {
                Toast.makeText(this, this.getString(R.string.menu_message_already_path), Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.action_clients) {
            if (!(this instanceof Clients_afficher)) {
                startActivity(new Intent(this, Clients_afficher.class));
            }else {
                Toast.makeText(this, this.getString(R.string.menu_message_already_customers), Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.action_iti) {
            if (!(this instanceof NewRouteActivity)) {
                startActivity(new Intent(this, NewRouteActivity.class));
            }else {
                Toast.makeText(this, this.getString(R.string.menu_message_already_itineraries), Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
