import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Yahtzee {
    public static void main(String[] args) {
        YahtzeeGame yahtzeeGame = new YahtzeeGame();
        yahtzeeGame.start();
    }
}

class YahtzeeGame {
    private JFrame frame;
    private JPanel dicePanel;
    private JTextField[] diceFields;
    private JCheckBox[] checkBoxes;
    private JPanel scorePanel;
    private JTextField[][] scores;
    private JButton rollButton;
    private JLabel playerLabel;
    private int currentPlayer;
    private int rollsRemaining;
    private int[][] diceValues;
    private boolean[] diceToRoll;
    private String[] categories;
    private boolean gameOver;
    private boolean player1Turn;
    private int round; // Track the current round

    public void start() {
        frame = new JFrame("Yahtzee Scorecard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);

        currentPlayer = 1;
        rollsRemaining = 3;
        diceValues = new int[5][2];
        diceToRoll = new boolean[5];
        categories = new String[]{
            "Points", "Ones", "Twos", "Threes", "Fours", "Fives", "Sixes",
            "Total Upper", "Bonus (35)", "3 of a kind", "4 of a kind", "Full House (25)",
            "Sm Straight (30)", "Lg Straight (40)", "Yahtzee (50)", "Chance", "Yahtzee Bonus (100)",
            "Grand Total"
        };

        dicePanel = new JPanel();
        dicePanel.setLayout(new FlowLayout());
        diceFields = new JTextField[5];
        checkBoxes = new JCheckBox[5];

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
        rollButton = new JButton("Roll the Dice");
        buttonPanel.add(rollButton);

        scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(19, 3));
        scores = new JTextField[19][2];

        playerLabel = new JLabel("Player 1");
        scorePanel.add(new JLabel());
        scorePanel.add(playerLabel);
        playerLabel = new JLabel("Player 2");
        scorePanel.add(playerLabel);

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

        rollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    if (rollsRemaining > 0) {
                        rollDice();
                        updateDiceFields();
                        rollsRemaining--;
                        rollButton.setText("Roll (" + rollsRemaining + " rolls left)");

                        if (rollsRemaining == 0) {
                            rollButton.setEnabled(false);
                            // Determine and update the score after the third roll
                            updateScore();
                        }
                    }
                }
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(dicePanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(scorePanel, BorderLayout.SOUTH);
        frame.setVisible(true);

        player1Turn = true; // Initialize the turn to Player 1
        round = 1; // Start with the first round
        gameOver = false; // The game is not over initially
    }

    private void rollDice() {
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            if (!checkBoxes[i].isSelected()) {
                diceValues[i][0] = random.nextInt(6) + 1;
            }
        }
    }

    private void updateDiceFields() {
        for (int i = 0; i < 5; i++) {
            diceFields[i].setText(String.valueOf(diceValues[i][0]));
        }
    }

    private void updateScore() {
        // Implement the logic to determine and update the score for the selected category.

        // Reset checkboxes after the last throw of the current player
        for (int i = 0; i < 5; i++) {
            checkBoxes[i].setSelected(false);
        }

        if (player1Turn) {
            player1Turn = false; // Switch to Player 2's turn
            playerLabel.setText("Player 2");
        } else {
            // Player 2's turn ends...

            // Implement the end of the game logic and scoring for Player 2.
            // You can also check for the game's end condition here.

            if (round == 13) {
                // The game is over after 13 rounds
                gameOver = true;
                determineWinner(); // Implement a method to determine the winner
            } else {
                round++; // Move to the next round
            }

            player1Turn = true; // Switch back to Player 1's turn
            playerLabel.setText("Player 1");
        }
        rollsRemaining = 3; // Reset rolls for the next player
        rollButton.setEnabled(true);
        rollButton.setText("Roll (3 rolls left)");
    }

    private void determineWinner() {
        // Implement the logic to calculate and display the winner.
        // You can compare the final scores of both players here.
    }
}
