package com.example.autopark;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("select c from Car c where lower(c.brand) like lower(concat('%', :text, '%')) " +
            "or lower(c.ownerName) like lower(concat('%', :text, '%'))")
    List<Car> search(@Param("text") String text, Sort sort);

    @Query("select c.registrationDate, count(c) from Car c " +
            "where c.registrationDate >= :from " +
            "group by c.registrationDate order by c.registrationDate")
    List<Object[]> countByDay(@Param("from") LocalDate from);
}
