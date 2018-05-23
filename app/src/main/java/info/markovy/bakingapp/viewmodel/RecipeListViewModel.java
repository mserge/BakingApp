package info.markovy.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import info.markovy.bakingapp.data.Recipe;
import info.markovy.bakingapp.data.Resource;
import info.markovy.bakingapp.data.Step;
import info.markovy.bakingapp.repository.RecipesRepository;

@Singleton
public class RecipeListViewModel extends AndroidViewModel {

    private MutableLiveData<Integer> mCurrentRecipeId = new MutableLiveData<Integer>();
    private MutableLiveData<Integer> mCurrentStepIdx = new MutableLiveData<Integer>();
    private LiveData<Resource<List<Recipe>>> mRecipes;
    private LiveData<Resource<Recipe>> mRecipe;
    private LiveData<Resource<Step>> mStep;

    private RecipesRepository recipesRepository;

    @Inject
    public RecipeListViewModel(@NonNull Application application, RecipesRepository recipesRepository) {
        super(application);
        this.recipesRepository = recipesRepository;
        mRecipe =  Transformations.switchMap(mCurrentRecipeId, id ->
                recipesRepository.getRecipeById(id)
        );
        mStep =  Transformations.map(mCurrentStepIdx, idx ->
             getStepByIdx(idx)
        );

    }

    private Resource<Step> getStepByIdx(Integer idx) {
        Resource<Recipe> value = mRecipe.getValue();
        if(value!= null) {
                return new Resource<Step>(value.status, value.data != null ? value.data.getSteps().get(idx) : null, value.message);
            }
        return null;
    }


    public LiveData<Resource<List<Recipe>>> getRecipes() {
        if(mRecipes == null) {
            mRecipes = recipesRepository.loadRecipes();
        }
        return mRecipes;
    }


    public void setCurrentRecipe(@Nullable Integer newRecipeId){
        if(newRecipeId != null) {
            recipesRepository.updateSaved(newRecipeId);
        } // otherwise keep previous one
        mCurrentRecipeId.setValue(newRecipeId);
    }

    public LiveData<Resource<Recipe>> getCurrentRecipe() {
        return mRecipe;
    }

    public void setCurrentStep(int currentStep) {
        mCurrentStepIdx.setValue(currentStep);
    }

    public void nextStep(){
        if(mCurrentStepIdx!= null && mCurrentStepIdx.getValue() != null){
            int newStepIdx = mCurrentStepIdx.getValue() + 1;
            try{ // INexOutOfBound
            if(getStepByIdx(newStepIdx) != null && getStepByIdx(newStepIdx).data != null){
                setCurrentStep(newStepIdx);
            }
            } catch (Exception ex){
                setCurrentStep(0);
            }
        }
    }

    public LiveData<Resource<Step>> getCurrentStep() {
        return mStep;
    }
}
