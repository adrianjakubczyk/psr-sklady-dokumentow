import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.*;

public class Menu {
    final private MongoCollection<Document> collection;
    final private Scanner scanner = new Scanner(System.in);

    private void printMenu() {
        int i=1;
        System.out.println("===================================================");
        System.out.println("Please choose operation:");
        System.out.println(i+++": Add Student");
        System.out.println(i+++": Add Student's grade");
        System.out.println(i+++": Select by ID");
        System.out.println(i+++": Select by name");
        System.out.println(i+++": Select All");
        System.out.println(i+++": Delete by ID");
        System.out.println(i+++": Calculate average age (server side)");
        System.out.println(i+": Calculate average age (client side)");

        System.out.println("9: Exit");
        System.out.println("===================================================");
    }

    public Menu(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    public void selectOperation() {
        boolean doExit = false;
        while (!doExit) {
            printMenu();
            int op = scanner.nextInt();
            scanner.nextLine();
            switch (op) {
                case 1:
                    System.out.println("Adding student");
                    addStudent();
                    break;
                case 2:
                    System.out.println("Adding grade");
                    addGrade();
                    break;
                case 3:
                    System.out.println("Select by ID");
                    selectById();
                    break;
                case 4:
                    System.out.println("Select by name");
                    selectByName();
                    break;
                case 5:
                    System.out.println("Select All");
                    selectAll();
                    break;
                case 6:
                    System.out.println("Delete");
                    deleteById();
                    break;
                case 7:
                    System.out.println("Calculate average age (server side)");
                    calculateAvgAge();
                    break;
                case 8:
                    System.out.println("Calculate average age (client side)");
                    calculateAvgAgeClient();
                    break;
                case 9:
                    System.out.println("Exit");
                    doExit = true;
                    break;
                default:
                    System.out.println("Not a recognizable choice");
                    break;
            }
        }

    }

    private void addStudent() {
        System.out.println("Student's first name:");
        String firstName = scanner.nextLine();
        System.out.println("Student's last name:");
        String lastName = scanner.nextLine();
        System.out.println("Student's age:");
        int age = scanner.nextInt();

        long id = collection.countDocuments()+1;

        Document student = new Document("_id", id)
                .append("lastname", lastName)
                .append("name", firstName)
                .append("age", age);
        collection.insertOne(student);
    }

    private void addGrade() {
        System.out.println("Student's id:");
        long id = scanner.nextLong();
        scanner.nextLine();

        Document student = collection.find(eq("_id",id)).first();

        if(student!=null){
            System.out.println(student.toJson());
            System.out.println("Subject:");
            String subject = scanner.nextLine();
            System.out.println(subject);
            System.out.println("Grade:");
            float grade = scanner.nextFloat();
            System.out.println(grade);

            Document newGrade = new Document().append(subject,grade);

            collection.updateOne(eq("_id",id), Updates.addToSet("grades",newGrade));
        }else{
            System.out.println("This student doesn't exist");
        }
    }

    private void selectById(){
        System.out.println("Student's id:");
        long id = scanner.nextLong();

        Document student = collection.find(eq("_id",id)).first();

        if(student!=null){
            System.out.println(student.toJson());
        }else{
            System.out.println("This student doesn't exist");
        }
    }

    private void selectByName(){
        System.out.println("Student's first name:");
        String name = scanner.nextLine();
        System.out.println("Student's last name:");
        String lastName = scanner.nextLine();

        Document student = collection.find(and(eq("lastname",lastName),eq("name",name))).first();

        if(student!=null){
            System.out.println(student.toJson());
        }else{
            System.out.println("This student doesn't exist");
        }
    }

    private void selectAll(){
        for (Document doc : collection.find())
            System.out.println(doc.toJson());
    }

    private void deleteById(){
        System.out.println("Student's id:");
        long id = scanner.nextLong();

        DeleteResult deleted = collection.deleteOne(eq("_id",id));

        System.out.println("Deleted "+deleted.getDeletedCount()+" elements");
    }

    private void calculateAvgAge(){

        AggregateIterable<Document> avg = collection.aggregate(Arrays.asList(Aggregates.group("_id", new BsonField("averageAge", new BsonDocument("$avg", new BsonString("$age"))))));
        for(Document d : avg){
            System.out.println(d.toJson());
        }
    }

    private void calculateAvgAgeClient(){
        int count = 0;
        int sum = 0;
        for (Document doc : collection.find()){
            System.out.println(doc.toJson());
            sum+=doc.getInteger("age");
            count++;
        }
        System.out.println("average age = "+sum/(double)count);
    }

}
