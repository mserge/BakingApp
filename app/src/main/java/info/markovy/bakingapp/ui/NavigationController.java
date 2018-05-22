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

package info.markovy.bakingapp.ui;

import android.support.v4.app.FragmentManager;

import javax.inject.Inject;

import info.markovy.bakingapp.R;

/**
 * A utility class that handles navigation in {@link MainActivity}.
 */
public class NavigationController {
    private final int containerId;
    private final int detailContainerId;
    private final FragmentManager fragmentManager;

    @Inject
    public NavigationController(MainActivity mainActivity) {
        this.containerId = R.id.frameLayout;
        this.detailContainerId = R.id.recipestep_detail_container;
        this.fragmentManager = mainActivity.getSupportFragmentManager();
    }

    public void navigateToRecipes() {
        RecipeCardsFragment recipeCardsFragment = RecipeCardsFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(containerId, recipeCardsFragment)
                .commitAllowingStateLoss();
    }

    public void navigateToDetail(Integer id) {
        RecipeDetailFragment fragment = RecipeDetailFragment.newInstance(id);
        String tag = "detail";
        fragmentManager.beginTransaction()
                .replace(containerId, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToStep(Integer stepIdx) {
        String tag = "step/" + stepIdx;
        RecipeStepDetailFragment stepDetailFragment = RecipeStepDetailFragment.newInstance(stepIdx);
        fragmentManager.beginTransaction()
                .replace(containerId, stepDetailFragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

//    // TODO add ViewModel set for Step
//        if (mTwoPane) {
//        Bundle arguments = new Bundle();
//        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment(this);
//        fragment.setArguments(arguments);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.recipestep_detail_container, fragment)
//                .commit();
//    } else {
//        Intent intent = new Intent(this, RecipeStepDetailActivity.class);
//
//        startActivity(intent);
//    }
}
