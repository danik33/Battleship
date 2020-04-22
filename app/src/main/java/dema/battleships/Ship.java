package dema.battleships;

public class Ship
{
    public int x, y;
    public Rotation rotation;
    public ShipType type;
    private int length;


    public Ship(Rotation rotation, ShipType type, int x, int y)
    {
        this.rotation = rotation;
        this.length = type.ordinal()+1;
        this.type = ShipType.values()[length-1];
        this.x = x;
        this.y = y;
    }


    public Ship clone()
    {
        return new Ship(this.rotation, this.type, this.x, this.y);
    }




    public int getLength()
    {
        return this.length;
    }

    public Rotation getRotation()
    {
        return this.rotation;
    }

    public String toString()
    {
        return "Ship @" + type + "[" + rotation + "]";
    }


}
