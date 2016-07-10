# Yiya
一个无字典分词程序，名字来源于汉语“咿呀学语”。

测试方法如下：

```java
String text = "xxx";
// text中频率最高的100个词
Yiya.words(text, 100).forEach(System.out::println);
```
