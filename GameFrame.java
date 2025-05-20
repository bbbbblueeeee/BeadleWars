import com.sun.jdi.event.ExceptionEvent;
import org.w3c.dom.css.Rect;

import javax.print.attribute.standard.DialogOwner;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.*;
import java.util.TimerTask;

public class GameFrame extends JFrame implements MouseListener {

    private int width,height,playerID,otherBuilding;
    private Container contentPane;
    private Player me,other;
    private Timer animationTimer,clockTimer;
    private boolean up,down,left,right;
    private Image mySprite,otherSprite,map,myInsideSprite,otherInsideSprite,insideMap,invPaper,invFood,minigame,menu,itemOnMap;
    private DrawingComponent drawingComponent;
    private Socket socket;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;
    private Rectangle[] paths,entryPoints,menuOptions, charaSelect;
    private Quest current;
    private Font customFont;
    private String stall;

    private int currentHour = 7;
    private int currentMinute = 50;
    private boolean showEndScreen = false;
    private Image endScreen = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/endscreen.png"));
    private Image titleScreen = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/title.png"));
    private boolean showTitleScreen = true;

    private File file;
    private AudioInputStream audioStream;
    private Clip clip;

    public GameFrame(int w,int h) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        width=w;
        height=h;
        up=false;
        down=false;
        left=false;
        right=false;
        paths=new Rectangle[14];
        entryPoints=new Rectangle[13];
        menuOptions=new Rectangle[4];
        charaSelect=new Rectangle[9];
        this.addMouseListener(this);
        try{
            InputStream inputStream = getClass().getResourceAsStream("/assets/DisposableDroidBB.ttf");
            customFont=Font.createFont(Font.TRUETYPE_FONT,inputStream);
        }
        catch(Exception e){
            System.out.println("haha your font wont import");
        }

        map=Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/map.png"));
        invPaper = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/inv_paper.png"));
        invFood = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/inv_food.png"));
        minigame=null;
        file = new File("Ooblets!.wav");
        audioStream = AudioSystem.getAudioInputStream(file);
        clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void setUpGUI(){
        contentPane=this.getContentPane();
        this.setTitle("Player #"+playerID);
        contentPane.setPreferredSize(new Dimension(width,height));
        createPlayer();
        drawingComponent=new DrawingComponent();
        contentPane.add(drawingComponent);
        //initialize path coordinates
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
        //initialize entryPoint coordinates
        entryPoints[0]= new Rectangle(186,172,41,35); //NBL
        entryPoints[1]=new Rectangle(494,307,35,41); //D-Shop
        entryPoints[2]=new Rectangle(409,360,35,43); //Pawra
        entryPoints[3]=new Rectangle(438,619,43,35); //Gomz Caf
        entryPoints[4]=new Rectangle(735,101,35,42); //SIC-A
        entryPoints[5]=new Rectangle(742,244,35,41); //SIC-B
        entryPoints[6]=new Rectangle(759,401,35,42); //SIC-C
        entryPoints[7]=new Rectangle(694,668,35,41); //Professors' Building
        //initialize menuOptions coordinates
        menuOptions[0]=new Rectangle(512,138,245,294);
        menuOptions[1]=new Rectangle(762,138,241,294);
        menuOptions[2]=new Rectangle(512,438,245,304);
        menuOptions[3]=new Rectangle(762,438,241,304);
        // character select
        charaSelect[0]=new Rectangle(580,160,200,150);
        charaSelect[1]=new Rectangle(780,160,200,150);
        charaSelect[2]=new Rectangle(580,310,200,150);
        charaSelect[3]=new Rectangle(780,310,200,150);
        charaSelect[4]=new Rectangle(580,460,200,150);
        charaSelect[5]=new Rectangle(780,460,200,150);
        charaSelect[6]=new Rectangle(580,610,200,150);
        charaSelect[7]=new Rectangle(780,610,200,150);
        //start button
       charaSelect[8]=new Rectangle(100,640,370,90);
        //make code that assigns sprites depending on what the player and opponent chose. something with arrays

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setUpKeyListener();
        this.setVisible(true);
        this.setUpTimer();



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

    private class DrawingComponent extends JComponent {
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2d = (Graphics2D) graphics;
            //show title screen first
            if (showTitleScreen == true) {
                g2d.drawImage(titleScreen, 0, 0, null);
                if (me.getStartPressed() == true) {
                    g2d.setFont(customFont);
                    g2d.setColor(Color.black);
                    g2d.setFont(g2d.getFont().deriveFont(25f));
                    g2d.drawString("Waiting for other player to start...", 100, 500);
                }
                if (playerID == 1) {
                    otherSprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/player_" + other.getColorNum() + "_" + other.getDirection() + ".png"));
                    g2d.drawImage(mySprite, 150, 400, null);
                    g2d.drawImage(otherSprite, 200, 400, null);
                } else {
                    otherSprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/player_" + other.getColorNum() + "_" + other.getDirection() + ".png"));
                    g2d.drawImage(otherSprite, 150, 400, null);
                    g2d.drawImage(mySprite, 200, 400, null);
                }
            } else if (showTitleScreen == false) {
                if (me.getCurrentBuilding() == 0) {
                    g2d.drawImage(map, 0, 0, null);
                    mySprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/player_" + me.getColorNum() + "_" + me.getDirection() + ".png"));
                    otherSprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/player_" + other.getColorNum() + "_" + other.getDirection() + ".png"));
                    g2d.drawImage(mySprite, me.getX(), me.getY(), null);
                    g2d.drawImage(otherSprite, other.getX(), other.getY(), null);
                    repaint();
                }
                //if the player is in a building
                else {
                    //if the player isn't in a Gomz stall: draws the interior background and the player's indoor sprite
                    if (me.getCurrentStall() == 0) {
                        insideMap = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/interior_" + me.getCurrentBuilding() + ".png"));
                        g2d.drawImage(insideMap, me.getInsideMapX(), 0, null);
                        //if the player is in the profs' building, they have an active DeliverPapers quest, and haven't received the papers for it yet: draw papers in the building
                        if (me.getCurrentBuilding() == 8 && me.findQuestType(1) != null && ((DeliverPapers) me.findQuestType(1)).getStatus() == 0) {
                            g2d.drawImage(invPaper, me.getInsideMapX() + 34, 500, null);
                            System.out.println("drawing papers");
                        }
                        //if the player is holding an item
                        if (me.getItemNum() != 0) {
                            //look through the player's quest list for DeliverPapers, OrderFood, and PrintPapers quests
                            for (int i = 1; i <= 3; i++) {
                                //if the target building of an existing quest is the same as the buliding the player is in: set that quest as current and break out of the loop
                                if (me.findQuestType(i) != null && me.findQuestType(i).getTargetBuildingNum() == me.getCurrentBuilding()) {
                                    current = me.findQuestType(i);
                                    break;
                                }
                            }
                        }
                        //if the current quest's target building is the same as the building the player is in
                        if (current != null && me.getCurrentBuilding() == current.getTargetBuildingNum()) {
                            //if the current quest is in the phase where the player has placed the item in the designated building: draw the corresponding item
                            if (current.getStatus() == 2) {
                                if (current.getQuestType() == 2)
                                    itemOnMap = invFood;
                                else
                                    itemOnMap = invPaper;
                                g2d.drawImage(itemOnMap, me.getInsideMapX() + 34, 500, null);
                                System.out.println("drawing itemOnMap");
                            }
                        }
                        //if the player and their opponent are in the same building: draw the opponent's indoor sprite
                        if (other.getCurrentBuilding() == me.getCurrentBuilding()) {
                            otherInsideSprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/playerIn_" + other.getColorNum() + "_" + other.getDirection() + "_1.png"));
                            g2d.drawImage(otherInsideSprite, other.getInsideX(), 411, null);
                        }
                        myInsideSprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/playerIn_" + me.getColorNum() + "_" + me.getDirection() + "_1.png"));
                        g2d.drawImage(myInsideSprite, me.getInsideX(), 411, null);
                        repaint();
                    }
                    //if the player is in a Gomz stall: draws the corresponding menu and the text
                    else {
                        menu = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/menu_" + me.getCurrentStall() + ".png"));
                        g2d.drawImage(menu, 0, 0, null);
                        g2d.setFont(customFont);
                        g2d.setFont(g2d.getFont().deriveFont(35f));
                        stall = "hi, welcome to \nchili's";
                        int i = 0;
                        for (String line : stall.split("\n")) {
                            g2d.drawString(line, 267, 158 + i);
                            i += 35;
                        }
                    }
                    //if the player is not in a building: draw map and overworld sprites
                    if (me.getCurrentBuilding() == 0) {
                        g2d.drawImage(map, 0, 0, null);
                        mySprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/player_" + me.getColorNum() + "_" + me.getDirection() + ".png"));
                        otherSprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/player_" + other.getColorNum() + "_" + other.getDirection() + ".png"));
                        g2d.drawImage(mySprite, me.getX(), me.getY(), null);
                        g2d.drawImage(otherSprite, other.getX(), other.getY(), null);
                        repaint();
                    }
                    //if the player is in a building
                    else {
                        //if the player isn't in a Gomz stall: draws the interior background and the player's indoor sprite
                        if (me.getCurrentStall() == 0) {
                            insideMap = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/interior_" + me.getCurrentBuilding() + ".png"));
                            g2d.drawImage(insideMap, me.getInsideMapX(), 0, null);
                            //if the player is in the profs' building, they have an active DeliverPapers quest, and haven't received the papers for it yet: draw papers in the building
                            if (me.getCurrentBuilding() == 8 && me.findQuestType(1) != null && ((DeliverPapers) me.findQuestType(1)).getStatus() == 0) {
                                g2d.drawImage(invPaper, me.getInsideMapX() + 34, 500, null);
                                System.out.println("drawing papers");
                            }
                            //if the player is holding an item
                            if (me.getItemNum() != 0) {
                                //look through the player's quest list for DeliverPapers, OrderFood, and PrintPapers quests
                                for (int i = 1; i <= 3; i++) {
                                    //if the target building of an existing quest is the same as the player's current building and the quest is in the phase where the item has been taken: set that quest as current and break out of the loop
                                    if (me.findQuestType(i) != null && me.findQuestType(i).getStatus() == 1 && me.findQuestType(i).getTargetBuildingNum() == me.getCurrentBuilding()) {
                                        current = me.findQuestType(i);
                                        System.out.println("current quest assigned to quest type " + i);
                                        break;
                                    }
                                }
                            }
                            //if the current quest's target building is the same as the building the player is in
                            if (current != null) {
                                //if the current quest is in the phase where the player has placed the item in the designated building: draw the corresponding item
                                if (current.getStatus() == 2) {
                                    if (current.getQuestType() == 2)
                                        itemOnMap = invFood;
                                    else
                                        itemOnMap = invPaper;
                                    g2d.drawImage(itemOnMap, me.getInsideMapX() + 34, 500, null);
                                    System.out.println("drawing itemOnMap");
                                }
                            }
                            //if the player and their opponent are in the same building: draw the opponent's indoor sprite
                            if (otherBuilding == me.getCurrentBuilding()) {
                                int myAbsInsideX = me.getInsideX() - me.getInsideMapX();
                                int otherAbsInsideX = other.getInsideX() - other.getInsideMapX();
                                otherInsideSprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/playerIn_" + other.getColorNum() + "_" + other.getDirection() + "_1.png"));
                                g2d.drawImage(otherInsideSprite, me.getInsideX() - myAbsInsideX + otherAbsInsideX, 411, null);
                            }
                            myInsideSprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/playerIn_" + me.getColorNum() + "_" + me.getDirection() + "_1.png"));
                            g2d.drawImage(myInsideSprite, me.getInsideX(), 411, null);
                            repaint();
                        }
                        //if the player is in a Gomz stall: draws the corresponding menu and the text
                        else {
                            menu = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/menu_" + me.getCurrentStall() + ".png"));
                            g2d.drawImage(menu, 0, 0, null);
                            g2d.setFont(customFont);
                            g2d.setFont(g2d.getFont().deriveFont(35f));
                            stall = "hi, welcome to \nchili's";
                            int i = 0;
                            for (String line : stall.split("\n")) {
                                g2d.drawString(line, 267, 158 + i);
                                i += 35;
                            }
                        }


                        //draws the player's inventory depending on which item they have
                        if (me.getItemNum() == 1)
                            g2d.drawImage(invPaper, 35, 629, null);
                        else if (me.getItemNum() == 2)
                            g2d.drawImage(invFood, 35, 629, null);
                    }

                    //if there is an active minigame: draw the minigame image
                    if (minigame != null) {
                        g2d.drawImage(minigame, 0, 0, null);
                        //if the player is in D-Shop and they have an active PrintPapers quest: draw the printer text
                        if (me.getCurrentBuilding() == 2 && me.findQuestType(3) != null) {
                            g2d.setFont(customFont);
                            g2d.setFont(g2d.getFont().deriveFont(75f));
                            g2d.drawString("Number of Copies:", 38, 330);
                            g2d.setFont(g2d.getFont().deriveFont(100f));
                            g2d.drawString(((PrintPapers) me.findQuestType(3)).getQuantity(), 38, 430);
                        }
                        //if the player is in Pawra and they have an active PictureCats quest: draw the number of photo's they've taken
                        else if (me.getCurrentBuilding() == 3 && me.findQuestType(5) != null) {
                            g2d.setFont(customFont);
                            g2d.setFont(g2d.getFont().deriveFont(100f));
                            g2d.drawString(((PictureCats) me.findQuestType(5)).getTakenPics() + "!", 900, 100);
                        }
                        //if the player is in NBL
                        if (me.getCurrentBuilding() == 1) {
                            //if the player has an active SendEmails quest: set text font
                            if (me.findQuestType(4) != null && me.findQuestType(4).getStatus() != 2) {
                                g2d.setFont(customFont);
                                g2d.setFont(g2d.getFont().deriveFont(20f));
                                //if the player has sent the email: draw sent text
                                if (me.findQuestType(4).getStatus() == 1)
                                    g2d.drawString("Your email has been sent!", 346, 254);
                                    //if the player has not sent the email: draw email text
                                else {
                                    int i = 0;
                                    for (String line : ((SendEmail) me.findQuestType(4)).getEmailArray()[((SendEmail) me.findQuestType(4)).getKeyCount()].split("\n")) {
                                        g2d.drawString(line, 346, 254 + i);
                                        i += 25;
                                    }
                                }
                            }
                            //if the player has an active PictureCats quest: set text font
                            else if (me.findQuestType(5) != null && me.findQuestType(5).getStatus() != 2) {
                                g2d.setFont(customFont);
                                g2d.setFont(g2d.getFont().deriveFont(20f));
                                // if the player has sent the email: draw sent text
                                if (me.findQuestType(5).getStatus() == 1)
                                    g2d.drawString("Your email has been sent!", 346, 254);
                                    //if the player sent the email with the wrong number of photos: draw retry text
                                else if (((PictureCats) me.findQuestType(5)).wasJustSent()) {
                                    int i = 0;
                                    for (String line : "Professor:\nDid you not see the number of photographs I asked for? \nDo it again, and do it properly this time.".split("\n")) {
                                        g2d.drawString(line, 346, 254 + i);
                                        i += 25;
                                    }
                                }
                                //if the player hasn't sent the email: draw email text
                                else {
                                    int i = 0;
                                    for (String line : ((PictureCats) me.findQuestType(5)).getEmailArray()[((PictureCats) me.findQuestType(5)).getKeyCount()].split("\n")) {
                                        g2d.drawString(line, 346, 254 + i);
                                        i += 25;
                                    }
                                }
                            }
                        }
                    }
                    }
                //for drawing the clock
                g2d.setColor(Color.white);
                g2d.setFont(customFont);
                g2d.setFont(g2d.getFont().deriveFont(35f));
                String clockTime = String.format("Time : %02d:%02d", currentHour, currentMinute);
                g2d.drawString(clockTime, 840, 25);


                // If game time is over, show end screen
                if (showEndScreen) {
                    g2d.drawImage(endScreen, 0, 0, null);
                    g2d.drawImage(mySprite, 500, 150, null);
                    g2d.drawImage(otherSprite, 550, 150, null);
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
            if(path.hasPlayer(x,y))
                return true;
        }
        return false;
    }
    //player collision
    public boolean isColliding(Player other) {
        return !(me.getX() + 28 <= other.getX() ||
                me.getX() >= other.getX() + 28 ||
                me.getY() + 28 <= other.getY() ||
                me.getY() >= other.getY() + 28);
    }

    private void setUpTimer2() {
        // + 10 minutes every 2 seconds!!
        clockTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                currentMinute += 10;
                if (currentMinute == 60) {
                    currentMinute = 0;
                    currentHour++;
                }

                if (currentHour == 17) {
                    clockTimer.stop();
                    showEndScreen = true;
                }

                repaint();
            }
        });

        clockTimer.setInitialDelay(0);
        clockTimer.start();
    }


    private void setUpTimer(){
        int interval=10;
        ActionListener actionListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int speed=3;
                if (up) {
                    me.moveV(-speed);
                    if (!isOnPath(me.getX(),me.getY()) || isColliding(other)){
                        me.setX(me.getPrevX());
                        me.setY(me.getPrevY());
                    }
                }
                if (down) {
                    me.moveV(speed);
                    if (!isOnPath(me.getX(), me.getY())|| isColliding(other)) {
                        me.setX(me.getPrevX());
                        me.setY(me.getPrevY());
                    }
                }
                if (left) {
                    if (me.getCurrentBuilding()==0) {
                        me.moveH(-speed);
                        if (!isOnPath(me.getX(), me.getY())|| isColliding(other)) {
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
                        if (!isOnPath(me.getX(), me.getY())|| isColliding(other)) {
                            me.setX(me.getPrevX());
                            me.setY(me.getPrevY());
                        }
                    }
                    else
                        me.moveH(speed+3);
                }

                drawingComponent.repaint();
            }
        };
        animationTimer=new Timer(interval,actionListener);
        animationTimer.start();
    }


    private void setUpKeyListener(){
        KeyListener keyListener=new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                int keyCode = e.getKeyCode();

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                if(keyCode == KeyEvent.VK_ENTER){
                    if(current!=null) {
                        current = null;
                        System.out.println("current made null");
                    }
                    Quest q=null;
                    for(Quest quest : me.getQuestList()){
                        if(quest.isCompleted()) {
                            q = quest;
                            break;
                        }
                    }
                    if(q!=null)
                        q.finish(me);
                }

                //if the key typed is a number, there is an active PrintPapers quest, and the minigame in D-Shop is active: add the typed number (as a String) to the PrintPapers quest's quantity value
                if(keyCode>47&&keyCode<58&&minigame!=null&&me.getCurrentBuilding()==2&&me.findQuestType(3)!=null){
                    ((PrintPapers) me.findQuestType(3)).editQuantity(Integer.toString(keyCode-48));
                    System.out.println("quantity is now "+((PrintPapers) me.findQuestType(3)).getQuantity());
                }

                // just testing quest assignment, will delete
                else if (keyCode == KeyEvent.VK_1) {
                    int t=(int)(Math.random()*3+5);
                    me.getQuestList().add(new DeliverPapers(me,t));
                    System.out.println("assigned deliver papers to building#"+t);
                } else if (keyCode == KeyEvent.VK_2) {
                    int t=(int)(Math.random()*4+5);
                    int o=(int)(Math.random()*9+1);
                    me.getQuestList().add(new OrderFood(me,t,o));
                    System.out.println("assigned deliver order#"+o+" to building#"+t);
                } else if (keyCode == KeyEvent.VK_3) {
                    int t=(int)(Math.random()*4+5);
                    int q=(int)(Math.random()*25+5);
                    me.getQuestList().add(new PrintPapers(me,t,q));
                    System.out.println("assigned deliver "+q+" papers to building#"+t);
                } else if(keyCode==KeyEvent.VK_4){
                    me.getQuestList().add(new SendEmail(me,0));
                    System.out.println("assigned send email#"+0);
                } else if(keyCode==KeyEvent.VK_5){
                    int p=(int)(Math.random()*11+10);
                    me.getQuestList().add(new PictureCats(me,p));
                    System.out.println("assigned picture cats "+p+" times and then send email");
                }

                //if the player is in NBL and the minigame is active
                if(me.getCurrentBuilding()==1&&minigame!=null) {
                    //if there is an unsent SendEmail quest
                    if(me.findQuestType(4)!=null&&me.findQuestType(4).getStatus()==0) {
                        //if the email has not been fully typed
                        if (((SendEmail) me.findQuestType(4)).getKeyCount() < ((SendEmail) me.findQuestType(4)).getEmailArray().length - 1) {
                            //if a letter key is pressed: progress the email by 1 character
                            if (keyCode > 64 && keyCode < 91) {
                                ((SendEmail) me.findQuestType(4)).incrementKeyCount();
                                System.out.println("keyCount is now " + ((SendEmail) me.findQuestType(4)).getKeyCount());
                            }
                        }
                        //if the email has been fully typed
                        else {
                            //if the Enter key is pressed: send the email
                            if (keyCode == KeyEvent.VK_ENTER) {
                                ((SendEmail) me.findQuestType(4)).sendEmail();
                                System.out.println("sent email!");
                            }
                        }
                    }
                    //if there is a sent SendEmail quest
                    else if(me.findQuestType(4)!=null&&me.findQuestType(4).getStatus()==1&&keyCode == KeyEvent.VK_ENTER) {
                        ((SendEmail)me.findQuestType(4)).complete();
                        minigame = null;
                        System.out.println("made minigame null");
                    }
                    //if there is an uncompleted PictureCats quest
                    else if(me.findQuestType(5)!=null&&me.findQuestType(5).getStatus()==0&&me.findQuestType(4)==null){
                        //if an incorrect PictureCats email was just sent
                        if(((PictureCats) me.findQuestType(5)).wasJustSent()) {
                            if (keyCode == KeyEvent.VK_ENTER) {
                                ((PictureCats) me.findQuestType(5)).resetJustSent();
                                minigame=null;
                                System.out.println("made minigame null");
                            }
                        }
                        //if the email has not been fully typed
                        else if (((PictureCats) me.findQuestType(5)).getKeyCount() < ((PictureCats) me.findQuestType(5)).getEmailArray().length - 1) {
                            //if a letter key is pressed: progress the email by 1 character
                            if (keyCode > 64 && keyCode < 91) {
                                ((PictureCats) me.findQuestType(5)).incrementKeyCount();
                                System.out.println("keyCount is now " + ((PictureCats) me.findQuestType(5)).getKeyCount());
                            }
                        }
                        //if the email has been fully typed
                        else {
                            //if the Enter key is pressed:
                            if (keyCode == KeyEvent.VK_ENTER&&me.findQuestType(5).getStatus()==0){
                                //if the player took the wrong number of photos: reset the quest
                                if(!((PictureCats) me.findQuestType(5)).hasCorrectPhotos()) {
                                    ((PictureCats) me.findQuestType(5)).resetQuest();
                                }
                                //if the player took the correct number of photos: send the email
                                else {
                                    ((PictureCats) me.findQuestType(5)).sendEmail();
                                    System.out.println("sent email!");
                                }
                            }
                        }
                    }
                    //if there is a sent PictureCats quest: mark the quest as completed and exit the minigame
                    else if(me.findQuestType(5)!=null&&keyCode == KeyEvent.VK_ENTER&&me.findQuestType(5).getStatus()==1) {
                        ((PictureCats)me.findQuestType(5)).complete();
                        minigame = null;
                        System.out.println("made minigame null");
                    }
                }
                //if the Enter key is pressed:
                else if (keyCode == KeyEvent.VK_ENTER) {
                    //if the player is not in a building: check all entryPoints
                    if (me.getCurrentBuilding() == 0) {
                        for (int i = 0; i < 8; i++) {
                            //if the player is inside an entryPoint, enter its corresponding building
                            if (entryPoints[i].hasPlayer(me.getX(), me.getY()))
                                me.enterBuilding(playerID, i + 1);
                        }
                    }
                    //if the player is in a building
                    else {
                        //if the player is in the rightmost side of the interior: leave the building
                        if (me.getInsideMapX() == -1024 && me.getInsideX() > 820)
                            me.leaveBuilding();
                        //if the player is in Gomz and not in any stall: enter a stall depending on their position
                        else if (me.getCurrentBuilding() == 4 && me.getCurrentStall() == 0) {
                            if (me.getInsideX() < 381 && me.getInsideX() > 26 && me.getInsideMapX() == 0) {
                                me.setCurrentStall(1);
                            } else if (me.getInsideMapX() < -210 && me.getInsideMapX() > -577) {
                                me.setCurrentStall(2);
                            } else if (me.getInsideMapX() < -855 && me.getInsideX() < 657) {
                                me.setCurrentStall(3);
                            }
                        }
                        //if the player is on the left side of the interior
                        else if (me.getInsideMapX()>-100) {
                            //if the player is in D-Shop or Pawra
                            if (me.getCurrentBuilding() == 2 || me.getCurrentBuilding() == 3) {
                                //if there is no active minigame: assign it a corresponding image
                                if (minigame == null)
                                    minigame = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/minigame_" + me.getCurrentBuilding() + ".png"));
                                //if there is an active minigame: make the minigame inactive
                                else {
                                    if (me.getCurrentBuilding() == 2 && me.findQuestType(3) != null && !(((PrintPapers) me.findQuestType(3)).getQuantity().equals(""))) {
                                        ((DeliverQuest) me.findQuestType(3)).takeItem(0);
                                        System.out.println("took papers");
                                    }
                                    minigame = null;
                                    System.out.println("made minigame null");
                                }
                            }
                            //if the player is on the leftmost side of the interior
                            else if (me.getInsideMapX() == 0 && me.getInsideX() < 230) {
                                //if the player is in a SIC building: make the minigame active and assign it to a whiteboard image
                                if (me.getCurrentBuilding() == 5 || me.getCurrentBuilding() == 6 || me.getCurrentBuilding() == 7)
                                    minigame = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/whiteboard.png"));
                                //if the player is in NBL: switch the minigame to active or inactive depending on its current state
                                else if (me.getCurrentBuilding() == 1) {
                                    if (minigame == null)
                                        minigame = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/minigame_" + me.getCurrentBuilding() + ".png"));
                                    else {
                                        minigame = null;
                                        System.out.println("made minigame null");
                                    }
                                }
                            }
                        }
                    }
                }

                //if the I key is pressed and the player is on the leftmost side of the interior
                if(keyCode==KeyEvent.VK_I&&me.getInsideMapX()==0&&me.getInsideX()<230){
                    //if the player is not holding any items, is in NBL, has an active DeliverPapers quest, and has not received papers for said quest: take papers
                    if (me.getItemNum() == 0&&me.getCurrentBuilding()==8&&me.findQuestType(1)!=null&&me.findQuestType(1).getStatus()==0) {
                        ((DeliverPapers) me.findQuestType(1)).takeItem(0);
                        System.out.println("took papers");
                    }
                }

                //if the O key is pressed and the player is on the leftmost side of the interior
                if(keyCode==KeyEvent.VK_O&&me.getInsideMapX()==0&&me.getInsideX()<230){
                    //if there is an active DeliverPapers,OrderFood, or PrintPapers quest, the player is in its target building, and it is in the phase where its item has been taken
                    for(int i=1;i<=3;i++){
                        if(me.findQuestType(i)!=null&&me.findQuestType(i).getTargetBuildingNum()==me.getCurrentBuilding()&&me.findQuestType(i).getStatus()==1) {
                            //if it is a DeliverPapers quest: place the papers
                            if(i==1){
                                ((DeliverQuest) me.findQuestType(i)).placeItem();
                                System.out.println("placed papers");
                                break;
                            }
                            //if it is an OrderFood quest: place the order if the order is correct and reset the quest if not
                            if (i==2) {
                                if (((OrderFood) me.findQuestType(i)).hasCorrectOrder()) {
                                    ((DeliverQuest) me.findQuestType(i)).placeItem();
                                    System.out.println("placed order");
                                }
                                else {
                                    ((OrderFood) me.findQuestType(i)).resetQuest();
                                    System.out.println("wrong order, try again");
                                }
                                break;
                            }
                            //if it is a PrintPapers quest: place the papers if the quantity is correct and reset the quest if not
                            if(((PrintPapers) me.findQuestType(i)).hasCorrectQuantity()){
                                ((DeliverQuest) me.findQuestType(i)).placeItem();
                                System.out.println("placed papers");
                            }
                            else{
                                ((PrintPapers) me.findQuestType(i)).resetQuest();
                                System.out.println("wrong quantity, try again");
                            }
                        }
                    }
                }

                //if the Space key is pressed, there is an active minigame, and there is an active PictureCats quest: add 1 to the number of photos taken
                if(keyCode==KeyEvent.VK_SPACE&&me.getCurrentBuilding()==3&&minigame!=null&&me.findQuestType(5)!=null){
                    ((PictureCats) me.findQuestType(5)).incrementTakenPics();
                    System.out.println("you've taken a total of "+((PictureCats) me.findQuestType(5)).getTakenPics()+" photos!");
                }

                if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
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
                    if(minigame==null) {
                        left = true;
                        me.setDirection(3);
                    }
                } else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
                    if (minigame == null) {
                        right = true;
                        me.setDirection(4);
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
        contentPane.setFocusable(true);
        contentPane.requestFocusInWindow();
        contentPane.addKeyListener(keyListener);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //if the cursor is in the lower right portion of the menu: leave the current stall
        if(menuOptions[3].contains((int)MouseInfo.getPointerInfo().getLocation().getX()-(int)GameFrame.this.getLocation().getX(),(int)MouseInfo.getPointerInfo().getLocation().getY()-(int)GameFrame.this.getLocation().getY()))
            me.setCurrentStall(0);
        //if the cursor is in any other portion of the menu
        for(int i=0;i<3;i++) {
            if (menuOptions[i].contains((int) MouseInfo.getPointerInfo().getLocation().getX()-(int)GameFrame.this.getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY()-(int)GameFrame.this.getLocation().getY())) {
                //if the player is in a stall, there is an active OrderFood quest, and that quest is in the phase where the player has not yet taken an order: take the corresponding order and leave the stall
                if (me.getCurrentStall() != 0&&me.findQuestType(2)!=null&&me.findQuestType(2).getStatus()==0) {
                    ((OrderFood) me.findQuestType(2)).takeItem(me.getCurrentStall() * 3 - (2-i));
                    System.out.println("took food order#" + (me.getCurrentStall() * 3 - (2-i)));
                    me.setCurrentStall(0);
                }
                break;
            }
        }
        // if cursor is on start button
        if(charaSelect[8].contains((int)MouseInfo.getPointerInfo().getLocation().getX()-(int)GameFrame.this.getLocation().getX(),(int)MouseInfo.getPointerInfo().getLocation().getY()-(int)GameFrame.this.getLocation().getY()))
        {
            if (showTitleScreen == true) {
                if (me.getStartPressed()==false) {
                    me.setStartPressed(true);
                    System.out.println("Waiting for other player to start...");

                }
            }
        }
        //if the cursor is in color change buttons
        for(int i=0;i<8;i++) {
            if (charaSelect[i].contains((int) MouseInfo.getPointerInfo().getLocation().getX()-(int)GameFrame.this.getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY()-(int)GameFrame.this.getLocation().getY())) {
                if (showTitleScreen) {
                    int colorNum = i + 1;
                    me.setColorNum(colorNum);
                    System.out.println("switched to "+colorNum);
                    mySprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/player_" + me.getColorNum() + "_" + me.getDirection() + ".png"));
                    otherSprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/player_" + other.getColorNum() + "_" + other.getDirection() + ".png"));
                }
                break;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

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
                    int otherX=dataIn.readInt();
                    int otherY=dataIn.readInt();
                    int otherDirection=dataIn.readInt();
                    otherBuilding=dataIn.readInt();
                    int otherInsideX=dataIn.readInt();
                    int otherInsideMapX=dataIn.readInt();
                    int otherColorNum = dataIn.readInt();
                    boolean otherStartPressed = dataIn.readBoolean();
                    if(other!=null){
                        other.setX(otherX);
                        other.setY(otherY);
                        other.setDirection(otherDirection);
                        other.setInsideX(otherInsideX);
                        other.setInsideMapX(otherInsideMapX);
                        other.setColorNum(otherColorNum);
                        other.setStartPressed(otherStartPressed);
                        if (me.getStartPressed()==true && other.getStartPressed()==true && showTitleScreen==true) {
                            System.out.println("Game starting!!!");
                            showTitleScreen = false;
                            setUpTimer2();
                            clip.start();
                            repaint();
                    }
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
                clip.start();

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
                        dataOut.writeInt(me.getCurrentBuilding());
                        dataOut.writeInt(me.getInsideX());
                        dataOut.writeInt(me.getInsideMapX());
                        dataOut.writeInt(me.getColorNum());
                        dataOut.writeBoolean(me.getStartPressed());
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


    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        GameFrame gameFrame=new GameFrame(1024,768);
        gameFrame.connectToServer();
        gameFrame.setUpGUI();
    }
}
