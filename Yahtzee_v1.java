import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Arrays;

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
    private JButton[][] scoreButtons;
    private JButton rollButton;
    private JButton restartButton;
    private JLabel player1Label;
    private JLabel player2Label;
    private int currentPlayer;
    private int rollsRemainingForTurn;
    private int[][] diceValues;
    private boolean gameOver;
    private String[] categories;
    private JTextField probabilityField;
    private Random random;
    private int[] totalScores, upperScores, lowerScores, yahtzeeBonus;
    private int[][] categoryScores;
    private boolean[] scoreButtonClicked, bonusAdded, yahtzeeBonusScored;

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

        frame.setSize(700, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void start() {
        frame.setTitle("Yahtzee Scorecard");
        frame.setSize(700, 700);

        currentPlayer = 1;
        rollsRemainingForTurn = 3;
        diceValues = new int[5][2];
        categories = new String[]{
                "Grand Total", "Ones", "Twos", "Threes", "Fours", "Fives", "Sixes",
                "Bonus (35)", "Total Upper", "3 of a kind", "4 of a kind", "Full House (25)",
                "Sm Straight (30)", "Lg Straight (40)", "Yahtzee (50)", "Chance", "Yahtzee Bonus (100)",
                "Total Lower"
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
        restartButton = new JButton("Restart game");
        rollButton = new JButton("Roll the Dice (" + rollsRemainingForTurn + " rolls left)");
        buttonPanel.add(restartButton);
        buttonPanel.add(rollButton);

        JLabel probabilityLabel = new JLabel("Yahtzee Probability: ");
        buttonPanel.add(probabilityLabel);

        probabilityField = new JTextField(10);
        buttonPanel.add(probabilityField);
        probabilityField.setEditable(false);

        scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(19, 3));
        scoreButtons = new JButton[19][2];

        player1Label = new JLabel("Player 1");
        player1Label.setForeground(Color.RED);
        scorePanel.add(new JLabel());
        scorePanel.add(player1Label);
        player2Label = new JLabel("Player 2");
        scorePanel.add(player2Label);

        for (int i = 0; i < 18; i++) {
            JLabel label = new JLabel(categories[i]);
            scorePanel.add(label);

            for (int j = 0; j < 2; j++) {
                if (i == 7 || i == 8 || i == 16) {
                    scoreButtons[i][j] = new JButton("No score");
                    scoreButtons[i][j].setEnabled(false);
                } else if (i >= 1 && i <= 6) {
                    final int categoryIndex = i;
                    final int playerIndex = j;
                    scoreButtons[i][j] = new JButton("Score");
                    scoreButtons[i][j].addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (!gameOver && (playerIndex + 1) == currentPlayer) {
                                int category = categoryIndex - 1; // Adjust for zero-based array
                                categoryScores[category][playerIndex] = calculateScoreForCategory(category, playerIndex + 1);
                                totalScores[playerIndex] += categoryScores[category][playerIndex]; // Accumulate the score in the total score
                                upperScores[playerIndex] += categoryScores[category][playerIndex];
                                scoreButtonClicked[categoryIndex] = true; // Mark the button as clicked
                                scoreButtons[categoryIndex][playerIndex].setEnabled(false);
                                if(!bonusAdded[playerIndex]){
                                    if(upperScores[playerIndex] > 63) {
                                        totalScores[playerIndex] += 35;
                                        bonusAdded[playerIndex] = true;
                                        scoreButtons[7][playerIndex].setText("35");
                                    }
                                }
                                scoreButtons[0][playerIndex].setText(String.valueOf(totalScores[playerIndex])); // Update the total score label
                                scoreButtons[8][playerIndex].setText(String.valueOf(upperScores[playerIndex])); // Update upper score total
                                updateScore();
                                if (!gameOver) {
                                    switchPlayer();
                                }
                                resetCheckBoxes();
                            }
                        }
                    });
                } else if (i >= 9 && i <= 15) {
                    // Categories for 3 of a kind, 4 of a kind, Full House, Small Straight, Large Straight, Yahtzee, and Chance
                    JTextField textField = new JTextField();
                    textField.setEditable(false);
                    final int categoryIndex = i;
                    final int playerIndex = j;
                    scoreButtons[i][j] = new JButton("Score");
                    scoreButtons[i][j].addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (!gameOver && (playerIndex + 1) == currentPlayer) {
                                int category = categoryIndex - 1; // Adjust for zero-based array
                                categoryScores[category][playerIndex] = calculateScoreForCategory(category, playerIndex + 1);
                                totalScores[playerIndex] += categoryScores[category][playerIndex]; // Accumulate the score in the total score
                                lowerScores[playerIndex] += categoryScores[category][playerIndex];
                                if(categoryIndex != 14) { //exclude yahtzee button to allow bonus scores
                                    scoreButtonClicked[categoryIndex] = true; // Mark the button as clicked
                                    scoreButtons[categoryIndex][playerIndex].setEnabled(false);
                                }
                                scoreButtons[0][playerIndex].setText(String.valueOf(totalScores[playerIndex])); // Update the total score label
                                scoreButtons[17][playerIndex].setText(String.valueOf(lowerScores[playerIndex])); // Update upper score total
                                updateScore();
                                if (!gameOver) {
                                    switchPlayer();
                                }
                                resetCheckBoxes();
                            }
                        }
                    });

                } else {
                    // Grand Total
                    JTextField textField = new JTextField();
                    textField.setEditable(false);
                    scoreButtons[i][j] = new JButton();
                    scoreButtons[i][j].setEnabled(false);
                }
                scorePanel.add(scoreButtons[i][j]);
            }
        }

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                frame.dispose();
                openNewGame();
            }
        });

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
                        enableCategoryButtons(); // Enable the score buttons for categories 1 to 6
                    }

                    rollButton.setText("Roll the Dice (" + rollsRemainingForTurn + " rolls left)"); // Update the rollButton text
                    disableCheckboxes();
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
        upperScores = new int[2];
        bonusAdded = new boolean[2];
        categoryScores = new int[17][2];
        lowerScores = new int[2];
        yahtzeeBonus = new int[2];
        yahtzeeBonusScored = new boolean[2];
        scoreButtonClicked = new boolean[16];

        for (int i = 0; i < 7; i++) {
            scoreButtonClicked[i] = false;
        }
    }

    private int calculateScoreForCategory(int category, int player) {
        int score = 0;
        int categoryValue = category + 1; // Adjust for one-based array

        if (categoryValue < 7) {
            for (int i = 0; i < 5; i++) {
                if (diceValues[i][player - 1] == categoryValue) {
                    score += categoryValue; // Count the category value rolled
                }
            }
            return score;
        } else {
            switch (categoryValue) {
                case 9:
                    int sum = 0;
                    int[] currentPlayerDice = new int[5];
                    for (int i = 0; i < 5; i++) {
                        currentPlayerDice[i] = diceValues[i][player - 1];
                        sum += currentPlayerDice[i];
                    }

                    Arrays.sort(currentPlayerDice);

                    boolean threeAreSame = false;
                    for (int i = 0; i <= 2; i++) {
                        if (currentPlayerDice[i] == currentPlayerDice[i + 2]) {
                            threeAreSame = true;
                            break;
                        }
                    }

                    return threeAreSame ? sum : 0;

                case 10:
                    int[] counts = new int[7];
                    int maxSum = 0;

                    for (int i = 0; i < 5; i++) {
                        int currentValue = diceValues[i][player - 1];
                        counts[currentValue]++;
                        maxSum += currentValue;
                    }

                    for (int value = 1; value <= 6; value++) {
                        if (counts[value] >= 4) {
                            return maxSum;
                        }
                    }
                    return 0;

                case 11:
                    int[] counts11 = new int[7];
                    for (int i = 0; i < 5; i++) {
                        int currentValue = diceValues[i][player - 1];
                        counts11[currentValue]++;
                    }

                    boolean hasThreeOfAKind = false;
                    boolean hasTwoOfAKind = false;

                    for (int value = 1; value <= 6; value++) {
                        if (counts11[value] == 3) {
                            hasThreeOfAKind = true;
                        } else if (counts11[value] == 2) {
                            hasTwoOfAKind = true;
                        }
                    }

                    return (hasThreeOfAKind && hasTwoOfAKind) ? 25 : 0;

                case 12:
                    int[] counts2 = new int[7];
                    for (int i = 0; i < 5; i++) {
                        int currentValue = diceValues[i][player - 1];
                        counts2[currentValue]++;
                    }

                    int consecutiveCount = 0;
                    for (int value = 1; value <= 6; value++) {
                        if (counts2[value] > 0) {
                            consecutiveCount++;
                        } else {
                            consecutiveCount = 0;
                        }

                        if (consecutiveCount >= 4) {
                            return 30;
                        }
                    }
                    return 0;

                case 13:
                    int[] counts13 = new int[7];
                    for (int i = 0; i < 5; i++) {
                        int currentValue = diceValues[i][player - 1];
                        counts13[currentValue]++;
                    }

                    int consecutiveCount13 = 0;
                    for (int value = 1; value <= 6; value++) {
                        if (counts13[value] > 0) {
                            consecutiveCount13++;
                        } else {
                            consecutiveCount13 = 0;
                        }

                        if (consecutiveCount13 >= 5) {
                            return 40;
                        }
                    }
                    return 0;

                case 14:
                    int[] counts14 = new int[7];
                    for (int i = 0; i < 5; i++) {
                        int currentValue = diceValues[i][player - 1];
                        counts14[currentValue]++;
                    }

                    for (int value = 1; value <= 6; value++) {
                        if (counts14[value] >= 5) {
                            if(categoryScores[13][player - 1] != 0){
                                yahtzeeBonusScored[player - 1] = true;
                            }
                            if(yahtzeeBonusScored[player - 1]) {
                                yahtzeeBonus[player - 1] += 100;
                                totalScores[player - 1] += 100;
                                scoreButtons[16][player - 1].setText(String.valueOf(yahtzeeBonus[player - 1]));
                                return 0; //Player already scores bonus, no "normal" point rewarded
                            }
                            return 50;
                        }
                    }
                    return 0;

                case 15:
                    int sumChance = 0;
                    for (int i = 0; i < 5; i++) {
                        int currentValue = diceValues[i][player - 1];
                        sumChance += currentValue;
                    }
                    return sumChance;

                default:
                    return -1;
            }
        }
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
        disableCheckboxes();
        rollButton.setText("Roll the Dice (" + rollsRemainingForTurn + " rolls left)"); // Update the rollButton text
    }

    private void resetCheckBoxes() {
        for (int i = 0; i < 5; i++) {
            checkBoxes[i].setSelected(false);
        }
    }

    private void enableCategoryButtons() {
        for (int i = 1; i <= 6; i++) { // Skip Bonus and Total Upper categories
            if (!scoreButtonClicked[i]) {
                scoreButtons[i][currentPlayer - 1].setEnabled(true);
            }
        }
    }

    private void disableCheckboxes() {
        for (int i = 0; i < 5; i++) {
            checkBoxes[i].setEnabled(rollsRemainingForTurn > 0);
        }
    }
    private void calculateYahtzeeProbability() {
        int keptDiceCount = 0;
        int keptValue = 0;

        for (int i = 0; i < 5; i++) {
            if (checkBoxes[i].isSelected()) {
                if (keptValue == 0) {
                    keptValue = diceValues[i][currentPlayer - 1];
                } else if (keptValue != diceValues[i][currentPlayer-1])
                    keptValue = -1;
            } else {
                keptDiceCount++;
            }

            double yahtzeeProbability = 0.0;

            if (keptValue != -1 && keptDiceCount > 0) {
                yahtzeeProbability = Math.pow(1.0 / 6.0, keptDiceCount);
            } else if (keptValue != -1 && keptDiceCount == 0) {
                yahtzeeProbability = 1;
            }

            double percentage = yahtzeeProbability * 100;

            probabilityField.setText(String.format("%.2f%%", percentage));
        }
    }
}