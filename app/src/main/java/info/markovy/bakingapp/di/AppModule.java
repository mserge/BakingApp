/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.markovy.bakingapp.di;

import android.app.Application;
import android.arch.persistence.room.Room;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import info.markovy.bakingapp.Constants;
import info.markovy.bakingapp.api.BakingService;
import info.markovy.bakingapp.db.RecipesDAO;
import info.markovy.bakingapp.db.RecipesDB;
import info.markovy.bakingapp.util.LiveDataCallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {
    @Singleton @Provides
    BakingService provideBakingService() {
        return new Retrofit.Builder()
                .baseUrl(Constants.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(BakingService.class);
    }

    @Singleton @Provides
    RecipesDB provideDb(Application app) {
        return Room.databaseBuilder(app, RecipesDB.class,"database-name").build();
    }

    @Singleton @Provides
    RecipesDAO provideUserDao(RecipesDB db) {
        return db.recipesDAO();
    }

}
