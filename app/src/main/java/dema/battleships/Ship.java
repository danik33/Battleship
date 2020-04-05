package dema.battleships;

public class Ship
{
    Rotation rotation;
    ShipType type;
    boolean[] tiles; //True is hit
    private int length;


    public Ship(Rotation rotation, ShipType type)
    {
        this.rotation = rotation;
        this.length = type.ordinal()+1;
        this.type = ShipType.values()[length-1];
        tiles = new boolean[length];
        for(int i = 0; i < tiles.length; i++)
        {
            tiles[i] = false;
        }
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
