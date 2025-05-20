/**
 The DeliverPapers class extends quest and implements DeliverQuest. it scores the player 300 points when completed,
 and is used to deliver papers from one target to another.


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
public class DeliverPapers extends Quest implements DeliverQuest{
    public boolean hasPaper;
    public Player p;
    /**
     Initializes the variables needed for the class
     **/
    public DeliverPapers(Player player,int b){
        p=player;
        hasPaper=false;
        targetBuildingNum=b;
        points=300;
        questType=1;
    }

    /**
     gets the status of the quest at current.
     @return a number corresponding to the status
     **/
    public int getStatus(){
        if(taskComplete)
            return 2;
        else if(hasPaper)
            return 1;
        else
            return 0;
    }

    /**
     the take item method takes an int and makes the plaer receive that number of papers
     @param //the number of papers to be received
     **/
    public void takeItem(int i){
        p.receiveItem(1);
        hasPaper=true;
    }

    /**
     places the papers and completes the quest.
     **/
    public void placeItem(){
        p.giveItem();
        taskComplete=true;
    }
}
