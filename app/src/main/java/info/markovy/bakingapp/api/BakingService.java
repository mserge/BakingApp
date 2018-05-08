package info.markovy.bakingapp.api;

import android.arch.lifecycle.LiveData;

import java.util.List;

import info.markovy.bakingapp.data.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;


public interface BakingService {
    @GET("android-baking-app-json")
    LiveData<ApiResponse<List<Recipe>>> getRecipes();

}
