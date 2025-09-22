package org.smarteye.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.smarteye.backend.domain.Recipe;
import org.smarteye.backend.mapper.RecipeMapper;
import org.smarteye.backend.service.RecipeService;
import org.smarteye.backend.web.dto.RecipeDtos.RecipeCreateRequest;
import org.smarteye.backend.web.dto.RecipeDtos.RecipeResponse;
import org.smarteye.backend.web.dto.RecipeDtos.RecipeUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeMapper recipeMapper;

    @GetMapping
    public List<RecipeResponse> list() {
        return recipeService.list().stream().map(recipeMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public RecipeResponse get(@PathVariable Long id) {
        return recipeMapper.toResponse(recipeService.getOrThrow(id));
    }

    @PostMapping
    public ResponseEntity<RecipeResponse> create(@Valid @RequestBody RecipeCreateRequest req) {
        Recipe entity = recipeMapper.toEntity(req);
        Recipe saved = recipeService.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeMapper.toResponse(saved));
    }

    @PatchMapping("/{id}")
    public RecipeResponse update(@PathVariable Long id, @Valid @RequestBody RecipeUpdateRequest req) {
        Recipe patch = recipeMapper.toEntity(new RecipeCreateRequest(
                // для патча поля code/name могут быть null
                req.code(), req.name(), req.productCode(), req.description()
        ));
        Recipe updated = recipeService.update(id, patch);
        return recipeMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        recipeService.delete(id);
    }
}
