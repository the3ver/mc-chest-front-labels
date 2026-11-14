package net.frank.chestfrontlabels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ChestLabelLogicTest {

    @Test
    void keepsPlainText() {
        assertEquals("Erze & Kohle", ChestLabelLogic.sanitize("Erze & Kohle"));
    }

    @Test
    void trimsWhitespace() {
        assertEquals("Holz", ChestLabelLogic.sanitize("  Holz  "));
    }

    @Test
    void blankMeansRemove() {
        assertEquals("", ChestLabelLogic.sanitize("   "));
    }

    @Test
    void stripsControlAndFormattingCharacters() {
        assertEquals("Rotes Holz", ChestLabelLogic.sanitize("§cRotes Holz§r\n"));
        assertEquals("ab", ChestLabelLogic.sanitize("a\tb"));
        assertEquals("ab", ChestLabelLogic.sanitize("a\u0000b\u007f"));
    }

    @Test
    void cutsToMaxLengthWithoutSplittingSurrogates() {
        String emoji = "📦"; // ein Codepoint, zwei chars
        String label = ChestLabelLogic.sanitize(emoji.repeat(ChestLabelLogic.MAX_LENGTH + 5));
        assertEquals(ChestLabelLogic.MAX_LENGTH, label.codePointCount(0, label.length()));
        assertEquals(emoji.repeat(ChestLabelLogic.MAX_LENGTH), label);
    }

    @Test
    void keepsTwoLinesAndDropsTheRest() {
        assertEquals("Erze\nKohle", ChestLabelLogic.sanitize("Erze\nKohle\nEisen"));
        assertEquals("\nUnten", ChestLabelLogic.sanitize("\nUnten"));
        assertEquals("Oben", ChestLabelLogic.sanitize(" Oben \n  "));
    }

    @Test
    void doubleChestPrefersLeftLabel() {
        assertEquals("Links", ChestLabelLogic.combine("Links", "Rechts"));
        assertEquals("Rechts", ChestLabelLogic.combine("", "Rechts"));
        assertEquals("", ChestLabelLogic.combine("", ""));
    }

    @Test
    void doubleFrontFitsLongerTextAtSameScale() {
        int width = 100;
        assertEquals(
                ChestLabelLogic.MAX_TEXT_SCALE, ChestLabelLogic.textScale(width, ChestLabelLogic.DOUBLE_FRONT_WIDTH));
        assertEquals(
                ChestLabelLogic.SINGLE_FRONT_WIDTH,
                ChestLabelLogic.textScale(width, ChestLabelLogic.SINGLE_FRONT_WIDTH) * width,
                1e-6);
    }

    @Test
    void shortTextUsesMaxScale() {
        assertEquals(ChestLabelLogic.MAX_TEXT_SCALE, ChestLabelLogic.textScale(10, ChestLabelLogic.SINGLE_FRONT_WIDTH));
        assertEquals(ChestLabelLogic.MAX_TEXT_SCALE, ChestLabelLogic.textScale(0, ChestLabelLogic.SINGLE_FRONT_WIDTH));
    }

    @Test
    void longTextShrinksToFrontWidth() {
        int width = 200;
        assertEquals(
                ChestLabelLogic.SINGLE_FRONT_WIDTH,
                ChestLabelLogic.textScale(width, ChestLabelLogic.SINGLE_FRONT_WIDTH) * width,
                1e-6);
    }

    // Die Kontur trennt die Schrift vom hellen und dunklen Holz der Truhe (WCAG-Kontrast mindestens 7:1)
    @Test
    void outlineContrastsWithText() {
        double ratio = contrast(ChestLabelLogic.TEXT_COLOR, ChestLabelLogic.OUTLINE_COLOR);
        assertTrue(ratio >= 7, "Kontrast " + ratio);
    }

    private static double contrast(int a, int b) {
        double la = luminance(a);
        double lb = luminance(b);
        return (Math.max(la, lb) + 0.05) / (Math.min(la, lb) + 0.05);
    }

    private static double luminance(int argb) {
        return 0.2126 * channel(argb >> 16) + 0.7152 * channel(argb >> 8) + 0.0722 * channel(argb);
    }

    private static double channel(int value) {
        double c = (value & 0xFF) / 255.0;
        return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
    }
}
