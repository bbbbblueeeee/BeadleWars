public abstract class Quest {
    int points,questType,targetBuildingNum;
    boolean taskComplete=false;

    public boolean isCompleted(){
        return taskComplete;
    }

    public int getQuestType(){
        return questType;
    }

    public int getTargetBuildingNum(){
        return targetBuildingNum;
    }

    public abstract int getStatus();

    public void finish(Player p){
        p.finishQuest(this);
    }

    public int getPoints(){
        return points;
    }
}
