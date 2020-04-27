package dema.battleships;

public class Ship
{
    public int x, y;
    public Rotation rotation;
    public ShipType type;
    private int length;
    boolean[] hit;


    public Ship(Rotation rotation, ShipType type, int x, int y)
    {
        this.rotation = rotation;
        this.length = type.ordinal()+1;
        this.type = ShipType.values()[length-1];
        this.x = x;
        this.y = y;
        hit = new boolean[length];
        for(int i = 0; i < hit.length; i++)
            hit[i] = false;
    }


    public Ship clone()
    {
        return new Ship(this.rotation, this.type, this.x, this.y);
    }


    public boolean hitSlot(int slot)
    {
        hit[slot] = true;
        return shipSank();
    }

    public boolean shipSank()
    {
        for(int i = 0; i < hit.length; i++)
        {
            if(!hit[i])
                return false;
        }
        return true;
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
