package project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.Manifest;
import com.keshavarz.mehdi.dotline.R;


public class MainActivity extends AppCompatActivity {

    private  GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnRestGame = findViewById(R.id.btn_rest);
        gameView = findViewById(R.id.game_view);

        requestWritePermission();

        btnRestGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameView.restGame();
            }
        });

        gameView.restGame();
    }


    private void requestWritePermission() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        G.hasWriteAccess = hasPermission;
        G.createDirectory();
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 123: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    G.hasWriteAccess = true;
                    G.createDirectory();
                } else {
                    Toast.makeText(this, "Write to external storage required for loading & saving game", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        gameView.saveGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.loadGame();
    }
}