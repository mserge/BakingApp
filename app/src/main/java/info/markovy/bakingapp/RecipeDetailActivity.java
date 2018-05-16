package info.markovy.bakingapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewFinder;

import info.markovy.bakingapp.data.DummyContent;
import info.markovy.bakingapp.data.Ingredient;
import info.markovy.bakingapp.data.Recipe;
import info.markovy.bakingapp.data.Status;
import info.markovy.bakingapp.data.Step;
import info.markovy.bakingapp.viewmodel.CategoryViewModel;
import info.markovy.bakingapp.viewmodel.IngredientViewModel;
import info.markovy.bakingapp.viewmodel.RecipeListViewModel;
import info.markovy.bakingapp.viewmodel.RecipeViewModel;
import info.markovy.bakingapp.viewmodel.StepViewModel;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of RecipeSteps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeStepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_STEP_ID = "info.markovy.bakingapp.EXTRA_STEP_ID";
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RendererRecyclerViewAdapter mRecyclerViewAdapter;
    private RecipeListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipestep_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        int stepExtra = getIntent().getIntExtra(EXTRA_STEP_ID, -1);
        // can be passed from widget
        Toast.makeText(this, "Called with id " + stepExtra, Toast.LENGTH_LONG).show();


        if (findViewById(R.id.recipestep_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        mRecyclerViewAdapter = new RendererRecyclerViewAdapter();

        registerIngridientRenderer();
        registerStepRenderer();

        final RecyclerView recyclerView  = findViewById(R.id.recipestep_list);
        recyclerView.setAdapter(mRecyclerViewAdapter);

        viewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        if(viewModel.getCurrentRecipe() !=null){
            showRecipe(viewModel.getCurrentRecipe().getValue());
        }
        viewModel.getCurrentRecipe().observe(this, recipe -> {

            // update UI
            showRecipe(recipe);

       });
    }

    private void showRecipe(Recipe recipe) {
        if(recipe == null) {
            // close
            Timber.d("Recipe is null observsed");
        } else {
            Timber.d("Load recipe list for  %s", recipe.getName());
                mRecyclerViewAdapter.setItems(mapFromAllModel(recipe));
            //
        }
    }

    public  List<ViewModel> mapFromAllModel(Recipe recipe) {
        List<ViewModel> allitems = new ArrayList<>();
        if(recipe != null){
            if(recipe.getSteps()!=null){
                allitems.add(new CategoryViewModel(this.getString(R.string.detail_list_catgory_steps)));
                for(Step step :recipe.getSteps()){
                    allitems.add(new StepViewModel(step));
                }
            } else {
                allitems.add(new CategoryViewModel("No steps available"));

            }
            if(recipe.getIngredients()!=null){
                for(Ingredient ingredient : recipe.getIngredients()){
                    allitems.add(new IngredientViewModel(ingredient));
                }
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
                                .setText(R.id.step_card_name, model.getStep().getShortDescription())
                                .setOnClickListener((view) -> RecipeDetailActivity.this.onStepClick(model));
                    }
                }
        ));
    }

    private void onStepClick( @NonNull StepViewModel item){

        // TODO add ViewModel set for Step
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipestep_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, RecipeStepDetailActivity.class);

            startActivity(intent);
        }
    }

}
