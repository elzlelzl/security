package net.explaza.security.dto;

import lombok.*;
import net.explaza.security.domain.entity.Group;
import net.explaza.security.domain.entity.GroupResources;
import net.explaza.security.domain.entity.ManagerGroup;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"managerList","resourcesList"})
@NoArgsConstructor
public class GroupDto {
    private Long id;
    private String name;
    private  String roles;
    private Integer level;
    private  String status;
    private LocalDateTime createdAt;
    private  LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private List<ManagerGroup> managerList = new ArrayList<>();
    private List<GroupResources> resourcesList = new ArrayList<>();

    private Integer managerCount;
    public Group toEntity(){
        return Group.builder()
                .id(id)
                .name(name)
                .roles(roles)
                .level(level)
                .status(status)
                .managerList(managerList)
                .resourcesList(resourcesList)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    @Builder
    public GroupDto(Long id, String name, String roles, Integer level, String status,
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

    public Page<GroupDto> toDtoList(Page<Group> boardList){
        Page<GroupDto> groupDtoList = boardList.map(m -> GroupDto.builder()
                .id(m.getId())
                .name(m.getName())
                .level(m.getLevel())
                .roles(m.getRoles())
                .status(m.getStatus())
                .managerList(m.getManagerList())
                .resourcesList(m.getResourcesList())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .deletedAt(m.getDeletedAt())
                .build());
        return groupDtoList;
    }

}
