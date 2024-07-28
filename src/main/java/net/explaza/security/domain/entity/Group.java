package net.explaza.security.domain.entity;

import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "user_group")
public class Group {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @Comment("그룹명")
    @Column(name = "name", columnDefinition = "varchar(125)")
    private String name;

    @Comment("roles")
    @Column(name = "roles", columnDefinition = "varchar(50) default null")
    private  String roles;

    @Comment("LEVEL")
    @Column(columnDefinition = "int")
    private Integer level;

    @Comment("상태값")
    @Column(name = "status", columnDefinition = "varchar(30) default null")
    private  String status;

    @Comment("생성일")
    @Column(name="created_at", columnDefinition="timestamp default current_timestamp")
    private LocalDateTime createdAt;

    @Comment("업데이트일")
    @Column(name="updated_at", columnDefinition="timestamp default current_timestamp ON UPDATE current_timestamp")
    private  LocalDateTime updatedAt;

    @Comment("삭제날짜")
    @Column(name = "deleted_at", columnDefinition="timestamp null default null ")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "group")
    private List<ManagerGroup> managerList = new ArrayList<>();

    @OneToMany(mappedBy = "group")
    private List<GroupResources> resourcesList = new ArrayList<>();

    @Builder
    public Group(Long id, String name, String roles, Integer level, String status,
                 LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt,
                 List<ManagerGroup> managerList, List<GroupResources> resourcesList) {
        this.id = id;
        this.name = name;
        this.roles = roles;
        this.level = level;
        this.status = status;
        this.managerList = managerList;
        this.resourcesList = resourcesList;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

}
