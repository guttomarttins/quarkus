package br.com.gt.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@Table(name = "POSTS")
public class Post  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_text", nullable = false)
    private String text;

    @Column(name = "dateTime", nullable = false)
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @PrePersist
    protected void prePersist(){
        this.dateTime = LocalDateTime.now();
    }
}
