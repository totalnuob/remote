package kz.nicnbk.service.api.authentication;

import kz.nicnbk.service.api.base.BaseService;
import kz.nicnbk.service.dto.authentication.AuthenticatedUserDto;
import kz.nicnbk.service.dto.authentication.TokenUserInfo;

import java.util.Date;

/**
 * Created by magzumov on 21.02.2017.
 */
public interface TokenService extends BaseService {

    boolean verify(String token);

    String create(AuthenticatedUserDto authenticatedUserDto);

    String createForReset(AuthenticatedUserDto authenticatedUserDto);

    TokenUserInfo decode(String token);

    Date checkRevocationUsername(String username);

    boolean revokeUsername(String username);

    boolean cancelRevocationUsername(String username);

}
