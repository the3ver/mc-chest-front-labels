package net.frank.chestfrontlabels;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class ResourceConsistencyTest {

    // Mixin verbietet direkte Zugriffe auf Klassen im Mixin-Paket: dort nur gelistete Mixins
    @Test
    void mixinPackageHoldsExactlyTheListedMixins() throws IOException {
        var config = JsonParser.parseString(
                        Files.readString(Path.of("src/client/resources/chest-front-labels.client.mixins.json")))
                .getAsJsonObject();
        Set<String> listed = new TreeSet<>();
        config.getAsJsonArray("mixins").forEach(e -> listed.add(e.getAsString()));

        Path dir =
                Path.of("src/client/java", config.get("package").getAsString().replace('.', '/'));
        try (Stream<Path> files = Files.list(dir)) {
            Set<String> present = files.map(p -> p.getFileName().toString().replace(".java", ""))
                    .collect(Collectors.toCollection(TreeSet::new));
            assertEquals(listed, present);
        }
    }

    @Test
    void languageFilesHaveSameKeys() throws IOException {
        assertEquals(langKeys("en_us"), langKeys("de_de"));
    }

    private static Set<String> langKeys(String lang) throws IOException {
        var json = Files.readString(Path.of("src/main/resources/assets/chest-front-labels/lang/" + lang + ".json"));
        return new TreeSet<>(JsonParser.parseString(json).getAsJsonObject().keySet());
    }
}
