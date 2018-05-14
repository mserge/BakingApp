package info.markovy.bakingapp.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface RecipesDAO {
    @Query("SELECT * FROM recipes")
     List<RecipeEntity> getAll();

    @Query("SELECT * FROM steps")
    List<StepEntity> getAllSteps();

    @Query("SELECT * FROM steps where recipe_id = :recipe_id")
    List<StepEntity> getStepsByRecipeId(int recipe_id);

    @Query("SELECT * FROM ingrinients")
    List<IngredientEntity> getAllIngriIngredients();

    @Insert(onConflict = REPLACE)
    void insertAll(RecipeEntity... recipes);

    @Insert
    void insertStepsAll(StepEntity... steps);

    @Insert(onConflict = REPLACE)
    void insertIngridientsAll(IngredientEntity... ingredientEntities);

}
