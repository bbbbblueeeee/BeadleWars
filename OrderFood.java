/**
 The orderfood class extends quest and implements deliver quest, It is a type of quest where the player has to
 order food from a building and deliver it to another target building.


 @author Krystal O. Lim Tiong Soon (242615)
 @author Francine Denise L. Lee (24537)
 @version 20 May 2025


 We have not discussed the Java language code in our program
 with anyone other than our instructor or the teaching assistants
 assigned to this course.


 We have not used Java language code obtained from another student,
 or any other unauthorized source, either modified or unmodified.


 If any Java language code or documentation used in our program
 was obtained from another source, such as a textbook or website,
 that has been clearly noted with a proper citation in the comments
 of our program.
 */

public class OrderFood extends Quest implements DeliverQuest{
    public int wantedOrder,receivedOrder;
    public boolean hasFood;
    public Player p;

    /**
        initializes the variables needed for this class
     **/
    public OrderFood(Player player,int b,int o){
        p=player;
        hasFood=false;
        targetBuildingNum=b;
        wantedOrder=o;
        receivedOrder=0;
        points=350;
        questType=2;
    }

    /**
        gets the current status of the ques
     @return the number corresponding to the status
     **/
    public int getStatus(){
        if(taskComplete)
            return 2;
        else if(hasFood)
            return 1;
        else
            return 0;
    }

    /**
        takes an item and sets hasfood to true
     @param // the type of food received as an int
     **/
    public void takeItem(int r){
        p.receiveItem(2);
        receivedOrder=r;
        hasFood=true;
    }

    /**
        gives the food to the target and copletes the task
     **/
    public void placeItem(){
        p.giveItem();
        taskComplete=true;
    }

    /**
        checks whether the player gave the correct order or not
     @return boolean if it was correct or not
     **/
    public boolean hasCorrectOrder(){
        return (wantedOrder==receivedOrder);
    }

    /**
        resets the quest back to when it first started
     **/
    public void resetQuest(){
        receivedOrder=0;
        taskComplete=false;
        hasFood=false;
        p.giveItem();
    }

    /**
        gets the number of the correct order
     @return the int of the correct order
     **/
    public int getWantedOrder(){
        return wantedOrder;
    }
}
