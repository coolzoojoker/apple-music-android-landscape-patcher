package com.apple.android.music.car;

import android.app.Activity;
import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import java.util.WeakHashMap;

/**
 * Car-only process initializer that keeps Apple Music in immersive mode.
 *
 * The BYD/DiLink system UI can overlay third-party activities at the top of
 * the display.  Applying the policy to every activity avoids fixing individual
 * layouts or hard-coding a status-bar height for one vehicle or resolution.
 */
public final class CarWindowProvider extends ContentProvider {
    private static final int LEGACY_IMMERSIVE_FLAGS =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

    private final WindowCallbacks callbacks = new WindowCallbacks();

    @Override
    public boolean onCreate() {
        Context context = getContext();
        if (context == null) {
            return false;
        }
        Context appContext = context.getApplicationContext();
        if (appContext instanceof Application) {
            ((Application) appContext).registerActivityLifecycleCallbacks(callbacks);
            return true;
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }

    private static void applyNow(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        Window window = activity.getWindow();
        if (window == null) {
            return;
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(LEGACY_IMMERSIVE_FLAGS);
        if (Build.VERSION.SDK_INT >= 30) {
            WindowInsetsController controller = decor.getWindowInsetsController();
            if (controller != null) {
                controller.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                controller.hide(WindowInsets.Type.systemBars());
            }
        }
    }

    private static final class WindowCallbacks
            implements Application.ActivityLifecycleCallbacks {
        private final WeakHashMap<Activity, ViewTreeObserver.OnWindowFocusChangeListener>
                focusListeners = new WeakHashMap<>();

        private void install(final Activity activity) {
            applyNow(activity);
            final View decor = activity.getWindow().getDecorView();
            if (!focusListeners.containsKey(activity)) {
                ViewTreeObserver.OnWindowFocusChangeListener listener =
                        new ViewTreeObserver.OnWindowFocusChangeListener() {
                            @Override
                            public void onWindowFocusChanged(boolean hasFocus) {
                                if (hasFocus) {
                                    applyNow(activity);
                                }
                            }
                        };
                decor.getViewTreeObserver().addOnWindowFocusChangeListener(listener);
                focusListeners.put(activity, listener);
            }
            decor.postDelayed(new Runnable() {
                @Override
                public void run() {
                    applyNow(activity);
                }
            }, 120L);
            decor.postDelayed(new Runnable() {
                @Override
                public void run() {
                    applyNow(activity);
                }
            }, 700L);
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle state) {
            install(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            install(activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            install(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle state) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            ViewTreeObserver.OnWindowFocusChangeListener listener = focusListeners.remove(activity);
            if (listener == null) {
                return;
            }
            View decor = activity.getWindow().getDecorView();
            ViewTreeObserver observer = decor.getViewTreeObserver();
            if (observer.isAlive()) {
                observer.removeOnWindowFocusChangeListener(listener);
            }
        }
    }
}
