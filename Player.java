public class Player {

    private int x,y,prevX,prevY,colorNum,rotation;
    private boolean onMap,faceUp,faceDown,faceLeft,faceRight;

    public Player(int a,int b,int c,int d){
        x=a;
        y=b;
        prevX=a;
        prevY=b;
        colorNum=c;
        rotation = 0;
        onMap=true;
        if(d==1){
            faceUp=true;
            faceDown=false;
            faceLeft=false;
            faceRight=false;
        }
        else{
            faceUp=false;
            faceDown=true;
            faceLeft=false;
            faceRight=false;
        }
    }

    public void lookRight(){
        rotation = 90;
        if(faceUp) {
            this.setX(this.getX()+28);
            faceUp=false;
        }
        else if(faceDown) {
            this.setX(this.getX()+2);
            this.setY(this.getY()-28);
            faceDown=false;
        }
        else if (faceLeft) {
            this.setX(this.getX()+28);
            this.setY(this.getY()-26);
            faceLeft=false;
        }
        faceRight=true;
    }

    public void lookLeft(){
        rotation = -90;
        if(faceUp){
            this.setY(this.getY()+26);
            faceUp=false;
        }
        if(faceDown){
            this.setX(this.getX()-26);
            this.setY(this.getY()-2);
            faceDown=false;
        }
        if(faceRight){
            this.setX(this.getX()-28);
            this.setY(this.getY()+26);
            faceRight=false;
        }
        faceLeft=true;
    }

    public void lookUp(){
        rotation = 0;
        if(faceDown){
            this.setX(this.getX()-26);
            this.setY(this.getY()-28);
            faceDown=false;
        }
        if(faceLeft){
            this.setY(this.getY()-26);
            faceLeft=false;
        }
        if(faceRight){
            this.setX(this.getX()-28);
            faceRight=false;
        }
        faceUp=true;
    }

    public void lookDown(){
        rotation = 180;
        if(faceUp){
            this.setX(this.getX()+26);
            this.setY(this.getY()+28);
            faceUp=false;
        }
        if(faceRight){
            this.setX(this.getX()-2);
            this.setY(this.getY()+28);
            faceRight=false;
        }
        if(faceLeft){
            this.setX(this.getX()+26);
            this.setY(this.getY()+2);
            faceLeft=false;
        }
        faceDown=true;
    }

    public int direction(){
        if(faceUp)
            return 1;
        else if(faceDown)
            return 2;
        else if(faceLeft)
            return 3;
        else
            return 4;
    }

    public void moveH(int n){
        prevX=x;
        x+=n;
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

    public int getRotation() {return rotation;}

    public boolean isOnMap() {return onMap;}

    public void leaveMap(){onMap=false;}

    public void enterMap(){onMap=true;}
}
