package TranQuocToan.Java.DoAn.Controller;

import TranQuocToan.Java.DoAn.Model.Question;
import TranQuocToan.Java.DoAn.Repository.IQuestionRepository;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/api")
public class UploadFileController {

    @Autowired
    private IQuestionRepository questionRepository;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return "No file uploaded!";
        }

        try (InputStream inputStream = file.getInputStream()) {
            XWPFDocument doc = new XWPFDocument(inputStream);
            List<String> currentChoices = new ArrayList<>();
            Question currentQuestion = null;

            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                String text = paragraph.getText().trim();

                // Kiểm tra nếu đoạn văn là câu hỏi
                if (text.matches("^\\d+\\..*")) {
                    // Lưu câu hỏi trước đó (nếu có)
                    if (currentQuestion != null) {
                        currentQuestion.setChoices(new ArrayList<>(currentChoices));
                        questionRepository.save(currentQuestion);
                        currentChoices.clear();
                    }

                    // Tạo câu hỏi mới
                    String questionText = text.substring(text.indexOf('.') + 1).trim();
                    currentQuestion = new Question();
                    currentQuestion.setQuestionText(questionText);

                } else if (text.matches("^[A-D]\\..*")) {
                    // Nếu là lựa chọn, thêm vào danh sách
                    String choiceText = text.substring(2).trim();
                    currentChoices.add(choiceText);
                }
            }

            // Lưu câu hỏi cuối cùng (nếu có)
            if (currentQuestion != null) {
                currentQuestion.setChoices(currentChoices);
                questionRepository.save(currentQuestion);
            }

            return "File uploaded and processed successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing file: " + e.getMessage();
        }
    }


    // API để upload file và trả về danh sách đáp án
    @PostMapping("/upload-answer")
    public List<String> uploadAnswerFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("No file uploaded!");
        }

        try (InputStream inputStream = file.getInputStream()) {
            XWPFDocument doc = new XWPFDocument(inputStream);
            return extractAnswersFromTable(doc);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing file: " + e.getMessage());
        }
    }

    // Hàm xử lý tách đáp án từ file
    private List<String> extractAnswersFromTable(XWPFDocument doc) {
        List<String> answers = new ArrayList<>();
        List<XWPFTable> tables = doc.getTables();

        if (tables.isEmpty()) {
            return answers; // Không có bảng
        }

        XWPFTable table = tables.get(0); // Giả sử chỉ xử lý bảng đầu tiên

        for (int i = 1; i < table.getRows().size(); i++) { // Bỏ qua hàng tiêu đề nếu có
            XWPFTableRow row = table.getRow(i);

            // Kiểm tra nếu hàng có đủ cột và cột đáp án không trống
            if (row.getTableCells().size() > 1) {
                String answer = row.getCell(1).getText().trim();
                if (!answer.isEmpty()) {
                    answers.add(answer);
                }
            }
        }

        return answers; // Trả về danh sách đáp án
    }

}
