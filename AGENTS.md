# Chest Front Labels – Agent Guidelines

Fabric-Mod für Minecraft 26.3 (Java 25). Einzige Funktion: Truhen wie Schilder beschriften. Klein halten, keine weiteren Features ohne Auftrag.

## Arbeitsregeln
- Kent Beck: erst die Änderung einfach machen (eigener `refactor:`-Commit, Tests grün), dann die einfache Änderung (`feat:`).
- Matt Pocock: Maximum Conciseness. So wenig Code, Kommentare und Text wie möglich.

## Aufbau
- `ChestLabelLogic`: reine Regeln (Text bereinigen, Schriftgröße), unit-getestet.
- `ChestLabels`: Attachment `chest-front-labels:label` am `ChestBlockEntity` (persistent, an alle Clients synchronisiert), Payloads, Schleichen-Rechtsklick.
- Client: `ChestLabelScreen` (Eingabe), `mixin/ChestRendererMixin` zeichnet den Text. Klassen, die Mixins referenzieren (z. B. `ChestLabelHolder`), gehören nicht ins Mixin-Paket.

## Commands
- Build + Tests: `./gradlew build` (Error Prone mit `-Werror`, Spotless-Check)
- Formatieren: `./gradlew spotlessApply`
- Client: `./gradlew runClient`. Mixin-Fehler zeigen sich erst beim Start im Log.
- Version steht nur in `gradle.properties`.
