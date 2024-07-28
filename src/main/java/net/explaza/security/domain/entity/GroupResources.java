package net.explaza.security.domain.entity;

import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Getter
@Table(name = "group_resources")
public class GroupResources{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable=false)
    private Group group;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resources_id", nullable=false)
    private Resources resources;

    @Comment("생성일")
    @Column(name="created_at", columnDefinition="timestamp default current_timestamp")
    private LocalDateTime createdAt;

    @Builder
    public GroupResources(Long id, Resources resources, Group group, LocalDateTime createdAt) {
        this.id = id;
        this.resources = resources;
        this.group = group;
        this.createdAt = createdAt;
    }
}
