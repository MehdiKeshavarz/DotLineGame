package project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;

public class GameView extends View {
    private Paint paint;
    private int cols = 5;
    private int rows = 6;
    private int offsetY;
    private int offsetX;
    private int space = 150;
    private int radius = 15;
    public GameView(Context context) {
        super(context);
        initialize();
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize(){
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        int boxWidth= (cols -1) * space;
        int boxHeight= (rows -1) * space;


      DisplayMetrics dp =  G.context.getResources().getDisplayMetrics();
      int screenHeight =dp.heightPixels;
      int screenWidth =dp.widthPixels;

        offsetX = (screenWidth - boxWidth) /2;
        offsetY = (screenHeight - boxHeight) /2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#222222"));
        for (int i =0; i<cols; i++){
            for (int j=0; j<rows; j++){
                int x = offsetX +(i * space);
                int y = offsetY + (j * space);
                canvas.drawCircle(x, y, radius,paint);
            }
        }
    }
}
