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
    private int p1x,p1y,p1d,p2d,p1ix,p1imx,p2x,p2y,p2ix,p2imx; //x and y coords for players

    public GameServer(){
        System.out.println("==== GAME SERVER ====");
        numPlayers = 0;
        maxPlayers = 2;

        try {
            ss = new ServerSocket(11037);
        } catch (IOException ex) {
            System.out.println("IOException from GameServer constructor");
        }
        System.out.println("za bluetooth device isa connectedu succesfullay");
    }


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

    private class ReadFromClient implements Runnable{

        private int playerID;
        private DataInputStream dataIn;

        public ReadFromClient (int pid, DataInputStream in){
            playerID = pid;
            dataIn = in;
            System.out.println("ReadFromClient"+playerID+"Runnable created");
        }
        public void run(){
            try{
                while (true){
                    if (playerID ==1)
                    {
                        p1x = dataIn.readInt();
                        p1y = dataIn.readInt();
                        p1d=dataIn.readInt();
                        p1ix=dataIn.readInt();
                        p1imx=dataIn.readInt();
                    }
                    else
                    {
                        p2x = dataIn.readInt();
                        p2y = dataIn.readInt();
                        p2d=dataIn.readInt();
                        p2ix=dataIn.readInt();
                        p2imx=dataIn.readInt();
                    }
                }

            } catch (IOException ex) {
                System.out.println("IOEx from RFC run()");
            }
        }
    }

    private class WriteToClient implements Runnable{

        private int playerID;
        private DataOutputStream dataOut;

        public WriteToClient (int pid, DataOutputStream out){
            playerID = pid;
            dataOut = out;
            System.out.println("WriteToClient"+playerID+"Runnable created");
        }
        public void run(){
            try{
                while (true){
                    if (playerID ==1)
                    {
                        dataOut.writeInt(p2x);
                        dataOut.writeInt(p2y);
                        dataOut.writeInt(p2d);
                        dataOut.writeInt(p2ix);
                        dataOut.writeInt(p2imx);
                        dataOut.flush();
                    }
                    else
                    {
                        dataOut.writeInt(p1x);
                        dataOut.writeInt(p1y);
                        dataOut.writeInt(p1d);
                        dataOut.writeInt(p1ix);
                        dataOut.writeInt(p1imx);
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

        public void sendStartMsg(){
            try{
                dataOut.writeUTF("We now have 2 players...heh..");
            }catch(IOException ex){
                System.out.println("IOException from sendStartMsg()");
            }
        }
    }

    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.acceptConnections();
    }
}
