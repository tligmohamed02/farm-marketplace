package net.mohamed.devwebproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "authentications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Authentication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;

    @Column(nullable = false)
    private String passwordHash;

    private String salt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    private int failedAttempts = 0;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}