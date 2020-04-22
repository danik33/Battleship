package dema.battleships;

import java.util.ArrayList;

public enum ShipType {
    CELL1,
    CELL2,
    CELL3,
    CELL4,
    CELL5;




    public static ShipType[] getFleet()
    {
        return new ShipType[]{CELL5, CELL4, CELL3, CELL2, CELL2, CELL1, CELL1};
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