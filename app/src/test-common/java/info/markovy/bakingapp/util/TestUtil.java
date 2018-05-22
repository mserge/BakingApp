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

package info.markovy.bakingapp.util;

import java.util.ArrayList;
import java.util.List;

import info.markovy.bakingapp.data.Ingredient;
import info.markovy.bakingapp.data.Recipe;
import info.markovy.bakingapp.data.Step;

public class TestUtil {
    public static List<Recipe> createRecipes(int count) {
        List<Recipe> recipes = new ArrayList<>(count);
        for(int i = 0; i < count; i ++) {
            recipes.add(new Recipe(i,
                            "Recipe " + i,
                            createIngridients(i+1),
                            createSteps(i+1),
                            i,
                            null));
        }
        return recipes;
    }

    private static List<Step> createSteps(int count) {
        List<Step> steps = new ArrayList<>(count);
        for(int i = 0; i < count; i ++) {
            steps.add(new Step( i,
                    "Step  " + count + " " + i,
                    "Long step description Step  " + count + " " + i,
                    null,
                    null));
        }
        return steps;
    }

    private static List<Ingredient>  createIngridients(int count) {
        List<Ingredient> ingridients = new ArrayList<>(count);
        for(int i = 0; i < count; i ++) {
            ingridients.add(new Ingredient(new Double(1+i),
                    "I  " + count + "/" + i,
                    "Ingridient  " + count + " " + i));
        }
        return ingridients;
    }
//
//    public static User createUser(String login) {
//        return new User(login, null,
//                login + " name", null, null, null);
//    }
//
//    public static List<Repo> createRepos(int count, String owner, String name,
//            String description) {
//        List<Repo> repos = new ArrayList<>();
//        for(int i = 0; i < count; i ++) {
//            repos.add(createRepo(owner + i, name + i, description + i));
//        }
//        return repos;
//    }
//
//    public static Repo createRepo(String owner, String name, String description) {
//        return createRepo(Repo.UNKNOWN_ID, owner, name, description);
//    }
//
//    public static Repo createRepo(int id, String owner, String name, String description) {
//        return new Repo(id, name, owner + "/" + name,
//                description, new Repo.Owner(owner, null), 3);
//    }
//
//    public static Contributor createContributor(Repo repo, String login, int contributions) {
//        Contributor contributor = new Contributor(login, contributions, null);
//        contributor.setRepoName(repo.name);
//        contributor.setRepoOwner(repo.owner.login);
//        return contributor;
//    }
}
