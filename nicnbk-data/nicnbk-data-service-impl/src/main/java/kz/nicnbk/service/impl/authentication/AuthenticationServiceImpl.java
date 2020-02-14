package kz.nicnbk.service.impl.authentication;

import kz.nicnbk.common.service.model.BaseDictionaryDto;
import kz.nicnbk.common.service.util.StringUtils;
import kz.nicnbk.service.api.authentication.AuthenticationService;
import kz.nicnbk.service.api.employee.EmployeeService;
import kz.nicnbk.service.dto.authentication.AuthenticatedUserDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by magzumov on 21.02.2017.
 */

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Autowired
    private EmployeeService employeeService;

    @Override
    public AuthenticatedUserDto authenticate(String username, String password, String code) {
        try {
            EmployeeDto employeeDto = employeeService.findActiveByUsernamePasswordCode(username, password, code);
            if (employeeDto == null) {
                logger.error("Failed to authenticate: username=" + username);
                return null;
            } else {
                AuthenticatedUserDto authenticatedUserDto = new AuthenticatedUserDto();
                authenticatedUserDto.setUsername(employeeDto.getUsername());
                if (employeeDto.getRoles() != null) {
                    Set<String> roles = new HashSet<>();
                    for (BaseDictionaryDto role : employeeDto.getRoles()) {
                        String roleOutput = convertToOutputRoleName(role.getCode());
                        if(StringUtils.isNotEmpty(roleOutput)) {
                            roles.add(roleOutput);
                        }
                    }
                    authenticatedUserDto.setRoles(roles);
                }
                logger.info("Successfully authenticated: username=" + username);
                return authenticatedUserDto;
            }
        }catch (Exception ex){
            logger.error("Authentication error: username=" + username, ex);
        }
        return null;
    }

    // TODO: change ROLE.CODE field size and remove this conversion, store long code
    private String convertToOutputRoleName(String roleName){
        switch(roleName){
            case "ADMIN":
                return "ROLE_ADMIN";
            case "NEWS_EDIT":
                return "ROLE_NEWS_EDITOR";
            case "NEWS_VIEW":
                return "ROLE_NEWS_VIEWER";
            case "HF_EDIT":
                return "ROLE_HEDGE_FUND_EDITOR";
            case "HF_VIEW":
                return "ROLE_HEDGE_FUND_VIEWER";
            case "PE_EDIT":
                return "ROLE_PRIVATE_EQUITY_EDITOR";
            case "PE_VIEW":
                return "ROLE_PRIVATE_EQUITY_VIEWER";
            case "RE_EDIT":
                return "ROLE_REAL_ESTATE_EDITOR";
            case "RE_VIEW":
                return "ROLE_REAL_ESTATE_VIEWER";
            case "RA_EDIT":
                return "ROLE_REPORTING_EDITOR";
            case "RA_VIEW":
                return "ROLE_REPORTING_VIEWER";
            case "SRM_EDIT":
                return "ROLE_STRATEGY_RISK_MANAGEMENT_EDITOR";
            case "SRM_VIEW":
                return "ROLE_STRATEGY_RISK_MANAGEMENT_VIEWER";
            case "STRTG_EDIT":
                return "ROLE_STRATEGY_EDITOR";
            case "STRTG_VIEW":
                return "ROLE_STRATEGY_VIEWER";
            case "RISKS_EDIT":
                return "ROLE_RISKS_EDITOR";
            case "RISKS_VIEW":
                return "ROLE_RISKS_VIEWER";
            case "MM_VIEW":
                return "ROLE_MACROMONITOR_VIEWER";
            case "MM_EDIT":
                return "ROLE_MACROMONITOR_EDITOR";
            case "IC_MEMBR":
                return "ROLE_IC_MEMBER";
            case "MEMO_RESTR":
                return "ROLE_MEMO_RESTRICTED";
            case "CM_EDIT":
                return "ROLE_CORPMEETINGS_EDITOR";
            case "CM_VIEW":
                return "ROLE_CORPMEETINGS_VIEWER";
            case "HR_EDIT":
                return "ROLE_HR_EDITOR";
            case "USER_EDIT":
                return "ROLE_USER_PROFILE_EDITOR";
            case "MONIT_VIEW":
                return "ROLE_MONITORING_VIEWER";
            case "MONIT_EDIT":
                return "ROLE_MONITORING_EDITOR";
            case "LOOKP_VIEW":
                return "ROLE_LOOKUPS_VIEWER";
            case "LOOKP_EDIT":
                return "ROLE_LOOKUPS_EDITOR";
            case "M2S2_VIEW":
                return "ROLE_M2S2_VIEWER";
            case "M2S2_EDIT":
                return "ROLE_M2S2_EDITOR";
            default:
                return null;
        }
    }
}
