public class DeliverPapers extends Quest {
    boolean hasPaper;
    Player p;

    public DeliverPapers(Player player,int b){
        p=player;
        hasPaper=false;
        targetBuildingNum=b;
        points=300;
        questType=1;
    }

    public int getStatus(){
        if(taskComplete)
            return 2;
        else if(hasPaper)
            return 1;
        else
            return 0;
    }

    public void takePaper(){
        p.receiveItem(1);
        hasPaper=true;
    }

    public void placePaper(){
        p.giveItem();
        taskComplete=true;
    }
}
