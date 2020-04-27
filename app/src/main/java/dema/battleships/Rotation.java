package dema.battleships;


public enum Rotation
{
    VERTICAL,
    HORIZONTAL;

    public static Rotation random()
    {
        String a = "";
        if(Math.random() < 0.5)
            return VERTICAL;
        else
            return HORIZONTAL;
    }

    public Rotation invert()
    {
        if(this == VERTICAL)
            return HORIZONTAL;
        return VERTICAL;
    }

}
