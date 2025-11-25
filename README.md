# save-steve-hangman-java
"This is Steve. He was kidnapped. The kidnapper is playing a deadly game. Guess the word or Steve gets hanged."

Small Java Swing hangman game where the player must guess a hidden word to "save Steve". Includes simple UI, sound effects, background music, and a local leaderboard stored in `scores.txt`.

## Features
- Java Swing GUI with animated backgrounds per wrong guess
- Keyboard-style letter input (A–Z)
- Difficulty levels: Easy / Medium / Hard
- Background music and sound effects (optional)
- Local top-5 leaderboard saved to `scores.txt`

## Requirements
- Java 8+ (JDK)
- Audio support via javax.sound.sampled
- Project folder layout:
  - `images/` (UI images e.g. stage0.jpg, hanged.jpg, welcome.jpg, icon.png)
  - `music/` (bgm.wav, correct.wav, wrong.wav)
  - `scores.txt` (created automatically when saving scores)

## Run
- Compile:
  javac HangmanGame.java
- Run:
  java HangmanGame

## Notes
- If audio or images are missing, the game still runs but will log missing asset messages.
- Scores are appended to `scores.txt` in the format: `name,difficulty,score`.

## License
- MIT License
- Copyright (c) 2025 Jerin Sirija
