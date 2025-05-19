import java.awt.event.KeyEvent;
import java.lang.reflect.Array;

public class SendEmail extends Quest{
    public int emailNum,keyCount;
    public boolean sentEmail;
    public String[][] emails;
    public Player p;

    public SendEmail(Player player,int n){
        p=player;
        emailNum=n;
        emails=new String[3][];
        keyCount=0;
        sentEmail=false;
        points=200;
        questType=4;

        initializeEmail1();
    }

    public void sendEmail(){
        sentEmail=true;
    }

    public boolean hasSentEmail(){
        return sentEmail;
    }

    public int getKeyCount(){
        return keyCount;
    }

    public String[] getEmailArray(){
        return emails[emailNum];
    }

    public void incrementKeyCount(){
        if(keyCount<emails[emailNum].length-1)
            keyCount++;
    }

    public void initializeEmail1(){
        emails[0]=new String[409];
        emails[0][0]="|";
        String text1="Good day, Professor!";
        for(int i=1;i<=20;i++){
            emails[0][i]=text1.substring(0,i)+"|";
        }
        text1="Good day, Professor!\n\nI am emailing you about our exam on the 25th on behalf of ";
        for(int i=21;i<=79;i++){
            emails[0][i]=text1.substring(0,i+1)+"|";
        }
        text1="Good day, Professor!\n\nI am emailing you about our exam on the 25th on behalf of \nthe class. We are not ready";
        for(int i=80;i<=106;i++){
            emails[0][i]=text1.substring(0,i+2)+"|";
        }
        for(int i=107;i<=115;i++){
            emails[0][i]=text1.substring(0,214-i)+"|";
        }
        text1="Good day, Professor!\n\nI am emailing you about our exam on the 25th on behalf of \nthe class. We are tired of trying to meet your ";
        for(int i=116;i<=125;i++){
            emails[0][i]=text1.substring(0,i-16)+"|";
        }
        text1="Good day, Professor!\n\nI am emailing you about our exam on the 25th on behalf of \nthe class. We are tired of trying to meet your \nunreasonable demands";
        for(int i=126;i<=164;i++){
            emails[0][i]=text1.substring(0,i-15)+"|";
        }
        for(int i=165;i<=213;i++){
            emails[0][i]=text1.substring(0,312-i)+"|";
        }
        text1="Good day, Professor!\n\nI am emailing you about our exam on the 25th on behalf of \nthe class. We are currently very busy with requirements ";
        for(int i=214;i<=251;i++){
            emails[0][i]=text1.substring(0,i-115)+"|";
        }
        text1="Good day, Professor!\n\nI am emailing you about our exam on the 25th on behalf of \nthe class. We are currently very busy with requirements \nfrom our other subjects as we are taking 21 units of ";
        for(int i=252;i<=304;i++){
            emails[0][i]=text1.substring(0,i-115)+"|";
        }
        text1="Good day, Professor!\n\nI am emailing you about our exam on the 25th on behalf of \nthe class. We are currently very busy with requirements \nfrom our other subjects as we are taking 21 units of \nclasses this semester. May we request that the exam be postponed?";
        for(int i=305;i<=359;i++){
            emails[0][i]=text1.substring(0,i-113)+"|";
        }
        text1="Good day, Professor!\n\nI am emailing you about our exam on the 25th on behalf of \nthe class. We are currently very busy with requirements \nfrom our other subjects as we are taking 21 units of \nclasses this semester. May we request that the exam be \npostponed?\n\nThank you for your kind consideration.";
        for(int i=360;i<=369;i++){
            emails[0][i]=text1.substring(0,i-112)+"|";
        }
        text1="Good day, Professor!\n\nI am emailing you about our exam on the 25th on behalf of \nthe class. We are currently very busy with requirements \nfrom our other subjects as we are taking 21 units of \nclasses this semester. May we request that the exam be \npostponed?\n\nThank you for your kind consideration.";
        for(int i=370;i<=408;i++){
            emails[0][i]=text1.substring(0,i-110)+"|";
        }
    }

}

