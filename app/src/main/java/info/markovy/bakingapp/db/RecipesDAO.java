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

    @Query("SELECT * FROM ingridients where recipe_id = :recipe_id")
    List<IngredientEntity> getIngridientsByRecipeId(int recipe_id);

    @Query("SELECT * FROM ingridients JOIN saved ON  saved.recipe_id = ingridients.recipe_id ")
    List<IngredientEntity> getIngridientsSaved();


    @Query("SELECT * FROM ingridients")
    List<IngredientEntity> getAllIngriIngredients();

    @Insert(onConflict = REPLACE)
    void insertAll(RecipeEntity... recipes);

    @Insert(onConflict = REPLACE)
    void insertSaved(SavedRecipeEntity latest);

    @Insert
    void insertStepsAll(StepEntity... steps);

    @Insert(onConflict = REPLACE)
    void insertIngridientsAll(IngredientEntity... ingredientEntities);

}
