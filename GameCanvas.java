import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class GameCanvas extends JComponent{
    private Image mySprite,otherSprite,map,myInsideSprite,otherInsideSprite,insideMap,invPaper,invFood,minigame,menu,itemOnMap,endScreen,titleScreen,endScreen_Win,endScreen_Lose,endScreen_Draw;
    private Font customFont;
    private String stall;
    private Player me,other;
    private int playerID;
    private GameFrame frame;

    public GameCanvas(Player p1,Player p2,int id,GameFrame gf){
        me=p1;
        other=p2;
        playerID=id;
        frame=gf;
        endScreen = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/endscreen.png"));
        titleScreen = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/title.png"));
        map=Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/map.png"));
        invPaper = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/inv_paper.png"));
        invFood = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/inv_food.png"));
        endScreen_Win = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/endscreen_win.png"));
        endScreen_Lose = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/endscreen_lose.png"));
        endScreen_Draw = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/endscreen_draw.png"));

        try{
            InputStream inputStream = getClass().getResourceAsStream("/assets/DisposableDroidBB.ttf");
            customFont=Font.createFont(Font.TRUETYPE_FONT,inputStream);
        }
        catch(Exception e){
            System.out.println("haha your font wont import");
        }
    }

    protected void paintComponent(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        //if in title screen
        if (frame.getGameState()==1) {
            g2d.drawImage(titleScreen, 0, 0, null);
            mySprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/player_" + me.getColorNum() + "_" + me.getDirection() + ".png"));
            System.out.println(other.getColorNum());
            System.out.println(other.getDirection());
            otherSprite = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/assets/player_" + other.getColorNum() + "_" + other.getDirection() + ".png"));
            if (me.getStartPressed()) {
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
        }
        //if the game has started
        else if (frame.getGameState()==2) {
            //if the player is not in a building
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
                    //if the current quest's target building is the same as the building the player is in
                    if (me.getCompletedQuest() != null) {
                        //if the current quest is in the phase where the player has placed the item in the designated building: draw the corresponding item
                        if (me.getCompletedQuest().getStatus() == 2) {
                            if (me.getCompletedQuest().getQuestType() == 2)
                                itemOnMap = invFood;
                            else
                                itemOnMap = invPaper;
                            //g2d.drawImage(itemOnMap, me.getInsideMapX() + 34, 500, null);
                            g2d.drawImage(itemOnMap, 400, 500, null);
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
                        //if the current quest's target building is the same as the building the player is in
                        if (me.getCompletedQuest() != null) {
                            //if the current quest is in the phase where the player has placed the item in the designated building: draw the corresponding item
                            if (me.getCompletedQuest().getStatus() == 2) {
                                if (me.getCompletedQuest().getQuestType() == 2)
                                    itemOnMap = invFood;
                                else
                                    itemOnMap = invPaper;
                                g2d.drawImage(itemOnMap, me.getInsideMapX() + 34, 500, null);
                                System.out.println("drawing itemOnMap");
                            }
                        }
                        //if the player and their opponent are in the same building: draw the opponent's indoor sprite
                        if (other.getCurrentBuilding() == me.getCurrentBuilding()) {
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
                        g2d.setFont(g2d.getFont().deriveFont(30f));
                        if (me.getCurrentStall() == 1)
                            stall = "Hello, welcome to \nAte Kristen's \nBacsilog! What \ncan I get you?";
                        else if (me.getCurrentStall() == 2)
                            stall = "Hello, welcome to \nCFK Chicken! What \ncan I get you?";
                        else
                            stall = "Hello, welcome to \nHunger Burger! \nWhat can I get \nyou?";
                        int i = 0;
                        for (String line : stall.split("\n")) {
                            g2d.drawString(line, 267, 158 + i);
                            i += 35;
                        }
                    }
                }

                //draws the player's inventory depending on which item they have
                if (me.getItemNum() == 1)
                    g2d.drawImage(invPaper, 35, 629, null);
                else if (me.getItemNum() == 2)
                    g2d.drawImage(invFood, 35, 629, null);

                //if there is an active minigame: draw the minigame image
                if (frame.getMinigame() != null) {
                    g2d.drawImage(frame.getMinigame(), 0, 0, null);
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
            String clockTime = String.format("Time : %02d:%02d", frame.getCurrentHour(), frame.getCurrentMinute());
            g2d.drawString(clockTime, 840, 25);
        }
        // If game time is over, show end screen
        else if (frame.getGameState()==3) {
            if (me.getPoints() > other.getPoints()) {
                g2d.drawImage(endScreen_Win, 0, 0, null);
            } else if (me.getPoints() < other.getPoints()) {
                g2d.drawImage(endScreen_Lose, 0, 0, null);
            } else {
                g2d.drawImage(endScreen_Draw, 0, 0, null);
            }
            g2d.drawImage(mySprite, 325, 230, null);
            g2d.drawImage(otherSprite, 680, 230, null);
            g2d.setFont(customFont);
            g2d.setColor(Color.black);
            g2d.setFont(g2d.getFont().deriveFont(35f));


            g2d.drawString("Your Score: " + me.getPoints(), 250, 285);
            g2d.drawString("Opponent's Score: " + other.getPoints(), 560, 285);
        }
    }
}
