import java.awt.event.KeyEvent;

public class GameStarter {

    //main method that starts game for players
    public static void main(String[] args) {

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

        //int line = "Attached below are the Pawra cat photos you requested.".length();
        //    System.out.println(line);
    }
}
