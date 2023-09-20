package ru.practicum.shareit.request.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Integer id;

    @NotBlank
    @Column(nullable = false)
     String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
     User requestor;


    @NotNull
    @Column(name = "creation_date", nullable = false)
     LocalDateTime created;
}