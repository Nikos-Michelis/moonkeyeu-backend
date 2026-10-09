package com.moonkeyeu.core.api.launch.services;

import com.moonkeyeu.core.api.launch.dto.CrawlerDTO;

public interface CrawlerService {
    String getDefaultMetaHtml(String url);
    String getMetaHtmlBySegment(String url, String segment);
    String getLaunchMetaHtml(String id, String url);
    String getAstronautMetaHtml(Integer id, String url);
    String getProgramMetaHtml(Integer id, String url);
    String getSpacecraftMetaHtml(Integer id, String url);
    String getLaunchPadMetaHtml(Integer id, String url);
    String getAgencyMetaHtml(Integer id, String url);
    String getMetaHtml(String content, String url, CrawlerDTO crawlerDTO);
    boolean isCrawler(String userAgent);
}
