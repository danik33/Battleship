package dema.battleships;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class Game extends Activity {

    ImageView canvas;

    Bitmap bitmap, buffer;
    Bitmap back, mid, front;
    Paint paint;
    Canvas cv;
    View decorView;

    Board board;
    GameStage gameStage;


    int screenWidth, screenHeight;
    int x;
    boolean right;
    int squareSize = convert(9.2, false);
    int offsetX = convert(4, true);
    int offsetY = convert(4, false);

    ArrayList<Ship> player1, player2;


    Thread redrawing, logic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        player1 = new ArrayList<Ship>();
        player2 = new ArrayList<Ship>();

        placeShip(true, new Ship(Rotation.HORIZONTAL, ShipType.CELL5, 2, 2));
        placeShip(true, new Ship(Rotation.VERTICAL, ShipType.CELL1, 6, 6));

        decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);


        gameStage = GameStage.Player1Ships;
        board = new Board();

        //Resolution
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        squareSize = convert(9.2, false);
        offsetX = convert(4, true);
        offsetY = convert(4, false);

        readImages();


        //Setting up drawing part;
        canvas = findViewById(R.id.battleground);
        bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.RGB_565);

        paint = new Paint();


        canvas.setImageBitmap(bitmap);

        System.out.println("Height:" + canvas.getHeight() + ", Screen height: " + screenHeight + ", pos?:" + canvas.getX());

        x = 100;
        right = true;


        canvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                processTouch(event.getX(), event.getY());

                return false;
            }
        });

        //Process to redraw
        redrawing = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {

                        redraw();
                        Thread.sleep(1000 / 60);
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }

            }
        });

        logic = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (right)
                            x += 5;
                        else
                            x -= 5;
                        if (x + 400 > screenWidth || x < 0)
                            right = !right;
                        Thread.sleep(10);
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
            }
        });
        redrawing.start();
        logic.start();


    }


    /**
     * Converts percentages of screen to pixels
     * Example: (50, true) will return half of the width of the screen
     *
     * @return
     */
    public int convert(double percentage, boolean width) {
        if (width) {
            return (int) Math.floor(screenWidth * (percentage / 100));
        }
        return (int) Math.floor(screenHeight * (percentage / 100));
    }


    private void processTouch(float x, float y) {
        System.out.println("Touch: (" + x + ", " + y + ")");

        int tileX, tileY;
        tileX = ((int) x - offsetX) / squareSize;
        tileY = ((int) y - offsetY) / squareSize;
//
        if (tileX >= 0 && tileX < 10 && tileY >= 0 && tileY < 10)  //Board touch
        {
            board.shoot(tileX, tileY);
        }
    }


    private void readImages() {
        Resources res = getApplicationContext().getResources();
        back = BitmapFactory.decodeResource(res, R.drawable.back);
        mid = BitmapFactory.decodeResource(res, R.drawable.mid);
        front = BitmapFactory.decodeResource(res, R.drawable.front);

        back = Bitmap.createScaledBitmap(back, squareSize, squareSize, false);
        mid = Bitmap.createScaledBitmap(mid, squareSize, squareSize, false);
        front = Bitmap.createScaledBitmap(front, squareSize, squareSize, false);

    }


    public void placeShip(boolean player1, int x, int y, Rotation r, ShipType t)
    {
        Ship ship = new Ship(r, t, x, y);
        if(player1)
            this.player1.add(ship);
        else
            this.player2.add(ship);
    }

    public void placeShip(boolean player1, Ship ship)
    {
        if(player1)
            this.player1.add(ship);
        else
            this.player2.add(ship);
    }


    public Bitmap createShipPic(ShipType type, Rotation rotation)
    {
        Bitmap b;
        if(rotation == Rotation.HORIZONTAL)
        {
            b = Bitmap.createBitmap(squareSize*(type.ordinal()+1), squareSize, Bitmap.Config.ARGB_8888);
            Canvas cv = new Canvas(b);
            cv.drawBitmap(front, 0,0, null);
            for(int i = 1; i <= type.ordinal(); i++)
            {

                if(i == type.ordinal())
                    cv.drawBitmap(back, i*squareSize,0, null);
                else
                    cv.drawBitmap(mid, i*squareSize, 0, null);
            }
        }
        else
        {
            b = Bitmap.createBitmap(squareSize, squareSize*(type.ordinal()+1), Bitmap.Config.ARGB_8888);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);

            Bitmap b1 = Bitmap.createBitmap(back, 0, 0, back.getWidth(), back.getHeight(), matrix, true);
            Bitmap m1 = Bitmap.createBitmap(mid, 0, 0, back.getWidth(), back.getHeight(), matrix, true);
            Bitmap f1 = Bitmap.createBitmap(front, 0, 0, back.getWidth(), back.getHeight(), matrix, true);
            Canvas cv = new Canvas(b);
            cv.drawBitmap(f1, 0,0, null);
            for(int i = 1; i <= type.ordinal(); i++)
            {

                if(i == type.ordinal())
                    cv.drawBitmap(b1, 0,i*squareSize, null);
                else
                    cv.drawBitmap(m1, 0, i*squareSize, null);
            }
        }

        return b;
    }
    public Bitmap createShipPic(Ship ship)
    {
        return createShipPic(ship.type, ship.rotation);
    }


    public void redraw()  //Double buffering logic
    {

        buffer = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.RGB_565);
        cv = new Canvas(buffer);


        draw();
        bitmap = buffer.copy(buffer.getConfig(), true);

        canvas.setImageBitmap(bitmap);
        canvas.invalidate();
    }

    public void draw()   //The actual drawing
    {
        //Clear everything
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(240,240,240));
        cv.drawRect(0, 0, screenWidth, screenHeight, paint);
        //


        //Drawing grid
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);


        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                cv.drawRect( offsetX + i*squareSize, offsetY + j*squareSize, offsetX + (i+1)*squareSize, offsetY +  (j+1)*squareSize, paint);
            }
        }


        //Filling grid
        paint.setStyle(Paint.Style.FILL);
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                if(board.getTile(i, j).isShot())
                    cv.drawRect( offsetX + i*squareSize, offsetY + j*squareSize, offsetX + (i+1)*squareSize, offsetY +  (j+1)*squareSize, paint);
            }
        }

        for(Ship p : player1)
        {
            cv.drawBitmap(createShipPic(p), offsetX + p.x*squareSize, offsetY + p.y*squareSize, null);
        }


        //
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cv.drawRoundRect(x, 100, x+400, 500, 50,50, paint);
        }


    }




}
