package kz.nicnbk.ws.rest;

import kz.nicnbk.repo.model.m2s2.MeetingMemo;
import kz.nicnbk.service.api.m2s2.*;
import kz.nicnbk.service.dto.m2s2.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by magzumov on 20.07.2016.
 */

@RestController
@RequestMapping("/m2s2")
public class MemoServiceREST {

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


    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public MemoPagedSearchResult search(@RequestBody MemoSearchParams searchParams){
        MemoPagedSearchResult searchResult = memoService.search(searchParams);
        return searchResult;
    }

    @RequestMapping(value = "/get/{type}/{memoId}", method = RequestMethod.GET)
    public MeetingMemoDto get(@PathVariable int type, @PathVariable long memoId){
        switch (type){
            case MeetingMemo.GENERAL_DISCRIMINATOR:
                return generalMemoService.get(memoId);
            case MeetingMemo.PE_DISCRIMINATOR:
                return PEmemoService.get(memoId);
            case MeetingMemo.HF_DISCRIMINATOR:
                return HFmemoService.get(memoId);
            case MeetingMemo.RE_DISCRIMINATOR:
                return REmemoService.get(memoId);
        }

        // TODO: custom response if not matched
        return null;
    }

    @RequestMapping(value = "/PE/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody PrivateEquityMeetingMemoDto memoDto){
        Long id = PEmemoService.save(memoDto);
        // TODO: response
        memoDto.setId(id);

        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<>(memoDto, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/HF/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody HedgeFundsMeetingMemoDto memoDto){
        Long id = HFmemoService.save(memoDto);
        // TODO: response
        memoDto.setId(id);

        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<>(memoDto, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/RE/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody RealEstateMeetingMemoDto memoDto){
        Long id = REmemoService.save(memoDto);
        // TODO: response
        memoDto.setId(id);
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<>(memoDto, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/GN/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody GeneralMeetingMemoDto memoDto){
        Long id = generalMemoService.save(memoDto);
        // TODO: response
        memoDto.setId(id);

        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<>(memoDto, httpHeaders, HttpStatus.OK);
    }
}
