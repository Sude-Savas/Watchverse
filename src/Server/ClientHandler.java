package Server;

import Model.Item;
import Services.ApiManager;
import Services.AuthService;
import Model.AuthResult;
import Services.WatchlistService;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

//Implemented Runnable to use it as a thread
public class ClientHandler implements Runnable {

    private Socket socket;
    private ApiManager apiManager;
    private AuthService authService;
    private WatchlistService watchlistService;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.apiManager = new ApiManager(); //For every client a new Api Manager
        this.authService = new AuthService();
        this.watchlistService = new WatchlistService();
    }

    @Override
    public void run() {
        try {
            //Opening both channels
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            //Waiting for a response from the client
            Object receivedMessage;
            while ((receivedMessage = in.readObject()) != null) {
                String request = (String) receivedMessage;

                //For protocol
                String[] parts = request.split(":");
                String command = parts[0]; //First part is always the command

                switch (command) {

                    case "SEARCH":
                        if (parts.length >= 3) {
                            String title = parts[1]; //Title
                            String type = parts[2];  //Movie or Series

                            //Asking API
                            String JsonResponse = apiManager.search(title, type);
                            //Writing results to the list
                            List<Item> results = apiManager.parseResponse(JsonResponse, type);
                            //Results back to client
                            out.writeObject(results);
                            out.flush();
                        }
                        break;

                    case "LOGIN":
                        // Protocol: LOGIN:username:password
                        if (parts.length >= 3) {
                            String uName = parts[1];
                            String pass = parts[2];

                            AuthResult result = authService.login(uName, pass);

                            // Directly sends enum
                            out.writeObject(result.toString());
                            out.flush();
                        }
                        break;

                    case "REGISTER":
                        // Protocol: REGISTER:username:password:question:answer
                        if (parts.length >= 5) {

                            AuthResult result = authService.register(parts[1], parts[2], parts[3], parts[4]);

                            out.writeObject(result.toString());
                            out.flush();
                        }
                        break;


                    case "CREATE_LIST":
                        // Protocol: CREATE_LIST:username:listName
                        if (parts.length >= 3) {
                            boolean created = watchlistService.createWatchlist(parts[1], parts[2]);

                            //Sends fail or success message to the user
                            out.writeObject(created ? "SUCCESS" : "FAIL");
                            out.flush();
                        }
                        break;


                    case "GET_MY_LISTS":
                        // Protocol: GET_MY_LISTS:username
                        if (parts.length >= 2) {
                            List<String> myLists = watchlistService.getUserWatchlists(parts[1]);
                            // Sending List<String> object back
                            out.writeObject(myLists);
                            out.flush();
                        }
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Connection error");
        }
    }
}
