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
    private Image mySprite,otherSprite,map,myInsideSprite,myInsideBody,otherInsideSprite,otherInsideBody,insideMap,paper,food;
    private DrawingComponent drawingComponent;
    private Socket socket;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;
    private Rectangle[] paths,entryPoints;
    //private String myIconText,otherIconText;

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
        paper = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/paper.png"));
        food = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/food.png"));
    }

    public void setUpGUI(){
        contentPane=this.getContentPane();
        this.setTitle("Player #"+playerID);
        contentPane.setPreferredSize(new Dimension(width,height));
        createPlayer();
        drawingComponent=new DrawingComponent();
        contentPane.add(drawingComponent);
        paths[0]=new Rectangle(186,172,41,395);
        paths[1]=new Rectangle(186,185,295,43);
        paths[2]=new Rectangle(186,524,295,43);
        paths[3]=new Rectangle(438,101,43,553);
        paths[4]=new Rectangle(439,307,90,43);
        paths[5]=new Rectangle(409,360,72,43);
        paths[6]=new Rectangle(439,101,331,42);
        paths[7]=new Rectangle(438,605,243,43);
        paths[8]=new Rectangle(701,101,41,342);
        paths[9]=new Rectangle(701,244,76,41);
        paths[10]=new Rectangle(701,401,93,42);
        paths[11]=new Rectangle(639,355,103,42);
        paths[12]=new Rectangle(639,355,42,354);
        paths[13]=new Rectangle(639,668,90,41);
        entryPoints[0]= new Rectangle(186,172,41,35); //NBL
        entryPoints[1]=new Rectangle(494,307,35,41); //D-Shop
        entryPoints[2]=new Rectangle(409,360,35,43); //Pawra
        entryPoints[3]=new Rectangle(438,619,43,35); //Gomz Caf
        entryPoints[4]=new Rectangle(735,101,35,42); //SIC-A
        entryPoints[5]=new Rectangle(742,244,35,41); //SIC-B
        entryPoints[6]=new Rectangle(759,401,35,42); //SIC-C
        entryPoints[7]=new Rectangle(694,668,35,41); //Professors' Building
        //make code that assigns sprites depending on what the player and opponent chose. something with arrays
        //myIconText = "/assets/player_"+me.getColorNum()+"_"+me.direction()+".png";
        //otherIconText="/assets/player_"+other.getColorNum()+"_"+other.direction()+".png";

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
            //AffineTransform reset = g2d.getTransform();
            if(me.getCurrentBuilding()==0) {
                g2d.drawImage(map, 0, 0, null);
                mySprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/player_" + me.getColorNum() + "_" + me.direction() + ".png"));
                otherSprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/player_" + other.getColorNum() + "_" + other.direction() + ".png"));
                g2d.drawImage(mySprite, me.getX(), me.getY(), null);
                //g2d.setTransform(reset);
                g2d.drawImage(otherSprite, other.getX(), other.getY(), null);
                repaint();
            }
            else {
                insideMap=Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/interior_"+me.getCurrentBuilding()+".png"));
                g2d.drawImage(insideMap, me.getInsideMapX(), 0, null);
                if(other.getCurrentBuilding()==me.getCurrentBuilding()){
                    otherInsideSprite=Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/playerIn_"+other.getColorNum()+"_"+other.direction()+".png"));
                    g2d.drawImage(otherInsideSprite, other.getInsideX(), other.getInsideY(), null);
                }
                myInsideSprite=Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/playerIn_"+me.getColorNum()+"_"+me.direction()+".png"));
                g2d.drawImage(otherInsideSprite, me.getInsideX(), me.getInsideY(), null);
                repaint();
            }
            if (me.getItemNum() == 1) {
                g2d.drawImage(paper, 35, 629, null);
            } else if (me.getItemNum() == 2) {
                g2d.drawImage(food, 35, 629, null);
            }
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
                    if (me.getCurrentBuilding()==0) {
                        me.moveH(-speed);
                        if (!isOnPath(me.getX(), me.getY())) {
                            me.setX(me.getPrevX());
                            me.setY(me.getPrevY());
                        }
                    }
                    else
                        me.moveH(-speed-2);
                }
                if(right) {
                    if(me.getCurrentBuilding()==0) {
                        me.moveH(speed);
                        if (!isOnPath(me.getX(), me.getY())) {
                            me.setX(me.getPrevX());
                            me.setY(me.getPrevY());
                        }
                    }
                    else
                        me.moveH(speed+2);
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
                int keyCode = e.getKeyCode();

                // just testing the inventory switch, will delete
                if (keyCode == KeyEvent.VK_1) {
                    me.receiveItem(1);
                } else if (keyCode == KeyEvent.VK_2) {
                    me.receiveItem(2);
                } else if (keyCode == KeyEvent.VK_0) {
                    me.giveItem();
                }

                if (keyCode == KeyEvent.VK_ENTER) {
                    if (me.getCurrentBuilding() == 0) {
                        for (int i = 0; i < 8; i++) {
                            if (entryPoints[i].contains(me.getX(), me.getY()))
                                me.enterBuilding(playerID, i + 1);
                        }
                    } else
                        me.leaveBuilding();
                } else if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
                    if (me.getCurrentBuilding() == 0) {
                        up = true;
                        me.lookUp();
                    }
                } else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
                    if (me.getCurrentBuilding() == 0) {
                        down = true;
                        me.lookDown();
                    }
                } else if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
                    left = true;
                    me.lookLeft();
                } else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
                    right = true;
                    me.lookRight();
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
                        other.setX(dataIn.readInt());
                        other.setY(dataIn.readInt());
                        other.setInsideX(dataIn.readInt());
                        other.setInsideY(dataIn.readInt());
                        other.setInsideMapX(dataIn.readInt());
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
                        dataOut.writeInt(me.getX());
                        dataOut.writeInt(me.getY());
                        dataOut.writeInt(me.getInsideX());
                        dataOut.writeInt(me.getInsideY());
                        dataOut.writeInt(me.getInsideMapX());
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
