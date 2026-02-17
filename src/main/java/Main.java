import java.util.List;
import javax.swing.*;
public class Main {

    public static void main(String[] args) {
        TaskRepo.initDatabase();
        TaskRepo repo = new TaskRepo();
        SwingUtilities.invokeLater(() -> {
            UI gui = new UI(repo);
            gui.showWindow();
        });

    }
}
