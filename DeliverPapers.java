public class DeliverPapers extends Quest {
    int targetBuildingNum;
    boolean hasPaper;
    Player p;

    public DeliverPapers(Player player){
        p=player;
        hasPaper=false;
        points=300;
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
