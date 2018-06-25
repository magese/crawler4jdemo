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
        HashSet<BasicHeader> collections = new HashSet<>();
        collections.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36"));
        collections.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"));
        collections.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
        collections.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.9"));
        collections.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"));
        collections.add(new BasicHeader("Connection", "keep-alive"));
        collections.add(new BasicHeader("Cookie", "SESSc60faee9ca2381b86f19bef9617d499b=8i2uas6262pkgdki4uvl7pnro5; __cfduid=d4e66b6380d32924e8e5f18c88161d9dc1529572688; xqrclbr=8062; visited=1; bdshare_firstime=1529572683897; has_js=1; Hm_lvt_0684e5255bde597704c827d5819167ba=1529572684,1529887451; xqrclm=; UM_distinctid=16434e3760620a-0ef6aad6ba756f-5b183a13-1fa400-16434e37607885; CNZZDATA1256504232=332210172-1529890941-%7C1529890941; homere=3; xqrcli=MTUyOTg5NTk0OCwxOTIwKjEwODAsV2luMzIsTmV0c2NhcGUsODA2Miw%3D; Hm_lpvt_0684e5255bde597704c827d5819167ba=1529895949; Hm_cv_0684e5255bde597704c827d5819167ba=1*login*PC-0!1*version*PC"));
        config.setDefaultHeaders(collections);
        config.setCrawlStorageFolder(crawlStorageFolder); // 设置爬虫文件存储位置
        config.setPolitenessDelay(20000); // 设置爬取时间间隔；
        config.setIncludeBinaryContentInCrawling(true); // 设置可以爬取二进制内容
        config.setMaxPagesToFetch(-1); // 要抓取的最大页数
        config.setMaxDepthOfCrawling(-1); // 爬取深度
        config.setProxyHost("139.59.2.223"); // 设置代理主机
        config.setProxyPort(8888); // 设置代理端口
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
        controller.addSeed("http://www.juzimi.com/writer/%E8%8E%8E%E5%A3%AB%E6%AF%94%E4%BA%9A");

        /*
         * 启动爬虫，爬虫从此刻开始执行爬虫任务，根据以上配置
         */
        controller.start(JuzimiCrawler.class, numberOfCrawlers);
    }
}
