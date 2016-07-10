package com.pingao.yiya;

import java.util.*;

/**
 * Created by wocanmei on 2016/7/9.
 */
public class Parser {
    // 词的最大长度
    private static final int MAX_WORD_LEN = 4;
    // 待处理的字符串
    private String text;
    // 词典
    private Map<String, Word> dict;
    // 符合条件的候选词
    private List<Word> candidates;

    public Parser(String text) {
        this.text = clean(text);
        this.dict = new HashMap<>();
        this.candidates = new ArrayList<>();
    }

    public List<Word> parse() {
        makeDict();
        filter();
        Collections.sort(candidates);
        return candidates;
    }

    private void makeDict() {
        int N = this.text.length();
        for (int j = 0; j < N; j++) {
            for (int len = 1; j + len <= N && len <= MAX_WORD_LEN; len++) {
                String w = this.text.substring(j, j + len).intern();

                Word word = dict.get(w);
                if (word == null) {
                    word = new Word();
                    word.setValue(w);
                    word.setFrequency(1);
                    if (len > 1 && j > 0) {
                        char l = this.text.charAt(j - 1);
                        word.addLeftWords(l);
                    }
                    if (len > 1 && j + len < N) {
                        char r = this.text.charAt(j + len);
                        word.addRightWords(r);
                    }

                    dict.put(w, word);
                } else {
                    word.setFrequency(word.getFrequency() + 1);
                    if (len > 1 && j > 0) {
                        char l = this.text.charAt(j - 1);
                        word.addLeftWords(l);
                    }
                    if (len > 1 && j + len < N) {
                        char r = this.text.charAt(j + len);
                        word.addRightWords(r);
                    }
                }
            }
        }
    }

    private void filter() {
        for (Map.Entry<String, Word> entry : dict.entrySet()) {
            Word word = entry.getValue();

            word.setMi(mi(word));
            word.setEntropy(entropy(word));
            if (word.getMi() > 300 && word.getEntropy() > 0.02) {  // 筛选条件
                candidates.add(word);
            }
        }
    }

    private float entropy(Word word) {
        if (word.getValue().length() == 1
                || word.getLeftWordsNum() == 0
                || word.getRightWordsNum() == 0) {
            return 0.0f;
        }
        float left = 0.0f;
        float right = 0.0f;
        for (Map.Entry<Character, Integer> entry : word.getLeftWords().entrySet()) {
            float p = (float) entry.getValue() / word.getLeftWordsNum();
            left += (-p) * Math.log(p);
        }

        for (Map.Entry<Character, Integer> entry : word.getRightWords().entrySet()) {
            float p = (float) entry.getValue() / word.getRightWordsNum();
            right += (-p) * Math.log(p);
        }
        return left > right ? right : left;
    }

    private float mi(Word word) {
        if (word.getValue().length() == 1) {
            return 1.0f;
        }

        float tmp = 1.0f;
        int len = word.getValue().length();
        String val = word.getValue();
        for (int i = 1; i < len; i++) {
            String left = val.substring(0, i);
            String right = val.substring(i);
            if (i == 1 || tmp < dict.get(left).getFrequency() * dict.get(right).getFrequency()) {
                tmp = dict.get(left).getFrequency() * dict.get(right).getFrequency();
            }
        }

        return dict.size() * word.getFrequency() / tmp;
    }

    private String clean(String str) {
        return str.replaceAll("[^\\u4E00-\\u9FA5]", "");
    }
}
