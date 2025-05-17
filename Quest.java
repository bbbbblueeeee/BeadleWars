abstract class Quest {
    int points;

    public abstract boolean isActive(int currentBldgNum);

    public int getPoints(){
        return points;
    }
}
