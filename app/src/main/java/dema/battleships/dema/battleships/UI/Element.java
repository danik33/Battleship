package dema.battleships.dema.battleships.UI;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public abstract class Element {

    private boolean visible;
    private int x, y;
    private Action act;
    private int height, width;
    Bitmap b;


    public Element(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visible = true;
    }



    public void draw(Canvas cv)
    {
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.BLACK);
        if(this.getBitmap() == null)
        {
            cv.drawRoundRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(),50,50, p);
        }
        else
        {
            cv.drawBitmap(b, getX(), getY(), null);
        }
    }

    public abstract void draw(Canvas cv, int x, int y);

    public abstract void press(int x, int y);


    public int getX(){return this.x;}
    public int getY(){return this.y;}
    public int getWidth(){return this.width;}
    public int getHeight(){return this.height;}
    public boolean isVisible(){return this.visible;}

    public void setWidth(int width) {
        this.width = width;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    public Action getActtion() {
        return act;
    }

    public void setAction(Action act) {
        this.act = act;
    }

    public Bitmap getBitmap() {
        return b;
    }

    public void setImage(Bitmap b)
    {
        Bitmap k = Bitmap.createScaledBitmap(b, width, height, false);
        this.b = k;
    }

    public Rect getRect()
    {
        return new Rect((int)x, (int)y, (int)x + width, (int)y + height);
    }








}
