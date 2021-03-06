package cn.magese.crawler4jdemo;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 自定义爬虫类
 */
public class DeviantartCrawler extends WebCrawler {
    /**
     * 正则匹配指定的后缀文件
     */
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|mp3|zip|gz))$");
    private final static Pattern MATCHING = Pattern.compile(".*(\\.(jpg|jepg|png|bmp|ico|gif))$");

    /**
     * 这个方法主要是决定哪些url我们需要抓取，返回true表示是我们需要的，返回false表示不是我们需要的Url
     * 第一个参数referringPage封装了当前爬取的页面信息
     * 第二个参数url封装了当前爬取的页面url信息
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();// 得到小写的url
        return !FILTERS.matcher(href).matches() /* 正则匹配，过滤掉我们不需要的后缀文件*/
                && (href.contains("deviantart.com") || href.contains("deviantart.net"));
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

        /*// 图片
        if (url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".bmp")
                || url.endsWith(".ico") || url.endsWith(".gif")) {
            byte[] contentData = page.getContentData();

            String extName = url.substring(url.lastIndexOf("."));
            String filename = UUID.randomUUID().toString();
            File file = new File("E:\\crawl\\pic\\" + filename + extName);
            System.out.println(file);
            OutputStream out = null;
            try {
                out = new FileOutputStream(file);
                IOUtils.write(contentData, out);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    Objects.requireNonNull(out).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/

        if (page.getParseData() instanceof HtmlParseData) {  // 判断是否是html数据
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData(); // 强制类型转换，获取html数据对象
            String text = htmlParseData.getText();  // 获取页面纯文本（无html标签）
            String html = htmlParseData.getHtml();  // 获取页面Html
            Set<WebURL> links = htmlParseData.getOutgoingUrls();  // 获取页面输出链接

            for (WebURL link : links) {
                String strURL = link.getURL();
                System.out.println(strURL);
                if (MATCHING.matcher(strURL).matches()
                        && (strURL.startsWith("https://pre00.deviantart.net")
                        || strURL.startsWith("https://img00.deviantart.net")
                        || strURL.startsWith("https://orig00.deviantart.net")))
                    download(strURL);
            }
            System.out.println("纯文本长度: " + text.length());
            System.out.println("html长度: " + html.length());
            System.out.println("输出链接个数: " + links.size());
        }
    }

    private static void download(String strURL) {
        InputStream in = null;
        OutputStream out = null;
        try {
            URL url = new URL(strURL);
            URLConnection con = url.openConnection();
            con.setConnectTimeout(5000);
            in = con.getInputStream();
            String extName = url.toString().substring(url.toString().lastIndexOf("."));
            String filename = UUID.randomUUID().toString();
            File file = new File("E:\\crawl\\dotapic\\" + filename + extName);
            System.out.println(file);
            out = new FileOutputStream(file);
            IOUtils.copy(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(out).close();
                Objects.requireNonNull(in).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
