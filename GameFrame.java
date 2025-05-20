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

    private int width,height,playerID,currentHour,currentMinute;
    private Container contentPane;
    private Player me,other;
    private Timer animationTimer,clockTimer,questManagerTimer,positionCheckingTimer;
    private boolean up,down,left,right;
    private Image minigame;
    private Socket socket;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;
    private Rectangle[] paths,entryPoints,menuOptions, charaSelect;
    private Quest current;
    private GameCanvas gameCanvas;

    private boolean showEndScreen = false;
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
        currentHour=7;
        currentMinute=50;
        paths=new Rectangle[14];
        entryPoints=new Rectangle[13];
        menuOptions=new Rectangle[4];
        charaSelect=new Rectangle[9];
        this.addMouseListener(this);

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
        gameCanvas=new GameCanvas(me,other,playerID,this);
        contentPane.add(gameCanvas);
        //drawingComponent=new DrawingComponent();
        //contentPane.add(drawingComponent);
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

    public int getGameState(){
        if(showTitleScreen)
            return 1;
        else if(!showEndScreen)
            return 2;
        else
            return 3;
    }

    public int getCurrentHour(){
        return currentHour;
    }

    public int getCurrentMinute(){
        return currentMinute;
    }

    public Image getMinigame(){
        return minigame;
    }

    private void setUpPositionCheckingTimer(){
        positionCheckingTimer=new Timer(25,new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {

                if (me.getCurrentBuilding() == 0) {
                    if (new Rectangle(166, 50, 145, 101).contains(me.getX(), me.getY())) {
                        me.setDirection(2);
                        me.setX(191);
                        me.setY(175);
                        System.out.println("position mistake 1");
                    } else if (new Rectangle(544, 233, 68, 65).contains(me.getX(), me.getY())||new Rectangle(564,373,28,28).contains(me.getX(),me.getY())) {
                        me.setDirection(3);
                        me.setX(501);
                        me.setY(316);
                        System.out.println("position mistake 2");
                    } else if (new Rectangle(254, 285, 116, 150).contains(136, 150)) {
                        me.setDirection(4);
                        me.setX(409);
                        me.setY(367);
                        System.out.println("position mistake 3");
                    } else if (new Rectangle(199, 633, 210, 83).contains(me.getX(), me.getY())) {
                        me.setDirection(1);
                        me.setX(443);
                        me.setY(625);
                        System.out.println("position mistake 4");
                    } else if (new Rectangle(794, 42, 178, 143).contains(me.getX(), me.getY())) {
                        me.setDirection(3);
                        me.setX(742);
                        me.setY(107);
                        System.out.println("position mistake 5");
                    } else if (new Rectangle(794, 216, 178, 97).contains(me.getX(), me.getY())) {
                        me.setDirection(3);
                        me.setX(749);
                        me.setY(249);
                        System.out.println("position mistake 6");
                    } else if (new Rectangle(811, 387, 161, 107).contains(me.getX(), me.getY())) {
                        me.setDirection(3);
                        me.setX(766);
                        me.setY(407);
                        System.out.println("position mistake 7");
                    } else if (new Rectangle(694, 544, 117, 110).contains(me.getX(), me.getY())) {
                        me.setDirection(3);
                        me.setX(701);
                        me.setY(668);
                        System.out.println("position mistake 8");
                    }
                }
            }
        });

        positionCheckingTimer.setInitialDelay(25);
        positionCheckingTimer.start();
    }

    private void setUpQuestManagerTimer(){
        questManagerTimer=new Timer(48000,new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                if(me.getQuestList().get(2)==null) {
                    int random = (int) (Math.random() * 5) + 1;
                    while (me.findQuestType(random) != null)
                        random = (int) (Math.random() * 5) + 1;
                    me.addQuest(random);
                }
            }
        });

        questManagerTimer.setInitialDelay(48000);
        questManagerTimer.start();
    }

    private void setUpTimer2() {
        // + 10 minutes every 2 seconds!!
        clockTimer = new Timer(4000, new ActionListener() {
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

                gameCanvas.repaint();
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
                        if(quest!=null&&quest.isCompleted()) {
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
                    else if(me.findQuestType(4)==null&&me.findQuestType(5)==null&&keyCode==KeyEvent.VK_ENTER)
                        minigame=null;
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
                                    if (me.getCurrentBuilding() == 2 && me.findQuestType(3) != null && !(((PrintPapers) me.findQuestType(3)).getQuantity().equals(""))&&me.getItemNum()==0) {
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
                    //if the player is not holding any items, is in prof's building, has an active DeliverPapers quest, and has not received papers for said quest: take papers
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
        System.out.println(me.getCurrentBuilding());
        //if the cursor is in the lower right portion of the menu: leave the current stall
        if(menuOptions[3].contains((int)MouseInfo.getPointerInfo().getLocation().getX()-(int)GameFrame.this.getLocation().getX(),(int)MouseInfo.getPointerInfo().getLocation().getY()-(int)GameFrame.this.getLocation().getY()))
            me.setCurrentStall(0);
        //if the cursor is in any other portion of the menu
        for(int i=0;i<3;i++) {
            if (menuOptions[i].contains((int) MouseInfo.getPointerInfo().getLocation().getX()-(int)GameFrame.this.getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY()-(int)GameFrame.this.getLocation().getY())) {
                //if the player is in a stall, there is an active OrderFood quest, and that quest is in the phase where the player has not yet taken an order: take the corresponding order and leave the stall
                if (me.getCurrentStall() != 0&&me.findQuestType(2)!=null&&me.findQuestType(2).getStatus()==0&&me.getItemNum()==0) {
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
                    int otherBuilding=dataIn.readInt();
                    int otherInsideX=dataIn.readInt();
                    int otherInsideMapX=dataIn.readInt();
                    int otherColorNum = dataIn.readInt();
                    boolean otherStartPressed = dataIn.readBoolean();
                    if(other!=null){
                        other.setX(otherX);
                        other.setY(otherY);
                        if(me.getCurrentBuilding()==0&&other.getCurrentBuilding()==0)
                            other.setDirection(otherDirection);
                        other.setCurrentBuilding(otherBuilding);
                        other.setInsideX(otherInsideX);
                        other.setInsideMapX(otherInsideMapX);
                        other.setColorNum(otherColorNum);
                        other.setStartPressed(otherStartPressed);
                        if (me.getStartPressed()==true && other.getStartPressed()==true && showTitleScreen==true) {
                            System.out.println("Game starting!!!");
                            showTitleScreen = false;
                            me.initializeQuests();
                            setUpTimer2();
                            setUpQuestManagerTimer();
                            setUpPositionCheckingTimer();
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
