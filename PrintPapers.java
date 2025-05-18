public class PrintPapers extends Quest{
    int wantedQuantity,enteredQuantity;
    boolean hasPapers;
    Player p;

    public PrintPapers(Player player,int b,int o){
        p=player;
        hasPapers=false;
        targetBuildingNum=b;
        wantedQuantity=o;
        enteredQuantity=0;
        points=350;
        questType=3;
    }

    public int getStatus(){
        if(taskComplete)
            return 2;
        else if(hasPapers)
            return 1;
        else
            return 0;
    }

    public void takePapers(int r){
        p.receiveItem(1);
        enteredQuantity=r;
        hasPapers=true;
    }

    public void placePapers(){
        p.giveItem();
        taskComplete=true;
    }

    public boolean hasCorrectQuantity(){
        return (wantedQuantity==enteredQuantity);
    }

    public void resetQuest(){
        enteredQuantity=0;
        taskComplete=false;
        hasPapers=false;
        p.giveItem();
    }
}
