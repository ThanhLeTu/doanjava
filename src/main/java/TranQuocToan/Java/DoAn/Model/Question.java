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
    private String subject;

    @NotBlank
    private String questionType;

    @ElementCollection
    private List<String> choices;

    @ElementCollection
    private List<String> correctAnswers;

    
}
