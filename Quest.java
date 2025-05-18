public abstract class Quest {
    int points,questType,targetBuildingNum;
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

    public int getTargetBuildingNum(){
        return targetBuildingNum;
    }
}
