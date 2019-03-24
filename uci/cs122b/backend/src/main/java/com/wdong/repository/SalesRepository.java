package com.wdong.repository;

import com.wdong.model.Sale;
import org.springframework.data.repository.CrudRepository;

public interface SalesRepository extends CrudRepository<Sale, Integer> {

}
