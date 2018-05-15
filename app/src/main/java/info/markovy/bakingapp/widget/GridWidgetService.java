package info.markovy.bakingapp.widget;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import info.markovy.bakingapp.R;
import info.markovy.bakingapp.RecipeDetailActivity;
import info.markovy.bakingapp.db.IngredientEntity;
import info.markovy.bakingapp.db.RecipesDAO;
import info.markovy.bakingapp.repository.RecipesRepository;
import timber.log.Timber;

public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private RecipesDAO dao;
    Context mContext;
    List<IngredientEntity> ingredientEntities;

    public GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
        if(mContext != null) {
            //create another instance
            dao =  RecipesRepository.getRoomDAO(mContext);
            Timber.d("DAO acquired - %s", dao);
        } else
            Timber.e("No context given");
    }

    @Override
    public void onCreate() {

    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        // Get all plant info ordered by creation time
        if(dao != null){
            ingredientEntities = dao.getIngridientsSaved();
        } else {
            Timber.d("No dao set.");
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        // TODO get count
        if(ingredientEntities != null){
            Timber.d("retrieved ingridients %d", ingredientEntities.size());
            return ingredientEntities.size();
        }
        else
            Timber.d("No ingridients set.");
        return 0;

    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */
    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_grid_widget);
//        if (mCursor == null || mCursor.getCount() == 0) return null;
//        mCursor.moveToPosition(position);
//        int idIndex = mCursor.getColumnIndex(PlantContract.PlantEntry._ID);
//        int createTimeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
//        int waterTimeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
//        int plantTypeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);
//
//        long plantId = mCursor.getLong(idIndex);
//        int plantType = mCursor.getInt(plantTypeIndex);
//        long createdAt = mCursor.getLong(createTimeIndex);
//        long wateredAt = mCursor.getLong(waterTimeIndex);
//        long timeNow = System.currentTimeMillis();
//
//
//        // Update the plant image
//        int imgRes = PlantUtils.getPlantImageRes(mContext, timeNow - createdAt, timeNow - wateredAt, plantType);
//        views.setImageViewResource(R.id.widget_plant_image, imgRes);
        String ingStr = "No data for ingridient";
        if(ingredientEntities != null ){
            IngredientEntity iEnt = ingredientEntities.get(position);
            if(iEnt != null) ingStr = String.format("%10s - %2.2f %4s",
                    iEnt.ingredient, iEnt.quantity, iEnt.measure
            );
        }
        views.setTextViewText(R.id.widget_step_name, ingStr);
//        // Always hide the water drop in GridView mode
//        views.setViewVisibility(R.id.widget_water_button, View.GONE);
//
        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
        Bundle extras = new Bundle();
        extras.putInt(RecipeDetailActivity.EXTRA_STEP_ID, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_step_name, fillInIntent);

        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

