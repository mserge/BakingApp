package info.markovy.bakingapp.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import javax.inject.Inject;

import info.markovy.bakingapp.BuildConfig;
import info.markovy.bakingapp.Constants;
import info.markovy.bakingapp.R;
import info.markovy.bakingapp.data.Resource;
import info.markovy.bakingapp.data.Status;
import info.markovy.bakingapp.data.Step;
import info.markovy.bakingapp.di.Injectable;
import info.markovy.bakingapp.viewmodel.RecipeListViewModel;
import timber.log.Timber;

import static com.google.android.exoplayer2.ExoPlayer.STATE_READY;


public class RecipeStepDetailFragment extends Fragment implements Injectable, Player.EventListener {
    private static final String STEP_IDX = "info.markovy.bakingapp.ui.STEP_IDX";
    private static final String SAVED_POSITION = "info.markovy.bakingapp.ui.SAVED_POSITION";
    private static final String PLAY_WHEN_READY = "PLAY_WHEN_READY";
    @Inject
    NavigationController navigationController;

    private RecipeListViewModel viewModel;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private TextView tvDetail;
    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoPlayer;
    private ImageView mImageView;
    private ProgressBar progress;
    private TextView tv_error_message;
    private long mSavedPosition;
    private Boolean mPlayWhenReady= null;

    public static RecipeStepDetailFragment newInstance(int StepIdx) {
        RecipeStepDetailFragment recipeStepDetailFragment = new RecipeStepDetailFragment();
        Bundle args = new Bundle();
        args.putInt(STEP_IDX, StepIdx);
        recipeStepDetailFragment.setArguments(args);
        return recipeStepDetailFragment;
    }

    public RecipeStepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipestep_detail, container, false);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipeListViewModel.class);
        Bundle args = getArguments();
        mSavedPosition = C.TIME_UNSET;
        if(savedInstanceState != null ){
            mSavedPosition = savedInstanceState.getLong(SAVED_POSITION, C.TIME_UNSET);
            mPlayWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY, true);
        } else  if (args != null && args.containsKey(STEP_IDX)) {
            viewModel.setCurrentStep(args.getInt(STEP_IDX));
        }
        tvDetail = rootView.findViewById(R.id.recipestep_detail);
        // Initialize the player view.
        mPlayerView = (SimpleExoPlayerView)rootView.findViewById(R.id.playerView);
        mImageView = (ImageView)rootView.findViewById(R.id.imageView);
        progress = (ProgressBar) rootView.findViewById(R.id.step_progressBar);
        tv_error_message = (TextView) rootView.findViewById(R.id.step_error_message);
        rootView.findViewById(R.id.next_step).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.nextStep();
            }
        });

//        if(viewModel.getCurrentStep() !=null){
//            showStep(viewModel.getCurrentStep().getValue());
//        }
        viewModel.getCurrentStep().removeObservers(this);
        viewModel.getCurrentStep().observe(this, step -> {
            // update UI
            showStep(step);
        });
        return rootView;
    }

    private void showStep(Resource<Step> value) {
        // TODO loading / error handling
        if(value != null){
            if(value.status == Status.LOADING){
                progress.setVisibility(View.VISIBLE);
                tv_error_message.setVisibility(View.GONE);
            } else if(value.status == Status.ERROR){
                progress.setVisibility(View.GONE);
                if(value.message != null){
                    tv_error_message.setText(value.message);
                    tv_error_message.setVisibility(View.VISIBLE);
                }
            }else {
                progress.setVisibility(View.GONE);
                tv_error_message.setVisibility(View.GONE);

                Step step = value.data;
                if(step!= null) {
                    tvDetail.setText(step.getDescription());
                    // ?
                    getActivity().setTitle("Step " + step.getShortDescription());
                    // hack to replace empty video thumbs for testing
                    if(BuildConfig.DEBUG && step.getShortDescription().contains("Recipe Introduction"))
                        step.setThumbnailURL(Constants.THUMBNAIL_URL);

                    if(step.getThumbnailURL()!=null){
                        Glide.with(this).load(step.getThumbnailURL()).into(mImageView);
                    } else {
                        mImageView.setVisibility(View.GONE);
                    }
                    if(step.getVideoURL()!=null){
                        initializePlayer(Uri.parse(step.getVideoURL()), getActivity());
                    } else {
                       mPlayerView.setVisibility(View.GONE);
                    }
                } else{
                    tvDetail.setText("No data provided");
                }
            }
        }
    }

    /**
     * Initialize ExoPlayer.
     * @param mediaUri The URI of the sample to play.
     * @param context
     */
    private void initializePlayer(Uri mediaUri, Context context) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            //  Set the ExoPlayer.EventListener to this activity
            mExoPlayer.addListener(this);
        } else {
            Timber.d("Reopen the player!!");
        }
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(context, "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    context, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            if(mSavedPosition != C.TIME_UNSET) {
                Timber.d("Position set to %d", mSavedPosition);
                mExoPlayer.seekTo(mSavedPosition);
                mSavedPosition = C.TIME_UNSET;
            }
            boolean playwhenReady = true;
            if(mPlayWhenReady != null){ // not saved
                Timber.d("Set status to %b", mPlayWhenReady);
                playwhenReady = mPlayWhenReady;
                mPlayWhenReady = null;
            }
            mExoPlayer.setPlayWhenReady(playwhenReady);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mExoPlayer != null){
            long currentPosition = mExoPlayer.getCurrentPosition();
            outState.putLong(SAVED_POSITION, currentPosition);
            boolean playWhenReady = mExoPlayer.getPlayWhenReady();
            outState.putBoolean(PLAY_WHEN_READY, playWhenReady);

        } else { // for case when we have onStop saved and cleared
           if(mSavedPosition != C.TIME_UNSET) outState.putLong(SAVED_POSITION, mSavedPosition);
           if(mPlayWhenReady != null)outState.putBoolean(PLAY_WHEN_READY, mPlayWhenReady);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if(mExoPlayer != null) {
            mExoPlayer.removeListener(this);
            mPlayerView.setPlayer(null);
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mExoPlayer!=null){
            // save when no recreation happens
            mSavedPosition = mExoPlayer.getCurrentPosition();
            mPlayWhenReady = mExoPlayer.getPlayWhenReady();
        }
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }
    @Override
    public void onDestroy() {
        //  (4): When the activity is destroyed, set the MediaSession to inactive.
        super.onDestroy();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if(playbackState == STATE_READY && playWhenReady ){
            mImageView.setVisibility(View.GONE);
            Timber.d( "We're playing!");
        } else if(playbackState == STATE_READY ){
            mImageView.setVisibility(View.GONE);
            Timber.d( "We're paused! at position %d", mExoPlayer.getCurrentPosition());
        }

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Timber.d("Got error in player");
        Timber.e(error);
        tv_error_message.setVisibility(View.VISIBLE);
        String error_message = error.getMessage();
        switch (error.type) {
            case ExoPlaybackException.TYPE_SOURCE:
                error_message =  "TYPE_SOURCE: " + error.getSourceException().getMessage();
                break;

            case ExoPlaybackException.TYPE_RENDERER:
                error_message =  "TYPE_RENDERER: " + error.getRendererException().getMessage();
                break;

            case ExoPlaybackException.TYPE_UNEXPECTED:
                error_message =  "TYPE_UNEXPECTED: " + error.getUnexpectedException().getMessage();
                break;
        }
        tv_error_message.setText("Error playing: " + error_message);
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    public void navigateToStep(Integer stepIdx) {
        Bundle args = new Bundle();
        args.putInt(STEP_IDX, stepIdx);
        this.setArguments(args);
        if(viewModel!=null)
            viewModel.setCurrentStep(args.getInt(STEP_IDX));
    }
}
