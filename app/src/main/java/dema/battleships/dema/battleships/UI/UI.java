package dema.battleships.dema.battleships.UI;

import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;

public class UI {


    private ArrayList<Element> elements;

    public UI(){
        elements = new ArrayList<>();
    }


    public void addElement(Element el)
    {
        elements.add(el);
    }

    public boolean press(int x, int y)
    {
        boolean hit = false;
        for(Element b : elements)
        {
            if(new Rect(x, y, x+1, y+1).intersect(b.getRect()))
            {
                if(b.isVisible())
                {
                    b.press(x-b.getX(), y-b.getY());
                }
            }
        }
        return hit;
    }



    public void drawElements(Canvas cv)
    {
        for(Element b : elements)
        {
            if(b.isVisible())
                b.draw(cv);
        }
    }



}
