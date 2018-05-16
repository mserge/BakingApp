package info.markovy.bakingapp;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.vivchar.rendererrecyclerviewadapter.RendererRecyclerViewAdapter;
import com.github.vivchar.rendererrecyclerviewadapter.binder.ViewBinder;

import info.markovy.bakingapp.data.Status;
import info.markovy.bakingapp.viewmodel.RecipeListViewModel;
import info.markovy.bakingapp.viewmodel.RecipeViewModel;
import timber.log.Timber;

public class RecipeListActivity extends AppCompatActivity {

    private RendererRecyclerViewAdapter mRecyclerViewAdapter;
    private RecipeListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecyclerViewAdapter = new RendererRecyclerViewAdapter();

        mRecyclerViewAdapter.registerRenderer(new ViewBinder<>(
                R.layout.recipe_card,
                RecipeViewModel.class,
                (model, finder, payloads) -> finder
                      //  .setBackground(R.id.recipe_card_image, model.getBackground())
                        .setText(R.id.recipe_card_name, model.getName())
                        .setOnClickListener(new View.OnClickListener(){

                            @Override
                            public void onClick(View view) {
                                if(viewModel != null && model != null )
                                    viewModel.setCurrentRecipe(model.getRecipe());
                                else
                                    Timber.e("Some nullable model or view model pasesed");
                            }
                        })

        ));
        ProgressBar progress = (ProgressBar) findViewById(R.id.recipe_list_progress);
        TextView tv_error_message = (TextView) findViewById(R.id.recipe_list_error_message);
        final RecyclerView recyclerView  = findViewById(R.id.recipe_list);
        recyclerView.setAdapter(mRecyclerViewAdapter);

        viewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        viewModel.getRecipes().observe(this, recipes_resource -> {
            // update UI
            if(recipes_resource.status == Status.LOADING){
                progress.setVisibility(View.VISIBLE);
                tv_error_message.setVisibility(View.GONE);
            } else if(recipes_resource.status == Status.ERROR){
                progress.setVisibility(View.GONE);
                if(recipes_resource.message != null)
                    tv_error_message.setText(recipes_resource.message);
            }else {
                progress.setVisibility(View.GONE);
                tv_error_message.setVisibility(View.GONE);
                if(recipes_resource.data != null){
                    mRecyclerViewAdapter.setItems(RecipeViewModel.mapFromModel(recipes_resource.data));
                }
            }
        });
        viewModel.getCurrentRecipe().observe(this, recipe -> {
            if(recipe != null){
                startActivity(new Intent(this, RecipeDetailActivity.class));
            } else
                Timber.d("Null recipe set, ignoring");
        });
    }
}
