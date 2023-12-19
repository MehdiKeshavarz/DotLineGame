package project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.keshavarz.mehdi.dotline.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       Button btnRestGame = findViewById(R.id.btn_rest);
       final  GameView gameView = findViewById(R.id.game_view);

       btnRestGame.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               gameView.restGame();
           }
       });
    }
}