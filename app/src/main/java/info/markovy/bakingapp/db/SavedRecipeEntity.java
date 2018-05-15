package info.markovy.bakingapp.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;


@Entity(tableName = "saved",
        foreignKeys = @ForeignKey(entity = RecipeEntity.class, parentColumns = "id", childColumns = "recipe_id"  ))

public class SavedRecipeEntity{
       @PrimaryKey
        public int saved_id;

        public int recipe_id;

    public SavedRecipeEntity(int recipe_id) {
        this.saved_id = 0;
        this.recipe_id = recipe_id;
    }
}
