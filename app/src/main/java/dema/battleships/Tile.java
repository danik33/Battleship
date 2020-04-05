package dema.battleships;

public class Tile {

    private boolean beenshot, ship;

    public Tile()
    {
        beenshot = false;
        ship = false;
    }

    public Tile(boolean ship)
    {
        this.ship = ship;
    }

    public boolean isShot()
    {
        return this.beenshot;
    }

    public boolean hasShip()
    {
        return this.ship;
    }

    public void shoot()
    {
        this.beenshot = true;
    }

    public void setShip()
    {
        ship = true;
    }




}
