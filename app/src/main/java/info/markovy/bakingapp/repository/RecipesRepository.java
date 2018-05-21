package info.markovy.bakingapp.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import info.markovy.bakingapp.AppExecutors;
import info.markovy.bakingapp.Constants;
import info.markovy.bakingapp.api.ApiResponse;
import info.markovy.bakingapp.api.BakingService;
import info.markovy.bakingapp.data.Ingredient;
import info.markovy.bakingapp.data.Recipe;
import info.markovy.bakingapp.data.Resource;
import info.markovy.bakingapp.data.Step;
import info.markovy.bakingapp.db.IngredientEntity;
import info.markovy.bakingapp.db.RecipeEntity;
import info.markovy.bakingapp.db.RecipesDAO;
import info.markovy.bakingapp.db.SavedRecipeEntity;
import info.markovy.bakingapp.db.StepEntity;
import info.markovy.bakingapp.util.LiveDataCallAdapterFactory;
import info.markovy.bakingapp.widget.RecipeAppWidget;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Singleton
public class RecipesRepository {

    private static final long TIME_INVALIDATION_MILLIS = 1000 * 60 * 15; // 15 mins
    private Context mContext;
    private RecipesDAO mDAO;
    private BakingService bakingService;
    private AppExecutors appExecutors;

    private List<Recipe> mRecipes; // cached in memory implementation

    @Inject
    public RecipesRepository(Application app, RecipesDAO mDAO, BakingService bakingService, AppExecutors appExecutors) {
        this.mContext = app.getApplicationContext();
        this.mDAO = mDAO;
        this.bakingService = bakingService;
        this.appExecutors = appExecutors;
    }

    private long mLastUpdateMillis;


    public LiveData<Resource<List<Recipe>>> loadRecipes() {

        return new NetworkBoundResource<List<Recipe>, List<Recipe>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<Recipe> items) {
                mRecipes = items;
                updateCacheTime();
                List<RecipeEntity> recipeEntities = new ArrayList<>();
                List<IngredientEntity> ingredientEntities = new ArrayList<>();
                List<StepEntity> stepEntities = new ArrayList<>();
                for(Recipe item : items){
                    recipeEntities.add(new RecipeEntity(item.getId(),item.getName(), item.getServings(), item.getImage()));
                    if(item.getIngredients()!=null){
                        for (Ingredient ingredient: item.getIngredients()){
                            ingredientEntities.add(new IngredientEntity(item.getId(),
                                    ingredient.getQuantity(), ingredient.getMeasure(), ingredient.getIngredient()));
                        }
                    }
                    if(item.getSteps()!=null){
                        for (Step step: item.getSteps()){
                            stepEntities.add(new StepEntity( step.getId(), item.getId(),
                                    step.getShortDescription(), step.getDescription(), step.getVideoURL(), step.getThumbnailURL()));
                        }
                    }
                }
                mDAO.insertAll(recipeEntities.toArray(new RecipeEntity[recipeEntities.size()]));
                mDAO.insertIngridientsAll(ingredientEntities.toArray(new IngredientEntity[ingredientEntities.size()]));
                mDAO.insertStepsAll(stepEntities.toArray(new StepEntity[stepEntities.size()]));
                RecipeAppWidget.invokeUpdate(mContext);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Recipe> data) {
                return data == null || data.isEmpty() || cacheIsInvalidated();
            }

            @NonNull
            @Override
            protected LiveData<List<Recipe>> loadFromDb() {

                MutableLiveData<List<Recipe>> listMutableLiveData = new MutableLiveData<>();
                if(mRecipes != null && ! cacheIsInvalidated()){
                    listMutableLiveData.setValue(mRecipes);
                } else
                    appExecutors.diskIO().execute(
                        () ->{
                                List<Recipe> joinedList = new ArrayList<>();
                                List<RecipeEntity> all = mDAO.getAll();
                                List<StepEntity> allSteps = mDAO.getAllSteps();
                                List<IngredientEntity> allIngredients = mDAO.getAllIngredients();
                                if(all != null){
                                    for(RecipeEntity re : all){

                                        List<Ingredient> ingredients = new ArrayList<>();
                                        if(allIngredients != null){
                                            for(IngredientEntity ie: allIngredients ){
                                                if(ie.recipe_id == re.id){
                                                    ingredients.add(new Ingredient(ie.quantity, ie.measure, ie.ingredient));
                                                }
                                            }

                                        }
                                        List<Step> steps = new ArrayList<>();
                                        if(allSteps != null){
                                            for(StepEntity se: allSteps ){
                                                if(se.recipe_id == re.id){
                                                    steps.add(new Step(se.id, se.shortDescription, se.description, se.videoURL, se.thumbnailURL));
                                                }
                                            }

                                        }
                                        // copy
                                        joinedList.add( new Recipe(re.id, re.name, ingredients, steps, re.servings, re.image));
                                    }
                                }
                                mRecipes = joinedList; updateCacheTime();
                                listMutableLiveData.postValue(joinedList);

                        }
                );

                return listMutableLiveData;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Recipe>>> createCall() {
                return bakingService.getRecipes();
            }

            @Override
            protected void onFetchFailed() {

            }
        }.asLiveData();
    }

    private void updateCacheTime() {
        mLastUpdateMillis = System.currentTimeMillis();
    }

    private boolean cacheIsInvalidated() {
        return mLastUpdateMillis > System.currentTimeMillis() + TIME_INVALIDATION_MILLIS;
    }

    // normally call will be initiated on the main thread
    public void updateSaved(int recipe_id){
        appExecutors.diskIO().execute(() -> {
            Timber.d("Save recipe: %d", recipe_id);
            mDAO.insertSaved(new SavedRecipeEntity(recipe_id));
            RecipeAppWidget.invokeUpdate(mContext);
        });
    }

}
