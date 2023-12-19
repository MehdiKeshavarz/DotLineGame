package project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GameView extends View {

    private Paint paintDot;
    private Paint paintText;
    private Paint paintLine;
    private Paint paintTouch;
    private Paint paintBox;

    public int boxWidth;
    public int boxHeight;
    public int screenHeight;
    public int screenWidth;
    public int screenWidthHalf;

    private int offsetY;
    private int offsetX;

    private float touchX;
    private float touchY;

    private static class Theme {
        private static final int[] playerColors =new int[]{
                Color.parseColor("#4444ff"),
                Color.parseColor("#ff4444")};
        private static final int space = 150;
        private static final int radius = 15;
        private static final int backgroundColor = Color.parseColor("#222222");
    }

    private static class State {
        private static int[] playerScores = new int[]{0,0};
        private static boolean isGameOver = false;
        private static boolean isSide1 = true;
        private static final ArrayList<Line> lines = new ArrayList<>();
        private static final ArrayList<Box> boxes = new ArrayList<>();
    }

    private static class Options {
        private static final int cols = 4;
        private static final int rows = 4;

        //private static String[] playerNames = new String[]{"player1 , player2"};

    }

    private static class Debug {
        private static boolean isDebugMode = false;
        private static final boolean drawTouch = false;
        private static final boolean drawDotNames = false;
    }


    private static class Position {
        public int x;
        public int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class Diff {
        public int i;
        public int j;
        public Float diff;

        Diff(int i, int j, float diff) {
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

        Line(int i1, int j1, int i2, int j2, int playerIndex) {
            this.i1 = i1;
            this.j1 = j1;
            this.i2 = i2;
            this.j2 = j2;
            this.playerIndex = playerIndex;
        }
    }


    private static class Box {
        public int i;
        public int j;
        public int playerIndex;

        Box(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }


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

    private void initialize() {
        if (isInEditMode()) {
            return;
        }
        initializePaints();
        initializeMetrics();
    }

    private void initializePaints() {
        paintDot = new Paint();
        paintDot.setColor(Color.WHITE);
        paintDot.setStyle(Paint.Style.FILL);
        paintDot.setAntiAlias(true);

        paintBox = new Paint();
        paintBox.setColor(Color.WHITE);
        paintBox.setStyle(Paint.Style.FILL);
        paintBox.setAntiAlias(true);

        paintTouch = new Paint();
        paintTouch.setColor(Color.RED);
        paintTouch.setStyle(Paint.Style.FILL);
        paintTouch.setAntiAlias(true);

        paintLine = new Paint();
        paintLine.setColor(Color.parseColor("#4444ff"));
        paintLine.setStyle(Paint.Style.FILL);
        paintLine.setAntiAlias(true);
        paintLine.setStrokeWidth(10);

        paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setTextSize(40);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setAntiAlias(true);
    }

    private void initializeMetrics() {
        boxWidth = (Options.cols - 1) * Theme.space;
        boxHeight = (Options.rows - 1) * Theme.space;

        screenHeight = G.displayMetrics.heightPixels;
        screenWidth = G.displayMetrics.widthPixels;

        screenWidthHalf = screenWidth / 2;

        offsetX = (screenWidth - boxWidth) / 2;
        offsetY = (screenHeight - boxHeight) / 2;
    }


    public void restGame() {

        State.playerScores[0] = 0;
        State.playerScores[1] = 0;


        Debug.isDebugMode = false;
        State.isSide1 = true;
        State.isGameOver = false;

        State.lines.clear();
        State.boxes.clear();
        refresh();
    }

    private void refresh(){
        if (State.boxes.size() == (Options.cols - 1) * (Options.rows - 1)) {
            State.isGameOver = true;
        }
        invalidate();
    }

    private float computeDiff(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private int getPlayerColor(int playerIndex){
        return Theme.playerColors[playerIndex -1];
    }

    private int getPlayerIndex(){
        return State.isSide1 ? 1 : 2;
    }

    private int getPlayerScore(int playerIndex){
        return State.playerScores[playerIndex -1];
    }


    private void increasePlayerScore(int playerIndex){
        State.playerScores[playerIndex - 1] ++;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode()) {
            return;
        }
        drawBackground(canvas);
        drawConnectedLines(canvas);
        drawBoxes(canvas);
        drawDots(canvas);
        drawScores(canvas);
        drawDebugTouchPosition(canvas);
        drawDebugNaming(canvas);
        if (State.isGameOver) {
            drawFinishMessage(canvas);
        }
    }

    private Position getPointPosition(int i, int j) {
        int x = offsetX + (i * Theme.space);
        int y = offsetY + ((Options.rows - 1 - j) * Theme.space);

        return new Position(x, y);
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Theme.backgroundColor);
    }

    private void drawConnectedLines(Canvas canvas) {
        for (Line line : State.lines) {
            drawLine(canvas, line);
        }
    }

    private void drawLine(Canvas canvas, Line line) {
        Position p1 = getPointPosition(line.i1, line.j1);
        Position p2 = getPointPosition(line.i2, line.j2);
        paintLine.setColor(getPlayerColor(line.playerIndex));
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paintLine);
    }

    private void drawBoxes(Canvas canvas) {
        for (Box box : State.boxes) {
            paintBox.setColor(getPlayerColor(box.playerIndex));
            Position boxPos = getPointPosition(box.i, box.j);
            canvas.drawCircle(boxPos.x + Theme.space / 2f, boxPos.y - Theme.space / 2f, 30, paintBox);
        }
    }

    private void drawDots(Canvas canvas) {
        for (int i = 0; i < Options.cols; i++) {
            for (int j = 0; j < Options.rows; j++) {
                Position point = getPointPosition(i, j);
                canvas.drawCircle(point.x, point.y, Theme.radius, paintDot);
            }
        }
    }




    private void drawPlayerScore(Canvas canvas , int playerIndex, int x, int y) {
        paintBox.setColor(getPlayerColor(playerIndex));
        canvas.drawCircle(x, y, 60, paintBox);
        canvas.drawText("" + getPlayerScore(playerIndex), x, y + 10, paintText);
        canvas.drawText("player" + playerIndex, x, y + 110, paintText);
    }
    private void drawScores(Canvas canvas){
        drawPlayerScore(canvas , 1,screenWidthHalf - 100, 100);
        drawPlayerScore(canvas , 2,screenWidthHalf + 100, 101);
    }

    private void drawDebugTouchPosition(Canvas canvas){
        if (!Debug.isDebugMode || !Debug.drawTouch) {
            return;
        }
        canvas.drawCircle(touchX, touchY, 10, paintTouch);
    }

    private void drawDebugNaming(Canvas canvas) {
        if (!Debug.isDebugMode || !Debug.drawDotNames){
            return;
        }
        for (int i = 0; i < Options.cols; i++) {
            for (int j = 0; j < Options.rows; j++) {
                Position point = getPointPosition(i, j);
                String name = "" + i + "," + j;
                canvas.drawText(name, point.x, point.y + 50, paintText);
            }
        }
    }


    private void drawFinishMessage(Canvas canvas){
        canvas.drawText(getGameFinishMessage(), screenWidthHalf, screenHeight - 350, paintText);
    }

    private String getGameFinishMessage(){
        String message = "";
        if (getPlayerScore(1) == getPlayerScore(2)) {
            message = "GAME DRAW";
        } else if (getPlayerScore(1)  >getPlayerScore(2) ) {
            message = "PLAYER 1 WON THE GAME";
        } else {
            message = "PLAYER 2 WON THE GAME";
        }

        return  message;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (State.isGameOver) {
            return true;
        }

        touchX = event.getX();
        touchY = event.getY();

        drawLine();
        refresh();

        return super.onTouchEvent(event);
    }


    private ArrayList<Diff> getDiffsByOrder(){
        ArrayList<Diff> diffs = new ArrayList<>();
        for (int i = 0; i < Options.cols; i++) {
            for (int j = 0; j < Options.rows; j++) {
                Position position = getPointPosition(i, j);
                float diff = computeDiff(touchX, touchY, position.x, position.y);
                diffs.add(new Diff(i, j, diff));
            }
        }

        Collections.sort(diffs, new Comparator<Diff>() {
            @Override
            public int compare(Diff o1, Diff o2) {
                return o1.diff.compareTo(o2.diff);
            }
        });

        return  diffs;
    }

    private void drawLine() {
        ArrayList<Diff> diffs = getDiffsByOrder();

        Diff point1 = diffs.get(0);
        Diff point2 = diffs.get(1);

        Diff firstPoint;
        Diff secondPoint;

        Box box1;
        Box box2 = null;
        if (point1.i == point2.i) {
            //vertical
            if (point1.j < point2.j) {
                firstPoint = point1;
                secondPoint = point2;
            } else {
                firstPoint = point2;
                secondPoint = point1;
            }

            box1 = new Box(firstPoint.i, firstPoint.j);
            if (firstPoint.i > 0) {
                box2 = new Box(firstPoint.i - 1, firstPoint.j);
            }
        } else {
            // horizontal
            if (point1.i < point2.i) {
                firstPoint = point1;
                secondPoint = point2;
            } else {
                firstPoint = point2;
                secondPoint = point1;
            }


            box1 = new Box(firstPoint.i, firstPoint.j);
            if (firstPoint.j > 0) {
                box2 = new Box(firstPoint.i, firstPoint.j - 1);
            }
        }
        // if this line is already connected
        for (Line line : State.lines) {
            if (line.i1 == firstPoint.i && line.j1 == firstPoint.j && line.i2 == secondPoint.i && line.j2 == secondPoint.j) {
                return;
            }
        }
        // add line to list of connected lines
        Line line = new Line(firstPoint.i, firstPoint.j, secondPoint.i, secondPoint.j, getPlayerIndex());
        State.lines.add(line);



        // check if player get award
        boolean wonBox1 = checkBox(box1);
        boolean wonBox2 = false;

        if (box2 != null) {
            wonBox2 = checkBox(box2);
        }

        boolean mustPlayerNextPlayer = !wonBox1 && !wonBox2;

        // if switching side required
        if (mustPlayerNextPlayer) {
            State.isSide1 = !State.isSide1;
        }

    }

    private boolean checkBox(Box box) {
        int i = box.i;
        int j = box.j;

        boolean hasLeft = false;
        boolean hasRight = false;
        boolean hasTop = false;
        boolean hasBottom = false;

        for (Line line : State.lines) {
            if (line.i1 == i && line.j1 == j && line.i2 == i && line.j2 == j + 1) {
                hasLeft = true;
            }
            if (line.i1 == i + 1 && line.j1 == j && line.i2 == i + 1 && line.j2 == j + 1) {
                hasRight = true;
            }
            if (line.i1 == i && line.j1 == j + 1 && line.i2 == i + 1 && line.j2 == j + 1) {
                hasTop = true;
            }
            if (line.i1 == i && line.j1 == j && line.i2 == i + 1 && line.j2 == j) {
                hasBottom = true;
            }
        }

        boolean isFullConnected = hasLeft && hasRight && hasTop && hasBottom;

        if (isFullConnected) {
            box.playerIndex = getPlayerIndex();
            State.boxes.add(box);
            increasePlayerScore(box.playerIndex);

            return true;
        }
        return false;
    }

}
