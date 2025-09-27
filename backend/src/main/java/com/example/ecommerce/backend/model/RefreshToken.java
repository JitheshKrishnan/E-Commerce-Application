package com.example.ecommerce.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false, unique = true)
    private String accessToken;

    @Column(nullable = false)
    private Instant expiryDate;

    public RefreshToken(User user, String token, Instant expiryDate) {
        this.user = user;
        this.accessToken = token;
        this.expiryDate = expiryDate;
    }
}