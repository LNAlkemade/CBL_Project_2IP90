import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class YahtzeeGame extends JFrame implements ActionListener {
    private JButton rollButton;
    private JButton scoreButton;
    private JButton[] categoryButtons;
    private JButton[] diceButtons;  // Use buttons for dice
    private JLabel[] diceLabels;
    private int[] diceValues;
    private boolean[] diceHeld;  // To keep track of held dice

    public YahtzeeGame() {
        setTitle("Yahtzee Game");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        rollButton = new JButton("Roll Dice");
        rollButton.addActionListener(this);
        scoreButton = new JButton("Score");
        scoreButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(rollButton);
        buttonPanel.add(scoreButton);
        add(buttonPanel, BorderLayout.SOUTH);

        JPanel dicePanel = new JPanel();
        diceButtons = new JButton[5];  // Use buttons for dice
        diceLabels = new JLabel[5];
        diceValues = new int[5];
        diceHeld = new boolean[5];

        for (int i = 0; i < 5; i++) {
            diceButtons[i] = new JButton();  // Create buttons for dice
            diceButtons[i].addActionListener(this);
            diceButtons[i].setText("Hold");
            diceButtons[i].setActionCommand("Hold" + i);

            diceLabels[i] = new JLabel();
            dicePanel.add(diceButtons[i]);  // Add dice buttons
            dicePanel.add(diceLabels[i]);
        }

        add(dicePanel, BorderLayout.CENTER);

        JPanel categoryPanel = new JPanel();
        categoryButtons = new JButton[13];
        String[] categories = {
            "Ones", "Twos", "Threes", "Fours", "Fives", "Sixes",
            "Three of a Kind", "Four of a Kind", "Full House",
            "Small Straight", "Large Straight", "Chance", "Yahtzee"
        };

        for (int i = 0; i < 13; i++) {
            categoryButtons[i] = new JButton(categories[i]);
            categoryButtons[i].addActionListener(this);
            categoryPanel.add(categoryButtons[i]);
        }

        add(categoryPanel, BorderLayout.EAST);

        // Disable category buttons initially
        for (int i = 0; i < 13; i++) {
            categoryButtons[i].setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == rollButton) {
            rollDice();
            // Enable category buttons based on current dice values
            updateCategoryButtonAvailability();
        } else if (e.getSource() == scoreButton) {
            int score = calculateTotalScore();
            JOptionPane.showMessageDialog(this, "Your total score is: " + score);
        } else {
            for (int i = 0; i < 5; i++) {
                if (e.getSource() == diceButtons[i]) {
                    // Toggle the hold state of the dice
                    diceHeld[i] = !diceHeld[i];
                    diceButtons[i].setText(diceHeld[i] ? "Release" : "Hold");
                }
            }
            updateCategoryButtonAvailability();
        }
    }

    private void rollDice() {
        for (int i = 0; i < 5; i++) {
            if (!diceHeld[i]) {
                diceValues[i] = (int) (Math.random() * 6) + 1;
                diceLabels[i].setText(String.valueOf(diceValues[i]));
            }
        }
    }

    private void updateCategoryButtonAvailability() {
        // Enable category buttons based on the current dice values
        // Implement logic to determine which categories are valid
        // Example: Enable "Ones" category if there is at least one 1 in the dice values
        for (int i = 0; i < 13; i++) {
            if (i >= 0 && i <= 5) {
                // Ones to Sixes
                int categoryValue = i + 1;
                boolean isValid = Arrays.stream(diceValues).anyMatch(value -> value == categoryValue);
                categoryButtons[i].setEnabled(isValid);
            } else {
                categoryButtons[i].setEnabled(true); // Enable other categories
            }
        }
    }

    private int calculateTotalScore() {
        int totalScore = 0;
        for (int i = 0; i < 13; i++) {
            totalScore += calculateCategoryScore(i);
        }
        return totalScore;
    }

    private int calculateCategoryScore(int category) {
        // Implement scoring logic for each category
        // You can use the diceValues array to calculate the score
        // Replace this with your scoring logic
        return 0; // Default value
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            YahtzeeGame game = new YahtzeeGame();
            game.setVisible(true);
        });
    }
}
