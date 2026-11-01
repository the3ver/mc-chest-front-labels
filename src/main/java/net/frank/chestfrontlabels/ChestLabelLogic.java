package net.frank.chestfrontlabels;

// Reine Regeln ohne laufendes Spiel: Text bereinigen und Schriftgröße auf der Truhenfront
public final class ChestLabelLogic {

    public static final int MAX_LENGTH = 24;
    // Größte Schrift in Blöcken pro Schriftpixel, etwas größer als auf Schildern (1/96)
    public static final float MAX_TEXT_SCALE = 1.0f / 80.0f;
    // Nutzbare Breite einer einzelnen Truhenfront in Blöcken (Front ist 14/16 breit, je 1/16 Rand)
    public static final float SINGLE_FRONT_WIDTH = 12.0f / 16.0f;
    // Doppeltruhe: Front 30/16 breit, je 1/16 Rand
    public static final float DOUBLE_FRONT_WIDTH = 28.0f / 16.0f;

    private ChestLabelLogic() {}

    // Entfernt Steuer- und Formatierungszeichen, kürzt auf MAX_LENGTH. Leerer Text heißt: Beschriftung entfernen.
    public static String sanitize(String raw) {
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
