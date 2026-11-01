# Chest Labels

Kleine Fabric-Mod für Minecraft 26.3 mit genau einer Funktion: Truhen lassen sich wie Schilder beschriften.

## Benutzung

1. Mit leeren Händen schleichen und die Truhe rechtsklicken.
2. Text eingeben (max. 24 Zeichen), mit Enter oder "Fertig" bestätigen.
3. Der Text steht auf der Truhenfront unter dem Schloss. Lange Texte werden kleiner gezeichnet.

Leeren Text bestätigen entfernt die Beschriftung. Gilt für normale Truhen, Fallentruhen und Doppeltruhen (jede Hälfte einzeln).

Die Mod muss auf Server und Client installiert sein. Die Beschriftung wird mit der Truhe gespeichert und an alle Spieler synchronisiert.

## Entwicklung

- Java 25, Fabric Loader >= 0.20.0, Fabric API
- Build und Tests: `./gradlew build`
- Formatieren: `./gradlew spotlessApply`
- Client starten: `./gradlew runClient`
