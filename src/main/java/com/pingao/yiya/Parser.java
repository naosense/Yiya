package com.pingao.yiya;

import java.util.*;

/**
 * Created by wocanmei on 2016/8/4.
 */
abstract class Parser {
    // 词的最大长度
    protected static final int MAX_WORD_LEN = 6;
    // 待处理的字符串
    protected String text;
    // 内部词典
    protected Map<String, Word> dict;
    // 符合条件的候选词
    protected List<Word> candidates;

    public Parser(String text) {
        this.text = clean(text);
        this.dict = new HashMap<>();
        this.candidates = new ArrayList<>();
    }

    private String clean(String str) {
        return str.replaceAll("[^\\u4E00-\\u9FA50-9]+", "");
    }

    public List<Word> parse() {
        Word.N = makeDict();
        filter();
        return this.candidates;
    }

    abstract long makeDict();

    abstract void filter();
}
