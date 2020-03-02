package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.hr.HRNewsService;
import kz.nicnbk.service.api.legal.LegalUpdateService;
import kz.nicnbk.service.dto.hr.HRNewsDto;
import kz.nicnbk.service.dto.legal.LegalUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/legal")
public class LegalServiceREST extends CommonServiceREST {

    public static final int DEFAULT_PAGE_SIZE = 5;
    public static final int MAX_PAGE_SIZE = 20;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private LegalUpdateService legalUpdateService;

    //@PreAuthorize("hasRole('ROLE_NEWS_VIEWER') OR hasRole('ROLE_NEWS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/updates/load", method = RequestMethod.GET)
    public ResponseEntity<?> getLegalUpdatesShort(){
        List<LegalUpdateDto> news = legalUpdateService.loadNewsShort(0, DEFAULT_PAGE_SIZE);
        return buildNonNullResponse(news);
    }

    //@PreAuthorize("hasRole('ROLE_NEWS_VIEWER') OR hasRole('ROLE_NEWS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/updates/load/{pageSize}/{page}", method = RequestMethod.GET)
    public ResponseEntity<?> getLegalUpdatesShort(@PathVariable Integer pageSize, @PathVariable Integer page){
        List<LegalUpdateDto> news = legalUpdateService.loadNewsShort(page, Math.min(pageSize, MAX_PAGE_SIZE));
        return buildNonNullResponse(news);
    }

    @PreAuthorize("hasRole('ROLE_LEGAL_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/updates/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody LegalUpdateDto newsDto){
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Long id = legalUpdateService.save(newsDto, username);
        if(id == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(id, newsDto.getCreationDate());
        }
    }

    //@PreAuthorize("hasRole('ROLE_NEWS_VIEWER') OR hasRole('ROLE_NEWS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/updates/get/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable Long id){
        LegalUpdateDto newsDto = this.legalUpdateService.get(id);
        return buildNonNullResponse(newsDto);
    }
}
