package kz.nicnbk.ws.rest;

import kz.nicnbk.repo.model.hf.HFResearch;
import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.hf.HFManagerService;
import kz.nicnbk.service.api.hf.HFResearchPageService;
import kz.nicnbk.service.api.hf.HFResearchService;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.hf.*;
import kz.nicnbk.service.dto.m2s2.MemoDeleteResultDto;
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
 * Created by magzumov on 20.07.2016.
 */

@RestController
@RequestMapping("/hf/manager")
public class HedgeFundManagerServiceREST extends CommonServiceREST{

    @Autowired
    private HFManagerService service;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private HFResearchService hfResearchService;

    @Autowired
    private HFResearchPageService hfResearchPageService;

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable long id){
        HFManagerDto firmDto = this.service.get(id);
        return buildNonNullResponse(firmDto);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody HFManagerDto firmDto) {
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        if(firmDto.getId() == null){
            firmDto.setOwner(username);
        }
        Long id = this.service.save(firmDto, username);
        if(id == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(id, firmDto.getCreationDate());
        }
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseEntity search(@RequestBody HedgeFundSearchParams searchParams){
        HedgeFundManagerPagedSearchResult searchResult = this.service.findByName(searchParams);
        return buildNonNullResponse(searchResult);
    }

    @RequestMapping(value = "/invested", method = RequestMethod.GET)
    public ResponseEntity findInvestedFunds(){
        List<HFManagerDto> searchResult = this.service.findInvestedFunds();
        return buildNonNullResponse(searchResult);
    }

    @RequestMapping(value = "/research/get/{id}", method = RequestMethod.GET)
    public ResponseEntity getResearch(@PathVariable long id){
        HFResearchDto researchDtoDto = this.hfResearchService.get(id);
        return buildNonNullResponse(researchDtoDto);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/research/save", method = RequestMethod.POST)
    public ResponseEntity<?>  saveResearch(@RequestBody HFResearchDto researchDto) {

        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        if(researchDto.getId() == null || researchDto.getId() == 0){
            researchDto.setOwner(username);
        }
        Long id = this.hfResearchService.save(researchDto, username);
        if(id == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(id, researchDto.getCreationDate());
        }
    }

    @RequestMapping(value = "/research/page/get/{id}", method = RequestMethod.GET)
    public ResponseEntity getResearchPages(@PathVariable long id){
        HFResearchPageDto researchPageDto = this.hfResearchPageService.get(id);
        return  buildNonNullResponse(researchPageDto);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/research/page/save", method = RequestMethod.POST)
    public ResponseEntity<?>  saveResearchPage(@RequestBody HFResearchPageDto researchPageDto) {

        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        if(researchPageDto.getId() == null || researchPageDto.getId() == 0){
            researchPageDto.setOwner(username);
        }
        Long id = this.hfResearchPageService.save(researchPageDto, username);
        if(id == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(id, researchPageDto.getCreationDate());
        }
    }

    @RequestMapping(value = "/research/page/delete/{pageId}", method = RequestMethod.GET)
    public ResponseEntity<?> delete(@PathVariable long pageId){

        // set destructor
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        MemoDeleteResultDto resultDto = this.hfResearchPageService.delete(pageId, username);

        if (resultDto.getStatus().getCode().equals("SUCCESS")) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/research/page/attachment/upload/{researchPageId}")
    public ResponseEntity<?> handleFileUpload(@PathVariable("researchPageId") long researchPageId,
                                              @RequestParam(value = "file", required = false) MultipartFile[] files) {

        // TODO: control file upload by user role

        Set<FilesDto> filesDtoSet = buildFilesDtoFromMultipart(files, FileTypeLookup.MEMO_ATTACHMENT.getCode());
        if(filesDtoSet != null){
            Set<FilesDto> savedAttachments = this.hfResearchPageService.saveAttachments(researchPageId, filesDtoSet);
            if(savedAttachments == null){
                // error occurred
                return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }else{
                return new ResponseEntity<>(savedAttachments, null, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, null, HttpStatus.OK);
    }

    @RequestMapping(value="/research/page/attachment/delete/{researchPageId}/{fileId}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> deleteFile(@PathVariable(value="researchPageId") Long researchPageId, @PathVariable(value="fileId") Long fileId){
        //check rights
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        //boolean deleted = this.memoService.deleteAttachment(memoId, fileId, username);
        boolean deleted = this.hfResearchPageService.safeDeleteAttachment(researchPageId, fileId, username);
        return buildDeleteResponseEntity(deleted);
    }


    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity getManagers(){
        List<HFManagerDto> managers = this.service.findAll();
        return buildNonNullResponse(managers);
    }
}
