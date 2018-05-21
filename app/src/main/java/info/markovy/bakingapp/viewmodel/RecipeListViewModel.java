package info.markovy.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import info.markovy.bakingapp.data.Recipe;
import info.markovy.bakingapp.data.Resource;
import info.markovy.bakingapp.repository.RecipesRepository;
import info.markovy.bakingapp.widget.RecipeAppWidget;

@Singleton
public class RecipeListViewModel extends AndroidViewModel {

    private MutableLiveData<Recipe> mCurrentRecipe = new MutableLiveData<Recipe>();
    private LiveData<Resource<List<Recipe>>> mRecipes;
    private RecipesRepository recipesRepository;

    @Inject
    public RecipeListViewModel(@NonNull Application application, RecipesRepository recipesRepository) {
        super(application);
        this.recipesRepository = recipesRepository;
    }

    public LiveData<Resource<List<Recipe>>> getRecipes() {
        if(mRecipes == null) {
            mRecipes = recipesRepository.loadRecipes();
        }
        return mRecipes;
    }


    public void setCurrentRecipe(@Nullable Recipe newRecipe){
        if(newRecipe != null) {
            recipesRepository.updateSaved(newRecipe.getId());
        } // otherwise keep previous one
        mCurrentRecipe.setValue(newRecipe);
    }

    public MutableLiveData<Recipe> getCurrentRecipe() {
        return mCurrentRecipe;
    }
}
