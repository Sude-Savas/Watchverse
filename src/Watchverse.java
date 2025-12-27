import Server.Server;
import Client.Client;

//--------------------- Watchverse App ------------------------

public class Watchverse {
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                Server.main(new String[]{}); //Starting the server first
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        try {
            Thread.sleep(1000); //Waiting a bit for the server to start
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Starting the client
        Client.main(new String[]{});
    }
}