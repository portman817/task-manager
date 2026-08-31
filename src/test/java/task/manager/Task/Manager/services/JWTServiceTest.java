package task.manager.Task.Manager.services;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import task.manager.Task.Manager.entity.User;
import task.manager.Task.Manager.enums.Role;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
@ExtendWith(MockitoExtension.class)
public class JWTServiceTest {
    private User user;
    private final String SECRET_KEY = "my-super-secret-key-my-super-secret-key";
    @InjectMocks
    JWTService jwtService;
    @BeforeEach
    void Setup(){
        user = new User();
        user.setUserId(3L);
        user.setUsername("Anna");
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
    }
    @Test
    void generateToken_ShouldGenerateTokenSuccessfully(){
        String token = jwtService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isBlank());
    }
    @Test
    void extractUsername_ShouldReturnUsernameFromToken(){
        String token = jwtService.generateToken(user);
        String username = jwtService.extractUsername(token);
        assertEquals(user.getUsername(), username);
    }
    @Test
    void extractRole_ShouldReturnRoleFromToken(){
        String token = jwtService.generateToken(user);
        String role = jwtService.extractRole(token);
        assertEquals(user.getRole().name(), role);
    }
    @Test
    void isTokenValid_ShouldReturnTrueForValidToken(){
        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token);
        assertTrue(isValid);
    }
    @Test
    void isTokenValid_ShouldReturnFalseForInvalidToken(){
        String token = "Wrong token";
        boolean isValid = jwtService.isTokenValid(token);
        assertFalse(isValid);
    }
    @Test
    void extractAllClaims_ShouldReturnClaimsFromToken(){
        String token = jwtService.generateToken(user);
        Claims claims = jwtService.extractAllClaims(token);
        assertEquals(user.getUsername(), claims.getSubject());
        assertEquals(user.getRole().name(), claims.get("role", String.class));
        assertEquals(user.getUserId(), claims.get("userId", Long.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }
}
