public class Player {

    private int x,y,colorNum,rotation;

    public Player(int a,int b,int c){
        x=a;
        y=b;
        colorNum=c;
        rotation = 0;
    }

    public void lookRight(){ rotation = 90; }

    public void lookLeft(){ rotation = -90; }

    public void lookUp(){ rotation = 0; }

    public void lookDown(){ rotation = 180; }

    public void moveH(int n){
        x+=n;
    }

    public void moveV(int n){
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

    public int getRotation() {return rotation;}
}
