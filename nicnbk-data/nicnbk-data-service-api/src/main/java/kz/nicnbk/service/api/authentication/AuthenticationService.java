package kz.nicnbk.service.api.authentication;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.authentication.AuthenticatedUserDto;

/**
 * Created by magzumov on 21.02.2017.
 */
public interface AuthenticationService extends BaseService {

    AuthenticatedUserDto authenticate(String username, String password, String otp);
}
