package dema.battleships;

public enum GameStage {

    Player1Ships,
    PassTO,
    Player2Ships,
    Game,
    PostGame;

    public GameStage nextStage()
    {
        if(this == PostGame)
            return Player1Ships;
        return GameStage.values()[this.ordinal()+1];
    }


}
