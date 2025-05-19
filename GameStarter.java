import java.awt.event.KeyEvent;

public class GameStarter {

    //main method that starts game for players
    public static void main(String[] args) {

        SendEmail test=new SendEmail(new Player(445,626,3,1),0);
        String text1="Good day, Professor!";
        System.out.println(test.getEmailArray()[369]);
        System.out.println(test.getEmailArray()[370]);
        System.out.println(test.getEmailArray()[371]);
    }
}
