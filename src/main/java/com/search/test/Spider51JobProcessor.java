package com.search.test;

import java.io.IOException;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class Spider51JobProcessor implements PageProcessor{

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    
	@Override
	public Site getSite() {

		return site;
	}
	@Override
	public void process(Page page) {

        page.putField("author", page.getUrl().regex("http://search.51job.com/job/(\\w+)/.*").toString());
        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
        if (page.getResultItems().get("name") == null) {
            //skip this page
           // page.setSkip(true);
        }
        page.putField("job", page.getHtml().xpath("//div[@class='jobs_1']/text()"));

        // 部分三：从页面发现后续的url地址来抓取
        page.addTargetRequests(page.getHtml().links().regex("(http://\\w\\.51job\\.com/\\w+/\\w+)").all());
        
	}

    public static void main(String[] args) {

    	//System.setProperty("javax.net.ssl.trustStore", "/Users/fuhe-apple-02/Documents/workspace-sts-3.4.0.RELEASE/CuteSpider/jssecacerts");
    	
        Spider.create(new GithubRepoPageProcessor())
                .addUrl("http://search.51job.com/list/%2B,%2B,%2B,%2B,%2B,%2B,abc,2,%2B.html?lang=c&stype=1&image_x=0&image_y=0")
                .addPipeline(new ConsolePipeline())
                //开启5个线程抓取
                .thread(1)
                //启动爬虫
                .run();
        
        try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }
}
