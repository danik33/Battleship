package dema.battleships;

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


}