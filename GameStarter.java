/**
 The Gamstarter class is responsible for setting up the main method and starting the game


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
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

public class GameStarter {

    /**
     this main method sets up the gameframe, connects to the server, and sets up the GUIs
     */

    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        GameFrame gameFrame=new GameFrame(1024,768);
        gameFrame.connectToServer();
        gameFrame.setUpGUI();
    }
}
