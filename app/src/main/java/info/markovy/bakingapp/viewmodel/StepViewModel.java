
package info.markovy.bakingapp.viewmodel;

import com.github.vivchar.rendererrecyclerviewadapter.ViewModel;

import info.markovy.bakingapp.data.Step;

public class StepViewModel implements ViewModel{
    private final Step step;
    private final int idx;

    public int getIdx() {
        return idx;
    }

    public StepViewModel(Step step, int idx) {
        this.step = step;
        this.idx = idx;

    }

    public Step getStep() {
        return step;
    }
}
