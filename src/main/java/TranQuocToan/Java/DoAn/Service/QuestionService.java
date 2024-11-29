package TranQuocToan.Java.DoAn.Service;

import TranQuocToan.Java.DoAn.Model.Question;
import TranQuocToan.Java.DoAn.Repository.IQuestionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

@RequiredArgsConstructor

public class QuestionService implements IQuestionService {

    private final IQuestionRepository questionRepository;

    @Override
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    @Override
    public List<String> getAllsubject() {
        return questionRepository.findDistinctsubject();
    }

    @Override
    public Question updateQuestion(Long id, Question question) throws ChangeSetPersister.NotFoundException {
        Optional<Question> theQuestion = this.getQuestionById(id);
        if (theQuestion.isPresent()) {
            Question updatedQuestion = theQuestion.get();
            updatedQuestion.setQuestion(question.getQuestion());
            updatedQuestion.setChoices(question.getChoices());
            updatedQuestion.setCorrectAnswers(question.getCorrectAnswers());
            return questionRepository.save(updatedQuestion);
        } else {
            throw new ChangeSetPersister.NotFoundException();
        }
    }

    @Override
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
        resetIds(); // Sắp xếp lại ID
    }

    @Override
    public List<Question> getQuestionForUser(Integer numOfQuestions, String subject) {
        Pageable pageable = PageRequest.of(0, numOfQuestions);
        return questionRepository.findBysubject(subject, pageable).getContent();
    }


    @Transactional
    public void resetIds() {
        List<Question> questions = questionRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        Long newId = 1L; // ID bắt đầu từ 1
        for (Question question : questions) {
            question.setId(newId); // Gán lại ID
            questionRepository.save(question); // Lưu thay đổi
            newId++;
        }
    }

}