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

    private static class Point{
        public int x;
        public int y;

        Point(int x , int y){
            this.x = x;
            this.y = y;
        }
    }


    private Paint dotPaint;
    private Paint textPaint;
    private Paint linePaint;

    private int cols = 4;
    private int rows = 4;
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
        dotPaint = new Paint();
        dotPaint.setColor(Color.WHITE);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#4444ff"));
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(10);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

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

        connect(canvas,0, 2,1,2);
        connect(canvas,2, 2,3,1);
        for (int i =0; i<cols; i++){
            for (int j=0; j<rows; j++){
                Point point = computePoint(i, j);
                canvas.drawCircle(point.x, point.y, radius, dotPaint);
            }
        }
        debugNaming(canvas);


    }

    private void connect(Canvas canvas, int i1, int j1,int i2, int j2){
        float c =(float)Math.sqrt(Math.pow(i1 - i2,2) + Math.pow(j1 - j2,2));
        if(c != 1){
            return;
        }

        Point p1 = computePoint(i1, 2);
        Point p2 = computePoint(i2,j2);
        canvas.drawLine(p1.x, p1.y, p2.x,p2.y, linePaint);
    }


    private void debugNaming(Canvas canvas){
        for (int i =0; i<cols; i++){
            for (int j=0; j<rows; j++) {
                Point point = computePoint(i, j);
                String name = "" + i + "," +j;
                canvas.drawText(name,point.x, point.y + 50,textPaint);
            }
        }
    }
    private Point computePoint(int i , int j){
        int x = offsetX +(i * space);
        int y = offsetY + ((rows -1 - j) * space);

        return new Point(x, y);
    }
}
