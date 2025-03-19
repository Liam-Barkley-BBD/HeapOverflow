package com.heapoverflow.cli.utils;

import static org.junit.jupiter.api.Assertions.*;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;

@SpringBootTest
public class TokenUtilsTest {
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode("testSecretKeyForJwtSigning12345testSecretKeyForJwtSigning12345"));
    private static final MacAlgorithm ALGORITHM = Jwts.SIG.HS256;

    private String generateJWT(String key, String value) {
        return Jwts.builder()
                .claim(key, value)
                .signWith(SECRET_KEY, ALGORITHM)
                .compact();
    }

    @Test
    void testDecodeJWT_validToken() {
        try {
            String key = "sub";
            String value = "1234567890";
            String jwt = generateJWT(key, value);
            String result = TokenUtils.decodeJWT(key, jwt);
            assertEquals(value, result);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testDecodeJWT_invalidTokenStructure() {
        try {
            String jwt = "invalid.jwt.token";
            TokenUtils.decodeJWT("sub", jwt);
            fail("Expected exception due to invalid token structure");
        } catch (Exception e) {
            assertTrue(true, "Exception was correctly thrown");
        }
    }

    @Test
    void testDecodeJWT_missingKey() {
        try {
            String jwt = generateJWT("name", "John Doe");
            String result = TokenUtils.decodeJWT("sub", jwt);
            assertNull(result);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testDecodeJWT_malformedBase64() {
        try {
            String jwt = "header.malformedPayload.signature";
            TokenUtils.decodeJWT("sub", jwt);
            fail("Expected exception due to malformed Base64");
        } catch (Exception e) {
            assertTrue(true, "Exception was correctly thrown");
        }
    }
}
