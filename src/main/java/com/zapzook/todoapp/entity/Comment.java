package com.zapzook.todoapp.entity;

import com.zapzook.todoapp.dto.CommentRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String contents;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;

    public Comment(String contents, Todo todo, User user){
        this.contents = contents;
        this.todo = todo;
        this.user = user;
    }

    public void update(String contents) {
        this.contents = contents;
    }
}
