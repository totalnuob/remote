package kz.nicnbk.ws.rest;

import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.api.monitoring.MonitoringHedgeFundService;
import kz.nicnbk.service.dto.common.EntitySaveResponseDto;
import kz.nicnbk.service.dto.common.ResponseStatusType;
import kz.nicnbk.service.dto.monitoring.*;
import org.springframework.beans.factory.annotation.Autowired;
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

        MonitoringHedgeFundListDataHolderDto data = this.monitoringHedgeFundService.getAllData();

        return buildNonNullResponse(data);
    }

    @PreAuthorize(hedgeFundsViewerRole)
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public ResponseEntity getMonitoringDataByDate(@RequestBody MonitoringHedgeFundSearchParamsDto searchParams) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        MonitoringHedgeFundDataHolderDto data = this.monitoringHedgeFundService.getMonitoringDataByDate(searchParams);

        return buildNonNullResponse(data);
    }

    @PreAuthorize(hedgeFundsViewerRole)
    @RequestMapping(value = "/getPrevious", method = RequestMethod.POST)
    public ResponseEntity getMonitoringDataForPreviousDate(@RequestBody MonitoringHedgeFundTypedSearchParamsDto searchParams) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        MonitoringHedgeFundDataDto data = this.monitoringHedgeFundService.getMonitoringDataForPreviousDate(searchParams);

        return buildNonNullResponse(data);
    }



    /* IC MEETING *****************************************************************************************************/
    @PreAuthorize(hedgeFundsEditorRole)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> saveData(@RequestBody MonitoringHedgeFundDataHolderDto dataDto) {

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String username = this.tokenService.decode(token).getUsername();

        try {
            EntitySaveResponseDto saveResponseDto = this.monitoringHedgeFundService.save(dataDto, username);
            return buildEntitySaveResponse(saveResponseDto);
        }catch (Exception ex){
            EntitySaveResponseDto saveResponseDto = new EntitySaveResponseDto();
            saveResponseDto.setStatus(ResponseStatusType.FAIL);
            return buildEntitySaveResponse(saveResponseDto);
        }
    }

}
