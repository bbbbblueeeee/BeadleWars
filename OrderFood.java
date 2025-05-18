public class OrderFood extends Quest{
    public int wantedOrder,receivedOrder;
    public boolean hasFood;
    public Player p;

    public OrderFood(Player player,int b,int o){
        p=player;
        hasFood=false;
        targetBuildingNum=b;
        wantedOrder=o;
        receivedOrder=0;
        points=350;
        questType=2;
    }

    public int getStatus(){
        if(taskComplete)
            return 2;
        else if(hasFood)
            return 1;
        else
            return 0;
    }

    public void takeOrder(int r){
        p.receiveItem(2);
        receivedOrder=r;
        hasFood=true;
    }

    public void placeOrder(){
        p.giveItem();
        taskComplete=true;
    }

    public boolean hasCorrectOrder(){
        return (wantedOrder==receivedOrder);
    }

    public void resetQuest(){
        receivedOrder=0;
        taskComplete=false;
        hasFood=false;
        p.giveItem();
    }
}
