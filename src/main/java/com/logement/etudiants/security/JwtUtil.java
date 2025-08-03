package com.logement.etudiants.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilitaire pour la gestion des tokens JWT
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Génère la clé de signature sécurisée
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Génère un token JWT pour un utilisateur
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Génère un token JWT avec des claims personnalisés
     */
    public String generateToken(String username, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        return createToken(claims, username);
    }

    /**
     * Crée le token JWT
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extrait l'username du token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration du token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait la date d'émission du token
     */
    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    /**
     * Extrait un claim spécifique du token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait tous les claims du token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Token JWT expiré: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("Token JWT non supporté: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("Token JWT malformé: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("Token JWT invalide: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de l'analyse du token JWT: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Vérifie si le token est expiré
     */
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Valide le token JWT
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = extractUsername(token);
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token JWT invalide: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Récupère la durée d'expiration en secondes
     */
    public Long getExpirationInSeconds() {
        return expiration / 1000;
    }

    /**
     * Récupère la date d'expiration du token sous forme LocalDateTime
     */
    public LocalDateTime getExpirationAsLocalDateTime(String token) {
        Date expirationDate = extractExpiration(token);
        return expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Vérifie si le token sera bientôt expiré (dans les 5 prochaines minutes)
     */
    public Boolean isTokenExpiringSoon(String token) {
        try {
            Date expiration = extractExpiration(token);
            Date fiveMinutesFromNow = new Date(System.currentTimeMillis() + (5 * 60 * 1000));
            return expiration.before(fiveMinutesFromNow);
        } catch (JwtException e) {
            return true;
        }
    }

    /**
     * Rafraîchit un token (génère un nouveau token avec la même username)
     */
    public String refreshToken(String token) {
        try {
            String username = extractUsername(token);
            return generateToken(username);
        } catch (JwtException e) {
            log.warn("Impossible de rafraîchir le token: {}", e.getMessage());
            throw new IllegalArgumentException("Token invalide pour le rafraîchissement");
        }
    }

    /**
     * Extrait des informations détaillées du token pour le debugging
     */
    public Map<String, Object> getTokenInfo(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Map<String, Object> tokenInfo = new HashMap<>();

            tokenInfo.put("subject", claims.getSubject());
            tokenInfo.put("issuedAt", claims.getIssuedAt());
            tokenInfo.put("expiration", claims.getExpiration());
            tokenInfo.put("issuer", claims.getIssuer());
            tokenInfo.put("audience", claims.getAudience());
            tokenInfo.put("isExpired", isTokenExpired(token));
            tokenInfo.put("timeUntilExpiration",
                    claims.getExpiration().getTime() - System.currentTimeMillis());

            return tokenInfo;
        } catch (JwtException e) {
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("error", e.getMessage());
            errorInfo.put("valid", false);
            return errorInfo;
        }
    }
}



