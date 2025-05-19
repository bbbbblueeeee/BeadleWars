public class Rectangle {
    int x;
    int y;
    int width;
    int height;

    public Rectangle (int x,int y,int width,int height){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
    }

    public boolean hasPlayer(int x,int y){
        return (x>this.x || x==this.x) && (y>this.y || y==this.y) && (x+28<this.x+this.width || x+28==this.x+this.width) && (y+28<this.y+this.height || y+28==this.y+this.height);
    }

    public boolean contains(int x,int y){
        return x>=this.x&&y-20>=this.y&&x<=this.x+this.width&&y-20<=this.y+this.height;
    }
}
