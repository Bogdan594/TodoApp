import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskRepo {
    private static final String URL = "jdbc:sqlite:tasks.db";

    public static void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "is_done INTEGER DEFAULT 0" +
                ");";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error" + e.getMessage());
        }
    }

    public static List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT id, title, description, is_done FROM tasks";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String desc = rs.getString("description");
                boolean done = rs.getInt("is_done") == 1;
                list.add(new Task(id, title, desc, done));
            }
        } catch (SQLException e) {
            System.out.println("Eroare la citire: " + e.getMessage());
        }
        return list;
    }

    public static List<Task> getTasksSorted(int tipSortare) {
        List<Task> list = new ArrayList<>();
        String directie = (tipSortare == 1) ? "ASC" : "DESC";
        String sql = "SELECT * FROM tasks ORDER BY is_done " + directie;
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("is_done") == 1
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error sort" + e.getMessage());
        }
        return list;
    }

    public static void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try(Connection conn = DriverManager.getConnection(URL); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error delete" + e.getMessage());
        }
    }

    public static void addTask(String title, String description){
        String sql = "INSERT INTO tasks (title, description, is_done) VALUES (?, ?, 0)";
        try(Connection conn = DriverManager.getConnection(URL); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error add" + e.getMessage());
        }
    }

    public static void updateTask(int id, String title, String description) {
        String sql = "UPDATE tasks SET title = ?, description = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error update" + e.getMessage());
        }
    }

    public static void updateTaskStatus(int id, boolean isDone) {
        String sql = "UPDATE tasks SET is_done = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, isDone ? 1 : 0);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error update" + e.getMessage());
        }
    }

    public static List<Task> searchTasks(String keyword) {
        List<Task> results = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE title LIKE ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String desc = rs.getString("description");
                boolean done = rs.getInt("is_done") == 1;
                results.add(new Task(id, title, desc, done));
            }
        } catch (SQLException e) {
            System.out.println("Error search" + e.getMessage());
        }

        return results;
    }
}
