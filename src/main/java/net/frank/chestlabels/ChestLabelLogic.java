package net.frank.chestlabels;

// Reine Regeln ohne laufendes Spiel: Text bereinigen und Schriftgröße auf der Truhenfront
public final class ChestLabelLogic {

    public static final int MAX_LENGTH = 24;
    // Größte Schrift in Blöcken pro Schriftpixel, etwas größer als auf Schildern (1/96)
    public static final float MAX_TEXT_SCALE = 1.0f / 80.0f;
    // Nutzbare Breite der Truhenfront in Blöcken (Front ist 14/16 breit, je 1/16 Rand)
    public static final float FRONT_WIDTH = 12.0f / 16.0f;

    private ChestLabelLogic() {}

    // Entfernt Steuer- und Formatierungszeichen, kürzt auf MAX_LENGTH. Leerer Text heißt: Beschriftung entfernen.
    public static String sanitize(String raw) {
        if (raw == null) return "";
        StringBuilder clean = new StringBuilder();
        // Formatierungscodes bestehen aus Paragraphenzeichen plus Codezeichen
        raw.replaceAll("§.?", "")
                .codePoints()
                .filter(c -> c >= 0x20 && c != 0x7F)
                .limit(MAX_LENGTH)
                .forEach(clean::appendCodePoint);
        return clean.toString().strip();
    }

    // Lange Texte werden kleiner, damit sie auf die Front passen
    public static float textScale(int textWidthPx) {
        if (textWidthPx <= 0) return MAX_TEXT_SCALE;
        return Math.min(MAX_TEXT_SCALE, FRONT_WIDTH / textWidthPx);
    }
}
