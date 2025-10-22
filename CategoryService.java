package com.simplifica.simplificajava.service;

import com.simplifica.simplificajava.dto.CategoryDTO;
import com.simplifica.simplificajava.model.Category;
import com.simplifica.simplificajava.model.User;
import com.simplifica.simplificajava.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService; // To get User entities

    public List<CategoryDTO> findCategoriesByUserId(Long userId) {
        return categoryRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<CategoryDTO> findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::convertToDto);
    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Optional<User> userOptional = userService.getUserEntityById(categoryDTO.getUserId());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Check if category with same name already exists for the user
        if (categoryRepository.findByUserIdAndName(categoryDTO.getUserId(), categoryDTO.getName()).isPresent()) {
            throw new RuntimeException("Category with this name already exists for the user");
        }

        Category category = new Category();
        category.setUser(userOptional.get());
        category.setName(categoryDTO.getName());
        category.setType(categoryDTO.getType());
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    public Optional<CategoryDTO> updateCategory(Long id, CategoryDTO categoryDTO) {
        return categoryRepository.findById(id).map(existingCategory -> {
            if (categoryDTO.getName() != null) {
                // Check for duplicate name if name is being updated
                Optional<Category> duplicateCategory = categoryRepository.findByUserIdAndName(existingCategory.getUser().getId(), categoryDTO.getName());
                if (duplicateCategory.isPresent() && !duplicateCategory.get().getId().equals(id)) {
                    throw new RuntimeException("Category with this name already exists for the user");
                }
                existingCategory.setName(categoryDTO.getName());
            }
            if (categoryDTO.getType() != null) {
                existingCategory.setType(categoryDTO.getType());
            }
            Category updatedCategory = categoryRepository.save(existingCategory);
            return convertToDto(updatedCategory);
        });
    }

    public boolean deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private CategoryDTO convertToDto(Category category) {
        return new CategoryDTO(category.getId(), category.getUser().getId(), category.getName(), category.getType());
    }

    // Helper method to get Category entity from ID
    public Optional<Category> getCategoryEntityById(Long id) {
        return categoryRepository.findById(id);
    }
}

