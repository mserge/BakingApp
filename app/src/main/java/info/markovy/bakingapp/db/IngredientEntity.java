
package info.markovy.bakingapp.db;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "ingrinients",
    foreignKeys = @ForeignKey(entity = RecipeEntity.class, parentColumns = "id", childColumns = "recipe_id",  onDelete =  CASCADE))
public class IngredientEntity {
    @PrimaryKey(autoGenerate = true)
    public int _id;

    public int recipe_id;
    public double quantity;

    public String measure;

    public String ingredient;

    public IngredientEntity(int recipe_id, double quantity, String measure, String ingredient) {
        this.recipe_id = recipe_id;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }
}
