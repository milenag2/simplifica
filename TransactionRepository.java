package com.simplifica.simplificajava.repository;

import com.simplifica.simplificajava.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);
    List<Transaction> findByUserIdAndTypeOrderByDateDesc(Long userId, String type);
    List<Transaction> findByUserIdAndCategoryIdOrderByDateDesc(Long userId, Long categoryId);
    List<Transaction> findByUserIdAndDateOrderByDateDesc(Long userId, LocalDate date);
    List<Transaction> findByUserIdAndDateBetweenAndTypeOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate, String type);
    List<Transaction> findByUserIdAndDateBetweenAndCategoryIdOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate, Long categoryId);
    List<Transaction> findByUserIdAndDateBetweenAndTypeAndCategoryIdOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate, String type, Long categoryId);
    List<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<Transaction> findByUserIdAndDateBetweenAndType(Long userId, LocalDate startDate, LocalDate endDate, String type);
    List<Transaction> findByUserIdAndDateBetweenAndCategory_Id(Long userId, LocalDate startDate, LocalDate endDate, Long categoryId);

}

