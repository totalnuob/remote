package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.hf.HedgeFundScoringService;
import kz.nicnbk.service.dto.hf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by magzumov on 20.07.2016.
 */

@RestController
@RequestMapping("/hf/scoring")
public class HedgeFundScoringServiceREST extends CommonServiceREST{

    @Autowired
    private HedgeFundScoringService scoringService;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable long id){
        HedgeFundScoringDto dto = this.scoringService.getScoring(id);
        return buildNonNullResponse(dto);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?>  save(@RequestBody HedgeFundScoringDto scoringDto) {
        // set creator
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        Long id = this.scoringService.save(scoringDto, username);

        return buildEntitySaveResponse(id);
    }

    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseEntity search(@RequestBody HedgeFundScoringSearchParams searchParams){
        HedgeFundScoringPagedSearchResult searchResult = this.scoringService.searchScoring(searchParams);
        return buildNonNullResponse(searchResult);
    }

//    @PreAuthorize("hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')")
//    @RequestMapping(value = "/calculateScoring", method = RequestMethod.POST)
//    public ResponseEntity getCalculatedScoring(@RequestBody HedgeFundScoringFundParamsDto params){
//        List<HedgeFundScreeningParsedDataDto> fundList = this.scoringService.getCalculatedScoring(params);
//        return buildNonNullResponse(fundList);
//    }

}
