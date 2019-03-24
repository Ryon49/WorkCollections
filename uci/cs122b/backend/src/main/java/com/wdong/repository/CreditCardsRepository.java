package com.wdong.repository;

import com.wdong.model.CreditCard;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CreditCardsRepository extends CrudRepository<CreditCard, String> {

    boolean existsById(String id);

    Optional<CreditCard> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
}

