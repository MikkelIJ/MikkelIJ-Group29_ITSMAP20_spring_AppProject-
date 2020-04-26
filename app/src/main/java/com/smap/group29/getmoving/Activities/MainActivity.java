package com.smap.group29.getmoving.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.Utils.FirebaseUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.logout_menu:
                AuthUI.getInstance().signOut(this)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("Logout", "User logged Out!");
                                            FirebaseUtil.attachListener();
                                        }
                                    });
                FirebaseUtil.detachListener();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.attachListener();
    }

    public void showMenu() {
        invalidateOptionsMenu();
    }
}
