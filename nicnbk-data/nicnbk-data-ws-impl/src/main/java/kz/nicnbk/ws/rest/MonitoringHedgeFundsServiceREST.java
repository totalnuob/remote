package kz.nicnbk.ws.rest;

import kz.nicnbk.repo.model.lookup.FileTypeLookup;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.corpmeetings.CorpMeetingService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.monitoring.MonitoringHedgeFundService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.corpmeetings.*;
import kz.nicnbk.service.dto.files.FilesDto;
import kz.nicnbk.service.dto.monitoring.MonitoringHedgeFundDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Created by magzumov.
 */

@RestController
@RequestMapping("/monitoring/hf")
public class MonitoringHedgeFundsServiceREST extends CommonServiceREST{

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private MonitoringHedgeFundService monitoringHedgeFundService;

    private static final String hedgeFundsEditorRole = "hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')";
    private static final String hedgeFundsViewerRole = "hasRole('ROLE_HEDGE_FUND_VIEWER') OR hasRole('ROLE_HEDGE_FUND_EDITOR') OR hasRole('ROLE_ADMIN')";

    @PreAuthorize(hedgeFundsViewerRole)
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public ResponseEntity getAllData() {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        List<MonitoringHedgeFundDataDto> data = this.monitoringHedgeFundService.getAllData();

        return buildNonNullResponse(data);
    }


    /* IC MEETING *****************************************************************************************************/
    @PreAuthorize(hedgeFundsEditorRole)
    @RequestMapping(value = "/ICMeeting/save", method = RequestMethod.POST)
    public ResponseEntity<?> saveICMeeting(@RequestBody MonitoringHedgeFundDataDto dataDto) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        boolean saved  = this.monitoringHedgeFundService.save(dataDto, username);
        return buildEntitySaveResponseEntity(saved);
    }

}
