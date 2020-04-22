package dema.battleships;

import android.graphics.Color;

public enum ColorFilter
{
    Normal,
    Gray,
    Red,
    Green;


    public int getColor() {
        if(this == Normal)
            return Color.argb(0,0,0,0);
        if(this == Gray)
            return Color.argb(50, 150,150,150);
        if(this == Red)
            return Color.argb(90, 255, 0, 0);
        if(this == Green)
            return Color.argb(50, 0, 200, 0);
        return -1;
    }

}
