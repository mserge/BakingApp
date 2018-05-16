
package info.markovy.bakingapp.viewmodel;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

import info.markovy.bakingapp.data.Step;

public class StepViewModel implements ViewModel{
    private final Step step;

    public StepViewModel(Step step) {
        this.step = step;
    }

    public Step getStep() {
        return step;
    }
}
