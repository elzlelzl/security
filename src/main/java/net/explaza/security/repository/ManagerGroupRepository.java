package net.explaza.security.repository;

import net.explaza.security.domain.entity.ManagerGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ManagerGroupRepository extends JpaRepository<ManagerGroup, Long> {


    Optional<ManagerGroup> findByGroup_IdAndManager_Email(Long groupId, String email);

    Optional<ManagerGroup> deleteByGroup_IdAndManager_Id(Long groupId, Long managerId);

    List<ManagerGroup> findAllByGroup_Id(Long id);
    List<ManagerGroup> findAllByManager_Id(Long id);
}
