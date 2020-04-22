package dema.battleships;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import static dema.battleships.ColorFilter.Normal;

public class Game extends Activity {

    ImageView canvas;

    Bitmap bitmap, buffer;
    Bitmap back, mid, front;
    Bitmap explosion, xMark, background;
    Paint paint;
    Canvas cv;
    View decorView;

    Rect[] shipSelec;

    Rect rotate, place;


    GameStage gameStage;


    int screenWidth, screenHeight;
    int x;
    boolean right;
    int squareSize;
    int offsetX;
    int offsetY;

    ArrayList<Ship> player1, player2;
    Board boardP1, boardP2;
    boolean player1Turn;
    ArrayList<ShipType> p1Fleet, p2Fleet;

    Bitmap[] ships;


    ShipType selected;

    Ship tempShip;
    Rotation tempRotation;
    boolean tempCanPlace;




    Thread redrawing, logic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);



        ships = new Bitmap[8];

        gameStage = GameStage.Player1Ships;
        boardP1 = new Board();
        boardP2 = new Board();

        player1 = new ArrayList<>();
        player2 = new ArrayList<>();

        p1Fleet = ShipType.getFleetV2();
        p2Fleet = ShipType.getFleetV2();

        player1Turn = true;






//        placeShip(true, new Ship(Rotation.HORIZONTAL, ShipType.CELL5, 2, 2));

        decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);




        //Resolution
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        squareSize = convert(9.2, false);
        offsetX = convert(4, true);
        offsetY = convert(4, false);

        shipSelec = new Rect[4];
        int twenty = convert(20, false);
        int selecX = convert(75, true);
        shipSelec[0] = new Rect(selecX, twenty*4, selecX + 2*squareSize, twenty*4+squareSize);
        shipSelec[1] = new Rect(selecX, twenty*3, selecX + 3*squareSize, twenty*3+squareSize);
        shipSelec[2] = new Rect(selecX, twenty*2, selecX + 4*squareSize, twenty*2+squareSize);
        shipSelec[3] = new Rect(selecX, twenty, selecX + 5*squareSize, twenty+squareSize);

        tempRotation = Rotation.HORIZONTAL;
        tempCanPlace = true;

        rotate = new Rect(convert(53, true), convert(77, false), convert(53, true) + squareSize*2, convert(77, false) + squareSize *2 + 7  );
        place = new Rect(convert(53, true), convert(57, false), convert(53, true) + squareSize*2, convert(57, false) + squareSize*2 + 7  );

        readImages();


        //Setting up drawing part;
        canvas = findViewById(R.id.battleground);
        bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.RGB_565);

        paint = new Paint();


        canvas.setImageBitmap(bitmap);

        System.out.println("Height:" + canvas.getHeight() + ", Screen height: " + screenHeight + ", pos?:" + canvas.getX());




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

        if(gameStage == GameStage.Player1Ships || gameStage == GameStage.Player2Ships)
        {
            ShipType sh = touched((int)x, (int)y);
            if (selected != null && tileX >= 0 && tileX < 10 && tileY >= 0 && tileY < 10)  //Board touch
            {
                Ship trying = new Ship(tempRotation, selected, tileX, tileY);
                tempCanPlace = boardP1.canPlace(trying);
                tempShip = trying.clone();

            }
            else if(new Rect((int)x,(int)y,(int)x+1,(int)y+1).intersect(place))
            {
                if(tempCanPlace)
                {
                    placeShip(player1Turn, tempShip);
                    tempShip = null;
                }
            }
            else
            {
                selected = sh;
                if(selected == null)
                    tempShip = null;
                Log.v("dema.battleships", "Selected: " + selected);
            }

        }

        if (tileX >= 0 && tileX < 10 && tileY >= 0 && tileY < 10)  //Board touch
        {

            if(gameStage == GameStage.Game)
            {
                if(player1Turn)
                    boardP2.shoot(tileX, tileY);
                else
                    boardP1.shoot(tileX, tileY);
                player1Turn = !player1Turn;
            }

        }
        else
        {
            if(gameStage == GameStage.Player1Ships || gameStage == GameStage.Player2Ships)
            {
//                if(x)
            }
        }
    }


    public ShipType touched(int x, int y)
    {
        Rect touched = new Rect(x, y, x+1, y+1);
        for(int i = 0; i < shipSelec.length; i++)
        {
            if(shipSelec[i].contains(touched))
            {
                ShipType b = ShipType.values()[i+1];
                return b;
            }

        }
        return null;
    }


    private void readImages() {
        Resources res = getApplicationContext().getResources();
        back = BitmapFactory.decodeResource(res, R.drawable.back);
        mid = BitmapFactory.decodeResource(res, R.drawable.mid);
        front = BitmapFactory.decodeResource(res, R.drawable.front);
        xMark = BitmapFactory.decodeResource(res, R.drawable.xsign);
        explosion = BitmapFactory.decodeResource(res, R.drawable.explosion);

        back = Bitmap.createScaledBitmap(back, squareSize, squareSize, false);
        mid = Bitmap.createScaledBitmap(mid, squareSize, squareSize, false);
        front = Bitmap.createScaledBitmap(front, squareSize, squareSize, false);
        xMark = Bitmap.createScaledBitmap(xMark, squareSize, squareSize, false);
        explosion = Bitmap.createScaledBitmap(explosion, squareSize, squareSize, false);

    }


    public void placeShip(boolean player1, int x, int y, Rotation r, ShipType t)
    {

        Ship ship = new Ship(r, t, x, y);
        placeShip(player1, ship);

    }

    public void placeShip(boolean player1, Ship ship)
    {
        if(player1)
        {
            if(p1Fleet.contains(ship.type))
            {
                this.player1.add(ship);
                boardP1.setShip(ship);
                p1Fleet.remove(ship.type);
            }
        }
        else
        {
            if(p2Fleet.contains(ship.type))
            {
                this.player2.add(ship);
                boardP2.setShip(ship);
                p2Fleet.remove(ship.type);
            }
        }

    }

    private Bitmap createShipPic(Ship ship, ColorFilter cf)
    {
        return createShipPic(ship.type, ship.rotation, cf);
    }

    public Bitmap createShipPic(ShipType type, Rotation rotation, ColorFilter cf) // Gray for out of, and false = green for selected
    {
        Bitmap k = cloneBitmap(createShipPic(type, rotation));
        if(cf == Normal)
            return k;
        Canvas cv = new Canvas(k);
        Paint p = new Paint();
        p.setColor(cf.getColor());
        p.setStyle(Paint.Style.FILL);



        for(int i = 0; i < k.getWidth(); i++)
        {
            for(int j = 0; j < k.getHeight(); j++)
            {
                if(k.getPixel(i, j) != 0)
                    cv.drawPoint(i, j, p);
            }
        }

        return k;
    }

    public Bitmap cloneBitmap(Bitmap bm)
    {
        Bitmap b = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), bm.getConfig());
        Canvas op = new Canvas(b);
        op.drawBitmap(bm, 0, 0, null);
        return b;
    }




    public Bitmap createShipPic(ShipType type, Rotation rotation)
    {
        int index = type.ordinal()-1;
        if(rotation == Rotation.HORIZONTAL)
            index += 4;
        if(ships[index] != null)
        {
            return ships[index];
        }
        else
        {
            Bitmap b;
            if (rotation == Rotation.HORIZONTAL) {
                b = Bitmap.createBitmap(squareSize * (type.ordinal() + 1), squareSize, Bitmap.Config.ARGB_8888);
                Canvas cv = new Canvas(b);
                cv.drawBitmap(front, 0, 0, null);
                for (int i = 1; i <= type.ordinal(); i++) {

                    if (i == type.ordinal())
                        cv.drawBitmap(back, i * squareSize, 0, null);
                    else
                        cv.drawBitmap(mid, i * squareSize, 0, null);
                }
            } else {
                b = Bitmap.createBitmap(squareSize, squareSize * (type.ordinal() + 1), Bitmap.Config.ARGB_8888);

                Matrix matrix = new Matrix();
                matrix.postRotate(90);

                Bitmap b1 = Bitmap.createBitmap(back, 0, 0, back.getWidth(), back.getHeight(), matrix, true);
                Bitmap m1 = Bitmap.createBitmap(mid, 0, 0, back.getWidth(), back.getHeight(), matrix, true);
                Bitmap f1 = Bitmap.createBitmap(front, 0, 0, back.getWidth(), back.getHeight(), matrix, true);
                Canvas cv = new Canvas(b);
                cv.drawBitmap(f1, 0, 0, null);
                for (int i = 1; i <= type.ordinal(); i++) {

                    if (i == type.ordinal())
                        cv.drawBitmap(b1, 0, i * squareSize, null);
                    else
                        cv.drawBitmap(m1, 0, i * squareSize, null);
                }
            }
            ships[index] = b;
        }

        return createShipPic(type, rotation);
    }
    public Bitmap createShipPic(Ship ship)
    {
        return createShipPic(ship.type, ship.rotation);
    }


    public void redraw()  //Double buffering logic
    {

        buffer = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.RGB_565);
        cv = new Canvas(buffer);

        switch(gameStage)
        {
            case Player1Ships:
                drawFleetPlacing();
                break;
            case PassTO:
                drawPassing();
                break;
            case Player2Ships:
                drawFleetPlacing();
                break;
            case Game:
                drawGame();
                break;
        }
        bitmap = buffer.copy(buffer.getConfig(), true);

        canvas.setImageBitmap(bitmap);
        canvas.invalidate();
    }

    private void drawGame()
    {
    }

    private void drawPassing()
    {
    }


    public void drawBackground()
    {
        paint.setColor(Color.rgb(240,240, 240));
        paint.setStyle(Paint.Style.FILL);
        cv.drawRect(new Rect(0, 0, screenWidth, screenHeight), paint);
    }

    public void drawGrid(int x, int y)
    {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                cv.drawRect( x + i*squareSize, y + j*squareSize, x + (i+1)*squareSize, y +  (j+1)*squareSize, paint);
            }
        }
    }



    public void drawFleetPlacing()   //The actual drawing
    {
        drawBackground();

        drawGrid(offsetX, offsetY);

        drawChoosingShips();

        drawTempShip();

        drawButtons();



        for(Ship p : player1)
        {
            cv.drawBitmap(createShipPic(p), offsetX + p.x*squareSize, offsetY + p.y*squareSize, null);
        }

        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                if(boardP1.getTile(i, j).isShot())
                {
                    if(boardP1.hasShip(i, j))
                        cv.drawBitmap(explosion, offsetX + i * squareSize, offsetY + j * squareSize, null);
                    else
                        cv.drawBitmap(xMark, offsetX + i * squareSize, offsetY + j * squareSize, null);
                }
            }
        }







    }

    private void drawTempShip()
    {
        if(tempShip != null)
            cv.drawBitmap(createShipPic(tempShip, (tempCanPlace) ? ColorFilter.Green : ColorFilter.Red), offsetX + tempShip.x*squareSize, offsetY + tempShip.y*squareSize, null);
    }



    private void drawButtons()
    {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        cv.drawRect(rotate, paint);

        paint.setColor(Color.RED);
        cv.drawRect(place, paint);
    }

    private void drawChoosingShips()
    {

        paint.setTextSize(convert(3, true));
        paint.setStyle(Paint.Style.FILL);

        cv.drawText("Place ships", convert(65, true), convert(7, false), paint);
        for(int i = 0; i < 4; i++)
        {
            cv.drawText("X " + countFleet(ShipType.values()[i+1], (player1Turn) ? p1Fleet : p2Fleet), shipSelec[i].left - convert(5, true), shipSelec[i].top + convert(6.5, false), paint);
            cv.drawBitmap(createShipPic(ShipType.values()[i+1], Rotation.HORIZONTAL, (selected == ShipType.values()[i+1]) ? ColorFilter.Green : Normal), convert(75, true), convert(80- i*20, false), null);
        }





    }

    private int countFleet(ShipType st, ArrayList<ShipType> p1Fleet)
    {
        int i = 0;
        for(ShipType k : p1Fleet)
            if(k == st)
                i++;

        return i;
    }


}
