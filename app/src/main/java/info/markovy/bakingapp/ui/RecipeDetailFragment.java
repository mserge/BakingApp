package info.markovy.bakingapp.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewFinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import info.markovy.bakingapp.R;
import info.markovy.bakingapp.data.Ingredient;
import info.markovy.bakingapp.data.Recipe;
import info.markovy.bakingapp.data.Resource;
import info.markovy.bakingapp.data.Step;
import info.markovy.bakingapp.di.Injectable;
import info.markovy.bakingapp.viewmodel.CategoryViewModel;
import info.markovy.bakingapp.viewmodel.IngredientViewModel;
import info.markovy.bakingapp.viewmodel.RecipeListViewModel;
import info.markovy.bakingapp.viewmodel.StepViewModel;
import timber.log.Timber;


public class RecipeDetailFragment extends Fragment implements Injectable{


    private static final String RECIPE_ID = "info.markovy.bakingapp.ui.RECIPE_ID";

    private RendererRecyclerViewAdapter mRecyclerViewAdapter;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private RecipeListViewModel viewModel;
    @Inject
     NavigationController navigationController;


    public RecipeDetailFragment() {
        // Required empty public constructor
    }

    public static RecipeDetailFragment newInstance(Integer recipeId) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putInt(RECIPE_ID, recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipeListViewModel.class);
        Bundle args = getArguments();
        if (args != null && args.containsKey(RECIPE_ID)) {
            viewModel.setCurrentRecipe(args.getInt(RECIPE_ID));
        }
        viewModel.getCurrentRecipe().removeObservers(this);
        viewModel.getCurrentRecipe().observe(this, recipe -> {
            // update UI
            showRecipe(recipe);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_steplist, container, false);

        mRecyclerViewAdapter = new RendererRecyclerViewAdapter();

        registerIngridientRenderer();
        registerStepRenderer();
        registerCategoryRenderer();

        final RecyclerView recyclerView  = view.findViewById(R.id.recipestep_list);
        recyclerView.setAdapter(mRecyclerViewAdapter);

//        if(viewModel.getCurrentRecipe() !=null){
//            showRecipe(viewModel.getCurrentRecipe().getValue());
//        }

        return view;
    }

    private void showRecipe(Resource<Recipe> recipeResource) {
        if(recipeResource == null) {
            // close
            Timber.d("Recipe is null observsed");
        } else {
            // TODO logic to loader and error
            Recipe recipe = recipeResource.data;
            if(recipe == null)
                return;

            Timber.d("Load recipe list for  %s", recipe.getName());
            mRecyclerViewAdapter.setItems(mapFromAllModel(recipe));
            //
            getActivity().setTitle("Recipe " + recipe.getName());

        }
    }

    public List<ViewModel> mapFromAllModel(Recipe recipe) {
        List<ViewModel> allitems = new ArrayList<>();
        if(recipe != null){
            if(recipe.getIngredients()!=null){
                allitems.add(new CategoryViewModel(
                        "Ingridients"
                ));

                for(Ingredient ingredient : recipe.getIngredients()){
                    allitems.add(new IngredientViewModel(ingredient));
                }
            } else{
                allitems.add(new CategoryViewModel(
                        "No Ingridients"
                ));
            }

            if(recipe.getSteps()!=null){
                allitems.add(new CategoryViewModel(this.getString(R.string.detail_list_catgory_steps)));
                int idx = 0;
                for(Step step :recipe.getSteps()){
                    allitems.add(new StepViewModel(step, idx));
                    idx++;
                }
            } else {
                allitems.add(new CategoryViewModel("No steps available"));

            }


        }
        return allitems;
    }
    private void registerIngridientRenderer() {
        mRecyclerViewAdapter.registerRenderer(new ViewBinder<>(
                R.layout.ingridient_list_item,
                IngredientViewModel.class,
                (model, finder, payloads) -> finder
                        .setText(R.id.ingridient_name, model.getIngredientString())
                        .setText(R.id.ingridient_quantity_type, model.getQuantityType())
        ));
    }

    private void registerStepRenderer() {
        mRecyclerViewAdapter.registerRenderer(new ViewBinder<>(
                R.layout.step_card,
                StepViewModel.class,
                new ViewBinder.Binder<StepViewModel>() {
                    @Override
                    public void bindView(@NonNull StepViewModel model, @NonNull ViewFinder finder, @NonNull List<Object> payloads) {
                        finder
                                .find(R.id.step_card_image, (ViewProvider<ImageView>) imageView -> {
                                    Glide.with(getContext()).load(model.getStep().getThumbnailURL()).into(imageView);
                                })
                                .setText(R.id.step_card_name, model.getStep().getShortDescription())
                                .setOnClickListener((view) -> onStepClick(model));
                    }
                }
        ));
    }

    private void registerCategoryRenderer() {
        mRecyclerViewAdapter.registerRenderer(new ViewBinder<>(
                R.layout.category_list_item,
                CategoryViewModel.class,
                new ViewBinder.Binder<CategoryViewModel>() {
                    @Override
                    public void bindView(@NonNull CategoryViewModel model, @NonNull ViewFinder finder, @NonNull List<Object> payloads) {
                        finder
                                .setText(R.id.category_name, model.getString());
                    }
                }
        ));
    }
    private void onStepClick( @NonNull StepViewModel item){
        navigationController.navigateToStep(item.getIdx());
        //TODO implment model setup

    }
}
