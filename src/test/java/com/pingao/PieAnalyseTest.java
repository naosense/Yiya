package com.pingao;

import com.pingao.yiya.Word;
import com.pingao.yiya.Yiya;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Created by wocanmei on 2016/8/8.
 */
public class PieAnalyseTest {
    private static int articleCount = 0;
    private static int maleArticleCount = 0;
    private static int femaleArticleCount = 0;
    private static StringBuilder allContent = new StringBuilder();
    private static StringBuilder maleContent = new StringBuilder();
    private static StringBuilder femaleContent = new StringBuilder();
    private static StringBuilder maleContentOf70Age = new StringBuilder();
    private static StringBuilder maleContentOf80Age = new StringBuilder();
    private static StringBuilder maleContentOf90Age = new StringBuilder();
    private static StringBuilder femaleContentOf70Age = new StringBuilder();
    private static StringBuilder femaleContentOf80Age = new StringBuilder();
    private static StringBuilder femaleContentOf90Age = new StringBuilder();

    public static void main(String[] args) throws IOException {
        int pointer = 0;
        String EMPTY = "";
        String title = EMPTY, content = EMPTY, sex = EMPTY, age = EMPTY, weekday = EMPTY, hour = EMPTY;
        boolean isend = false;
        BufferedReader reader = Files.newBufferedReader(Paths.get("./book/byr.txt"), Charset.forName("UTF-8"));
        System.out.println("sex\tage\tweekday\thour\ttitle");
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            if (line.equals("-----------------------------------------------------------------------------")) {
                pointer++;
                if (pointer == 4) {
                    if (!sex.isEmpty()) {
                        articleCount++;
                        if ("m".equals(sex)) {
                            maleArticleCount++;
                        } else {
                            femaleArticleCount++;
                        }
                        System.out.println(sex + "\t" + age + "\t" + weekday + "\t" + hour + "\t" + title);
                    }

                    title = content = sex = age = weekday = hour = EMPTY;
                    isend = false;
                    pointer = 0;
                }
                continue;
            }

            if (pointer == 2) {
                //System.out.println(line);
                if (line.startsWith("m:") ||
                    line.startsWith("f:") ||
                    line.startsWith("m：") ||
                    line.startsWith("f：")) {

                    sex = line.substring(0, 1);
                    title = line;
                } else {
                    continue;
                }
            }

            if (pointer == 3 && !sex.isEmpty()) {

                if (isend || line.startsWith("发信人") || line.startsWith("标  题")) {
                    continue;
                }

                if (line.startsWith("发信站")) {
                    int index = line.indexOf("(");
                    if (index > -1) {
                        weekday = line.substring(index + 1, index + 4);
                        hour = line.substring(index + 12, index + 14);
                    }
                } else if (line.startsWith("※")) {
                    isend = true;

                    int start;
                    String s = content;
                    int index = s.indexOf("年");
                    while (index > -1) {
                        if (index >= 2 && isDigit(s.charAt(index - 1)) && isDigit(s.charAt(index - 2))) {
                            age = s.substring(index - 2, index);
                            break;
                        }
                        start = index + 1;
                        s = s.substring(start);
                        index = s.indexOf("年");
                    }

                    allContent.append(content);
                    if ("m".equals(sex)) {
                        maleContent.append(content);
                        if (!age.isEmpty()) {
                            switch (age.charAt(0)) {
                                case '7':
                                    maleContentOf70Age.append(content);
                                    break;
                                case '8':
                                    maleContentOf80Age.append(content);
                                    break;
                                case '9':
                                    maleContentOf90Age.append(content);
                                    break;
                            }
                        }
                    } else {
                        femaleContent.append(content);
                        if (!age.isEmpty()) {
                            switch (age.charAt(0)) {
                                case '7':
                                    femaleContentOf70Age.append(content);
                                    break;
                                case '8':
                                    femaleContentOf80Age.append(content);
                                    break;
                                case '9':
                                    femaleContentOf90Age.append(content);
                                    break;
                            }
                        }
                    }
                } else {
                    content += line;
                }
            }
        }
        reader.close();
        System.out.println("===========ALL==========");
        topn(allContent.toString(), 100);
        System.out.println("===========Male==========");
        topn(maleContent.toString(), 200);
        System.out.println("===========Female==========");
        topn(femaleContent.toString(), 200);
        //System.out.println("===========70Male==========");
        //topn(maleContentOf70Age.toString(), 100);
        //System.out.println("===========70Female==========");
        //topn(femaleContentOf70Age.toString(), 100);
        System.out.println("===========80Male==========");
        topn(maleContentOf80Age.toString(), 200);
        System.out.println("===========90Male==========");
        topn(maleContentOf90Age.toString(), 200);
        System.out.println("===========80Female==========");
        topn(femaleContentOf80Age.toString(), 200);
        System.out.println("===========90Female==========");
        topn(femaleContentOf90Age.toString(), 200);

        System.out.println("article count=" + articleCount);
        System.out.println("male count=" + maleArticleCount);
        System.out.println("female count=" + femaleArticleCount);
    }

    private static boolean isDigit(char c) {
        return c >= 48 && c <= 57;
    }

    private static void topn(String content, int n) {
        List<Word> words = Yiya.words(content);
        Collections.sort(words);
        words = (words.size() > n ? words.subList(0, n) : words);
        words.forEach(word -> System.out.println(word.getFrequency() + "\t" + word.getValue()));
    }
}
