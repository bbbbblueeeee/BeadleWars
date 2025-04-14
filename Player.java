public class Player {

    private double x,y;
    private int colorNum;

    public Player(double a,double b,int c){
        x=a;
        y=b;
        colorNum=c;
    }

    public void moveH(double n){
        x+=n;
    }

    public void moveV(double n){
        y+=n;
    }

    public void setX(double n){
        x=n;
    }

    public void setY(double n){
        y=n;
    }

    public void setColorNum(int n){
        colorNum=n;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }
}
