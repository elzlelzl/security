package net.explaza.security.repository;

import net.explaza.security.domain.entity.Manager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Manager, Long> {

    Optional<Manager> findByEmailAndDeletedAtIsNull(String userEmail);
    Optional<Manager> findByNameAndDeletedAtIsNull(String Name);
    Optional<Manager> findByIdAndDeletedAtIsNull(Long Id);


    Page findAll(Specification<Manager> employeeName, Pageable pageable);

    @Query("SELECT m FROM Manager m WHERE m.deletedAt is null and (m.email LIKE %:keyword% or m.name LIKE %:keyword%) ")
    Page<Manager> searchByEmailAndName(@Param("keyword") String keyword, Pageable pageable);

//    @Query(value = "SELECT m.* FROM manager m Left OUTER JOIN\n" +
//            "manager_group g ON m.id = g.manager_id\n" +
//            "WHERE (m.deleted_at IS NULL AND (m.email LIKE :keyword% OR m.name LIKE :keyword%)) AND (g.group_id != :groupId OR g.group_id IS NULL)" ,nativeQuery = true)
    @Query( value = "SELECT m.* FROM manager m left join\n" +
            "           manager_group g on\n" +
            "           m.id = g.manager_id \n" +
            "           WHERE (m.deleted_at IS NULL AND " +
            "           (m.email LIKE CONCAT('%',:keyword, '%') OR " +
            "           m.name LIKE CONCAT('%',:keyword, '%'))) and " +
            "           ( g.group_id != :groupId OR g.group_id IS NULL) " +
            "           AND (m.id NOT IN (SELECT manager_id FROM manager_group WHERE group_id = :groupId)) GROUP BY m.id",
            countQuery = "SELECT COUNT(*) FROM manager m left join\n" +
                    "           manager_group g on\n" +
                    "           m.id = g.manager_id \n" +
                    "           WHERE (m.deleted_at IS NULL AND " +
                    "           (m.email LIKE CONCAT('%',:keyword, '%') OR " +
                    "           m.name LIKE CONCAT('%',:keyword, '%'))) and " +
                    "           ( g.group_id != :groupId OR g.group_id IS NULL)" +
                    "           AND (m.id NOT IN (SELECT manager_id FROM manager_group WHERE group_id = :groupId)) GROUP BY m.id",
            nativeQuery = true
    )
    Page<Manager> searchByEmailAndName(@Param("keyword") String keyword, @Param("groupId") Long groupId, Pageable pageable);

    @Query(value = "SELECT a.* FROM manager a JOIN\n" +
            "            manager_group b ON a.id = b.manager_id\n" +
            "            WHERE (a.email like CONCAT('%',:keyword, '%') OR a.name like CONCAT('%',:keyword, '%'))\n" +
            "            AND b.group_id = :groupId AND a.deleted_At IS NULL ",nativeQuery = true)
    List<Manager> findBySelectManager(@Param("keyword") String keyword, @Param("groupId") Long groupId);

}