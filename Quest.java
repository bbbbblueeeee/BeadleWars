abstract class Quest {
    int points,questType;
    boolean taskComplete=false;

    public boolean isCompleted(){
        return taskComplete;
    }

    public int getPoints(){
        return points;
    }

    public int getQuestType(){
        return questType;
    }
}
