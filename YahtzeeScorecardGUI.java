import javax.swing.*;
import java.awt.*;

public class YahtzeeScorecardGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Yahtzee Scorecard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);

        JPanel dicePanel = new JPanel();
        dicePanel.setLayout(new FlowLayout());

        JTextField[] diceFields = new JTextField[5];
        JCheckBox[] checkBoxes = new JCheckBox[5];

        for (int i = 0; i < 5; i++) {
            diceFields[i] = new JTextField(2);
            diceFields[i].setPreferredSize(new Dimension(50, 50));
            checkBoxes[i] = new JCheckBox("Keep");
            JPanel diceWithCheckbox = new JPanel();
            diceWithCheckbox.setLayout(new BoxLayout(diceWithCheckbox, BoxLayout.Y_AXIS));
            diceWithCheckbox.add(diceFields[i]);
            diceWithCheckbox.add(checkBoxes[i]);
            dicePanel.add(diceWithCheckbox);
        }

        JPanel buttonPanel = new JPanel();
        JButton rollButton = new JButton("Roll the Dice");
        buttonPanel.add(rollButton);

        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(19, 3));

        JLabel playerLabel = new JLabel("Player 1");
        scorePanel.add(new JLabel());
        scorePanel.add(playerLabel);
        playerLabel = new JLabel("Player 2");
        scorePanel.add(playerLabel);

        String[] categories = {
            "Points", "Ones", "Twos", "Threes", "Fours", "Fives", "Sixes",
            "Total Upper", "Bonus (35)", "3 of a kind", "4 of a kind", "Full House (25)",
            "Sm Straight (30)", "Lg Straight (40)", "Yahtzee (50)", "Chance", "Yahtzee Bonus (100)",
            "Grand Total"
        };

        JTextField[][] scores = new JTextField[19][2];

        for (int i = 0; i < 18; i++) {
            JLabel label = new JLabel(categories[i]);
            scorePanel.add(label);

            for (int j = 0; j < 2; j++) {
                if (i == 7 || i == 8 || i == 16) {
                    scores[i][j] = new JTextField();
                    scores[i][j].setEditable(false);
                } else {
                    scores[i][j] = new JTextField();
                }
                scorePanel.add(scores[i][j]);
            }
        }

        frame.setLayout(new BorderLayout());

        frame.add(dicePanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(scorePanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}
