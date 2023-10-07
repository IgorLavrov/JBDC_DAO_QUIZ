package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    private Connection connection;
    private static final String JDBC_URL = "jdbc:mysql://localhost/quiz";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public Main() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load the JDBC driver
            this.connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
            createTables(); // Create tables when the DAO is instantiated
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    private void createTables() {
        try {
            // Create the Topics table
            String createTopicsTableSQL = "CREATE TABLE IF NOT EXISTS Topics (" +
                    "TopicID INT PRIMARY KEY AUTO_INCREMENT," +
                    "TopicName VARCHAR(255) NOT NULL" +
                    ")";
            try (PreparedStatement preparedStatement = connection.prepareStatement(createTopicsTableSQL)) {
                preparedStatement.executeUpdate();
            }

            // Create the Questions table
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

            // Create the Responses table
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

            // Create the table to handle multiple correct responses for a question
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
            // Check if the provided topicId exists in the Topics table
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
                return resultSet.next(); // Returns true if a matching topicId is found
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
        try {
            String sql = "SELECT * FROM Questions JOIN Topics ON Questions.TopicID = Topics.TopicID WHERE Topics.TopicName = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, topicName);
                return preparedStatement.executeQuery();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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
        // Example usage
        Main dao = new Main();
        // dao.saveQuestion(1, 1, "What is your question?");

        dao.updateQuestion(2,  "What is question?");
        dao.close();
    }
}