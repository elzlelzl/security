package net.explaza.security.dto;

import lombok.*;
import net.explaza.security.domain.entity.Group;
import net.explaza.security.domain.entity.Manager;
import net.explaza.security.domain.entity.ManagerGroup;

import java.time.LocalDateTime;

@NoArgsConstructor
@ToString
@Setter
@Getter
public class ManagerGroupDto {

    private Long id;
    private Manager manager;
    private Group group;
    private LocalDateTime createdAt;

    public ManagerGroup toEntity(){
        return ManagerGroup.builder()
                .id(id)
                .manager(manager)
                .group(group)
                .createdAt(createdAt)
                .build();
    }

    @Builder
    public ManagerGroupDto(Long id, Manager manager, Group group, LocalDateTime createdAt) {
        this.id = id;
        this.manager = manager;
        this.group = group;
        this.createdAt = createdAt;
    }
}
