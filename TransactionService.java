package com.simplifica.simplificajava.service;

import com.simplifica.simplificajava.dto.CategoryReportDTO;
import com.simplifica.simplificajava.dto.TransactionDTO;
import com.simplifica.simplificajava.model.Category;
import com.simplifica.simplificajava.model.Transaction;
import com.simplifica.simplificajava.model.User;
import com.simplifica.simplificajava.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    public List<TransactionDTO> findAllTransactions(Long userId, LocalDate startDate, LocalDate endDate, String type, Long categoryId) {
        List<Transaction> transactions;

        if (startDate != null && endDate != null && type != null && categoryId != null) {
            transactions = transactionRepository.findByUserIdAndDateBetweenAndTypeAndCategoryIdOrderByDateDesc(userId, startDate, endDate, type, categoryId);
        } else if (startDate != null && endDate != null && type != null) {
            transactions = transactionRepository.findByUserIdAndDateBetweenAndTypeOrderByDateDesc(userId, startDate, endDate, type);
        } else if (startDate != null && endDate != null && categoryId != null) {
            transactions = transactionRepository.findByUserIdAndDateBetweenAndCategoryIdOrderByDateDesc(userId, startDate, endDate, categoryId);
        } else if (startDate != null && endDate != null) {
            transactions = transactionRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
        } else if (type != null) {
            transactions = transactionRepository.findByUserIdAndTypeOrderByDateDesc(userId, type);
        } else if (categoryId != null) {
            transactions = transactionRepository.findByUserIdAndCategoryIdOrderByDateDesc(userId, categoryId);
        } else {
            transactions = transactionRepository.findByUserId(userId);
        }

        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<TransactionDTO> findTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(this::convertToDto);
    }

    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        Optional<User> userOptional = userService.getUserEntityById(transactionDTO.getUserId());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Optional<Category> categoryOptional = categoryService.getCategoryEntityById(transactionDTO.getCategoryId());
        if (categoryOptional.isEmpty()) {
            throw new RuntimeException("Category not found");
        }

        Transaction transaction = new Transaction();
        transaction.setUser(userOptional.get());
        transaction.setCategory(categoryOptional.get());
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setType(transactionDTO.getType());
        transaction.setDate(transactionDTO.getDate() != null ? transactionDTO.getDate() : LocalDate.now());
        transaction.setIsRecurring(transactionDTO.getIsRecurring() != null ? transactionDTO.getIsRecurring() : false);
        transaction.setRecurringFrequency(transactionDTO.getRecurringFrequency());
        transaction.setRecurringEndDate(transactionDTO.getRecurringEndDate());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDto(savedTransaction);
    }

    public Optional<TransactionDTO> updateTransaction(Long id, TransactionDTO transactionDTO) {
        return transactionRepository.findById(id).map(existingTransaction -> {
            if (transactionDTO.getCategoryId() != null) {
                Optional<Category> categoryOptional = categoryService.getCategoryEntityById(transactionDTO.getCategoryId());
                if (categoryOptional.isEmpty()) {
                    throw new RuntimeException("Category not found");
                }
                existingTransaction.setCategory(categoryOptional.get());
            }
            if (transactionDTO.getDescription() != null) {
                existingTransaction.setDescription(transactionDTO.getDescription());
            }
            if (transactionDTO.getAmount() != null) {
                existingTransaction.setAmount(transactionDTO.getAmount());
            }
            if (transactionDTO.getType() != null) {
                existingTransaction.setType(transactionDTO.getType());
            }
            if (transactionDTO.getDate() != null) {
                existingTransaction.setDate(transactionDTO.getDate());
            }
            if (transactionDTO.getIsRecurring() != null) {
                existingTransaction.setIsRecurring(transactionDTO.getIsRecurring());
            }
            if (transactionDTO.getRecurringFrequency() != null) {
                existingTransaction.setRecurringFrequency(transactionDTO.getRecurringFrequency());
            }
            if (transactionDTO.getRecurringEndDate() != null) {
                existingTransaction.setRecurringEndDate(transactionDTO.getRecurringEndDate());
            }
            Transaction updatedTransaction = transactionRepository.save(existingTransaction);
            return convertToDto(updatedTransaction);
        });
    }

    public boolean deleteTransaction(Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Map<String, Double> getMonthlyBalance(Long userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<Transaction> incomes = transactionRepository.findByUserIdAndDateBetweenAndType(userId, startDate, endDate, "income");
        List<Transaction> expenses = transactionRepository.findByUserIdAndDateBetweenAndType(userId, startDate, endDate, "expense");

        double incomeTotal = incomes.stream().mapToDouble(Transaction::getAmount).sum();
        double expenseTotal = expenses.stream().mapToDouble(Transaction::getAmount).sum();

        return Map.of(
                "incomeTotal", incomeTotal,
                "expenseTotal", expenseTotal,
                "balance", incomeTotal - expenseTotal
        );
    }

    public List<TransactionDTO> getDailyReport(Long userId, LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return transactionRepository.findByUserIdAndDateOrderByDateDesc(userId, date).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getMonthlyReport(Long userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return transactionRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Map<Integer, Map<String, Double>> getAnnualReport(Long userId, int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);

        Map<Integer, List<Transaction>> transactionsByMonth = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getDate().getMonthValue()));

        return transactionsByMonth.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            double incomeTotal = entry.getValue().stream().filter(t -> "income".equals(t.getType())).mapToDouble(Transaction::getAmount).sum();
                            double expenseTotal = entry.getValue().stream().filter(t -> "expense".equals(t.getType())).mapToDouble(Transaction::getAmount).sum();
                            return Map.of(
                                    "incomeTotal", incomeTotal,
                                    "expenseTotal", expenseTotal,
                                    "balance", incomeTotal - expenseTotal
                            );
                        }
                ));
    }

    public List<CategoryReportDTO> getCategoryReport(Long userId, LocalDate startDate, LocalDate endDate, String type) {
        List<Transaction> transactions;
        if (startDate != null && endDate != null && type != null) {
            transactions = transactionRepository.findByUserIdAndDateBetweenAndType(userId, startDate, endDate, type);
        } else if (startDate != null && endDate != null) {
            transactions = transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        } else if (type != null) {
            transactions = transactionRepository.findByUserIdAndTypeOrderByDateDesc(userId, type);
        } else {
            transactions = transactionRepository.findByUserId(userId);
        }

        Map<Long, List<Transaction>> transactionsByCategory = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getCategory().getId()));

        return transactionsByCategory.entrySet().stream()
                .map(entry -> {
                    Long categoryId = entry.getKey();
                    Category category = entry.getValue().get(0).getCategory(); // Assuming all transactions in a group have the same category
                    double totalAmount = entry.getValue().stream().mapToDouble(Transaction::getAmount).sum();
                    long transactionCount = entry.getValue().size();
                    return new CategoryReportDTO(categoryId, category.getName(), category.getType(), totalAmount, transactionCount);
                })
                .collect(Collectors.toList());
    }


    private TransactionDTO convertToDto(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getUser().getId(),
                transaction.getCategory().getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDate(),
                transaction.getIsRecurring(),
                transaction.getRecurringFrequency(),
                transaction.getRecurringEndDate(),
                transaction.getCategory() != null ? transaction.getCategory().getName() : null
        );
    }

}

