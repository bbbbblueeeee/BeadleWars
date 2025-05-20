/**
 The abstract class quest serves as a blueprint for other quest class types.
 It has methods that other class quest types can use


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
public abstract class Quest {
    int points,questType,targetBuildingNum;
    boolean taskComplete=false;

    /**
        checks if the task has been completed
     @return a boolean if the task is done or not
     **/
    public boolean isCompleted(){
        return taskComplete;
    }

    /**
        gets the quest type
     @return int of the quest type
     **/
    public int getQuestType(){
        return questType;
    }

    /**
        gets target building the quest is in
     @return the number of the building
     **/
    public int getTargetBuildingNum(){
        return targetBuildingNum;
    }

    /**
        gets the status of the quest
     **/
    public abstract int getStatus();

    /**
    finishes the quest and marks it as complete
     @param //the player to finish the test
     **/
    public void finish(Player p){
        p.finishQuest(this);
    }

    /**
        gets the amount of points the quest gives after completion
     @return the number of points
     **/
    public int getPoints(){
        return points;
    }
}
