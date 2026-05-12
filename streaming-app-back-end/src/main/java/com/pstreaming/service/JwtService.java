package com.pstreaming.service;

import com.pstreaming.domain.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;

@Service
public class JwtService {

    /* Los tokens JWT nos sirven para comunicar las sesiones de usuario al proyecto de frontend*/

 /* La secret key es una variable declarada en el app properties que es una  high-entropy string que se 
    utiliza para firmar y verificar criptográficamente tokens, garantizando la integridad y autenticidad de los datos. 
    Permite a los servidores validar que los tokens son legítimos y no han sido manipulados, 
    sin necesidad de realizar una consulta a la base de datos para cada solicitud. */
    @Value("${jwt.secret}")
    private String secretK;
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    @Value("${jwt.temp.expiration}")
    private long jwtTempExpiration;
    @Autowired
    private UsuarioService usuarioService;

    /* Convierte el String secreto en un SecretKey criptográfico
     JJWT necesita un objeto SecretKey, no un String directamente*/
    private SecretKey getSigningKey() {
        byte[] keyByte = Decoders.BASE64.decode(secretK);
        return Keys.hmacShaKeyFor(keyByte);
    }

    /* Genera un JWT definitivo para un usuario autenticado
     UserDetails contiene el correo y los roles del usuario*/
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                /* El subject identifica al usuario, usamos el correo */
                .subject(userDetails.getUsername())
                /* Agrega los roles como claim adicional en el payload del token */
                .claim("roles", userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                /* Fecha en que se generó el token */
                .issuedAt(now)
                /* Fecha en que expira el token */
                .expiration(expireDate)
                /* Firma el token con la clave secreta usando HMAC-SHA256 */
                .signWith(getSigningKey())
                /* Construye y serializa el token como String compacto: header.payload.signature */
                .compact();
    }

    public String generateTempToken(Usuario usuario) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + jwtTempExpiration);

        return Jwts.builder()
                .subject(usuario.getCorreo())
                .claim("roles", usuario.getRol())
                /* Marca este token como temporal, el backend lo valida antes de emitir el JWT definitivo */
                .claim("scope", "2fa-pending")
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(getSigningKey())
                .compact();
    }

    /* Extrae el correo (subject) de un token válido */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /* Extrae un claim específico del token usando una función lambda */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTempToken(String token) {
        Claims claims = extractAllClaims(token);
        /* Revisa el claim "scope" que agregamos en generateTempToken */
        return "2fa-pending".equals(claims.get("scope", String.class));
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /* Parsea y valida la firma del token, retorna todos los claims del payload
    Lanza excepción automáticamente si el token está malformado, expirado o la firma no coincide */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
