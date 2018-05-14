package info.markovy.bakingapp.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

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
import info.markovy.bakingapp.db.RecipesDB;
import info.markovy.bakingapp.db.StepEntity;
import info.markovy.bakingapp.util.LiveDataCallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipesRepository {

    private static final long TIME_INVALIDATION_MILLIS = 1000 * 60 * 15; // 15 mins
    private static RecipesRepository mRepository;
    private static Context mContext;
    private static RecipesDAO mDAO;
    private BakingService bakingService;
    private AppExecutors appExecutors;
    private List<Recipe> mRecipes; // cached in memory implementation
    private long mLastUpdateMillis;

    public static RecipesRepository getInstance(){
        if(mRepository == null){
            mRepository = new RecipesRepository();
        }
        return mRepository;
    }

    public static RecipesDAO getDAO(){
        if(mDAO == null){
            RecipesDB db = Room.databaseBuilder(mContext,
                    RecipesDB.class, "database-name").build();
            mDAO = db.recipesDAO();
        }
        return mDAO;
    };

    public LiveData<Resource<List<Recipe>>> loadRecipes() {
        if(appExecutors == null){
            appExecutors = AppExecutors.getInstance();
        }
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
                getDAO().insertAll(recipeEntities.toArray(new RecipeEntity[recipeEntities.size()]));
                getDAO().insertIngridientsAll(ingredientEntities.toArray(new IngredientEntity[ingredientEntities.size()]));
                getDAO().insertStepsAll(stepEntities.toArray(new StepEntity[stepEntities.size()]));

            }

            @Override
            protected boolean shouldFetch(@Nullable List<Recipe> data) {
                return data == null || data.isEmpty() || cacheIsInvalidated();
            }

            @NonNull
            @Override
            protected LiveData<List<Recipe>> loadFromDb() {
                MutableLiveData<List<Recipe>> listMutableLiveData = new MutableLiveData<>();
                listMutableLiveData.setValue(mRecipes);
                return listMutableLiveData;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Recipe>>> createCall() {
                if(bakingService == null)
                    bakingService = new Retrofit.Builder()
                            .baseUrl(Constants.URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                            .build()
                            .create(BakingService.class);
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

    //TODO clean context?
    public void init(Context applicationContext) {
        mContext = applicationContext;
    }
}
