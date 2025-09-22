package org.smarteye.backend.service;

import lombok.RequiredArgsConstructor;
import org.smarteye.backend.common.exception.NotFoundException;
import org.smarteye.backend.domain.Recipe;
import org.smarteye.backend.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @Transactional(readOnly = true)
    public Recipe getOrThrow(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recipe not found: id=" + id));
    }

    @Transactional(readOnly = true)
    public Recipe getByCodeOrThrow(String code) {
        return recipeRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Recipe not found: code=" + code));
    }

    public Recipe create(Recipe r) {
        return recipeRepository.save(r);
    }

    public Recipe update(Long id, Recipe patch) {
        Recipe r = getOrThrow(id);
        if (patch.getCode() != null) r.setCode(patch.getCode());
        if (patch.getName() != null) r.setName(patch.getName());
        if (patch.getProductCode() != null) r.setProductCode(patch.getProductCode());
        if (patch.getDescription() != null) r.setDescription(patch.getDescription());
        return r;
    }

    public void delete(Long id) {
        recipeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Recipe> list() {
        return recipeRepository.findAll();
    }
}
