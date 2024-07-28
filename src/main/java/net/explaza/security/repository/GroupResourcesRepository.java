package net.explaza.security.repository;

import net.explaza.security.domain.entity.GroupResources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface GroupResourcesRepository extends JpaRepository<GroupResources, Long> {


    List<GroupResources> findAllByGroup_IdOrderByResourcesAsc(Long id);

    Optional<GroupResources> findByGroup_IdAndResources_Id(Long groupId, Long id);

    void deleteByGroup_IdAndResources_Id(Long groupId, Long resourceId);


}
