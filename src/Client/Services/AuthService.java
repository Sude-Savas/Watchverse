package Client.Services;

import Model.AuthResult;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AuthService {

    // Constructor artık boş kalabilir, çünkü bağlantıyı metodun içinde açacağız
    public AuthService() {
    }

    public AuthResult login(String username, String password) {
        // Senin projendeki diğer yerler (AddGroup vb.) gibi try-with-resources kullanıyoruz.
        // İşlem bitince soket otomatik kapanır.
        try (Socket socket = new Socket("localhost", 12345); // Port numaran 12345
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // Sunucuya "Ben giriş yapıyorum" diyoruz
            out.writeObject("LOGIN###" + username + "###" + password);
            out.flush();

            // Cevabı okuyoruz
            String response = (String) in.readObject();
            return AuthResult.valueOf(response);

        } catch (Exception e) {
            e.printStackTrace();
            return AuthResult.ERROR;
        }
    }

    // Register için de aynısını yapabilirsin
    public AuthResult register(String username, String password, String question, String answer) {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

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