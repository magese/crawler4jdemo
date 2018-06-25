package cn.magese.crawler4jdemo;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class JuzimiCrawler extends WebCrawler {
    /**
     * 正则匹配指定的后缀文件
     */
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|mp3|zip|gz))$");

    /**
     * 这个方法主要是决定哪些url我们需要抓取，返回true表示是我们需要的，返回false表示不是我们需要的Url
     * 第一个参数referringPage封装了当前爬取的页面信息
     * 第二个参数url封装了当前爬取的页面url信息
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();// 得到小写的url
        return !FILTERS.matcher(href).matches() /* 正则匹配，过滤掉我们不需要的后缀文件*/
                && href.startsWith("http://www.juzimi.com/writer");
    }

    /**
     * 当我们爬到我们需要的页面，这个方法会被调用，我们可以尽情的处理这个页面
     * page参数封装了所有页面信息
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();  // 获取url
        url = url.toLowerCase();
        System.out.println("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {  // 判断是否是html数据
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData(); // 强制类型转换，获取html数据对象
            String text = htmlParseData.getText();  // 获取页面纯文本（无html标签）
            String html = htmlParseData.getHtml();  // 获取页面Html
            Set<WebURL> links = htmlParseData.getOutgoingUrls();  // 获取页面输出链接

            download(html, url);

            System.out.println("纯文本长度: " + text.length());
            System.out.println("html长度: " + html.length());
            System.out.println("输出链接个数: " + links.size());
        }
    }

    private static void download(String html, String url) {
        // 转换为dom树对象
        Document doc = Jsoup.parse(html);

        // 获取面包树导航
        Elements breadcrumblink = doc.getElementsByClass("breadcrumblink");
        StringBuilder catelog = new StringBuilder("E:\\crawl\\text");
        for (Element element : breadcrumblink) {
            String text = element.text().replaceAll("[^a-zA-Z0-9\u4E00-\u9FA5]", "");
            catelog.append("\\").append(text);
            File f = new File(catelog.toString());
            if (!f.exists())
                f.mkdir();
        }

        Elements breadlast = doc.getElementsByClass("breadlast");
        if (breadlast.size() > 0) {
            String folder = breadlast.get(0).text();
            folder = folder.replaceAll("[^a-zA-Z0-9\u4E00-\u9FA5]", "");
            catelog.append("\\").append(folder);
        }

        // 获取内容div标签
        Elements div = doc.getElementsByClass("views-field-phpcode");
        Set<String> set = new LinkedHashSet<>();
        for (Element element : div) {
            Elements xlistju = element.getElementsByClass("xlistju");
            Elements active = element.getElementsByClass("active");
            String content = "";
            if (xlistju.size() > 0)
                content = xlistju.get(0).text();
            String from = "";
            if (active.size() > 0)
                from = active.get(0).text();
            System.out.println("content:" + content);
            System.out.println("from:--" + from);
            set.add("content=" + content + IOUtils.LINE_SEPARATOR + "from=" + from);
        }

        // 判断文件夹是否存在 不存在则创建新的文件夹
        File dir = new File(catelog.toString());
        if (!dir.exists())
            dir.mkdir();
        System.out.println("folder:" + dir);

        // 获取文件名
        String filename = "page=0";
        int index = url.lastIndexOf("page=");
        if (index != -1)
            filename = url.substring(index);
        filename += ".txt";


        // 写出文件
        File file = new File(catelog.toString() + "\\" + filename);
        System.out.println("file:" + file);
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            IOUtils.writeLines(set, IOUtils.LINE_SEPARATOR, out, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(out).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
