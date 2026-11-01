# Chest Front Labels – Agent Guidelines

Fabric-Mod für Minecraft 26.3 (Java 25). Einzige Funktion: Truhen wie Schilder beschriften. Klein halten, keine weiteren Features ohne Auftrag.

## Arbeitsregeln
- Kent Beck: erst die Änderung einfach machen (eigener `refactor:`-Commit, Tests grün), dann die einfache Änderung (`feat:`).
- Matt Pocock: Maximum Conciseness in Antworten an Frank. Kürzest mögliche Antwort, nur Ergebnis und offene Punkte.

## Aufbau
- `ChestLabelLogic`: reine Regeln (Text bereinigen, Schriftgröße), unit-getestet.
- `ChestLabels`: Attachment `chest-front-labels:label` am `ChestBlockEntity` (persistent, an alle Clients synchronisiert), Payloads, Schleichen-Rechtsklick.
- Client: `ChestLabelScreen` (Eingabe), `mixin/ChestRendererMixin` zeichnet den Text. Klassen, die Mixins referenzieren (z. B. `ChestLabelHolder`), gehören nicht ins Mixin-Paket.

## Git
- `main` ist per Ruleset geschützt (`.github/rulesets/main.json`): nur per Pull Request, Check `build` muss grün sein. Auf einem Branch arbeiten, PR öffnen.

## Commands
- Prüfen: `bash scripts/check.sh` (Format, Error Prone, NullAway, Tests; grün eine Zeile). Läuft als Stop-Hook.
- Commit-Hook aktivieren: `git config core.hooksPath .githooks` (Conventional Commits).
- Formatieren: `./gradlew spotlessApply`
- Client-Gametest: `./gradlew runClientGameTest` (ca. 30 s, öffnet ein Fenster). Screenshot: `build/run/clientGameTest/screenshots/`.
- Client: `./gradlew runClient`. Mixin-Fehler zeigen sich erst beim Start im Log.
- Version steht nur in `gradle.properties`. Nach dem Erhöhen: Tag `v<version>` auf den Commit, dann `uvx git-cliff -o CHANGELOG.md` (nur `feat`/`fix`).
