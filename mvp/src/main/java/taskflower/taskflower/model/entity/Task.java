package taskflower.taskflower.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import taskflower.taskflower.model.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Task extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String title;

    @Column
    private String description;

    @ManyToOne
    @NotNull
    private User user;

    @Column
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;

//    단방향 참조
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Tag> tags;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

}
