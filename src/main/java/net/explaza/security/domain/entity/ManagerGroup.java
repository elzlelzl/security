package net.explaza.security.domain.entity;

import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Getter
@Table(name = "manager_group")
public class ManagerGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_id", nullable=false)
    private Manager manager;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable=false)
    private Group group;

    @Comment("생성일")
    @Column(name="created_at", columnDefinition="timestamp default current_timestamp")
    private LocalDateTime createdAt;

    @Builder
    public ManagerGroup(Long id, Manager manager, Group group, LocalDateTime createdAt) {
        this.id = id;
        this.manager = manager;
        this.group = group;
        this.createdAt = createdAt;
    }
}
