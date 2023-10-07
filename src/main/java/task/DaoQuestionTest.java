package task;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DaoQuestionTest {
    private General dao;

    private String jdbcUrl = "jdbc:mysql://localhost:3306/quiz";
    private String username = "root";
    private String password = "Nucmed2018!";

    @Before
    public void setUp() {

        dao = new General(jdbcUrl, username, password);
    }

    @After
    public void tearDown() {

        dao.close();
    }

    @Test
    public void testSaveQuestion() {
        assertTrue(dao.saveQuestion(1, 2, "Sample Question"));
    }

    @Test
    public void testUpdateQuestion() {

        dao.saveQuestion(1, 2, "Sample Question");

        assertTrue(dao.updateQuestion(1, "Updated Question Content"));
    }

    @Test
    public void testDeleteQuestion() {

        dao.saveQuestion(1, 2, "Sample Question");

        assertTrue(dao.deleteQuestion(1));
    }

    @Test
    public void testSearchQuestionByTopic() {

        dao.saveQuestion(1, 2, "Sample Question");

        ResultSet resultSet = dao.searchQuestionByTopic("Science");
        assertNotNull(resultSet);
        try {
            assertTrue(resultSet.next());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}