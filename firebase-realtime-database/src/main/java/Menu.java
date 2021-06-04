import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Menu {
    final private Scanner scanner = new Scanner(System.in);
    final private DatabaseReference database;

    public Menu(DatabaseReference database) {
        this.database = database;
    }

    private void printMenu() {
        int i = 1;
        System.out.println("===================================================");
        System.out.println("Please choose operation:");
        System.out.println(i++ + ": Add Student");
        System.out.println(i++ + ": Add Student's grade");
        System.out.println(i++ + ": Select by ID");
        System.out.println(i++ + ": Select by name");
        System.out.println(i++ + ": Select All");
        System.out.println(i++ + ": Delete by ID");
        System.out.println(i + ": Calculate average age");

        System.out.println("9: Exit");
        System.out.println("===================================================");
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
                    System.out.println("Calculate average age");
                    calculateAvgAge();
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
        scanner.nextLine();

        Student student = new Student(firstName, lastName, age);

        CountDownLatch studentAdded = new CountDownLatch(1);

        database.child("students").push().setValue(student, new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                studentAdded.countDown();
            }
        });
        try {
            studentAdded.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Added " + student);

    }

    private void addGrade() {
        System.out.println("Student's id:");
        String id = scanner.nextLine();

        CountDownLatch checkExists = new CountDownLatch(1);
        final Boolean[] exists = new Boolean[1];
        final long[] gradeId = new long[1];
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                exists[0] = dataSnapshot.child("students").child(String.valueOf(id)).exists();
                gradeId[0] = dataSnapshot.child("students").child(String.valueOf(id)).child("grades").getChildrenCount();
                checkExists.countDown();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        try {
            checkExists.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (exists[0]) {
            System.out.println("Subject:");
            String subject = scanner.nextLine();
            System.out.println("Grade:");
            Double value = scanner.nextDouble();
            scanner.nextLine();
            Grade grade = new Grade(subject, value);


            CountDownLatch gradeAdded = new CountDownLatch(1);
            database.child("students").child(id).child("grades").child(String.valueOf(gradeId[0])).setValue(grade, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    gradeAdded.countDown();
                }
            });

            try {
                gradeAdded.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Added " + grade);
        } else {
            System.out.println("Student with id:" + id + " not found");
        }


    }

    private void selectById() {
        System.out.println("Student's id:");
        String id = scanner.nextLine();

        CountDownLatch done = new CountDownLatch(1);
        database.child("students").orderByKey().equalTo(id).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 1) {
                    System.out.println("Not found");
                } else {
                    System.out.println(dataSnapshot.getValue());
                }

                done.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void selectByName() {
        System.out.println("Student First Name:");
        String name = scanner.nextLine();


        CountDownLatch done = new CountDownLatch(1);
        database.child("students").orderByChild("firstName").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 1) {
                    System.out.println("Not found");
                } else {
                    Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                    iterable.forEach(ds -> System.out.println(ds));
                }

                done.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void selectAll() {
        CountDownLatch done = new CountDownLatch(1);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> iterable = dataSnapshot.child("students").getChildren();
                iterable.forEach(ds -> {
                    System.out.println(ds);
                });
                done.countDown();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void deleteById() {
        System.out.println("Student's id:");
        String id = scanner.nextLine();

        CountDownLatch done = new CountDownLatch(1);

        database.child("students").child(id).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                System.out.println("deleted");
                done.countDown();
            }
        });


        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void calculateAvgAge() {

        CountDownLatch done = new CountDownLatch(1);

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Map<String, Object>> data = new ArrayList<>();
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.child("students").getChildren();
                dataSnapshots.forEach(ds1 -> {
                    data.add((Map<String, Object>) ds1.getValue());
                });

                int sum = 0;
                for (Map<String, Object> m : data) {
                    System.out.println(m.get("age"));
                    sum += Integer.parseInt(m.get("age").toString());
                }
                System.out.println("Average age: " + sum / (double) data.size());

                done.countDown();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError);
                done.countDown();
            }
        });

        try {
            done.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
