# DOPO Soccer

A 2D "big head" soccer game built with Java Swing. Two players face each other on a side-view field, each defending a goal, and try to score by running, jumping, kicking and using their team's unique **power shot**.

Project for the **DOPO** course (Object-Oriented Development), Escuela Colombiana de Ingeniería Julio Garavito.

**Authors:** Elian Ibarra · Oscar Poveda

---

## How to play

### Modalities
- **Player vs Player (PVP):** two people on the same keyboard.
- **Player vs Machine (PVM):** the right-side player is controlled by the AI.

### Game modes
| Mode | Description | Modalities | Match types |
|------|-------------|------------|-------------|
| **Arcade** | A match between the two chosen characters (or a series of matches, in survival). | PVP and PVM | By goals, by time, survival (PVM only) |
| **Tournament** | 8 teams: quarterfinals, semifinal and final against the machine. 500-point prize for the champion. | PVM only | By goals or by time |

### Match types
| Type | Rule |
|------|------|
| **By goals** | The first player to reach the goal target wins. |
| **By time** | The player with more goals when time runs out wins. On a tie, **sudden death**: the next goal wins. |
| **Survival** | Each player starts with 3 balls and loses one for every goal conceded. The match ends when a player has no balls left. Only against the machine. |

### Survival
Survival is a **series** of matches against the machine:
- The machine starts **every** match with 3 balls; you keep the balls you have left from one match to the next.
- While you win, a new match starts against the same rival (match 1, 2, 3...).
- The series ends when you run out of balls. The result card shows how many matches you played and won.
- Each match won or lost gives the usual points (see *Progression*).

### Tournament
- You choose your team, the ball, the resolution and the match type (by goals or by time). The 7 rivals are the other teams, drawn into a random bracket.
- You play your match on screen against the machine. The other matches of each round are simulated.
- After each round the bracket screen shows who goes on. If you lose a match, or end it by hand, you are out.
- Winning the final makes you the **champion** and gives you the **500-point** prize on top of the match points.

### Controls
| Action | Player 1 (left) | Player 2 (right) |
|--------|-----------------|------------------|
| Move | `A` / `D` | `←` / `→` |
| Jump | `W` | `↑` |
| Kick | `S` | `↓` |
| Power shot | `Q` | Right `Ctrl` |
| Pause / resume | `Esc` | `Esc` |

### Characters and power shots
There are 8 national teams sharing 4 power shots. Spain and Japan must be unlocked in the store. The in-game (Spanish) names are shown in parentheses.

| Power shot | Teams | Effect on the rival it hits |
|------------|-------|-----------------------------|
| **Blue Aura** (*Aura Azul*): straight and very fast | South Korea, Spain (800 pts) | Pushes them back toward their own goal |
| **Lightning** (*Rayo*): straight and electrified, the highest damage | Cameroon, Argentina | Electrocuted for 3 s |
| **Tornado** (*Tornado*): a low whirlwind that bounces along the ground | Nigeria, Japan (1200 pts) | Thrown into the air and knocked out for 3 s |
| **Illusion** (*Ilusión*): the ball splits into 8, 7 of them fake | United States, Colombia | Confused for 2.5 s |

The power bar fills over time (10 s to charge fully) and gets a bonus when you concede a goal. Kicks and power shots deal damage: when a player's life reaches 0, they are stunned for 3 s.

### Artificial intelligence
In PVM mode you choose the machine's profile:

| Profile | Behavior |
|---------|----------|
| **Clumsy** (*Torpe*) | Goes for the ball only 40 % of the time; otherwise it makes random moves. |
| **Defensive** (*Defensiva*) | Guards its goal and only comes out when the ball is in its half. |
| **Offensive** (*Ofensiva*) | Always chases the ball to attack. |
| **Expert** (*Experta*) | Reacts twice as fast. Clears the ball when it is in its half, attacks when it is closer to the ball than the rival, and goes back to guard its goal when the rival has the ball. Shoots as soon as it is in front of the rival goal. |

### Progression
- Each match gives **100 points** to the winner and **25** to the loser. Against the machine only player 1 gets points. A match ended by hand gives no points.
- The tournament champion also wins **500 points**.
- Points are spent in the **store**: unlock characters, buy balls and upgrade stats (Speed, Jump, Kick, Survival, from 1 to 10; each level adds 10 %).
- **Settings** let you change the music and sound-effects volume.
- The field can be played in three resolutions: 960×540, 1280×720 and 1600×900.
- Points and purchases last while the game is open.

### Under construction
Saving and loading (**save game**, **open game**, and saving the progress) are in the design but not built yet: those buttons show *"Opción en construcción."*

---

## Requirements
- **JDK 21** or newer (`java -version` and `javac -version` must work in the terminal).
- The libraries are already included in [`lib/`](lib/):
  - `dyn4j-5.0.2.jar`: 2D physics engine.
  - `junit-platform-console-standalone-1.11.4.jar`: to compile and run the tests.

## Build and run

Run every command from the **project root** (the folder that contains `src`, `lib` and `assets`). If you run it from another folder, the game can't find its images and sounds.

### Windows (PowerShell)
```powershell
# Compile
javac -encoding UTF-8 -cp "lib/*" -d bin (Get-ChildItem src -Recurse -Filter *.java).FullName

# Run
java -cp "bin;lib/*" presentation.DOPOSoccerGUI
```

### Linux / macOS / Git Bash
```bash
# Compile
javac -encoding UTF-8 -cp "lib/*" -d bin $(find src -name "*.java")

# Run  (in Git Bash on Windows, use ";" instead of ":")
java -cp "bin:lib/*" presentation.DOPOSoccerGUI
```

Recompile after every code change before running again.

### Run the tests
After compiling:
```powershell
java -jar lib/junit-platform-console-standalone-1.11.4.jar execute -cp "bin;lib/dyn4j-5.0.2.jar" --scan-classpath
```
(On Linux/macOS, use `:` instead of `;`.)

### Coverage and code quality reports (JaCoCo and PMD)
The [`pom.xml`](pom.xml) runs the tests and writes two reports into [`docs/informes/`](docs/informes/):

| Report | Tool | What it measures | Open |
|--------|------|------------------|------|
| **Tests** | JaCoCo | Test coverage (instructions and branches) of the `domain` package only | `docs/informes/jacoco/index.html` |
| **Good practices** | PMD | Static analysis of `domain` and `presentation` (tests are not analyzed) | `docs/informes/pmd/pmd.html` |

**Requirement:** [Maven 3.9+](https://maven.apache.org/download.cgi) (`mvn -v` must work). The first run downloads dyn4j, JUnit and the plugins from Maven Central, so it needs internet.

From the project root:
```bash
mvn clean verify
```

This compiles the code, runs all the tests (JUnit 5), and generates both reports. When it finishes, open the HTML files in a browser:
```powershell
# Windows (PowerShell)
start docs/informes/jacoco/index.html
start docs/informes/pmd/pmd.html
```
```bash
# Linux / macOS
xdg-open docs/informes/jacoco/index.html   # macOS: open
xdg-open docs/informes/pmd/pmd.html
```

How to read them:
- **JaCoCo:** the main page shows the total coverage of `domain`. Click the package, then a class, to see its code colored line by line: green means covered by the tests, yellow means a partly covered branch, and red means not covered.
- **PMD:** lists each rule violation with its file, line and rule name. Click the rule name to read why it is a bad practice. `pmd.xml` has the same data for other tools.

Notes:
- A failing test is shown in the console (`Tests run: ..., Failures: ...`) but does not stop the build, so the reports are always generated. The detail of each test is in `target/surefire-reports/`.
- To run only the tests, without the reports: `mvn test`.
- Maven compiles into `target/`, which can be deleted at any time (`mvn clean`).

### From an IDE
- **VS Code / IntelliJ:** add both `.jar` files from `lib/` as project libraries, mark `src` as the source folder and run `presentation.DOPOSoccerGUI` with the project root as the working directory.
- **BlueJ:** *Tools → Preferences → Libraries → Add File…*, add `lib/dyn4j-5.0.2.jar` and restart BlueJ.

---

## Project structure
```
DOPO-HeadSoccer/
├── src/
│   ├── domain/         Game logic (DOPOSoccer facade, modes, match types, players, physics, AI, shots, effects)
│   ├── presentation/   Swing GUI (DOPOSoccerGUI and views: menu, store, arcade, survival, tournament, match)
│   └── tests/          JUnit 5 unit tests of the domain
├── assets/             Images, fonts and sounds
├── lib/                External libraries (dyn4j, JUnit)
├── design/             UML model in Astah (DOPOSoccer.asta)
├── docs/               Documentation and retrospectives
│   └── informes/       JaCoCo and PMD reports (generated by mvn verify)
└── pom.xml             Maven build: tests, JaCoCo and PMD
```

### Design
- **Facade:** the GUI only talks to `domain.DOPOSoccer`.
- **Strategy:** `MatchType` (match rules: `GoalsMatch`, `TimeMatch`, `SurvivalMatch`) and `PowerShot` (each character's shot).
- **Template Method:** `AIController` fixes the decision loop and each AI profile implements `decide`.
- **Modes:** `GameMode` has `ArcadeMode` (one match, or a survival series) and `TournamentMode` (the bracket, with a `BracketMatch` per match).
- Effects (`Effect`) and modes (`GameMode`) are extended with new subclasses without touching the rest of the code.
- Physics (collisions, bounces, gravity) is delegated to **dyn4j**, and only `PhysicsEngine` uses it.
