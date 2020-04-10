package dema.battleships;

public enum GameStage {

    Player1Ships,
    PassTO,
    Player2Ships,
    Game;

    public GameStage nextStage()
    {
        return GameStage.values()[this.ordinal()+1];
    }


}
