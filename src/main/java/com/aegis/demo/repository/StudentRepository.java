package com.aegis.demo.repository;

import com.aegis.demo.pojo.Student;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.Transient;
import javax.transaction.Transactional;
import java.util.Optional;

/**
 * @author 李成超
 * @date 2019/10/9 16:18
 * @description TODO
 **/
public interface StudentRepository extends CrudRepository<Student,String> , JpaSpecificationExecutor<Student> {
    /**
     * 查询学生列表
     * @param pageable
     * @return
     */
    @Query("select s from Student s")
    Page<Student> findList(Pageable pageable);

    /**
     * 根据id查询学生列表
     * @param sId
     * @return
     */
    Optional<Student> findBysId(String sId);


    /**
     * 根据用户名查询学生列表
     * （简单自定义查询）
     * @param sName
     * @param pageable
     * @return
     */
    Page<Student> findBysNameLike(String sName,Pageable pageable);

    /**
     * 根据id删除学生
     * @param sId
     */
    @Modifying
    @Transactional
    void deleteBysId(String sId);

    /**
     * 添加学生
     * @param stu
     * @return
     */
    @Override
    @Modifying
    @Transactional
    Student save(@Param("stu") Student stu);

}
