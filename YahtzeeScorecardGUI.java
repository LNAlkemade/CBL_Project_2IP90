import javax.swing.*;
import java.awt.*;

public class YahtzeeScorecardGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Yahtzee Scorecard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(18, 3));

        JLabel playerLabel = new JLabel("Player 1");
        panel.add(new JLabel()); // An empty cell in the grid
        panel.add(playerLabel);
        playerLabel = new JLabel("Player 2");
        panel.add(playerLabel);

        String[] categories = {
            "Points", "Ones", "Twos", "Threes", "Fours", "Fives", "Sixes",
            "Total Upper", "Bonus (35)", "3 of a kind", "4 of a kind", "Full House (25)",
            "Sm Straight (30)", "Lg Straight (40)", "Yahtzee (50)", "Chance", "Yahtzee Bonus (100)",
            "Grand Total"
        };

        JTextField[][] scores = new JTextField[18][2];

        for (int i = 0; i < 17; i++) {
            JLabel label = new JLabel(categories[i]);
            panel.add(label);

            for (int j = 0; j < 2; j++) {
                if (i == 7 || i == 8 || i == 16) {
                    scores[i][j] = new JTextField();
                    scores[i][j].setEditable(false); // Make the text fields uneditable
                } else {
                    scores[i][j] = new JTextField();
                }
                panel.add(scores[i][j]);
            }
        }

        frame.add(panel);
        frame.setVisible(true);
    }
}
