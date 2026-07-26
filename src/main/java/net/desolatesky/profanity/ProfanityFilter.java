package net.desolatesky.profanity;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.jspecify.annotations.Nullable;

import java.text.Normalizer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class ProfanityFilter {

    private static final Map<Character, Character> LEET = Map.of(
            '0', 'o',
            '1', 'i',
            '3', 'e',
            '4', 'a',
            '5', 's',
            '7', 't',
            '@', 'a',
            '$', 's'
    );

    private final Trie words;

    public ProfanityFilter(List<String> words) {
        this.words = Trie.builder()
                .addKeywords(words)
                .ignoreCase()
                .build();
    }

    public @Nullable String filter(final String message) {
        String filtered = Normalizer.normalize(message, Normalizer.Form.NFKC);
        final StringBuilder builder = new StringBuilder();
        for (final char c : filtered.toCharArray()) {
            builder.append(LEET.getOrDefault(c, c));
        }
        filtered = builder.toString();
        filtered = filtered.replaceAll("[^\\p{L}\\p{N}\\s]", "");
        filtered = filtered.replaceAll("\\s+", " ").trim();
        filtered = filtered.replaceAll("(.)\\1{2,}", "$1");

        final Collection<Emit> emits = this.words.parseText(filtered);
        if (emits.isEmpty()) {
            return null;
        }
        final StringBuilder finalBuilder = new StringBuilder(filtered);
        for (final Emit emit : emits) {
            final int start = emit.getStart();
            for (int i = start; i <= emit.getEnd(); i++) {
                finalBuilder.setCharAt(i, '*');
            }
        }
        return finalBuilder.toString();
    }

}
