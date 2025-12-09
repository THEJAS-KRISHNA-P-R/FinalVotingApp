package com.spring.tkpr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Optional;

@Service
public class PollingService {

    @Autowired private UserRepository userRepo;
    @Autowired private NomineeRepository nomineeRepo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // REGISTER → hash password
    public boolean registerUser(String username, String email, String password) {
        if (userRepo.findByUsername(username).isPresent() || userRepo.findByEmail(email).isPresent()) {
            return false;
        }
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(encoder.encode(password));   // ← hashed
        userRepo.save(u);
        return true;
    }

    // LOGIN → compare raw password with hash
    public Optional<User> loginUser(String username, String email, String rawPassword) {
        return userRepo.findByUsername(username)
                .filter(u -> u.getEmail().equals(email))
                .filter(u -> encoder.matches(rawPassword, u.getPassword())); // ← secure check
    }

    // VOTE → unchanged
    public boolean vote(Long userId, Long nomineeId) {
        User user = userRepo.findById(userId).orElse(null);
        Nominee nominee = nomineeRepo.findById(nomineeId).orElse(null);
        if (user == null || nominee == null || user.getVotedFor() != null) return false;

        user.setVotedFor(nomineeId);
        user.setVotedForName(nominee.getName());
        nominee.setVoteCount(nominee.getVoteCount() + 1);
        userRepo.save(user);
        nomineeRepo.save(nominee);
        return true;
    }

    // ADMIN → unchanged (hardcoded for simplicity)
    public boolean adminLogin(String name, String pass) {
        return "admin".equals(name) && "admin123".equals(pass);
    }

    public Nominee addNominee(String name) {
        if (nomineeRepo.findByName(name).isPresent()) return null;
        Nominee n = new Nominee();
        n.setName(name.trim());
        return nomineeRepo.save(n);
    }

    public void deleteNominee(Long id) {
        nomineeRepo.deleteById(id);
    }

    public Page<Nominee> getNominees(int page, int size) {
        return nomineeRepo.findAll(PageRequest.of(page, size));
    }

    public List<Nominee> getAllNominees() {
        return nomineeRepo.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepo.findById(id);
    }
}