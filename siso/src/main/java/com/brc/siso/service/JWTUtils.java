package com.brc.siso.service;

import com.fasterxml.jackson.databind.ser.Serializers;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Component
public class JWTUtils {
    private final SecretKey Key;
    private static final long EXPIRATION_TIME = 86400000L; //24hours ... change this later to 8 hours

    public JWTUtils(){
        String secretString ="5ZlJfIB52MUeafYhDwfpLiMEscYiAgf63Z9aKbsj0Mlpe0LW1i4Qeg2xgwszh7mF";
        byte[] keyBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
        this.Key = new SecretKeySpec(keyBytes,"HmacSHA256");/**used for Hashing**/
    }


    /**passing Spring security userDetails object to generateToken function**/
    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ EXPIRATION_TIME) )
                .signWith(Key)
                .compact();
    }
    public String generateRefreshToken(HashMap<String, Object> claims, UserDetails userDetails){
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Key)
                .compact();
    }

    /**this f(x) gets the user token**/
    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    /** This is a generic T Function in Java designed to control the typing of the function
     * https://www.baeldung.com/java-generics
     * https://javadoc.pentaho.com/reporting700/library/libformula-7.0.0.0-25-javadoc/org/pentaho/reporting/libraries/formula/function/text/TFunction.html **/
    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction){
        return claimsTFunction.apply(Jwts.parser().verifyWith(Key).build().parseSignedClaims(token).getPayload());
    }

    public boolean isTokenValid(String token,UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token){
        return extractClaims(token,Claims::getExpiration).before(new Date());
    }


}
