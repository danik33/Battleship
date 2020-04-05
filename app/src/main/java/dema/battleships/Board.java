package dema.battleships;




public class Board {


    private Tile[][] tiles;

//    public static void main(String[] args)
//    {
//        Board t = new Board();
//        t.randomizeShips();
//        for(int j = 0; j < 10; j++)
//        {
//            for(int i = 0; i < 10; i++)
//            {
//                if(t.tiles[i][j].hasShip())
//                    System.out.print(1 +" ");
//                else
//                    System.out.print(0 + " ");
//            }
//            System.out.println();
//        }
//
//    }

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

    }

    public void setShip(Ship s, int x, int y)
    {
        System.out.println(s + ": (" + x + ", " + y + ")");
        for(int i = 0; i < s.getLength(); i++)
        {

            if(s.getRotation() == Rotation.HORIZONTAL)
                tiles[x + i][y].setShip();

            else
                tiles[x][y + i].setShip();

        }
    }

    public boolean canPlace(Ship s, int x, int y)  //TODO FIX HOOYNA
    {
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

    public void randomizeShips()
    {
        ShipType[] fleet = ShipType.getFleet();
        for(ShipType s : fleet)
        {
            Ship tr;
            int x,y;
            do
            {
                tr = new Ship(Rotation.random(), s);
                x = rand(0, 9);
                y = rand(0, 9);
            } while(!canPlace(tr, x, y));

            setShip(tr, x, y);
        }
    }

    public int rand(int min, int max)
    {
        return (int) (Math.random()*(max-min+1)+min);
    }

    public void shoot(int x, int y)
    {
        tiles[x][y].shoot();
    }

    public Tile getTile(int x, int y)
    {
        return tiles[x][y];
    }



}
