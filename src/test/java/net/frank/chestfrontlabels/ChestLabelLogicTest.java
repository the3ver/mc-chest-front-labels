package net.frank.chestfrontlabels;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void nullAndBlankMeanRemove() {
        assertEquals("", ChestLabelLogic.sanitize(null));
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
}
