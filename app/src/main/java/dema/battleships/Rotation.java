package dema.battleships;


public enum Rotation
{
    VERTICAL,
    HORIZONTAL;

    public static Rotation random()
    {
        if(Math.random() < 0.5)
            return VERTICAL;
        else
            return HORIZONTAL;
    }
}
