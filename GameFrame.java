import org.w3c.dom.css.Rect;

import javax.print.attribute.standard.DialogOwner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.*;

public class GameFrame extends JFrame {

    private int width,height,playerID;
    private Container contentPane;
    private Player me,other;
    private Timer animationTimer;
    private boolean up,down,left,right;
    private Image mySprite,otherSprite,map;
    private DrawingComponent drawingComponent;
    private Socket socket;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;
    private Rectangle[] paths,entryPoints;
    private String myIconText, otherIconText;

    public GameFrame(int w,int h){
        width=w;
        height=h;
        up=false;
        down=false;
        left=false;
        right=false;
        paths=new Rectangle[14];
        entryPoints=new Rectangle[8];

        map=Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/map.png"));
    }

    public void setUpGUI(){
        contentPane=this.getContentPane();
        this.setTitle("Player #"+playerID);
        contentPane.setPreferredSize(new Dimension(width,height));
        createPlayer();
        drawingComponent=new DrawingComponent();
        contentPane.add(drawingComponent);
        paths[0]=new Rectangle(191,172,31,390);
        paths[1]=new Rectangle(191,190,285,31);
        paths[2]=new Rectangle(191,528,285,34);
        paths[3]=new Rectangle(444,107,32,547);
        paths[4]=new Rectangle(444,316,85,32);
        paths[5]=new Rectangle(409,367,67,32);
        paths[6]=new Rectangle(444,107,326,31);
        paths[7]=new Rectangle(444,611,233,31);
        paths[8]=new Rectangle(703,107,34,330);
        paths[9]=new Rectangle(703,249,74,32);
        paths[10]=new Rectangle(703,407,91,30);
        paths[11]=new Rectangle(644,361,93,31);
        paths[12]=new Rectangle(644,361,33,338);
        paths[13]=new Rectangle(644,668,85,31);
        entryPoints[0]= new Rectangle(191,172,31,35); //NBL
        entryPoints[1]=new Rectangle(494,316,35,32); //D-Shop
        entryPoints[2]=new Rectangle(409,367,35,32); //Pawra
        entryPoints[3]=new Rectangle(444,619,32,35); //Gomz Caf
        entryPoints[4]=new Rectangle(735,107,35,31); //SIC-A
        entryPoints[5]=new Rectangle(742,249,35,32); //SIC-B
        entryPoints[6]=new Rectangle(759,407,35,30); //SIC-C
        entryPoints[7]=new Rectangle(694,668,35,31); //Professors' Building
        //make code that assigns sprites depending on what the player and opponent chose. something with arrays
        myIconText = "/assets/player_"+me.getColorNum()+"_"+me.direction()+".png";
        otherIconText="/assets/player_"+other.getColorNum()+"_"+other.direction()+".png";

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setUpTimer();
        this.setUpKeyListener();
    }

    private void connectToServer(){
        try {
            // change once this gets tested on other devices i think
           socket = new Socket("localhost", 11037);
           DataInputStream in = new DataInputStream(socket.getInputStream());
           DataOutputStream out = new DataOutputStream(socket.getOutputStream());
           playerID = in.readInt();
            System.out.println("You are player#"+playerID);
            if(playerID ==1)
                System.out.println("Waiting for player#2 to connect...");
            rfsRunnable = new ReadFromServer(in);
            wtsRunnable = new WriteToServer(out);
            rfsRunnable.waitForStartMsg();
        }
        catch (IOException ex) {
            System.out.println("IOException from GameServer constructor");
        }
    }

    private class DrawingComponent extends JComponent{
        protected void paintComponent(Graphics graphics){
            Graphics2D g2d=(Graphics2D) graphics;
            AffineTransform reset = g2d.getTransform();
            g2d.drawImage(map,0,0,null);
            myIconText = "/assets/player_"+me.getColorNum()+"_"+me.direction()+".png";
            otherIconText="/assets/player_"+other.getColorNum()+"_"+other.direction()+".png";
            mySprite=Toolkit.getDefaultToolkit().getImage(getClass().getResource(myIconText));
            otherSprite=Toolkit.getDefaultToolkit().getImage(getClass().getResource(otherIconText));
            //g2d.rotate(Math.toRadians(me.getRotation()), me.getX(), me.getY());
            g2d.drawImage(mySprite,me.getX(),me.getY(),null);
            g2d.setTransform(reset);
            g2d.drawImage(otherSprite,other.getX(), other.getY(),null);
        }
    }

    private void createPlayer(){
        if (playerID ==1){
            me=new Player(445,108,1,2);
            other=new Player(445,626,5,1);
        }
        else{
            other=new Player(445,108,1,2);
            me=new Player(445,626,5,1);
        }
    }

    private boolean isOnPath(int x,int y){
        for (Rectangle path : paths){
            if(path.contains(x,y))
                return true;
        }
        return false;
    }

    private void setUpTimer(){
        int interval=10;
        ActionListener actionListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int speed=3;
                if (up) {
                    me.moveV(-speed);
                    if (!isOnPath(me.getX(),me.getY())){
                        me.setX(me.getPrevX());
                        me.setY(me.getPrevY());
                    }
                }
                if (down) {
                    me.moveV(speed);
                    if (!isOnPath(me.getX(), me.getY())) {
                        me.setX(me.getPrevX());
                        me.setY(me.getPrevY());
                    }
                }
                if (left) {
                    me.moveH(-speed);
                    if (!isOnPath(me.getX(), me.getY())) {
                        me.setX(me.getPrevX());
                        me.setY(me.getPrevY());
                    }
                }
                if(right) {
                    me.moveH(speed);
                    if (!isOnPath(me.getX(), me.getY())) {
                        me.setX(me.getPrevX());
                        me.setY(me.getPrevY());
                    }
                }

                drawingComponent.repaint();
                System.out.println(me.getX()+","+me.getY());
            }
        };
        animationTimer=new Timer(interval,actionListener);
        animationTimer.start();
    }


    private void setUpKeyListener(){
        KeyListener keyListener=new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode=e.getKeyCode();

                if(keyCode==KeyEvent.VK_ENTER){
                    if (me.getCurrentBuilding()==0){
                        for(int i=0;i<8;i++){
                            if(entryPoints[i].contains(me.getX(),me.getY()))
                                me.enterBuilding(playerID,i+1);
                        }
                    }
                    else
                        me.leaveBuilding();
                }
                else if (keyCode==KeyEvent.VK_UP || keyCode==KeyEvent.VK_W) {
                    if(me.getCurrentBuilding()==0) {
                        up = true;
                        me.lookUp();
                    }
                }
                else if (keyCode==KeyEvent.VK_DOWN || keyCode==KeyEvent.VK_S) {
                    if(me.getCurrentBuilding()==0) {
                        down = true;
                        me.lookDown();
                    }
                }
                else if(keyCode==KeyEvent.VK_LEFT || keyCode==KeyEvent.VK_A) {
                    if(me.getCurrentBuilding()==0) {
                        left = true;
                        me.lookLeft();
                    }
                }
                else if(keyCode==KeyEvent.VK_RIGHT ||keyCode==KeyEvent.VK_D) {
                    if(me.getCurrentBuilding()==0) {
                        right = true;
                        me.lookRight();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode=e.getKeyCode();

                if (keyCode==KeyEvent.VK_UP || keyCode==KeyEvent.VK_W)
                    up=false;
                else if (keyCode==KeyEvent.VK_DOWN || keyCode==KeyEvent.VK_S)
                    down=false;
                else if(keyCode==KeyEvent.VK_LEFT || keyCode==KeyEvent.VK_A)
                    left=false;
                else if(keyCode==KeyEvent.VK_RIGHT ||keyCode==KeyEvent.VK_D)
                    right=false;
            }
        };
        this.addKeyListener(keyListener);
        contentPane.setFocusable(true);
    }

    private class ReadFromServer implements Runnable{
        private DataInputStream dataIn;

        public ReadFromServer (DataInputStream in){
            dataIn = in;
            System.out.println("ReadFromServer runnable created");
        }
        public void run(){
            try{
                while (true){
                    if (other != null)
                    {
                        other.setX((int) dataIn.readDouble());
                        other.setY((int) dataIn.readDouble());
                    }
                }

            } catch (IOException ex) {
                System.out.println("IOEx from RFC run()");
            }
        }

        // for player's gui to start at the same time only when both players are connected successfully

        public void waitForStartMsg(){
            try{
                String startMsg = dataIn.readUTF();
                System.out.println("Message from server: "+ startMsg);
                Thread readThread = new Thread(rfsRunnable);
                Thread writeThread = new Thread(wtsRunnable);
                readThread.start();
                writeThread.start();
            }catch(IOException ex){
                System.out.println("IOExceptoin from waitForStartMsg");
            }
        }
    }

    private class WriteToServer implements Runnable{
        private DataOutputStream dataOut;

        public WriteToServer (DataOutputStream out){
            dataOut = out;
            System.out.println("WriteToServer runnable created");
        }
        public void run(){
            try{
                while (true){
                    if (me!=null)
                    {
                        dataOut.writeDouble(me.getX());
                        dataOut.writeDouble(me.getY());
                        dataOut.flush();
                    }
                    try{
                        Thread.sleep(25);
                    }catch (InterruptedException ex){
                        System.out.println("InterruptedException from WTS run()");
                    }
                }

            }catch (IOException ex){
                System.out.println("IOException from WTS run()");
            }

        }
    }
    public static void main(String[] args) {
        GameFrame gameFrame=new GameFrame(1024,768);
        gameFrame.connectToServer();
        gameFrame.setUpGUI();
    }
}
