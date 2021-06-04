import java.util.ArrayList;
import java.util.List;

public class Student {
    public String firstName;
    public String lastName;
    public Integer age;
    public List<Grade> grades;

    public Student() {
    }

    public Student(String firstName, String lastName, Integer age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        grades = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "First name: " + firstName + "; Last name: " + lastName + "; Age: " + age + "; Grades: " + grades;
    }

}
