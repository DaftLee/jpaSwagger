package com.aegis.demo.repository;

import com.aegis.demo.pojo.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

/**
 * @author 李成超
 * @date 2019/10/11 9:57
 * @description TODO
 **/
public interface ScoreRepository extends CrudRepository<Score,String> {
    /**
     * 联合查询
     * @param specification
     * @param pageable
     * @return
     */
    Page<Score> findAll(Specification<Score> specification, Pageable pageable);
}
