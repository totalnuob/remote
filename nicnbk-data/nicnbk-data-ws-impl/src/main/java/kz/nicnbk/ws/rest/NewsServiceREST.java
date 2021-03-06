package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.news.NewsService;
import kz.nicnbk.service.dto.news.NewsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by magzumov on 19.07.2016.
 */

@RestController
@RequestMapping("/news")
public class NewsServiceREST extends CommonServiceREST{

    public static final int DEFAULT_PAGE_SIZE = 5;
    public static final int MAX_PAGE_SIZE = 20;

    @Autowired
    private NewsService newsService;

    @Autowired
    private TokenService tokenService;

    //@PreAuthorize("hasRole('ROLE_NEWS_VIEWER') OR hasRole('ROLE_NEWS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/load", method = RequestMethod.GET)
    public ResponseEntity<?> getNewsShort(){
        List<NewsDto> news = newsService.loadNewsShort(DEFAULT_PAGE_SIZE);
        return buildNonNullResponse(news);
    }

//    @RequestMapping(value = "/load/{pageSize}", method = RequestMethod.GET)
//    public ResponseEntity<?> getNewsShort(@PathVariable Integer pageSize){
//
//        List<NewsDto> news = newsService.loadNewsShort(pageSize);
//        return new ResponseEntity<>(news, getHeaders(), HttpStatus.CREATED);
//    }

    //@PreAuthorize("hasRole('ROLE_NEWS_VIEWER') OR hasRole('ROLE_NEWS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/load/{pageSize}/{newsType}/{page}", method = RequestMethod.GET)
    public ResponseEntity<?> getNewsShort(@PathVariable Integer pageSize, @PathVariable String newsType, @PathVariable Integer page){
        List<NewsDto> news = newsService.loadNewsShort(newsType, page, Math.min(pageSize, MAX_PAGE_SIZE));
        return buildNonNullResponse(news);
    }

    @PreAuthorize("hasRole('ROLE_NEWS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody NewsDto newsDto){
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        if(newsDto.getId() == null){
            newsDto.setOwner(username);
        }

        Long id = newsService.save(newsDto, username);
        // TODO: response
        if(id == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(id, newsDto.getCreationDate());
        }
    }

    //@PreAuthorize("hasRole('ROLE_NEWS_VIEWER') OR hasRole('ROLE_NEWS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/get/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable Long id){
        NewsDto newsDto = this.newsService.get(id);
        return buildNonNullResponse(newsDto);
    }

//    private HttpHeaders getHeaders(){
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add("Cache-control", "no-cache");
//        httpHeaders.add("Cache-control", "no-store");
//        httpHeaders.add("Pragma", "no-cache");
//        httpHeaders.add("Expires", "0");
//        return httpHeaders;
//    }

}
