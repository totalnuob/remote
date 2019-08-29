package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.hr.DocsService;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hr.DocsResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * Created by Pak on 28.08.2019.
 */

@RestController
@RequestMapping("/hr")
public class HRServiceRest extends CommonServiceREST {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private DocsService docsService;

    @RequestMapping(value = "/docs/get", method = RequestMethod.GET)
    public ResponseEntity docsGet() {

        DocsResultDto resultDto = this.docsService.get();

        if (resultDto.getStatus().getCode().equals("SUCCESS")) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_HR_DOCS_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/docs/upload", method = RequestMethod.POST)
    public ResponseEntity docsUpload(@RequestParam(value = "file", required = false) MultipartFile[] files) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Set<FilesDto> filesDtoSet = buildFilesDtoFromMultipart(files, null);

        DocsResultDto resultDto = this.docsService.upload(filesDtoSet, username);

        if (resultDto.getStatus().getCode().equals("SUCCESS")) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
