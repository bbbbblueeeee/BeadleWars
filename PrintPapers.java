import java.awt.event.KeyEvent;

public class PrintPapers extends Quest{
    public int wantedQuantity;
    public boolean hasPapers;
    public String quantity;
    public Player p;

    public PrintPapers(Player player,int b,int o){
        p=player;
        hasPapers=false;
        targetBuildingNum=b;
        wantedQuantity=o;
        quantity="";
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

    public void takePapers(){
        p.receiveItem(1);
        hasPapers=true;
    }

    public void placePapers(){
        p.giveItem();
        taskComplete=true;
    }

    public boolean hasCorrectQuantity(){
        return (wantedQuantity==Integer.valueOf(quantity));
    }

    public void resetQuest(){
        quantity="";
        taskComplete=false;
        hasPapers=false;
        p.giveItem();
    }

    public void editQuantity(String s){
        quantity+=s;
    }

    public String getQuantity(){
        return quantity;
    }

}
