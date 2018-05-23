package info.markovy.bakingapp.viewmodel;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

import java.util.ArrayList;
import java.util.List;

import info.markovy.bakingapp.BuildConfig;
import info.markovy.bakingapp.Constants;
import info.markovy.bakingapp.data.Ingredient;
import info.markovy.bakingapp.data.Recipe;
import info.markovy.bakingapp.data.Step;

public class RecipeViewModel implements ViewModel {
    private final Recipe recipe;


    public RecipeViewModel( Recipe recipe) {
        this.recipe = recipe;
    }

    public Integer getId() {
        return recipe.getId();
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public String getName() {
        return recipe.getName();
    }

    public Integer getIngredients_num() {
        return  recipe.getIngredients() == null ? 0 : recipe.getIngredients().size();
    }

    public Integer getSteps_num() {
        return recipe.getSteps() == null ? 0 : recipe.getSteps().size();
    }

    public Integer getServings() {
        return recipe.getServings();
    }

    public String getImage() {
        return BuildConfig.DEBUG ? Constants.THUMBNAIL_URL : recipe.getImage();
//        return recipe.getImage();
    }

    // stream().map not available yet
    public static List<RecipeViewModel> mapFromModel(List<Recipe> source){
        List<RecipeViewModel> copy = new ArrayList<>();
        for(Recipe recipe : source){
            copy.add(new RecipeViewModel(recipe));
        }
        return copy;
    };

}
