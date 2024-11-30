package TranQuocToan.Java.DoAn.Repository;

import TranQuocToan.Java.DoAn.Model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IQuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q FROM Question q WHERE LOWER(q.question) = LOWER(:questionText)")
    Optional<Question> findByQuestionTextIgnoreCase(@Param("questionText") String questionText);

    @Query("SELECT DISTINCT q.subject FROM Question q")
    List<String> findDistinctsubject();

    Page<Question> findBysubject(String subject, Pageable pageable);
    @Query("SELECT MAX(q.id) FROM Question q")
    Optional<Long> findMaxId();


}
