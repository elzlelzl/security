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
@Table(name = "resources")
public class Resources {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @Comment("http_method")
    @Column(name = "http_method", columnDefinition = "varchar(30) default null")
    private String httpMethod;

    @Comment("resource_name")
    @Column(name = "resource_name", columnDefinition = "varchar(255) default null")
    private String resourceName;

    @Comment("상세 내용")
    @Column(name = "detail", columnDefinition = "text default null")
    private String detail;

    @Comment("orderNum")
    @Column(name = "order_num", columnDefinition = "int(10) default null")
    private Integer orderNum;

    @Comment("resource_type")
    @Column(name = "resource_type", columnDefinition = "varchar(30) default null")
    private String resourceType;

    @Comment("생성일")
    @Column(name="created_at", columnDefinition="timestamp default current_timestamp")
    private LocalDateTime createdAt;

    @Comment("업데이트일")
    @Column(name="updated_at", columnDefinition="timestamp default current_timestamp ON UPDATE current_timestamp")
    private  LocalDateTime updatedAt;

    @Comment("삭제날짜")
    @Column(name = "deleted_at", columnDefinition="timestamp null default null ")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "resources")
    private List<GroupResources> groupList = new ArrayList<>();

    @Builder
    public Resources(Long id, String httpMethod, String detail, Integer orderNum, String resourceName, String resourceType,
                     LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt,
                     List<GroupResources> groupList) {
        this.id = id;
        this.httpMethod = httpMethod;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.detail = detail;
        this.orderNum = orderNum;
        this.groupList = groupList;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

}
