/**
 The Player class handles everything a player can do, from moving, to quest handling, to checking for out of bounds positions, and points


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
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Player {

    private int x,y,prevX,prevY,colorNum,direction,currentBuilding,currentStall,activeEvent,insideX,insideMapX,itemNum,points;
    private boolean startPressed;
    private ArrayList <Quest> quests;

    /**
        initializes the variables needed for this class
     **/
    public Player(int a,int b,int c,int d){
        x=a;
        y=b;
        prevX=a;
        prevY=b;
        colorNum=c;
        currentBuilding = 0;
        currentStall=0;
        points=0;
        startPressed = false;
        quests=new ArrayList<>();
        if(d==1)
            direction=1;
        else
            direction=2;
    }

    /**
        sets direction to an integer
     @param //the directoinal value assigned to the int
     **/
    public void setDirection(int i){
        direction=i;
    }
    /**
        gets the direction of the player
     @return an int for the direction
     **/
    public int getDirection(){
        return direction;
    }
    /**
        moves the player horizontally and checking whether the player is on the map or in a building
     **/
    public void moveH(int n){
        if(currentBuilding==0) {
            prevX = x;
            x += n;
        }
        else if(n<0){
            if(insideX>462) {
                insideX += n;
                if(insideX>924)
                    insideX=924;
            }
            else if(insideMapX<0){
                insideMapX-=n;
                if(insideMapX>0)
                    insideMapX=0;
            }
            else {
                insideX+=n;
                if(insideX<0)
                    insideX=0;
            }
        }
        else{
            if(insideX<=462) {
                insideX+=n;
                if(insideX<0)
                    insideX=0;
            }
            else if(insideMapX>-1024){
                insideMapX-=n;
                if(insideMapX<-1024)
                    insideMapX=-1024;
            }
            else{
                insideX += n;
                if(insideX>924)
                    insideX=924;
            }
        }
    }

    /**
        moves the player vertically.
     @param //the number to be added to the player's y coordinate
     **/
    public void moveV(int n){
        prevY=y;
        y+=n;
    }
    /**
sets the players x coordinate
     @param //the number to be set
     **/
    public void setX(int n){
        x=n;
    }

    /**
    sets the players y coordinate
     @param //the number to be set as the y coordinate
     **/
    public void setY(int n){
        y=n;
    }

    /**

     sets the color
     @param //the numerical variable assigned to the color, to determind the color
     **/
    public void setColorNum(int n){
        colorNum=n;
    }

    /**

     gets the number assigned to a certain color
     @return the number of the color
     **/
    public int getColorNum (){
        return colorNum;
    }
    /**
    get player's x coordinate
     @return player's x coordinate
     **/
    public int getX(){
        return x;
    }

    /**
     get player's y coordinate
     @return player's y coordinate
     **/
    public int getY(){
        return y;
    }
    /**
     get player's previous x coordinate
     @return player's previous x coordinate
     **/
    public int getPrevX(){
        return prevX;
    }

    /**
     get player's previous y coordinate
     @return player's previous y coordinate
     **/
    public int getPrevY() {
        return prevY;
    }

    /**
     set player's x coordinate inside a building
     @param //the number the coordinate is to be set as
     **/
    public void setInsideX(int n){
        insideX=n;
    }

    /**
     set map's x coordinate inside a building
     @param //the number the coordinate is to be set as
     **/
    public void setInsideMapX(int n){
        insideMapX=n;
    }

    /**
     get player's x coordinate inside a building
     @return //the number of the x coordinate
     **/
    public int getInsideX(){
        return insideX;
    }

    /**
     get map's x coordinate inside a building
     @return the map's x coordinate
     **/
    public int getInsideMapX(){
        return insideMapX;
    }

    /**
     gets the current building
     @return int corresponsinf to each building
     **/
    public int getCurrentBuilding() {return currentBuilding;}

    /**
        sets the coordinates od the player when exiting the building to not go out of bounds
     **/
    public void leaveBuilding(){
        if (getCurrentBuilding()==1) {
            setDirection(2);
            setX(191);
            setY(175);
        }
        else if(getCurrentBuilding()==2) {
            setDirection(3);
            setX(501);
            setY(316);
        }
        else if (getCurrentBuilding()==3) {
            setDirection(4);
            setX(409);
            setY(367);
        }
        else if(getCurrentBuilding()==4) {
            setDirection(1);
            setX(443);
            setY(625);
        }
        else if(getCurrentBuilding()==5){
            setDirection(3);
            setX(742);
            setY(107);
        }
        else if (getCurrentBuilding()==6){
            setDirection(3);
            setX(749);
            setY(249);
        }
        else if (getCurrentBuilding()==7) {
            setDirection(3);
            setX(766);
            setY(407);
        }
        else {
            setDirection(3);
            setX(701);
            setY(668);
        }
        currentBuilding=0;
    }

    /**
        sets the player's point to a certain number
     @param //the number to be set as points
     **/
    public void setPoints(int n){
        points=n;
    }

    /**
        randomizes the assigning of quests to a player
     **/
    public void initializeQuests(){
        for(int i=0;i<3;i++) {
            int random = (int) (Math.random() * 5) + 1;
            while (findQuestType(random) != null)
                random = (int) (Math.random() * 5) + 1;
            addQuest(random);
        }
    }

    /**
        sets the x and y coordinates of a player when entering certain buildings so the player isn't out of bounds
     **/
    public void enterBuilding(int playerNum,int bldgNum){
        insideX=924;
        setDirection(3);
        insideMapX=-1024;
        if (bldgNum==1){
            if(playerNum==1){
                setX(200);
                setY(93);
            }
            else {
                setX(259);
                setY(93);
            }
        }
        else if(bldgNum==2){
            if(playerNum==1){
                setX(564);
                setY(257);
            }
            else {
                setX(564);
                setY(373);
            }
        }
        else if(bldgNum==3){
            if(playerNum==1){
                setX(277);
                setY(347);
            }
            else {
                setX(336);
                setY(347);
            }
        }
        else if(bldgNum==4){
            if(playerNum==1){
                setX(240);
                setY(675);
            }
            else {
                setX(299);
                setY(675);
            }
        }
        else if(bldgNum==5){
            if(playerNum==1){
                setX(858);
                setY(109);
            }
            else {
                setX(917);
                setY(109);
            }
        }
        else if(bldgNum==6){
            if(playerNum==1){
                setX(829);
                setY(244);
            }
            else {
                setX(888);
                setY(244);
            }
        }
        else if(bldgNum==7){
            if(playerNum==1){
                setX(838);
                setY(418);
            }
            else {
                setX(897);
                setY(418);
            }
        }
        else{
            if(playerNum==1){
                setX(739);
                setY(583);
            }
            else {
                setX(739);
                setY(642);
            }
        }
        currentBuilding=bldgNum;
    }

    /**
    gets the item number
     @return the item number
     **/
    public int getItemNum(){return itemNum;}

    /**
        receives an item
     @param //the number of the item to be added
     **/
    public void receiveItem (int n)
    {
        itemNum = n;
    }

    /**
    gives an item away and sets it to 0
     **/
    public void giveItem () {
        itemNum =0;
    }

    /**
        get the player's points
     @return the player's points
     **/
    public int getPoints(){
        return points;
    }

    /**
        gets a random type of quests from an arraylist
     @return arraylist quests

     **/
    public ArrayList<Quest> getQuestList(){
        return quests;
    }

    /**
        finishes a quest and marks it as completed and adds points.
     @param //the quest to be marked as complete
     **/
    public void finishQuest(Quest q){
        for(int i=0;i<quests.size();i++){
            if(quests.get(i).getQuestType()==q.getQuestType()){
                for(int j=i;j<quests.size()-1;j++)
                    quests.set(j,quests.get(j+1));
                quests.set(quests.size()-1,null);
                System.out.println("removed a quest");
                break;
            }
        }
        points+=q.getPoints();
    }

    /**
        gets the current stall in the cafeteria
     @return int of current stall
     **/
    public int getCurrentStall(){
        return currentStall;
    }

    /**
        sets the current stall to a number
     @param //the stall tobe set as current
     **/
    public void setCurrentStall(int n){
        currentStall=n;
    }

    /**
        finds a quest of a certain type
     @return a quests of type n
     **/
    public Quest findQuestType(int n){
        for (Quest q : quests) {
            if(q==null)
                break;
            if (q.getQuestType() == n)
                return q;
        }
        return null;
    }

    /**
        sets startpressed to a boolean
     @param // the boolean to be set
     **/
    public void setStartPressed(boolean b) {
        startPressed = b;
    }

    /**
        checks if the player has pressed start
     @return boolean if the player has clicked it or not
     **/
    public boolean getStartPressed() {
        return startPressed;
    }

    /**
        gets the current quest of the player
     @return quest type after looking through the player's quest list
     **/
    public Quest getCurrentQuest(){
        if (getItemNum() != 0) {
            //look through the player's quest list for DeliverPapers, OrderFood, and PrintPapers quests
            for (int i = 1; i <= 3; i++) {
                //if the target building of an existing quest is the same as the player's current building and the quest is in the phase where the item has been taken: set that quest as current and break out of the loop
                if (findQuestType(i) != null && findQuestType(i).getStatus() == 1 && findQuestType(i).getTargetBuildingNum() == getCurrentBuilding()) {
                    return findQuestType(i);
                }
            }
        }
        return null;
    }

    /**
        Looks through the player's quest list for DeliverPapers, OrderFood, and PrintPapers quests, and
     if the target building of a completed quest is the same as the player's current building: set that quest as completed
     and break out of the loop
    @return quest or null
     **/
    public Quest getCompletedQuest(){
        //look through the player's quest list for DeliverPapers, OrderFood, and PrintPapers quests
        for (int i = 1; i <= 3; i++) {
            //if the target building of a completed quest is the same as the player's current building: set that quest as completed and break out of the loop
            if (findQuestType(i) != null && findQuestType(i).getStatus() == 2) {
                return findQuestType(i);
            }
        }
        return null;
    }

    /**
     sets the current buildng to a number
     @param //the number the building is to be set as
     **/
    public void setCurrentBuilding(int i){
        currentBuilding=i;
    }

    /**
        adds a certain quest type to the player's list
     @param //the type of quest to be added
     **/
    public void addQuest(int t){
        if (t == 1) {
            int i=(int)(Math.random()*3+5);
            quests.add(new DeliverPapers(this,i));
            System.out.println("assigned deliver papers to building#"+i);
        } else if (t == 2) {
            int i=(int)(Math.random()*4+5);
            int o=(int)(Math.random()*9+1);
            quests.add(new OrderFood(this,i,o));
            System.out.println("assigned deliver order#"+o+" to building#"+i);
        } else if (t == 3) {
            int i=(int)(Math.random()*4+5);
            int q=(int)(Math.random()*25+5);
            quests.add(new PrintPapers(this,i,q));
            System.out.println("assigned deliver "+q+" papers to building#"+i);
        } else if(t==4){
            quests.add(new SendEmail(this,0));
            System.out.println("assigned send email#"+0);
        } else if(t==5){
            int p=(int)(Math.random()*11+10);
            quests.add(new PictureCats(this,p));
            System.out.println("assigned picture cats "+p+" times and then send email");
        }
    }

}
