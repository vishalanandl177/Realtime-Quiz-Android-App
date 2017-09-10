package com.google.firebase.codelab.friendlychat;

import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;

import android.animation.Animator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;


public class SplashScreen extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    Button mStarter;
    private static final AccelerateInterpolator sAccelerator = new AccelerateInterpolator();
    private static final DecelerateInterpolator sDecelerator = new DecelerateInterpolator();
    private static final LinearInterpolator sLinearInterpolator = new LinearInterpolator();
    ViewGroup mContainer;
    static long SHORT_DURATION = 100;
    static long MEDIUM_DURATION = 200;
    static long REGULAR_DURATION = 300;
    static long LONG_DURATION = 500;

    private static float sDurationScale = 1f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_splash_screen);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mStarter = (Button) findViewById(R.id.button);
       /* mStarter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashScreen.this, TopicsActivity.class));
                finish();
            }
        });*/
        mContainer = (ViewGroup) findViewById(R.id.relative_one);
        // mStarter.setOnTouchListener(this);
        mStarter.setOnTouchListener(funButtonListener);
        mStarter.animate().setDuration(100);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        assert mViewPager != null;
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setPageTransformer(true, new CubeOutTransformer());

    }

    private View.OnTouchListener funButtonListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mStarter.setBackground(getResources().getDrawable(R.drawable.button_pressed));
                    mStarter.animate().scaleX(.8f).scaleY(.8f).setInterpolator(sDecelerator);
                    mStarter.removeCallbacks(mSquishRunnable);
                    mStarter.setPressed(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = event.getX();
                    float y = event.getY();
                    boolean isInside = (x > 0 && x < mStarter.getWidth() &&
                            y > 0 && y < mStarter.getHeight());
                    if (mStarter.isPressed() != isInside) {
                        mStarter.setPressed(isInside);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mStarter.isPressed()) {
                        mStarter.performClick();
                        mStarter.setPressed(false);
                    } else {
                        mStarter.animate().scaleX(1).scaleY(1).setInterpolator(sAccelerator);
                    }
                    mStarter.setBackground(getResources().getDrawable(R.drawable.button));
                    break;

            }
            return true;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mContainer.setScaleX(1);
        mContainer.setScaleY(1);
        mContainer.setAlpha(1);
        mStarter.setVisibility(View.INVISIBLE);
        mContainer.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStarter.removeCallbacks(mSquishRunnable);
    }

    private Runnable mSquishRunnable = new Runnable() {
        public void run() {
            squishyBounce(mStarter, 0,
                    mContainer.getHeight() - mStarter.getTop() - mStarter.getHeight(),
                    0, .5f, 1.5f);
        }
    };

    public void play(View view) {
        mContainer.animate().scaleX(5).scaleY(5).alpha(0).setDuration(LONG_DURATION).
                setInterpolator(sLinearInterpolator).
                withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mStarter.postOnAnimation(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(SplashScreen.this,
                                        TopicsActivity.class);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                            }
                        });
                    }
                });
        view.removeCallbacks(mSquishRunnable);
    }

    private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener =
            new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    mContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    mContainer.postDelayed(new Runnable() {
                        public void run() {
                            // Drop in the button from off the top of the screen
                            mStarter.setVisibility(View.VISIBLE);
                            mStarter.setY(-mStarter.getHeight());
                            squishyBounce(mStarter,
                                    -(mStarter.getTop() + mStarter.getHeight()),
                                    mContainer.getHeight() - mStarter.getTop() -
                                            mStarter.getHeight(),
                                    0, .5f, 1.5f);
                        }
                    }, 500);
                    return true;
                }
            };

    private void squishyBounce(final View view, final float startTY, final float bottomTY,
                               final float endTY, final float squash, final float stretch) {
        view.setPivotX(view.getWidth() / 2);
        view.setPivotY(view.getHeight());
        PropertyValuesHolder pvhTY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y,
                startTY, bottomTY);
        PropertyValuesHolder pvhSX = PropertyValuesHolder.ofFloat(View.SCALE_X, .7f);
        PropertyValuesHolder pvhSY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f);
        ObjectAnimator downAnim = ObjectAnimator.ofPropertyValuesHolder(view, pvhTY, pvhSX, pvhSY);
        downAnim.setInterpolator(sAccelerator);

        pvhTY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, bottomTY, endTY);
        pvhSX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1);
        pvhSY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1);
        ObjectAnimator upAnim = ObjectAnimator.ofPropertyValuesHolder(view, pvhTY, pvhSX, pvhSY);
        upAnim.setInterpolator(sDecelerator);

        pvhSX = PropertyValuesHolder.ofFloat(View.SCALE_X, stretch);
        pvhSY = PropertyValuesHolder.ofFloat(View.SCALE_Y, squash);
        ObjectAnimator stretchAnim = ObjectAnimator.ofPropertyValuesHolder(view, pvhSX, pvhSY);
        stretchAnim.setRepeatCount(1);
        stretchAnim.setRepeatMode(ValueAnimator.REVERSE);
        stretchAnim.setInterpolator(sDecelerator);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(downAnim, stretchAnim, upAnim);
        set.setDuration(getDuration(SHORT_DURATION));
        set.start();
        set.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                view.postDelayed(mSquishRunnable, (long) (500 + Math.random() * 2000));
            }
        });
    }

    public static long getDuration(long baseDuration) {
        return (long) (baseDuration * sDurationScale);
    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_splash_screen, container, false);

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment;
            if (position == 0) {
                fragment = new Picture_One();
            } else if (position == 1) {
                fragment = new Picture_Two();
            } else
                fragment = new Picture_Three();

            //return PlaceholderFragment.newInstance(position + 1);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

    }
}
