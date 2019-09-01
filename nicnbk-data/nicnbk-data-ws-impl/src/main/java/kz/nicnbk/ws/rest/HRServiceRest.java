package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.hr.HRDocsService;
import kz.nicnbk.service.api.hr.HRNewsService;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hr.HRDocsResultDto;
import kz.nicnbk.service.dto.hr.HRNewsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * Created by Pak on 28.08.2019.
 */

@RestController
@RequestMapping("/hr")
public class HRServiceREST extends CommonServiceREST {

    public static final int DEFAULT_PAGE_SIZE = 5;
    public static final int MAX_PAGE_SIZE = 20;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private HRDocsService HRDocsService;

    @Autowired
    private HRNewsService hrNewsService;

    @RequestMapping(value = "/docs/get", method = RequestMethod.GET)
    public ResponseEntity docsGet() {

        HRDocsResultDto resultDto = this.HRDocsService.get();

        if (resultDto.getStatus().getCode().equals("SUCCESS")) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_HR_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/docs/upload", method = RequestMethod.POST)
    public ResponseEntity docsUpload(@RequestParam(value = "file", required = false) MultipartFile[] files) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Set<FilesDto> filesDtoSet = buildFilesDtoFromMultipart(files, null);

        HRDocsResultDto resultDto = this.HRDocsService.upload(filesDtoSet, username);

        if (resultDto.getStatus().getCode().equals("SUCCESS")) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_HR_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/docs/delete/{fileId}", method = RequestMethod.DELETE)
    public ResponseEntity deleteDocument(@PathVariable(value="fileId") Long fileId) {

        HRDocsResultDto resultDto = this.HRDocsService.deleteDocument(fileId);

        if (resultDto.getStatus().getCode().equals("SUCCESS")) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //@PreAuthorize("hasRole('ROLE_NEWS_VIEWER') OR hasRole('ROLE_NEWS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/news/load", method = RequestMethod.GET)
    public ResponseEntity<?> getNewsShort(){
        List<HRNewsDto> news = hrNewsService.loadNewsShort(0, DEFAULT_PAGE_SIZE);
        return buildNonNullResponse(news);
    }

    //@PreAuthorize("hasRole('ROLE_NEWS_VIEWER') OR hasRole('ROLE_NEWS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/news/load/{pageSize}/{page}", method = RequestMethod.GET)
    public ResponseEntity<?> getNewsShort(@PathVariable Integer pageSize, @PathVariable Integer page){
        List<HRNewsDto> news = hrNewsService.loadNewsShort(page, Math.min(pageSize, MAX_PAGE_SIZE));
        return buildNonNullResponse(news);
    }

    //@PreAuthorize("hasRole('ROLE_NEWS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/news/save", method = RequestMethod.POST)
    public ResponseEntity<?> save(@RequestBody HRNewsDto newsDto){
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Long id = hrNewsService.save(newsDto, username);
        if(id == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(id, newsDto.getCreationDate());
        }
    }

    //@PreAuthorize("hasRole('ROLE_NEWS_VIEWER') OR hasRole('ROLE_NEWS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/news/get/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable Long id){
        HRNewsDto newsDto = this.hrNewsService.get(id);
        return buildNonNullResponse(newsDto);
    }
}
