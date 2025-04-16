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
    private Image cap1,cap2,cap3,cap4,cap5,mySprite,otherSprite,map;
    private DrawingComponent drawingComponent;
    private Socket socket;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;

    public GameFrame(int w,int h){
        width=w;
        height=h;
        up=false;
        down=false;
        left=false;
        right=false;

        cap1=Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/cap_1.png"));
        cap5=Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/cap_5.png"));
        map=Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/map.png"));
    }

    public void setUpGUI(){
        contentPane=this.getContentPane();
        this.setTitle("Player #"+playerID);
        contentPane.setPreferredSize(new Dimension(width,height));
        createPlayer();
        drawingComponent=new DrawingComponent();
        contentPane.add(drawingComponent);
        //make code that assigns sprites depending on what the player and opponent chose. something with arrays
        if (playerID == 1) {
            mySprite = cap1;
            otherSprite = cap5;
        } else {
            mySprite = cap5;
            otherSprite = cap1;
        }
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
            g2d.rotate(Math.toRadians(me.getRotation()), me.getX()+25, me.getY()+25);
            g2d.drawImage(mySprite,me.getX(),me.getY(),null);
            g2d.setTransform(reset);
            g2d.drawImage(otherSprite,other.getX(), other.getY(),null);
        }
    }

    private void createPlayer(){
        if (playerID ==1){
            me=new Player(100,400,1);
            other=new Player(400,400,5);
        }
        else{
            other=new Player(100,400,1);
            me=new Player(400,400,5);
        }
    }

    private void setUpTimer(){
        int interval=10;
        ActionListener actionListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int speed=5;
                if (up) {
                    me.moveV(-speed);
                }
                if (down) {
                    me.moveV(speed);
                }
                if (left) {
                    me.moveH(-speed);
                }
                if(right){
                    me.moveH(speed);
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

                if (keyCode==KeyEvent.VK_UP || keyCode==KeyEvent.VK_W) {
                    up = true;
                    me.lookUp();
                }
                else if (keyCode==KeyEvent.VK_DOWN || keyCode==KeyEvent.VK_S) {
                    down = true;
                    me.lookDown();
                }
                else if(keyCode==KeyEvent.VK_LEFT || keyCode==KeyEvent.VK_A) {
                    left = true;
                    me.lookLeft();
                }
                else if(keyCode==KeyEvent.VK_RIGHT ||keyCode==KeyEvent.VK_D) {
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
