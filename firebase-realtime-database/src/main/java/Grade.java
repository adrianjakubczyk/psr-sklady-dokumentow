public class Grade {
    public String subject;
    public Double value;


    public Grade(String subject, Double value) {
        this.subject = subject;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Subject: " + subject + "; Value: " + value + ";";
    }
}
