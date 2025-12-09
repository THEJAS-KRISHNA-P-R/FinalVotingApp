package com.spring.tkpr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "app_users")
@Data
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    // Password is stored hashed + never sent to frontend
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Long getVotedFor() {
		return votedFor;
	}
	public void setVotedFor(Long votedFor) {
		this.votedFor = votedFor;
	}
	public String getVotedForName() {
		return votedForName;
	}
	public void setVotedForName(String votedForName) {
		this.votedForName = votedForName;
	}
	private Long votedFor;
    private String votedForName;

	public User orElse(User user) {
		// TODO Auto-generated method stub
		return null;
	}
}