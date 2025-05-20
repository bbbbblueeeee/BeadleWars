import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Player {

    private int x,y,prevX,prevY,colorNum,direction,currentBuilding,currentStall,activeEvent,insideX,insideMapX,itemNum,points;
    private boolean startPressed;
    private ArrayList <Quest> quests;

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

    public void setDirection(int i){
        direction=i;
    }

    public int getDirection(){
        return direction;
    }

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

    public void moveV(int n){
        prevY=y;
        y+=n;
    }

    public void setX(int n){
        x=n;
    }

    public void setY(int n){
        y=n;
    }

    public void setColorNum(int n){
        colorNum=n;
    }

    public int getColorNum (){
        return colorNum;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getPrevX(){
        return prevX;
    }

    public int getPrevY() {
        return prevY;
    }

    public void setInsideX(int n){
        insideX=n;
    }

    public void setInsideMapX(int n){
        insideMapX=n;
    }

    public int getInsideX(){
        return insideX;
    }

    public int getInsideMapX(){
        return insideMapX;
    }

    public int getCurrentBuilding() {return currentBuilding;}

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

    public void setPoints(int n){
        points=n;
    }

    public void initializeQuests(){
        for(int i=0;i<3;i++) {
            int random = (int) (Math.random() * 5) + 1;
            while (findQuestType(random) != null)
                random = (int) (Math.random() * 5) + 1;
            addQuest(random);
        }
    }

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

    public int getItemNum(){return itemNum;}

    public void receiveItem (int n)
    {
        itemNum = n;
    }

    public void giveItem () {
        itemNum =0;
    }

    public int getPoints(){
        return points;
    }

    public ArrayList<Quest> getQuestList(){
        return quests;
    }

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

    public int getCurrentStall(){
        return currentStall;
    }

    public void setCurrentStall(int n){
        currentStall=n;
    }

    public Quest findQuestType(int n){
        for (Quest q : quests) {
            if(q==null)
                break;
            if (q.getQuestType() == n)
                return q;
        }
        return null;
    }
    public void setStartPressed(boolean b) {
        startPressed = b;
    }

    public boolean getStartPressed() {
        return startPressed;
    }

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

    public void setCurrentBuilding(int i){
        currentBuilding=i;
    }

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
