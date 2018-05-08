package info.markovy.bakingapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import info.markovy.bakingapp.data.Recipe;
import info.markovy.bakingapp.data.Resource;
import info.markovy.bakingapp.repository.RecipesRepository;

public class RecipeListViewModel extends ViewModel {

    public LiveData<Resource<List<Recipe>>> getRecipes() {
        return RecipesRepository.getInstance().loadRecipes();
    }

}
