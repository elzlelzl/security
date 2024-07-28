package net.explaza.security.repository;
import net.explaza.security.domain.entity.Resources;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ResourcesRepository extends JpaRepository<Resources, Long> {




    @Query( value = "SELECT r.* FROM resources r left join\n" +
            "           group_resources g on\n" +
            "           r.id = g.resources_id \n" +
            "           WHERE (r.deleted_at IS NULL AND " +
            "           (r.resource_name LIKE CONCAT('%',:keyword, '%') OR " +
            "           r.http_method LIKE CONCAT('%',:keyword, '%'))) and " +
            "           ( g.group_id != :groupId OR g.group_id IS NULL) " +
            "           AND (r.id NOT IN (SELECT resources_id FROM group_resources WHERE group_id = :groupId)) GROUP BY r.id",
            countQuery = "SELECT COUNT(*) FROM resources r left join\n" +
                    "           group_resources g on\n" +
                    "           r.id = g.resources_id \n" +
                    "           WHERE (r.deleted_at IS NULL AND " +
                    "           (r.resource_name LIKE CONCAT('%',:keyword, '%') OR " +
                    "           r.http_method LIKE CONCAT('%',:keyword, '%'))) and " +
                    "           ( g.group_id != :groupId OR g.group_id IS NULL)" +
                    "           AND (r.id NOT IN (SELECT resources_id FROM group_resources WHERE group_id = :groupId)) GROUP BY r.id",
            nativeQuery = true
    )
    Page<Resources> searchByEmailAndName(@Param("keyword") String keyword, @Param("groupId") Long groupId, Pageable pageable);


    @Query(value = "SELECT a.* FROM resources a JOIN\n" +
            "            group_resources b ON a.id = b.resources_id\n" +
            "            WHERE (a.resource_name like CONCAT('%',:keyword, '%') OR a.http_method like CONCAT(:keyword, '%'))\n" +
            "            AND b.group_id = :groupId AND a.deleted_At IS NULL ORDER BY id ASC",nativeQuery = true)
    List<Resources> findBySelectResources(@Param("keyword") String keyword, @Param("groupId") Long groupId);

    Optional<Resources> findById(Long id);

}
