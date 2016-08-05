package com.pingao.yiya;

import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * Created by pingao.liu on 2016/8/4.
 */
public class Yiya {
    private Yiya() {}

    /**
     * 使用内部存储进行分词
     *
     * @param text 待分词文本
     * @return 所有词
     */
    public static List<Word> words(String text) {
        Parser Parser = new OnceParser(text);
        return Parser.parse();
    }

    /**
     * 使用redis作为存储进行分词
     *
     * @param text   待分词文本
     * @param jedis  jedis实例
     * @param append 是否将新的分词追加到redis
     * @return 所有词
     */
    public static List<Word> words(String text, Jedis jedis, boolean append) {
        Parser Parser = new MemoryParser(text, jedis, append);
        return Parser.parse();
    }
}
