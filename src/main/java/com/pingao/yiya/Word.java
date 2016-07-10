package com.pingao.yiya;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wocanmei on 2016/7/9.
 */
public class Word implements Comparable<Word> {
    // 左邻字，存储格式：字，出现次数
    private Map<Character, Integer> leftWords;
    // 左邻字个数
    private int leftWordsNum;
    // 右邻字，存储格式：字，出现次数
    private Map<Character, Integer> rightWords;
    // 右邻字个数
    private int rightWordsNum;
    // 词的频数
    private int frequency;
    // 词的熵值
    private float entropy;
    // 词的互信息
    private float mi;
    // 词的字符串值
    private String value;

    public Word() {
        this.leftWords = new HashMap<>();
        this.rightWords = new HashMap<>();
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public float getEntropy() {
        return entropy;
    }

    public void setEntropy(float entropy) {
        this.entropy = entropy;
    }

    public float getMi() {
        return mi;
    }

    public void setMi(float mi) {
        this.mi = mi;
    }

    public Map<Character, Integer> getLeftWords() {
        return leftWords;
    }

    public int getLeftWordsNum() {
        return leftWordsNum;
    }

    public Map<Character, Integer> getRightWords() {
        return rightWords;
    }

    public int getRightWordsNum() {
        return rightWordsNum;
    }

    public void addLeftWords(char c) {
        if (this.leftWords.get(c) == null) {
            this.leftWords.put(c, 1);
        } else {
            this.leftWords.put(c, this.leftWords.get(c) + 1);
        }
        this.leftWordsNum++;
    }

    public void addRightWords(char c) {
        if (this.rightWords.get(c) == null) {
            this.rightWords.put(c, 1);
        } else {
            this.rightWords.put(c, this.rightWords.get(c) + 1);
        }
        this.rightWordsNum++;
    }

    @Override
    public int compareTo(Word o) {
        if (this.frequency > o.frequency) {
            return -1;
        } else if (this.frequency < o.frequency) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Word[value=" + value
                + ", frequency=" + frequency
                + ", entropy=" + entropy
                + ", mi=" + mi + "]";
    }
}
