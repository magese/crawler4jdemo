package cn.magese.crawler4jdemo;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.http.message.BasicHeader;

import java.util.HashSet;

public class CrawlerController {
    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "E:/crawl"; // 定义爬虫数据存储位置
        int numberOfCrawlers = 7; // 定义7个爬虫，也就是7个线程

        CrawlConfig config = new CrawlConfig(); // 定义爬虫配置
        HashSet<BasicHeader> collections = new HashSet<BasicHeader>();
        collections.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36"));
        collections.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"));
        collections.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
        collections.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.9"));
        collections.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"));
        collections.add(new BasicHeader("Connection", "keep-alive"));
        collections.add(new BasicHeader("Cookie", "__cfduid=d83a0e64068437a969871414939c43ab31529024454; PHPSESSID=ipuv3qa5a41jufbq5bsmnslr42; UM_distinctid=16400f57f75d8-0428d8e0235cd3-5b183a13-1fa400-16400f57f76503; CNZZDATA30058803=cnzz_eid%3D1255225115-1529022468-https%253A%252F%252Fwww.baidu.com%252F%26ntime%3D1529022468; yjs_id=ceb5623f6e2a5b45281cd75395ac1e7f; ctrl_time=1; CNZZDATA30058806=cnzz_eid%3D1621400449-1529019905-https%253A%252F%252Fwww.baidu.com%252F%26ntime%3D1529025322; Hm_lvt_09abe82d1cd5a1c4f81584b7035300ab=1529024446,1529027799; Hm_lpvt_09abe82d1cd5a1c4f81584b7035300ab=1529027805"));
        config.setDefaultHeaders(collections);
        config.setCrawlStorageFolder(crawlStorageFolder); // 设置爬虫文件存储位置

        /*
         * 实例化爬虫控制器
         */
        PageFetcher pageFetcher = new PageFetcher(config); // 实例化页面获取器
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig(); // 实例化爬虫机器人配置 比如可以设置 user-agent

        // 实例化爬虫机器人对目标服务器的配置，每个网站都有一个robots.txt文件 规定了该网站哪些页面可以爬，哪些页面禁止爬，该类是对robots.txt规范的实现
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        // 实例化爬虫控制器
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        /*
         * 配置爬虫种子页面，就是规定的从哪里开始爬，可以配置多个种子页面
         */
        controller.addSeed("https://www.cnxiangyan.com/brand/");
//        controller.addSeed("http://www.java1234.com/a/kaiyuan/");
//        controller.addSeed("http://www.java1234.com/a/bysj/");

        /*
         * 启动爬虫，爬虫从此刻开始执行爬虫任务，根据以上配置
         */
        controller.start(MyCrawler.class, numberOfCrawlers);
    }
}
