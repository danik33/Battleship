package dema.battleships.dema.battleships.UI;

import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;

public class UI {


    private ArrayList<MyButton> buttons;  //Could be any other ui elements if needed

    public UI(){
        buttons = new ArrayList<>();
    }


    public void addButton(MyButton btn)
    {
        buttons.add(btn);
    }

    public boolean press(int x, int y)
    {
        boolean hit = false;
        for(MyButton b : buttons)
        {
            if(new Rect(x, y, x+1, y+1).intersect(b.getRect()))
            {
                if(b.getVisible())
                {
                    if(b.act != null)
                    {
                        b.act.act();
                        hit = true;
                    }
                }
            }
        }
        return hit;
    }



    public void drawElements(Canvas cv)
    {
        for(MyButton b : buttons)
        {
            if(b.getVisible())
                b.draw(cv);
        }
    }



}
