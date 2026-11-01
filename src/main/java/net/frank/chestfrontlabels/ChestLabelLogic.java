package net.frank.chestfrontlabels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Reine Regeln ohne laufendes Spiel: Text bereinigen und Schriftgröße auf der Truhenfront
public final class ChestLabelLogic {

    public static final int MAX_LENGTH = 24;
    // Wie ein Schild: Zeilen brechen bei LINE_WIDTH_PX Schriftpixeln um, Enter beginnt eine neue Zeile
    public static final int MAX_LINES = 2;
    public static final int LINE_WIDTH_PX = 90;
    // Größte Schrift in Blöcken pro Schriftpixel, etwas größer als auf Schildern (1/96)
    public static final float MAX_TEXT_SCALE = 1.0f / 80.0f;
    // Nutzbare Breite einer einzelnen Truhenfront in Blöcken (Front ist 14/16 breit, je 1/16 Rand)
    public static final float SINGLE_FRONT_WIDTH = 12.0f / 16.0f;
    // Doppeltruhe: Front 30/16 breit, je 1/16 Rand
    public static final float DOUBLE_FRONT_WIDTH = 28.0f / 16.0f;

    private ChestLabelLogic() {}

    // Höchstens MAX_LINES Zeilen ohne Steuer- und Formatierungszeichen, je auf MAX_LENGTH gekürzt.
    // Leerer Text heißt: Beschriftung entfernen.
    public static String sanitize(String raw) {
        List<String> lines = new ArrayList<>(Arrays.stream(raw.split("\n", -1))
                .limit(MAX_LINES)
                .map(ChestLabelLogic::sanitizeLine)
                .toList());
        while (!lines.isEmpty() && lines.getLast().isEmpty()) lines.removeLast();
        return String.join("\n", lines);
    }

    private static String sanitizeLine(String raw) {
        StringBuilder clean = new StringBuilder();
        // Formatierungscodes bestehen aus Paragraphenzeichen plus Codezeichen
        raw.replaceAll("§.?", "")
                .codePoints()
                .filter(c -> c >= 0x20 && c != 0x7F)
                .limit(MAX_LENGTH)
                .forEach(clean::appendCodePoint);
        return clean.toString().strip();
    }

    // Eine Doppeltruhe hat eine Beschriftung. Haben beide Hälften eine (zwei beschriftete Truhen zusammengestellt),
    // gilt die der linken Hälfte.
    public static String combine(String left, String right) {
        return left.isEmpty() ? right : left;
    }

    // Lange Texte werden kleiner, damit sie auf die Front passen
    public static float textScale(int textWidthPx, float frontWidth) {
        if (textWidthPx <= 0) return MAX_TEXT_SCALE;
        return Math.min(MAX_TEXT_SCALE, frontWidth / textWidthPx);
    }
}
