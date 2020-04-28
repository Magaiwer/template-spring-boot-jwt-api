package com.serverside.api.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.serverside.api.property.JwtConfiguration;
import com.serverside.api.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenProvider implements Serializable {
    private static final long serialVersionUID = -2550185165626007488L;

    private final JwtConfiguration jwtConfiguration;
    private final CustomUserDetailService customUserDetailService;

    @SneakyThrows
    public String generateEncryptedToken(Authentication authentication) {
        SignedJWT signedJWT = createSignedJWT(authentication);
        log.info("Token Encrypted generated '{}'", signedJWT.serialize());
        return encryptToken(signedJWT);
    }

    @SneakyThrows
    public String generateSignedToken(Authentication authentication) {
        SignedJWT signedJWT = createSignedJWT(authentication);
        log.info("Token Signed generated '{}'", signedJWT.serialize());
        return signedJWT.serialize();
    }
    public Boolean validateToken(String token, Authentication userAuthentication) {
        log.info("validate token");
        final String username = getUsernameFromToken(token);
        return (username.equals(userAuthentication.getName()) && !isTokenExpired(token));
    }

    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @SneakyThrows
    public void validateTokenSignature(String signedToken) {
        log.info("Starting method to validate token signature...");
        SignedJWT signedJWT = SignedJWT.parse(signedToken);
        log.info("Token Parsed! Retrieving public key from signed token");
        RSAKey publicKey = RSAKey.parse(signedJWT.getHeader().getJWK().toJSONObject());
        log.info("Public key retrieved, validating signature. . . ");

        if (!signedJWT.verify(new RSASSAVerifier(publicKey)))
            throw new AccessDeniedException("Invalid token signature!");

        log.info("The token has a valid signature");
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, JWTClaimsSet::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, JWTClaimsSet::getExpirationTime);
    }

    public <T> T getClaimFromToken(String token, Function<JWTClaimsSet, T> claimsResolver) {
        final JWTClaimsSet claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    @SneakyThrows
    private JWTClaimsSet getAllClaimsFromToken(String token) {
        return SignedJWT.parse(token).getJWTClaimsSet();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private JWTClaimsSet generateClaimSet(Authentication authentication) {
        return new JWTClaimsSet.Builder()
                .subject(authentication.getName())
                .issueTime(new Date())
                .expirationTime(new Date(new Date().getTime() + jwtConfiguration.getExpirationTimeMS() * 1000))
                .claim("roles", getAuthorities(authentication))
                .build();
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        log.info("Generate keyPairs ");
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.genKeyPair();
    }

    private List<String> getAuthorities(Authentication userAuthentication) {
        return userAuthentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    private SignedJWT createSignedJWT(Authentication authentication) throws NoSuchAlgorithmException, JOSEException {
        KeyPair rsaKeyPair = generateKeyPair();
        JWK jwk = new RSAKey.Builder((RSAPublicKey) rsaKeyPair.getPublic()).keyID(UUID.randomUUID().toString()).build();
        log.info("Add key public on header request '{}' ", jwk);
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS512)
                .jwk(jwk)
                .type(JOSEObjectType.JWT)
                .build(), generateClaimSet(authentication));
        log.info("Signing the token with the private RSA Key");
        RSASSASigner signer = new RSASSASigner(rsaKeyPair.getPrivate());
        jwt.sign(signer);
        return jwt;
    }

    private String encryptToken(SignedJWT signedJWTToken) throws JOSEException {
        log.info("Starting the encryptToken method");
        DirectEncrypter directEncrypter = new DirectEncrypter(jwtConfiguration.getPrivateKey().getBytes());
        JWEObject jweObject = new JWEObject(new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256)
                .contentType("JWT")
                .build(), new Payload(signedJWTToken));

        log.info("Encrypting token with system's private key");
        jweObject.encrypt(directEncrypter);
        log.info("Token encrypted  '{}' ", jweObject.serialize());
        return jweObject.serialize();
    }

    @SneakyThrows
    public String decryptToken(String encryptedToken) {
        log.info("Decrypting token and validating");
        JWEObject jweObject = JWEObject.parse(encryptedToken);
        DirectDecrypter directDecrypter = new DirectDecrypter(jwtConfiguration.getPrivateKey().getBytes());
        jweObject.decrypt(directDecrypter);
        log.info("Token decrypted, returning signed token . . . ");
        return jweObject.getPayload().toSignedJWT().serialize();
    }

}
