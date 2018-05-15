package info.markovy.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.List;

import info.markovy.bakingapp.data.Recipe;
import info.markovy.bakingapp.data.Resource;
import info.markovy.bakingapp.repository.RecipesRepository;
import info.markovy.bakingapp.widget.RecipeAppWidget;

public class RecipeListViewModel extends AndroidViewModel {

    private MutableLiveData<Integer> mCurrentRecipe = new MutableLiveData<Integer>();
    private LiveData<Resource<List<Recipe>>> mRecipes;
    private RecipesRepository recipesRepository;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        recipesRepository = RecipesRepository.init(application.getApplicationContext());

    }

    public LiveData<Resource<List<Recipe>>> getRecipes() {
        if(mRecipes == null) {
            mRecipes = recipesRepository.loadRecipes();
        }
        return mRecipes;
    }


    public void setCurrentRecipe(Integer id){
        recipesRepository.updateSaved(id);
        mCurrentRecipe.setValue(id);

    }
}
