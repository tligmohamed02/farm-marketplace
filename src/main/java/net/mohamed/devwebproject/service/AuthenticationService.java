package net.mohamed.devwebproject.service;


import net.mohamed.devwebproject.entity.Authentication;
import net.mohamed.devwebproject.entity.User;
import net.mohamed.devwebproject.repository.AuthenticationRepository;
import net.mohamed.devwebproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationRepository authRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();
        Optional<Authentication> authOpt = authRepository.findByUserUserId(user.getUserId());

        if (authOpt.isEmpty()) {
            return null;
        }

        Authentication auth = authOpt.get();

        if (auth.getFailedAttempts() >= 5) {
            throw new RuntimeException("Compte bloqué après trop de tentatives échouées");
        }

        if (passwordEncoder.matches(password, auth.getPasswordHash())) {
            auth.setLastLogin(new Date());
            auth.setFailedAttempts(0);
            authRepository.save(auth);
            return user;
        }

        auth.setFailedAttempts(auth.getFailedAttempts() + 1);
        authRepository.save(auth);
        return null;
    }

    public void registerUser(User user, String password) {
        userRepository.save(user);

        Authentication auth = new Authentication();
        auth.setUser(user);
        auth.setPasswordHash(passwordEncoder.encode(password));
        authRepository.save(auth);
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<Authentication> authOpt = authRepository.findByUserUserId(userId);
        if (authOpt.isEmpty()) {
            return false;
        }

        Authentication auth = authOpt.get();
        if (passwordEncoder.matches(oldPassword, auth.getPasswordHash())) {
            auth.setPasswordHash(passwordEncoder.encode(newPassword));
            authRepository.save(auth);
            return true;
        }
        return false;
    }
}