package dema.battleships;

public class Tile {

    private boolean beenshot, ship;

    public Tile()
    {
        beenshot = false;
        ship = false;
    }

    public boolean isShot()
    {
        return this.beenshot;
    }

    public boolean hasShip()
    {
        return this.ship;
    }

    public boolean shoot()
    {
        this.beenshot = true;
        if(this.ship)
            return true;
        return false;
    }

    public void setShip()
    {
        ship = true;
    }




}
