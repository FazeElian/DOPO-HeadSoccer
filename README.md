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
| **Tournament** | 8 teams: quarterfinals, semifinal and final against the Expert machine. 500-point prize for the champion. | PVM only | By goals or by time |

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
- You play your match on screen against the machine, always with the **Expert** profile. The other matches of each round are simulated.
- Each of your matches is played in a stadium drawn at random.
- After each round the bracket screen shows who goes on. If you lose a match, or end it by hand, you are out.
- Winning the final makes you the **champion** and gives you the **500-point** prize on top of the match points.

### Stadiums
There are 9 stadiums: *default*, *day*, *sunset*, *rain*, *snow*, *fog*, *storm*, *albiceleste* and *empty*. Only the picture changes (weather, time of day and crowd); the field and its physics are the same in all of them.
- **Arcade:** you choose the stadium after choosing the rival, before the ball.
- **Tournament:** every match is played in a random stadium.

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
In Arcade PVM you choose the machine's profile (the tournament always uses *Expert*):

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
- Points, purchases and upgrades are **saved automatically** (see *Saving*).

### Saving
**Progress (automatic).** The profiles of both players (points, purchases, upgrades and selected ball) are saved in `DOPOSoccer.progress`, in the folder the game is run from (the project root). You never choose this file.
- It is loaded when the game opens. If it does not exist, a new one is created with both players from zero. If it is damaged, the game warns you and starts from zero.
- It is written again every time a profile changes: when you buy a character or a ball, upgrade a stat, or earn points at the end of a match.
- The volumes of *Settings* are not saved: they start at their default values every time.

**Saved games (manual).** A match in progress can be saved and played later:
- **Save:** pause the match and press *GUARDAR PARTIDA*. Choose where to save it; the file gets the **`.ds`** extension (DOPO Soccer) if you don't write it. Arcade, survival and tournament matches can be saved, but not a match that already ended.
- **Open:** on the home screen press *ABRIR PARTIDA* and choose a `.ds` file. The match opens **paused**, as it was (score, time, players, ball, stadium, tournament bracket or survival series); press *REANUDAR* to go on.
- An opened game uses your **current** progress (the one in `DOPOSoccer.progress`), not the one you had when you saved it, so opening an old game never takes back points or purchases.
- If the file is not a saved game or is damaged, the game shows an error and the current game is not touched.

---

## Requirements
- **JDK 21** or newer (`java -version` and `javac -version` must work in the terminal).
- The libraries are already included in [`lib/`](lib/):
  - `dyn4j-5.0.2.jar`: 2D physics engine.
  - `junit-platform-console-standalone-1.11.4.jar`: to compile and run the tests.
- **Maven 3.9+** only to generate the reports and the Javadoc (see below). It is not needed to play.

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

### Reports and documentation (JaCoCo, PMD and Javadoc)
The [`pom.xml`](pom.xml) runs the tests and writes two reports into [`docs/informes/`](docs/informes/), and the code documentation into [`docs/apidocs/`](docs/apidocs/):

| Output | Tool | What it has | Open |
|--------|------|-------------|------|
| **Tests** | JaCoCo | Test coverage (instructions and branches) of the `domain` package only | `docs/informes/jacoco/index.html` |
| **Good practices** | PMD | Static analysis of `domain` and `presentation` (tests are not analyzed) | `docs/informes/pmd/pmd.html` |
| **Documentation** | Javadoc | Every class of `domain` and `presentation`, with their attributes and methods (also the private ones), authors and version | `docs/apidocs/index.html` |

**Requirement:** [Maven 3.9+](https://maven.apache.org/download.cgi) (`mvn -v` must work). The first run downloads dyn4j, JUnit and the plugins from Maven Central, so it needs internet.

From the project root:
```bash
mvn clean verify
```

This compiles the code, runs all the tests (JUnit 5), and generates the two reports and the Javadoc. When it finishes, open the HTML files in a browser:
```powershell
# Windows (PowerShell)
start docs/informes/jacoco/index.html
start docs/informes/pmd/pmd.html
start docs/apidocs/index.html
```
```bash
# Linux / macOS
xdg-open docs/informes/jacoco/index.html   # macOS: open
xdg-open docs/informes/pmd/pmd.html
xdg-open docs/apidocs/index.html
```

To generate only the Javadoc, without Maven, use the `javadoc` tool of the JDK (from the project root):
```bash
javadoc -encoding UTF-8 -docencoding UTF-8 -charset UTF-8 -cp "lib/dyn4j-5.0.2.jar" -sourcepath src -subpackages domain:presentation -private -author -version -d docs/apidocs
```
(If `javadoc` is not found, call it with its full path, for example `"C:\Program Files\Java\jdk-21\bin\javadoc.exe"`.)

How to read them:
- **JaCoCo:** the main page shows the total coverage of `domain`. Click the package, then a class, to see its code colored line by line: green means covered by the tests, yellow means a partly covered branch, and red means not covered.
- **PMD:** lists each rule violation with its file, line and rule name. Click the rule name to read why it is a bad practice. `pmd.xml` has the same data for other tools.

Notes:
- The reports in `docs/informes/` and the Javadoc in `docs/apidocs/` are files in the repository: they show the code as it was the last time `mvn clean verify` was run. **Run it again after changing the code or the tests** to update them.
- A failing test is shown in the console (`Tests run: ..., Failures: ...`) but does not stop the build, so the reports are always generated. The detail of each test is in `target/surefire-reports/`.
- To run only the tests, without the reports: `mvn test`.
- Maven compiles into `target/`, which can be deleted at any time (`mvn clean`).

### From an IDE
- **VS Code / IntelliJ:** add both `.jar` files from `lib/` as project libraries, mark `src` as the source folder and run `presentation.DOPOSoccerGUI` with the project root as the working directory. Because of the `pom.xml`, the IDE may offer to import the project as Maven: both ways work (Maven compiles into `target/` instead of `bin/`).
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
│   ├── informes/       JaCoCo and PMD reports (generated by mvn verify)
│   └── apidocs/        Javadoc of the code (generated by mvn verify)
├── pom.xml             Maven build: tests, JaCoCo, PMD and Javadoc
├── DOPOSoccer.progress Saved progress of both players (created when the game runs)
└── DOPOSoccer.log      Error log (created when an error happens)
```

### Design
- **Facade:** the GUI only talks to `domain.DOPOSoccer`.
- **Strategy:** `MatchType` (match rules: `GoalsMatch`, `TimeMatch`, `SurvivalMatch`) and `PowerShot` (each character's shot).
- **Template Method:** `AIController` fixes the decision loop and each AI profile implements `decide`.
- **Modes:** `GameMode` has `ArcadeMode` (one match, or a survival series) and `TournamentMode` (the bracket, with a `BracketMatch` per match).
- Effects (`Effect`) and modes (`GameMode`) are extended with new subclasses without touching the rest of the code.
- Physics (collisions, bounces, gravity) is delegated to **dyn4j**, and only `PhysicsEngine` uses it.
- **Persistence** uses Java serialization:
  - `saveProgress` / `loadProgress` write and read the `PlayerProfile[]` of both players in the fixed progress file.
  - `saveGame` writes the whole `DOPOSoccer`, and the static `openGame` reads it back as a **new** `DOPOSoccer` that the GUI uses from then on.
  - `profiles` and `settings` are `transient` (they are not part of a saved game), and so are the dyn4j objects of `PhysicsEngine`: the physics world is rebuilt from the saved players and ball on the first step after opening.
  - Errors reading or writing a file become a `DOPOSoccerException` (`SAVE_FAILED`, `LOAD_FAILED`, `GAME_SAVE_FAILED`, `GAME_OPEN_FAILED`), which the GUI logs in `DOPOSoccer.log` and shows.
