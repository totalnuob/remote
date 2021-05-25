package kz.nicnbk.service.api.authentication;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.authentication.AuthenticatedUserDto;
import kz.nicnbk.service.dto.common.ResponseDto;
import kz.nicnbk.service.dto.employee.EmployeeDto;

import java.util.List;

/**
 * Created by magzumov on 21.02.2017.
 */
public interface AuthenticationService extends BaseService {

    AuthenticatedUserDto authenticate(String username, String password, String otp);

    ResponseDto reset(String email, List<EmployeeDto> employeeDtos);

    AuthenticatedUserDto verifyForReset(String email);

    void sendResetLink(String email, String username, String token);

    void sendHtmlResetLink(String email, String username, String token);
}
