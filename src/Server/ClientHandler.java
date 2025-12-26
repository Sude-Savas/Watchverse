package Server;

import Model.Item;
import Services.ApiManager;
import Services.AuthService;
import Model.AuthResult;
import Services.WatchlistService;
import Model.PublicWatchlist;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
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
                        if (parts.length >= 2) {
                            String title = parts[1]; //Title

                            //default multi
                            String type = (parts.length >= 3) ? parts[2] : "multi";

                            //Asking API
                            String JsonResponse = apiManager.search(title, type);
                            List<Item> results = apiManager.parseResponse(JsonResponse, type);
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
                        if (parts.length >= 4) {
                            String username = parts[1];
                            String listName = parts[2];
                            String visibility = parts[3];

                            boolean created = watchlistService.createWatchlist(username, listName, visibility);

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

                    case "ADD_ITEM":
                        if (parts.length >= 8) {
                            String user = parts[1];
                            String list = parts[2];

                            String posterUrl = parts[7].equals("null") ? null : parts[7];

                            Item newItem = new Item(parts[3], parts[4], parts[5], parts[6], posterUrl);

                            String result = watchlistService.addItem(user, list, newItem);
                            out.writeObject(result);
                            out.flush();
                        }
                        break;

                    case "REMOVE_ITEM":
                        // Protocol: REMOVE_ITEM:username:listName:apiId
                        if (parts.length >= 4) {
                            String user = parts[1];
                            String list = parts[2];
                            String apiId = parts[3];

                            boolean removed = watchlistService.removeItem(user, list, apiId);

                            out.writeObject(removed ? "SUCCESS" : "FAIL");
                            out.flush();
                        }
                        break;

                    case "GET_LIST_ITEMS":
                        // Protocol: GET_LIST_ITEMS:username:listName
                        if (parts.length >= 3) {
                            List<Item> items = watchlistService.getListItems(parts[1], parts[2]);

                            // Sending List<Item> object back
                            out.writeObject(items);
                            out.flush();
                        }
                        break;

                    case "GET_PUBLIC_LISTS":
                        List<PublicWatchlist> publicLists =
                                watchlistService.getPublicWatchlists();

                        out.writeObject(publicLists);
                        out.flush();
                        break;

                    case "GET_PUBLIC_LIST_ITEMS":
                        // Protocol: GET_PUBLIC_LIST_ITEMS:listId
                        if (parts.length >= 2) {
                            int listId = Integer.parseInt(parts[1]);

                            List<Item> items =
                                    watchlistService.getPublicListItemsById(listId);

                            out.writeObject(items);
                            out.flush();
                        }
                        break;

                    case "CREATE_GROUP":
                        // Protocol: CREATE_GROUP:username:groupName
                        if (parts.length >= 3) {
                            String username = parts[1];
                            String groupName = parts[2];
                            boolean created = watchlistService.createGroup(username, groupName);
                            out.writeObject(created ? "SUCCESS" : "FAIL");
                            out.flush();
                        }
                        break;

                    case "GET_MY_GROUPS":
                        // Protocol: GET_MY_GROUPS:username
                        if (parts.length >= 2) {
                            List<String> myGroups = watchlistService.getUserGroups(parts[1]);
                            out.writeObject(myGroups);
                            out.flush();
                        }
                        break;

                    case "GET_LINK_ONLY_LISTS":
                        // Protocol: GET_LINK_ONLY_LISTS:username
                        if (parts.length >= 2) {
                            List<String> lists = watchlistService.getLinkOnlyLists(parts[1]);
                            out.writeObject(lists);
                            out.flush();
                        }
                        break;

                    case "ADD_LIST_TO_GROUP":
                        // Protocol: ADD_LIST_TO_GROUP:username:groupName:listName
                        if (parts.length >= 4) {
                            String username = parts[1];
                            String groupName = parts[2];
                            String listName = parts[3];

                            boolean success = watchlistService.addListToGroup(username, groupName, listName);
                            out.writeObject(success ? "SUCCESS" : "FAIL");
                            out.flush();
                        }
                        break;

                    case "GET_GROUP_WATCHLISTS":
                        // Protocol: GET_GROUP_WATCHLISTS:username:groupName
                        if (parts.length >= 3) {
                            List<String> lists = watchlistService.getGroupWatchlists(parts[1], parts[2]);
                            out.writeObject(lists);
                            out.flush();
                        }
                        break;

                    case "GET_SHARED_LIST_ITEMS":
                        // Protocol: GET_SHARED_LIST_ITEMS:listId
                        if (parts.length >= 2) {
                            int listId = Integer.parseInt(parts[1]);
                            List<Item> items = watchlistService.getSharedListItems(listId);
                            out.writeObject(items);
                            out.flush();
                        }
                        break;

                    case "GET_LIST_VISIBILITY":
                        // Protocol: GET_LIST_VISIBILITY:username:listName
                        if (parts.length >= 3) {
                            String uName = parts[1];
                            String lName = parts[2];
                            try {
                                String visibility = watchlistService.getWatchlistVisibility(uName, lName);
                                out.writeObject(visibility);
                                out.flush();
                            } catch (SQLException e) {
                                out.writeObject("PRIVATE"); // IF there is errors, use default
                            }
                        }
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Connection error");
        }
    }
}