import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Yahtzee_v1 {
    public static void main(String[] args) {
        YahtzeeGame yahtzeeGame = new YahtzeeGame();
        yahtzeeGame.openNewGame();
    }
}

class YahtzeeGame {
    private JFrame frame;
    private JPanel dicePanel;
    private JTextField[] diceFields;
    private JCheckBox[] checkBoxes;
    private JPanel scorePanel;
    private JButton[][] scoreButtons; // Use JButtons instead of JTextFields
    private JButton rollButton;
    private JLabel player1Label;
    private JLabel player2Label;
    private int currentPlayer;
    private int rollsRemainingForTurn;
    private int[][] diceValues;
    private boolean gameOver;
    private String[] categories;
    private JTextField probabilityField;
    private Random random;
    private int[] totalScores;
    private boolean[] scoreButtonClicked;

    public void openNewGame() {
        frame = new JFrame("Yahtzee");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel startPanel = new JPanel();
        startPanel.setLayout(new FlowLayout());

        JButton button = new JButton("Start Game");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.remove(startPanel);
                start();
            }
        });

        startPanel.add(button);

        frame.add(startPanel, BorderLayout.SOUTH);

        frame.setSize(200, 300);
        frame.setVisible(true);
    }

    public void start() {
        frame.setTitle("Yahtzee Scorecard");
        frame.setSize(600, 700);

        currentPlayer = 1;
        rollsRemainingForTurn = 3;
        diceValues = new int[5][2];
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
            diceFields[i].setEditable(false);
            checkBoxes[i] = new JCheckBox("Keep");
            JPanel diceWithCheckbox = new JPanel();
            diceWithCheckbox.setLayout(new BoxLayout(diceWithCheckbox, BoxLayout.Y_AXIS));
            diceWithCheckbox.add(diceFields[i]);
            diceWithCheckbox.add(checkBoxes[i]);
            dicePanel.add(diceWithCheckbox);
        }

        JPanel buttonPanel = new JPanel();
        rollButton = new JButton("Roll the Dice (" + rollsRemainingForTurn + " rolls left)");
        buttonPanel.add(rollButton);

        JLabel probabilityLabel = new JLabel("Yahtzee Probability: ");
        buttonPanel.add(probabilityLabel);

        probabilityField = new JTextField(10);
        buttonPanel.add(probabilityField);
        probabilityField.setEditable(false);

        scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(19, 3));
        scoreButtons = new JButton[19][2]; // Use JButtons for score buttons

        player1Label = new JLabel("Player 1");
        player1Label.setForeground(Color.RED);
        scorePanel.add(new JLabel());
        scorePanel.add(player1Label);
        player2Label = new JLabel("Player 2");
        scorePanel.add(player2Label);

        // Inside the start() method
        for (int i = 0; i < 18; i++) {
            JLabel label = new JLabel(categories[i]);
            scorePanel.add(label);

            for (int j = 0; j < 2; j++) {
                if (i == 7 || i == 8 || i == 16) {
                    scoreButtons[i][j] = new JButton();
                    scoreButtons[i][j].setEnabled(false);
                } else {
                    final int categoryIndex = i;
                    final int playerIndex = j;
                    if (i >= 1 && i <= 6) {
                        scoreButtons[i][j] = new JButton("Score");
                        scoreButtons[i][j].addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (!gameOver && (playerIndex + 1) == currentPlayer) {
                                    int category = categoryIndex - 1;
                                    int score = calculateScoreForCategory(category, playerIndex + 1);
                                    totalScores[playerIndex] += score;
                                    scoreButtonClicked[categoryIndex] = true;
                                    scoreButtons[categoryIndex][playerIndex].setEnabled(false);
                                    scoreButtons[0][playerIndex].setText(String.valueOf(totalScores[playerIndex]));
                                    updateScore();
                                    if (!gameOver) {
                                        switchPlayer();
                                    }
                                    resetCheckBoxes();
                                }
                            }
                        });
                    } else if (i >= 9 && i <= 15) {
                        scoreButtons[i][j] = new JButton("Score");
                        scoreButtons[i][j].addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (!gameOver && (playerIndex + 1) == currentPlayer) {
                                    int category = categoryIndex - 1;
                                    int score = 0; // Always set the score to zero for lower categories
                                    totalScores[playerIndex] += score;
                                    scoreButtonClicked[categoryIndex] = true;
                                    scoreButtons[categoryIndex][playerIndex].setEnabled(false);
                                    scoreButtons[0][playerIndex].setText(String.valueOf(totalScores[playerIndex]));
                                    updateScore();
                                    if (!gameOver) {
                                        switchPlayer();
                                    }
                                    resetCheckBoxes();
                                }
                            }
                        });
                    } else {
                        // Add buttons for other cases
                        scoreButtons[i][j] = new JButton();
                        scoreButtons[i][j].setEnabled(false);
                    }
                }
                scorePanel.add(scoreButtons[i][j]);
            }
        }


        rollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver && rollsRemainingForTurn > 0) {
                    rollDice();
                    updateDiceFields();
                    rollsRemainingForTurn--;

                    if (rollsRemainingForTurn == 0) {
                        rollButton.setEnabled(false);
                    }

                    if (rollsRemainingForTurn < 3) {
                        calculateYahtzeeProbability();
                        enableCategoryButtons();
                    }

                    rollButton.setText("Roll the Dice (" + rollsRemainingForTurn + " rolls left)");
                    disableCheckBoxes();
                }
            }
        });

        for (int i = 0; i < 5; i++) {
            checkBoxes[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    calculateYahtzeeProbability();
                }
            });
        }

        frame.setLayout(new BorderLayout());
        frame.add(dicePanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(scorePanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gameOver = false;
        random = new Random();
        totalScores = new int[2];
        scoreButtonClicked = new boolean[17];

        for (int i = 0; i < 17; i++) {
            scoreButtonClicked[i] = false;
        }
    }

    private int calculateScoreForCategory(int category, int player) {
        int score = 0;
        int categoryValue = category + 1;

        for (int i = 0; i < 5; i++) {
            if (diceValues[i][player - 1] == categoryValue) {
                score += categoryValue;
            }
        }
        return score;
    }

    private void rollDice() {
        for (int i = 0; i < 5; i++) {
            if (!checkBoxes[i].isSelected()) {
                diceValues[i][currentPlayer - 1] = random.nextInt(6) + 1;
            }
        }
    }

    private void updateDiceFields() {
        for (int i = 0; i < 5; i++) {
            diceFields[i].setText(String.valueOf(diceValues[i][currentPlayer - 1]));
        }
    }

    private void updateScore() {
        for (int i = 0; i < 5; i++) {
            diceValues[i][currentPlayer - 1] = 0;
        }

        rollButton.setEnabled(true);

        for (int i = 0; i < 5; i++) {
            diceFields[i].setText("");
        }
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        rollsRemainingForTurn = 3;

        if (currentPlayer == 1) {
            player1Label.setForeground(Color.RED);
            player2Label.setForeground(Color.BLACK);
        } else {
            player1Label.setForeground(Color.BLACK);
            player2Label.setForeground(Color.RED);
        }

        probabilityField.setText("");
        disableCheckBoxes();
        rollButton.setText("Roll the Dice (" + rollsRemainingForTurn + " rolls left)");
    }

    private void resetCheckBoxes() {
        for (int i = 0; i < 5; i++) {
            checkBoxes[i].setSelected(false);
        }
    }

    private void enableCategoryButtons() {
        for (int i = 1; i <= 6; i++) {
            if (!scoreButtonClicked[i] && i != 7 && i != 8) {
                scoreButtons[i][currentPlayer - 1].setEnabled(true);
            }
        }
    }

    private void disableCheckBoxes() {
        for (int i = 0; i < 5; i++) {
            checkBoxes[i].setEnabled(rollsRemainingForTurn > 0);
        }
    }

    private void calculateYahtzeeProbability() {
        int keptDiceCount = 0;
        int sameValue = 0;

        for (int i = 0; i < 5; i++) {
            if (checkBoxes[i].isSelected()) {
                int diceValue = diceValues[i][currentPlayer - 1];
                if (sameValue == 0) {
                    sameValue = diceValue;
                } else if (diceValue != sameValue) {
                    sameValue = -1;
                }
            } else {
                keptDiceCount++;
            }

            double yahtzeeProbability = 0.0;

            if (sameValue != -1 && keptDiceCount > 0) {
                yahtzeeProbability = Math.pow(1.0 / 6.0, keptDiceCount);
            }

            double percentage = yahtzeeProbability * 100;

            probabilityField.setText(String.format("%.2f%%", percentage));
        }
    }
}
