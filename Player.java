public class Player {

    private int x,y,colorNum;

    public Player(int a,int b,int c){
        x=a;
        y=b;
        colorNum=c;
    }

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
}
