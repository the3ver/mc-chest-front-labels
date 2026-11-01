# Chest Front Labels

Kleine Fabric-Mod für Minecraft 26.3 mit genau einer Funktion: Truhen lassen sich wie Schilder beschriften.

## Benutzung

1. Mit leeren Händen schleichen und die Truhe rechtsklicken.
2. Text eingeben wie auf einem Schild: bis zu zwei Zeilen, Enter beginnt die zweite. Mit "Fertig" bestätigen.
3. Der Text steht auf der Truhenfront unter dem Schloss. Lange Texte werden kleiner gezeichnet.

Leeren Text bestätigen entfernt die Beschriftung. Gilt für normale Truhen und Fallentruhen. Eine Doppeltruhe hat eine gemeinsame, breitere Beschriftung über beide Hälften.

Die Mod muss auf Server und Client installiert sein. Die Beschriftung wird mit der Truhe gespeichert und an alle Spieler synchronisiert.

## Entwicklung

- Java 25, Fabric Loader >= 0.20.0, Fabric API
- Build und Tests: `./gradlew build`
- Formatieren: `./gradlew spotlessApply`
- Client starten: `./gradlew runClient`
