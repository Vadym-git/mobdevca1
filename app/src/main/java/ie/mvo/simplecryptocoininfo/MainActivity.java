package ie.mvo.simplecryptocoininfo;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import ie.mvo.simplecryptocoininfo.dataUtils.DataBase;
import ie.mvo.simplecryptocoininfo.dataUtils.DbHelper;
import ie.mvo.simplecryptocoininfo.databinding.ActivityMainBinding;

import ie.mvo.simplecryptocoininfo.screens.MainFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBase dataBase = new DataBase(this);
        new DbHelper(dataBase.getWritableDatabase());
        //
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportFragmentManager()
                .beginTransaction()
                        .add(binding.fragmentMainHolder.getId(), new MainFragment())
                                .commit();
    }
    public static void showAlert(Context context, String text){

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(text)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataBase.getInstance(this).close();
    }
}