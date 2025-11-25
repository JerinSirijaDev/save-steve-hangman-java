import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.sound.sampled.*;
import javax.swing.*;

public class HangmanGame {

    JFrame frame;
    CardLayout cardLayout;
    JPanel mainPanel;
    WelcomePanel welcomePanel;
    GamePanel gamePanel;

    String[] words = {
        "KIDNAP", "ESCAPE", "CAPTURE", "DANGER", "PRISON",
        "HOSTAGE", "FUGITIVE", "LOCKDOWN", "TRESPASS", "SURVIVAL",
        "CONSPIRACY", "INTERROGATE", "ENCRYPTION", "VANISHING", "CRIMINAL",
        "DETECTION", "BREAKOUT", "ALIBI", "MYSTERIOUS", "BLACKMAIL",
        "SUSPICION", "AMBUSH", "INFILTRATE", "REDEMPTION", "SHACKLES",
        "ABDUCTION", "DECEPTION", "UNDERGROUND", "RESTRAINT", "EVIDENCE",
        "REVENGE", "PRISONER", "TRAITOR", "ESCORT", "GUILTY",
        "DUNGEON", "PANICROOM", "RESCUE", "ISOLATION", "MANHUNT",
        "PUZZLING", "LOCKPICK", "BREAKING", "TRAPPED", "WATCHTOWER",
        "SURVEILLANCE", "FRACTURED", "ENTANGLED", "OPPONENT", "EXPOSURE",
        "INTERROGATION", "ESCALATION", "STOCKHOLM", "UNDERCOVER", "DISGUISE",
        "INTERCEPT", "HOSTILITY", "SMUGGLING", "MOTIVE", "TRAIL",
        "ENEMY", "SECURITY", "WIRETAP", "INTRUDER", "INSOMNIA",
        "ALARMED", "TRACKED", "SURVEIL", "FOOTSTEPS", "RECON",
        "BACKSTAB", "TERROR", "MISLEAD", "PREDATOR", "SHADOW",
        "INNOCENT", "CONFESS", "INTERLOCK", "ENCLOSED", "PARANOIA",
        "ESCALATOR", "EVACUATE", "OBSCURED", "DETAINED", "HUNTED",
        "AMBIGUITY", "HOSTEL", "UNTRACEABLE", "DECOY", "EXTRACTION",
        "FORCED", "PANICKED", "RESTRAINTS", "CHAINED", "DEADLINE",
        "ESCAPADE", "VULNERABLE", "DISTORTED", "TRIGGER", "SECRETIVE"

    };

    // triger the countdown
    private void startGameWithCountdown() {
        JDialog countdownDialog = new JDialog(frame, "Get Ready!", false);
        countdownDialog.setUndecorated(true); 
        countdownDialog.setBackground(new Color(0, 0, 0, 0));
        countdownDialog.setLayout(new BorderLayout());

        JLabel label = new JLabel("3", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 450));

       
        Color[] colors = {
            Color.WHITE,
            new Color(255, 215, 0),
            new Color(0, 255, 255),
            new Color(255, 0, 255),
            new Color(50, 255, 50),
            new Color(138, 43, 226),
            new Color(192, 192, 192)
        };
        label.setForeground(colors[new Random().nextInt(colors.length)]);

        label.setOpaque(false); 
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        countdownDialog.add(label, BorderLayout.CENTER);
        countdownDialog.setSize(640, 640);
        countdownDialog.setLocationRelativeTo(frame);

        final int[] counter = {3};

        javax.swing.Timer timer = new javax.swing.Timer(1000, null);
        timer.addActionListener(e -> {
            counter[0]--;
            if (counter[0] > 0) {
                label.setText(String.valueOf(counter[0]));
            } else {
                timer.stop();
                countdownDialog.dispose();
                cardLayout.show(mainPanel, "game");
            }
        });

        timer.setInitialDelay(1000);
        timer.start();

        countdownDialog.setVisible(true);
    }

    // Game state variables
    String selectedWord;
    String currentDifficulty;
    Set<Character> guessedLetters = new HashSet<>();
    int wrongGuesses = 0;
    final int MAX_WRONG = 6;
    SoundPlayer soundPlayer = new SoundPlayer();
    long startTime;
    int totalSteps = 0;


    public HangmanGame() {
        frame = new JFrame("Save Steve - Hangman");
        frame.setIconImage(new ImageIcon("images/icon.png").getImage());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        welcomePanel = new WelcomePanel();
        mainPanel.add(welcomePanel, "welcome");

        frame.add(mainPanel);
        frame.setSize(640, 640);
        frame.setVisible(true);
    }

    // Welcome screen UI
    class WelcomePanel extends JPanel {

        private Image background;
        private JTextArea leaderboard;

        public WelcomePanel() {
            background = new ImageIcon("images/welcome.jpg").getImage();
            setLayout(new BorderLayout());


            JLabel title = new JLabel("SAVE STEVE!", JLabel.CENTER);
            title.setFont(new Font("Segoe UI Black", Font.BOLD, 95));

            //randomized title color
            Color[] titleColors = {
                Color.YELLOW, // Bright Yellow (current)
                new Color(255, 215, 0), // Gold
                new Color(255, 255, 102), // Soft Lemon Yellow
                new Color(255, 165, 0), // Orange (not too dark)
                new Color(255, 105, 180), // Hot Pink (pops well)
                new Color(135, 206, 250), // Light Sky Blue (if you want calm contrast)
                new Color(255, 240, 0), // Vivid Yellow (very close to sun-yellow)
                new Color(0, 255, 127), // Spring Green (bright, stands out)
                new Color(255, 0, 255), // Magenta (great for retro feel)
            };
            title.setForeground(titleColors[new Random().nextInt(titleColors.length)]);

            add(title, BorderLayout.NORTH);

            // Story and the Box it sits on, so I call it Storybox.
            JTextArea story = new JTextArea("This is Steve. He was kidnapped.\nThe kidnapper is playing a deadly game.\nGuess the word or Steve gets hanged.");
            story.setFont(new Font("Courier New", Font.BOLD, 16));
            story.setEditable(false);
            story.setOpaque(false);
            story.setForeground(Color.WHITE);

            JPanel storyBox = new JPanel(new BorderLayout());
            storyBox.setOpaque(true);
            storyBox.setBackground(new Color(0, 0, 0, 150));
            storyBox.setBorder(BorderFactory.createEmptyBorder(20, 14, 20, 5));
            storyBox.add(story, BorderLayout.CENTER);

            JPanel storyContainer = new JPanel(new GridBagLayout());
            storyContainer.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(-160, -15, 0, 0);
            gbc.anchor = GridBagConstraints.NORTH;
            storyContainer.add(storyBox, gbc);

            add(storyContainer, BorderLayout.CENTER);

            // Leaderboard
            leaderboard = new JTextArea("Top 5 Scores:\n");
            leaderboard.setFont(new Font("Courier", Font.BOLD, 14));
            leaderboard.setEditable(false);
            leaderboard.setOpaque(false);

            //This is to change the color of leaderboard
            Color[] colors = {
                new Color(0, 0, 0), // Pure Black – safest contrast
                new Color(33, 33, 33), // Charcoal – softer than black
                new Color(25, 25, 112), // Midnight Blue – stylish and readable
                new Color(85, 107, 47), // Dark Olive Green – earthy contrast
                new Color(139, 0, 0), // Dark Red – dramatic, but visible
                new Color(0, 100, 0), // Dark Green – stands out well
                new Color(72, 61, 139), // Dark Slate Blue – elegant + legible
            };

            leaderboard.setForeground(colors[new Random().nextInt(colors.length)]);
            leaderboard.setBorder(BorderFactory.createEmptyBorder(80, 30, 10, 15));
            refreshLeaderboard();
            add(leaderboard, BorderLayout.EAST);

            // Difficulty buttons
            JPanel difficultyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            difficultyPanel.setOpaque(false);

            String[] levels = {"Easy", "Medium", "Hard"};
            for (String level : levels) {
                JButton btn = new JButton(level);
                btn.setFont(new Font("Arial", Font.BOLD, 20));
                btn.setPreferredSize(new Dimension(130, 45));
                switch (level) {
                    case "Easy" ->
                        btn.setToolTipText("2 letters revealed at the start.");
                    case "Medium" ->
                        btn.setToolTipText("1 letter revealed at the start.");
                    case "Hard" ->
                        btn.setToolTipText("No letters revealed.");
                }
                btn.addActionListener(difficultyListener);
                difficultyPanel.add(btn);
            }

            add(difficultyPanel, BorderLayout.SOUTH);
        }

        // refresh top 5 leaderboard
        public void refreshLeaderboard() {
            List<String> scores = getTopScores();
            leaderboard.setText("Top 5 Scores:\n");
            for (String line : scores) {
                String[] parts = line.split(",");
                leaderboard.append(parts[0] + " - " + parts[1] + " - " + parts[2] + "\n");
            }
        }

    
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }

        // Difficulty button action
        ActionListener difficultyListener = e -> {
            currentDifficulty = e.getActionCommand();
            int prefill = switch (currentDifficulty) {
                case "Easy" ->
                    2;
                case "Medium" ->
                    1;
                default ->
                    0;
            };
            selectedWord = words[new Random().nextInt(words.length)];
            guessedLetters.clear();
            wrongGuesses = 0;
            totalSteps = 0;
            Random rand = new Random();
            while (guessedLetters.size() < prefill) {
                guessedLetters.add(selectedWord.charAt(rand.nextInt(selectedWord.length())));
            }
            startTime = System.currentTimeMillis();
            gamePanel = new GamePanel();
            mainPanel.add(gamePanel, "game");
            startGameWithCountdown();

        };
    }

    class GamePanel extends JPanel {

        // UI elements and game state
        JLabel wordLabel;
        JPanel keyboardPanel;
        Image backgroundImage;
        JPanel imagePanel;
        long lastActionTime = System.currentTimeMillis();
        boolean gameEnded = false;

        // Steve's sarcastic and mean popup quotes
        String[] steveQuotes = {
            "Guess faster. I don’t have all day.",
            "Is this your first time using letters?",
            "Wrong again. Shocking.",
            "You're really bad at this.",
            "I’m not mad, just deeply disappointed.",
            "Your guess rate is criminal.",
            "Blink once if you're lost.",
            "At this point, I trust the kidnapper more.",
            "Just pick any letter. It won’t help.",
            "This is not how heroes play.",
            "Do you hate freedom?",
            "I blinked twice. Still here.",
            "Try harder. Or don’t. Whatever.",
            "Slow guessing is a crime too.",
            "I should’ve stayed kidnapped.",
            "You’re not even trying, are you?",
            "Letters. Use them wisely. Or not.",
            "You're the twist in my tragedy.",
            "I'm running out of hope… and patience.",
            "Still guessing? Still wrong."

        };

        String[] steveFinalQuotes = {
            "Wai— I just remembe—💀",
            "Sun of a— uh guh 💀",
            "You idiot! I— *gasp* 💀",
            "No no no— not like thi—💀",
            "I hate yo—💀",
            "I should’ve picked—💀",
            "Guh— *snap* 💀",
            "Tell my—💀"
        };

        public GamePanel() {
            setLayout(new BorderLayout());

            // Start background music loop
            soundPlayer.playSound("music/bgm.wav", true);

            // Set initial background image (stage0)
            backgroundImage = new ImageIcon("images/stage0.jpg").getImage();

            // Create image panel with custom painting
            imagePanel = new JPanel(null) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(backgroundImage, 0, -10, getWidth(), getHeight(), this);
                }
            };
            imagePanel.setPreferredSize(new Dimension(640, 500));
            imagePanel.setOpaque(false);

            // Word display label (e.g., "_ _ _ _")
            wordLabel = new JLabel(getDisplayWord(), JLabel.CENTER);
            wordLabel.setFont(new Font("Courier", Font.BOLD, 36));
            wordLabel.setForeground(Color.BLACK);
            wordLabel.setBounds(120, 400, 420, 50);
            imagePanel.add(wordLabel);

            add(imagePanel, BorderLayout.NORTH);

            // Keyboard layout with A-Z buttons
            keyboardPanel = new JPanel(new GridLayout(3, 9, 5, 5));
            keyboardPanel.setOpaque(false);

            for (char c = 'A'; c <= 'Z'; c++) {
                JButton button = new JButton(String.valueOf(c));
                button.setFont(new Font("Arial", Font.BOLD, 16));
                final char letter = c;
                button.addActionListener(e -> guessLetter(button, letter));
                keyboardPanel.add(button);
            }

            // Wrap keyboard with padding and add to bottom
            JPanel keyboardContainer = new JPanel(new BorderLayout());
            keyboardContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
            keyboardContainer.add(keyboardPanel, BorderLayout.CENTER);
            add(keyboardContainer, BorderLayout.SOUTH);

            // Timer: every 8 seconds, if idle for over 8 seconds, show sarcastic popup
            new javax.swing.Timer(8000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!gameEnded && System.currentTimeMillis() - lastActionTime > 8000) {
                        showStevePopup(steveQuotes[new Random().nextInt(steveQuotes.length)]);
                    }
                }
            }).start();
        }

        // Convert word to _ _ display format, showing guessed letters
        private String getDisplayWord() {
            StringBuilder display = new StringBuilder();
            for (char c : selectedWord.toCharArray()) {
                if (guessedLetters.contains(c)) {
                    display.append(c).append(" ");
                } else {
                    display.append("_ ");
                }
            }
            return display.toString();
        }

        // Handle player letter guess logic
        private void guessLetter(JButton button, char c) {
            lastActionTime = System.currentTimeMillis();
            button.setEnabled(false);
            totalSteps++;
            if (selectedWord.indexOf(c) >= 0) {

                // Correct guess
                guessedLetters.add(c);
                wordLabel.setText(getDisplayWord());
                soundPlayer.playSound("music/correct.wav", false);

                // All letters guessed — win condition
                if (!getDisplayWord().contains("_")) {
                    gameEnded = true;
                    soundPlayer.stopBGM();
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    handleWin(elapsedTime, totalSteps);
                }
            } else {

                // Incorrect guess
                wrongGuesses++;
                soundPlayer.playSound("music/wrong.wav", false);
                updateBackground();
                imagePanel.repaint();

                // Show Steve popup every second wrong guess
                if (wrongGuesses % 2 == 0 && wrongGuesses < MAX_WRONG) {
                    showStevePopup(steveQuotes[new Random().nextInt(steveQuotes.length)]);
                }

                // Max wrong guesses — game over
                if (wrongGuesses == MAX_WRONG) {
                    gameEnded = true;
                    soundPlayer.stopBGM();

                    // Show Steve's final interrupted quote
                    String finalQuote = steveFinalQuotes[new Random().nextInt(steveFinalQuotes.length)];
                    showStevePopup(finalQuote);

                    // Delay the Game Over screen slightly to let the final quote be visible
                    javax.swing.Timer deathTimer = new javax.swing.Timer(2000, e -> {
                        showMessage("Steve is gone...\nThe word was: " + selectedWord, "Game Over");
                        resetGame();
                    });
                    deathTimer.setRepeats(false);
                    deathTimer.start(); // ← this line is what you were missing

                }

            }
        }

        // Show non-blocking sarcastic popup (Steve's messages)
        private void showStevePopup(String message) {
            JDialog popup = new JDialog(frame, false);
            popup.setUndecorated(true);
            popup.setLayout(new BorderLayout());

            JLabel label = new JLabel("Steve: " + message, JLabel.CENTER);
            label.setFont(new Font("Dialog", Font.BOLD, 16));
            label.setForeground(Color.RED);
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            popup.add(label, BorderLayout.CENTER);

            popup.setSize(480, 50);

            // Center the popup slightly higher than full center, so that it won't cover the number behind.
            popup.setLocation(
                    frame.getX() + (frame.getWidth() - popup.getWidth()) / 2,
                    frame.getY() + (frame.getHeight() - popup.getHeight()) / 2 - 80 // ← shift up
            );

            popup.setVisible(true);

            // Auto-dismiss after 4 seconds
            new javax.swing.Timer(4000, e -> popup.dispose()).start();
        }

        // Handle winning: show score, save if entered, check leaderboard
        private void handleWin(long elapsedTime, int steps) {
            int timeInSeconds = (int) (elapsedTime / 1000);
            double multiplier = switch (currentDifficulty) {
                case "Easy" ->
                    1.0;
                case "Medium" ->
                    1.5;
                case "Hard" ->
                    2.0;
                default ->
                    1.0;
            };
            int score = (int) (multiplier * 1000 - (timeInSeconds + steps * 2));

            JOptionPane.showMessageDialog(frame, "You saved Steve!\nYour Score: " + score, "Victory!", JOptionPane.INFORMATION_MESSAGE);

            // Ask for player name to save score
            String name = JOptionPane.showInputDialog(frame, "Enter your name to save the score (leave blank to skip):");
            if (name != null && !name.isEmpty()) {
                saveScore(name, currentDifficulty, score);
                welcomePanel.refreshLeaderboard();

                // Check if it's the top score
                List<String> top = getTopScores();
                if (!top.isEmpty() && top.get(0).startsWith(name)) {
                    JOptionPane.showMessageDialog(frame, "Steve: You're a legend, " + name + "!", "High Score!", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            resetGame();
        }

        //Update image depending on wrong guess count
        private void updateBackground() {
            String path = (wrongGuesses == MAX_WRONG)
                    ? "images/hanged.jpg"
                    : "images/stage" + wrongGuesses + ".jpg";
            backgroundImage = new ImageIcon(path).getImage();
        }

        // Restart prompt after win/loss
        private void resetGame() {
            int option = JOptionPane.showConfirmDialog(frame, "Play again?", "Restart", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                cardLayout.show(mainPanel, "welcome");
            } else {
                frame.dispose(); // Close game
            }
        }

        // Show a generic info dialog 
        private void showMessage(String message, String title) {
            JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // MAIN method
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        new HangmanGame();
    }

    // Score saving
    public static void saveScore(String name, String difficulty, int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scores.txt", true))) {
            writer.write(name + "," + difficulty + "," + score);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get top scores
    public static List<String> getTopScores() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("scores.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException ignored) {
        }

        lines.sort((a, b) -> Integer.compare(
                Integer.parseInt(b.split(",")[2]),
                Integer.parseInt(a.split(",")[2])
        ));

        return lines.subList(0, Math.min(5, lines.size()));
    }
}

// Class for handling background music and sound effects
class SoundPlayer {

    private Clip bgmClip;

    public void playSound(String path, boolean loop) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(path));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                bgmClip = clip;
            } else {
                clip.start();
            }
        } catch (Exception e) {
            System.out.println("Sound error: " + path);
        }
    }

    public void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }
    }
}
