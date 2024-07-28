package net.explaza.security.domain.entity;

import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"groupList"})
@Entity
@Table(name = "manager")
@EqualsAndHashCode(of = {"id"})
public class Manager/* implements UserDetails*/ {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(columnDefinition = "timestamp" ,nullable = true)
    private LocalDateTime email_verified_at;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(columnDefinition = "timestamp", nullable = true)
    private LocalDateTime last_singin_at;

    @Column(length = 30, nullable = false)
    private  String status;

    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ManagerGroup> groupList = new ArrayList<>();

    @Comment("생성일")
    @Column(name="created_at", columnDefinition="timestamp default current_timestamp")
    private  LocalDateTime createdAt;

    @Comment("업데이트일")
    @Column(name="updated_at", columnDefinition="timestamp default current_timestamp ON UPDATE current_timestamp")
    private  LocalDateTime updatedAt;

    @Comment("삭제날짜")
    @Column(name = "deleted_at", columnDefinition = "timestamp null default null")
    private LocalDateTime deletedAt;

    @Builder
    public Manager(Long id, String name,
                   String email, LocalDateTime email_verified_at,
                   String password, String status, LocalDateTime last_singin_at,
                   LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, List<ManagerGroup> groupList) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.email_verified_at = email_verified_at;
        this.password = password;
        this.status = status;
        this.last_singin_at = last_singin_at;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.groupList = groupList;
    }
}
