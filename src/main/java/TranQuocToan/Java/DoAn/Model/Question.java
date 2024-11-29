package TranQuocToan.Java.DoAn.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data

@Entity
public class Question {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   @NotBlank(message = "")
   private String question;
   @NotBlank
   private String subject = "Default Subject";

   @NotBlank
   private String questionType = "single";
   @ElementCollection
   private List<String> choices;
   @ElementCollection
   private List<String> correctAnswers;

   public String getQuestionText() {
      return question;
   }

   public void setQuestionText(String question) {
      this.question = question;
   }

   // Getter và Setter cho 'choices'
   public List<String> getChoices() {
      return choices;
   }

   public void setChoices(List<String> choices) {
      this.choices = choices;
   }


}
