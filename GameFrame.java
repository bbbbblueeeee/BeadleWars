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
    private Image mySprite,otherSprite,map,myInsideSprite,otherInsideSprite,insideMap,invPaper,invFood,minigame;
    private DrawingComponent drawingComponent;
    private Socket socket;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;
    private Rectangle[] paths,entryPoints;
    private Quest current;

    public GameFrame(int w,int h){
        width=w;
        height=h;
        up=false;
        down=false;
        left=false;
        right=false;
        paths=new Rectangle[14];
        entryPoints=new Rectangle[13];

        map=Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/map.png"));
        invPaper = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/inv_paper.png"));
        invFood = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/inv_food.png"));
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
        entryPoints[8]=new Rectangle(1800,0,248,768); // exit for when indoors
        entryPoints[9]=new Rectangle(0,0,650,768); // minigame popups on the left
        entryPoints[10]=new Rectangle(38,0,452,768); // minigame popup for gonz red stall
        entryPoints[11]=new Rectangle(690,0,452,768); // minigame popups for gonz yellow stall
        entryPoints[12]=new Rectangle(1333,0,452,768); // minigame popups for gonz blue stall
        //make code that assigns sprites depending on what the player and opponent chose. something with arrays

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
                mySprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/player_" + me.getColorNum() + "_" + me.getDirection() + ".png"));
                otherSprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/player_" + other.getColorNum() + "_" + other.getDirection() + ".png"));
                g2d.drawImage(mySprite, me.getX(), me.getY(), null);
                g2d.drawImage(otherSprite, other.getX(), other.getY(), null);
                repaint();
            }
            else {
                insideMap=Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/interior_"+me.getCurrentBuilding()+".png"));
                g2d.drawImage(insideMap, me.getInsideMapX(), 0, null);
                if(me.getCurrentBuilding()==8){
                    Quest dp=null;
                    for(Quest q : me.getQuestList()){
                        if(q.getQuestType()==1) {
                            dp=q;
                            break;
                        }
                    }
                    if(dp!=null){
                        if(((DeliverPapers) dp).getStatus()==0) {
                            g2d.drawImage(invPaper, me.getInsideMapX() + 34, 500, null);
                            System.out.println("drawing papers");
                        }
                    }
                }
                if(me.getCurrentBuilding()!=0&&me.getItemNum()!=0){
                    for(Quest quest : me.getQuestList()){
                        if(quest.getTargetBuildingNum()==me.getCurrentBuilding()) {
                            if(quest.getQuestType()==1) {
                                if (((DeliverPapers) quest).getStatus() == 1) {
                                    current = quest;
                                    break;
                                }
                            }
                        }
                    }
                }
                if(current!=null){
                    if(current.getQuestType()==1) {
                        if (((DeliverPapers) current).getStatus() == 2) {
                            g2d.drawImage(invPaper, me.getInsideMapX() + 34, 500, null);
                            System.out.println("drawing papers");
                        }
                    }
                }
                if(other.getCurrentBuilding()==me.getCurrentBuilding()){
                    otherInsideSprite=Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/playerIn_"+other.getColorNum()+"_"+other.getDirection()+"_1.png"));
                    g2d.drawImage(otherInsideSprite, other.getInsideX(), 471, null);
                }
                myInsideSprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/playerIn_" + me.getColorNum() + "_" + me.getDirection() + "_1.png"));
                g2d.drawImage(myInsideSprite, me.getInsideX(), 471, null);
                repaint();
            }
            if (me.getItemNum() == 1) {
                g2d.drawImage(invPaper, 35, 629, null);
            } else if (me.getItemNum() == 2) {
                g2d.drawImage(invFood, 35, 629, null);
            }

            for (int i = 9; i <= 12; i++) {
                if (entryPoints[i].contains(me.getGlobalInsideX(), 520)) {
                    if (minigame != null)
                        g2d.drawImage(minigame, 0, 0, null);
                }
            }

        }
    }

    private void createPlayer(){
        if (playerID ==1){
            me=new Player(445,108,7,2);
            other=new Player(445,626,3,1);
        }
        else{
            other=new Player(445,108,7,2);
            me=new Player(445,626,3,1);
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
                        me.moveH(-speed-3);
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
                        me.moveH(speed+3);
                }

                drawingComponent.repaint();
                //System.out.println(me.getX()+","+me.getY());
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
                    int g=(int)(Math.random()*3+5);
                    me.getQuestList().add(new DeliverPapers(me,g));
                    System.out.println("assigned deliver papers to building#"+g);
                } else if (keyCode == KeyEvent.VK_2) {
                    me.receiveItem(2);
                } else if (keyCode == KeyEvent.VK_0) {
                    me.giveItem();
                }

                if(keyCode==KeyEvent.VK_I){
                    if(me.getInsideMapX()==0&&me.getInsideX()<230) {
                        if (me.getItemNum() == 0) {
                            for (Quest q : me.getQuestList()) {
                                if (q.getQuestType() == 1) {
                                    ((DeliverPapers) q).takePaper();
                                    System.out.println("took papers");
                                    break;
                                }
                            }
                        }
                        else{
                            for (Quest q : me.getQuestList()) {
                                if (q.getQuestType() == 1 && q.getTargetBuildingNum()==me.getCurrentBuilding()) {
                                    ((DeliverPapers) q).placePaper();
                                    System.out.println("placed papers");
                                    break;
                                }
                            }
                        }
                    }
                }

                if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_Z) {
                    if (me.getCurrentBuilding() == 0) {
                        for (int i = 0; i < 8; i++) {
                            if (entryPoints[i].contains(me.getX(), me.getY()))
                                me.enterBuilding(playerID, i + 1);
                        }
                    } else if (me.getCurrentBuilding() != 0 && me.getCurrentBuilding() < 9)
                    {
                        if (entryPoints[8].contains(me.getGlobalInsideX(), 550))
                            me.leaveBuilding();
                        else if (me.getCurrentBuilding() == 4) {
                            for (int i = 1; i < 4; i++) {
                                if (entryPoints[9 + i].contains(me.getGlobalInsideX(), 550)) {
                                    minigame = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/menu_" + i + ".png"));
                                }
                            }
                        } else if (entryPoints[9].contains(me.getGlobalInsideX(), 550)) {
                            // if player is in the sic buildings
                            if (me.getCurrentBuilding() == 5 || me.getCurrentBuilding() == 6 || me.getCurrentBuilding() == 7)
                                minigame = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/whiteboard.png"));
                            else
                                minigame = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/minigame_" + me.getCurrentBuilding() + ".png"));
                        }
                     }

                }
                else if (keyCode == KeyEvent.VK_X) {
                    if (minigame != null) {
                        minigame = null;
                        drawingComponent.repaint();
                        System.out.println("Minigame closed");
                    }
                }
                else if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
                    if (me.getCurrentBuilding() == 0) {
                        up = true;
                        me.setDirection(1);
                    }
                } else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
                    if (me.getCurrentBuilding() == 0) {
                        down = true;
                        me.setDirection(2);
                    }
                } else if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
                    left = true;
                    me.setDirection(3);
                } else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
                    right = true;
                    me.setDirection(4);
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
                        other.setDirection(dataIn.readInt());
                        other.setInsideX(dataIn.readInt());
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
                        dataOut.writeInt(me.getDirection());
                        dataOut.writeInt(me.getInsideX());
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
