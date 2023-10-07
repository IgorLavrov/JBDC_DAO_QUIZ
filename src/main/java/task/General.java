package task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class General {
    private Connection connection;
    private static final String JDBC_URL = "jdbc:mysql://localhost/quiz";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Nucmed2018!";

    public General() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public General(String jdbcUrl, String username, String password) {
        try {
            this.connection = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        try {

            String createTopicsTableSQL = "CREATE TABLE IF NOT EXISTS Topics (" +
                    "TopicID INT PRIMARY KEY AUTO_INCREMENT," +
                    "TopicName VARCHAR(255) NOT NULL" +
                    ")";
            try (PreparedStatement preparedStatement = connection.prepareStatement(createTopicsTableSQL)) {
                preparedStatement.executeUpdate();
            }


            String createQuestionsTableSQL = "CREATE TABLE IF NOT EXISTS Questions (" +
                    "QuestionID INT PRIMARY KEY AUTO_INCREMENT," +
                    "TopicID INT NOT NULL," +
                    "DifficultyRank INT NOT NULL," +
                    "Content TEXT NOT NULL," +
                    "FOREIGN KEY (TopicID) REFERENCES Topics(TopicID)" +
                    ")";
            try (PreparedStatement preparedStatement = connection.prepareStatement(createQuestionsTableSQL)) {
                preparedStatement.executeUpdate();
            }


            String createResponsesTableSQL = "CREATE TABLE IF NOT EXISTS Responses (" +
                    "ResponseID INT PRIMARY KEY AUTO_INCREMENT," +
                    "QuestionID INT NOT NULL," +
                    "Text TEXT NOT NULL," +
                    "IsCorrect BOOLEAN NOT NULL," +
                    "FOREIGN KEY (QuestionID) REFERENCES Questions(QuestionID)" +
                    ")";
            try (PreparedStatement preparedStatement = connection.prepareStatement(createResponsesTableSQL)) {
                preparedStatement.executeUpdate();
            }


            String createQuestionCorrectResponsesTableSQL = "CREATE TABLE IF NOT EXISTS QuestionCorrectResponses (" +
                    "QuestionID INT NOT NULL," +
                    "ResponseID INT NOT NULL," +
                    "PRIMARY KEY (QuestionID, ResponseID)," +
                    "FOREIGN KEY (QuestionID) REFERENCES Questions(QuestionID)," +
                    "FOREIGN KEY (ResponseID) REFERENCES Responses(ResponseID)" +
                    ")";
            try (PreparedStatement preparedStatement = connection.prepareStatement(createQuestionCorrectResponsesTableSQL)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean saveQuestion(int topicId, int difficultyRank, String content) {
        try {

            if (isTopicIdValid(topicId)) {
                String sql = "INSERT INTO Questions (TopicID, DifficultyRank, Content) VALUES (?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, topicId);
                    preparedStatement.setInt(2, difficultyRank);
                    preparedStatement.setString(3, content);
                    preparedStatement.executeUpdate();
                    return true;
                }
            } else {
                System.err.println("Invalid topicId. Please provide a valid topicId.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isTopicIdValid(int topicId) {
        try {
            String sql = "SELECT TopicID FROM Topics WHERE TopicID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, topicId);
                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateQuestion(int questionId, String newContent) {
        try {
            String sql = "UPDATE Questions SET Content = ? WHERE QuestionID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newContent);
                preparedStatement.setInt(2, questionId);
                preparedStatement.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteQuestion(int questionId) {
        try {
            String sql = "DELETE FROM Questions WHERE QuestionID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, questionId);
                preparedStatement.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet searchQuestionByTopic(String topicName) {
        ResultSet resultSet = null;

        try {
            String sql = "SELECT * FROM Questions JOIN Topics ON Questions.TopicID = Topics.TopicID WHERE Topics.TopicName = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, topicName);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;
    }



    public boolean insertTopic(String topicName) {
        try {
            String sql = "INSERT INTO Topics (TopicName) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, topicName);
                preparedStatement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {


        General dao = new General();
        dao.insertTopic("Mathematics");
        dao.insertTopic("Science");

        dao.saveQuestion(1, 1, "What is your question?");


        dao.saveQuestion(2, 2, "Sample Question");


        dao.updateQuestion(1, "Updated Question Content");


        dao.deleteQuestion(1);


        ResultSet resultSet = dao.searchQuestionByTopic("Science");
        try {
            while (resultSet.next()) {
                int questionId = resultSet.getInt("QuestionID");
                String content = resultSet.getString("Content");
                System.out.println("Question ID: " + questionId);
                System.out.println("Content: " + content);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dao.close();
    }
}