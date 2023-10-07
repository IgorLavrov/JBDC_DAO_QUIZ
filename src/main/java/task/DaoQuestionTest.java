package task;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DaoQuestionTest {
    private General dao;

    // Replace with your actual database connection details
    private String jdbcUrl = "jdbc:mysql://localhost:3306/quiz";
    private String username = "root";
    private String password = "Nucmed2018!";

    @Before
    public void setUp() {
        // Initialize the DAO with the database connection
        dao = new General(jdbcUrl, username, password);
    }

    @After
    public void tearDown() {
        // Close the database connection after each test
        dao.close();
    }

    @Test
    public void testSaveQuestion() {
        assertTrue(dao.saveQuestion(1, 2, "Sample Question"));
    }

    @Test
    public void testUpdateQuestion() {
        // Save a question first
        dao.saveQuestion(1, 2, "Sample Question");

        assertTrue(dao.updateQuestion(1, "Updated Question Content"));
    }

    @Test
    public void testDeleteQuestion() {
        // Save a question first
        dao.saveQuestion(1, 2, "Sample Question");

        assertTrue(dao.deleteQuestion(1));
    }

    @Test
    public void testSearchQuestionByTopic() {
        // Save a question with a specific topic
        dao.saveQuestion(1, 2, "Sample Question");

        ResultSet resultSet = dao.searchQuestionByTopic("Science");
        assertNotNull(resultSet);
        try {
            assertTrue(resultSet.next()); // Check if there is at least one result
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}