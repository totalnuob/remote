package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.news.NewsService;
import kz.nicnbk.service.dto.news.NewsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by magzumov on 19.07.2016.
 */

@RestController
@RequestMapping("/news")
public class NewsServiceREST {

    public static final int DEFAULT_PAGE_SIZE = 5;
    public static final int MAX_PAGE_SIZE = 20;

    @Autowired
    private NewsService newsService;

    @RequestMapping(value = "/load", method = RequestMethod.GET)
    public List<NewsDto> getNewsShort(){
        List<NewsDto> news = newsService.loadNewsShort(DEFAULT_PAGE_SIZE);
        return news;
    }

    @RequestMapping(value = "/load/{pageSize}", method = RequestMethod.GET)
    public List<NewsDto> getNewsShort(@PathVariable Integer pageSize){

        List<NewsDto> news = newsService.loadNewsShort(pageSize);
        return news;
    }

    @RequestMapping(value = "/load/{pageSize}/{newsType}/{page}", method = RequestMethod.GET)
    public List<NewsDto> getNewsShort(@PathVariable Integer pageSize, @PathVariable String newsType, @PathVariable Integer page){

        List<NewsDto> news = newsService.loadNewsShort(newsType, page, Math.min(pageSize, MAX_PAGE_SIZE));
        return news;
    }

    @RequestMapping(value="/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody NewsDto newsDto){
        Long id = newsService.save(newsDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value="/get/{id}", method = RequestMethod.GET)
    public NewsDto get(@PathVariable Long id){
        NewsDto newsDto = this.newsService.get(id);
        return newsDto;
    }

}
