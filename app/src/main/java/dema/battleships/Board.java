package dema.battleships;


import android.graphics.Point;

import java.util.ArrayList;

public class Board {


    private Tile[][] tiles;

    private ArrayList<Ship> ships;


    public Board()
    {
        tiles = new Tile[10][10];
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                tiles[i][j] = new Tile();
            }
        }
        ships = new ArrayList<Ship>();

    }

    public boolean defeated()
    {
        boolean flag = true;
        for(Ship s : ships)
        {
            if(!s.shipSank())
                flag = false;
        }
        return flag;
    }

    public void setShip(Ship s)
    {
        int x = s.x, y = s.y;
        System.out.println(s + ": (" + x + ", " + y + ")");
        Point[] shipTiles = getShipTiles(s);
        for(Point p : shipTiles)
            tiles[p.x][p.y].setShip();

        ships.add(s);
    }

    public Point[] getShipTiles(Ship p)
    {
        Point[] til = new Point[p.getLength()];
        for(int i = 0; i < p.getLength(); i++)
        {

            if(p.getRotation() == Rotation.HORIZONTAL)
                til[i] = new Point(p.x + i,p.y);
            else
                til[i] = new Point(p.x ,p.y+ i);
        }
        return til;
    }

    public boolean hasShip(int x, int y) { return tiles[x][y].hasShip();}



    public boolean canPlace(Ship s)
    {
        int x = s.x;
        int y = s.y;
        if(x < 0 || y < 0)
            return false;
        if(s.getRotation() == Rotation.HORIZONTAL)
        {
            if(x+s.getLength()-1 > 9)
                return false;
        }
        else
        if(y+s.getLength()-1 > 9)
            return false;



        for(int i = x-1; i <= ( (s.getRotation() == Rotation.HORIZONTAL) ? x+s.getLength(): x+1 ) ; i++)
        {

            for(int j = y-1; j <= ( (s.getRotation() == Rotation.VERTICAL) ? y+s.getLength()  : y+1 ); j++)
            {
//				System.out.println("Checking (" + i + ", " + j + ")");
                if(i > 9 || j > 9 || i < 0 || j < 0)
                    continue;
                if(tiles[i][j].hasShip())
                    return false;
            }
        }

        return true;
    }


    public boolean shoot(int x, int y)
    {
        boolean hit = tiles[x][y].shoot();
        if(hit)
        {
            for(Ship s : ships)
            {
                Point[] shipTiles = getShipTiles(s);
                for(int i = 0; i < shipTiles.length; i++)
                {
                    if(shipTiles[i].equals(x, y))
                    {
                        if(s.hitSlot(i))
                        {
                            shootAround(s);
                        }
                    }
                }
            }
        }
        return hit;
    }

    private void shootAround(Ship s)
    {
        for(Point p : getShipTiles(s))
            shootAround(p.x, p.y);
    }

    private void shootAround(int x, int y)
    {
        for(int i = x - 1; i <= x + 1; i++)
        {
            for (int j = y - 1; j <= y + 1; j++)
            {
                if(i >= 0 && i < 10 && j >= 0 && j < 10)
                {
                    tiles[i][j].shoot();
                }
            }
        }
    }


    public Tile getTile(int x, int y)
    {
        return tiles[x][y];
    }



}
