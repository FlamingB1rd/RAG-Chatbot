//package com.tu.chatbot.security.jwt;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import lombok.val;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//import java.security.NoSuchAlgorithmException;
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.Base64;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//@Service
//public class JwtService {
//    private String jwtSigningKey = "";
//
//    public JwtService() {
//        try {
//            val keyGenerator = KeyGenerator.getInstance("HmacSHA256");
//            val secretKey = keyGenerator.generateKey();
//            jwtSigningKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public String generateJwt(String username) {
//
//        Map<String, Object> claims = new HashMap<>();
//
//        return Jwts.builder()
//                .claims()
//                .add(claims)
//                .subject(username)
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(Date.from(Instant.now().plus(35, ChronoUnit.MINUTES)))
//                .and()
//                .signWith(getSigningKey())
//                .compact();
//    }
//
//    public boolean isJwtValid(String jwt, UserDetails userDetails) {
//        val userName = extractUserName(jwt);
//        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(jwt);
//    }
//
//    public String extractUserName(String jwt) {
//        return extractClaim(jwt, Claims::getSubject);
//    }
//
//    private boolean isTokenExpired(String jwt) {
//        return extractExpiration(jwt).before(new Date());
//    }
//
//    private Date extractExpiration(String jwt) {
//        return extractClaim(jwt, Claims::getExpiration);
//    }
//
//    private <T> T extractClaim(String jwt, Function<Claims, T> claimsResolvers) {
//        final Claims claims = extractAllClaims(jwt);
//        return claimsResolvers.apply(claims);
//    }
//
//    private Claims extractAllClaims(String jwt) {
//        return Jwts.parser()
//                .verifyWith(getSigningKey())
//                .build()
//                .parseSignedClaims(jwt)
//                .getPayload();
//    }
//
//    private SecretKey getSigningKey() {
//        val keyBytes = Decoders.BASE64.decode(jwtSigningKey);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//}
