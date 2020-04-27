package dema.battleships.dema.battleships.UI;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class MyButton {

    float x,y;
    int width, height;
    Bitmap image;
    Action act;
    private boolean visible;

    public MyButton(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visible = true;
    }

    public MyButton(float x, float y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visible = true;
    }

    public MyButton(float x, float y, float width, float height)
    {
        this(x,y,(int)width, (int)height);
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public boolean getVisible()
    {
        return visible;
    }




    public void setImage(Bitmap b)
    {
        Bitmap k = b.copy(b.getConfig(), false);
        k = Bitmap.createScaledBitmap(k, width, height, false);
        this.image = k;
    }

    public void draw(Canvas cv)
    {
        draw(cv, Color.rgb(220, 220, 240));
    }

    public void draw(Canvas cv, int color)
    {
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(color);
        if(this.image == null)
        {
            cv.drawRoundRect(x, y, x + width, y + height,50,50, p);
        }
        else
        {
            cv.drawBitmap(image, x, y, null);
        }
    }



    public void setAction(Action k) { this.act = k;}


    public Rect getRect()
    {
        return new Rect((int)x, (int)y, (int)x + width, (int)y + height);
    }
}
