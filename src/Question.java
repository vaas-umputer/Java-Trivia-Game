// Question.java
import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private String question;
    private List<String> options;
    private int correctAnswerIndex;

    public Question(String question, List<String> options) {
        this.question = question;
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }
}