package dema.battleships.dema.battleships.UI;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class MyButton extends Element
{

    public MyButton(int x, int y, int width, int height)
    {
        super(x,y,width,height);
    }


    public void draw(Canvas cv)
    {
        draw(cv, Color.rgb(220, 220, 240));
    }

    @Override
    public void press(int x, int y)
    {
        this.getActtion().act();
    }

    public void draw(Canvas cv, int color)
    {
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(color);
        if(this.getBitmap() == null)
        {
            cv.drawRoundRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(),50,50, p);
        }
        else
        {
            cv.drawBitmap(b, getX(), getY(), null);
        }
    }






}
