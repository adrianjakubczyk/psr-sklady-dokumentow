import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args) {
        MongoClient mongoClient = new MongoClient("localhost",27017);

        MongoDatabase db = mongoClient.getDatabase("deansOffice");

        db.getCollection("students").drop();

        MongoCollection<Document> collection = db.getCollection("students");

        Menu menu = new Menu(collection);
        try{
            menu.selectOperation();
        } catch (InputMismatchException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        mongoClient.close();

    }
}
