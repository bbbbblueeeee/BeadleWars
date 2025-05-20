/**
The gameserver class acts as the server to host the clients. It handles port number, server socket, datainput and dataoutput creations.
 It is in charge of reading client's inputs and writing them out to the other to reflect changes in real time.

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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class GameServer {

    private ServerSocket ss;
    private int numPlayers,maxPlayers;
    private Socket p1Socket,p2Socket;
    private ReadFromClient p1ReadRunnable,p2ReadRunnable;
    private WriteToClient p1WriteRunnable,p2WriteRunnable;
    private int p1x,p1y,p1d,p1b,p1ix,p1imx,p2x,p2y,p2d,p2b,p2ix,p2imx,p1color,p2color,p1points,p2points; //x and y coords for players
    private Boolean p1start,p2start;

    /**
     initializes the variables needed for this class
     **/
    public GameServer(){
        System.out.println("==== GAME SERVER ====");
        numPlayers = 0;
        maxPlayers = 2;
        p1start = false;
        p2start = false;


        try {
            ss = new ServerSocket(11037);
        } catch (IOException ex) {
            System.out.println("IOException from GameServer constructor");
        }
        System.out.println("za bluetooth device isa connectedu succesfullay");
    }


    /**
     accepting clients aslong as the max num of players hasn't been reached and creating new threads for each one
     **/
    public void acceptConnections() {
        try {
            System.out.println("accepting connections...");
            while (numPlayers < maxPlayers) {
                Socket s = ss.accept();
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                numPlayers++;
                out.writeInt(numPlayers);
                System.out.println("Player #"+numPlayers+" has joined.");

                ReadFromClient rfc = new ReadFromClient(numPlayers, in);
                WriteToClient wtc = new WriteToClient(numPlayers, out);

                if (numPlayers ==1)
                {
                    p1Socket = s;
                    p1ReadRunnable = rfc;
                    p1WriteRunnable = wtc;
                }
                else
                {
                    p2Socket = s;
                    p2ReadRunnable = rfc;
                    p2WriteRunnable = wtc;
                    p1WriteRunnable.sendStartMsg();
                    p2WriteRunnable.sendStartMsg();

                    Thread readThread1 = new Thread(p1ReadRunnable);
                    Thread readThread2 = new Thread(p2ReadRunnable);
                    readThread1.start();
                    readThread2.start();

                    Thread writeThread1 = new Thread(p1WriteRunnable);
                    Thread writeThread2 = new Thread(p2WriteRunnable);
                    writeThread1.start();
                    writeThread2.start();
                }
            }
            System.out.println("No longer accepting connections.");
        } catch (IOException ex) {
            System.out.println("IOEx from acceptConnections method");
        }
    }

    /**
     Readfromclient class implements runnable and handles the reading information from the clients
     **/
    private class ReadFromClient implements Runnable{

        private int playerID;
        private DataInputStream dataIn;
        /**
            initializes the needed variables
         **/
        public ReadFromClient (int pid, DataInputStream in){
            playerID = pid;
            dataIn = in;
            System.out.println("ReadFromClient"+playerID+"Runnable created");
        }

        /**
            runs the class
         **/
        public void run(){
            try{
                while (true){
                    if (playerID ==1)
                    {
                        p1x = dataIn.readInt();
                        p1y = dataIn.readInt();
                        p1d=dataIn.readInt();
                        p1b=dataIn.readInt();
                        p1ix=dataIn.readInt();
                        p1imx=dataIn.readInt();
                        p1color = dataIn.readInt();
                        p1start = dataIn.readBoolean();
                        p1points = dataIn.readInt();
                    }
                    else
                    {
                        p2x = dataIn.readInt();
                        p2y = dataIn.readInt();
                        p2d=dataIn.readInt();
                        p2b=dataIn.readInt();
                        p2ix=dataIn.readInt();
                        p2imx=dataIn.readInt();
                        p2color = dataIn.readInt();
                        p2start = dataIn.readBoolean();
                        p2points = dataIn.readInt();
                    }
                }

            } catch (IOException ex) {
                System.out.println("IOEx from RFC run()");
            }
        }
    }

    /**
    Writetoclient class implements runnable and writes out the information to each client
     **/
    private class WriteToClient implements Runnable{

        private int playerID;
        private DataOutputStream dataOut;
        /**
        initializes needed variables
         **/
        public WriteToClient (int pid, DataOutputStream out){
            playerID = pid;
            dataOut = out;
            System.out.println("WriteToClient"+playerID+"Runnable created");
        }
        /**
            runs the class
         **/
        public void run(){
            try{
                while (true){
                    if (playerID ==1)
                    {
                        dataOut.writeInt(p2x);
                        dataOut.writeInt(p2y);
                        dataOut.writeInt(p2d);
                        dataOut.writeInt(p2b);
                        dataOut.writeInt(p2ix);
                        dataOut.writeInt(p2imx);
                        dataOut.writeInt(p2color);
                        dataOut.writeBoolean(p2start);
                        dataOut.writeInt(p2points);
                        dataOut.flush();
                    }
                    else
                    {
                        dataOut.writeInt(p1x);
                        dataOut.writeInt(p1y);
                        dataOut.writeInt(p1d);
                        dataOut.writeInt(p1b);
                        dataOut.writeInt(p1ix);
                        dataOut.writeInt(p1imx);
                        dataOut.writeInt(p1color);
                        dataOut.writeBoolean(p1start);
                        dataOut.writeInt(p1points);
                        dataOut.flush();
                    }
                    try{
                        Thread.sleep(25);
                    }catch (InterruptedException ex){
                        System.out.println("InterruptedException from WTC run()");
                    }
                }


            } catch (IOException ex) {
                System.out.println("IOEx from WTC run()");
            }
        }

        /**
        sends a starting message once the max number ofplayers has joined
         **/
        public void sendStartMsg(){
            try{
                dataOut.writeUTF("We now have 2 players...heh..");
            }catch(IOException ex){
                System.out.println("IOException from sendStartMsg()");
            }
        }
    }

    /**
        Main method to start
     **/
    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.acceptConnections();
    }
}
