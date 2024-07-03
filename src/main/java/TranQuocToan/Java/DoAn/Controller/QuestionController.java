package TranQuocToan.Java.DoAn.Controller;

import TranQuocToan.Java.DoAn.Model.Question;
import TranQuocToan.Java.DoAn.Service.IQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.*;

import static org.springframework.http.HttpStatus.CREATED;


@CrossOrigin("http://localhost:5173")
@Controller
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/quizzes")
public class QuestionController {
    private final  IQuestionService questionService;

    @PostMapping("/create-new-question")
    public ResponseEntity<Question> createQuestion(@Valid @RequestBody Question question){
        Question createdQuestion = questionService.createQuestion(question);
        return ResponseEntity.status(CREATED).body(createdQuestion);
    }
    @GetMapping("/all-questions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        List<Question> questions   = questionService.getAllQuestions();

        return ResponseEntity.ok(questions);
    }


    @GetMapping("/question/{id}")

    public  ResponseEntity<Question> getQuestionById (@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        Optional<Question> theQuestion = questionService.getQuestionById(id);
        if(theQuestion.isPresent()) {
            return ResponseEntity.ok(theQuestion.get());
        } else  {
            throw new ChangeSetPersister.NotFoundException();
        }
    }

    @PutMapping("/question/{id}/update")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable Long id, @RequestBody Question question) throws ChangeSetPersister.NotFoundException {
        Question updatedQuestion = questionService.updateQuestion(id, question);

        return ResponseEntity.ok(updatedQuestion);


    }

    @DeleteMapping("/question/{id}/delete")
    public ResponseEntity<Void> deleteQuestion (@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/subjects")
    public  ResponseEntity<List<String>> getAllSubjects() {
        List<String> subjects = questionService.getAllSubject();

        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/quiz/fetch-questions-for-user")
    public ResponseEntity<List<Question>> getQuestionsForUser(
            @RequestParam Integer numOfQuestions, @RequestParam String subject){
        List<Question> allQuestions = questionService.getQuestionForUser(numOfQuestions, subject);

        List<Question> mutableQuestions = new ArrayList<>(allQuestions);
        Collections.shuffle(mutableQuestions);

        int availableQuestions = Math.min(numOfQuestions, mutableQuestions.size());
        List<Question> randomQuestions = mutableQuestions.subList(0, availableQuestions);
        return ResponseEntity.ok(randomQuestions);
    }


 }
