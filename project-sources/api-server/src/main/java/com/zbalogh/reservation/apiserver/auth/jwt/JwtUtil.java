package com.zbalogh.reservation.apiserver.auth.jwt;

import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbalogh.reservation.apiserver.config.ReservationAppConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtUtil {
	
	@Autowired
	private ReservationAppConfig reservationAppConfig;

    public String extractUsername(String token)
    {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token)
    {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token)
    {
    	String SECRET_KEY = getSecretKey();
    	
    	//SECRET_KEY = TextCodec.BASE64URL.encode(SECRET_KEY);
    	
        return Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }

    /*
    public String generateToken(UserDetails userDetails)
    {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject)
    {
    	String SECRET_KEY = getSecretKey();
    	
    	//SECRET_KEY = TextCodec.BASE64URL.encode(SECRET_KEY);
    	
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration( new Date( System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 365) ) )	// 365 days
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes()).compact();
    }
    */
    
    public Boolean validateToken(String token)
    {
        return !isTokenExpired(token);
    }
    
    private String getSecretKey()
    {
    	String secret = reservationAppConfig.getJwtSecretKey();
    	
    	return secret;
    }
    
}
