/**
 The Picturecats class extends quest and isa quest where the 


 @author Krystal O. Lim Tiong Soon (242615)
 @author Francine Denise L. Lee (24537)
 @version 20 May 2025


 We have not discussed the Java language code in our program
 with anyone other than our instructor or the teaching assistants
 assigned to this course.


 We have not used Java language code obtained from another student,
 or any other unauthorized source, either modified or unmodified.


 If any Java language code or documentation used in our program
 was obtained from another source, such as a textbook or website,
 that has been clearly noted with a proper citation in the comments
 of our program.
 */
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;

public class PictureCats extends Quest{
    public int wantedPics,takenPics,keyCount;
    public boolean sentEmail,justSent;
    public String[] email;
    public Player p;

    public PictureCats(Player player,int n){
        p=player;
        targetBuildingNum=1;
        wantedPics=n;
        takenPics=0;
        keyCount=0;
        points=400;
        sentEmail=false;
        justSent=false;
        questType=5;

        initializeEmail1();
    }

    public boolean hasCorrectPhotos(){
        return takenPics==wantedPics;
    }

    public void incrementTakenPics(){
        takenPics++;
    }

    public int getTakenPics(){
        return takenPics;
    }

    public void sendEmail(){
        sentEmail=true;
    }

    public void complete(){
        taskComplete=true;
    }

    public int getKeyCount(){
        return keyCount;
    }

    public String[] getEmailArray(){
        return email;
    }

    public void incrementKeyCount(){
        if(keyCount<email.length-1)
            keyCount++;
    }

    public int getStatus(){
        if(taskComplete)
            return 2;
        else if(sentEmail)
            return 1;
        else
            return 0;
    }

    public boolean wasJustSent(){
        return justSent;
    }

    public void resetJustSent(){
        justSent=false;
    }

    public void resetQuest(){
        takenPics=0;
        keyCount=0;
        taskComplete=false;
        sentEmail=false;
        justSent=true;
    }

    public int getWantedPics(){
        return wantedPics;
    }

    public void initializeEmail1(){
        email=new String[87];
        email[0]="|";
        String text="Good day, Professor!";
        for(int i=1;i<=20;i++){
            email[i]=text.substring(0,i)+"|";
        }
        text="Good day, Professor!\n\nAttached below are the Pawra cat photos you requested.";
        for(int i=21;i<=75;i++){
            email[i]=text.substring(0,i+1)+"|";
        }
        text="Good day, Professor!\n\nAttached below are the Pawra cat photos you requested.\n\nphotos.zip";
        for(int i=76;i<=86;i++){
            email[i]=text.substring(0,i+2)+"|";
        }
    }

}

