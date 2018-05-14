
package info.markovy.bakingapp.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;


@Entity(tableName = "steps",
        primaryKeys = {"id", "recipe_id"},
        foreignKeys = @ForeignKey(entity = RecipeEntity.class, parentColumns = "id", childColumns = "recipe_id", onDelete =  CASCADE))
public class StepEntity {
    @NonNull
    public Integer id;
    public int recipe_id;
    @ColumnInfo(name = "short_desc")
    public String shortDescription;
    @ColumnInfo(name = "desc")
    public String description;
    @ColumnInfo(name = "video")
    public String videoURL;
    @ColumnInfo(name = "thumbnail")
    public String thumbnailURL;

    public StepEntity(@NonNull Integer id, int recipe_id, String shortDescription, String description, String videoURL, String thumbnailURL) {
        this.id = id;
        this.recipe_id = recipe_id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }
}
