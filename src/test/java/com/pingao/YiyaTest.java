package com.pingao;

import com.pingao.yiya.Yiya;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by wocanmei on 2016/7/10.
 */
public class YiyaTest {
    public static void main(String[] args) throws IOException {
        Yiya.words(FileUtils.readFileToString(new File("./wangfeng.txt"), "GBK"), 100).forEach(System.out::println);
    }
}
