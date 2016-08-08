package com.pingao.yiya;

import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by wocanmei on 2016/8/4.
 */
public class MemoryParser extends Parser {
    private Jedis jedis;
    // redis词典key
    private static final String DICT_DB = "yiya:dict";
    // redis词典词汇量
    private static final String DICT_SIZE = "yiya:dict:size";
    // redis邻字key前缀
    private static final String NEIGHBOUR_PRE = "yiya:neighbour:";
    // redis左邻字前缀
    private static final String NEIGHBOUR_LEFT_PRE = "left:";
    // redis右邻字前缀
    private static final String NEIGHBOUR_RIGHT_PRE = "right:";
    // redis左邻字计数器key
    private static final String NEIGHBOR_LEFT_COUNTER = "yiya:left:counter:";
    // redis右邻字记数字key
    private static final String NEIGHBOR_RIGHT_COUNTER = "yiya:right:counter:";
    // 拆分出的所有词
    private Set<String> words;
    // 是否将分词追加到redis字典
    private boolean append;

    public MemoryParser(String text, Jedis jedis, boolean append) {
        super(text);
        this.jedis = jedis;
        this.append = append;
        this.words = new HashSet<>();
    }

    @Override
    long makeDict() {
        int N = this.text.length();
        for (int j = 0; j < N; j++) {
            for (int len = 1; j + len <= N && len <= MAX_WORD_LEN; len++) {
                String w = this.text.substring(j, j + len).intern();
                words.add(w);
                if (append) {
                    jedis.incr(DICT_SIZE);
                    jedis.hincrBy(DICT_DB, w, 1);
                    if (len > 1 && j > 0) {
                        char l = this.text.charAt(j - 1);
                        jedis.hincrBy(NEIGHBOUR_PRE + w, NEIGHBOUR_LEFT_PRE + l, 1);
                        jedis.hincrBy(NEIGHBOR_LEFT_COUNTER, w, 1);
                    }
                    if (len > 1 && j + len < N) {
                        char r = this.text.charAt(j + len);
                        jedis.hincrBy(NEIGHBOUR_PRE + w, NEIGHBOUR_RIGHT_PRE + r, 1);
                        jedis.hincrBy(NEIGHBOR_RIGHT_COUNTER, w, 1);
                    }
                }
            }
        }
        return Long.parseLong(jedis.get(DICT_SIZE));
    }

    @Override
    void filter() {
        for (String w : words) {
            Word word = new Word(w);
            word.setFrequency(Long.parseLong(jedis.hget(DICT_DB, w)));
            word.setMi(mi(word));
            word.setEntropy(entropy(word));
            if (word.getMi() > 300 && word.getEntropy() > 0.5) {  // 筛选条件
                candidates.add(word);
            }
        }
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
            long leftFreq = Long.parseLong(jedis.hget(DICT_DB, left));
            long rightFreq = Long.parseLong(jedis.hget(DICT_DB, right));
            if (i == 1 || tmp < leftFreq * rightFreq) {
                tmp = leftFreq * rightFreq;
            }
        }

        return Word.N * word.getFrequency() / tmp;
    }

    private float entropy(Word word) {
        if (word.getValue().length() == 1) {
            return 0.0f;
        }
        float left = 0.0f;
        float right = 0.0f;
        for (Map.Entry<String, String> entry : jedis.hgetAll(NEIGHBOUR_PRE + word.getValue()).entrySet()) {
            if (entry.getKey().startsWith(NEIGHBOUR_LEFT_PRE)) {
                long freq = Long.parseLong(entry.getValue());
                long total = Long.parseLong(jedis.hget(NEIGHBOR_LEFT_COUNTER, word.getValue()));
                float p = (float) freq / total;
                left += (-p) * Math.log(p);
            } else {
                long freq = Long.parseLong(entry.getValue());
                long total = Long.parseLong(jedis.hget(NEIGHBOR_RIGHT_COUNTER, word.getValue()));
                float p = (float) freq / total;
                right += (-p) * Math.log(p);
            }
        }

        return left > right ? right : left;
    }
}
