package dema.battleships;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import dema.battleships.dema.battleships.UI.Action;
import dema.battleships.dema.battleships.UI.MyButton;
import dema.battleships.dema.battleships.UI.UI;

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

    int touches = 0;

    int screenWidth, screenHeight;
    int x;
    boolean right;
    int squareSize;
    float gameSquareSize;
    int offsetX;
    int offsetY;
    int backgroundColor;

    Point offsetP1, offsetP2;
    Point lastTileTouched;

    ArrayList<Ship> player1, player2;
    Board boardP1, boardP2;
    boolean player1Turn;
    ArrayList<ShipType> p1Fleet, p2Fleet;
    MyButton setShip, rotateButton, reset, random, next;
    MyButton mainMenu, replay;

    Bitmap[] ships;

    Bitmap arrow[];


    ShipType selected;

    Ship tempShip;
    Rotation tempRotation;
    boolean tempCanPlace;

    UI ui;




    Thread redrawing, logic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        backgroundColor = Color.rgb(240,240,240);

        ships = new Bitmap[16];

        gameStage = GameStage.Player1Ships;
        boardP1 = new Board();
        boardP2 = new Board();

        player1 = new ArrayList<>();
        player2 = new ArrayList<>();

        p1Fleet = ShipType.getFleetV2();
        p2Fleet = ShipType.getFleetV2();

        player1Turn = true;

        arrow = new Bitmap[2];






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
        gameSquareSize = squareSize*0.85f;
        offsetX = convert(4, true);
        offsetY = convert(4, false);
        offsetP1 = new Point(offsetX, offsetY + convert(10, false));
        offsetP2 = new Point(convert(57, true), offsetY + convert(10, false));



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
//        place = new Rect(convert(53, true), convert(57, false), convert(53, true) + squareSize*2, convert(57, false) + squareSize*2 + 7  );

        readImages();


        initUI();


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

    private void initUI()
    {
        ui = new UI();
        setShip = new MyButton(convert(53, true), convert(65, false), (int)(squareSize*1.5), (int)(squareSize*1.5));
        setShip.setImage(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ok));
        setShip.setAction(new Action()
        {
            @Override
            public void act()
            {
                if(gameStage == GameStage.Player1Ships || gameStage == GameStage.Player2Ships)
                {
                    if (tempShip != null && tempCanPlace)
                    {
                        ArrayList<ShipType> k = (player1Turn) ? p1Fleet : p2Fleet;
                        if(k.contains(tempShip.type))
                            placeShip(tempShip);
                        tempShip = null;
                        if(k.isEmpty())
                            next.setVisible(true);
                        else
                            next.setVisible(false);
                    }
                }
            }
        });


        rotateButton = new MyButton(convert(53, true), convert(80, false), (int)(squareSize*1.5), (int)(squareSize*1.5));
        rotateButton.setImage(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.rotate));
        rotateButton.setAction(new Action() {
            @Override
            public void act()
            {
                if(gameStage == GameStage.Player1Ships || gameStage == GameStage.Player2Ships)
                {
                    tempRotation = tempRotation.invert();
                    if(tempShip != null)
                    {
                        tempShip = new Ship(tempRotation, selected, tempShip.x, tempShip.y);
                        tempCanPlace = canPlace();
                    }
                }
            }
        });

        reset = new MyButton(convert(53, true), convert(5, false), (int)(squareSize*1.5), (int)(squareSize*1.5));
        reset.setImage(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.reset));
        reset.setAction(new Action()
        {
            @Override
            public void act()
            {
                resetShips();
                next.setVisible(false);
            }
        });

        random = new MyButton(convert(53, true), convert(20, false), (int)(squareSize*1.5), (int)(squareSize*1.5));
        random.setImage(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.random));
        random.setAction(new Action()
        {
            @Override
            public void act()
            {
                randomizeShips();
                next.setVisible(true);
            }
        });

        next = new MyButton(convert(90, true), convert(88, false), squareSize*2, squareSize);
        next.setVisible(false);
        next.setImage(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.arrow));
        next.setAction(new Action()
        {
            @Override
            public void act()
            {
                nextStage();
            }
        });


        mainMenu = new MyButton(convert(25, true), convert(55, false), squareSize*4, squareSize*2);
        mainMenu.setImage(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.mainmenu));
        mainMenu.setVisible(false);
        mainMenu.setAction(new Action() {
            @Override
            public void act()
            {
                Intent t = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(t);
            }
        });

        replay = new MyButton(convert(60, true), convert(55, false), squareSize*4, squareSize*2);
        replay.setImage(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.playagain));
        replay.setVisible(false);
        replay.setAction(new Action() {
            @Override
            public void act()
            {
                Intent t = new Intent(getApplicationContext(), Game.class);
                startActivity(t);
            }
        });

        ui.addButton(replay);
        ui.addButton(mainMenu);
        ui.addButton(setShip);
        ui.addButton(rotateButton);
        ui.addButton(reset);
        ui.addButton(random);
        ui.addButton(next);
    }

    private void nextStage()
    {
        gameStage = gameStage.nextStage();
        switch(gameStage)
        {
            case Player1Ships:
                rescaleShipImages(squareSize);
            case Player2Ships:
                random.setVisible(true);
                reset.setVisible(true);
                setShip.setVisible(true);
                rotateButton.setVisible(true);
                player1Turn = false;
                break;
            case Game:
                next.setVisible(false);
                ships = new Bitmap[8];
                rescaleShipImages((int)gameSquareSize);
                player1Turn = true;
            case PassTO:
                random.setVisible(false);
                reset.setVisible(false);
                setShip.setVisible(false);
                rotateButton.setVisible(false);
                break;
        }
    }

    private void rescaleShipImages(int squareSize)
    {
        Resources res = getApplicationContext().getResources();
        back = BitmapFactory.decodeResource(res, R.drawable.back);
        mid = BitmapFactory.decodeResource(res, R.drawable.mid);
        front = BitmapFactory.decodeResource(res, R.drawable.front);


        back = Bitmap.createScaledBitmap(back, squareSize, squareSize, false);
        mid = Bitmap.createScaledBitmap(mid, squareSize, squareSize, false);
        front = Bitmap.createScaledBitmap(front, squareSize, squareSize, false);

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

    public void randomizeShips()
    {
        Board b;
        if(player1Turn)
        {

            boardP1 = new Board();
            b = boardP1;
            player1.clear();
            p1Fleet = ShipType.getClearFleet();
        }
        else
        {

            boardP2 = new Board();
            b = boardP2;
            player2.clear();
            p2Fleet = ShipType.getClearFleet();
        }
        selected = null;
        tempShip = null;
        ArrayList<ShipType> fleet = ShipType.getFleetV2();
        for(ShipType s : fleet)
        {
            Ship tr;
            int x,y;
            do
            {
                x = rand(0, 9);
                y = rand(0, 9);
                tr = new Ship(Rotation.random(), s, x ,y);

            } while(!b.canPlace(tr));

            placeShip(tr);
        }
    }

    public int rand(int min, int max)
    {
        return (int) (Math.random()*(max-min+1)+min);
    }


    private void processTouch(float x, float y) {
        System.out.println("Touch: (" + x + ", " + y + ")");

        int tileX, tileY;
        tileX = ((int) x - offsetX) / squareSize;
        tileY = ((int) y - offsetY) / squareSize;


        boolean hit = ui.press((int)x, (int)y);

        if(gameStage == GameStage.Player1Ships || gameStage == GameStage.Player2Ships)
        {
            ShipType sh = touched((int)x, (int)y);
            if (selected != null && tileX >= 0 && tileX < 10 && tileY >= 0 && tileY < 10)  //Board touch
            {
                lastTileTouched = new Point(tileX, tileY);
                Ship trying = new Ship(tempRotation, selected, tileX, tileY);
                tempCanPlace = canPlace();

                tempShip = trying.clone();

            }
            else if(!hit)
            {
                ShipType temp = selected;
                selected = sh;
                if(temp != selected && tempShip != null && sh != null)
                {
                    tempShip = new Ship(tempRotation, sh, lastTileTouched.x, lastTileTouched.y);
                    tempCanPlace = canPlace();
                }
                if(selected == null)
                    tempShip = null;
                Log.v("dema.battleships", "Selected: " + selected);
            }

        }
        if(gameStage == GameStage.Game)
        {
            Point tileHit;
            if(player1Turn)
                tileHit = new Point((int)((x-offsetP2.x)/gameSquareSize), (int)((y-offsetP2.y)/gameSquareSize));
            else
                tileHit = new Point((int)((x-offsetP1.x)/gameSquareSize), (int)((y-offsetP1.y)/gameSquareSize));

            if (tileHit.x >= 0 && tileHit.x < 10 && tileHit.y >= 0 && tileHit.y < 10)  //Board touch
            {
                Board b = (player1Turn) ? boardP2 : boardP1;
                if(!b.getTile(tileHit.x, tileHit.y).isShot() && !b.shoot(tileHit.x, tileHit.y)) {
                    player1Turn = !player1Turn;
                    touches++;
                }
                if(touches > 10 || b.defeated())
                {
                    endGame();
                }
            }


        }

    }

    private void endGame()
    {
        nextStage();
        mainMenu.setVisible(true);
        replay.setVisible(true);
    }

    private boolean canPlace()
    {
        Ship p = new Ship(tempRotation, selected, lastTileTouched.x, lastTileTouched.y);
        if(player1Turn)
        {
            return boardP1.canPlace(p);
        }
        return boardP2.canPlace(p);
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
        Matrix matrix = new Matrix();
        matrix.postRotate(180);
        arrow[0] = BitmapFactory.decodeResource(res, R.drawable.arrow);
        arrow[0] = Bitmap.createScaledBitmap(arrow[0], squareSize*2, squareSize, false);
        arrow[1] = Bitmap.createBitmap(arrow[0], 0,0, arrow[0].getWidth(), arrow[0].getHeight(), matrix, true);


        back = Bitmap.createScaledBitmap(back, squareSize, squareSize, false);
        mid = Bitmap.createScaledBitmap(mid, squareSize, squareSize, false);
        front = Bitmap.createScaledBitmap(front, squareSize, squareSize, false);


        xMark = Bitmap.createScaledBitmap(xMark, (int)gameSquareSize , (int)gameSquareSize, false);
        explosion = Bitmap.createScaledBitmap(explosion, (int)gameSquareSize, (int)gameSquareSize, false);

    }


    public void placeShip(int x, int y, Rotation r, ShipType t)
    {

        Ship ship = new Ship(r, t, x, y);
        placeShip(ship);

    }

    public void resetShips()
    {
        if(player1Turn)
        {
            p1Fleet = ShipType.getFleetV2();
            boardP1 = new Board();
            player1.clear();

        }
        else
        {
            p2Fleet = ShipType.getFleetV2();
            boardP2 = new Board();
            player2.clear();
        }
        tempShip = null;
        selected = null;
        tempCanPlace = true;
    }

    public void placeShip(Ship ship)
    {
        if(player1Turn)
        {
            this.player1.add(ship);
            boardP1.setShip(ship);
            p1Fleet.remove(ship.type);
        }
        else
        {
            this.player2.add(ship);
            boardP2.setShip(ship);
            p2Fleet.remove(ship.type);
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
        int squareSize = (gameStage == GameStage.Game) ? (int)gameSquareSize : this.squareSize;
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
            case PostGame:
                drawPostGame();
                break;
        }
        ui.drawElements(cv);
        bitmap = buffer.copy(buffer.getConfig(), true);

        canvas.setImageBitmap(bitmap);
        canvas.invalidate();
    }

    public void drawPostGame()
    {
        drawGame();
        clear(Color.argb(70, 0,0,0));

        paint.setColor(Color.argb(170, 40, 170, 40));
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL);
        cv.drawRoundRect(convert(15, true), convert(20, false), convert(85, true), convert(80, false), 50, 50, paint);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        cv.drawRoundRect(convert(15, true), convert(20, false), convert(85, true), convert(80, false), 50, 50, paint);

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(convert(10, true));
        String message = (!player1Turn) ? "Player 1 won" : "Player 2 won";
        cv.drawText( message, convert(20, true), convert(37, false), paint);




    }

    private void drawGame()
    {
        clear(backgroundColor);

        drawGrid(offsetP1.x, offsetP1.y, squareSize*0.85f);
        drawGrid(offsetP2.x, offsetP2.y, squareSize*0.85f);

        drawTextures(offsetP1.x, offsetP1.y, true, squareSize*0.85f);
        drawTextures(offsetP2.x, offsetP2.y, false, squareSize*0.85f);

        drawTurn();




    }

    private void drawTurn()
    {
        Point arr = new Point(convert(46.5, true), convert(48, false));
        Bitmap ar = (player1Turn) ? arrow[0] : arrow[1];
        cv.drawBitmap(ar, arr.x, arr.y, null);

        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setTextSize(convert(5, true));

        String text = "Player " + ((player1Turn) ? "1" : "2") + " turn to shoot";
        cv.drawText(text, convert(30, true), convert(10, false), p);
    }

    private void drawTextures(int xOff, int yOff, boolean player, float squareSize)
    {
        Board b = (player) ? boardP1 : boardP2;


        for(Ship p : (player) ? player1 : player2)
        {
            if(p.shipSank())
                cv.drawBitmap(createShipPic(p), xOff + p.x*squareSize, yOff + p.y*squareSize, null);
        }


        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                if(b.getTile(i, j).isShot())
                {
                    if(b.hasShip(i, j))
                        cv.drawBitmap(explosion, xOff + i * squareSize, yOff + j * squareSize, null);
                    else
                        cv.drawBitmap(xMark, xOff + i * squareSize, yOff + j * squareSize, null);
                }
            }
        }
    }



    private void clear(int color)
    {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        cv.drawRect(0,0,screenWidth, screenHeight, paint);
    }


    private void drawPassing()
    {
        clear(backgroundColor);
        paint.setColor(Color.BLACK);
        paint.setTextSize(convert(6, true));
        cv.drawText("GET TO OTHER PLAYER PLEASE", convert(10, true), convert(20, false), paint);

    }



    public void drawGrid(int x, int y, float square)
    {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                cv.drawRect( x + i*square, y + j*square, x + (i+1)*square, y +  (j+1)*square, paint);
            }
        }
    }



    public void drawFleetPlacing()  
    {
        clear(backgroundColor);

        drawGrid(offsetX, offsetY, squareSize);

        drawChoosingShips();

        drawTempShip();



        for(Ship p : (player1Turn) ? player1 : player2)
        {
            cv.drawBitmap(createShipPic(p), offsetX + p.x*squareSize, offsetY + p.y*squareSize, null);
        }

    }

    private void drawTempShip()
    {
        try
        {
            if(tempShip != null)
                cv.drawBitmap(createShipPic(tempShip, (tempCanPlace) ? ColorFilter.Green : ColorFilter.Red), offsetX + tempShip.x*squareSize, offsetY + tempShip.y*squareSize, null);
        }
        catch (NullPointerException ee)
        {
            Log.v("dema.battleships", "Null pointer at temp ship "); //I don't know why this happens actually
        }
    }




    private void drawChoosingShips()
    {

        paint.setTextSize(convert(3, true));
        paint.setStyle(Paint.Style.FILL);
        String place = "Player ";
        if(player1Turn)
            place += 1 + " ships";
        else
            place += 2 + " ships";

        cv.drawText(place, convert(65, true), convert(7, false), paint);
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
