package Server;
import Model.Item;
import Services.ApiManager;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

//Implemented Runnable to use it as a thread
public class ClientHandler implements Runnable {

    private Socket socket;
    private ApiManager apiManager;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.apiManager = new ApiManager(); //For every client a new Api Manager
    }

    @Override
    public void run() {
        try {
            //Opening both channels
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            //Waiting for a response from the client
            Object receivedMessage;
            while ((receivedMessage=in.readObject())!=null){
                String request = (String) receivedMessage;

                //For protocol
                String[] parts = request.split(":");

                if(parts.length>= 3 && parts[0].equals("SEARCH")){
                    String title = parts[1]; //Title
                    String type = parts[2];  //Movie or Series

                    //Asking API
                    String JsonResponse = apiManager.search(title,type);
                    //Writing results to the list
                    List<Item> results = apiManager.parseResponse(JsonResponse,type);
                    //Results back to client
                    out.writeObject(results);
                    out.flush();
                }
            }
        }
        catch (Exception e){
            System.out.println("Connection error");
        }
    }
}
