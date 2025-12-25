package Server;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int port = 12345;

    public static void main(String[] args){

        try{
            ServerSocket serverSocket = new ServerSocket(port);

            //Server will wait for client all the time
            while(true){
                //Accept when there is a request from a client
                Socket clientSocket = serverSocket.accept();

                //Thread to take care of the client
                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
