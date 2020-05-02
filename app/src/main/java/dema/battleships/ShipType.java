package dema.battleships;

import java.util.ArrayList;

public enum ShipType {
    CELL1, //UNUSED
    CELL2,
    CELL3,
    CELL4,
    CELL5;




    public static ArrayList<ShipType> getClearFleet()
    {
        return new ArrayList<>();
    }

    public static ArrayList<ShipType> getFleetV2()
    {
        ArrayList<ShipType> arr = new ArrayList<>();
        arr.add(CELL5);
        arr.add(CELL4);
        arr.add(CELL4);
        arr.add(CELL3);
        arr.add(CELL3);
        arr.add(CELL3);
        arr.add(CELL2);
        arr.add(CELL2);
        arr.add(CELL2);
        arr.add(CELL2);

        return arr;
    }


}