package kz.nicnbk.service.impl.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import kz.nicnbk.common.service.util.HashUtils;
import kz.nicnbk.service.api.authentication.TokenService;
import kz.nicnbk.service.dto.authentication.AuthenticatedUserDto;
import kz.nicnbk.service.dto.authentication.TokenUserInfo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by magzumov on 21.02.2017.
 */

@Service
public class JWTTokenServiceImpl implements TokenService {

    private static ConcurrentMap<String,Date> revocationList = new ConcurrentHashMap<String,Date>(100);

    // TODO: concurrent access - read and update
    private final ConcurrentMap<String,String> authPrivateKey = new ConcurrentHashMap<String,String>(1);


    @PostConstruct
    public void init(){
        this.authPrivateKey.put("key", HashUtils.generateRandomText());
        // TODO: log
        System.out.println("Generated token authentication key: " + getKey());
    }

    private String getKey(){
        return this.authPrivateKey.get("key");
    }

    @Scheduled(cron = "0 1 5 * * ?") // at 5:01 every day
    public void resetAuthPrivateKey(){
        this.authPrivateKey.put("key", HashUtils.generateRandomText());
        // TODO: log
        System.out.println("Generated token authentication key (RESET): " + getKey());

        // reset revocation list
        this.revocationList = new ConcurrentHashMap<String,Date>(100);
    }


    @Override
    public Date checkRevocationUsername(String username) {
        if(this.revocationList.containsKey(username)){
            return this.revocationList.get(username);
        }else{
            return null;
        }
    }

    @Override
    public boolean revokeUsername(String username){
        this.revocationList.put(username, new Date());

        return true;
    }

    @Override
    public boolean cancelRevocationUsername(String username) {
        if(this.revocationList.containsKey(username)){
            this.revocationList.remove(username);
            return true;
        }
        return false;
    }


    @Override
    public boolean verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(getKey()))
                    .withIssuer("NICAuth")
                    .build(); //Reusable verifier instance
            JWT jwt = (JWT) verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception){
            //Invalid signature/claims
            // TODO: log error
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
            // TODO: log error
        }
        return false;
    }

    @Override
    public String create(AuthenticatedUserDto authenticatedUserDto) {
        try {
            String token = JWT.create()
                    .withIssuer("NICAuth")
                    .withIssuedAt(new Date())
                    .withClaim("username", authenticatedUserDto.getUsername())
                    .withArrayClaim("roles", authenticatedUserDto.getRolesAsArray())
                    .sign(Algorithm.HMAC256(getKey()));
            return token;
        } catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
            // TODO: log error
        } catch (UnsupportedEncodingException e) {
            // TODO: log error
        }
        return null;
    }

    @Override
    public TokenUserInfo decode(String token) {
        //token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCJ9.AbIJTDMFc7yUa5MhvcP03nJPyCPzZtQcGEp-zWfOkEE";
        try {
            JWT jwt = JWT.decode(token);
            return buildUserInfo(jwt);
        } catch (JWTDecodeException exception){
            //Invalid token
        }
        return null;
    }

    private TokenUserInfo buildUserInfo(JWT jwt){
        TokenUserInfo userInfo = new TokenUserInfo();
        userInfo.setUsername(jwt.getClaim("username").asString());
        userInfo.setIssuedAt(jwt.getIssuedAt());
        if(jwt.getClaim("roles") != null){
            String[] roles = jwt.getClaim("roles").asArray(String.class);
            userInfo.setRoles(roles);
        }
        return userInfo;
    }
}
