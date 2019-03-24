package com.wdong.repository.simple;

import com.wdong.model.SimpleGenre;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SimpleGenresRepository extends PagingAndSortingRepository<SimpleGenre, String> { }