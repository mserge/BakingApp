package info.markovy.bakingapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import info.markovy.bakingapp.R;


// Main Activity
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_STEP_ID = "info.markovy.bakingapp.EXTRA_STEP_ID";
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipestep_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // handling call from widget
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
        new NavigationController(this, mTwoPane).navigateToRecipes();
    }

}
