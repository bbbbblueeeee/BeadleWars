import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GameStarter {

    //main method that starts game for players
    public static void main(String[] args) {

        /*
        String[] email;
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
        for(String line : email)
            System.out.println(line);
        */

        //int line = "Attached below are the Pawra cat photos you requested.".length();
        //    System.out.println(line);
        ArrayList<Quest> quests;
        Player player = new Player(1,1,1,1);
        Quest quest = new PrintPapers(player,1,1);
        for(int i=0;i<3;i++) {
            int random = (int) (Math.random() * 5) + 1;
            while (player.findQuestType(random) != null)
                random = (int) (Math.random() * 5) + 1;
            player.addQuest(random);
            System.out.println("added quest type " + random);
        }
    }
}
