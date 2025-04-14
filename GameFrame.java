import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameFrame extends JFrame {

    private int width,height;
    private Container contentPane;
    private Player me;
    private Timer animationTimer;
    private boolean up,down,left,right;

    public GameFrame(int w,int h){
        width=w;
        height=h;
    }

    public void setUpGUI(){
        contentPane=this.getContentPane();
        this.setTitle("");
        contentPane.setPreferredSize(new Dimension(width,height));
        createPlayer();
        //make image of player visible on screen
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);

        setUpTimer();
        setUpKeyListener();
    }

    private void createPlayer(){
        me=new Player(100,400,1);
    }

    private void setUpTimer(){
        int interval=10;
        ActionListener actionListener=new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double speed=5;
                if (up) {
                    me.moveV(-speed);
                }
                if (down) {
                    me.moveV(speed);
                }
                if (left) {
                    me.moveH(-speed);
                }
                if(right){
                    me.moveH(speed);
                }
                //equivalent of repaint
            }
        };
        animationTimer=new Timer(interval,actionListener);
        animationTimer.start();
    }

    private void setUpKeyListener(){
        KeyListener keyListener=new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode=e.getKeyCode();

                switch (keyCode){
                    case KeyEvent.VK_UP:
                        up=true;
                        break;
                    case KeyEvent.VK_DOWN:
                        down=true;
                        break;
                    case KeyEvent.VK_LEFT:
                        left=true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        right=true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode=e.getKeyCode();

                switch (keyCode){
                    case KeyEvent.VK_UP:
                        up=false;
                        break;
                    case KeyEvent.VK_DOWN:
                        down=false;
                        break;
                    case KeyEvent.VK_LEFT:
                        left=false;
                        break;
                    case KeyEvent.VK_RIGHT:
                        right=false;
                        break;
                }
            }
        };
        contentPane.addKeyListener(keyListener);
        contentPane.setFocusable(true);
    }
}
