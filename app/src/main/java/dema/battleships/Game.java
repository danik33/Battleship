package dema.battleships;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class Game extends AppCompatActivity {

    ImageView canvas;

    Bitmap bitmap, buffer;
    Paint paint;
    Canvas cv;

    Board board;

    int width, height;
    int x;
    boolean right;

    int topMargin = 100, sidesMargin = 20;

    Thread redrawing, logic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        board = new Board();

        //Resolution
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        sidesMargin = width/80;
        topMargin = height/10;


        //Setting up drawing part;
        canvas = findViewById(R.id.battleground);
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        paint = new Paint();


        canvas.setImageBitmap(bitmap);

        System.out.println("Height:" + canvas.getHeight() + ", Screen height: " + height + ", pos?:" + canvas.getX());

        x = 100;
        right = true;




        canvas.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                processTouch(event.getX(), event.getY());

                return false;
            }
        });

        //Process to redraw
        redrawing = new Thread(new Runnable() {
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {

                        redraw();
                        Thread.sleep(1000/60);
                    }
                    catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }
                }

            }
        });

        logic = new Thread(new Runnable() {
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {
                        if(right)
                            x += 5;
                        else
                            x -= 5;
                        if(x+400 > width || x < 0)
                            right = !right;
                        Thread.sleep(10);
                    }
                    catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }
                }
            }
        });
        redrawing.start();
        logic.start();






    }

    private void processTouch(float x, float y)
    {
        System.out.println("Touch: (" + x + ", " + y + ")");

        int tileX, tileY;
        int squareSize = (width - (sidesMargin * 2)) / 10;
        tileX = ((int)x-sidesMargin*2)/squareSize;
        tileY = ((int)y-topMargin)/squareSize;

        if(tileX >= 0 && tileX < 10 && tileY >= 0 && tileY < 10)  //Board touch
        {
            board.shoot(tileX, tileY);
        }


    }

    public void redraw()  //Double buffering logic
    {

        buffer = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
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
        cv.drawRect(0, 0, width, height, paint);
        //


        //Drawing grid
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
        int squareSize = (width - (sidesMargin * 2)) / 10;
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                cv.drawRect( sidesMargin+i*squareSize ,   topMargin+j * squareSize, sidesMargin+i*squareSize +squareSize ,  topMargin + j * squareSize + squareSize, paint);
            }
        }

        //Filling grid
        paint.setStyle(Paint.Style.FILL);
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                if(board.getTile(i, j).isShot())
                    cv.drawRect( sidesMargin+i*squareSize ,   topMargin+j * squareSize, sidesMargin+i*squareSize +squareSize ,  topMargin + j * squareSize + squareSize, paint);
            }
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
