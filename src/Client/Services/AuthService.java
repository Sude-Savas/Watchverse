package Client.Services;

import Model.AuthResult;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AuthService {

    //Connection will be in the method
    public AuthService() {
    }

    //Sends a login request to the server and returns the authentication result.
    public AuthResult login(String username, String password) {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // Send login request using the protocol: LOGIN###username###password
            out.writeObject("LOGIN###" + username + "###" + password);
            out.flush();

            //Reading the response from the server
            String response = (String) in.readObject();
            return AuthResult.valueOf(response);

        } catch (Exception e) {
            e.printStackTrace();
            return AuthResult.ERROR;
        }
    }

    //Sends a registration request to the server with security credentials.
    public AuthResult register(String username, String password, String question, String answer) {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // Protocol: REGISTER###username###password###question###answer
            out.writeObject("REGISTER###" + username + "###" + password + "###" + question + "###" + answer);
            out.flush();

            String response = (String) in.readObject();
            return AuthResult.valueOf(response);

        } catch (Exception e) {
            e.printStackTrace();
            return AuthResult.ERROR;
        }
    }
}