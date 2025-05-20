import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

public class GameStarter {

    //main method that starts game for players
    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        GameFrame gameFrame=new GameFrame(1024,768);
        gameFrame.connectToServer();
        gameFrame.setUpGUI();
    }
}
