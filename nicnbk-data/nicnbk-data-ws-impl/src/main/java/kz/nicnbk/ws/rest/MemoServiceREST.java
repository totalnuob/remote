package kz.nicnbk.ws.rest;

import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.files.FileService;
import kz.nicnbk.service.api.m2s2.*;
import kz.nicnbk.service.dto.authentication.TokenUserInfo;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.m2s2.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Set;

/**
 * Created by magzumov on 20.07.2016.
 */

@RestController
@RequestMapping("/m2s2")
public class MemoServiceREST extends CommonServiceREST {

    @Autowired
    private MeetingMemoService memoService;

    @Autowired
    private GeneralMeetingMemoService generalMemoService;
    @Autowired
    private PEMeetingMemoService PEmemoService;
    @Autowired
    private HFMeetingMemoService HFmemoService;
    @Autowired
    private REMeetingMemoService REmemoService;

    @Autowired
    private FileService fileService;

    @Autowired
    private TokenService tokenService;


    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseEntity<?> search(@RequestBody MemoSearchParamsExtended searchParams){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        MemoPagedSearchResult searchResult = memoService.search(searchParams, username);
        if(searchResult != null){
            return new ResponseEntity<>(searchResult, null, HttpStatus.OK);
        }else{
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "PE/search", method = RequestMethod.POST)
    public ResponseEntity<?> searchPE(@RequestBody MemoSearchParams searchParams){
        MemoPagedSearchResult searchResult = PEmemoService.search(searchParams);
        if(searchResult != null){
            return new ResponseEntity<>(searchResult, null, HttpStatus.OK);
        }else{
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "HF/search", method = RequestMethod.POST)
    public ResponseEntity<?> searchHF(@RequestBody MemoSearchParams searchParams){
        MemoPagedSearchResult searchResult = HFmemoService.search(searchParams);
        if(searchResult != null){
            return new ResponseEntity<>(searchResult, null, HttpStatus.OK);
        }else{
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/get/{type}/{memoId}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable int type, @PathVariable long memoId){
        switch (type){
            case MeetingMemo.GENERAL_DISCRIMINATOR:
                MeetingMemoDto generalMeetingMemoDto = generalMemoService.get(memoId);
                if(generalMeetingMemoDto != null){
                    return new ResponseEntity<>(generalMeetingMemoDto, null, HttpStatus.OK);
                }else{
                    // error occurred
                    return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            case MeetingMemo.PE_DISCRIMINATOR:
                MeetingMemoDto privateEquityMeetingMemoDto =  PEmemoService.get(memoId);
                if(privateEquityMeetingMemoDto != null){
                    return new ResponseEntity<>(privateEquityMeetingMemoDto, null, HttpStatus.OK);
                }else{
                    // error occurred
                    return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            case MeetingMemo.HF_DISCRIMINATOR:
                MeetingMemoDto hedgeFundMeetingMemoDto = HFmemoService.get(memoId);
                if(hedgeFundMeetingMemoDto != null){
                    return new ResponseEntity<>(hedgeFundMeetingMemoDto, null, HttpStatus.OK);
                }else{
                    // error occurred
                    return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            case MeetingMemo.RE_DISCRIMINATOR:
                MeetingMemoDto realEstateMeetingMemoDto =  REmemoService.get(memoId);
                if(realEstateMeetingMemoDto != null){
                    return new ResponseEntity<>(realEstateMeetingMemoDto, null, HttpStatus.OK);
                }else{
                    // error occurred
                    return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            default:
                // error occurred
                return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @RequestMapping(value = "/get/{type}/{memoId}", method = RequestMethod.GET)
//    public HedgeFundsMeetingMemoDto get(@PathVariable int type, @PathVariable long memoId){
//        return HFmemoService.get(memoId);
//    }

    @PreAuthorize("hasRole('ROLE_PRIVATE_EQUITY_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/PE/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody PrivateEquityMeetingMemoDto memoDto){
        if(memoDto.getFund() != null && memoDto.getFund().getId() == null) {
            memoDto.setFund(null);
        }
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        if(memoDto.getId() == null){
            memoDto.setOwner(username);
        }
        Long id = PEmemoService.save(memoDto, username);

        if(id == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(id, memoDto.getCreationDate());
        }
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/HF/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody HedgeFundsMeetingMemoDto memoDto){
        if(memoDto.getFund() != null && memoDto.getFund().getId() == null) {
            memoDto.setFund(null);
        }
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        if(memoDto.getId() == null){
            memoDto.setOwner(username);
        }
        Long id = HFmemoService.save(memoDto, username);
        if(id == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(id, memoDto.getCreationDate());
        }
    }

    @PreAuthorize("hasRole('ROLE_REAL_ESTATE_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/RE/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody RealEstateMeetingMemoDto memoDto){
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        if(memoDto.getId() == null){
            memoDto.setOwner(username);
        }
        Long id = REmemoService.save(memoDto, username);
        if(id == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(id, memoDto.getCreationDate());
        }
    }

    @RequestMapping(value = "/delete/{memoId}", method = RequestMethod.GET)
    public ResponseEntity<?> delete(@PathVariable long memoId){

        MemoDeleteResultDto resultDto = this.memoService.safeDelete(memoId);

        if (resultDto.getStatus().getCode().equals("SUCCESS")) {
            return new ResponseEntity<>(resultDto, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultDto, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/GN/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody GeneralMeetingMemoDto memoDto){
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        // check access by owner
        if(memoDto.getId() != null){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean access = this.generalMemoService.checkOwner((String)auth.getDetails(), memoDto.getId());
            if(!access){
                return buildUnauthorizedResponse();
            }
        } else{
            // set creator
            memoDto.setOwner(username);
        }

        Long id = generalMemoService.save(memoDto, username);
        if(id == null){
            // error occurred
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else{
            // TODO: response from DB, not UI
            return buildEntitySaveResponse(id, memoDto.getCreationDate());
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/attachment/upload/{memoId}")
    public ResponseEntity<?> handleFileUpload(@PathVariable("memoId") long memoId,
                                              @RequestParam(value = "file", required = false) MultipartFile[] files) {

        // TODO: control file upload by user role

        Set<FilesDto> filesDtoSet = buildFilesDtoFromMultipart(files, FileTypeLookup.MEMO_ATTACHMENT.getCode());
        if(filesDtoSet != null){
            Set<FilesDto> savedAttachments = this.memoService.saveAttachments(memoId, filesDtoSet);
            if(savedAttachments == null){
                // error occurred
                return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
            }else{
                return new ResponseEntity<>(savedAttachments, null, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, null, HttpStatus.OK);
    }


    @RequestMapping(value="/attachment/delete/{memoId}/{fileId}", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> deleteFile(@PathVariable(value="memoId") Long memoId, @PathVariable(value="fileId") Long fileId){
        //check rights
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();
        int memoType = this.memoService.getMemoType(memoId);

        if(memoType == 1){ // GENERAL MEMO
            // check owner
            boolean access = this.generalMemoService.checkOwner(token, memoId);
            if(!access){
                return buildUnauthorizedResponse();
            }
        }else if(!checkMemoEditRights(token, memoType)){
            // check module rights
            return buildUnauthorizedResponse();
        }

        //boolean deleted = this.memoService.deleteAttachment(memoId, fileId, username);
        boolean deleted = this.memoService.safeDeleteAttachment(memoId, fileId, username);
        return buildDeleteResponseEntity(deleted);
    }


    @RequestMapping(value = "/attachment/list/{memoId}", method = RequestMethod.GET)
    private Set<FilesDto> getFiles(@PathVariable("memoId") long memoId){
        return this.memoService.getAttachments(memoId);
    }


    public boolean checkMemoEditRights(String token, int memoType){
        TokenUserInfo userInfo = this.tokenService.decode(token);
        String roleName = "";
        if(memoType == 2){ // PRIVATE EQUITY
            roleName = "ROLE_PRIVATE_EQUITY_EDITOR";
        }else if(memoType == 3){ // HEDGE FUNDS
            roleName = "ROLE_HEDGE_FUND_EDITOR";
        }else if(memoType == 4){ // REAL ESTATE
            roleName = "ROLE_REAL_ESTATE_EDITOR";
        }
        if(userInfo != null && userInfo.getRoles() != null) {
            for (int i = 0; i < userInfo.getRoles().length; i++) {
                String role = userInfo.getRoles()[i];
                if(role.equalsIgnoreCase("ROLE_ADMIN")){
                    return true;
                }else if(userInfo.getRoles()[i].equalsIgnoreCase(roleName)){
                    return true;
                }
            }
        }
        return false;
    }
}
