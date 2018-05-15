package info.markovy.bakingapp.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {RecipeEntity.class, StepEntity.class, IngredientEntity.class, SavedRecipeEntity.class}, version = 1)

public abstract class RecipesDB extends RoomDatabase {
    abstract public RecipesDAO recipesDAO();
}
