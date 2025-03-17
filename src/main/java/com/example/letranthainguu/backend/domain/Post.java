package com.example.letranthainguu.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    private String image;

    @Lob
    @NotBlank
    private String content;

    private Integer status;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;
}
