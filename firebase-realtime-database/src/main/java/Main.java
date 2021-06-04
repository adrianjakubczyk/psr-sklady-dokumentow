import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) {

        try {
            FileInputStream serviceAccount =
                    new FileInputStream("./serviceAccountKey.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://psr-sklady-dokumentow-default-rtdb.europe-west1.firebasedatabase.app/")
                    .build();

            FirebaseApp.initializeApp(options);

            final CountDownLatch done = new CountDownLatch(1);

            DatabaseReference database = FirebaseDatabase.getInstance().getReference("deansOffice");

            Menu menu = new Menu(database);
            menu.selectOperation();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
