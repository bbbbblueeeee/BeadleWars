import java.util.ArrayList;

public class Player {

    private int x,y,prevX,prevY,colorNum,direction,currentBuilding,insideX,insideMapX,itemNum,points;
    private ArrayList <Quest> quests;

    public Player(int a,int b,int c,int d){
        x=a;
        y=b;
        prevX=a;
        prevY=b;
        colorNum=c;
        currentBuilding = 0;
        points=0;
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
            setY(172);
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
            setX(444);
            setY(626);
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
                setX(547);
                setY(280);
            }
            else {
                setX(547);
                setY(339);
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
                setX(314);
                setY(676);
            }
            else {
                setX(373);
                setY(676);
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

    public void addPoints(int num){
        points+=num;
    }

    public int getPoints(){
        return points;
    }

    public ArrayList<Quest> getQuestList(){
        return quests;
    }

}
