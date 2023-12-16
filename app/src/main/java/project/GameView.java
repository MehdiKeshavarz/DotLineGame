package project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GameView extends View {

    private static class Position {
        public int x;
        public int y;

        Position(int x , int y){
            this.x = x;
            this.y = y;
        }
    }

    private static class Diff {
        public int i;
        public int j;
        public Float diff;

        Diff(int i , int j,float diff){
            this.i = i;
            this.j = j;
            this.diff = diff;
        }
    }

    private static class Line {
        public int i1;
        public int j1;
        public int i2;
        public int j2;
        public int playerIndex;

        Line(int i1 , int j1, int i2 ,int j2,int playerIndex){
            this.i1 = i1;
            this.j1 = j1;
            this.i2 = i2;
            this.j2 = j2;
            this.playerIndex = playerIndex;
        }
    }


    private static class Home{
        public int i;
        public int j;
        public int playerIndex;

        Home(int i, int j){
            this.i = i;
            this.j = j;
        }
    }


    private Paint dotPaint;
    private Paint textPaint;
    private Paint linePaint;
    private Paint touchPaint;
    private Paint homePaint;

    private final String player1Color = "#4444ff";
    private final String player2Color = "#ff4444";

    private int player1Score = 0;
    private int player2Score = 0;
    private int cols = 4;
    private int rows = 4;
    private int offsetY;
    private int offsetX;
    private int space = 150;
    private int radius = 15;

    private float touchX;
    private float touchY;

    private boolean isDebugMode = false;
    private boolean isSide1 = true;
    private boolean isGameOver = false;


    private final ArrayList<Line> lines = new ArrayList<>();
    private final ArrayList<Home> homes = new ArrayList<>();

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
        if (isInEditMode()){
            return;
        }
        dotPaint = new Paint();
        dotPaint.setColor(Color.WHITE);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setAntiAlias(true);

        homePaint = new Paint();
        homePaint.setColor(Color.WHITE);
        homePaint.setStyle(Paint.Style.FILL);
        homePaint.setAntiAlias(true);

        touchPaint = new Paint();
        touchPaint.setColor(Color.RED);
        touchPaint.setStyle(Paint.Style.FILL);
        touchPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#4444ff"));
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(10);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(40);
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

    public void rest(){

        player1Score = 0;
        player2Score = 0;

        isDebugMode = false;
        isSide1 = true;
        isGameOver = false;

        lines.clear();
        homes.clear();
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()){
            return;
        }
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#222222"));

        for (Line line: lines){
          connectLine(canvas,line);
        }

        for (Home home : homes){
            if (home.playerIndex == 1){
              homePaint.setColor(Color.parseColor(player1Color));
            }else {
                homePaint.setColor(Color.parseColor(player2Color));
            }
           Position homePosition =computePoint(home.i, home.j);
          canvas.drawCircle(homePosition.x + space /2 ,homePosition.y - space /2,30,homePaint);
        }

        for (int i =0; i<cols; i++){
            for (int j=0; j<rows; j++){
                Position point = computePoint(i, j);
                canvas.drawCircle(point.x, point.y, radius, dotPaint);
            }
        }
        int screenWidthHalf = G.context.getResources().getDisplayMetrics().widthPixels / 2;
        int screenHeight = G.context.getResources().getDisplayMetrics().heightPixels;
        homePaint.setColor(Color.parseColor(player1Color));
        canvas.drawCircle(screenWidthHalf - 100 ,100,60,homePaint);
        canvas.drawText("" +player1Score,screenWidthHalf - 100 ,110,textPaint);
        canvas.drawText("player1" ,screenWidthHalf - 100 ,210,textPaint);
        homePaint.setColor(Color.parseColor(player2Color));
        canvas.drawCircle(screenWidthHalf + 100 ,100,60,homePaint);
        canvas.drawText("" +player2Score,screenWidthHalf + 100 ,110,textPaint);
        canvas.drawText("player2",screenWidthHalf + 100 ,210,textPaint);

        if(isDebugMode){
            canvas.drawCircle(touchX,touchY,10,touchPaint);
            debugNaming(canvas);
        }

        if(homes.size() == (cols - 1) * (rows - 1)){
            isGameOver = true;

        }
        if (isGameOver) {
            String message = "";
            if (player1Score == player2Score) {
                message = "GAME DRAW";
            } else if (player1Score > player2Score) {
                message = "PLAYER 1 WON THE GAME";
            } else {
                message = "PLAYER 2 WON THE GAME";
            }
            canvas.drawText(message, screenWidthHalf, screenHeight - 350, textPaint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver){
            return true;
        }

        touchX = event.getX();
        touchY = event.getY();

        detectConnectingLine();
        invalidate();
        return super.onTouchEvent(event);
    }

    public void detectConnectingLine(){
        ArrayList<Diff> diffs = new ArrayList<>();
        for (int i =0; i<cols; i++){
            for (int j=0; j<rows; j++) {
               Position position =  computePoint(i,j);
               float diff = computeDiff(touchX,touchY,position.x,position.y);
               diffs.add(new Diff(i , j , diff));
            }
        }

        Collections.sort(diffs, new Comparator<Diff>() {
            @Override
            public int compare(Diff o1, Diff o2) {
               return o1.diff.compareTo(o2.diff);
            }
        });

       Diff min1 = diffs.get(0);
       Diff min2 = diffs.get(1);

       Diff firstPoint;
       Diff secondPoint;

        Home home1 = null;
        Home home2 = null;
       if (min1.i == min2.i){
           //vertical
           if (min1.j < min2.j){
               firstPoint = min1;
               secondPoint = min2;
           }else {
               firstPoint = min2;
               secondPoint = min1;
           }

           home1 = new Home(firstPoint.i, firstPoint.j);
           if (firstPoint.i> 0){
               home2 = new Home(firstPoint.i - 1, firstPoint.j);
           }
       }
       else {
           // horizontal
           if (min1.i < min2.i){
               firstPoint = min1;
               secondPoint = min2;
           }else {
               firstPoint = min2;
               secondPoint = min1;
           }


           home1 = new Home(firstPoint.i, firstPoint.j);
           if (firstPoint.j> 0){
               home2 = new Home(firstPoint.i, firstPoint.j -1);
           }
       }


       if (firstPoint.diff > space / 2){
           //return;
       }

       for (Line line : lines){
           if (line.i1 == firstPoint.i && line.j1 == firstPoint.j && line.i2 == secondPoint.i && line.j2 == secondPoint.j){
               return;
           }
       }
       lines.add(new Line(firstPoint.i, firstPoint.j,secondPoint.i, secondPoint.j,isSide1 ? 1 : 2));


        boolean wonHome1 = false;
        boolean wonHome2 = false;
        if (home1 != null){
            wonHome1 = checkHome(home1);
        }

        if (home2 != null){
            wonHome2 =checkHome(home2);
        }

        if (!wonHome1 && !wonHome2){
            isSide1 = !isSide1;
        }

    }

    private boolean checkHome (Home home){
        int i = home.i;
        int j = home.j;
        boolean leftConnected = false;
        boolean rightConnected = false;
        boolean topConnected = false;
        boolean bottomConnected = false;

        for (Line line : lines){
          if (line.i1 == i && line.j1 == j && line.i2 == i && line.j2 == j+1){
              leftConnected = true;
          }
          if (line.i1 == i+1 && line.j1 == j && line.i2 == i+1 && line.j2 == j+1){
              rightConnected = true;
          }
          if (line.i1 == i && line.j1 == j+1 && line.i2 == i+1 && line.j2 == j+1){
              topConnected = true;
          }
          if (line.i1 == i && line.j1 == j && line.i2 == i+1 && line.j2 == j){
              bottomConnected = true;
          }
        }

       boolean isFullConnected = leftConnected && rightConnected && topConnected && bottomConnected;

       if (isFullConnected){
           home.playerIndex = isSide1 ? 1 : 2;
           homes.add(home);
           if (home.playerIndex == 1){
               player1Score++;
           }else {
               player2Score++;
           }
           return true;
       }
        return false;
    }

    private float computeDiff(float x1,float y1, float x2, float y2){
        return (float)Math.sqrt(Math.pow(x1 - x2,2) + Math.pow(y1 - y2,2));
    }

    private void connectLine(Canvas canvas,Line line){
        Position p1 = computePoint(line.i1,line.j1);
        Position p2 = computePoint(line.i2,line.j2);
        if (line.playerIndex == 1){
            linePaint.setColor(Color.parseColor(player1Color));
        }else {
            linePaint.setColor(Color.parseColor(player2Color));
        }

        canvas.drawLine(p1.x, p1.y, p2.x,p2.y, linePaint);
    }


    private void debugNaming(Canvas canvas){
        for (int i =0; i<cols; i++){
            for (int j=0; j<rows; j++) {
                Position point = computePoint(i, j);
                String name = "" + i + "," +j;
                canvas.drawText(name,point.x, point.y + 50,textPaint);
            }
        }
    }
    private Position computePoint(int i , int j){
        int x = offsetX +(i * space);
        int y = offsetY + ((rows -1 - j) * space);

        return new Position(x, y);
    }
}
