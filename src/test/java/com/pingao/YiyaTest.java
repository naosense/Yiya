package com.pingao;

import com.pingao.yiya.Word;
import com.pingao.yiya.Yiya;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Created by wocanmei on 2016/7/10.
 */
public class YiyaTest {
    public static void main(String[] args) throws IOException {
        Jedis jedis = new Jedis("localhost");
        jedis.select(1);
        try {
            String text = new String(Files.readAllBytes(Paths.get("./book/pie.txt")), "GBK");
            List<Word> words = Yiya.words(text, jedis, false);
            Collections.sort(words, (o1, o2) -> {
                float p1 = o1.getEntropy() / o1.getFrequency() + o1.getMi() / Word.N;
                float p2 = o2.getEntropy() / o2.getFrequency() + o2.getMi() / Word.N;
                if (p1 > p2) {
                    return -1;
                } else if (p1 < p2) {
                    return 1;
                } else {
                    return 0;
                }
            });
            words.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }
}
