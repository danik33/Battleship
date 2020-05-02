package dema.battleships.dema.battleships.UI;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

public class Pane extends Element
{

    ArrayList<Element> subElements;

    public Pane(int x, int y, int width, int height)
    {
        super(x, y, width, height);
        subElements = new ArrayList<>();
    }

    public void addElement(Element e)
    {
        subElements.add(e);
    }



    public void draw(Canvas cv)
    {
        Paint p = new Paint();

        p.setStrokeWidth(5);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.BLACK);
        cv.drawRoundRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(),50,50, p);

        p.setStrokeWidth(0);
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(240,240,240));
        cv.drawRoundRect(getX()+5, getY()+5, getX() + getWidth()-5, getY() + getHeight()-5,50,50, p);

        for(Element k : subElements)
        {
            k.draw(cv);
        }

    }

    @Override
    public void draw(Canvas cv, int x, int y) {

    }


    @Override
    public void press(int x, int y)
    {
        for(Element b : subElements)
        {
            if(new Rect(x, y, x+1, y+1).intersect(b.getRect()))
            {
                if(b.isVisible())
                {
                    b.press(x-b.getX(), y-b.getY());
                }
            }
        }
    }
}
