
package info.markovy.bakingapp.viewmodel;


import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

import info.markovy.bakingapp.data.Ingredient;

public class IngredientViewModel implements ViewModel{

    private Ingredient ingredient;

    public IngredientViewModel(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getIngredientString() {
        return ingredient.getIngredient();
    }

    public String getQuantityType(){
        return String.format("%3.2f %s", ingredient.getQuantity(), ingredient.getMeasure());
    }
}
