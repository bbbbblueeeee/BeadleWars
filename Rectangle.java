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

    public boolean contains(int x,int y,int width,int height){
        return (x>this.x || x==this.x) && (y>this.y || y==this.y) && (x+width<this.x+this.width || x+width==this.x+this.width) && (y+height<this.y+this.height || y+height==this.y+this.height);
        //return x>this.x && y>this.y && x<this.x+this.width && y<this.y+this.height;
    }
}
