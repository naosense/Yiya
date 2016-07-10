package com.pingao.yiya;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wocanmei on 2016/7/9.
 */
public class Yiya {
    private Yiya() {}

    public static List<Word> words(String text) {
        Parser parser = new Parser(text);
        return parser.parse();
    }

    public static List<Word> words(String text, int n) {
        Parser parser = new Parser(text);
        List<Word> words = parser.parse();
        return words.size() > n ? words.subList(0, n) : words;
    }

    public static List<String> topn(String text, int n) {
        List<Word> words = words(text, n);
        List<String> strings = new ArrayList<>(words.size());
        words.forEach(word -> strings.add(word.getValue()));
        return strings;
    }
}
