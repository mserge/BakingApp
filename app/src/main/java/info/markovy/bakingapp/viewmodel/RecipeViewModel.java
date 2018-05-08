package info.markovy.bakingapp.viewmodel;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

import java.util.ArrayList;
import java.util.List;

import info.markovy.bakingapp.data.Recipe;

public class RecipeViewModel implements ViewModel {
    private Integer id;
    private String name;
    private Integer ingredients_num;
    private Integer steps_num;
    private Integer servings;
    private String image;

    public RecipeViewModel(Integer id, String name, Integer ingredients_num, Integer steps_num, Integer servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients_num = ingredients_num;
        this.steps_num = steps_num;
        this.servings = servings;
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getIngredients_num() {
        return ingredients_num;
    }

    public Integer getSteps_num() {
        return steps_num;
    }

    public Integer getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }

    // stream().map not available yet
    public static List<RecipeViewModel> mapFromModel(List<Recipe> source){
        List<RecipeViewModel> copy = new ArrayList<>();
        for(Recipe recipe : source){
            copy.add(new RecipeViewModel(recipe.getId(), recipe.getName(),
                    recipe.getIngredients() == null ? 0 : recipe.getIngredients().size(),
                    recipe.getSteps() == null ? 0 : recipe.getSteps().size(),
                    recipe.getServings(),
                    recipe.getImage()
            ));
        }
        return copy;
    };
}
