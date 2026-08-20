package com.apple.android.music.player.fragment;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public final class TVLyricsLayout {
    private static final String TAG = "TVLandscapeState";
    private static final int ID_ARTWORK_IMAGE = 2131362074;
    private static final int ID_ARTWORK_CONTAINER = 2131362069;
    private static final int ID_FULLPLAYER_SONG_IMAGE = 2131362813;
    private static final int ID_CONTROLS = 2131362490;
    private static final int ID_CONTROLS_TAP_TARGET = 2131362492;
    private static final int ID_CURRENT_PLAYER_ITEM = 2131362510;
    private static final int ID_ENTER_FULL_SCREEN = 2131362651;
    private static final int ID_LIST_FAVORITE_ICON = 2131363578;
    private static final int ID_LIST_LEFT_ICON = 2131363583;
    private static final int ID_LOADING_PROGRESS = 2131363624;
    private static final int ID_LYRICS_MAIN_CONTENT = 2131363636;
    private static final int ID_LYRICS_THUMBNAIL_CONTAINER = 2131363647;
    private static final int ID_MEDIA_ROUTE_BUTTON = 2131363684;
    private static final int ID_MINI_PLAYER_SUBTITLE = 2131363722;
    private static final int ID_MINI_PLAYER_TITLE = 2131363723;
    private static final int ID_NO_LYRICS_AVAILABLE = 2131363890;
    private static final int ID_NEXT_FAST_FORWARD = 2131363885;
    private static final int ID_PLAY_PAUSE = 2131363995;
    private static final int ID_HOME_CONTROLS = 2131364003;
    private static final int ID_PLAYER_ACTION_BUTTONS = 2131364000;
    private static final int ID_NATIVE_HOME_PLAYER = 2131364002;
    private static final int ID_PLAYER_FRAGMENTS_HOST = 2131364004;
    private static final int ID_PLAYER_LYRICS = 2131364006;
    private static final int ID_PLAYER_QUEUE = 2131364008;
    private static final int ID_PLAYER_SHEET_CONTAINER = 2131364010;
    private static final int ID_PREVIOUS_REWIND = 2131364050;
    private static final int ID_QUEUE_MAIN_CONTENT = 2131364100;
    private static final int ID_QUEUE_THUMBNAIL_CONTAINER = 2131364102;
    private static final int ID_FRAGMENT_CONTAINER_VIEW_TAG = 2131362805;
    private static final int ID_RECYCLER_GRADIENTS = 2131364134;
    private static final int ID_SEEK_BAR_CONTROLS = 2131364236;
    private static final int ID_STICKY_HEADER_CLIP = 2131364416;
    private static final int ID_TAP_TARGET_GUIDELINE = 2131364498;
    private static final int ID_TEXT_METADATA_CONTAINER = 2131364527;
    private static final int ID_THUMBNAIL = 2131364543;
    private static final int ID_TRANSLATIONS_BUBBLE_TIP = 2131364613;
    private static final int ID_TRANSLATIONS_BUTTON = 2131364614;
    private static final int ID_VA_BUBBLE_TIP = 2131364645;
    private static final int ID_VIDEO_SURFACE = 2131364657;
    private static final int ID_VOCAL_CONTROL = 2131364685;
    private static final int ID_TITLE = 2131364545;
    private static final int ID_SUBTITLE = 2131364437;
    private static final int ICON_FULLSCREEN_ENTER = 2131231828;
    private static final int TEXT_FULLSCREEN_ENTER = 2131952827;
    private static final int PARENT = 0;
    private static final int UNSET = -1;
    private static final int MODE_HOME = 0;
    private static final int MODE_LYRICS = 1;
    private static final int MODE_QUEUE = 2;
    private static final String[] CONSTRAINT_FIELDS = {"e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "s", "t", "u", "v"};
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private static Controller active;
    private static int generation;
    private static boolean earlyPrepareActive;
    private static final LinkedHashMap<String, Bitmap> ARTWORK_MEMORY_CACHE = new LinkedHashMap<String, Bitmap>(16, 0.75f, true);

    private TVLyricsLayout() {
    }

    private static synchronized Bitmap getCachedArtwork(String str) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        Bitmap bitmap = ARTWORK_MEMORY_CACHE.get(str);
        return bitmap != null && !bitmap.isRecycled() ? bitmap : null;
    }

    private static synchronized void putCachedArtwork(String str, Bitmap bitmap) {
        if (str == null || str.trim().length() == 0 || bitmap == null || bitmap.isRecycled()) {
            return;
        }
        Bitmap bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        if (bitmapCopy == null) {
            return;
        }
        Bitmap bitmapPut = ARTWORK_MEMORY_CACHE.put(str, bitmapCopy);
        if (bitmapPut != null && bitmapPut != bitmapCopy && !bitmapPut.isRecycled()) {
            bitmapPut.recycle();
        }
        while (ARTWORK_MEMORY_CACHE.size() > 16) {
            Iterator<Map.Entry<String, Bitmap>> it = ARTWORK_MEMORY_CACHE.entrySet().iterator();
            if (!it.hasNext()) {
                break;
            }
            Bitmap value = it.next().getValue();
            it.remove();
            if (value != null && !value.isRecycled()) {
                value.recycle();
            }
        }
        Log.i(TAG, "ARTWORK_CACHE stored=true entries=" + ARTWORK_MEMORY_CACHE.size() + " key=" + str);
    }

    public static void onPlayerState(final View view, int i) {
        Log.i(TAG, "native sheet state=" + i);
        if (i == 2 && isLandscape(view) && (active == null || !active.expanded)) {
            earlyPrepareActive = true;
            final int i2 = generation + MODE_LYRICS;
            generation = i2;
            MAIN.post(new Runnable() {
                @Override
                public void run() {
                    if (i2 == TVLyricsLayout.generation) {
                        TVLyricsLayout.prepareSideSession(view, 0);
                    }
                }
            });
        } else if (i == 3 && isLandscape(view)) {
            if (earlyPrepareActive) {
                return;
            }
            final int i2 = generation + MODE_LYRICS;
            generation = i2;
            MAIN.postDelayed(new Runnable() {
                @Override // java.lang.Runnable
                public void run() {
                    if (i2 == TVLyricsLayout.generation) {
                        TVLyricsLayout.prepareSideSession(view, 0);
                    }
                }
            }, 60L);
        } else if (i == 4 || i == 5) {
            earlyPrepareActive = false;
            generation += MODE_LYRICS;
            restoreInitialSideLayout("sheet-state-" + i);
        }
    }

    public static void install(final View view) {
        final Controller controller;
        if (!isLandscape(view) || (controller = active) == null || !controller.expanded) {
            return;
        }
        final boolean z = view.findViewById(ID_QUEUE_MAIN_CONTENT) != null;
        final boolean z2 = view.findViewById(ID_LYRICS_MAIN_CONTENT) != null;
        if (!z && !z2) {
            return;
        }
        if ((controller.mode == MODE_LYRICS && z2) || (controller.mode == MODE_QUEUE && z)) {
            view.post(new Runnable() {
                @Override // java.lang.Runnable
                public void run() {
                    Controller controller2 = TVLyricsLayout.active;
                    if (controller2 != controller) {
                        return;
                    }
                    if (controller2.mode == TVLyricsLayout.MODE_LYRICS && z2) {
                        controller2.applyLyricsLayout(view);
                    } else if (controller2.mode == TVLyricsLayout.MODE_QUEUE && z) {
                        controller2.applyQueueLayout(view);
                    }
                }
            });
        } else {
            view.postDelayed(new Runnable() {
                @Override // java.lang.Runnable
                public void run() {
                    Controller controller2 = TVLyricsLayout.active;
                    if (controller2 == controller && controller2.mode == 0) {
                        TVLyricsLayout.releaseStaleNativePanelTransitionLocks(view);
                        TVLyricsLayout.invokeNativeHomeMode(view);
                    }
                }
            }, 60L);
        }
    }

    public static boolean beforeNativeModeClick(View view, int i) {
        View viewFindVisible;
        Controller controller = active;
        if (controller == null || !controller.expanded) {
            return false;
        }
        if (i != 4 && i != 5) {
            return false;
        }
        // Native video state can change between two 400 ms watcher ticks.
        // Refresh it synchronously before accepting a lyrics click so a music
        // video can never reopen a retained lyrics Fragment from the previous
        // audio item.
        controller.refreshSharedVideoPresentation(false);
        int i2 = i == 5 ? MODE_QUEUE : MODE_LYRICS;
        if (i2 == MODE_LYRICS && (((viewFindVisible = controller.findVisible(ID_PLAYER_LYRICS)) != null && !viewFindVisible.isEnabled()) || (view != null && !view.isEnabled()))) {
            controller.enterHome("lyrics-disabled-click");
            return true;
        }
        if (controller.mode == i2) {
            controller.enterHome(i2 == MODE_QUEUE ? "queue-button-toggle" : "lyrics-button-toggle");
            return true;
        }
        if (controller.mode == 0 && controller.showRetainedPanelFromHome(i2)) {
            return true;
        }
        if (controller.mode == 0 && controller.homeRetainedMode != 0 && controller.panelRoot != null) {
            controller.handoffOutgoingRoot = controller.panelRoot;
            controller.handoffUntil = System.currentTimeMillis() + 2400;
            controller.homeRetainedMode = 0;
            controller.beginSharedPlayerTransition();
            controller.mode = i2;
            controller.startLyricsAvailabilityWatch();
            releaseStaleNativePanelTransitionLocks(view);
            if (invokeNativePanelMode(view, i2 == MODE_QUEUE)) {
                controller.reapplyRetainedPanelWhenShown(i2);
                Log.i(TAG, "MODE_SWITCH virtual-home-direct target=" + (i2 == MODE_QUEUE ? "QUEUE" : "LYRICS"));
                return true;
            }
            controller.mode = 0;
            Log.w(TAG, "MODE_SWITCH virtual-home-direct invocation=false");
            return true;
        }
        if (controller.mode != 0 && controller.panelRoot != null) {
            controller.switchPanelAnimated(view, i2);
            return true;
        }
        controller.beginSharedPlayerTransition();
        controller.mode = i2;
        controller.startLyricsAvailabilityWatch();

        // In Windows-HOME the real controls already live in the shared layer.
        // Letting the original click callback continue from there is unstable:
        // Apple's listener resolves its Fragment host through the old parent.
        // Use the same native mode entry point that the retained-panel handoff
        // already proved reliable, then consume this detached-view click.
        releaseStaleNativePanelTransitionLocks(view);
        if (invokeNativePanelMode(view, i2 == MODE_QUEUE)) {
            controller.reapplyRetainedPanelWhenShown(i2);
            Log.i(TAG, "MODE_SWITCH target=" + (i2 == MODE_QUEUE ? "QUEUE" : "LYRICS") + " via-direct-native-entry=true");
            return true;
        }
        controller.mode = 0;
        controller.setSharedModeVisual(0);
        Log.w(TAG, "MODE_SWITCH direct-native-entry=false");
        return true;
    }

    public static void exitFullscreen(View view) {
        Controller controller = active;
        if (controller != null && controller.expanded) {
            controller.beginNativePanelExit(view);
        }
    }

    public static void destroy(View view) {
        Controller controller = active;
        if (controller != null && controller.panelRoot == view) {
            controller.panelExitGeneration += MODE_LYRICS;
            controller.restorePanelLayout("fragment-destroy");
            controller.panelRoot = null;
            controller.mode = 0;
        }
    }

    public static int scaleArtworkDimension(View view, int i) {
        return i;
    }

    private static boolean isLandscape(View view) {
        return view != null && view.getResources().getConfiguration().orientation == MODE_QUEUE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void prepareSideSession(final View view, final int i) {
        if (view == null || !view.isAttachedToWindow()) {
            return;
        }
        View rootView = view.getRootView();
        View viewFindViewById = rootView.findViewById(ID_PLAYER_SHEET_CONTAINER);
        if (viewFindViewById == null || viewFindViewById.getWidth() <= 0) {
            Log.w(TAG, "HOME start postponed: native sheet missing");
            if (i < 30) {
                final int i2 = generation;
                MAIN.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (i2 == TVLyricsLayout.generation) {
                            TVLyricsLayout.prepareSideSession(view, i + MODE_LYRICS);
                        }
                    }
                }, 24L);
            } else {
                earlyPrepareActive = false;
            }
            return;
        }
        Controller controller = active;
        if (controller != null && controller.sheet == viewFindViewById && !controller.restoring) {
            return;
        }
        View viewFindTopVisiblePlayerPage = findTopVisiblePlayerPage(rootView);
        if (viewFindTopVisiblePlayerPage == null || viewFindTopVisiblePlayerPage.getId() != ID_NATIVE_HOME_PLAYER) {
            View viewFindViewById2 = rootView.findViewById(ID_NATIVE_HOME_PLAYER);
            if (i >= 1 && viewFindViewById2 != null && viewFindViewById2.isAttachedToWindow()) {
                restoreInitialSideLayout("replace-session-fallback");
                Controller controller2 = new Controller(rootView, viewFindViewById);
                active = controller2;
                earlyPrepareActive = false;
                controller2.captureInitialSideLayout();
                controller2.captureInitial(viewFindViewById2);
                controller2.expandInPlace();
                controller2.installFullscreenChromeIsolation();
                viewFindViewById2.animate().cancel();
                viewFindViewById2.clearAnimation();
                viewFindViewById2.setVisibility(0);
                viewFindViewById2.setAlpha(1.0f);
                viewFindViewById2.setTranslationX(0.0f);
                viewFindViewById2.setTranslationY(0.0f);
                if (viewFindTopVisiblePlayerPage != null && viewFindTopVisiblePlayerPage != viewFindViewById2) {
                    controller2.panelRoot = viewFindTopVisiblePlayerPage;
                    if (viewFindTopVisiblePlayerPage.findViewById(ID_QUEUE_MAIN_CONTENT) != null) {
                        controller2.homeRetainedMode = MODE_QUEUE;
                    } else if (viewFindTopVisiblePlayerPage.findViewById(ID_LYRICS_MAIN_CONTENT) != null) {
                        controller2.homeRetainedMode = MODE_LYRICS;
                    }
                    controller2.hideInactivePanel(viewFindTopVisiblePlayerPage);
                }
                controller2.mode = 0;
                controller2.expanded = true;
                controller2.installInputShield();
                controller2.startLyricsAvailabilityWatch();
                controller2.captureNativeHomeUnderlay(null);
                viewFindViewById2.setAlpha(0.0f);
                controller2.scheduleWindowsHomeCommit("fresh-session-fallback");
                viewFindViewById2.requestLayout();
                Log.i(TAG, "STATE=HOME trigger=fresh-session-fallback retained=" + controller2.homeRetainedMode + " top=" + describe(viewFindTopVisiblePlayerPage));
                Log.i(TAG, "AUTO_FULLSCREEN_SWITCH invoked=true fallback=true");
                return;
            }
            if (i >= 30) {
                Log.w(TAG, "HOME normalization failed; fullscreen not applied");
                return;
            }
            if (viewFindTopVisiblePlayerPage != null) {
                releaseStaleNativePanelTransitionLocks(viewFindTopVisiblePlayerPage);
                if (!invokeNativeHomeMode(viewFindTopVisiblePlayerPage)) {
                    clickNativeArtwork(viewFindTopVisiblePlayerPage);
                }
                Log.i(TAG, "HOME_NORMALIZE requested=true top=" + describe(viewFindTopVisiblePlayerPage) + " attempt=" + i);
            }
            final int i2 = generation;
            MAIN.postDelayed(new Runnable() {
                @Override // java.lang.Runnable
                public void run() {
                    if (i2 == TVLyricsLayout.generation) {
                        TVLyricsLayout.prepareSideSession(view, i + TVLyricsLayout.MODE_LYRICS);
                    }
                }
            }, 120L);
            return;
        }
        restoreInitialSideLayout("replace-session");
        Controller controller3 = new Controller(rootView, viewFindViewById);
        active = controller3;
        earlyPrepareActive = false;
        controller3.captureInitialSideLayout();
        controller3.mode = 0;
        controller3.expandFromTrigger("auto-side-open");
        Log.i(TAG, "AUTO_FULLSCREEN_SWITCH invoked=true");
    }

    private static void restoreInitialSideLayout(String str) {
        Controller controller = active;
        if (controller == null || controller.restoring) {
            return;
        }
        controller.restoring = true;
        controller.expanded = false;
        controller.stopCallbacks();
        controller.restoreFullscreenChromeIsolation();
        controller.removeAddedViews();
        controller.restorePanelLayout("session-restore");
        controller.restoreSharedPlayerImmediate("session-restore");
        controller.restoreNativeHomeUnderlays();
        controller.concealRetainedPanelsForCollapsedSheet();
        boolean zRestoreSnapshots = restoreSnapshots(controller.initialSnapshots);
        controller.requestLayouts(controller.initialSnapshots);
        controller.ensureMiniPlayerAfterClose();
        Log.i(TAG, "RESTORE_INITIAL reason=" + str + " match=" + zRestoreSnapshots + " count=" + controller.initialSnapshots.size());
        controller.initialSnapshots.clear();
        controller.restoring = false;
        if (active == controller) {
            active = null;
        }
    }

    private static boolean clickNativeArtwork(View view) {
        if (view == null) {
            return false;
        }
        View viewFindVisible = findVisible(view, ID_ARTWORK_IMAGE);
        if (viewFindVisible == null) {
            viewFindVisible = view.findViewById(ID_ARTWORK_IMAGE);
        }
        if (viewFindVisible != null && viewFindVisible.isEnabled()) {
            Log.i(TAG, "native artwork HOME action");
            return viewFindVisible.performClick();
        }
        View viewFindViewById = view.findViewById(ID_CURRENT_PLAYER_ITEM);
        return viewFindViewById != null && viewFindViewById.isEnabled() && viewFindViewById.performClick();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean invokeNativePanelMode(View view, boolean z) {
        Context baseContext;
        try {
            Context context = view.getContext();
            Method methodFindNoArgMethod = null;
            Context context2 = null;
            while (context != null) {
                methodFindNoArgMethod = findNoArgMethod(context.getClass(), "f1");
                if (methodFindNoArgMethod != null) {
                    context2 = context;
                    break;
                }
                if (!(context instanceof ContextWrapper) || (baseContext = ((ContextWrapper) context).getBaseContext()) == context) {
                    break;
                }
                context = baseContext;
            }
            if (methodFindNoArgMethod == null || context2 == null) {
                return false;
            }
            methodFindNoArgMethod.setAccessible(true);
            Object objInvoke = methodFindNoArgMethod.invoke(context2, new Object[0]);
            if (objInvoke == null) {
                return false;
            }
            Enum enumValueOf = Enum.valueOf((Class) Class.forName("com.apple.android.music.player.fragment.w0$n", true, objInvoke.getClass().getClassLoader()), z ? "QUEUE" : "LYRICS");
            Method method = null;
            Method[] methods = objInvoke.getClass().getMethods();
            int length = methods.length;
            for (int i = 0; i < length; i += MODE_LYRICS) {
                Method method2 = methods[i];
                if (method2.getName().equals("F1") && method2.getParameterTypes().length == MODE_QUEUE) {
                    method = method2;
                    break;
                }
            }
            if (method == null) {
                return false;
            }
            Bundle bundle = new Bundle();
            Field fieldFindField = findField(Class.forName("com.apple.android.music.utils.F", true, objInvoke.getClass().getClassLoader()), z ? "o" : "n");
            if (fieldFindField != null) {
                fieldFindField.setAccessible(true);
                Object obj = fieldFindField.get(null);
                if (obj instanceof String) {
                    bundle.putBoolean((String) obj, true);
                }
            }
            method.invoke(objInvoke, enumValueOf, bundle);
            return true;
        } catch (Throwable th) {
            Log.w(TAG, "native panel invocation failed: " + th.getClass().getSimpleName());
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean invokeNativeHomeMode(View view) {
        Context baseContext;
        try {
            Context context = view.getContext();
            Method methodFindNoArgMethod = null;
            Context context2 = null;
            while (context != null) {
                methodFindNoArgMethod = findNoArgMethod(context.getClass(), "f1");
                if (methodFindNoArgMethod != null) {
                    context2 = context;
                    break;
                }
                if (!(context instanceof ContextWrapper) || (baseContext = ((ContextWrapper) context).getBaseContext()) == context) {
                    break;
                }
                context = baseContext;
            }
            if (methodFindNoArgMethod == null || context2 == null) {
                return false;
            }
            methodFindNoArgMethod.setAccessible(true);
            Object objInvoke = methodFindNoArgMethod.invoke(context2, new Object[0]);
            if (objInvoke == null) {
                return false;
            }
            Enum enumValueOf = Enum.valueOf((Class) Class.forName("com.apple.android.music.player.fragment.w0$n", true, objInvoke.getClass().getClassLoader()), "SONG");
            Method method = null;
            Method[] methods = objInvoke.getClass().getMethods();
            int length = methods.length;
            for (int i = 0; i < length; i += MODE_LYRICS) {
                Method method2 = methods[i];
                if (method2.getName().equals("F1") && method2.getParameterTypes().length == MODE_QUEUE) {
                    method = method2;
                    break;
                }
            }
            if (method == null) {
                return false;
            }
            method.invoke(objInvoke, enumValueOf, new Bundle());
            return true;
        } catch (Throwable th) {
            Log.w(TAG, "HOME_NORMALIZE invocation failed=" + th.getClass().getSimpleName());
            return false;
        }
    }

    private static Object findNativePlayerController(View view) {
        Context baseContext;
        if (view == null) {
            return null;
        }
        try {
            Context context = view.getContext();
            while (context != null) {
                Method methodFindNoArgMethod = findNoArgMethod(context.getClass(), "f1");
                if (methodFindNoArgMethod != null) {
                    methodFindNoArgMethod.setAccessible(true);
                    return methodFindNoArgMethod.invoke(context, new Object[0]);
                }
                if (!(context instanceof ContextWrapper) || (baseContext = ((ContextWrapper) context).getBaseContext()) == context) {
                    break;
                }
                context = baseContext;
            }
            return null;
        } catch (Throwable th) {
            Log.w(TAG, "native player controller lookup failed=" + th.getClass().getSimpleName());
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void collapseNativePlayerSheet(View view) {
        try {
            Object objFindNativePlayerController = findNativePlayerController(view);
            if (objFindNativePlayerController == null) {
                Log.w(TAG, "DRAG_DISMISS controller-missing=true");
                return;
            }
            Field fieldFindField = findField(objFindNativePlayerController.getClass(), "c");
            if (fieldFindField == null) {
                Log.w(TAG, "DRAG_DISMISS behavior-field-missing=true");
                return;
            }
            fieldFindField.setAccessible(true);
            Object obj = fieldFindField.get(objFindNativePlayerController);
            Method method = null;
            if (obj != null) {
                Method[] methods = obj.getClass().getMethods();
                int length = methods.length;
                for (int i = 0; i < length; i += MODE_LYRICS) {
                    Method method2 = methods[i];
                    if (method2.getName().equals("G") && method2.getParameterTypes().length == MODE_LYRICS && method2.getParameterTypes()[0] == Integer.TYPE) {
                        method = method2;
                        break;
                    }
                }
            }
            if (method == null) {
                Log.w(TAG, "DRAG_DISMISS set-state-missing=true");
            } else {
                method.invoke(obj, 4);
                Log.i(TAG, "DRAG_DISMISS native-collapse-invoked=true");
            }
        } catch (Throwable th) {
            Log.w(TAG, "DRAG_DISMISS native-collapse-failed=" + th.getClass().getSimpleName());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Method findNoArgMethod(Class<?> cls, String str) {
        Class<?> superclass = cls;
        while (true) {
            Class<?> cls2 = superclass;
            if (cls2 != null && cls2 != Object.class) {
                try {
                    return cls2.getDeclaredMethod(str, new Class[0]);
                } catch (NoSuchMethodException e) {
                    superclass = cls2.getSuperclass();
                }
            } else {
                return null;
            }
        }
    }

    private static Field findField(Class<?> cls, String str) {
        Class<?> superclass = cls;
        while (true) {
            Class<?> cls2 = superclass;
            if (cls2 != null && cls2 != Object.class) {
                try {
                    return cls2.getDeclaredField(str);
                } catch (NoSuchFieldException e) {
                    superclass = cls2.getSuperclass();
                }
            } else {
                return null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void releaseStaleNativePanelTransitionLocks(View view) {
        Context baseContext;
        if (view == null) {
            return;
        }
        try {
            Context context = view.getContext();
            Object objInvoke = null;
            while (context != null) {
                Method methodFindNoArgMethod = findNoArgMethod(context.getClass(), "f1");
                if (methodFindNoArgMethod != null) {
                    methodFindNoArgMethod.setAccessible(true);
                    objInvoke = methodFindNoArgMethod.invoke(context, new Object[0]);
                    break;
                } else {
                    if (!(context instanceof ContextWrapper) || (baseContext = ((ContextWrapper) context).getBaseContext()) == context) {
                        break;
                    }
                    context = baseContext;
                }
            }
            if (objInvoke == null) {
                return;
            }
            Field fieldFindField = findField(objInvoke.getClass(), "h");
            Field fieldFindField2 = findField(objInvoke.getClass(), "j");
            boolean z = false;
            if (fieldFindField != null) {
                fieldFindField.setAccessible(true);
                z = false | fieldFindField.getBoolean(objInvoke);
                fieldFindField.setBoolean(objInvoke, false);
            }
            if (fieldFindField2 != null) {
                fieldFindField2.setAccessible(true);
                z |= fieldFindField2.getBoolean(objInvoke);
                fieldFindField2.setBoolean(objInvoke, false);
            }
            Log.i(TAG, "NATIVE_PANEL_LOCKS released=" + z);
        } catch (Throwable th) {
            Log.w(TAG, "NATIVE_PANEL_LOCKS failed=" + th.getClass().getSimpleName());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static View findPanelRoot(View view) {
        View view2 = view;
        while (true) {
            View view3 = view2;
            if (view3 != null) {
                if (view3.findViewById(ID_CURRENT_PLAYER_ITEM) != null && (view3.findViewById(ID_LYRICS_MAIN_CONTENT) != null || view3.findViewById(ID_QUEUE_MAIN_CONTENT) != null)) {
                    return view3;
                }
                Object parent = view3.getParent();
                view2 = parent instanceof View ? (View) parent : null;
            } else {
                return view;
            }
        }
    }

    private static View findTopVisiblePlayerPage(View view) {
        View viewFindViewById = view == null ? null : view.findViewById(ID_PLAYER_FRAGMENTS_HOST);
        if (!(viewFindViewById instanceof ViewGroup)) {
            return null;
        }
        ViewGroup viewGroup = (ViewGroup) viewFindViewById;
        for (int childCount = viewGroup.getChildCount() - MODE_LYRICS; childCount >= 0; childCount += UNSET) {
            View childAt = viewGroup.getChildAt(childCount);
            if (childAt.isAttachedToWindow() && childAt.getVisibility() == 0 && childAt.getAlpha() > 0.0f && childAt.getWidth() > 0 && childAt.getHeight() > 0) {
                return childAt;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static View findVisible(View view, int i) {
        if (view == null) {
            return null;
        }
        if (view.getId() == i && view.getVisibility() == 0 && view.isAttachedToWindow() && view.getWidth() > 0 && view.getHeight() > 0) {
            return view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int childCount = viewGroup.getChildCount() - MODE_LYRICS; childCount >= 0; childCount += UNSET) {
                View viewFindVisible = findVisible(viewGroup.getChildAt(childCount), i);
                if (viewFindVisible != null) {
                    return viewFindVisible;
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static View findAttached(View view, int i) {
        if (view == null) {
            return null;
        }
        if (view.getId() == i && view.isAttachedToWindow()) {
            return view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int childCount = viewGroup.getChildCount() - MODE_LYRICS; childCount >= 0; childCount += UNSET) {
                View viewFindAttached = findAttached(viewGroup.getChildAt(childCount), i);
                if (viewFindAttached != null) {
                    return viewFindAttached;
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: loaded from: classes.jar:com/apple/android/music/player/fragment/TVLyricsLayout$Controller.class */
    private static final class Controller {
        final View windowRoot;
        final View sheet;
        View outerContainer;
        View panelRoot;
        View activePanelRoot;
        View activeCurrent;
        View activeRecycler;
        View activeMainContent;
        View activeControls;
        View activeArtwork;
        View activeVideo;
        View activeThumbnail;
        View nativeHomeRoot;
        FrameLayout sharedLayer;
        View sharedArtworkContainer;
        View sharedArtworkCard;
        View sharedArtworkSourceCard;
        ImageView sharedArtworkProxy;
        TextureView sharedVideoSurface;
        View sharedNativeArtwork;
        View sharedNativeFullscreenButton;
        Drawable sharedArtworkProxySource;
        Bitmap sharedArtworkSourceBitmap;
        Bitmap sharedArtworkProxyBitmap;
        String sharedArtworkProxyKey;
        String sharedVideoKey;
        boolean sharedVideoActive;
        boolean sharedVideoPresented;
        boolean sharedVideoFrameReady;
        float sharedVideoAspect;
        TextureView sharedVideoConfirmedSurface;
        boolean sharedArtworkPinned;
        boolean panelNativeVideoShown;
        boolean videoHomeBootstrapPending;
        int videoHomeBootstrapGeneration;
        View retainedQueueVideoRoot;
        View retainedQueueVideoArtwork;
        TextureView retainedQueueVideoSurface;
        float retainedQueueVideoPanelX;
        float retainedQueueVideoPanelY;
        float retainedQueueVideoPanelScaleX = 1.0f;
        float retainedQueueVideoPanelScaleY = 1.0f;
        boolean retainedQueueVideoAtHome;
        boolean retainedQueueVideoReturningToPanel;
        int retainedQueueVideoGeneration;
        String pendingVideoGeometryKey;
        String pendingVideoArtworkKey;
        Bitmap pendingVideoArtworkBitmap;
        String pendingPanelKeepAliveLogKey;
        String pendingVideoSurfaceBaselineKey;
        TextureView pendingVideoSurfaceBaselineView;
        long pendingVideoSurfaceBaseline;
        boolean pendingVideoSurfaceBaselineCaptured;
        long pendingVideoSurfaceBaselineAt;
        final IdentityHashMap<TextureView, Long> pendingVideoSurfaceBaselines = new IdentityHashMap<>();
        final IdentityHashMap<TextureView, Long> pendingVideoSurfaceBaselineTimes = new IdentityHashMap<>();
        long lastTextureDiagnosticAt;
        int currentVideoBackgroundColor;
        View sharedTitle;
        View sharedSubtitle;
        View sharedFavorite;
        View sharedMore;
        View sharedControls;
        View sharedLyricsButton;
        View sharedQueueButton;
        View sharedRouteButton;
        DragDismissOverlay dragDismissOverlay;
        View fullscreenBackdrop;
        View openingCurtain;
        HiddenPanelState navigationChromeState;
        HiddenPanelState miniPlayerChromeState;
        boolean sharedPlayerActive;
        boolean sharedPlayerAnimating;
        boolean sharedAtPanelGeometry;
        int sharedAnimationGeneration;
        float dismissProgress;
        float panelDismissStartY;
        float panelDismissStartAlpha;
        boolean closeChromePreviewVisible;
        boolean closingInProgress;
        int homeCommitGeneration;
        View queueLyricsOverlay;
        View activeModeHomeOverlay;
        InputShield inputShield;
        ImageButton manualToggle;
        boolean expanded;
        boolean restoring;
        int watchGeneration;
        int panelExitGeneration;
        int panelShowGeneration;
        int persistenceGeneration;
        View handoffOutgoingRoot;
        long handoffUntil;
        Drawable retainedArtwork;
        boolean panelTransitioning;
        int pendingPanelMode;
        View pendingPanelView;
        long panelAnimationUntil;
        int queueDiagnosticCount;
        String lastArtworkRefreshSignature;
        String lastArtworkMatrixSignature;
        String lastVideoAspectSignature;
        String lastVideoFitSignature;
        String confirmedLyricsAvailableKey;
        Object activatedQueueFragment;
        final ArrayList<ViewSnapshot> initialSnapshots = new ArrayList<>();
        final ArrayList<ViewSnapshot> panelSnapshots = new ArrayList<>();
        final ArrayList<ViewSnapshot> sharedSnapshots = new ArrayList<>();
        final ArrayList<SharedItem> sharedItems = new ArrayList<>();
        final ArrayList<View> addedViews = new ArrayList<>();
        final ArrayList<View> persistentViews = new ArrayList<>();
        final ArrayList<HiddenPanelState> hiddenPanels = new ArrayList<>();
        final ArrayList<HiddenPanelState> nativeHomeUnderlays = new ArrayList<>();
        final IdentityHashMap<View, Boolean> initialSeen = new IdentityHashMap<>();
        final IdentityHashMap<View, Boolean> panelSeen = new IdentityHashMap<>();
        final IdentityHashMap<View, Boolean> sharedSeen = new IdentityHashMap<>();
        final IdentityHashMap<View, Boolean> persistentSeen = new IdentityHashMap<>();
        final IdentityHashMap<View, Drawable> videoBackgroundOriginals = new IdentityHashMap<>();
        int mode = 0;
        int panelMode = 0;
        int homeRetainedMode = 0;

        Controller(View view, View view2) {
            this.windowRoot = view;
            this.sheet = view2;
            Object parent = view2.getParent();
            this.outerContainer = parent instanceof View ? (View) parent : view2;
        }

        void captureInitialSideLayout() {
            captureInitial(this.outerContainer);
            captureInitial(this.sheet);
            Log.i(TVLyricsLayout.TAG, "SNAPSHOT_INITIAL outer=" + TVLyricsLayout.describe(this.outerContainer) + " sheet=" + TVLyricsLayout.describe(this.sheet));
        }

        void expandInPlace() {
            if (this.outerContainer != null) {
                ViewGroup.LayoutParams layoutParamsCloneLayoutParams = TVLyricsLayout.cloneLayoutParams(this.outerContainer.getLayoutParams());
                if (layoutParamsCloneLayoutParams == null) {
                    layoutParamsCloneLayoutParams = this.outerContainer.getLayoutParams();
                }
                layoutParamsCloneLayoutParams.width = 0;
                layoutParamsCloneLayoutParams.height = 0;
                TVLyricsLayout.zeroMargins(layoutParamsCloneLayoutParams);
                TVLyricsLayout.clearConstraints(layoutParamsCloneLayoutParams);
                TVLyricsLayout.setInt(layoutParamsCloneLayoutParams, "t", 0);
                TVLyricsLayout.setInt(layoutParamsCloneLayoutParams, "v", 0);
                TVLyricsLayout.setInt(layoutParamsCloneLayoutParams, "i", 0);
                TVLyricsLayout.setInt(layoutParamsCloneLayoutParams, "l", 0);
                TVLyricsLayout.setInt(layoutParamsCloneLayoutParams, "L", 0);
                TVLyricsLayout.setInt(layoutParamsCloneLayoutParams, "N", 0);
                TVLyricsLayout.setObject(layoutParamsCloneLayoutParams, "G", null);
                TVLyricsLayout.setBoolean(layoutParamsCloneLayoutParams, "W", false);
                this.outerContainer.setLayoutParams(layoutParamsCloneLayoutParams);
                this.outerContainer.setVisibility(0);
                this.outerContainer.setAlpha(1.0f);
                this.outerContainer.requestLayout();
            }
        }

        void addManualFullscreenToggle() {
            if (!(this.sheet instanceof ViewGroup) || this.expanded || this.restoring) {
                return;
            }
            if (this.manualToggle != null && this.manualToggle.getParent() == this.sheet) {
                return;
            }
            ImageButton imageButton = new ImageButton(this.sheet.getContext());
            this.manualToggle = imageButton;
            imageButton.setImageResource(TVLyricsLayout.ICON_FULLSCREEN_ENTER);
            imageButton.setContentDescription(this.sheet.getResources().getText(TVLyricsLayout.TEXT_FULLSCREEN_ENTER));
            imageButton.setColorFilter(TVLyricsLayout.UNSET);
            imageButton.setBackgroundColor(0);
            imageButton.setPadding(TVLyricsLayout.dp(imageButton, 8), TVLyricsLayout.dp(imageButton, 8), TVLyricsLayout.dp(imageButton, 8), TVLyricsLayout.dp(imageButton, 8));
            imageButton.setClickable(true);
            imageButton.setFocusable(true);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (TVLyricsLayout.active == Controller.this && !Controller.this.expanded && !Controller.this.restoring) {
                        Controller.this.expandFromTrigger("manual-switch");
                    }
                }
            });
            int iDp = TVLyricsLayout.dp(imageButton, 44);
            ((ViewGroup) this.sheet).addView(imageButton, new ViewGroup.LayoutParams(iDp, iDp));
            imageButton.setX(Math.max(0, this.sheet.getWidth() - TVLyricsLayout.dp(imageButton, 56)));
            imageButton.setY(TVLyricsLayout.dp(imageButton, 8));
            imageButton.setElevation(TVLyricsLayout.dp(imageButton, 24));
            imageButton.setTranslationZ(TVLyricsLayout.dp(imageButton, 24));
            imageButton.bringToFront();
            this.addedViews.add(imageButton);
            Log.i(TVLyricsLayout.TAG, "MANUAL_FULLSCREEN_SWITCH ready=true");
        }

        void expandFromTrigger(String str) {
            if (this.expanded || this.restoring) {
                return;
            }
            if (this.manualToggle != null) {
                ViewParent parent = this.manualToggle.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(this.manualToggle);
                }
                this.manualToggle = null;
            }
            installOpeningCurtain();
            expandInPlace();
            installFullscreenChromeIsolation();
            installInputShield();
            captureNativeHomeUnderlay(null);
            this.nativeHomeRoot = findNativeHomePage();
            if (this.nativeHomeRoot != null) {
                this.nativeHomeRoot.animate().cancel();
                this.nativeHomeRoot.setAlpha(0.0f);
            }
            this.mode = 0;
            this.expanded = true;
            startLyricsAvailabilityWatch();
            scheduleWindowsHomeCommit(str);
            Log.i(TVLyricsLayout.TAG, "STATE=HOME trigger=" + str + " parent-preserved=true");
        }

        void scheduleWindowsHomeCommit(final String str) {
            final int i = this.homeCommitGeneration + MODE_LYRICS;
            this.homeCommitGeneration = i;
            this.sheet.post(new Runnable() {
                int attempts;

                @Override
                public void run() {
                    if (TVLyricsLayout.active != Controller.this || !Controller.this.expanded || Controller.this.restoring || Controller.this.mode != 0 || i != Controller.this.homeCommitGeneration) {
                        return;
                    }
                    Controller.this.beginSharedPlayerTransition();
                    if (Controller.this.sharedPlayerActive) {
                        Controller.this.commitSharedHomeGeometry();
                        Controller.this.animateWindowsHomeEntrance();
                        Log.i(TVLyricsLayout.TAG, "WINDOWS_HOME committed-on-expand=true reason=" + str + " attempts=" + this.attempts);
                        return;
                    }
                    int i2 = this.attempts + MODE_LYRICS;
                    this.attempts = i2;
                    if (i2 < 45) {
                        TVLyricsLayout.MAIN.postDelayed(this, 24L);
                    } else {
                        Log.w(TVLyricsLayout.TAG, "WINDOWS_HOME commit-timeout=true reason=" + str);
                    }
                }
            });
        }

        void animateWindowsHomeEntrance() {
            if (this.sharedLayer == null) {
                return;
            }
            this.sharedLayer.animate().cancel();
            this.sharedLayer.setAlpha(1.0f);
            this.sharedLayer.setScaleX(1.0f);
            this.sharedLayer.setScaleY(1.0f);
            this.sharedLayer.setTranslationY(0.0f);
            int i = this.sheet.getResources().getDisplayMetrics().widthPixels;
            int i2 = this.sheet.getResources().getDisplayMetrics().heightPixels;
            float f = i / 1920.0f;
            float f2 = i2 / 1080.0f;
            float f3 = Math.round(82.0f * Math.min(f, f2));
            float f4 = Math.round(1312.0f * f);
            float f5 = Math.round(983.0f * f2);
            float f6 = Math.round(1410.0f * f);
            float f7 = Math.round(1005.0f * f2);
            PathInterpolator pathInterpolator = new PathInterpolator(0.22f, 1.0f, 0.36f, 1.0f);
            for (SharedItem sharedItem : this.sharedItems) {
                sharedItem.view.animate().cancel();
                sharedItem.view.setPivotX(0.0f);
                sharedItem.view.setPivotY(0.0f);
                if (sharedItem.view == this.sharedArtworkContainer && this.sharedArtworkCard != null) {
                    int i3 = Math.max(MODE_LYRICS, this.sharedArtworkCard.getWidth());
                    float f8 = f3 / i3;
                    float f9 = Math.max(0.0f, (sharedItem.view.getWidth() - i3) / 2.0f);
                    float f10 = Math.max(0.0f, (sharedItem.view.getHeight() - this.sharedArtworkCard.getHeight()) / 2.0f);
                    sharedItem.view.setX(sharedItemX(sharedItem, f4 - (f9 * f8)));
                    sharedItem.view.setY(sharedItemY(sharedItem, f5 - (f10 * f8)));
                    sharedItem.view.setScaleX(f8);
                    sharedItem.view.setScaleY(f8);
                    sharedItem.view.setAlpha(1.0f);
                } else if (sharedItem.view == this.sharedTitle || sharedItem.view == this.sharedSubtitle) {
                    sharedItem.view.setX(f6);
                    sharedItem.view.setY(f7 + (sharedItem.view == this.sharedSubtitle ? Math.round(26.0f * f2) : 0));
                    sharedItem.view.setScaleX(0.72f);
                    sharedItem.view.setScaleY(0.72f);
                    sharedItem.view.setAlpha(0.0f);
                } else {
                    sharedItem.view.setTranslationY(TVLyricsLayout.dp(sharedItem.view, 54));
                    sharedItem.view.setScaleX(0.92f);
                    sharedItem.view.setScaleY(0.92f);
                    sharedItem.view.setAlpha(0.0f);
                }
                long j = sharedItem.view == this.sharedArtworkContainer ? 440L : ((sharedItem.view == this.sharedTitle || sharedItem.view == this.sharedSubtitle) ? 380L : 300L);
                sharedItem.view.animate().x(sharedItem.homeX).y(sharedItem.homeY).scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setInterpolator(pathInterpolator).setDuration(j).start();
            }
            if (this.sharedArtworkPinned && hasVideoBootstrapEvidence()) {
                ensureQueueVideoSurfaceForHome();
            } else {
                dismissOpeningCurtain();
            }
        }

        boolean hasVideoBootstrapEvidence() {
            if (this.sharedVideoActive && currentArtworkKey().equals(this.sharedVideoKey)) {
                return true;
            }
            if (currentArtworkKey().equals(this.pendingVideoArtworkKey)
                    && usableBitmapAspect(this.pendingVideoArtworkBitmap) > 1.30f) {
                return true;
            }
            if (currentArtworkKey().equals(this.sharedArtworkProxyKey)
                    && usableBitmapAspect(this.sharedArtworkProxyBitmap) > 1.30f) {
                return true;
            }
            return usableBitmapAspect(this.sharedArtworkSourceBitmap) > 1.30f;
        }

        void installOpeningCurtain() {
            if (this.openingCurtain != null || !(this.windowRoot instanceof ViewGroup)) {
                return;
            }
            ImageView imageView = new ImageView(this.windowRoot.getContext());
            this.openingCurtain = imageView;
            imageView.setBackgroundColor(0xff181818);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setColorFilter(0x99000000, PorterDuff.Mode.SRC_OVER);
            Bitmap bitmapFindLargestArtworkBitmap = findLargestArtworkBitmap(this.windowRoot, null);
            if (bitmapFindLargestArtworkBitmap != null && !bitmapFindLargestArtworkBitmap.isRecycled()) {
                imageView.setImageBitmap(bitmapFindLargestArtworkBitmap);
            }
            if (android.os.Build.VERSION.SDK_INT >= 31) {
                imageView.setRenderEffect(RenderEffect.createBlurEffect(TVLyricsLayout.dp(imageView, 42), TVLyricsLayout.dp(imageView, 42), Shader.TileMode.CLAMP));
            }
            imageView.setClickable(true);
            imageView.setFocusable(false);
            ((ViewGroup) this.windowRoot).addView(imageView, new ViewGroup.LayoutParams(TVLyricsLayout.UNSET, TVLyricsLayout.UNSET));
            imageView.setElevation(TVLyricsLayout.dp(imageView, 1490));
            imageView.setTranslationZ(TVLyricsLayout.dp(imageView, 1490));
            imageView.bringToFront();
            Log.i(TVLyricsLayout.TAG, "OPENING_CURTAIN installed=true");
        }

        void dismissOpeningCurtain() {
            final View view = this.openingCurtain;
            if (view == null) {
                return;
            }
            view.animate().cancel();
            view.animate().alpha(0.0f).setInterpolator(new PathInterpolator(0.22f, 1.0f, 0.36f, 1.0f)).setDuration(300L).withEndAction(new Runnable() {
                @Override
                public void run() {
                    if (view.getParent() instanceof ViewGroup) {
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                    if (Controller.this.openingCurtain == view) {
                        Controller.this.openingCurtain = null;
                    }
                }
            }).start();
        }

        void removeOpeningCurtainImmediate() {
            if (this.openingCurtain != null) {
                this.openingCurtain.animate().cancel();
                if (this.openingCurtain.getParent() instanceof ViewGroup) {
                    ((ViewGroup) this.openingCurtain.getParent()).removeView(this.openingCurtain);
                }
                this.openingCurtain = null;
            }
        }

        void installFullscreenChromeIsolation() {
            this.closeChromePreviewVisible = false;
            ViewParent parent = this.outerContainer != null ? this.outerContainer.getParent() : null;
            ViewGroup backdropHost = this.outerContainer instanceof ViewGroup
                    ? (ViewGroup) this.outerContainer
                    : (parent instanceof ViewGroup ? (ViewGroup) parent : null);
            if (this.fullscreenBackdrop == null && backdropHost != null) {
                ImageView view = new ImageView(this.outerContainer.getContext());
                this.fullscreenBackdrop = view;
                view.setBackgroundColor(0xff181818);
                view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                view.setAlpha(1.0f);
                view.setColorFilter(0x99000000, PorterDuff.Mode.SRC_OVER);
                Bitmap bitmapFindLargestArtworkBitmap = findLargestArtworkBitmap(this.windowRoot, null);
                if (bitmapFindLargestArtworkBitmap != null && !bitmapFindLargestArtworkBitmap.isRecycled()) {
                    view.setImageBitmap(bitmapFindLargestArtworkBitmap);
                }
                if (android.os.Build.VERSION.SDK_INT >= 31) {
                    view.setRenderEffect(RenderEffect.createBlurEffect(TVLyricsLayout.dp(view, 42), TVLyricsLayout.dp(view, 42), Shader.TileMode.CLAMP));
                }
                view.setClickable(true);
                view.setFocusable(false);
                view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
                // The outer player container has its own opaque black background.
                // A sibling inserted behind it can never be seen. Install the
                // artwork backdrop as child 0 instead: above that black canvas,
                // below every native player page and the shared controls.
                backdropHost.addView(view, 0, new ViewGroup.LayoutParams(TVLyricsLayout.UNSET, TVLyricsLayout.UNSET));
                Log.i(TVLyricsLayout.TAG, "FULLSCREEN_BACKDROP host=outer-child-zero");
            }
            int identifier = this.sheet.getResources().getIdentifier("bottom_navigation_tabs_frame", "id", this.sheet.getContext().getPackageName());
            View viewFindViewById = identifier != 0 ? this.windowRoot.findViewById(identifier) : null;
            if (viewFindViewById != null && this.navigationChromeState == null) {
                this.navigationChromeState = new HiddenPanelState(viewFindViewById);
                viewFindViewById.animate().cancel();
                viewFindViewById.setAlpha(0.0f);
                // The full-screen backdrop now owns the close composition, so
                // the real navigation chrome must not remain hit-testable or
                // participate in the expanded player layout.
                viewFindViewById.setVisibility(View.INVISIBLE);
            }
            int identifier2 = this.sheet.getResources().getIdentifier("mini_player", "id", this.sheet.getContext().getPackageName());
            View viewFindViewById2 = identifier2 != 0 ? this.windowRoot.findViewById(identifier2) : null;
            if (viewFindViewById2 != null && this.miniPlayerChromeState == null) {
                this.miniPlayerChromeState = new HiddenPanelState(viewFindViewById2);
                viewFindViewById2.animate().cancel();
                viewFindViewById2.setAlpha(0.0f);
                viewFindViewById2.setVisibility(View.INVISIBLE);
            }
            Log.i(TVLyricsLayout.TAG, "FULLSCREEN_CHROME isolated=true nav=" + TVLyricsLayout.describe(viewFindViewById) + " mini=" + TVLyricsLayout.describe(viewFindViewById2));
        }

        void restoreFullscreenChromeIsolation() {
            this.closeChromePreviewVisible = false;
            restoreVideoBackgrounds();
            removeOpeningCurtainImmediate();
            if (this.navigationChromeState != null) {
                this.navigationChromeState.restore();
                this.navigationChromeState = null;
            }
            if (this.miniPlayerChromeState != null) {
                this.miniPlayerChromeState.restore();
                this.miniPlayerChromeState = null;
            }
            final View view = this.fullscreenBackdrop;
            if (view != null && view.getParent() instanceof ViewGroup) {
                view.animate().cancel();
                ((ViewGroup) view.getParent()).removeView(view);
            }
            this.fullscreenBackdrop = null;
            this.closingInProgress = false;
            Log.i(TVLyricsLayout.TAG, "FULLSCREEN_CHROME restored=true");
        }

        void prepareFullscreenChromeForClose() {
            // Native collapse starts at the same time as the remaining shared
            // artwork motion. Do not insert a separate backdrop phase here.
            this.closingInProgress = true;
            if (this.sharedLayer != null) {
                this.sharedLayer.bringToFront();
            }
            this.closeChromePreviewVisible = false;
            Log.i(TVLyricsLayout.TAG, "FULLSCREEN_CHROME close-concurrent-native=true");
        }

        void applyCloseChromeProgress(float f) {
            float f2 = Math.max(0.0f, Math.min(1.0f, f));
            if (this.fullscreenBackdrop != null) {
                this.fullscreenBackdrop.animate().cancel();
                this.fullscreenBackdrop.setAlpha(1.0f - f2);
            }
            HiddenPanelState hiddenPanelState = this.navigationChromeState;
            HiddenPanelState hiddenPanelState2 = this.miniPlayerChromeState;
            if (hiddenPanelState != null) {
                hiddenPanelState.view.animate().cancel();
                hiddenPanelState.view.setVisibility(View.VISIBLE);
                hiddenPanelState.view.setAlpha(f2);
            }
            if (hiddenPanelState2 != null) {
                hiddenPanelState2.view.animate().cancel();
                hiddenPanelState2.view.setVisibility(View.VISIBLE);
                hiddenPanelState2.view.setAlpha(f2);
            }
            this.closeChromePreviewVisible = f2 > 0.0f;
        }

        void animateCloseChromeProgress(boolean z, long j) {
            float f = z ? 1.0f : 0.0f;
            PathInterpolator pathInterpolator = new PathInterpolator(0.2f, 0.0f, 0.0f, 1.0f);
            if (this.fullscreenBackdrop != null) {
                this.fullscreenBackdrop.animate().cancel();
                this.fullscreenBackdrop.animate().alpha(1.0f - f).setInterpolator(pathInterpolator).setDuration(j).start();
            }
            HiddenPanelState hiddenPanelState = this.navigationChromeState;
            HiddenPanelState hiddenPanelState2 = this.miniPlayerChromeState;
            if (hiddenPanelState != null) {
                hiddenPanelState.view.setVisibility(View.VISIBLE);
                hiddenPanelState.view.animate().cancel();
                hiddenPanelState.view.animate().alpha(f).setInterpolator(pathInterpolator).setDuration(j).start();
            }
            if (hiddenPanelState2 != null) {
                hiddenPanelState2.view.setVisibility(View.VISIBLE);
                hiddenPanelState2.view.animate().cancel();
                hiddenPanelState2.view.animate().alpha(f).setInterpolator(pathInterpolator).setDuration(j).start();
            }
            this.closeChromePreviewVisible = z;
        }

        void setCloseChromePreview(boolean z) {
            if (z == this.closeChromePreviewVisible) {
                return;
            }
            HiddenPanelState hiddenPanelState = this.navigationChromeState;
            HiddenPanelState hiddenPanelState2 = this.miniPlayerChromeState;
            if (z) {
                if (hiddenPanelState != null) {
                    hiddenPanelState.view.animate().cancel();
                    hiddenPanelState.view.setVisibility(View.VISIBLE);
                    hiddenPanelState.view.setAlpha(1.0f);
                    hiddenPanelState.view.requestLayout();
                }
                if (hiddenPanelState2 != null) {
                    hiddenPanelState2.view.animate().cancel();
                    hiddenPanelState2.view.setVisibility(View.VISIBLE);
                    hiddenPanelState2.view.setAlpha(1.0f);
                    hiddenPanelState2.view.setTranslationX(0.0f);
                    hiddenPanelState2.view.setTranslationY(0.0f);
                    hiddenPanelState2.view.requestLayout();
                }
                int identifier = this.windowRoot.getResources().getIdentifier("mini_player_content", "id", this.windowRoot.getContext().getPackageName());
                View viewFindViewById = identifier != 0 ? this.windowRoot.findViewById(identifier) : null;
                if (viewFindViewById != null) {
                    viewFindViewById.animate().cancel();
                    viewFindViewById.setVisibility(View.VISIBLE);
                    viewFindViewById.setAlpha(1.0f);
                    viewFindViewById.setTranslationX(0.0f);
                    viewFindViewById.setTranslationY(0.0f);
                    viewFindViewById.requestLayout();
                }
            } else {
                if (hiddenPanelState != null) {
                    hiddenPanelState.view.animate().cancel();
                    hiddenPanelState.view.setAlpha(0.0f);
                    hiddenPanelState.view.setVisibility(View.INVISIBLE);
                }
                if (hiddenPanelState2 != null) {
                    hiddenPanelState2.view.animate().cancel();
                    hiddenPanelState2.view.setAlpha(0.0f);
                    hiddenPanelState2.view.setVisibility(View.INVISIBLE);
                }
            }
            this.closeChromePreviewVisible = z;
            Log.i(TVLyricsLayout.TAG, "FULLSCREEN_CHROME drag-preview=" + z + " progress=" + this.dismissProgress);
        }

        void ensureMiniPlayerAfterClose() {
            final View view = this.windowRoot;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int identifier = view.getResources().getIdentifier("bottom_navigation_tabs_frame", "id", view.getContext().getPackageName());
                    View viewFindViewById = identifier != 0 ? view.findViewById(identifier) : null;
                    int identifier2 = view.getResources().getIdentifier("mini_player", "id", view.getContext().getPackageName());
                    View viewFindViewById2 = identifier2 != 0 ? view.findViewById(identifier2) : null;
                    int identifier3 = view.getResources().getIdentifier("mini_player_content", "id", view.getContext().getPackageName());
                    View viewFindViewById3 = identifier3 != 0 ? view.findViewById(identifier3) : null;
                    if (viewFindViewById != null) {
                        viewFindViewById.animate().cancel();
                        viewFindViewById.setVisibility(View.VISIBLE);
                        viewFindViewById.setAlpha(1.0f);
                    }
                    if (viewFindViewById2 != null) {
                        viewFindViewById2.animate().cancel();
                        viewFindViewById2.setVisibility(View.VISIBLE);
                        viewFindViewById2.setAlpha(1.0f);
                        viewFindViewById2.setTranslationX(0.0f);
                        viewFindViewById2.setTranslationY(0.0f);
                        viewFindViewById2.bringToFront();
                        viewFindViewById2.requestLayout();
                    }
                    // A second expand can capture the native mini-player after its
                    // own transition has already hidden mini_player_content. Restoring
                    // that transient snapshot leaves a correct black shell with no
                    // artwork/title/buttons. Restore only the content ancestor; native
                    // descendants retain their play/pause-specific visibility.
                    if (viewFindViewById3 != null) {
                        viewFindViewById3.animate().cancel();
                        viewFindViewById3.clearAnimation();
                        viewFindViewById3.setVisibility(View.VISIBLE);
                        viewFindViewById3.setAlpha(1.0f);
                        viewFindViewById3.setTranslationX(0.0f);
                        viewFindViewById3.setTranslationY(0.0f);
                        viewFindViewById3.requestLayout();
                        viewFindViewById3.invalidate();
                    }
                    Log.i(TVLyricsLayout.TAG, "MINI_PLAYER ensured-after-close=" + TVLyricsLayout.describe(viewFindViewById2) + " content=" + TVLyricsLayout.describe(viewFindViewById3));
                }
            };
            // Restore the real mini-player in the same sheet-state callback so
            // there is no visible black shell before the queued verification.
            runnable.run();
        }

        void beginSharedPlayerTransition() {
            if (this.sharedPlayerActive || this.restoring || !(this.windowRoot instanceof ViewGroup)) {
                return;
            }
            this.nativeHomeRoot = findNativeHomePage();
            if (this.nativeHomeRoot == null || !this.nativeHomeRoot.isAttachedToWindow()) {
                Log.w(TVLyricsLayout.TAG, "SHARED_PLAYER home-root-missing=true");
                return;
            }
            this.sharedArtworkContainer = this.nativeHomeRoot.findViewById(TVLyricsLayout.ID_ARTWORK_CONTAINER);
            this.sharedArtworkCard = this.sharedArtworkContainer != null ? this.sharedArtworkContainer.findViewById(TVLyricsLayout.ID_FULLPLAYER_SONG_IMAGE) : null;
            this.sharedArtworkSourceCard = this.sharedArtworkCard;
            View viewFindViewById = this.sharedArtworkSourceCard != null ? this.sharedArtworkSourceCard.findViewById(TVLyricsLayout.ID_VIDEO_SURFACE) : null;
            this.sharedVideoSurface = viewFindViewById instanceof TextureView ? (TextureView) viewFindViewById : null;
            this.sharedNativeArtwork = this.sharedArtworkSourceCard != null ? this.sharedArtworkSourceCard.findViewById(TVLyricsLayout.ID_ARTWORK_IMAGE) : null;
            this.sharedNativeFullscreenButton = this.sharedArtworkSourceCard != null ? this.sharedArtworkSourceCard.findViewById(TVLyricsLayout.ID_ENTER_FULL_SCREEN) : null;
            this.sharedTitle = this.nativeHomeRoot.findViewById(TVLyricsLayout.ID_TITLE);
            this.sharedSubtitle = this.nativeHomeRoot.findViewById(TVLyricsLayout.ID_SUBTITLE);
            this.sharedFavorite = this.nativeHomeRoot.findViewById(TVLyricsLayout.ID_LIST_FAVORITE_ICON);
            this.sharedMore = this.nativeHomeRoot.findViewById(TVLyricsLayout.ID_LIST_LEFT_ICON);
            this.sharedControls = this.nativeHomeRoot.findViewById(TVLyricsLayout.ID_HOME_CONTROLS);
            if (this.sharedArtworkContainer == null || this.sharedTitle == null || this.sharedSubtitle == null || this.sharedControls == null) {
                Log.w(TVLyricsLayout.TAG, "SHARED_PLAYER incomplete=true artwork=" + TVLyricsLayout.describe(this.sharedArtworkContainer) + " controls=" + TVLyricsLayout.describe(this.sharedControls));
                return;
            }
            FrameLayout frameLayout = new DragGestureHost(this.windowRoot.getContext(), this);
            this.sharedLayer = frameLayout;
            frameLayout.setClipChildren(false);
            frameLayout.setClipToPadding(false);
            frameLayout.setClickable(false);
            frameLayout.setFocusable(false);
            ((ViewGroup) this.windowRoot).addView(frameLayout, new ViewGroup.LayoutParams(TVLyricsLayout.UNSET, TVLyricsLayout.UNSET));
            frameLayout.setElevation(TVLyricsLayout.dp(frameLayout, 1500));
            frameLayout.setTranslationZ(TVLyricsLayout.dp(frameLayout, 1500));
            frameLayout.addView(new InputShield(frameLayout.getContext()), 0, new FrameLayout.LayoutParams(TVLyricsLayout.UNSET, TVLyricsLayout.UNSET));
            this.sharedItems.clear();
            this.sharedSnapshots.clear();
            this.sharedSeen.clear();
            this.sharedLyricsButton = this.sharedControls.findViewById(TVLyricsLayout.ID_PLAYER_LYRICS);
            this.sharedQueueButton = this.sharedControls.findViewById(TVLyricsLayout.ID_PLAYER_QUEUE);
            this.sharedRouteButton = this.sharedControls.findViewById(TVLyricsLayout.ID_MEDIA_ROUTE_BUTTON);
            captureSharedOnly(this.sharedArtworkSourceCard);
            captureSharedOnly(this.sharedNativeFullscreenButton);
            captureSharedOnly(this.sharedArtworkContainer);
            captureSharedOnly(this.sharedTitle);
            captureSharedOnly(this.sharedSubtitle);
            captureSharedOnly(this.sharedFavorite);
            captureSharedOnly(this.sharedMore);
            captureSharedOnly(this.sharedControls);
            captureSharedOnly(this.sharedLyricsButton);
            captureSharedOnly(this.sharedQueueButton);
            captureSharedOnly(this.sharedRouteButton);
            installSharedArtworkProxy();
            concealNativeVideoFullscreenButton();
            this.sharedArtworkPinned = shouldPinNativeVideoSurface();
            if (this.sharedArtworkPinned) {
                addPinnedSharedArtworkItem(this.sharedArtworkContainer);
            } else {
                addSharedItem(this.sharedArtworkContainer);
            }
            addSharedItem(this.sharedTitle);
            addSharedItem(this.sharedSubtitle);
            addSharedItem(this.sharedFavorite);
            addSharedItem(this.sharedMore);
            addSharedItem(this.sharedControls);
            assignSharedHomeTargets();
            assignSharedPanelTargets();
            installSharedDragDismissOverlay();
            this.sharedPlayerActive = !this.sharedItems.isEmpty();
            if (this.sharedArtworkPinned) {
                isolatePinnedNativeHome();
            }
            this.sharedPlayerAnimating = false;
            this.sharedAtPanelGeometry = false;
            this.sharedAnimationGeneration += TVLyricsLayout.MODE_LYRICS;
            setSharedModeVisual(0);
            frameLayout.bringToFront();
            Log.i(TVLyricsLayout.TAG, "SHARED_PLAYER lifted=true items=" + this.sharedItems.size() + " artwork=" + TVLyricsLayout.describe(this.sharedArtworkContainer) + " controls=" + TVLyricsLayout.describe(this.sharedControls));
        }

        boolean shouldPinNativeVideoSurface() {
            if (this.sharedVideoSurface == null || !this.sharedVideoSurface.isAvailable() || this.sharedVideoSurface.getSurfaceTexture() == null) {
                return false;
            }
            boolean artworkHidden = this.sharedNativeArtwork == null
                    || this.sharedNativeArtwork.getVisibility() != View.VISIBLE
                    || this.sharedNativeArtwork.getAlpha() < 0.25f;
            boolean videoVisible = this.sharedVideoSurface.getVisibility() == View.VISIBLE
                    && this.sharedVideoSurface.getAlpha() > 0.05f;
            // Do not wait for Apple's asynchronous artwork->video crossfade.
            // On a cold mini-player -> HOME open the TextureView is already
            // available, but artwork can remain opaque for several hundred
            // milliseconds.  Reparenting during that window destroys the
            // decoder's BufferQueue before the first frame arrives.  Pin every
            // live native TextureView in its original hierarchy; audio tracks
            // continue to render through the existing artwork proxy.
            boolean pin = videoVisible && (artworkHidden || hasVideoBootstrapEvidence());
            Log.i(TVLyricsLayout.TAG, "VIDEO_SURFACE pin-before-lift=" + pin
                    + " available=true artworkHidden=" + artworkHidden
                    + " videoVisible=" + videoVisible);
            return pin;
        }

        void unpinSharedArtworkForAudio() {
            if (!this.sharedArtworkPinned || this.sharedArtworkContainer == null || this.sharedLayer == null) {
                return;
            }
            SharedItem artworkItem = findSharedArtworkItem();
            if (artworkItem == null || !artworkItem.pinned) {
                this.sharedArtworkPinned = false;
                return;
            }
            int[] artworkLocation = new int[TVLyricsLayout.MODE_QUEUE];
            int[] layerLocation = new int[TVLyricsLayout.MODE_QUEUE];
            this.sharedArtworkContainer.getLocationInWindow(artworkLocation);
            this.sharedLayer.getLocationInWindow(layerLocation);
            int width = Math.max(TVLyricsLayout.MODE_LYRICS, this.sharedArtworkContainer.getWidth());
            int height = Math.max(TVLyricsLayout.MODE_LYRICS, this.sharedArtworkContainer.getHeight());
            ArrayList<TextureListenerState> textureListeners = new ArrayList<>();
            TVLyricsLayout.guardTextureListenersForReparent(this.sharedArtworkContainer, textureListeners);
            try {
                ViewParent parent = this.sharedArtworkContainer.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(this.sharedArtworkContainer);
                }
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
                params.leftMargin = artworkLocation[0] - layerLocation[0];
                params.topMargin = artworkLocation[TVLyricsLayout.MODE_LYRICS] - layerLocation[TVLyricsLayout.MODE_LYRICS];
                this.sharedLayer.addView(this.sharedArtworkContainer, params);
            } finally {
                TVLyricsLayout.restoreTextureListeners(textureListeners);
            }
            artworkItem.pinned = false;
            artworkItem.pinnedOffsetX = 0;
            artworkItem.pinnedOffsetY = 0;
            this.sharedArtworkPinned = false;
            this.sharedArtworkContainer.setVisibility(View.VISIBLE);
            this.sharedArtworkContainer.setAlpha(1.0f);
            this.sharedLayer.bringToFront();
            Log.i(TVLyricsLayout.TAG, "VIDEO_SURFACE unpinned-for-audio=true parent=shared-layer");
        }

        void addPinnedSharedArtworkItem(View view) {
            if (view == null || this.sharedLayer == null || view.getWidth() <= 0 || view.getHeight() <= 0) {
                return;
            }
            captureSharedOnly(this.nativeHomeRoot);
            if (!this.sharedSeen.containsKey(view)) {
                captureSharedOnly(view);
            }
            int[] viewLocation = new int[TVLyricsLayout.MODE_QUEUE];
            int[] layerLocation = new int[TVLyricsLayout.MODE_QUEUE];
            int[] parentLocation = new int[TVLyricsLayout.MODE_QUEUE];
            view.getLocationInWindow(viewLocation);
            this.sharedLayer.getLocationInWindow(layerLocation);
            ViewParent parent = view.getParent();
            if (parent instanceof View) {
                ((View) parent).getLocationInWindow(parentLocation);
            }
            SharedItem item = new SharedItem(
                    view,
                    viewLocation[0] - layerLocation[0],
                    viewLocation[TVLyricsLayout.MODE_LYRICS] - layerLocation[TVLyricsLayout.MODE_LYRICS],
                    view.getWidth(),
                    view.getHeight(),
                    true,
                    layerLocation[0] - parentLocation[0],
                    layerLocation[TVLyricsLayout.MODE_LYRICS] - parentLocation[TVLyricsLayout.MODE_LYRICS]);
            this.sharedItems.add(item);
            Log.i(TVLyricsLayout.TAG, "TEXTURE_REPARENT skipped=true bound-surface-preserved=true parent="
                    + (parent == null ? "none" : parent.getClass().getSimpleName()));
        }

        void isolatePinnedNativeHome() {
            if (!this.sharedArtworkPinned || !(this.nativeHomeRoot instanceof ViewGroup)) {
                return;
            }
            ViewGroup root = (ViewGroup) this.nativeHomeRoot;
            for (int index = 0; index < root.getChildCount(); index++) {
                View child = root.getChildAt(index);
                if (child != this.sharedArtworkContainer && child != this.fullscreenBackdrop) {
                    captureSharedOnly(child);
                    child.animate().cancel();
                    child.setVisibility(View.INVISIBLE);
                    child.setAlpha(0.0f);
                }
            }
            this.nativeHomeRoot.animate().cancel();
            this.nativeHomeRoot.setVisibility(View.VISIBLE);
            this.nativeHomeRoot.setAlpha(1.0f);
            this.nativeHomeRoot.bringToFront();
            Log.i(TVLyricsLayout.TAG, "VIDEO_SURFACE native-parent-visible=true isolated-children=" + root.getChildCount());
        }

        float sharedItemX(SharedItem item, float sharedLayerX) {
            return item.pinned ? sharedLayerX + item.pinnedOffsetX : sharedLayerX;
        }

        float sharedItemY(SharedItem item, float sharedLayerY) {
            return item.pinned ? sharedLayerY + item.pinnedOffsetY : sharedLayerY;
        }

        void commitSharedItemBounds(SharedItem item, int x, int y, int width, int height) {
            if (!item.pinned) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
                layoutParams.leftMargin = x;
                layoutParams.topMargin = y;
                item.view.setLayoutParams(layoutParams);
                item.view.setTranslationX(0.0f);
                item.view.setTranslationY(0.0f);
            } else {
                ViewGroup.LayoutParams layoutParams = item.view.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = height;
                item.view.setLayoutParams(layoutParams);
                item.view.setTranslationX(0.0f);
                item.view.setTranslationY(0.0f);
                item.view.setX(sharedItemX(item, x));
                item.view.setY(sharedItemY(item, y));
                isolatePinnedNativeHome();
            }
            item.view.setScaleX(1.0f);
            item.view.setScaleY(1.0f);
            item.view.setVisibility(View.VISIBLE);
            item.view.setAlpha(1.0f);
        }

        void installSharedArtworkProxy() {
            if (!(this.sharedArtworkContainer instanceof FrameLayout) || this.sharedArtworkSourceCard == null) {
                return;
            }
            ImageView imageView = new ImageView(this.sharedArtworkContainer.getContext());
            this.sharedArtworkProxy = imageView;
            // Album artwork is not always square (some native sources are portrait).
            // Preserve its aspect ratio while filling the existing square player card.
            imageView.setScaleType(ImageView.ScaleType.MATRIX);
            imageView.setAdjustViewBounds(false);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(0x00000000);
            gradientDrawable.setCornerRadius(TVLyricsLayout.dp(imageView, 12));
            imageView.setBackground(gradientDrawable);
            imageView.setClipToOutline(true);
            imageView.setElevation(TVLyricsLayout.dp(imageView, 10));
            imageView.setTranslationZ(TVLyricsLayout.dp(imageView, 10));
            int iMax = Math.max(TVLyricsLayout.MODE_LYRICS, this.sharedArtworkSourceCard.getWidth());
            int iMax2 = Math.max(TVLyricsLayout.MODE_LYRICS, this.sharedArtworkSourceCard.getHeight());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(iMax, iMax2, Gravity.CENTER);
            // Keep the native card attached and VISIBLE so Apple's existing
            // TextureView keeps its SurfaceTexture lifecycle.  Alpha zero is
            // sufficient while the proxy supplies album artwork; setting the
            // whole card INVISIBLE prevents decoded music-video frames from
            // ever becoming visible.
            this.sharedArtworkSourceCard.animate().cancel();
            this.sharedArtworkSourceCard.setAlpha(0.0f);
            this.sharedArtworkSourceCard.setVisibility(View.VISIBLE);
            ((FrameLayout) this.sharedArtworkContainer).addView(imageView, layoutParams);
            refreshSharedArtworkProxy(true);
            this.sharedArtworkCard = imageView;
            scheduleArtworkUpgradeChecks(imageView);
            Log.i(TVLyricsLayout.TAG, "ARTWORK_PROXY installed=true source=" + TVLyricsLayout.describe(this.sharedArtworkSourceCard));
        }

        void concealNativeVideoFullscreenButton() {
            if (this.sharedNativeFullscreenButton == null) {
                return;
            }
            this.sharedNativeFullscreenButton.animate().cancel();
            this.sharedNativeFullscreenButton.setVisibility(View.INVISIBLE);
            this.sharedNativeFullscreenButton.setAlpha(0.0f);
            this.sharedNativeFullscreenButton.setEnabled(false);
            this.sharedNativeFullscreenButton.setClickable(false);
            this.sharedNativeFullscreenButton.setFocusable(false);
            Log.i(TVLyricsLayout.TAG, "VIDEO_NATIVE_FULLSCREEN concealed=true");
        }

        void refreshSharedVideoPresentation(boolean z) {
            TextureView textureView = this.sharedVideoSurface;
            View view = this.sharedNativeArtwork;
            if (textureView == null || this.sharedArtworkSourceCard == null || this.sharedArtworkProxy == null) {
                return;
            }
            int width = textureView.getWidth();
            int height = textureView.getHeight();
            ViewGroup.LayoutParams layoutParams = textureView.getLayoutParams();
            if (layoutParams != null) {
                if (layoutParams.width > 0) {
                    width = layoutParams.width;
                }
                if (layoutParams.height > 0) {
                    height = layoutParams.height;
                }
            }
            String strCurrentArtworkKey = currentArtworkKey();
            boolean currentProxy = strCurrentArtworkKey.equals(this.sharedArtworkProxyKey);
            // A 48px square thumbnail is held generically during every
            // cross-track artwork upgrade. It is not video evidence. Only a
            // current-key landscape bitmap may open the video Surface gate.
            boolean currentPendingVideo = strCurrentArtworkKey.equals(this.pendingVideoArtworkKey)
                    && usableBitmapAspect(this.pendingVideoArtworkBitmap) > 1.30f;
            boolean videoShapeEvidence = currentProxy && usableBitmapAspect(this.sharedArtworkProxyBitmap) > 0.0f;
            if (!videoShapeEvidence && currentProxy) {
                videoShapeEvidence = usableBitmapAspect(this.sharedArtworkSourceBitmap) > 0.0f;
            }
            if (!videoShapeEvidence && currentPendingVideo) {
                videoShapeEvidence = usableBitmapAspect(this.pendingVideoArtworkBitmap) > 0.0f;
            }
            float proxyAspect = currentPendingVideo
                    ? usableBitmapAspect(this.pendingVideoArtworkBitmap)
                    : (currentProxy ? usableBitmapAspect(this.sharedArtworkProxyBitmap) : 0.0f);
            if (proxyAspect <= 0.0f && currentProxy) {
                proxyAspect = usableBitmapAspect(this.sharedArtworkSourceBitmap);
            }
            // The current-key landscape thumbnail arrives before the decoder's
            // first frame.  Use it only to prepare the final rectangle and
            // background; the TextureView remains hidden until frameReady.
            boolean earlyWideVideoCandidate = proxyAspect > 1.30f;
            if (earlyWideVideoCandidate || this.sharedVideoActive || this.pendingVideoArtworkKey != null) {
                logAllTextureViews(strCurrentArtworkKey, "refresh");
            }
            if (earlyWideVideoCandidate) {
                applyPendingVideoPresentation(strCurrentArtworkKey, proxyAspect);
            } else if (this.pendingVideoGeometryKey != null
                    && !strCurrentArtworkKey.equals(this.pendingVideoGeometryKey)) {
                clearPendingVideoPresentation("media-key-changed");
            }
            View panelVideoRoot = this.mode == TVLyricsLayout.MODE_QUEUE && this.activePanelRoot != null
                    ? this.activePanelRoot : this.retainedQueueVideoRoot;
            TextureView panelTexture = null;
            if (panelVideoRoot != null) {
                View panelVideo = panelVideoRoot.findViewById(TVLyricsLayout.ID_VIDEO_SURFACE);
                if (panelVideo instanceof TextureView) {
                    panelTexture = (TextureView) panelVideo;
                }
            }
            boolean knownCurrentVideo = this.sharedVideoActive
                    && strCurrentArtworkKey.equals(this.sharedVideoKey);
            View panelThumbnail = panelVideoRoot != null
                    ? panelVideoRoot.findViewById(TVLyricsLayout.ID_THUMBNAIL) : null;
            boolean panelArtworkHidden = panelThumbnail == null
                    || panelThumbnail.getVisibility() != View.VISIBLE
                    || panelThumbnail.getAlpha() < 0.25f;
            boolean panelSurfaceFrameVisible = panelTexture != null
                    && panelTexture.isAvailable()
                    && panelTexture.getSurfaceTexture() != null
                    && panelTexture.getVisibility() == View.VISIBLE
                    && panelTexture.getAlpha() > 0.05f
                    && hasVisibleTextureFrame(panelTexture);
            // A moving-frame proof is needed only for a real cross-track
            // handoff, where a different artwork/video is still visible.  On a
            // cold open there is no old frame to confuse with the current
            // track; forcing the two-sample gate there rejects Apple's valid
            // first QUEUE frame and eventually diverts playback to a second
            // HOME Surface.
            boolean crossTrackFrameProofRequired = currentPendingVideo
                    && this.sharedArtworkProxyBitmap != null
                    && !this.sharedArtworkProxyBitmap.isRecycled()
                    && this.sharedArtworkProxyKey != null
                    && !strCurrentArtworkKey.equals(this.sharedArtworkProxyKey);
            boolean mainSurfaceCandidate = earlyWideVideoCandidate
                    && textureView.isAvailable()
                    && textureView.getSurfaceTexture() != null
                    && width > TVLyricsLayout.MODE_LYRICS
                    && height > TVLyricsLayout.MODE_LYRICS
                    && textureView.getAlpha() > 0.55f
                    && (view == null || view.getAlpha() < 0.45f
                    || this.sharedArtworkSourceCard.getAlpha() < 0.45f);
            boolean mainSurfaceFrameVisible = mainSurfaceCandidate
                    && hasVisibleTextureFrame(textureView);
            // Apple may bind the new decoder output to HOME or QUEUE depending
            // on which player state was active when the track changed. Probe
            // each concrete TextureView against its own baseline; never compare
            // fingerprints across surfaces. The surface that actually advances
            // becomes the confirmed owner for the whole presentation.
            boolean videoCandidateExpected = earlyWideVideoCandidate || knownCurrentVideo;
            boolean panelAdvanced = videoCandidateExpected && panelSurfaceFrameVisible
                    && (!crossTrackFrameProofRequired
                    || pendingVideoFrameAdvanced(panelTexture, strCurrentArtworkKey));
            boolean mainAdvanced = videoCandidateExpected && mainSurfaceFrameVisible
                    && (!crossTrackFrameProofRequired
                    || pendingVideoFrameAdvanced(textureView, strCurrentArtworkKey));
            TextureView candidateTexture;
            if (panelAdvanced && mainAdvanced) {
                candidateTexture = this.sharedVideoConfirmedSurface == textureView
                        ? textureView : panelTexture;
            } else if (panelAdvanced) {
                candidateTexture = panelTexture;
            } else if (mainAdvanced) {
                candidateTexture = textureView;
            } else {
                candidateTexture = null;
            }
            boolean candidateFrameReady = candidateTexture != null;
            boolean panelFrameReady = candidateFrameReady && candidateTexture == panelTexture;
            boolean mainFrameReady = candidateFrameReady && candidateTexture == textureView;
            boolean mainVideoDetected = mainFrameReady;
            // During the stable landscape flow QUEUE owns the decoder Surface.
            // Do not classify the track as audio merely because the hidden HOME
            // TextureView has no frame; the visible queue TextureView is the
            // authoritative surface until the shared player returns to audio.
            boolean z2 = knownCurrentVideo || mainFrameReady || panelFrameReady;
            TextureView detectedTexture = !mainVideoDetected && panelFrameReady
                    ? panelTexture : textureView;
            float f = knownCurrentVideo && !mainVideoDetected && !panelFrameReady
                    ? this.sharedVideoAspect
                    : (z2 ? resolveVideoContentAspect(detectedTexture) : 1.0f);
            boolean frameReady = z2 && (knownCurrentVideo || mainFrameReady || panelFrameReady);
            if (earlyWideVideoCandidate && !z2) {
                // Keep the previous proxy/background while Apple is handing the
                // decoder from the old item to the new item. Do not run the
                // audio restoration path during this intentionally pending gap.
                this.sharedVideoActive = false;
                this.sharedVideoFrameReady = false;
                this.sharedVideoKey = null;
                applySharedVideoPresentation("pending-current-frame");
                if (this.mode == 0 && !this.retainedQueueVideoAtHome) {
                    ensureQueueVideoSurfaceForHome();
                }
                return;
            }
            boolean wasFrameReady = this.sharedVideoFrameReady;
            boolean z3 = z2 != this.sharedVideoActive
                    || (z2 && Math.abs(f - this.sharedVideoAspect) > 0.01f)
                    || (z2 && !strCurrentArtworkKey.equals(this.sharedVideoKey));
            boolean firstReadyFrame = z2 && frameReady && (!wasFrameReady || z3);
            if (mainFrameReady || panelFrameReady) {
                this.sharedVideoConfirmedSurface = detectedTexture;
            }
            this.sharedVideoActive = z2;
            this.sharedVideoFrameReady = frameReady;
            this.sharedVideoAspect = f;
            this.sharedVideoKey = z2 ? strCurrentArtworkKey : null;
            if (firstReadyFrame) {
                applyVideoBackgroundFromCurrentArtwork();
                clearPendingVideoArtwork("first-frame");
                updateSharedHomeTargetsForVideo();
                if (this.mode == 0 && !this.sharedAtPanelGeometry && this.sharedLayer != null) {
                    this.sharedLayer.post(new Runnable() {
                        @Override
                        public void run() {
                            if (TVLyricsLayout.active == Controller.this
                                    && Controller.this.expanded
                                    && Controller.this.mode == 0
                                    && Controller.this.sharedVideoActive
                                    && !Controller.this.sharedAtPanelGeometry) {
                                Controller.this.commitSharedHomeGeometry();
                                Log.i(TVLyricsLayout.TAG, "VIDEO_HOME_TARGET initial-geometry-committed=true");
                            }
                        }
                    });
                }
            } else if (z3 && !z2) {
                clearRetainedQueueVideo();
                this.sharedVideoConfirmedSurface = null;
                clearPendingVideoPresentation("confirmed-audio");
                restoreVideoBackgrounds();
                unpinSharedArtworkForAudio();
                assignSharedHomeTargets();
                if (this.sharedPlayerActive && this.sharedLayer != null) {
                    if (this.mode == 0) {
                        commitSharedHomeGeometry();
                    } else {
                        commitSharedPanelGeometry();
                    }
                }
                View panelArtwork = this.panelRoot != null
                        ? this.panelRoot.findViewById(TVLyricsLayout.ID_QUEUE_THUMBNAIL_CONTAINER)
                        : null;
                if (panelArtwork != null) {
                    panelArtwork.setTranslationY(0.0f);
                }
                Log.i(TVLyricsLayout.TAG, "VIDEO_PRESENTATION audio-geometry-restored=true mode=" + this.mode);
            }
            if (z2 && this.sharedLyricsButton != null) {
                this.sharedLyricsButton.setEnabled(false);
                this.sharedLyricsButton.refreshDrawableState();
            }
            applySharedVideoPresentation(z ? "forced-refresh" : "watch");
            if (z2 && this.mode == TVLyricsLayout.MODE_LYRICS) {
                enterHome("video-has-no-lyrics");
            }
            if (z3) {
                Log.i(TVLyricsLayout.TAG, "VIDEO_PRESENTATION detected=" + z2
                        + " mode=" + this.mode
                        + " available=" + textureView.isAvailable()
                        + " surface=" + (textureView.getSurfaceTexture() != null)
                        + " texture=" + width + "x" + height
                        + " textureAlpha=" + textureView.getAlpha()
                        + " frameReady=" + frameReady
                        + " artworkAlpha=" + (view == null ? "none" : Float.valueOf(view.getAlpha()))
                        + " key=" + strCurrentArtworkKey);
            }
        }

        void applyPendingVideoPresentation(String key, float aspect) {
            if (key == null || key.length() == 0 || aspect <= 1.30f) {
                return;
            }
            boolean firstForKey = !key.equals(this.pendingVideoGeometryKey);
            this.pendingVideoGeometryKey = key;
            if (firstForKey) {
                Log.i(TVLyricsLayout.TAG, "VIDEO_PENDING geometry-held-until-frame=true key="
                        + key.replace('\n', '|') + " aspect=" + aspect);
            }
        }

        void clearPendingVideoPresentation(String reason) {
            clearPendingVideoArtwork(reason);
            if (this.pendingVideoGeometryKey == null) {
                return;
            }
            this.pendingVideoGeometryKey = null;
            Log.i(TVLyricsLayout.TAG, "VIDEO_PENDING cleared=true reason=" + reason);
        }

        boolean shouldHoldIncomingVideoArtwork(String key, Bitmap bitmap) {
            return this.expanded
                    && bitmap != null
                    && !bitmap.isRecycled()
                    && (usableBitmapAspect(bitmap) > 1.30f
                    || Math.min(bitmap.getWidth(), bitmap.getHeight()) < 160)
                    && this.sharedArtworkProxyBitmap != null
                    && !this.sharedArtworkProxyBitmap.isRecycled()
                    && this.sharedArtworkProxyKey != null
                    && !key.equals(this.sharedArtworkProxyKey);
        }

        void stagePendingVideoArtwork(String key, Bitmap bitmap, String source) {
            if (key == null || bitmap == null || bitmap.isRecycled()) {
                return;
            }
            boolean newKey = !key.equals(this.pendingVideoArtworkKey);
            boolean changed = newKey
                    || this.pendingVideoArtworkBitmap != bitmap;
            if (newKey) {
                this.pendingVideoSurfaceBaselines.clear();
                this.pendingVideoSurfaceBaselineTimes.clear();
                this.sharedVideoConfirmedSurface = null;
                this.pendingVideoSurfaceBaselineKey = null;
                this.pendingVideoSurfaceBaselineView = null;
                this.pendingVideoSurfaceBaseline = 0L;
                this.pendingVideoSurfaceBaselineCaptured = false;
                this.pendingVideoSurfaceBaselineAt = 0L;
            }
            this.pendingVideoArtworkKey = key;
            this.pendingVideoArtworkBitmap = bitmap;
            if (changed) {
                Log.i(TVLyricsLayout.TAG, "VIDEO_PENDING artwork-held=true source=" + source
                        + " size=" + bitmap.getWidth() + "x" + bitmap.getHeight()
                        + " visibleKey=" + String.valueOf(this.sharedArtworkProxyKey).replace('\n', '|')
                        + " pendingKey=" + key.replace('\n', '|'));
            }
        }

        void clearPendingVideoArtwork(String reason) {
            if (this.pendingVideoArtworkKey == null && this.pendingVideoArtworkBitmap == null) {
                this.pendingPanelKeepAliveLogKey = null;
                this.pendingVideoSurfaceBaselines.clear();
                this.pendingVideoSurfaceBaselineTimes.clear();
                this.pendingVideoSurfaceBaselineKey = null;
                this.pendingVideoSurfaceBaselineView = null;
                this.pendingVideoSurfaceBaseline = 0L;
                this.pendingVideoSurfaceBaselineCaptured = false;
                this.pendingVideoSurfaceBaselineAt = 0L;
                return;
            }
            this.pendingVideoArtworkKey = null;
            this.pendingVideoArtworkBitmap = null;
            this.pendingPanelKeepAliveLogKey = null;
            this.pendingVideoSurfaceBaselines.clear();
            this.pendingVideoSurfaceBaselineTimes.clear();
            this.pendingVideoSurfaceBaselineKey = null;
            this.pendingVideoSurfaceBaselineView = null;
            this.pendingVideoSurfaceBaseline = 0L;
            this.pendingVideoSurfaceBaselineCaptured = false;
            this.pendingVideoSurfaceBaselineAt = 0L;
            Log.i(TVLyricsLayout.TAG, "VIDEO_PENDING artwork-cleared=true reason=" + reason);
        }

        void applyVideoBackgroundFromCurrentArtwork() {
            Bitmap bitmap = currentArtworkKey().equals(this.pendingVideoArtworkKey)
                    ? this.pendingVideoArtworkBitmap : this.sharedArtworkProxyBitmap;
            if (bitmap == null || bitmap.isRecycled()) {
                bitmap = this.sharedArtworkSourceBitmap;
            }
            int color = darkVideoBackgroundColor(bitmap);
            if (color == 0) {
                return;
            }
            this.currentVideoBackgroundColor = color;
            updateFullscreenBackdrop(bitmap, color);
            // Both native player roots are full-screen opaque surfaces. A solid
            // per-track color here covers the retained QUEUE TextureView and
            // also flattens the blurred artwork backdrop. Keep the roots in the
            // hierarchy but make only their backgrounds transparent; their
            // exact original drawables are restored when playback returns to
            // audio or the expanded player closes.
            applyVideoBackgroundTo(this.panelRoot, Color.TRANSPARENT);
            applyVideoBackgroundTo(this.nativeHomeRoot, Color.TRANSPARENT);
            placeFullscreenBackdropUnder(this.panelRoot != null ? this.panelRoot : this.nativeHomeRoot,
                    "video-current-root");
            Log.i(TVLyricsLayout.TAG, "TRACK_BACKGROUND applied=video color="
                    + Integer.toHexString(color) + " key=" + currentArtworkKey().replace('\n', '|'));
        }

        void applyTrackBackgroundFromArtwork(Bitmap bitmap, String key, String source) {
            if (!this.expanded || bitmap == null || bitmap.isRecycled()
                    || key == null || !key.equals(currentArtworkKey())) {
                return;
            }
            int color = darkVideoBackgroundColor(bitmap);
            if (color == 0) {
                return;
            }
            this.currentVideoBackgroundColor = color;
            updateFullscreenBackdrop(bitmap, color);
            applyVideoBackgroundTo(this.panelRoot, Color.TRANSPARENT);
            applyVideoBackgroundTo(this.nativeHomeRoot, Color.TRANSPARENT);
            placeFullscreenBackdropUnder(this.nativeHomeRoot != null ? this.nativeHomeRoot : this.outerContainer,
                    "audio-home-root");
            Log.i(TVLyricsLayout.TAG, "TRACK_BACKGROUND applied=" + source
                    + " color=" + Integer.toHexString(color)
                    + " key=" + key.replace('\n', '|'));
        }

        void applyVideoBackgroundTo(View view, int color) {
            if (view == null) {
                return;
            }
            if (!this.videoBackgroundOriginals.containsKey(view)) {
                this.videoBackgroundOriginals.put(view, view.getBackground());
            }
            view.setBackgroundColor(color);
        }

        void updateFullscreenBackdrop(Bitmap bitmap, int color) {
            if (!(this.fullscreenBackdrop instanceof ImageView)) {
                return;
            }
            ImageView backdrop = (ImageView) this.fullscreenBackdrop;
            backdrop.animate().cancel();
            if (bitmap != null && !bitmap.isRecycled()) {
                backdrop.setImageBitmap(bitmap);
            }
            backdrop.setBackgroundColor(color);
            backdrop.setAlpha(1.0f);
        }

        void placeFullscreenBackdropUnder(View root, String reason) {
            if (!(this.fullscreenBackdrop instanceof ImageView) || !(root instanceof ViewGroup)) {
                return;
            }
            ViewGroup target = (ViewGroup) root;
            View backdrop = this.fullscreenBackdrop;
            ViewParent parent = backdrop.getParent();
            if (parent == target && target.indexOfChild(backdrop) == 0) {
                backdrop.setVisibility(View.VISIBLE);
                return;
            }
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(backdrop);
            }
            target.addView(backdrop, 0,
                    new ViewGroup.LayoutParams(TVLyricsLayout.UNSET, TVLyricsLayout.UNSET));
            backdrop.setVisibility(View.VISIBLE);
            backdrop.setAlpha(1.0f);
            Log.i(TVLyricsLayout.TAG, "FULLSCREEN_BACKDROP host=current-root reason=" + reason
                    + " root=" + TVLyricsLayout.describe(root));
        }

        void restoreVideoBackgrounds() {
            for (View view : new ArrayList<View>(this.videoBackgroundOriginals.keySet())) {
                if (view != null) {
                    view.setBackground(this.videoBackgroundOriginals.get(view));
                }
            }
            this.videoBackgroundOriginals.clear();
            this.currentVideoBackgroundColor = 0;
        }

        int darkVideoBackgroundColor(Bitmap bitmap) {
            if (bitmap == null || bitmap.isRecycled() || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
                return 0;
            }
            long red = 0L;
            long green = 0L;
            long blue = 0L;
            int count = 0;
            int sampleX = Math.max(1, bitmap.getWidth() / 16);
            int sampleY = Math.max(1, bitmap.getHeight() / 16);
            for (int y = sampleY / 2; y < bitmap.getHeight(); y += sampleY) {
                for (int x = sampleX / 2; x < bitmap.getWidth(); x += sampleX) {
                    int pixel = bitmap.getPixel(x, y);
                    int r = Color.red(pixel);
                    int g = Color.green(pixel);
                    int b = Color.blue(pixel);
                    int brightness = r + g + b;
                    if (Color.alpha(pixel) < 32 || brightness < 24 || brightness > 735) {
                        continue;
                    }
                    red += r;
                    green += g;
                    blue += b;
                    count++;
                }
            }
            if (count == 0) {
                return 0xff181818;
            }
            int average = Color.rgb((int) (red / count), (int) (green / count), (int) (blue / count));
            float[] hsv = new float[3];
            Color.colorToHSV(average, hsv);
            hsv[1] = Math.max(0.32f, Math.min(0.72f, hsv[1] * 1.25f));
            hsv[2] = Math.max(0.14f, Math.min(0.24f, hsv[2] * 0.42f));
            return Color.HSVToColor(hsv);
        }

        boolean hasVisibleTextureFrame(TextureView textureView) {
            if (textureView == null || !textureView.isAvailable()) {
                return false;
            }
            Bitmap sample = null;
            try {
                sample = textureView.getBitmap(24, 14);
                if (sample == null) {
                    return false;
                }
                int visible = 0;
                int total = sample.getWidth() * sample.getHeight();
                int[] pixels = new int[total];
                sample.getPixels(pixels, 0, sample.getWidth(), 0, 0, sample.getWidth(), sample.getHeight());
                for (int color : pixels) {
                    int rgb = Color.red(color) + Color.green(color) + Color.blue(color);
                    if (Color.alpha(color) > 24 && rgb > 24) {
                        visible++;
                    }
                }
                return visible >= Math.max(2, total / 50);
            } catch (Throwable ignored) {
                return false;
            } finally {
                if (sample != null && !sample.isRecycled()) {
                    sample.recycle();
                }
            }
        }

        long textureFrameFingerprint(TextureView textureView) {
            if (textureView == null || !textureView.isAvailable()) {
                return 0L;
            }
            Bitmap sample = null;
            try {
                sample = textureView.getBitmap(16, 9);
                if (sample == null) {
                    return 0L;
                }
                long hash = -3750763034362895579L;
                int visible = 0;
                int total = sample.getWidth() * sample.getHeight();
                int[] pixels = new int[total];
                sample.getPixels(pixels, 0, sample.getWidth(), 0, 0,
                        sample.getWidth(), sample.getHeight());
                for (int color : pixels) {
                    int alpha = Color.alpha(color);
                    int red = Color.red(color);
                    int green = Color.green(color);
                    int blue = Color.blue(color);
                    if (alpha > 24 && red + green + blue > 24) {
                        visible++;
                    }
                    long quantized = ((alpha >> 4) << 12)
                            | ((red >> 4) << 8)
                            | ((green >> 4) << 4)
                            | (blue >> 4);
                    hash ^= quantized;
                    hash *= 1099511628211L;
                }
                return visible >= Math.max(2, total / 50) ? hash : 0L;
            } catch (Throwable ignored) {
                return 0L;
            } finally {
                if (sample != null && !sample.isRecycled()) {
                    sample.recycle();
                }
            }
        }

        void logAllTextureViews(String key, String reason) {
            long now = android.os.SystemClock.uptimeMillis();
            if (this.windowRoot == null || now - this.lastTextureDiagnosticAt < 800L) {
                return;
            }
            this.lastTextureDiagnosticAt = now;
            java.util.ArrayList<TextureView> textures = new java.util.ArrayList<TextureView>();
            collectTextureViews(this.windowRoot, textures);
            int miniId = this.windowRoot.getResources().getIdentifier("mini_player", "id",
                    this.windowRoot.getContext().getPackageName());
            View mini = miniId != 0 ? this.windowRoot.findViewById(miniId) : null;
            String safeKey = key == null ? "none" : key.replace('\n', '|');
            Log.i(TVLyricsLayout.TAG, "TEXTURE_AUDIT begin reason=" + reason
                    + " mode=" + this.mode + " count=" + textures.size() + " key=" + safeKey);
            for (TextureView texture : textures) {
                int[] xy = new int[2];
                try {
                    texture.getLocationInWindow(xy);
                } catch (Throwable ignored) {
                    xy[0] = -1;
                    xy[1] = -1;
                }
                String role = texture == this.sharedVideoSurface ? "HOME"
                        : texture == this.retainedQueueVideoSurface ? "RETAINED_QUEUE"
                        : isDescendantOf(texture, this.activePanelRoot) ? "ACTIVE_PANEL"
                        : isDescendantOf(texture, mini) ? "MINI"
                        : "OTHER";
                Log.i(TVLyricsLayout.TAG, "TEXTURE_AUDIT item surface="
                        + Integer.toHexString(System.identityHashCode(texture))
                        + " role=" + role
                        + " pos=" + xy[0] + "," + xy[1]
                        + " size=" + texture.getWidth() + "x" + texture.getHeight()
                        + " vis=" + texture.getVisibility()
                        + " alpha=" + texture.getAlpha()
                        + " shown=" + texture.isShown()
                        + " attached=" + texture.isAttachedToWindow()
                        + " available=" + texture.isAvailable()
                        + " ancestors=" + visibleAncestorSummary(texture)
                        + " order=" + renderOrderSummary(texture)
                        + " frame=" + textureFrameStats(texture));
            }
            Log.i(TVLyricsLayout.TAG, "TEXTURE_AUDIT layers opening=" + layerOrderSummary(this.openingCurtain)
                    + " backdrop=" + layerOrderSummary(this.fullscreenBackdrop)
                    + " outer=" + layerOrderSummary(this.outerContainer)
                    + " shared=" + layerOrderSummary(this.sharedLayer)
                    + " shield=" + layerOrderSummary(this.inputShield)
                    + " artwork=" + layerOrderSummary(this.retainedQueueVideoArtwork)
                    + " panel=" + layerOrderSummary(this.retainedQueueVideoRoot));
            ViewParent panelParent = this.retainedQueueVideoRoot == null
                    ? null : this.retainedQueueVideoRoot.getParent();
            Log.i(TVLyricsLayout.TAG, "TEXTURE_AUDIT panel-siblings="
                    + childLayerSummary(panelParent instanceof ViewGroup ? (ViewGroup) panelParent : null));
            Log.i(TVLyricsLayout.TAG, "TEXTURE_AUDIT end");
        }

        void collectTextureViews(View view, java.util.ArrayList<TextureView> result) {
            if (view == null) {
                return;
            }
            if (view instanceof TextureView) {
                result.add((TextureView) view);
            }
            if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) view;
                for (int i = 0; i < group.getChildCount(); i++) {
                    collectTextureViews(group.getChildAt(i), result);
                }
            }
        }

        boolean isDescendantOf(View child, View ancestor) {
            if (child == null || ancestor == null) {
                return false;
            }
            View current = child;
            while (current != null) {
                if (current == ancestor) {
                    return true;
                }
                ViewParent parent = current.getParent();
                current = parent instanceof View ? (View) parent : null;
            }
            return false;
        }

        String visibleAncestorSummary(View view) {
            StringBuilder builder = new StringBuilder();
            View current = view;
            int depth = 0;
            while (current != null && depth < 7) {
                if (depth > 0) {
                    builder.append('>');
                }
                builder.append(Integer.toHexString(System.identityHashCode(current)))
                        .append(':').append(current.getVisibility())
                        .append(':').append(Math.round(current.getAlpha() * 100.0f));
                ViewParent parent = current.getParent();
                current = parent instanceof View ? (View) parent : null;
                depth++;
            }
            return builder.toString();
        }

        String layerOrderSummary(View view) {
            if (view == null) {
                return "null";
            }
            ViewParent parent = view.getParent();
            int index = parent instanceof ViewGroup ? ((ViewGroup) parent).indexOfChild(view) : -1;
            int count = parent instanceof ViewGroup ? ((ViewGroup) parent).getChildCount() : -1;
            int[] xy = new int[2];
            try {
                view.getLocationInWindow(xy);
            } catch (Throwable ignored) {
                xy[0] = -1;
                xy[1] = -1;
            }
            String clip = view instanceof ViewGroup
                    ? ",clip=" + ((ViewGroup) view).getClipChildren()
                    + "/" + ((ViewGroup) view).getClipToPadding()
                    : "";
            Drawable background = view.getBackground();
            String backgroundState = background == null ? "null"
                    : background.getClass().getSimpleName() + ":" + background.getOpacity();
            String imageState = view instanceof ImageView
                    ? ",img=" + (((ImageView) view).getDrawable() == null ? "null"
                    : ((ImageView) view).getDrawable().getClass().getSimpleName())
                    : "";
            return view.getClass().getSimpleName() + "@"
                    + Integer.toHexString(System.identityHashCode(view))
                    + "[" + index + "/" + count + ",e=" + view.getElevation()
                    + ",z=" + view.getZ() + ",tz=" + view.getTranslationZ()
                    + ",pos=" + xy[0] + "," + xy[1]
                    + ",size=" + view.getWidth() + "x" + view.getHeight()
                    + ",vis=" + view.getVisibility() + ",alpha=" + view.getAlpha()
                    + ",bg=" + backgroundState + imageState + clip + "]";
        }

        String renderOrderSummary(View view) {
            StringBuilder builder = new StringBuilder();
            View current = view;
            int depth = 0;
            while (current != null && depth < 12) {
                if (depth > 0) {
                    builder.append('>');
                }
                builder.append(layerOrderSummary(current));
                ViewParent parent = current.getParent();
                current = parent instanceof View ? (View) parent : null;
                depth++;
            }
            return builder.toString();
        }

        String childLayerSummary(ViewGroup group) {
            if (group == null) {
                return "null";
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < group.getChildCount(); i++) {
                if (i > 0) {
                    builder.append(" || ");
                }
                builder.append(layerOrderSummary(group.getChildAt(i)));
            }
            return builder.toString();
        }

        String textureFrameStats(TextureView texture) {
            if (texture == null || !texture.isAvailable()) {
                return "unavailable";
            }
            Bitmap sample = null;
            try {
                sample = texture.getBitmap(32, 18);
                if (sample == null) {
                    return "null";
                }
                int width = sample.getWidth();
                int height = sample.getHeight();
                int total = width * height;
                int[] pixels = new int[total];
                sample.getPixels(pixels, 0, width, 0, 0, width, height);
                boolean[] colors = new boolean[4096];
                int unique = 0;
                int visible = 0;
                double sum = 0.0d;
                double sumSq = 0.0d;
                double edge = 0.0d;
                long hash = -3750763034362895579L;
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int index = y * width + x;
                        int color = pixels[index];
                        int r = Color.red(color);
                        int g = Color.green(color);
                        int b = Color.blue(color);
                        int a = Color.alpha(color);
                        int quantized = ((r >> 4) << 8) | ((g >> 4) << 4) | (b >> 4);
                        if (!colors[quantized]) {
                            colors[quantized] = true;
                            unique++;
                        }
                        double luma = 0.2126d * r + 0.7152d * g + 0.0722d * b;
                        if (a > 24 && r + g + b > 24) {
                            visible++;
                        }
                        sum += luma;
                        sumSq += luma * luma;
                        if (x > 0) {
                            int left = pixels[index - 1];
                            double leftLuma = 0.2126d * Color.red(left)
                                    + 0.7152d * Color.green(left)
                                    + 0.0722d * Color.blue(left);
                            edge += Math.abs(luma - leftLuma);
                        }
                        if (y > 0) {
                            int up = pixels[index - width];
                            double upLuma = 0.2126d * Color.red(up)
                                    + 0.7152d * Color.green(up)
                                    + 0.0722d * Color.blue(up);
                            edge += Math.abs(luma - upLuma);
                        }
                        hash ^= (((long) a >> 4) << 12) | quantized;
                        hash *= 1099511628211L;
                    }
                }
                double mean = sum / Math.max(1, total);
                double variance = Math.max(0.0d, (sumSq / Math.max(1, total)) - mean * mean);
                double averageEdge = edge / Math.max(1, (width - 1) * height + (height - 1) * width);
                return "hash=" + Long.toHexString(hash)
                        + ",visible=" + visible + "/" + total
                        + ",unique=" + unique
                        + ",lumaVar=" + Math.round(variance)
                        + ",edge=" + Math.round(averageEdge * 10.0d) / 10.0d;
            } catch (Throwable throwable) {
                return "error=" + throwable.getClass().getSimpleName();
            } finally {
                if (sample != null && !sample.isRecycled()) {
                    sample.recycle();
                }
            }
        }

        boolean pendingVideoFrameAdvanced(TextureView textureView, String key) {
            if (key == null || !key.equals(this.pendingVideoArtworkKey)) {
                return false;
            }
            long fingerprint = textureFrameFingerprint(textureView);
            if (fingerprint == 0L) {
                return false;
            }
            if (!key.equals(this.pendingVideoSurfaceBaselineKey)) {
                this.pendingVideoSurfaceBaselineKey = key;
                this.pendingVideoSurfaceBaselines.clear();
                this.pendingVideoSurfaceBaselineTimes.clear();
            }
            Long baseline = this.pendingVideoSurfaceBaselines.get(textureView);
            if (baseline == null) {
                long now = android.os.SystemClock.uptimeMillis();
                this.pendingVideoSurfaceBaselines.put(textureView, Long.valueOf(fingerprint));
                this.pendingVideoSurfaceBaselineTimes.put(textureView, Long.valueOf(now));
                Log.i(TVLyricsLayout.TAG, "VIDEO_FRAME baseline=true surface="
                        + Integer.toHexString(System.identityHashCode(textureView))
                        + " key=" + key.replace('\n', '|')
                        + " fingerprint=" + Long.toHexString(fingerprint));
                return false;
            }
            if (fingerprint == baseline.longValue()) {
                return false;
            }
            Long baselineAt = this.pendingVideoSurfaceBaselineTimes.get(textureView);
            Log.i(TVLyricsLayout.TAG, "VIDEO_FRAME advanced=true surface="
                    + Integer.toHexString(System.identityHashCode(textureView))
                    + " key=" + key.replace('\n', '|') + " ageMs="
                    + (android.os.SystemClock.uptimeMillis() - (baselineAt == null ? 0L : baselineAt.longValue()))
                    + " from=" + Long.toHexString(baseline.longValue())
                    + " to=" + Long.toHexString(fingerprint));
            return true;
        }

        void ensureVideoSurfaceShowsFullFrame(final TextureView textureView, final float contentAspect, final String reason) {
            if (textureView == null || contentAspect <= 0.0f) {
                return;
            }
            textureView.post(new Runnable() {
                @Override
                public void run() {
                    if (!textureView.isAttachedToWindow() || textureView.getWidth() <= 0 || textureView.getHeight() <= 0) {
                        return;
                    }
                    float viewAspect = textureView.getWidth() / (float) textureView.getHeight();
                    float error = Math.abs(viewAspect - contentAspect) / contentAspect;
                    android.graphics.Matrix before = new android.graphics.Matrix();
                    textureView.getTransform(before);
                    boolean reset = false;
                    // The outer video rectangle is already sized to the decoded
                    // frame's aspect ratio.  Any remaining TextureView transform
                    // can only zoom/crop that matching rectangle, so clear it.
                    // When the ratios do not yet match, leave Apple's transform
                    // alone until the layout pass reaches final geometry.
                    if (error <= 0.035f && !before.isIdentity()) {
                        textureView.setTransform(new android.graphics.Matrix());
                        reset = true;
                    }
                    float[] values = new float[9];
                    before.getValues(values);
                    String signature = reason + ":" + textureView.getWidth() + "x" + textureView.getHeight()
                            + ":" + Math.round(contentAspect * 1000.0f) + ":" + reset;
                    if (!reset && signature.equals(Controller.this.lastVideoFitSignature)) {
                        return;
                    }
                    Controller.this.lastVideoFitSignature = signature;
                    Log.i(TVLyricsLayout.TAG, "VIDEO_FIT full-frame=true reason=" + reason
                            + " view=" + textureView.getWidth() + "x" + textureView.getHeight()
                            + " contentAspect=" + contentAspect + " error=" + error
                            + " oldScale=" + values[0] + "x" + values[4]
                            + " oldTranslate=" + values[2] + "," + values[5]
                            + " transformReset=" + reset);
                }
            });
        }

        void ensureQueueVideoSurfaceForHome() {
            if (!this.expanded || this.restoring || this.mode != 0 || this.retainedQueueVideoAtHome
                    || this.videoHomeBootstrapPending
                    || (!this.sharedArtworkPinned && !this.sharedVideoActive
                    && this.pendingVideoArtworkKey == null)) {
                return;
            }
            installOpeningCurtain();
            final int generation = this.videoHomeBootstrapGeneration + TVLyricsLayout.MODE_LYRICS;
            this.videoHomeBootstrapGeneration = generation;
            this.videoHomeBootstrapPending = true;
            // Opening QUEUE in the same frame as the native bottom sheet reaches
            // HOME is unsafe for music videos.  Apple's HOME TextureView listener
            // has not initialized its release queue yet; the Fragment replacement
            // then calls onSurfaceTextureDestroyed() and dereferences that null
            // queue.  A real user click happens after the HOME transition settles,
            // so reproduce that ordering before asking QUEUE to own the decoder.
            TVLyricsLayout.MAIN.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Controller.this.openQueueVideoSurfaceForHome(generation);
                }
            }, 1200L);
            Log.i(TVLyricsLayout.TAG, "VIDEO_HOME_BOOTSTRAP requested=true deferredMs=1200 key="
                    + currentArtworkKey().replace('\n', '|'));
        }

        void openQueueVideoSurfaceForHome(final int generation) {
            if (TVLyricsLayout.active != this || generation != this.videoHomeBootstrapGeneration
                    || !this.videoHomeBootstrapPending || !this.expanded || this.restoring || this.mode != 0) {
                return;
            }
            beginSharedPlayerTransition();
            this.mode = TVLyricsLayout.MODE_QUEUE;
            startLyricsAvailabilityWatch();
            TVLyricsLayout.releaseStaleNativePanelTransitionLocks(this.sheet);
            if (!TVLyricsLayout.invokeNativePanelMode(this.sheet, true)) {
                this.videoHomeBootstrapPending = false;
                this.mode = 0;
                startLyricsAvailabilityWatch();
                dismissOpeningCurtain();
                Log.w(TVLyricsLayout.TAG, "VIDEO_HOME_BOOTSTRAP native-entry=false");
                return;
            }
            reapplyRetainedPanelWhenShown(TVLyricsLayout.MODE_QUEUE);
            TVLyricsLayout.MAIN.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (TVLyricsLayout.active != Controller.this || generation != Controller.this.videoHomeBootstrapGeneration
                            || !Controller.this.videoHomeBootstrapPending) {
                        return;
                    }
                    Controller.this.videoHomeBootstrapPending = false;
                    if (Controller.this.mode == TVLyricsLayout.MODE_QUEUE) {
                        Controller.this.enterHome("video-home-bootstrap-timeout");
                    }
                    Controller.this.dismissOpeningCurtain();
                    Log.w(TVLyricsLayout.TAG, "VIDEO_HOME_BOOTSTRAP timeout=true");
                }
            }, 4500L);
            Log.i(TVLyricsLayout.TAG, "VIDEO_HOME_BOOTSTRAP native-entry=true key="
                    + currentArtworkKey().replace('\n', '|'));
        }

        void completeVideoHomeBootstrapIfReady(final View root, TextureView textureView) {
            if (!this.videoHomeBootstrapPending || this.mode != TVLyricsLayout.MODE_QUEUE
                    || !this.sharedVideoActive || !this.sharedVideoFrameReady
                    || !currentArtworkKey().equals(this.sharedVideoKey)
                    || !hasVisibleTextureFrame(textureView)) {
                return;
            }
            final int generation = this.videoHomeBootstrapGeneration;
            this.videoHomeBootstrapPending = false;
            root.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (TVLyricsLayout.active != Controller.this || generation != Controller.this.videoHomeBootstrapGeneration
                            || !Controller.this.expanded || Controller.this.mode != TVLyricsLayout.MODE_QUEUE) {
                        return;
                    }
                    Controller.this.enterHome("video-home-bootstrap-ready");
                    TVLyricsLayout.MAIN.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Controller.this.dismissOpeningCurtain();
                        }
                    }, 420L);
                    Log.i(TVLyricsLayout.TAG, "VIDEO_HOME_BOOTSTRAP completed=true");
                }
            }, 80L);
        }

        float usableBitmapAspect(Bitmap bitmap) {
            if (bitmap == null || bitmap.isRecycled() || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
                return 0.0f;
            }
            float aspect = bitmap.getWidth() / (float) bitmap.getHeight();
            // A square bitmap is the album-art fallback, not a measurement of
            // the decoder output.  Accept landscape/portrait video frames only.
            return aspect > 1.12f || aspect < 0.88f ? aspect : 0.0f;
        }

        float resolveVideoContentAspect(TextureView textureView) {
            String currentKey = currentArtworkKey();
            float aspect = 0.0f;
            String source = "none";
            // During an audio -> video transition the visible proxy deliberately
            // remains the previous track until the decoder has a real frame.
            // Its dimensions therefore must never be used as the new video's
            // aspect ratio.  Prefer the staged current-key landscape frame and
            // only consult proxy/source bitmaps when their key is current.
            if (currentKey.equals(this.pendingVideoArtworkKey)) {
                aspect = usableBitmapAspect(this.pendingVideoArtworkBitmap);
                source = "pending-video";
            }
            if (aspect <= 0.0f && currentKey.equals(this.sharedArtworkProxyKey)) {
                aspect = usableBitmapAspect(this.sharedArtworkProxyBitmap);
                source = "current-proxy";
            }
            if (aspect <= 0.0f && currentKey.equals(this.sharedArtworkProxyKey)) {
                aspect = usableBitmapAspect(this.sharedArtworkSourceBitmap);
                source = "current-native";
            }
            if (aspect <= 0.0f && currentKey.equals(this.sharedVideoKey)
                    && this.sharedVideoAspect > 1.12f) {
                aspect = this.sharedVideoAspect;
                source = "confirmed-video";
            }
            if (aspect <= 0.0f) {
                aspect = 16.0f / 9.0f;
                source = "fallback-16:9";
            }
            String signature = source + ":" + Math.round(aspect * 1000.0f);
            if (!signature.equals(this.lastVideoAspectSignature)) {
                this.lastVideoAspectSignature = signature;
                Log.i(TVLyricsLayout.TAG, "VIDEO_ASPECT source=" + source + " value=" + aspect
                        + " constrainedView=" + (textureView == null ? "null" : textureView.getWidth() + "x" + textureView.getHeight()));
            }
            return aspect;
        }

        void applySharedVideoPresentation(String str) {
            if (this.sharedArtworkSourceCard == null || this.sharedArtworkProxy == null) {
                return;
            }
            // After QUEUE has taken ownership of Apple's decoder Surface, keep
            // that exact native TextureView alive while HOME is shown.  The
            // queue artwork container is animated to HOME geometry; the stale
            // HOME TextureView must stay hidden and must not request a rebind.
            if (this.sharedVideoActive && this.retainedQueueVideoAtHome && isPanelNativeVideoAvailable(this.retainedQueueVideoRoot)) {
                this.sharedArtworkSourceCard.animate().cancel();
                this.sharedArtworkProxy.animate().cancel();
                this.sharedArtworkSourceCard.setAlpha(0.0f);
                this.sharedArtworkProxy.setAlpha(0.0f);
                this.sharedArtworkProxy.setVisibility(View.INVISIBLE);
                if (this.sharedArtworkContainer != null) {
                    this.sharedArtworkContainer.setAlpha(0.0f);
                }
                this.sharedVideoPresented = true;
                return;
            }
            View queueRoot = this.activePanelRoot != null ? this.activePanelRoot : this.panelRoot;
            View queueVideo = queueRoot != null
                    ? queueRoot.findViewById(TVLyricsLayout.ID_VIDEO_SURFACE) : null;
            if (this.sharedVideoActive && this.mode == TVLyricsLayout.MODE_QUEUE
                    && this.sharedVideoConfirmedSurface == queueVideo
                    && showPanelNativeVideoOnly(queueRoot)) {
                this.sharedArtworkSourceCard.animate().cancel();
                this.sharedArtworkProxy.animate().cancel();
                this.sharedArtworkSourceCard.setAlpha(0.0f);
                this.sharedArtworkProxy.setAlpha(0.0f);
                this.sharedArtworkProxy.setVisibility(View.INVISIBLE);
                this.sharedArtworkContainer.setAlpha(0.0f);
                this.sharedVideoPresented = true;
                return;
            }
            if (this.sharedArtworkContainer != null) {
                this.sharedArtworkContainer.setAlpha(1.0f);
            }
            // The same native TextureView remains visible in HOME, LYRICS and
            // QUEUE.  Page changes move/scale its existing outer container;
            // they never replace it with the artwork proxy or bind another
            // video output Surface.
            boolean z = this.sharedVideoActive && this.sharedVideoFrameReady && this.expanded && !this.restoring;
            boolean z2 = z != this.sharedVideoPresented;
            this.sharedVideoPresented = z;
            this.sharedArtworkSourceCard.animate().cancel();
            this.sharedArtworkProxy.animate().cancel();
            this.sharedArtworkSourceCard.setVisibility(View.VISIBLE);
            if (z) {
                this.sharedArtworkProxy.setAlpha(0.0f);
                this.sharedArtworkProxy.setVisibility(View.INVISIBLE);
                this.sharedArtworkSourceCard.setAlpha(1.0f);
                float max = Math.max(1.0f, this.sharedArtworkSourceCard.getWidth());
                float max2 = Math.max(1.0f, this.sharedArtworkSourceCard.getHeight());
                int sharedArtworkSize = sharedArtworkSize(this.sharedAtPanelGeometry);
                // HOME may use the wide native video aspect.  LYRICS/QUEUE are
                // deliberately compact: the video's displayed width may never
                // exceed the old square artwork width, otherwise it invades the
                // right-side lyrics/list column.  Scale uniformly (fit-center),
                // never stretch or crop the stream.
                int round = this.sharedAtPanelGeometry
                        ? sharedArtworkSize
                        : Math.round(this.sheet.getResources().getDisplayMetrics().widthPixels * 0.72f);
                float min = Math.min(sharedArtworkSize / max2, round / max);
                if (!Float.isFinite(min) || min <= 0.0f) {
                    min = 1.0f;
                }
                this.sharedArtworkSourceCard.setPivotX(max * 0.5f);
                this.sharedArtworkSourceCard.setPivotY(max2 * 0.5f);
                this.sharedArtworkSourceCard.setScaleX(min);
                this.sharedArtworkSourceCard.setScaleY(min);
                this.sharedArtworkSourceCard.bringToFront();
            } else {
                this.sharedArtworkSourceCard.setAlpha(0.0f);
                this.sharedArtworkSourceCard.setScaleX(1.0f);
                this.sharedArtworkSourceCard.setScaleY(1.0f);
                this.sharedArtworkProxy.setVisibility(View.VISIBLE);
                this.sharedArtworkProxy.setAlpha(1.0f);
                this.sharedArtworkProxy.bringToFront();
            }
            if (this.sharedArtworkContainer != null) {
                this.sharedArtworkContainer.invalidate();
            }
            if (z2) {
                Log.i(TVLyricsLayout.TAG, "VIDEO_PRESENTATION visible=" + z + " reason=" + str + " mode=" + this.mode);
            }
        }

        void scheduleArtworkUpgradeChecks(final ImageView imageView) {
            final long[] jArr = {80L, 180L, 360L, 720L, 1200L};
            for (final long j : jArr) {
                TVLyricsLayout.MAIN.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Controller.this.sharedArtworkProxy == imageView && imageView.isAttachedToWindow() && Controller.this.sharedPlayerActive) {
                            Controller.this.refreshSharedArtworkProxy(false);
                            Bitmap bitmap = Controller.this.sharedArtworkProxyBitmap;
                            Log.i(TVLyricsLayout.TAG, "ARTWORK_UPGRADE_CHECK delayMs=" + j + " current=" + (bitmap == null ? "none" : bitmap.getWidth() + "x" + bitmap.getHeight()) + " high=" + Controller.this.isHighQualityArtwork(bitmap) + " key=" + Controller.this.sharedArtworkProxyKey);
                        }
                    }
                }, j);
            }
        }

        void refreshSharedArtworkProxy(boolean z) {
            if (this.sharedArtworkProxy == null || this.sharedArtworkSourceCard == null) {
                return;
            }
            String strCurrentArtworkKey = currentArtworkKey();
            if (this.sharedVideoActive && this.sharedVideoFrameReady
                    && strCurrentArtworkKey.equals(this.sharedVideoKey)) {
                // A confirmed video may publish its 48px thumbnail again while
                // the decoder Surface is being moved between HOME and QUEUE.
                // Never let that late thumbnail restart the artwork handoff.
                return;
            }
            Bitmap bitmapCachedArtwork = TVLyricsLayout.getCachedArtwork(strCurrentArtworkKey);
            String artworkRefreshSignature = "cache=" + (bitmapCachedArtwork == null ? "miss" : bitmapCachedArtwork.getWidth() + "x" + bitmapCachedArtwork.getHeight())
                    + " proxy=" + (this.sharedArtworkProxyBitmap == null ? "none" : this.sharedArtworkProxyBitmap.getWidth() + "x" + this.sharedArtworkProxyBitmap.getHeight())
                    + " key=" + strCurrentArtworkKey;
            if (z || !artworkRefreshSignature.equals(this.lastArtworkRefreshSignature)) {
                this.lastArtworkRefreshSignature = artworkRefreshSignature;
                Log.i(TVLyricsLayout.TAG, "ARTWORK_REFRESH forced=" + z + " " + artworkRefreshSignature.replace('\n', '|'));
            }
            if (bitmapCachedArtwork != null && !strCurrentArtworkKey.equals(this.sharedArtworkProxyKey)) {
                if (shouldHoldIncomingVideoArtwork(strCurrentArtworkKey, bitmapCachedArtwork)) {
                    stagePendingVideoArtwork(strCurrentArtworkKey, bitmapCachedArtwork, "memory-cache");
                } else {
                    if (strCurrentArtworkKey.equals(this.pendingVideoArtworkKey)) {
                        clearPendingVideoArtwork("confirmed-audio-cache");
                    }
                    applySharedArtworkBitmap(bitmapCachedArtwork, strCurrentArtworkKey, false, "memory-cache");
                }
            }
            View viewFindViewById = this.sharedArtworkSourceCard.findViewById(TVLyricsLayout.ID_ARTWORK_IMAGE);
            Drawable drawable = viewFindViewById instanceof ImageView ? ((ImageView) viewFindViewById).getDrawable() : null;
            Bitmap bitmapFindLargestArtworkBitmap = findLargestArtworkBitmap(this.sharedArtworkSourceCard, null);
            if (drawable == null && bitmapFindLargestArtworkBitmap == null) {
                return;
            }
            if (!z && drawable == this.sharedArtworkProxySource && bitmapFindLargestArtworkBitmap == this.sharedArtworkSourceBitmap) {
                return;
            }
            this.sharedArtworkProxySource = drawable;
            this.sharedArtworkSourceBitmap = bitmapFindLargestArtworkBitmap;
            if (bitmapFindLargestArtworkBitmap != null && !bitmapFindLargestArtworkBitmap.isRecycled()) {
                // Forced initialization must not downgrade a high-resolution
                // memory-cache hit to the native view's transient thumbnail.
                // With no high-resolution image yet, the thumbnail is allowed
                // and the scheduled checks will replace it as soon as native
                // artwork is upgraded.
                if (strCurrentArtworkKey.equals(this.sharedArtworkProxyKey)
                        && this.sharedArtworkProxyBitmap != null
                        && isHighQualityArtwork(this.sharedArtworkProxyBitmap)
                        && !isHighQualityArtwork(bitmapFindLargestArtworkBitmap)) {
                    Log.i(TVLyricsLayout.TAG, "ARTWORK_PROXY downgrade-blocked=" + bitmapFindLargestArtworkBitmap.getWidth() + "x" + bitmapFindLargestArtworkBitmap.getHeight() + " forced=" + z + " key=" + strCurrentArtworkKey);
                    return;
                }
                if (shouldHoldIncomingVideoArtwork(strCurrentArtworkKey, bitmapFindLargestArtworkBitmap)) {
                    stagePendingVideoArtwork(strCurrentArtworkKey, bitmapFindLargestArtworkBitmap, "native");
                    return;
                }
                if (strCurrentArtworkKey.equals(this.pendingVideoArtworkKey)) {
                    clearPendingVideoArtwork("confirmed-audio-native");
                }
                if (applySharedArtworkBitmap(bitmapFindLargestArtworkBitmap, strCurrentArtworkKey, isHighQualityArtwork(bitmapFindLargestArtworkBitmap), "native")) {
                    return;
                }
            }
            Drawable.ConstantState constantState = drawable != null ? drawable.getConstantState() : null;
            this.sharedArtworkProxy.setImageDrawable(constantState != null ? constantState.newDrawable().mutate() : drawable);
            updateSharedArtworkCropMatrix();
            Log.i(TVLyricsLayout.TAG, "ARTWORK_PROXY refreshed=true forced=" + z);
        }

        String currentArtworkKey() {
            String str = this.sharedTitle instanceof TextView ? String.valueOf(((TextView) this.sharedTitle).getText()) : "";
            String str2 = this.sharedSubtitle instanceof TextView ? String.valueOf(((TextView) this.sharedSubtitle).getText()) : "";
            return str.trim() + "\n" + str2.trim();
        }

        boolean isHighQualityArtwork(Bitmap bitmap) {
            return bitmap != null && !bitmap.isRecycled() && Math.min(bitmap.getWidth(), bitmap.getHeight()) >= 480;
        }

        boolean applySharedArtworkBitmap(Bitmap bitmap, String str, boolean z, String str2) {
            try {
                Bitmap bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, false);
                if (bitmapCopy == null) {
                    return false;
                }
                Bitmap bitmap2 = this.sharedArtworkProxyBitmap;
                this.sharedArtworkProxyBitmap = bitmapCopy;
                this.sharedArtworkProxyKey = str;
                this.sharedArtworkProxy.setImageBitmap(bitmapCopy);
                updateSharedArtworkCropMatrix();
                if (this.fullscreenBackdrop instanceof ImageView) {
                    ((ImageView) this.fullscreenBackdrop).setImageBitmap(bitmapCopy);
                }
                applyTrackBackgroundFromArtwork(bitmapCopy, str, str2);
                if (z) {
                    TVLyricsLayout.putCachedArtwork(str, bitmapCopy);
                }
                if (bitmap2 != null && bitmap2 != bitmapCopy && !bitmap2.isRecycled()) {
                    bitmap2.recycle();
                }
                Log.i(TVLyricsLayout.TAG, "ARTWORK_PROXY applied source=" + str2 + " size=" + bitmapCopy.getWidth() + "x" + bitmapCopy.getHeight() + " cached=" + z + " key=" + str);
                return true;
            } catch (Throwable th) {
                Log.w(TVLyricsLayout.TAG, "ARTWORK_PROXY bitmap-copy-failed=" + th.getClass().getSimpleName());
                return false;
            }
        }

        void updateSharedArtworkCropMatrix() {
            ImageView imageView = this.sharedArtworkProxy;
            if (imageView == null) {
                return;
            }
            Drawable drawable = imageView.getDrawable();
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            int width = layoutParams != null && layoutParams.width > 0 ? layoutParams.width : imageView.getWidth();
            int height = layoutParams != null && layoutParams.height > 0 ? layoutParams.height : imageView.getHeight();
            int intrinsicWidth = drawable != null ? drawable.getIntrinsicWidth() : 0;
            int intrinsicHeight = drawable != null ? drawable.getIntrinsicHeight() : 0;
            if (width <= 0 || height <= 0 || intrinsicWidth <= 0 || intrinsicHeight <= 0) {
                return;
            }
            // Apply the crop in the same frame as the target LayoutParams. The
            // old posted update left one frame using the previous 532/600 matrix,
            // which looked like a jump after an otherwise completed animation.
            float max = Math.max(width / ((float) intrinsicWidth), height / ((float) intrinsicHeight)) * 1.12f;
            float f = (width - (intrinsicWidth * max)) * 0.5f;
            float f2 = (height - (intrinsicHeight * max)) * 0.5f;
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            matrix.setScale(max, max);
            matrix.postTranslate(f, f2);
            imageView.setScaleType(ImageView.ScaleType.MATRIX);
            imageView.setImageMatrix(matrix);
            String artworkMatrixSignature = width + "x" + height + ":" + intrinsicWidth + "x" + intrinsicHeight;
            if (!artworkMatrixSignature.equals(this.lastArtworkMatrixSignature)) {
                this.lastArtworkMatrixSignature = artworkMatrixSignature;
                Log.i(TVLyricsLayout.TAG, "ARTWORK_MATRIX sync=" + width + "x" + height + " source=" + intrinsicWidth + "x" + intrinsicHeight);
            }
        }

        Bitmap findLargestArtworkBitmap(View view, Bitmap bitmap) {
            if (view instanceof ImageView) {
                Drawable drawable = ((ImageView) view).getDrawable();
                if (drawable instanceof BitmapDrawable) {
                    Bitmap bitmap2 = ((BitmapDrawable) drawable).getBitmap();
                    if (bitmap2 != null && !bitmap2.isRecycled()) {
                        boolean z = isUsableSquareArtwork(bitmap2);
                        boolean z2 = bitmap != null && isUsableSquareArtwork(bitmap);
                        if (bitmap == null || (z && !z2) || (z == z2 && bitmap2.getWidth() * bitmap2.getHeight() > bitmap.getWidth() * bitmap.getHeight())) {
                            bitmap = bitmap2;
                        }
                    }
                }
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i += TVLyricsLayout.MODE_LYRICS) {
                    bitmap = findLargestArtworkBitmap(viewGroup.getChildAt(i), bitmap);
                }
            }
            return bitmap;
        }

        boolean isUsableSquareArtwork(Bitmap bitmap) {
            if (bitmap == null || bitmap.isRecycled()) {
                return false;
            }
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            return Math.min(width, height) >= 256 && Math.abs(width - height) <= Math.max(width, height) * 0.15f;
        }

        void assignSharedHomeTargets() {
            int i = this.sheet.getResources().getDisplayMetrics().widthPixels;
            int i2 = this.sheet.getResources().getDisplayMetrics().heightPixels;

            // Windows Apple Music HOME is a centered, self-contained player.
            // Its narrow 640 px control column is preserved here.  Android has
            // an extra lyrics/route/queue row, so the 560 px artwork variant
            // keeps every native control available without overlap on 1080p.
            float f = i / 1920.0f;
            float f2 = i2 / 1080.0f;
            int i3 = Math.round(640.0f * f);
            int i4 = (i - i3) / MODE_QUEUE;
            for (SharedItem sharedItem : this.sharedItems) {
                if (sharedItem.view == this.sharedArtworkContainer) {
                    int i7 = Math.round(600.0f * f);
                    sharedItem.setHome((i - i7) / MODE_QUEUE, Math.round(20.0f * f2), i7, Math.round(600.0f * f2));
                } else if (sharedItem.view == this.sharedTitle) {
                    sharedItem.setHome(i4 + Math.round(64.0f * f), Math.round(627.0f * f2), Math.round(512.0f * f), Math.round(51.0f * f2));
                } else if (sharedItem.view == this.sharedSubtitle) {
                    sharedItem.setHome(i4 + Math.round(64.0f * f), Math.round(678.0f * f2), Math.round(512.0f * f), Math.round(51.0f * f2));
                } else if (sharedItem.view == this.sharedFavorite) {
                    sharedItem.setHome(i - Math.round(208.0f * f), Math.round(32.0f * f2), Math.round(88.0f * f), Math.round(88.0f * f2));
                } else if (sharedItem.view == this.sharedMore) {
                    sharedItem.setHome(i - Math.round(120.0f * f), Math.round(32.0f * f2), Math.round(104.0f * f), Math.round(88.0f * f2));
                } else if (sharedItem.view == this.sharedControls) {
                    sharedItem.setHome(i4, Math.round(729.0f * f2), i3, Math.round(351.0f * f2));
                }
            }
        }

        void updateSharedHomeTargetsForVideo() {
            if (!this.sharedVideoActive || this.sharedItems.isEmpty()) {
                return;
            }
            int screenWidth = this.sheet.getResources().getDisplayMetrics().widthPixels;
            int screenHeight = this.sheet.getResources().getDisplayMetrics().heightPixels;
            float widthScale = screenWidth / 1920.0f;
            float heightScale = screenHeight / 1080.0f;
            int controlWidth = Math.round(640.0f * widthScale);
            int controlX = (screenWidth - controlWidth) / TVLyricsLayout.MODE_QUEUE;
            int videoY = Math.round(20.0f * heightScale);
            int videoHeight = Math.round(600.0f * heightScale);
            float aspect = this.sharedVideoAspect > 0.0f ? this.sharedVideoAspect : 16.0f / 9.0f;
            int videoWidth = Math.min(Math.round(screenWidth * 0.72f), Math.round(videoHeight * aspect));
            int videoX = (screenWidth - videoWidth) / TVLyricsLayout.MODE_QUEUE;
            int titleY = videoY + videoHeight + Math.round(7.0f * heightScale);
            int subtitleY = titleY + Math.round(51.0f * heightScale);
            int controlsY = subtitleY + Math.round(51.0f * heightScale);
            for (SharedItem sharedItem : this.sharedItems) {
                if (sharedItem.view == this.sharedArtworkContainer) {
                    sharedItem.setHome(videoX, videoY, videoWidth, videoHeight);
                } else if (sharedItem.view == this.sharedTitle) {
                    sharedItem.setHome(controlX + Math.round(64.0f * widthScale), titleY, Math.round(512.0f * widthScale), Math.round(51.0f * heightScale));
                } else if (sharedItem.view == this.sharedSubtitle) {
                    sharedItem.setHome(controlX + Math.round(64.0f * widthScale), subtitleY, Math.round(512.0f * widthScale), Math.round(51.0f * heightScale));
                } else if (sharedItem.view == this.sharedControls) {
                    sharedItem.setHome(controlX, controlsY, controlWidth, Math.max(TVLyricsLayout.MODE_LYRICS, screenHeight - controlsY));
                }
            }
            Log.i(TVLyricsLayout.TAG, "VIDEO_HOME_TARGET rect=" + videoX + "," + videoY + " " + videoWidth + "x" + videoHeight
                    + " titleY=" + titleY + " aspect=" + aspect);
        }

        View findNativeHomePage() {
            View viewFindViewById = this.windowRoot.findViewById(TVLyricsLayout.ID_PLAYER_FRAGMENTS_HOST);
            if (!(viewFindViewById instanceof ViewGroup)) {
                return null;
            }
            ViewGroup viewGroup = (ViewGroup) viewFindViewById;
            for (int childCount = viewGroup.getChildCount() - TVLyricsLayout.MODE_LYRICS; childCount >= 0; childCount += TVLyricsLayout.UNSET) {
                View childAt = viewGroup.getChildAt(childCount);
                if (childAt.getId() == TVLyricsLayout.ID_NATIVE_HOME_PLAYER && childAt.isAttachedToWindow()) {
                    return childAt;
                }
            }
            return null;
        }

        void addSharedItem(View view) {
            if (view == null || this.sharedLayer == null || view.getWidth() <= 0 || view.getHeight() <= 0) {
                return;
            }
            if (!this.sharedSeen.containsKey(view)) {
                captureSharedOnly(view);
            }
            int[] iArr = new int[TVLyricsLayout.MODE_QUEUE];
            int[] iArr2 = new int[TVLyricsLayout.MODE_QUEUE];
            view.getLocationInWindow(iArr);
            this.sharedLayer.getLocationInWindow(iArr2);
            SharedItem sharedItem = new SharedItem(view, iArr[0] - iArr2[0], iArr[TVLyricsLayout.MODE_LYRICS] - iArr2[TVLyricsLayout.MODE_LYRICS], view.getWidth(), view.getHeight());
            this.sharedItems.add(sharedItem);
            Log.i(TVLyricsLayout.TAG, "TEXTURE_REPARENT native-listener-preserved=true view=" + view.getClass().getSimpleName());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(sharedItem.originalWidth, sharedItem.originalHeight);
            layoutParams.leftMargin = sharedItem.originalX;
            layoutParams.topMargin = sharedItem.originalY;
            ArrayList<TextureListenerState> textureListeners = new ArrayList<>();
            TVLyricsLayout.guardTextureListenersForReparent(view, textureListeners);
            try {
                ViewParent parent = view.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(view);
                }
                this.sharedLayer.addView(view, layoutParams);
            } finally {
                TVLyricsLayout.restoreTextureListeners(textureListeners);
            }
            view.setPivotX(0.0f);
            view.setPivotY(0.0f);
            view.setTranslationX(0.0f);
            view.setTranslationY(0.0f);
        }

        void captureSharedOnly(View view) {
            if (view == null || this.sharedSeen.containsKey(view)) {
                return;
            }
            this.sharedSeen.put(view, Boolean.TRUE);
            this.sharedSnapshots.add(new ViewSnapshot(view));
        }

        void assignSharedPanelTargets() {
            int i = this.sheet.getResources().getDisplayMetrics().widthPixels;
            int i2 = this.sheet.getResources().getDisplayMetrics().heightPixels;

            // The left player in LYRICS/QUEUE uses the measured geometry of the
            // untouched Android side-player HOME page.  That native pane is a
            // 640 x 1080 design surface on the BRAVIA.  Keep its internal rhythm
            // intact instead of independently guessing artwork/text/control
            // margins.  Center it on the existing left-column centre so the
            // right-side lyrics/queue architecture does not move.
            int i3 = i / 8;
            int i4 = (i * 5) / 16;
            int i5 = i / 3;
            int i6 = (i3 + (i4 / MODE_QUEUE)) - (i5 / MODE_QUEUE);
            float f = i5 / 640.0f;
            float f2 = i2 / 1080.0f;
            for (SharedItem sharedItem : this.sharedItems) {
                if (sharedItem.view == this.sharedArtworkContainer) {
                    sharedItem.setTarget(i6 + Math.round(54.0f * f), Math.round(64.0f * f2), Math.round(532.0f * f), Math.round(531.0f * f2));
                } else if (sharedItem.view == this.sharedTitle) {
                    sharedItem.setTarget(i6 + Math.round(64.0f * f), Math.round(595.0f * f2), Math.round(352.0f * f), Math.round(51.0f * f2));
                } else if (sharedItem.view == this.sharedSubtitle) {
                    sharedItem.setTarget(i6 + Math.round(64.0f * f), Math.round(646.0f * f2), Math.round(352.0f * f), Math.round(51.0f * f2));
                } else if (sharedItem.view == this.sharedFavorite) {
                    sharedItem.setTarget(i6 + Math.round(416.0f * f), Math.round(595.0f * f2), Math.round(88.0f * f), Math.round(102.0f * f2));
                } else if (sharedItem.view == this.sharedMore) {
                    sharedItem.setTarget(i6 + Math.round(504.0f * f), Math.round(595.0f * f2), Math.round(104.0f * f), Math.round(102.0f * f2));
                } else if (sharedItem.view == this.sharedControls) {
                    sharedItem.setTarget(i6, Math.round(729.0f * f2), i5, Math.round(351.0f * f2));
                }
            }
        }

        void installSharedDragDismissOverlay() {
            // DragGestureHost observes the whole upper player surface and only
            // intercepts after a deliberate downward motion. A cover-sized
            // overlay made the gesture incorrectly depend on the artwork.
            this.dragDismissOverlay = null;
            Log.i(TVLyricsLayout.TAG, "DRAG_SURFACE installed=upper-fullscreen");
        }

        void updateSharedDismissDrag(float f) {
            if (!this.sharedPlayerActive || f < 0.0f) {
                return;
            }
            applyMiniTrajectoryProgress(Math.min(0.92f, f / Math.max(1.0f, this.sheet.getHeight() * 0.55f)));
        }

        void finishSharedDismissDrag(boolean z) {
            if (!this.sharedPlayerActive) {
                return;
            }
            if (z) {
                final int i = this.sharedAnimationGeneration + TVLyricsLayout.MODE_LYRICS;
                this.sharedAnimationGeneration = i;
                this.sharedPlayerAnimating = true;
                setSharedModeVisual(0);
                prepareFullscreenChromeForClose();
                int widthPixels = this.sheet.getResources().getDisplayMetrics().widthPixels;
                int heightPixels = this.sheet.getResources().getDisplayMetrics().heightPixels;
                float f = widthPixels / 1920.0f;
                float f2 = heightPixels / 1080.0f;
                float f3 = Math.round(82.0f * Math.min(f, f2));
                float f4 = Math.round(1312.0f * f);
                float f5 = Math.round(983.0f * f2);
                float f6 = Math.round(1410.0f * f);
                float f7 = Math.round(1005.0f * f2);
                // Match the native BottomSheet settle: the finger-driven part is
                // linear, then the remaining distance decelerates immediately.
                PathInterpolator closeInterpolator = new PathInterpolator(0.2f, 0.0f, 0.0f, 1.0f);
                long jMax = 0L;
                for (SharedItem sharedItem : this.sharedItems) {
                    sharedItem.view.animate().cancel();
                    sharedItem.view.setPivotX(0.0f);
                    sharedItem.view.setPivotY(0.0f);
                    long jMax2 = Math.max(60L, Math.round((sharedItem.view == this.sharedArtworkContainer ? 260.0f : 220.0f) * (1.0f - this.dismissProgress)));
                    jMax = Math.max(jMax, jMax2);
                    if (sharedItem.view == this.sharedArtworkContainer && this.sharedArtworkCard != null) {
                        int i2 = Math.max(TVLyricsLayout.MODE_LYRICS, this.sharedArtworkCard.getWidth());
                        float f8 = f3 / i2;
                        float f9 = Math.max(0.0f, (sharedItem.view.getWidth() - i2) / 2.0f);
                        float f10 = Math.max(0.0f, (sharedItem.view.getHeight() - this.sharedArtworkCard.getHeight()) / 2.0f);
                        sharedItem.view.animate().x(sharedItemX(sharedItem, f4 - (f9 * f8))).y(sharedItemY(sharedItem, f5 - (f10 * f8))).scaleX(f8).scaleY(f8).alpha(1.0f).setInterpolator(closeInterpolator).setDuration(jMax2).start();
                    } else if (sharedItem.view == this.sharedTitle || sharedItem.view == this.sharedSubtitle) {
                        sharedItem.view.animate().x(f6).y(f7 + (sharedItem.view == this.sharedSubtitle ? Math.round(26.0f * f2) : 0)).scaleX(0.72f).scaleY(0.72f).alpha(0.0f).setInterpolator(closeInterpolator).setDuration(jMax2).start();
                    } else {
                        sharedItem.view.animate().x(sharedItem.dismissStartX).y(sharedItem.dismissStartY + TVLyricsLayout.dp(sharedItem.view, 54)).scaleX(0.92f).scaleY(0.92f).alpha(0.0f).setInterpolator(closeInterpolator).setDuration(jMax2).start();
                    }
                }
                if (this.activePanelRoot != null) {
                    this.activePanelRoot.animate().y(this.panelDismissStartY + TVLyricsLayout.dp(this.activePanelRoot, 70)).alpha(0.0f).setInterpolator(closeInterpolator).setDuration(Math.max(60L, Math.round(220.0f * (1.0f - this.dismissProgress)))).start();
                }
                final View view = this.sheet;
                TVLyricsLayout.releaseStaleNativePanelTransitionLocks(view);
                animateCloseChromeProgress(true, jMax);
                Log.i(TVLyricsLayout.TAG, "DRAG_DISMISS concurrent-native=true home-reset=" + TVLyricsLayout.invokeNativeHomeMode(view));
                TVLyricsLayout.collapseNativePlayerSheet(view);
                Log.i(TVLyricsLayout.TAG, "DRAG_DISMISS continued-to-mini=true progress=" + this.dismissProgress + " remainingMs=" + jMax);
                return;
            }
            if (this.fullscreenBackdrop != null) {
                this.fullscreenBackdrop.animate().cancel();
            }
            PathInterpolator pathInterpolator = new PathInterpolator(0.22f, 1.0f, 0.36f, 1.0f);
            long j = Math.max(80L, Math.round(420.0f * this.dismissProgress));
            for (SharedItem sharedItem2 : this.sharedItems) {
                sharedItem2.view.animate().x(sharedItem2.dismissStartX).y(sharedItem2.dismissStartY).scaleX(sharedItem2.dismissStartScaleX).scaleY(sharedItem2.dismissStartScaleY).alpha(sharedItem2.dismissStartAlpha).setInterpolator(pathInterpolator).setDuration(sharedItem2.view == this.sharedArtworkContainer ? Math.max(80L, Math.round(500.0f * this.dismissProgress)) : j).start();
            }
            if (this.activePanelRoot != null) {
                this.activePanelRoot.animate().y(this.panelDismissStartY).alpha(this.panelDismissStartAlpha).setInterpolator(pathInterpolator).setDuration(j).start();
            }
            animateCloseChromeProgress(false, j);
            this.dismissProgress = 0.0f;
            this.sharedPlayerAnimating = false;
        }

        void beginSharedDismissDrag() {
            if (!this.sharedPlayerActive) {
                return;
            }
            this.sharedAnimationGeneration += TVLyricsLayout.MODE_LYRICS;
            this.sharedPlayerAnimating = true;
            this.closeChromePreviewVisible = false;
            if (this.fullscreenBackdrop != null) {
                this.fullscreenBackdrop.animate().cancel();
                this.fullscreenBackdrop.setAlpha(1.0f);
            }
            for (SharedItem sharedItem : this.sharedItems) {
                sharedItem.view.animate().cancel();
                sharedItem.dismissStartX = sharedItem.view.getX();
                sharedItem.dismissStartY = sharedItem.view.getY();
                sharedItem.dismissStartScaleX = sharedItem.view.getScaleX();
                sharedItem.dismissStartScaleY = sharedItem.view.getScaleY();
                sharedItem.dismissStartAlpha = sharedItem.view.getAlpha();
            }
            if (this.activePanelRoot != null) {
                this.activePanelRoot.animate().cancel();
                this.panelDismissStartY = this.activePanelRoot.getY();
                this.panelDismissStartAlpha = this.activePanelRoot.getAlpha();
            }
            this.dismissProgress = 0.0f;
            Log.i(TVLyricsLayout.TAG, "DRAG_TRAJECTORY captured=true mode=" + this.mode);
        }

        void applyMiniTrajectoryProgress(float f) {
            int widthPixels = this.sheet.getResources().getDisplayMetrics().widthPixels;
            int heightPixels = this.sheet.getResources().getDisplayMetrics().heightPixels;
            float f2 = widthPixels / 1920.0f;
            float f3 = heightPixels / 1080.0f;
            float f4 = Math.round(82.0f * Math.min(f2, f3));
            float f5 = Math.round(1312.0f * f2);
            float f6 = Math.round(983.0f * f3);
            float f7 = Math.round(1410.0f * f2);
            float f8 = Math.round(1005.0f * f3);
            float f9 = Math.max(0.0f, Math.min(1.0f, f));
            for (SharedItem sharedItem : this.sharedItems) {
                float f10;
                float f11;
                float f12;
                float f13;
                if (sharedItem.view == this.sharedArtworkContainer && this.sharedArtworkCard != null) {
                    int i = Math.max(TVLyricsLayout.MODE_LYRICS, this.sharedArtworkCard.getWidth());
                    float f14 = f4 / i;
                    float f15 = Math.max(0.0f, (sharedItem.view.getWidth() - i) / 2.0f);
                    float f16 = Math.max(0.0f, (sharedItem.view.getHeight() - this.sharedArtworkCard.getHeight()) / 2.0f);
                    f10 = sharedItemX(sharedItem, f5 - (f15 * f14));
                    f11 = sharedItemY(sharedItem, f6 - (f16 * f14));
                    f12 = f14;
                    f13 = 1.0f;
                } else if (sharedItem.view == this.sharedTitle || sharedItem.view == this.sharedSubtitle) {
                    f10 = f7;
                    f11 = f8 + (sharedItem.view == this.sharedSubtitle ? Math.round(26.0f * f3) : 0);
                    f12 = 0.72f;
                    f13 = 0.0f;
                } else {
                    f10 = sharedItem.dismissStartX;
                    f11 = sharedItem.dismissStartY + TVLyricsLayout.dp(sharedItem.view, 54);
                    f12 = 0.92f;
                    f13 = 0.0f;
                }
                sharedItem.view.setX(sharedItem.dismissStartX + ((f10 - sharedItem.dismissStartX) * f9));
                sharedItem.view.setY(sharedItem.dismissStartY + ((f11 - sharedItem.dismissStartY) * f9));
                sharedItem.view.setScaleX(sharedItem.dismissStartScaleX + ((f12 - sharedItem.dismissStartScaleX) * f9));
                sharedItem.view.setScaleY(sharedItem.dismissStartScaleY + ((f12 - sharedItem.dismissStartScaleY) * f9));
                sharedItem.view.setAlpha(sharedItem.dismissStartAlpha + ((f13 - sharedItem.dismissStartAlpha) * f9));
            }
            if (this.activePanelRoot != null) {
                this.activePanelRoot.setY(this.panelDismissStartY + (TVLyricsLayout.dp(this.activePanelRoot, 70) * f9));
                this.activePanelRoot.setAlpha(this.panelDismissStartAlpha * (1.0f - f9));
            }
            applyCloseChromeProgress(f9);
            this.dismissProgress = f9;
        }

        void closeNativeSheetFromDrag() {
            this.sharedAnimationGeneration += TVLyricsLayout.MODE_LYRICS;
            this.sharedPlayerAnimating = false;
            final View view = this.sheet;
            TVLyricsLayout.releaseStaleNativePanelTransitionLocks(view);
            Log.i(TVLyricsLayout.TAG, "DRAG_CLOSE immediate-native=true home-reset=" + TVLyricsLayout.invokeNativeHomeMode(view) + " mode=" + this.mode);
            TVLyricsLayout.collapseNativePlayerSheet(view);
        }

        void startUnifiedCloseAnimation(String str) {
            if (!this.sharedPlayerActive || this.sharedPlayerAnimating) {
                return;
            }
                this.sharedAnimationGeneration += MODE_LYRICS;
                this.sharedPlayerAnimating = true;
                setSharedModeVisual(0);
                prepareFullscreenChromeForClose();
                int i = this.sheet.getResources().getDisplayMetrics().widthPixels;
                int i2 = this.sheet.getResources().getDisplayMetrics().heightPixels;
                float f = i / 1920.0f;
                float f2 = i2 / 1080.0f;
                float f3 = Math.round(82.0f * Math.min(f, f2));
                float f4 = Math.round(1312.0f * f);
                float f5 = Math.round(983.0f * f2);
                float f6 = Math.round(1410.0f * f);
                float f7 = Math.round(1005.0f * f2);
                PathInterpolator pathInterpolator = new PathInterpolator(0.64f, 0.0f, 0.78f, 0.0f);
                for (SharedItem sharedItem : this.sharedItems) {
                    sharedItem.view.animate().cancel();
                    sharedItem.view.setPivotX(0.0f);
                    sharedItem.view.setPivotY(0.0f);
                    if (sharedItem.view == this.sharedArtworkContainer && this.sharedArtworkCard != null) {
                        int i3 = Math.max(MODE_LYRICS, this.sharedArtworkCard.getWidth());
                        float f8 = f3 / i3;
                        float f9 = Math.max(0.0f, (sharedItem.view.getWidth() - i3) / 2.0f);
                        float f10 = Math.max(0.0f, (sharedItem.view.getHeight() - this.sharedArtworkCard.getHeight()) / 2.0f);
                        sharedItem.view.animate().x(sharedItemX(sharedItem, f4 - (f9 * f8))).y(sharedItemY(sharedItem, f5 - (f10 * f8))).scaleX(f8).scaleY(f8).alpha(1.0f).setInterpolator(pathInterpolator).setDuration(440L).start();
                    } else if (sharedItem.view == this.sharedTitle || sharedItem.view == this.sharedSubtitle) {
                        sharedItem.view.animate().x(f6).y(f7 + (sharedItem.view == this.sharedSubtitle ? Math.round(26.0f * f2) : 0)).scaleX(0.72f).scaleY(0.72f).alpha(0.0f).setInterpolator(pathInterpolator).setDuration(380L).start();
                    } else {
                        sharedItem.view.animate().translationY(TVLyricsLayout.dp(sharedItem.view, 54)).scaleX(0.92f).scaleY(0.92f).alpha(0.0f).setInterpolator(pathInterpolator).setDuration(300L).start();
                    }
                }
                if (this.activePanelRoot != null) {
                    this.activePanelRoot.animate().translationY(TVLyricsLayout.dp(this.activePanelRoot, 70)).alpha(0.0f).setInterpolator(pathInterpolator).setDuration(300L).start();
                }
                final View view = this.sheet;
                TVLyricsLayout.releaseStaleNativePanelTransitionLocks(view);
                TVLyricsLayout.MAIN.postDelayed(new Runnable() {
                    @Override // java.lang.Runnable
                    public void run() {
                        Log.i(TVLyricsLayout.TAG, "DRAG_DISMISS native-home-reset=" + TVLyricsLayout.invokeNativeHomeMode(view));
                        TVLyricsLayout.collapseNativePlayerSheet(view);
                    }
                }, 450L);
                Log.i(TVLyricsLayout.TAG, "UNIFIED_CLOSE requested=true reason=" + str + " mode=" + this.mode + " symmetric-mini-target=true");
        }

        void animateSharedPlayerToPanel() {
            if (!this.sharedPlayerActive || this.sharedLayer == null || this.sharedItems.isEmpty()) {
                return;
            }
            if (this.sharedAtPanelGeometry) {
                setSharedModeVisual(this.mode);
                this.sharedLayer.bringToFront();
                return;
            }
            final int i = this.sharedAnimationGeneration + TVLyricsLayout.MODE_LYRICS;
            this.sharedAnimationGeneration = i;
            this.sharedPlayerAnimating = true;
            setSharedModeVisual(this.mode);
            android.animation.TimeInterpolator linearInterpolator = new android.view.animation.LinearInterpolator();
            PathInterpolator pathInterpolator = new PathInterpolator(0.20f, 0.0f, 0.20f, 1.0f);
            final boolean z = beginSharedArtworkRectTransition(true, linearInterpolator, 360L);
            for (SharedItem sharedItem : this.sharedItems) {
                if (sharedItem.view == this.sharedTitle || sharedItem.view == this.sharedSubtitle) {
                    continue;
                }
                sharedItem.view.animate().cancel();
                sharedItem.view.clearAnimation();
                sharedItem.view.setPivotX(0.0f);
                sharedItem.view.setPivotY(0.0f);
                float f = sharedItem.targetX;
                float f2 = sharedItem.targetY;
                float f3 = sharedItem.targetWidth / Math.max(TVLyricsLayout.MODE_LYRICS, sharedItem.view.getWidth());
                float f4 = sharedItem.targetHeight / Math.max(TVLyricsLayout.MODE_LYRICS, sharedItem.view.getHeight());
                if (z && sharedItem.view == this.sharedArtworkContainer) {
                    continue;
                }
                if (sharedItem.view == this.sharedArtworkContainer && this.sharedArtworkCard != null) {
                    int i2 = Math.max(MODE_LYRICS, this.sharedArtworkCard.getWidth());
                    int i3 = sharedArtworkSize(true);
                    float f5 = i3 / (float) i2;
                    float f6 = Math.max(0.0f, (sharedItem.view.getWidth() - i2) / 2.0f);
                    float f7 = Math.max(0.0f, (sharedItem.view.getHeight() - this.sharedArtworkCard.getHeight()) / 2.0f);
                    float f8 = sharedItem.targetX + ((sharedItem.targetWidth - i3) / 2.0f);
                    float f9 = sharedItem.targetY + ((sharedItem.targetHeight - i3) / 2.0f);
                    float f10 = sharedItem.view.getWidth() * 0.5f;
                    float f11 = sharedItem.view.getHeight() * 0.5f;
                    sharedItem.view.setPivotX(f10);
                    sharedItem.view.setPivotY(f11);
                    f = sharedItemX(sharedItem, f8 - (f10 * (1.0f - f5)) - (f6 * f5));
                    f2 = sharedItemY(sharedItem, f9 - (f11 * (1.0f - f5)) - (f7 * f5));
                    f3 = f5;
                    f4 = f5;
                }
                sharedItem.view.animate().x(f).y(f2).scaleX(f3).scaleY(f4).setInterpolator(pathInterpolator).setDuration(360L).start();
            }
            crossfadeSharedText(true, i);
            this.sharedLayer.postDelayed(new Runnable() {
                @Override // java.lang.Runnable
                public void run() {
                    if (TVLyricsLayout.active != Controller.this || !Controller.this.sharedPlayerActive || i != Controller.this.sharedAnimationGeneration) {
                        return;
                    }
                    if (z) {
                        Controller.this.finishSharedArtworkRectTransition(true);
                    }
                    Controller.this.sharedAtPanelGeometry = true;
                    Controller.this.commitSharedPanelGeometry();
                    Controller.this.sharedPlayerAnimating = false;
                    Log.i(TVLyricsLayout.TAG, "SHARED_PLAYER panel-geometry-committed=true mode=" + Controller.this.mode);
                }
            }, 370L);
        }

        void commitSharedPanelGeometry() {
            for (SharedItem sharedItem : this.sharedItems) {
                sharedItem.view.animate().cancel();
                commitSharedItemBounds(sharedItem, sharedItem.targetX, sharedItem.targetY, sharedItem.targetWidth, sharedItem.targetHeight);
            }
            if (this.sharedTitle instanceof TextView) {
                ((TextView) this.sharedTitle).setTextSize(TVLyricsLayout.MODE_QUEUE, 16.0f);
                ((TextView) this.sharedTitle).setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            }
            if (this.sharedSubtitle instanceof TextView) {
                ((TextView) this.sharedSubtitle).setTextSize(TVLyricsLayout.MODE_QUEUE, 16.0f);
                ((TextView) this.sharedSubtitle).setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            }
            configureControlGeometry(this.sharedControls);
            stabilizeSharedArtworkCard(true);
            updateSharedDragOverlayGeometry(true);
            updateSharedLayerTouchWidth(true);
            this.sharedLayer.requestLayout();
            this.sharedLayer.bringToFront();
            applySharedVideoPresentation("panel-geometry-commit");
        }

        void updateSharedLayerTouchWidth(boolean z) {
            if (this.sharedLayer == null) {
                return;
            }
            ViewGroup.LayoutParams layoutParams = this.sharedLayer.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(TVLyricsLayout.UNSET, TVLyricsLayout.UNSET);
            }
            // In panel modes the native lyrics/queue content starts at x=50%.
            // Do not put the drag host above that half: the RecyclerView must
            // receive its own scroll gestures. HOME still uses the full surface.
            layoutParams.width = z ? this.sheet.getResources().getDisplayMetrics().widthPixels / 2 : TVLyricsLayout.UNSET;
            layoutParams.height = TVLyricsLayout.UNSET;
            this.sharedLayer.setLayoutParams(layoutParams);
            Log.i(TVLyricsLayout.TAG, "DRAG_SURFACE panel-width=" + layoutParams.width + " panel=" + z);
        }

        void prepareSharedTextForPanel(TextView textView, SharedItem sharedItem) {
            float width = textView.getWidth();
            float height = textView.getHeight();
            float centerX = textView.getX() + (width * 0.5f);
            float centerY = textView.getY() + (height * 0.5f);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(sharedItem.targetWidth, sharedItem.targetHeight);
            layoutParams.leftMargin = sharedItem.targetX;
            layoutParams.topMargin = sharedItem.targetY;
            textView.setLayoutParams(layoutParams);
            textView.setTextSize(TVLyricsLayout.MODE_QUEUE, 16.0f);
            textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            float scale = 1.25f;
            float measuredText = textView.getPaint().measureText(String.valueOf(textView.getText()));
            float contentCenter = textView.getCompoundPaddingLeft() + (measuredText * 0.5f);
            textView.setX(centerX - (contentCenter * scale));
            textView.setY(centerY - ((sharedItem.targetHeight * scale) * 0.5f));
            textView.setScaleX(scale);
            textView.setScaleY(scale);
        }

        SharedItem findSharedArtworkItem() {
            for (SharedItem sharedItem : this.sharedItems) {
                if (sharedItem.view == this.sharedArtworkContainer) {
                    return sharedItem;
                }
            }
            return null;
        }

        int sharedArtworkSize(boolean z) {
            int widthPixels = this.sheet.getResources().getDisplayMetrics().widthPixels / 3;
            int heightPixels = this.sheet.getResources().getDisplayMetrics().heightPixels;
            return Math.round((z ? 532.0f : 600.0f) * Math.min(widthPixels / 640.0f, heightPixels / 1080.0f));
        }

        boolean beginSharedArtworkRectTransition(boolean z, android.animation.TimeInterpolator timeInterpolator, long j) {
            // HOME -> panel uses the direct-container path introduced in v5.3s.
            // Panel -> HOME deliberately keeps the previously approved v5.3o
            // artwork path; the two directions do not need to share an engine.
            if (z || this.sharedVideoActive || this.sharedArtworkPinned) {
                Log.i(TVLyricsLayout.TAG, "ARTWORK_RECT direct-container=true panel=true");
                return false;
            }
            SharedItem sharedItem = findSharedArtworkItem();
            if (sharedItem == null || this.sharedArtworkProxy == null || this.sharedLayer == null || this.sharedArtworkProxy.getParent() != this.sharedArtworkContainer) {
                return false;
            }
            int[] iArr = new int[TVLyricsLayout.MODE_QUEUE];
            int[] iArr2 = new int[TVLyricsLayout.MODE_QUEUE];
            this.sharedArtworkProxy.getLocationInWindow(iArr);
            this.sharedLayer.getLocationInWindow(iArr2);
            int width = Math.max(TVLyricsLayout.MODE_LYRICS, this.sharedArtworkProxy.getWidth());
            int height = Math.max(TVLyricsLayout.MODE_LYRICS, this.sharedArtworkProxy.getHeight());
            int i = iArr[0] - iArr2[0];
            int i2 = iArr[TVLyricsLayout.MODE_LYRICS] - iArr2[TVLyricsLayout.MODE_LYRICS];
            int i3 = sharedItem.homeX;
            int i4 = sharedItem.homeY;
            int i5 = sharedItem.homeWidth;
            int i6 = sharedItem.homeHeight;
            int i7 = sharedArtworkSize(false);
            ((ViewGroup) this.sharedArtworkContainer).removeView(this.sharedArtworkProxy);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
            layoutParams.leftMargin = i;
            layoutParams.topMargin = i2;
            this.sharedLayer.addView(this.sharedArtworkProxy, layoutParams);
            this.sharedArtworkProxy.setPivotX(0.0f);
            this.sharedArtworkProxy.setPivotY(0.0f);
            this.sharedArtworkProxy.setX(i);
            this.sharedArtworkProxy.setY(i2);
            this.sharedArtworkProxy.setScaleX(1.0f);
            this.sharedArtworkProxy.setScaleY(1.0f);
            FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(i5, i6);
            layoutParams2.leftMargin = i3;
            layoutParams2.topMargin = i4;
            this.sharedArtworkContainer.setLayoutParams(layoutParams2);
            this.sharedArtworkContainer.setTranslationX(0.0f);
            this.sharedArtworkContainer.setTranslationY(0.0f);
            this.sharedArtworkContainer.setScaleX(1.0f);
            this.sharedArtworkContainer.setScaleY(1.0f);
            float f = i3 + ((i5 - i7) * 0.5f);
            float f2 = i4 + ((i6 - i7) * 0.5f);
            this.sharedArtworkProxy.bringToFront();
            this.sharedArtworkProxy.animate().x(f).y(f2).scaleX(i7 / (float) width).scaleY(i7 / (float) height).setInterpolator(timeInterpolator).setDuration(j).start();
            Log.i(TVLyricsLayout.TAG, "ARTWORK_RECT legacy-home=true start=" + width + "x" + height + " target=" + i7 + "x" + i7);
            return true;
        }

        void finishSharedArtworkRectTransition(boolean z) {
            if (this.sharedArtworkProxy == null || this.sharedArtworkContainer == null || this.sharedArtworkProxy.getParent() != this.sharedLayer || !(this.sharedArtworkContainer instanceof FrameLayout)) {
                return;
            }
            int sharedArtworkSize = sharedArtworkSize(z);
            this.sharedArtworkProxy.animate().cancel();
            ((ViewGroup) this.sharedLayer).removeView(this.sharedArtworkProxy);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(sharedArtworkSize, sharedArtworkSize, Gravity.CENTER);
            ((FrameLayout) this.sharedArtworkContainer).addView(this.sharedArtworkProxy, layoutParams);
            this.sharedArtworkProxy.setTranslationX(0.0f);
            this.sharedArtworkProxy.setTranslationY(0.0f);
            this.sharedArtworkProxy.setScaleX(1.0f);
            this.sharedArtworkProxy.setScaleY(1.0f);
            this.sharedArtworkProxy.setVisibility(View.VISIBLE);
            Log.i(TVLyricsLayout.TAG, "ARTWORK_RECT committed=" + sharedArtworkSize + " panel=" + z);
        }

        void stabilizeSharedArtworkCard(boolean z) {
            FrameLayout.LayoutParams layoutParams;
            if (!this.sharedPlayerActive || this.sharedArtworkCard == null || !(this.sharedArtworkCard.getParent() instanceof FrameLayout)) {
                return;
            }
            int iMin = sharedArtworkSize(z);
            int cardWidth = iMin;
            int cardHeight = iMin;
            if (this.sharedVideoActive && this.sharedVideoFrameReady) {
                float aspect = this.sharedVideoAspect > 0.0f ? this.sharedVideoAspect : 16.0f / 9.0f;
                if (z) {
                    cardHeight = Math.max(TVLyricsLayout.MODE_LYRICS, Math.round(iMin / aspect));
                } else {
                    cardWidth = Math.max(TVLyricsLayout.MODE_LYRICS, Math.round(iMin * aspect));
                }
            }
            ViewGroup.LayoutParams layoutParams2 = this.sharedArtworkCard.getLayoutParams();
            if (layoutParams2 instanceof FrameLayout.LayoutParams) {
                layoutParams = new FrameLayout.LayoutParams((FrameLayout.LayoutParams) layoutParams2);
            } else {
                layoutParams = new FrameLayout.LayoutParams(cardWidth, cardHeight);
            }
            layoutParams.width = cardWidth;
            layoutParams.height = cardHeight;
            layoutParams.gravity = 17;
            layoutParams.leftMargin = 0;
            layoutParams.topMargin = 0;
            layoutParams.rightMargin = 0;
            layoutParams.bottomMargin = 0;
            this.sharedArtworkCard.setLayoutParams(layoutParams);
            this.sharedArtworkCard.setTranslationX(0.0f);
            this.sharedArtworkCard.setTranslationY(0.0f);
            this.sharedArtworkCard.setScaleX(1.0f);
            this.sharedArtworkCard.setScaleY(1.0f);
            updateSharedArtworkCropMatrix();
        }

        void updateSharedDragOverlayGeometry(boolean z) {
            if (this.dragDismissOverlay == null) {
                return;
            }
            for (SharedItem sharedItem : this.sharedItems) {
                if (sharedItem.view == this.sharedArtworkContainer) {
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(z ? sharedItem.targetWidth : sharedItem.homeWidth, z ? sharedItem.targetHeight : sharedItem.homeHeight);
                    layoutParams.leftMargin = z ? sharedItem.targetX : sharedItem.homeX;
                    layoutParams.topMargin = z ? sharedItem.targetY : sharedItem.homeY;
                    this.dragDismissOverlay.setLayoutParams(layoutParams);
                    this.dragDismissOverlay.bringToFront();
                    return;
                }
            }
        }

        void commitSharedHomeGeometry() {
            if (!this.sharedPlayerActive || this.sharedLayer == null) {
                return;
            }
            for (SharedItem sharedItem : this.sharedItems) {
                sharedItem.view.animate().cancel();
                commitSharedItemBounds(sharedItem, sharedItem.homeX, sharedItem.homeY, sharedItem.homeWidth, sharedItem.homeHeight);
            }
            if (this.sharedTitle instanceof TextView) {
                ((TextView) this.sharedTitle).setTextSize(TVLyricsLayout.MODE_QUEUE, 20.0f);
                ((TextView) this.sharedTitle).setGravity(Gravity.CENTER);
            }
            if (this.sharedSubtitle instanceof TextView) {
                ((TextView) this.sharedSubtitle).setTextSize(TVLyricsLayout.MODE_QUEUE, 20.0f);
                ((TextView) this.sharedSubtitle).setGravity(Gravity.CENTER);
            }
            configureControlGeometry(this.sharedControls);
            this.sharedAtPanelGeometry = false;
            stabilizeSharedArtworkCard(false);
            applySharedVideoPresentation("home-geometry-commit");
            updateSharedDragOverlayGeometry(false);
            updateSharedLayerTouchWidth(false);
            this.sharedLayer.requestLayout();
            this.sharedLayer.bringToFront();
        }

        void animateSharedPlayerToHome(final String str) {
            if (!this.sharedPlayerActive || this.sharedLayer == null || this.sharedItems.isEmpty()) {
                return;
            }
            if (!this.sharedAtPanelGeometry) {
                commitSharedHomeGeometry();
                return;
            }
            final int i = this.sharedAnimationGeneration + TVLyricsLayout.MODE_LYRICS;
            this.sharedAnimationGeneration = i;
            this.sharedPlayerAnimating = true;
            setSharedModeVisual(0);
            // Panel mode uses a half-width gesture surface so the native list can
            // scroll. HOME animation must expand it before any item crosses the
            // old boundary, otherwise the artwork is clipped mid-transition.
            updateSharedLayerTouchWidth(false);
            android.animation.TimeInterpolator linearInterpolator = new android.view.animation.LinearInterpolator();
            PathInterpolator pathInterpolator = new PathInterpolator(0.20f, 0.0f, 0.20f, 1.0f);
            final boolean z = beginSharedArtworkRectTransition(false, linearInterpolator, 360L);
            for (SharedItem sharedItem : this.sharedItems) {
                if (sharedItem.view == this.sharedTitle || sharedItem.view == this.sharedSubtitle) {
                    continue;
                }
                sharedItem.view.animate().cancel();
                sharedItem.view.setPivotX(0.0f);
                sharedItem.view.setPivotY(0.0f);
                float f = sharedItem.homeX;
                float f2 = sharedItem.homeY;
                float f3 = sharedItem.homeWidth / Math.max(TVLyricsLayout.MODE_LYRICS, sharedItem.view.getWidth());
                float f4 = sharedItem.homeHeight / Math.max(TVLyricsLayout.MODE_LYRICS, sharedItem.view.getHeight());
                if (z && sharedItem.view == this.sharedArtworkContainer) {
                    continue;
                }
                if (sharedItem.view == this.sharedArtworkContainer && this.sharedArtworkCard != null) {
                    int i2 = Math.max(MODE_LYRICS, this.sharedArtworkCard.getWidth());
                    int i3 = sharedArtworkSize(false);
                    float f5 = i3 / (float) i2;
                    float f6 = Math.max(0.0f, (sharedItem.view.getWidth() - i2) / 2.0f);
                    float f7 = Math.max(0.0f, (sharedItem.view.getHeight() - this.sharedArtworkCard.getHeight()) / 2.0f);
                    float f8 = sharedItem.homeX + ((sharedItem.homeWidth - i3) / 2.0f);
                    float f9 = sharedItem.homeY + ((sharedItem.homeHeight - i3) / 2.0f);
                    float f10 = sharedItem.view.getWidth() * 0.5f;
                    float f11 = sharedItem.view.getHeight() * 0.5f;
                    sharedItem.view.setPivotX(f10);
                    sharedItem.view.setPivotY(f11);
                    f = sharedItemX(sharedItem, f8 - (f10 * (1.0f - f5)) - (f6 * f5));
                    f2 = sharedItemY(sharedItem, f9 - (f11 * (1.0f - f5)) - (f7 * f5));
                    f3 = f5;
                    f4 = f5;
                }
                sharedItem.view.animate().x(f).y(f2).scaleX(f3).scaleY(f4).setInterpolator(pathInterpolator).setDuration(360L).start();
            }
            crossfadeSharedText(false, i);
            this.sharedLayer.postDelayed(new Runnable() {
                @Override // java.lang.Runnable
                public void run() {
                    if (TVLyricsLayout.active != Controller.this || i != Controller.this.sharedAnimationGeneration) {
                        return;
                    }
                    if (z) {
                        Controller.this.finishSharedArtworkRectTransition(false);
                    }
                    Controller.this.commitSharedHomeGeometry();
                    Controller.this.sharedPlayerAnimating = false;
                    Log.i(TVLyricsLayout.TAG, "SHARED_PLAYER windows-home-committed=true reason=" + str);
                }
            }, 370L);
        }

        void crossfadeSharedText(boolean z, int i) {
            for (SharedItem sharedItem : this.sharedItems) {
                if ((sharedItem.view == this.sharedTitle || sharedItem.view == this.sharedSubtitle) && sharedItem.view instanceof TextView) {
                    crossfadeSharedTextItem((TextView) sharedItem.view, sharedItem, z, i);
                }
            }
        }

        void crossfadeSharedTextItem(final TextView textView, final SharedItem sharedItem, final boolean z, final int i) {
            textView.animate().cancel();
            textView.animate().alpha(0.0f).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(100L).withEndAction(new Runnable() {
                @Override
                public void run() {
                    if (TVLyricsLayout.active != Controller.this || !Controller.this.sharedPlayerActive || i != Controller.this.sharedAnimationGeneration) {
                        return;
                    }
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(z ? sharedItem.targetWidth : sharedItem.homeWidth, z ? sharedItem.targetHeight : sharedItem.homeHeight);
                    layoutParams.leftMargin = z ? sharedItem.targetX : sharedItem.homeX;
                    layoutParams.topMargin = z ? sharedItem.targetY : sharedItem.homeY;
                    textView.setLayoutParams(layoutParams);
                    textView.setTextSize(TVLyricsLayout.MODE_QUEUE, z ? 16.0f : 20.0f);
                    textView.setGravity(z ? Gravity.START | Gravity.CENTER_VERTICAL : Gravity.CENTER);
                    textView.setPivotX(0.0f);
                    textView.setPivotY(0.0f);
                    textView.setTranslationX(0.0f);
                    textView.setTranslationY(0.0f);
                    textView.setScaleX(1.0f);
                    textView.setScaleY(1.0f);
                    textView.setAlpha(0.0f);
                    textView.requestLayout();
                    textView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (TVLyricsLayout.active == Controller.this && Controller.this.sharedPlayerActive && i == Controller.this.sharedAnimationGeneration) {
                                textView.animate().alpha(1.0f).setInterpolator(new android.view.animation.LinearInterpolator()).setDuration(160L).start();
                            }
                        }
                    }, 80L);
                }
            }).start();
        }

        void prepareSharedTextForHome(TextView textView, SharedItem sharedItem) {
            float height = textView.getHeight();
            float measuredText2 = textView.getPaint().measureText(String.valueOf(textView.getText()));
            float currentCenter = textView.getX() + textView.getCompoundPaddingLeft() + (measuredText2 * 0.5f);
            float centerY = textView.getY() + (height * 0.5f);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(sharedItem.homeWidth, sharedItem.homeHeight);
            layoutParams.leftMargin = sharedItem.homeX;
            layoutParams.topMargin = sharedItem.homeY;
            textView.setLayoutParams(layoutParams);
            textView.setTextSize(TVLyricsLayout.MODE_QUEUE, 20.0f);
            textView.setGravity(Gravity.CENTER);
            float scale = 0.8f;
            textView.setX(currentCenter - ((sharedItem.homeWidth * scale) * 0.5f));
            textView.setY(centerY - ((sharedItem.homeHeight * scale) * 0.5f));
            textView.setScaleX(scale);
            textView.setScaleY(scale);
        }

        void restoreSharedPlayerImmediate(String str) {
            if (!this.sharedPlayerActive && this.sharedSnapshots.isEmpty() && this.sharedLayer == null) {
                return;
            }
            restoreVideoBackgrounds();
            this.pendingVideoGeometryKey = null;
            this.pendingVideoArtworkKey = null;
            this.pendingVideoArtworkBitmap = null;
            this.pendingVideoSurfaceBaselineKey = null;
            this.pendingVideoSurfaceBaselineView = null;
            this.pendingVideoSurfaceBaseline = 0L;
            this.pendingVideoSurfaceBaselineCaptured = false;
            this.pendingVideoSurfaceBaselineAt = 0L;
            this.sharedAnimationGeneration += TVLyricsLayout.MODE_LYRICS;
            for (SharedItem sharedItem : this.sharedItems) {
                sharedItem.view.animate().cancel();
                sharedItem.view.clearAnimation();
            }
            if (this.sharedArtworkProxy != null && this.sharedArtworkProxy.getParent() instanceof ViewGroup) {
                ((ViewGroup) this.sharedArtworkProxy.getParent()).removeView(this.sharedArtworkProxy);
            }
            if (this.fullscreenBackdrop instanceof ImageView) {
                ((ImageView) this.fullscreenBackdrop).setImageDrawable(null);
            }
            boolean zRestoreSnapshots = TVLyricsLayout.restoreSnapshots(this.sharedSnapshots);
            if (this.sharedLayer != null && (this.sharedLayer.getParent() instanceof ViewGroup)) {
                ((ViewGroup) this.sharedLayer.getParent()).removeView(this.sharedLayer);
            }
            Log.i(TVLyricsLayout.TAG, "RESTORE_SHARED reason=" + str + " match=" + zRestoreSnapshots + " count=" + this.sharedSnapshots.size());
            this.sharedItems.clear();
            this.sharedSnapshots.clear();
            this.sharedSeen.clear();
            this.sharedLayer = null;
            this.sharedArtworkContainer = null;
            this.sharedArtworkCard = null;
            this.sharedArtworkSourceCard = null;
            this.sharedArtworkProxy = null;
            this.sharedArtworkProxySource = null;
            this.sharedArtworkProxyKey = null;
            this.sharedVideoKey = null;
            this.sharedArtworkSourceBitmap = null;
            if (this.sharedArtworkProxyBitmap != null && !this.sharedArtworkProxyBitmap.isRecycled()) {
                this.sharedArtworkProxyBitmap.recycle();
            }
            this.sharedArtworkProxyBitmap = null;
            this.sharedVideoSurface = null;
            this.sharedNativeArtwork = null;
            this.sharedNativeFullscreenButton = null;
            this.sharedVideoActive = false;
            this.sharedVideoPresented = false;
            this.sharedVideoConfirmedSurface = null;
            this.sharedVideoAspect = 0.0f;
            this.sharedArtworkPinned = false;
            this.panelNativeVideoShown = false;
            clearRetainedQueueVideo();
            this.sharedTitle = null;
            this.sharedSubtitle = null;
            this.sharedFavorite = null;
            this.sharedMore = null;
            this.sharedControls = null;
            this.sharedLyricsButton = null;
            this.sharedQueueButton = null;
            this.sharedRouteButton = null;
            this.dragDismissOverlay = null;
            this.sharedPlayerActive = false;
            this.sharedPlayerAnimating = false;
            this.sharedAtPanelGeometry = false;
        }

        void setSharedModeVisual(int i) {
            if (i != TVLyricsLayout.MODE_QUEUE) {
                this.panelNativeVideoShown = false;
            }
            if (this.sharedLyricsButton != null) {
                boolean z = i == TVLyricsLayout.MODE_LYRICS;
                this.sharedLyricsButton.setSelected(z);
                this.sharedLyricsButton.setActivated(z);
            }
            if (this.sharedQueueButton != null) {
                boolean z2 = i == TVLyricsLayout.MODE_QUEUE;
                this.sharedQueueButton.setSelected(z2);
                this.sharedQueueButton.setActivated(z2);
            }
            applySharedVideoPresentation("mode-" + i);
        }

        void hidePanelLeftCopies(View view) {
            if (view == null || !this.sharedPlayerActive) {
                return;
            }
            View viewFindViewById = view.findViewById(TVLyricsLayout.ID_CURRENT_PLAYER_ITEM);
            View viewFindViewById2 = view.findViewById(TVLyricsLayout.ID_CONTROLS);
            // QUEUE may remain the decoder owner after the visual state has
            // already changed to HOME.  A late panel-persistence callback must
            // not classify that live current-player subtree as a duplicate and
            // hide it; doing so leaves the TextureView attached and decoding,
            // but one INVISIBLE ancestor makes every frame disappear.
            boolean retainedVideoOwner = this.retainedQueueVideoAtHome
                    && view == this.retainedQueueVideoRoot;
            boolean panelVideo = (this.mode == TVLyricsLayout.MODE_QUEUE || retainedVideoOwner)
                    && showPanelNativeVideoOnly(view);
            // During an audio -> music-video handoff the incoming landscape
            // artwork is available before Apple's decoder advances the QUEUE
            // TextureView.  The old implementation hid CURRENT_PLAYER_ITEM
            // while waiting for that advance.  Because it is also the native
            // TextureView's ancestor, the decoder surface remained attached
            // but frozen behind an INVISIBLE parent, so the two-frame proof
            // could never complete.  Keep only that pending native surface
            // alive underneath the elevated shared artwork proxy.  Its own
            // thumbnail and metadata stay hidden, so no square/stale artwork
            // can leak into the transition.
            boolean pendingPanelVideoAlive = !panelVideo
                    && shouldKeepPendingPanelVideoAlive(view);
            if (pendingPanelVideoAlive) {
                keepPendingPanelVideoAlive(view);
            } else if (viewFindViewById != null && !panelVideo) {
                viewFindViewById.animate().cancel();
                viewFindViewById.setAlpha(0.0f);
                viewFindViewById.setVisibility(4);
            }
            if (viewFindViewById2 != null) {
                viewFindViewById2.animate().cancel();
                viewFindViewById2.setAlpha(0.0f);
                viewFindViewById2.setVisibility(4);
            }
        }

        boolean shouldKeepPendingPanelVideoAlive(View view) {
            if (view == null || this.mode != TVLyricsLayout.MODE_QUEUE) {
                return false;
            }
            String key = currentArtworkKey();
            if (!key.equals(this.pendingVideoArtworkKey)
                    || usableBitmapAspect(this.pendingVideoArtworkBitmap) <= 1.30f) {
                return false;
            }
            View video = view.findViewById(TVLyricsLayout.ID_VIDEO_SURFACE);
            if (!(video instanceof TextureView)) {
                return false;
            }
            TextureView textureView = (TextureView) video;
            return textureView.isAvailable() && textureView.getSurfaceTexture() != null;
        }

        void keepPendingPanelVideoAlive(View view) {
            View current = view.findViewById(TVLyricsLayout.ID_CURRENT_PLAYER_ITEM);
            View artwork = view.findViewById(TVLyricsLayout.ID_QUEUE_THUMBNAIL_CONTAINER);
            View video = view.findViewById(TVLyricsLayout.ID_VIDEO_SURFACE);
            View thumbnail = view.findViewById(TVLyricsLayout.ID_THUMBNAIL);
            if (current == null || artwork == null || !(video instanceof TextureView)) {
                return;
            }
            current.animate().cancel();
            current.setVisibility(View.VISIBLE);
            current.setAlpha(1.0f);
            artwork.animate().cancel();
            artwork.setVisibility(View.VISIBLE);
            artwork.setAlpha(1.0f);
            video.animate().cancel();
            video.setVisibility(View.VISIBLE);
            video.setAlpha(1.0f);
            if (thumbnail != null) {
                thumbnail.animate().cancel();
                thumbnail.setAlpha(0.0f);
                thumbnail.setVisibility(View.INVISIBLE);
            }
            int[] hiddenIds = {
                    TVLyricsLayout.ID_TEXT_METADATA_CONTAINER,
                    TVLyricsLayout.ID_LIST_FAVORITE_ICON,
                    TVLyricsLayout.ID_LIST_LEFT_ICON
            };
            for (int id : hiddenIds) {
                View hidden = view.findViewById(id);
                if (hidden != null) {
                    hidden.animate().cancel();
                    hidden.setAlpha(0.0f);
                    hidden.setVisibility(View.INVISIBLE);
                }
            }
            String key = currentArtworkKey();
            if (!key.equals(this.pendingPanelKeepAliveLogKey)) {
                this.pendingPanelKeepAliveLogKey = key;
                Log.i(TVLyricsLayout.TAG, "VIDEO_PENDING panel-kept-alive=true surface="
                        + Integer.toHexString(System.identityHashCode(video))
                        + " key=" + key.replace('\n', '|'));
            }
        }

        boolean showPanelNativeVideoOnly(View view) {
            if (view == null || (this.mode != TVLyricsLayout.MODE_QUEUE && !this.retainedQueueVideoAtHome)) {
                return false;
            }
            View current = view.findViewById(TVLyricsLayout.ID_CURRENT_PLAYER_ITEM);
            View artwork = view.findViewById(TVLyricsLayout.ID_QUEUE_THUMBNAIL_CONTAINER);
            View video = view.findViewById(TVLyricsLayout.ID_VIDEO_SURFACE);
            View thumbnail = view.findViewById(TVLyricsLayout.ID_THUMBNAIL);
            if (current == null || artwork == null || !(video instanceof TextureView)) {
                return false;
            }
            TextureView textureView = (TextureView) video;
            if (!textureView.isAvailable() || textureView.getSurfaceTexture() == null) {
                return false;
            }
            boolean nativeVideoVisible = textureView.getVisibility() == View.VISIBLE && textureView.getAlpha() > 0.05f;
            boolean nativeArtworkHidden = thumbnail == null
                    || thumbnail.getVisibility() != View.VISIBLE
                    || thumbnail.getAlpha() < 0.25f;
            boolean confirmedCurrentVideoFrame = this.sharedVideoFrameReady
                    && currentArtworkKey().equals(this.sharedVideoKey);
            if (!nativeVideoVisible || (!nativeArtworkHidden && !confirmedCurrentVideoFrame)) {
                return false;
            }
            if (!hasVisibleTextureFrame(textureView)) {
                return false;
            }
            // `video` was resolved from this exact QUEUE current-player root,
            // so its identity already excludes the separate mini-player
            // TextureView.  Apple's QUEUE surface initially measures only
            // about 213x120 and is resized by this method after the first
            // frame. Rejecting it by size made the real advancing surface fail
            // promotion and incorrectly exposed the empty HOME surface.
            // This renderer must never promote a merely visible TextureView to
            // the current video by itself.  Promotion used to happen here as a
            // side effect of layout/hide callbacks, while refreshSharedVideoPresentation()
            // also owned the same state transition.  Which callback ran first
            // then decided whether the track received a complete detected=true
            // commit, causing intermittent stale/square frames after repeated
            // audio <-> video switches.  The refresh watcher is now the only
            // authority: it proves frame advancement, records the exact owner,
            // and commits the key/aspect/background atomically before rendering.
            if (!confirmedCurrentVideoFrame || this.sharedVideoConfirmedSurface != textureView) {
                return false;
            }
            current.animate().cancel();
            current.setVisibility(View.VISIBLE);
            current.setAlpha(1.0f);
            artwork.setVisibility(View.VISIBLE);
            artwork.setAlpha(1.0f);
            int size = sharedArtworkSize(true);
            float aspect = resolveVideoContentAspect(textureView);
            int videoHeight = Math.max(TVLyricsLayout.MODE_LYRICS, Math.round(size / aspect));
            boolean preserveReturnAnimation = this.retainedQueueVideoReturningToPanel
                    && artwork == this.retainedQueueVideoArtwork;
            if (!preserveReturnAnimation) {
                artwork.animate().cancel();
                ViewGroup.LayoutParams artworkParams = artwork.getLayoutParams();
                if (artworkParams instanceof LinearLayout.LayoutParams) {
                    artwork.setLayoutParams(new LinearLayout.LayoutParams(size, videoHeight));
                } else {
                    artworkParams.width = size;
                    artworkParams.height = videoHeight;
                    artwork.setLayoutParams(artworkParams);
                }
                artwork.setTranslationY(Math.max(0, (size - videoHeight) / TVLyricsLayout.MODE_QUEUE));
            }
            TVLyricsLayout.fill(video);
            ensureVideoSurfaceShowsFullFrame(textureView, aspect, "queue-native");
            video.setVisibility(View.VISIBLE);
            video.setAlpha(1.0f);
            if (thumbnail != null) {
                thumbnail.setAlpha(0.0f);
                thumbnail.setVisibility(View.INVISIBLE);
            }
            int[] hiddenIds = {
                    TVLyricsLayout.ID_TEXT_METADATA_CONTAINER,
                    TVLyricsLayout.ID_LIST_FAVORITE_ICON,
                    TVLyricsLayout.ID_LIST_LEFT_ICON
            };
            for (int id : hiddenIds) {
                View hidden = view.findViewById(id);
                if (hidden != null) {
                    hidden.animate().cancel();
                    hidden.setAlpha(0.0f);
                    hidden.setVisibility(View.INVISIBLE);
                }
            }
            if (this.sharedArtworkContainer != null) {
                this.sharedArtworkContainer.setAlpha(0.0f);
            }
            if (!this.panelNativeVideoShown) {
                this.panelNativeVideoShown = true;
                Log.i(TVLyricsLayout.TAG, "VIDEO_PRESENTATION queue-native-surface=true size=" + size + "x" + videoHeight
                        + " centerOffsetY=" + Math.max(0, (size - videoHeight) / TVLyricsLayout.MODE_QUEUE) + " aspect=" + aspect);
            }
            completeVideoHomeBootstrapIfReady(view, textureView);
            return true;
        }

        boolean isPanelNativeVideoAvailable(View view) {
            if (view == null) {
                return false;
            }
            View video = view.findViewById(TVLyricsLayout.ID_VIDEO_SURFACE);
            View thumbnail = view.findViewById(TVLyricsLayout.ID_THUMBNAIL);
            if (!(video instanceof TextureView)) {
                return false;
            }
            TextureView textureView = (TextureView) video;
            return textureView.isAvailable()
                    && textureView.getSurfaceTexture() != null
                    && textureView.getVisibility() == View.VISIBLE
                    && textureView.getAlpha() > 0.05f
                    && (thumbnail == null
                    || thumbnail.getVisibility() != View.VISIBLE
                    || thumbnail.getAlpha() < 0.25f);
        }

        boolean retainQueueVideoForHome(View view, String reason) {
            if (view == null || !isPanelNativeVideoAvailable(view)) {
                return false;
            }
            View artwork = view.findViewById(TVLyricsLayout.ID_QUEUE_THUMBNAIL_CONTAINER);
            View video = view.findViewById(TVLyricsLayout.ID_VIDEO_SURFACE);
            if (artwork == null || !(video instanceof TextureView) || !(artwork.getParent() instanceof View)) {
                return false;
            }
            // Surface availability is not proof that QUEUE owns a decoded
            // frame.  During cold video startup Apple can create a blank QUEUE
            // TextureView while the live decoder continues advancing the HOME
            // TextureView.  Handing HOME to that merely-available Surface is
            // the black-video failure.  Only transfer ownership after the
            // stricter presentation check has sampled a real current frame.
            if (!showPanelNativeVideoOnly(view)) {
                Log.i(TVLyricsLayout.TAG, "VIDEO_SURFACE retain-rejected=no-current-frame reason=" + reason);
                return false;
            }
            this.retainedQueueVideoRoot = view;
            this.retainedQueueVideoArtwork = artwork;
            this.retainedQueueVideoSurface = (TextureView) video;
            this.retainedQueueVideoAtHome = true;
            this.retainedQueueVideoReturningToPanel = false;
            this.retainedQueueVideoGeneration += TVLyricsLayout.MODE_LYRICS;
            showPanel(view);
            placeFullscreenBackdropUnder(view, "retained-queue-home");
            ViewParent parent = artwork.getParent();
            while (parent instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) parent;
                group.setClipChildren(false);
                group.setClipToPadding(false);
                if (group == view) {
                    break;
                }
                parent = group.getParent();
            }
            this.retainedQueueVideoPanelX = artwork.getX();
            this.retainedQueueVideoPanelY = artwork.getY();
            this.retainedQueueVideoPanelScaleX = artwork.getScaleX();
            this.retainedQueueVideoPanelScaleY = artwork.getScaleY();
            int[] parentLocation = new int[TVLyricsLayout.MODE_QUEUE];
            ((View) artwork.getParent()).getLocationInWindow(parentLocation);
            SharedItem item = findSharedArtworkItem();
            int homeSize = sharedArtworkSize(false);
            float aspect = resolveVideoContentAspect(this.retainedQueueVideoSurface);
            int targetWidth = Math.max(homeSize, Math.round(homeSize * aspect));
            int centerX = item != null
                    ? item.homeX + (item.homeWidth / TVLyricsLayout.MODE_QUEUE)
                    : this.sheet.getResources().getDisplayMetrics().widthPixels / TVLyricsLayout.MODE_QUEUE;
            int targetX = centerX - (targetWidth / TVLyricsLayout.MODE_QUEUE) - parentLocation[0];
            int targetY = (item != null
                    ? item.homeY + ((item.homeHeight - homeSize) / TVLyricsLayout.MODE_QUEUE)
                    : TVLyricsLayout.dp(view, 84)) - parentLocation[TVLyricsLayout.MODE_LYRICS];
            float scaleX = targetWidth / (float) Math.max(TVLyricsLayout.MODE_LYRICS, artwork.getWidth());
            float scaleY = homeSize / (float) Math.max(TVLyricsLayout.MODE_LYRICS, artwork.getHeight());
            int[] hideIds = {
                    TVLyricsLayout.ID_QUEUE_MAIN_CONTENT,
                    TVLyricsLayout.ID_PLAYER_ACTION_BUTTONS,
                    TVLyricsLayout.ID_STICKY_HEADER_CLIP,
                    TVLyricsLayout.ID_RECYCLER_GRADIENTS,
                    TVLyricsLayout.ID_CONTROLS
            };
            for (int id : hideIds) {
                View hidden = view.findViewById(id);
                if (hidden != null) {
                    hidden.animate().cancel();
                    hidden.setAlpha(0.0f);
                    hidden.setVisibility(View.INVISIBLE);
                }
            }
            artwork.animate().cancel();
            artwork.setPivotX(0.0f);
            artwork.setPivotY(0.0f);
            artwork.animate()
                    .x(targetX)
                    .y(targetY)
                    .scaleX(scaleX)
                    .scaleY(scaleY)
                    .setInterpolator(new PathInterpolator(0.20f, 0.0f, 0.20f, 1.0f))
                    .setDuration(360L)
                    .start();
            if (this.sharedArtworkContainer != null) {
                this.sharedArtworkContainer.setAlpha(0.0f);
            }
            Log.i(TVLyricsLayout.TAG, "VIDEO_SURFACE retained-owner=QUEUE target=HOME reason=" + reason
                    + " target=" + targetWidth + "x" + homeSize
                    + " scale=" + scaleX + "x" + scaleY);
            return true;
        }

        void restoreRetainedQueueVideoToPanel(boolean animate) {
            final View root = this.retainedQueueVideoRoot;
            final View artwork = this.retainedQueueVideoArtwork;
            if (!this.retainedQueueVideoAtHome || root == null || artwork == null) {
                return;
            }
            this.retainedQueueVideoReturningToPanel = true;
            final int generation = this.retainedQueueVideoGeneration + TVLyricsLayout.MODE_LYRICS;
            this.retainedQueueVideoGeneration = generation;
            showPanel(root);
            artwork.animate().cancel();
            artwork.setPivotX(0.0f);
            artwork.setPivotY(0.0f);
            final Runnable commitReturn = new Runnable() {
                @Override
                public void run() {
                    if (Controller.this.retainedQueueVideoGeneration != generation
                            || Controller.this.retainedQueueVideoRoot != root
                            || Controller.this.retainedQueueVideoArtwork != artwork) {
                        return;
                    }
                    artwork.setX(Controller.this.retainedQueueVideoPanelX);
                    artwork.setY(Controller.this.retainedQueueVideoPanelY);
                    artwork.setScaleX(Controller.this.retainedQueueVideoPanelScaleX);
                    artwork.setScaleY(Controller.this.retainedQueueVideoPanelScaleY);
                    Controller.this.retainedQueueVideoAtHome = false;
                    Controller.this.retainedQueueVideoReturningToPanel = false;
                    Controller.this.showPanelNativeVideoOnly(root);
                    root.requestLayout();
                    root.invalidate();
                    Log.i(TVLyricsLayout.TAG, "VIDEO_SURFACE retained-owner=QUEUE target=QUEUE committed=true");
                }
            };
            if (animate) {
                artwork.animate()
                        .x(this.retainedQueueVideoPanelX)
                        .y(this.retainedQueueVideoPanelY)
                        .scaleX(this.retainedQueueVideoPanelScaleX)
                        .scaleY(this.retainedQueueVideoPanelScaleY)
                        .setInterpolator(new PathInterpolator(0.20f, 0.0f, 0.20f, 1.0f))
                        .setDuration(360L)
                        .withEndAction(commitReturn)
                        .start();
            } else {
                commitReturn.run();
            }
            Log.i(TVLyricsLayout.TAG, "VIDEO_SURFACE retained-owner=QUEUE target=QUEUE animated=" + animate);
        }

        void clearRetainedQueueVideo() {
            if (this.retainedQueueVideoArtwork != null) {
                this.retainedQueueVideoArtwork.animate().cancel();
            }
            this.retainedQueueVideoRoot = null;
            this.retainedQueueVideoArtwork = null;
            this.retainedQueueVideoSurface = null;
            this.retainedQueueVideoAtHome = false;
            this.retainedQueueVideoReturningToPanel = false;
            this.retainedQueueVideoGeneration += TVLyricsLayout.MODE_LYRICS;
        }

        void applyLyricsLayout(View view) {
            if (view == null || this.mode != TVLyricsLayout.MODE_LYRICS || !this.expanded) {
                return;
            }
            if (this.panelRoot == view && this.panelMode == TVLyricsLayout.MODE_LYRICS && !this.panelSnapshots.isEmpty()) {
                startPanelPersistence(view, TVLyricsLayout.MODE_LYRICS);
                hidePanelLeftCopies(view);
                setSharedModeVisual(TVLyricsLayout.MODE_LYRICS);
                return;
            }
            View view2 = this.panelRoot;
            restorePanelLayout("replace-lyrics-layout");
            if (view2 != null && view2 != view) {
                hideInactivePanel(view2);
            }
            showPanel(view);
            this.panelRoot = view;
            this.panelMode = TVLyricsLayout.MODE_LYRICS;
            this.homeRetainedMode = 0;
            LinearLayout linearLayoutAsLinear = TVLyricsLayout.asLinear(view.findViewById(TVLyricsLayout.ID_CURRENT_PLAYER_ITEM));
            View viewFindViewById = view.findViewById(TVLyricsLayout.ID_RECYCLER_GRADIENTS);
            View viewFindViewById2 = view.findViewById(TVLyricsLayout.ID_CONTROLS);
            if (linearLayoutAsLinear == null || viewFindViewById == null || viewFindViewById2 == null) {
                Log.w(TVLyricsLayout.TAG, "LYRICS layout skipped: incomplete native panel");
                enterHome("incomplete-lyrics-panel");
                return;
            }
            View viewFindViewById3 = view.findViewById(TVLyricsLayout.ID_LYRICS_THUMBNAIL_CONTAINER);
            View viewFindViewById4 = view.findViewById(TVLyricsLayout.ID_VIDEO_SURFACE);
            View viewFindViewById5 = view.findViewById(TVLyricsLayout.ID_THUMBNAIL);
            View viewFindViewById6 = view.findViewById(TVLyricsLayout.ID_TEXT_METADATA_CONTAINER);
            View viewFindViewById7 = view.findViewById(TVLyricsLayout.ID_LIST_FAVORITE_ICON);
            View viewFindViewById8 = view.findViewById(TVLyricsLayout.ID_LIST_LEFT_ICON);
            View viewFindViewById9 = view.findViewById(TVLyricsLayout.ID_TITLE);
            View viewFindViewById10 = view.findViewById(TVLyricsLayout.ID_SUBTITLE);
            View[] viewArr = {view, linearLayoutAsLinear, viewFindViewById, view.findViewById(TVLyricsLayout.ID_LYRICS_MAIN_CONTENT), viewFindViewById2, viewFindViewById3, viewFindViewById4, viewFindViewById5, viewFindViewById6, viewFindViewById7, viewFindViewById8, viewFindViewById9, viewFindViewById10, view.findViewById(TVLyricsLayout.ID_CONTROLS_TAP_TARGET), view.findViewById(TVLyricsLayout.ID_NO_LYRICS_AVAILABLE), view.findViewById(TVLyricsLayout.ID_LOADING_PROGRESS), view.findViewById(TVLyricsLayout.ID_TRANSLATIONS_BUTTON), view.findViewById(TVLyricsLayout.ID_VOCAL_CONTROL), view.findViewById(TVLyricsLayout.ID_TRANSLATIONS_BUBBLE_TIP), view.findViewById(TVLyricsLayout.ID_VA_BUBBLE_TIP), view.findViewById(TVLyricsLayout.ID_PLAYER_LYRICS), view.findViewById(TVLyricsLayout.ID_PLAYER_QUEUE), view.findViewById(TVLyricsLayout.ID_MEDIA_ROUTE_BUTTON), view.findViewById(TVLyricsLayout.ID_PREVIOUS_REWIND), view.findViewById(TVLyricsLayout.ID_PLAY_PAUSE), view.findViewById(TVLyricsLayout.ID_NEXT_FAST_FORWARD), view.findViewById(TVLyricsLayout.ID_SEEK_BAR_CONTROLS)};
            int length = viewArr.length;
            for (int i = 0; i < length; i += TVLyricsLayout.MODE_LYRICS) {
                capturePanel(viewArr[i]);
            }
            int i2 = view.getResources().getDisplayMetrics().widthPixels;
            int i3 = i2 / 8;
            int i4 = (i2 * 5) / 16;
            int i5 = i2 / 16;
            int i6 = i2 / 8;
            int iMin = Math.min(i4, TVLyricsLayout.dp(view, 240));
            int iDp = TVLyricsLayout.dp(view, 30);
            TVLyricsLayout.setSizeAndMargins(view, TVLyricsLayout.UNSET, TVLyricsLayout.UNSET, 0, 0, 0, 0);
            TVLyricsLayout.clearConstraints(linearLayoutAsLinear.getLayoutParams());
            TVLyricsLayout.setInt(linearLayoutAsLinear.getLayoutParams(), "t", 0);
            TVLyricsLayout.setInt(linearLayoutAsLinear.getLayoutParams(), "i", 0);
            TVLyricsLayout.setSizeAndMargins(linearLayoutAsLinear, i4, -2, i3, iDp, 0, 0);
            linearLayoutAsLinear.setOrientation(TVLyricsLayout.MODE_LYRICS);
            linearLayoutAsLinear.setGravity(TVLyricsLayout.MODE_LYRICS);
            linearLayoutAsLinear.setPadding(0, 0, 0, 0);
            if (viewFindViewById3 != null) {
                viewFindViewById3.setLayoutParams(new LinearLayout.LayoutParams(iMin, iMin));
            }
            TVLyricsLayout.fill(viewFindViewById4);
            TVLyricsLayout.fill(viewFindViewById5);
            keepRetainedArtwork(view);
            LinearLayout linearLayout = new LinearLayout(view.getContext());
            linearLayout.setOrientation(0);
            linearLayout.setGravity(16);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(TVLyricsLayout.UNSET, -2);
            layoutParams.topMargin = TVLyricsLayout.dp(view, 10);
            linearLayoutAsLinear.addView(linearLayout, layoutParams);
            this.addedViews.add(linearLayout);
            TVLyricsLayout.move(viewFindViewById6, linearLayout, new LinearLayout.LayoutParams(0, -2, 1.0f));
            TVLyricsLayout.move(viewFindViewById7, linearLayout, new LinearLayout.LayoutParams(TVLyricsLayout.dp(view, 44), TVLyricsLayout.dp(view, 52)));
            TVLyricsLayout.move(viewFindViewById8, linearLayout, new LinearLayout.LayoutParams(TVLyricsLayout.dp(view, 44), TVLyricsLayout.dp(view, 52)));
            if (viewFindViewById9 instanceof TextView) {
                ((TextView) viewFindViewById9).setTextSize(TVLyricsLayout.MODE_QUEUE, 16.0f);
            }
            if (viewFindViewById10 instanceof TextView) {
                ((TextView) viewFindViewById10).setTextSize(TVLyricsLayout.MODE_QUEUE, 16.0f);
            }
            TVLyricsLayout.clearConstraints(viewFindViewById.getLayoutParams());
            TVLyricsLayout.setInt(viewFindViewById.getLayoutParams(), "s", TVLyricsLayout.ID_CURRENT_PLAYER_ITEM);
            TVLyricsLayout.setInt(viewFindViewById.getLayoutParams(), "v", 0);
            TVLyricsLayout.setInt(viewFindViewById.getLayoutParams(), "i", 0);
            TVLyricsLayout.setInt(viewFindViewById.getLayoutParams(), "l", 0);
            TVLyricsLayout.setSizeAndMargins(viewFindViewById, 0, 0, i5, iDp, i6, iDp);
            TVLyricsLayout.clearConstraints(viewFindViewById2.getLayoutParams());
            TVLyricsLayout.setInt(viewFindViewById2.getLayoutParams(), "t", TVLyricsLayout.ID_CURRENT_PLAYER_ITEM);
            TVLyricsLayout.setInt(viewFindViewById2.getLayoutParams(), "v", TVLyricsLayout.ID_CURRENT_PLAYER_ITEM);
            TVLyricsLayout.setInt(viewFindViewById2.getLayoutParams(), "l", 0);
            TVLyricsLayout.setSizeAndMargins(viewFindViewById2, i4, TVLyricsLayout.dp(view, 180), 0, 0, 0, TVLyricsLayout.dp(view, 10));
            configureControlGeometry(view);
            View viewFindViewById11 = view.findViewById(TVLyricsLayout.ID_CONTROLS_TAP_TARGET);
            if (viewFindViewById11 != null) {
                TVLyricsLayout.clearConstraints(viewFindViewById11.getLayoutParams());
                TVLyricsLayout.setInt(viewFindViewById11.getLayoutParams(), "t", TVLyricsLayout.ID_CURRENT_PLAYER_ITEM);
                TVLyricsLayout.setInt(viewFindViewById11.getLayoutParams(), "v", TVLyricsLayout.ID_CURRENT_PLAYER_ITEM);
                TVLyricsLayout.setInt(viewFindViewById11.getLayoutParams(), "i", TVLyricsLayout.ID_TAP_TARGET_GUIDELINE);
                TVLyricsLayout.setInt(viewFindViewById11.getLayoutParams(), "l", 0);
                TVLyricsLayout.setSizeAndMargins(viewFindViewById11, 0, 0, 0, 0, 0, 0);
                disableFullscreenTapTarget(viewFindViewById11);
            }
            configureRightFill(view.findViewById(TVLyricsLayout.ID_NO_LYRICS_AVAILABLE), -2);
            configureRightFill(view.findViewById(TVLyricsLayout.ID_LOADING_PROGRESS), 0);
            configureRightEdge(view.findViewById(TVLyricsLayout.ID_TRANSLATIONS_BUTTON), true);
            configureRightEdge(view.findViewById(TVLyricsLayout.ID_VOCAL_CONTROL), false);
            configureRightSpan(view.findViewById(TVLyricsLayout.ID_TRANSLATIONS_BUBBLE_TIP));
            configureRightSpan(view.findViewById(TVLyricsLayout.ID_VA_BUBBLE_TIP));
            view.requestLayout();
            hidePanelLeftCopies(view);
            animateSharedPlayerToPanel();
            animateIncomingPanelIfNeeded(view);
            startPanelPersistence(view, TVLyricsLayout.MODE_LYRICS);
            startLyricsAvailabilityWatch();
            Log.i(TVLyricsLayout.TAG, "STATE=LYRICS root-parent-preserved=true");
        }

        void applyQueueLayout(View view) {
            if (view == null || this.mode != TVLyricsLayout.MODE_QUEUE || !this.expanded) {
                return;
            }
            if (this.panelRoot == view && this.panelMode == TVLyricsLayout.MODE_QUEUE && !this.panelSnapshots.isEmpty()) {
                startPanelPersistence(view, TVLyricsLayout.MODE_QUEUE);
                hidePanelLeftCopies(view);
                setSharedModeVisual(TVLyricsLayout.MODE_QUEUE);
                return;
            }
            View view2 = this.panelRoot;
            restorePanelLayout("replace-queue-layout");
            if (view2 != null && view2 != view) {
                hideInactivePanel(view2);
            }
            showPanel(view);
            this.panelRoot = view;
            this.panelMode = TVLyricsLayout.MODE_QUEUE;
            this.homeRetainedMode = 0;
            LinearLayout linearLayoutAsLinear = TVLyricsLayout.asLinear(view.findViewById(TVLyricsLayout.ID_CURRENT_PLAYER_ITEM));
            View viewFindViewById = view.findViewById(TVLyricsLayout.ID_PLAYER_ACTION_BUTTONS);
            View viewFindViewById2 = view.findViewById(TVLyricsLayout.ID_STICKY_HEADER_CLIP);
            View viewFindViewById3 = view.findViewById(TVLyricsLayout.ID_RECYCLER_GRADIENTS);
            View viewFindViewById4 = view.findViewById(TVLyricsLayout.ID_CONTROLS);
            if (linearLayoutAsLinear == null || viewFindViewById3 == null || viewFindViewById4 == null) {
                Log.w(TVLyricsLayout.TAG, "QUEUE layout skipped: incomplete native panel");
                enterHome("incomplete-queue-panel");
                return;
            }
            View viewFindViewById5 = view.findViewById(TVLyricsLayout.ID_QUEUE_THUMBNAIL_CONTAINER);
            View viewFindViewById6 = view.findViewById(TVLyricsLayout.ID_VIDEO_SURFACE);
            View viewFindViewById7 = view.findViewById(TVLyricsLayout.ID_THUMBNAIL);
            View viewFindViewById8 = view.findViewById(TVLyricsLayout.ID_TEXT_METADATA_CONTAINER);
            View viewFindViewById9 = view.findViewById(TVLyricsLayout.ID_LIST_FAVORITE_ICON);
            View viewFindViewById10 = view.findViewById(TVLyricsLayout.ID_LIST_LEFT_ICON);
            View viewFindViewById11 = view.findViewById(TVLyricsLayout.ID_MINI_PLAYER_TITLE);
            View viewFindViewById12 = view.findViewById(TVLyricsLayout.ID_MINI_PLAYER_SUBTITLE);
            View[] viewArr = {view, linearLayoutAsLinear, viewFindViewById, viewFindViewById2, viewFindViewById3, view.findViewById(TVLyricsLayout.ID_QUEUE_MAIN_CONTENT), viewFindViewById4, viewFindViewById5, viewFindViewById6, viewFindViewById7, viewFindViewById8, viewFindViewById9, viewFindViewById10, viewFindViewById11, viewFindViewById12, view.findViewById(TVLyricsLayout.ID_PLAYER_LYRICS), view.findViewById(TVLyricsLayout.ID_PLAYER_QUEUE), view.findViewById(TVLyricsLayout.ID_MEDIA_ROUTE_BUTTON), view.findViewById(TVLyricsLayout.ID_CONTROLS_TAP_TARGET), view.findViewById(TVLyricsLayout.ID_PREVIOUS_REWIND), view.findViewById(TVLyricsLayout.ID_PLAY_PAUSE), view.findViewById(TVLyricsLayout.ID_NEXT_FAST_FORWARD), view.findViewById(TVLyricsLayout.ID_SEEK_BAR_CONTROLS)};
            int length = viewArr.length;
            for (int i = 0; i < length; i += TVLyricsLayout.MODE_LYRICS) {
                capturePanel(viewArr[i]);
            }
            int i2 = view.getResources().getDisplayMetrics().widthPixels;
            int i3 = i2 / 8;
            int i4 = (i2 * 5) / 16;
            int i5 = i2 / 16;
            int i6 = i2 / 8;
            int iMin = Math.min(i4, TVLyricsLayout.dp(view, 240));
            int iDp = TVLyricsLayout.dp(view, 30);
            TVLyricsLayout.setSizeAndMargins(view, TVLyricsLayout.UNSET, TVLyricsLayout.UNSET, 0, 0, 0, 0);
            TVLyricsLayout.clearConstraints(linearLayoutAsLinear.getLayoutParams());
            TVLyricsLayout.setInt(linearLayoutAsLinear.getLayoutParams(), "t", 0);
            TVLyricsLayout.setInt(linearLayoutAsLinear.getLayoutParams(), "i", 0);
            TVLyricsLayout.setSizeAndMargins(linearLayoutAsLinear, i4, -2, i3, iDp, 0, 0);
            linearLayoutAsLinear.setOrientation(TVLyricsLayout.MODE_LYRICS);
            linearLayoutAsLinear.setGravity(TVLyricsLayout.MODE_LYRICS);
            linearLayoutAsLinear.setPadding(0, 0, 0, 0);
            if (viewFindViewById5 != null) {
                viewFindViewById5.setLayoutParams(new LinearLayout.LayoutParams(iMin, iMin));
            }
            TVLyricsLayout.fill(viewFindViewById6);
            TVLyricsLayout.fill(viewFindViewById7);
            keepRetainedArtwork(view);
            LinearLayout linearLayout = new LinearLayout(view.getContext());
            linearLayout.setOrientation(0);
            linearLayout.setGravity(16);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(TVLyricsLayout.UNSET, -2);
            layoutParams.topMargin = TVLyricsLayout.dp(view, 10);
            linearLayoutAsLinear.addView(linearLayout, layoutParams);
            this.addedViews.add(linearLayout);
            TVLyricsLayout.move(viewFindViewById8, linearLayout, new LinearLayout.LayoutParams(0, -2, 1.0f));
            TVLyricsLayout.move(viewFindViewById9, linearLayout, new LinearLayout.LayoutParams(TVLyricsLayout.dp(view, 44), TVLyricsLayout.dp(view, 52)));
            TVLyricsLayout.move(viewFindViewById10, linearLayout, new LinearLayout.LayoutParams(TVLyricsLayout.dp(view, 44), TVLyricsLayout.dp(view, 52)));
            if (viewFindViewById11 instanceof TextView) {
                ((TextView) viewFindViewById11).setTextSize(TVLyricsLayout.MODE_QUEUE, 16.0f);
            }
            if (viewFindViewById12 instanceof TextView) {
                ((TextView) viewFindViewById12).setTextSize(TVLyricsLayout.MODE_QUEUE, 16.0f);
            }
            if (viewFindViewById != null) {
                TVLyricsLayout.clearConstraints(viewFindViewById.getLayoutParams());
                TVLyricsLayout.setInt(viewFindViewById.getLayoutParams(), "t", 0);
                TVLyricsLayout.setInt(viewFindViewById.getLayoutParams(), "v", 0);
                TVLyricsLayout.setInt(viewFindViewById.getLayoutParams(), "i", 0);
                TVLyricsLayout.setSizeAndMargins(viewFindViewById, 0, -2, i3 + i4 + i5, iDp, i6, 0);
            }
            if (viewFindViewById2 != null) {
                TVLyricsLayout.clearConstraints(viewFindViewById2.getLayoutParams());
                TVLyricsLayout.setInt(viewFindViewById2.getLayoutParams(), "t", 0);
                TVLyricsLayout.setInt(viewFindViewById2.getLayoutParams(), "v", 0);
                TVLyricsLayout.setInt(viewFindViewById2.getLayoutParams(), "j", TVLyricsLayout.ID_PLAYER_ACTION_BUTTONS);
                TVLyricsLayout.setSizeAndMargins(viewFindViewById2, 0, -2, i3 + i4 + i5, 0, i6, 0);
            }
            TVLyricsLayout.clearConstraints(viewFindViewById3.getLayoutParams());
            TVLyricsLayout.setInt(viewFindViewById3.getLayoutParams(), "t", 0);
            TVLyricsLayout.setInt(viewFindViewById3.getLayoutParams(), "v", 0);
            TVLyricsLayout.setInt(viewFindViewById3.getLayoutParams(), "j", TVLyricsLayout.ID_STICKY_HEADER_CLIP);
            TVLyricsLayout.setInt(viewFindViewById3.getLayoutParams(), "l", 0);
            TVLyricsLayout.setSizeAndMargins(viewFindViewById3, 0, 0, i3 + i4 + i5, 0, i6, iDp);
            TVLyricsLayout.clearConstraints(viewFindViewById4.getLayoutParams());
            TVLyricsLayout.setInt(viewFindViewById4.getLayoutParams(), "t", TVLyricsLayout.ID_CURRENT_PLAYER_ITEM);
            TVLyricsLayout.setInt(viewFindViewById4.getLayoutParams(), "v", TVLyricsLayout.ID_CURRENT_PLAYER_ITEM);
            TVLyricsLayout.setInt(viewFindViewById4.getLayoutParams(), "l", 0);
            TVLyricsLayout.setSizeAndMargins(viewFindViewById4, i4, TVLyricsLayout.dp(view, 180), 0, 0, 0, TVLyricsLayout.dp(view, 10));
            configureControlGeometry(view);
            disableFullscreenTapTarget(view.findViewById(TVLyricsLayout.ID_CONTROLS_TAP_TARGET));
            view.requestLayout();
            hidePanelLeftCopies(view);
            animateSharedPlayerToPanel();
            animateIncomingPanelIfNeeded(view);
            startPanelPersistence(view, TVLyricsLayout.MODE_QUEUE);
            activateNativeQueueFragment(view);
            startLyricsAvailabilityWatch();
            Log.i(TVLyricsLayout.TAG, "STATE=QUEUE root-parent-preserved=true");
        }

        void activateNativeQueueFragment(final View view) {
            if (view == null) {
                return;
            }
            view.post(new Runnable() {
                int attempts;

                @Override // java.lang.Runnable
                public void run() {
                    if (TVLyricsLayout.active != Controller.this || !Controller.this.expanded || Controller.this.restoring || Controller.this.mode != TVLyricsLayout.MODE_QUEUE || Controller.this.panelRoot != view) {
                        return;
                    }
                    Object tag = view.getTag(TVLyricsLayout.ID_FRAGMENT_CONTAINER_VIEW_TAG);
                    if (tag == null) {
                        ViewParent parent = view.getParent();
                        while (true) {
                            Object obj = parent;
                            if (!(obj instanceof View) || tag != null) {
                                break;
                            }
                            View view2 = (View) obj;
                            tag = view2.getTag(TVLyricsLayout.ID_FRAGMENT_CONTAINER_VIEW_TAG);
                            parent = view2.getParent();
                        }
                    }
                    if (tag == null) {
                        int i = this.attempts + TVLyricsLayout.MODE_LYRICS;
                        this.attempts = i;
                        if (i < 30) {
                            TVLyricsLayout.MAIN.postDelayed(this, 16L);
                            return;
                        } else {
                            Log.w(TVLyricsLayout.TAG, "QUEUE_NATIVE_SYNC fragment-tag-missing=true");
                            return;
                        }
                    }
                    if (Controller.this.activatedQueueFragment != tag) {
                        try {
                            Method methodFindNoArgMethod = TVLyricsLayout.findNoArgMethod(tag.getClass(), "Q1");
                            if (methodFindNoArgMethod == null) {
                                Log.w(TVLyricsLayout.TAG, "QUEUE_NATIVE_SYNC Q1-missing=true class=" + tag.getClass().getName());
                                return;
                            }
                            methodFindNoArgMethod.setAccessible(true);
                            methodFindNoArgMethod.invoke(tag, new Object[0]);
                            int iDrainDeferredQueueTransition = Controller.this.drainDeferredQueueTransition(tag);
                            Controller.this.activatedQueueFragment = tag;
                            Log.i(TVLyricsLayout.TAG, "QUEUE_NATIVE_SYNC invoked=true class=" + tag.getClass().getSimpleName() + " deferred-drained=" + iDrainDeferredQueueTransition);
                        } catch (Throwable th) {
                            Log.w(TVLyricsLayout.TAG, "QUEUE_NATIVE_SYNC failed=" + th.getClass().getSimpleName());
                        }
                    }
                }
            });
        }

        int drainDeferredQueueTransition(Object obj) throws IllegalAccessException {
            Class<?> superclass = obj.getClass();
            while (true) {
                Class<?> cls = superclass;
                if (cls != null && cls != Object.class) {
                    Field[] declaredFields = cls.getDeclaredFields();
                    int length = declaredFields.length;
                    for (int i = 0; i < length; i += TVLyricsLayout.MODE_LYRICS) {
                        Field field = declaredFields[i];
                        field.setAccessible(true);
                        Object obj2 = field.get(obj);
                        if (obj2 instanceof Queue) {
                            Queue queue = (Queue) obj2;
                            ArrayList arrayList = new ArrayList();
                            while (true) {
                                Object objPoll = queue.poll();
                                if (objPoll == null) {
                                    break;
                                }
                                if (objPoll instanceof Runnable) {
                                    arrayList.add((Runnable) objPoll);
                                }
                            }
                            field.set(obj, null);
                            Iterator it = arrayList.iterator();
                            while (it.hasNext()) {
                                TVLyricsLayout.MAIN.post((Runnable) it.next());
                            }
                            return arrayList.size();
                        }
                    }
                    superclass = cls.getSuperclass();
                } else {
                    return 0;
                }
            }
        }

        void installQueueLyricsOverlay(final View view) {
            ViewGroup viewGroup;
            if (this.windowRoot instanceof ViewGroup) {
                viewGroup = (ViewGroup) this.windowRoot;
            } else {
                viewGroup = this.sheet instanceof ViewGroup ? (ViewGroup) this.sheet : null;
            }
            final ViewGroup viewGroup2 = viewGroup;
            if (viewGroup2 == null) {
                return;
            }
            if (this.queueLyricsOverlay != null && this.queueLyricsOverlay.getParent() != null) {
                this.queueLyricsOverlay.bringToFront();
            } else {
                final int i = this.persistenceGeneration;
                view.post(new Runnable() {
                    int attempts;

                    @Override // java.lang.Runnable
                    public void run() {
                        if (TVLyricsLayout.active != Controller.this || Controller.this.mode != TVLyricsLayout.MODE_QUEUE || Controller.this.panelMode != TVLyricsLayout.MODE_QUEUE || i != Controller.this.persistenceGeneration) {
                            return;
                        }
                        final View viewFindViewById = view.findViewById(TVLyricsLayout.ID_PLAYER_LYRICS);
                        if (viewFindViewById == null || viewFindViewById.getWidth() <= 0 || viewFindViewById.getHeight() <= 0) {
                            int i2 = this.attempts + TVLyricsLayout.MODE_LYRICS;
                            this.attempts = i2;
                            if (i2 < 30) {
                                TVLyricsLayout.MAIN.postDelayed(this, 16L);
                                return;
                            }
                            return;
                        }
                        int[] iArr = new int[TVLyricsLayout.MODE_QUEUE];
                        int[] iArr2 = new int[TVLyricsLayout.MODE_QUEUE];
                        viewFindViewById.getLocationInWindow(iArr);
                        viewGroup2.getLocationInWindow(iArr2);
                        View view2 = new View(view.getContext());
                        Controller.this.queueLyricsOverlay = view2;
                        view2.setBackgroundColor(0);
                        view2.setContentDescription(viewFindViewById.getContentDescription());
                        view2.setClickable(true);
                        view2.setFocusable(true);
                        view2.setElevation(TVLyricsLayout.dp(view2, 2000));
                        view2.setTranslationZ(TVLyricsLayout.dp(view2, 2000));
                        view2.setOnClickListener(new View.OnClickListener() {
                            @Override // android.view.View.OnClickListener
                            public void onClick(View view3) {
                                if (TVLyricsLayout.active != Controller.this || Controller.this.mode != TVLyricsLayout.MODE_QUEUE) {
                                    return;
                                }
                                if (viewFindViewById.isEnabled() && viewFindViewById.performClick()) {
                                    Log.i(TVLyricsLayout.TAG, "QUEUE -> LYRICS native button click=true");
                                    return;
                                }
                                Controller.this.mode = TVLyricsLayout.MODE_LYRICS;
                                Controller.this.startLyricsAvailabilityWatch();
                                Controller.this.reapplyRetainedPanelWhenShown(TVLyricsLayout.MODE_LYRICS);
                                if (!TVLyricsLayout.invokeNativePanelMode(view3, false)) {
                                    Controller.this.mode = TVLyricsLayout.MODE_QUEUE;
                                    Controller.this.startPanelPersistence(view, TVLyricsLayout.MODE_QUEUE);
                                    Log.w(TVLyricsLayout.TAG, "QUEUE -> LYRICS native invocation failed");
                                    return;
                                }
                                Log.i(TVLyricsLayout.TAG, "QUEUE -> LYRICS overlay invocation=true");
                            }
                        });
                        int iMax = Math.max(viewFindViewById.getWidth(), TVLyricsLayout.dp(viewFindViewById, 60));
                        int iMax2 = Math.max(viewFindViewById.getHeight(), TVLyricsLayout.dp(viewFindViewById, 60));
                        viewGroup2.addView(view2, new ViewGroup.LayoutParams(iMax, iMax2));
                        int i3 = view.getResources().getDisplayMetrics().widthPixels / 8;
                        view2.setX(i3 - iArr2[0]);
                        view2.setY(iArr[TVLyricsLayout.MODE_LYRICS] - iArr2[TVLyricsLayout.MODE_LYRICS]);
                        view2.bringToFront();
                        Controller.this.addedViews.add(view2);
                        Log.i(TVLyricsLayout.TAG, "QUEUE_LYRICS_OVERLAY positioned=" + i3 + "," + iArr[TVLyricsLayout.MODE_LYRICS] + " size=" + iMax + "x" + iMax2 + " host=" + TVLyricsLayout.describe(viewGroup2));
                    }
                });
            }
        }

        void installActiveModeHomeOverlay(final View view, final int i, final int i2) {
            ViewGroup viewGroup;
            if (this.windowRoot instanceof ViewGroup) {
                viewGroup = (ViewGroup) this.windowRoot;
            } else {
                viewGroup = this.sheet instanceof ViewGroup ? (ViewGroup) this.sheet : null;
            }
            final ViewGroup viewGroup2 = viewGroup;
            if (viewGroup2 == null) {
                return;
            }
            if (this.activeModeHomeOverlay != null && (this.activeModeHomeOverlay.getParent() instanceof ViewGroup)) {
                ((ViewGroup) this.activeModeHomeOverlay.getParent()).removeView(this.activeModeHomeOverlay);
                this.addedViews.remove(this.activeModeHomeOverlay);
                this.activeModeHomeOverlay = null;
            }
            final int i3 = this.persistenceGeneration;
            view.post(new Runnable() {
                int attempts;

                @Override // java.lang.Runnable
                public void run() {
                    if (TVLyricsLayout.active != Controller.this || Controller.this.mode != i2 || Controller.this.panelMode != i2 || i3 != Controller.this.persistenceGeneration) {
                        return;
                    }
                    View viewFindViewById = view.findViewById(i);
                    if (viewFindViewById == null || viewFindViewById.getWidth() <= 0 || viewFindViewById.getHeight() <= 0) {
                        int i4 = this.attempts + TVLyricsLayout.MODE_LYRICS;
                        this.attempts = i4;
                        if (i4 < 30) {
                            TVLyricsLayout.MAIN.postDelayed(this, 16L);
                            return;
                        }
                        return;
                    }
                    int[] iArr = new int[TVLyricsLayout.MODE_QUEUE];
                    int[] iArr2 = new int[TVLyricsLayout.MODE_QUEUE];
                    viewFindViewById.getLocationInWindow(iArr);
                    viewGroup2.getLocationInWindow(iArr2);
                    View view2 = new View(view.getContext());
                    Controller.this.activeModeHomeOverlay = view2;
                    view2.setBackgroundColor(0);
                    view2.setContentDescription(viewFindViewById.getContentDescription());
                    view2.setClickable(true);
                    view2.setFocusable(true);
                    view2.setElevation(TVLyricsLayout.dp(view2, 2100));
                    view2.setTranslationZ(TVLyricsLayout.dp(view2, 2100));
                    view2.setOnClickListener(new View.OnClickListener() {
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view3) {
                            String str;
                            if (TVLyricsLayout.active == Controller.this && Controller.this.mode == i2) {
                                Controller controller = Controller.this;
                                if (i2 == TVLyricsLayout.MODE_QUEUE) {
                                    str = "queue-overlay-toggle";
                                } else {
                                    str = "lyrics-overlay-toggle";
                                }
                                controller.enterHome(str);
                            }
                        }
                    });
                    int iMax = Math.max(viewFindViewById.getWidth(), TVLyricsLayout.dp(viewFindViewById, 60));
                    viewGroup2.addView(view2, new ViewGroup.LayoutParams(iMax, Math.max(viewFindViewById.getHeight(), TVLyricsLayout.dp(viewFindViewById, 60))));
                    int i5 = view.getResources().getDisplayMetrics().widthPixels;
                    int i6 = (i5 * 5) / 16;
                    int iMax2 = i5 / 8;
                    if (i2 == TVLyricsLayout.MODE_QUEUE) {
                        iMax2 += Math.max(0, i6 - iMax);
                    }
                    view2.setX(iMax2 - iArr2[0]);
                    view2.setY(iArr[TVLyricsLayout.MODE_LYRICS] - iArr2[TVLyricsLayout.MODE_LYRICS]);
                    view2.bringToFront();
                    Controller.this.addedViews.add(view2);
                    Log.i(TVLyricsLayout.TAG, "ACTIVE_MODE_HOME_OVERLAY mode=" + i2 + " positioned=" + iMax2 + "," + iArr[TVLyricsLayout.MODE_LYRICS]);
                }
            });
        }

        void enterHome(String str) {
            if (!this.expanded) {
                return;
            }
            int i = this.mode;
            // Once QUEUE owns the decoder output, returning to HOME must not
            // invoke Apple's SONG transition.  That transition creates a new
            // Fragment/TextureView and is the exact source of the reload/black
            // frame seen in compile 8/9.  Keep the live queue surface attached
            // and animate only its existing artwork container instead.
            boolean retainedQueueVideo = i == TVLyricsLayout.MODE_QUEUE
                    && retainQueueVideoForHome(this.panelRoot, str);
            this.mode = 0;
            this.panelExitGeneration += TVLyricsLayout.MODE_LYRICS;
            this.panelShowGeneration += TVLyricsLayout.MODE_LYRICS;
            this.panelTransitioning = false;
            this.pendingPanelMode = 0;
            this.pendingPanelView = null;
            this.panelAnimationUntil = 0L;
            // A retained QUEUE TextureView is still owned by the queue Fragment.
            // Restoring the native HOME underlay here makes Apple's fragment
            // controller remove that queue Fragment, which detaches the live
            // TextureView and crashes its SurfaceTexture listener before its
            // internal release queue has been initialized.  Keep the native
            // underlay hidden for the retained-video HOME; the shared player
            // already supplies every visible HOME control.  Normal/audio HOME
            // and the final sheet restore still take the original restore path.
            if (!retainedQueueVideo) {
                restoreNativeHomeUnderlays();
            }
            View view = this.panelRoot;
            if (view != null) {
                if (retainedQueueVideo) {
                    this.homeRetainedMode = i;
                    startLyricsAvailabilityWatch();
                    animateSharedPlayerToHome(str);
                    applySharedVideoPresentation("retained-queue-home");
                    Log.i(TVLyricsLayout.TAG, "STATE=HOME reason=" + str + " retained-live-video=QUEUE");
                    return;
                }
                clearRetainedQueueVideo();
                restorePanelLayout(str);
                hideInactivePanel(view);
                this.homeRetainedMode = i;
                startLyricsAvailabilityWatch();
                animateSharedPlayerToHome(str);
                Log.i(TVLyricsLayout.TAG, "STATE=HOME reason=" + str + " retained-panel=" + i);
                return;
            }
            this.homeRetainedMode = 0;
            startLyricsAvailabilityWatch();
            animateSharedPlayerToHome(str);
            Log.i(TVLyricsLayout.TAG, "STATE=HOME reason=" + str);
        }

        boolean showRetainedPanelFromHome(int i) {
            View view = this.panelRoot;
            if (view == null || !view.isAttachedToWindow()) {
                this.homeRetainedMode = 0;
                return false;
            }
            if (this.homeRetainedMode != i) {
                return false;
            }
            if (view.findViewById(i == TVLyricsLayout.MODE_QUEUE ? TVLyricsLayout.ID_QUEUE_MAIN_CONTENT : TVLyricsLayout.ID_LYRICS_MAIN_CONTENT) == null) {
                this.homeRetainedMode = 0;
                return false;
            }
            beginSharedPlayerTransition();
            this.mode = i;
            showPanel(view);
            if (i == TVLyricsLayout.MODE_QUEUE) {
                // Establish the queue's final content/layout first.  The old
                // order started the video return animation and immediately
                // cancelled it from applyQueueLayout/showPanelNativeVideoOnly,
                // leaving HOME x/y/scale on top of QUEUE content.
                applyQueueLayout(view);
                restoreRetainedQueueVideoToPanel(true);
                // The retained queue surface is only the video.  Title,
                // progress and transport controls still belong to the shared
                // player and must make the same HOME -> panel transition on
                // every re-entry, not only on the first native fragment load.
                animateSharedPlayerToPanel();
            } else {
                applyLyricsLayout(view);
            }
            Log.i(TVLyricsLayout.TAG, "MODE_SWITCH restored-retained target=" + (i == TVLyricsLayout.MODE_QUEUE ? "QUEUE" : "LYRICS"));
            return true;
        }

        void switchPanelAnimated(final View view, final int i) {
            if (this.panelTransitioning) {
                this.pendingPanelMode = i;
                this.pendingPanelView = view;
                Log.i(TVLyricsLayout.TAG, "MODE_SWITCH queued target=" + (i == TVLyricsLayout.MODE_QUEUE ? "QUEUE" : "LYRICS"));
                return;
            }
            if (this.panelRoot == null) {
                return;
            }
            this.panelTransitioning = true;
            final View view2 = this.panelRoot;
            rememberArtwork(view2);
            this.handoffOutgoingRoot = view2;
            this.handoffUntil = System.currentTimeMillis() + 2400;
            this.persistenceGeneration += TVLyricsLayout.MODE_LYRICS;
            this.mode = i;
            setSharedModeVisual(i);
            startLyricsAvailabilityWatch();
            animateRightContent(view2, this.panelMode, false);
            TVLyricsLayout.MAIN.postDelayed(new Runnable() {
                @Override // java.lang.Runnable
                public void run() {
                    if (TVLyricsLayout.active != Controller.this || !Controller.this.expanded || Controller.this.restoring || Controller.this.mode != i) {
                        Controller.this.panelTransitioning = false;
                        return;
                    }
                    Controller.this.restorePanelLayout("animated-panel-handoff");
                    Controller.this.hideInactivePanel(view2);
                    TVLyricsLayout.releaseStaleNativePanelTransitionLocks(view);
                    if (TVLyricsLayout.invokeNativePanelMode(view, i == TVLyricsLayout.MODE_QUEUE)) {
                        Controller.this.reapplyRetainedPanelWhenShown(i);
                        Log.i(TVLyricsLayout.TAG, "MODE_SWITCH right-content-only target=" + (i == TVLyricsLayout.MODE_QUEUE ? "QUEUE" : "LYRICS"));
                        return;
                    }
                    Controller.this.panelTransitioning = false;
                    Controller.this.mode = 0;
                    Controller.this.restoreNativeHomeUnderlays();
                    Controller.this.animateSharedPlayerToHome("panel-invocation-failed");
                    Log.w(TVLyricsLayout.TAG, "MODE_SWITCH animated invocation=false");
                }
            }, 210L);
        }

        void animateIncomingPanelIfNeeded(final View view) {
            if (!this.panelTransitioning || view == null) {
                return;
            }
            this.panelAnimationUntil = System.currentTimeMillis() + 290;
            animateRightContent(view, this.panelMode, true);
            TVLyricsLayout.MAIN.postDelayed(new Runnable() {
                @Override // java.lang.Runnable
                public void run() {
                    if (TVLyricsLayout.active == Controller.this && view == Controller.this.panelRoot) {
                        Controller.this.panelTransitioning = false;
                        Controller.this.panelAnimationUntil = 0L;
                        int i = Controller.this.pendingPanelMode;
                        View view2 = Controller.this.pendingPanelView;
                        Controller.this.pendingPanelMode = 0;
                        Controller.this.pendingPanelView = null;
                        if (i != 0 && i != Controller.this.mode && view2 != null) {
                            Controller.this.switchPanelAnimated(view2, i);
                        }
                    }
                }
            }, 290L);
        }

        void animateRightContent(View view, int i, boolean z) {
            if (view == null) {
                return;
            }
            ArrayList<View> arrayList = new ArrayList<>();
            if (i == TVLyricsLayout.MODE_QUEUE) {
                addAnimationView(arrayList, view.findViewById(TVLyricsLayout.ID_PLAYER_ACTION_BUTTONS));
                addAnimationView(arrayList, view.findViewById(TVLyricsLayout.ID_STICKY_HEADER_CLIP));
            }
            addAnimationView(arrayList, view.findViewById(TVLyricsLayout.ID_RECYCLER_GRADIENTS));
            if (i == TVLyricsLayout.MODE_LYRICS) {
                addAnimationView(arrayList, view.findViewById(TVLyricsLayout.ID_NO_LYRICS_AVAILABLE));
                addAnimationView(arrayList, view.findViewById(TVLyricsLayout.ID_LOADING_PROGRESS));
                addAnimationView(arrayList, view.findViewById(TVLyricsLayout.ID_TRANSLATIONS_BUTTON));
                addAnimationView(arrayList, view.findViewById(TVLyricsLayout.ID_VOCAL_CONTROL));
            }
            int iMax = Math.max(TVLyricsLayout.dp(view, 72), view.getWidth() / 12);
            PathInterpolator pathInterpolator = new PathInterpolator(0.22f, 1.0f, 0.36f, 1.0f);
            for (View view2 : arrayList) {
                view2.animate().cancel();
                if (z) {
                    view2.setTranslationX(iMax);
                    view2.setAlpha(0.0f);
                    view2.animate().translationX(0.0f).alpha(1.0f).setInterpolator(pathInterpolator).setDuration(280L).start();
                } else {
                    view2.animate().translationX(-iMax).alpha(0.0f).setInterpolator(pathInterpolator).setDuration(220L).start();
                }
            }
        }

        void addAnimationView(ArrayList<View> arrayList, View view) {
            if (view != null && !arrayList.contains(view)) {
                arrayList.add(view);
            }
        }

        void disableFullscreenTapTarget(View view) {
            if (view == null) {
                return;
            }
            view.animate().cancel();
            view.clearAnimation();
            view.setVisibility(4);
            view.setClickable(false);
            view.setLongClickable(false);
            view.setFocusable(false);
            view.setFocusableInTouchMode(false);
            view.setEnabled(false);
        }

        void beginNativePanelExit(final View view) {
            if (!this.expanded || view == null) {
                return;
            }
            if (view == this.handoffOutgoingRoot && System.currentTimeMillis() <= this.handoffUntil) {
                Log.i(TVLyricsLayout.TAG, "NATIVE_PANEL_EXIT ignored-handoff-panel=true mode=" + this.mode);
                this.handoffOutgoingRoot = null;
                this.handoffUntil = 0L;
                return;
            }
            if (System.currentTimeMillis() > this.handoffUntil) {
                this.handoffOutgoingRoot = null;
                this.handoffUntil = 0L;
            }
            if (this.mode != 0 && view != this.panelRoot) {
                Log.i(TVLyricsLayout.TAG, "NATIVE_PANEL_EXIT ignored-stale-panel=true mode=" + this.mode);
                return;
            }
            final int i = this.panelExitGeneration + TVLyricsLayout.MODE_LYRICS;
            this.panelExitGeneration = i;
            final int[] iArr = {0};
            final int[] iArr2 = {0};
            Log.i(TVLyricsLayout.TAG, "NATIVE_PANEL_EXIT waiting-for-hidden=true");
            TVLyricsLayout.MAIN.post(new Runnable() {
                @Override // java.lang.Runnable
                public void run() {
                    if (TVLyricsLayout.active != Controller.this || !Controller.this.expanded || Controller.this.restoring || i != Controller.this.panelExitGeneration) {
                        return;
                    }
                    if (!((view.isAttachedToWindow() && view.getVisibility() == 0 && view.isShown()) ? false : true)) {
                        if (iArr[0] > 0) {
                            Log.i(TVLyricsLayout.TAG, "NATIVE_PANEL_EXIT transient-hide-ignored=true");
                            return;
                        }
                        int[] iArr3 = iArr2;
                        int i2 = iArr3[0] + TVLyricsLayout.MODE_LYRICS;
                        iArr3[0] = i2;
                        if (i2 <= 240) {
                            TVLyricsLayout.MAIN.postDelayed(this, 16L);
                            return;
                        } else {
                            Log.i(TVLyricsLayout.TAG, "NATIVE_PANEL_EXIT no-hide-timeout=true");
                            return;
                        }
                    }
                    int[] iArr4 = iArr;
                    int i3 = iArr4[0] + TVLyricsLayout.MODE_LYRICS;
                    iArr4[0] = i3;
                    if (i3 < 8) {
                        TVLyricsLayout.MAIN.postDelayed(this, 16L);
                        return;
                    }
                    Controller.this.mode = 0;
                    Controller.this.restoreNativeHomeUnderlays();
                    Controller.this.restorePanelLayout("native-panel-hidden");
                    Controller.this.restoreSharedPlayerImmediate("native-panel-hidden");
                    Controller.this.startLyricsAvailabilityWatch();
                    Log.i(TVLyricsLayout.TAG, "STATE=HOME reason=native-panel-hidden");
                }
            });
        }

        void reapplyRetainedPanelWhenShown(final int i) {
            final int i2 = this.panelShowGeneration + TVLyricsLayout.MODE_LYRICS;
            this.panelShowGeneration = i2;
            TVLyricsLayout.MAIN.post(new Runnable() {
                @Override // java.lang.Runnable
                public void run() {
                    if (TVLyricsLayout.active != Controller.this || !Controller.this.expanded || Controller.this.restoring || Controller.this.mode != i || i2 != Controller.this.panelShowGeneration) {
                        return;
                    }
                    int i3 = i == TVLyricsLayout.MODE_QUEUE ? TVLyricsLayout.ID_QUEUE_MAIN_CONTENT : TVLyricsLayout.ID_LYRICS_MAIN_CONTENT;
                    View viewFindVisible = TVLyricsLayout.findVisible(Controller.this.windowRoot, i3);
                    if (viewFindVisible == null) {
                        viewFindVisible = TVLyricsLayout.findAttached(Controller.this.windowRoot, i3);
                    }
                    View viewFindPanelRoot = TVLyricsLayout.findPanelRoot(viewFindVisible);
                    if (viewFindPanelRoot == null || !viewFindPanelRoot.isAttachedToWindow() || !viewFindPanelRoot.isShown() || viewFindPanelRoot.getWidth() <= 0 || viewFindPanelRoot.getHeight() <= 0) {
                        TVLyricsLayout.MAIN.postDelayed(this, 16L);
                        return;
                    }
                    // Native fragment hand-off reapplies the phone/tablet side-player
                    // constraints to the host. The panel itself remains populated and
                    // alpha=1, but its 720 px right column is clipped by the restored
                    // 640 px mini-player ancestor. Reassert the already-captured
                    // fullscreen host constraints only after the incoming fragment is
                    // attached; this does not move or recreate the panel contents.
                    Controller.this.expandInPlace();
                    Controller.this.showPanel(viewFindPanelRoot);
                    if (i == TVLyricsLayout.MODE_QUEUE) {
                        Controller.this.applyQueueLayout(viewFindPanelRoot);
                    } else {
                        Controller.this.applyLyricsLayout(viewFindPanelRoot);
                    }
                    Controller.this.stabilizeRightContentVisuals(viewFindPanelRoot, i);
                    final View view2 = viewFindPanelRoot;
                    TVLyricsLayout.MAIN.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (TVLyricsLayout.active == Controller.this && Controller.this.expanded && Controller.this.mode == i && i2 == Controller.this.panelShowGeneration) {
                                Controller.this.expandInPlace();
                                Controller.this.stabilizeRightContentVisuals(view2, i);
                            }
                        }
                    }, 340L);
                    Log.i(TVLyricsLayout.TAG, "RETAINED_PANEL shown-and-reapplied mode=" + i);
                }
            });
        }

        void stabilizeRightContentVisuals(View view, int i) {
            if (view == null) {
                return;
            }
            view.animate().cancel();
            view.setVisibility(View.VISIBLE);
            view.setAlpha(1.0f);
            view.setTranslationX(0.0f);
            view.setTranslationY(0.0f);
            int[] iArr = {TVLyricsLayout.ID_RECYCLER_GRADIENTS, TVLyricsLayout.ID_PLAYER_ACTION_BUTTONS, TVLyricsLayout.ID_STICKY_HEADER_CLIP};
            for (int i2 : iArr) {
                View viewFindViewById = view.findViewById(i2);
                if (viewFindViewById != null) {
                    viewFindViewById.animate().cancel();
                    viewFindViewById.setTranslationX(0.0f);
                    viewFindViewById.setTranslationY(0.0f);
                    viewFindViewById.setAlpha(1.0f);
                    if (i2 == TVLyricsLayout.ID_RECYCLER_GRADIENTS || i == TVLyricsLayout.MODE_QUEUE) {
                        viewFindViewById.setVisibility(View.VISIBLE);
                    }
                }
            }
            Log.i(TVLyricsLayout.TAG, "RIGHT_CONTENT stabilized mode=" + i + " root=" + TVLyricsLayout.describe(view));
        }

        void startPanelPersistence(final View view, final int i) {
            this.persistenceGeneration += TVLyricsLayout.MODE_LYRICS;
            this.persistentViews.clear();
            this.persistentSeen.clear();
            this.activePanelRoot = view;
            this.activeCurrent = view.findViewById(TVLyricsLayout.ID_CURRENT_PLAYER_ITEM);
            this.activeRecycler = view.findViewById(TVLyricsLayout.ID_RECYCLER_GRADIENTS);
            this.activeMainContent = view.findViewById(i == TVLyricsLayout.MODE_QUEUE ? TVLyricsLayout.ID_QUEUE_MAIN_CONTENT : TVLyricsLayout.ID_LYRICS_MAIN_CONTENT);
            this.activeControls = view.findViewById(TVLyricsLayout.ID_CONTROLS);
            this.activeArtwork = view.findViewById(i == TVLyricsLayout.MODE_QUEUE ? TVLyricsLayout.ID_QUEUE_THUMBNAIL_CONTAINER : TVLyricsLayout.ID_LYRICS_THUMBNAIL_CONTAINER);
            this.activeVideo = view.findViewById(TVLyricsLayout.ID_VIDEO_SURFACE);
            this.activeThumbnail = view.findViewById(TVLyricsLayout.ID_THUMBNAIL);
            this.queueDiagnosticCount = 0;
            if (!this.sharedPlayerActive) {
                collectVisibleDescendants(this.activeControls);
                addPersistent(view.findViewById(TVLyricsLayout.ID_PREVIOUS_REWIND));
                addPersistent(view.findViewById(TVLyricsLayout.ID_PLAY_PAUSE));
                addPersistent(view.findViewById(TVLyricsLayout.ID_NEXT_FAST_FORWARD));
                addPersistent(view.findViewById(TVLyricsLayout.ID_SEEK_BAR_CONTROLS));
                addPersistent(view.findViewById(TVLyricsLayout.ID_PLAYER_LYRICS));
                addPersistent(view.findViewById(TVLyricsLayout.ID_PLAYER_QUEUE));
                addPersistent(view.findViewById(TVLyricsLayout.ID_MEDIA_ROUTE_BUTTON));
            }
            if (i == TVLyricsLayout.MODE_QUEUE) {
                collectVisibleDescendants(this.activeRecycler);
            }
            if (i == TVLyricsLayout.MODE_LYRICS) {
                applyFullscreenLyricsGradient();
            } else if (i == TVLyricsLayout.MODE_QUEUE) {
                applyFullscreenQueueGradient();
            }
            hideNativeHomeUnderlay(view);
            final int i2 = this.persistenceGeneration;
            TVLyricsLayout.MAIN.post(new Runnable() {
                @Override // java.lang.Runnable
                public void run() {
                    if (TVLyricsLayout.active != Controller.this || !Controller.this.expanded || Controller.this.restoring || Controller.this.mode != i || Controller.this.panelMode != i || i2 != Controller.this.persistenceGeneration || Controller.this.activePanelRoot != view) {
                        return;
                    }
                    long jCurrentTimeMillis = Controller.this.panelAnimationUntil - System.currentTimeMillis();
                    if (jCurrentTimeMillis > 0) {
                        TVLyricsLayout.MAIN.postDelayed(this, jCurrentTimeMillis);
                    } else {
                        Controller.this.keepPanelStable(i);
                        TVLyricsLayout.MAIN.postDelayed(this, 250L);
                    }
                }
            });
        }

        void collectVisibleDescendants(View view) {
            if (view == null) {
                return;
            }
            if (view.getVisibility() == 0) {
                addPersistent(view);
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i += TVLyricsLayout.MODE_LYRICS) {
                    collectVisibleDescendants(viewGroup.getChildAt(i));
                }
            }
        }

        void addPersistent(View view) {
            if (view != null && !this.persistentSeen.containsKey(view)) {
                this.persistentSeen.put(view, Boolean.TRUE);
                this.persistentViews.add(view);
            }
        }

        void keepPanelStable(int i) {
            hideNativeHomeUnderlay(this.activePanelRoot);
            keepVisible(this.activePanelRoot, true);
            keepVisible(this.activeRecycler, true);
            keepVisible(this.activeMainContent, true);
            if (this.sharedPlayerActive) {
                hidePanelLeftCopies(this.activePanelRoot);
                setSharedModeVisual(i);
                stabilizeSharedArtworkCard(true);
            } else {
                keepVisible(this.activeCurrent, true);
                keepVisible(this.activeControls, true);
                keepVisible(this.activeArtwork, true);
                keepVisible(this.activeThumbnail, true);
                keepRetainedArtwork(this.activePanelRoot);
                TVLyricsLayout.fill(this.activeVideo);
                TVLyricsLayout.fill(this.activeThumbnail);
            }
            disableFullscreenTapTarget(this.activePanelRoot.findViewById(TVLyricsLayout.ID_CONTROLS_TAP_TARGET));
            Iterator<View> it = this.persistentViews.iterator();
            while (it.hasNext()) {
                keepVisible(it.next(), false);
            }
            if (i == TVLyricsLayout.MODE_LYRICS) {
                stabilizeLyricsViewport();
                applyFullscreenLyricsGradient();
            } else if (i == TVLyricsLayout.MODE_QUEUE) {
                collectVisibleDescendants(this.activeRecycler);
                stabilizeQueueViewport();
                applyFullscreenQueueGradient();
                int i2 = this.queueDiagnosticCount;
                this.queueDiagnosticCount = i2 + TVLyricsLayout.MODE_LYRICS;
                if (i2 < 6) {
                    logQueueGeometry();
                }
            }
            if (this.activePanelRoot != null) {
                this.activePanelRoot.requestLayout();
                this.activePanelRoot.invalidate();
            }
        }

        void applyFullscreenLyricsGradient() {
            if (this.activeRecycler == null || this.panelMode != TVLyricsLayout.MODE_LYRICS) {
                return;
            }
            try {
                this.activeRecycler.getClass().getMethod("d", Integer.TYPE, Integer.TYPE).invoke(this.activeRecycler, Integer.valueOf(TVLyricsLayout.dp(this.activeRecycler, 38)), Integer.valueOf(TVLyricsLayout.dp(this.activeRecycler, 48)));
            } catch (Throwable th) {
                Log.w(TVLyricsLayout.TAG, "LYRICS_GRADIENT failed=" + th.getClass().getSimpleName());
            }
        }

        void applyFullscreenQueueGradient() {
            if (this.activeRecycler == null || this.panelMode != TVLyricsLayout.MODE_QUEUE) {
                return;
            }
            try {
                this.activeRecycler.getClass().getMethod("d", Integer.TYPE, Integer.TYPE).invoke(this.activeRecycler, 0, Integer.valueOf(TVLyricsLayout.dp(this.activeRecycler, 48)));
            } catch (Throwable th) {
                Log.w(TVLyricsLayout.TAG, "QUEUE_GRADIENT failed=" + th.getClass().getSimpleName());
            }
        }

        void stabilizeLyricsViewport() {
            if (this.activeRecycler == null || this.activeControls == null || this.activePanelRoot == null) {
                return;
            }
            int i = this.activePanelRoot.getResources().getDisplayMetrics().widthPixels;
            int i2 = i / 8;
            int i3 = (i * 5) / 16;
            int i4 = i / 16;
            int i5 = i / 8;
            int iDp = TVLyricsLayout.dp(this.activePanelRoot, 30);
            TVLyricsLayout.clearConstraints(this.activeRecycler.getLayoutParams());
            TVLyricsLayout.setInt(this.activeRecycler.getLayoutParams(), "s", TVLyricsLayout.ID_CURRENT_PLAYER_ITEM);
            TVLyricsLayout.setInt(this.activeRecycler.getLayoutParams(), "v", 0);
            TVLyricsLayout.setInt(this.activeRecycler.getLayoutParams(), "i", 0);
            TVLyricsLayout.setInt(this.activeRecycler.getLayoutParams(), "l", 0);
            TVLyricsLayout.setSizeAndMargins(this.activeRecycler, 0, 0, i4, iDp, i5, iDp);
            TVLyricsLayout.clearConstraints(this.activeControls.getLayoutParams());
            TVLyricsLayout.setInt(this.activeControls.getLayoutParams(), "t", TVLyricsLayout.ID_CURRENT_PLAYER_ITEM);
            TVLyricsLayout.setInt(this.activeControls.getLayoutParams(), "v", TVLyricsLayout.ID_CURRENT_PLAYER_ITEM);
            TVLyricsLayout.setInt(this.activeControls.getLayoutParams(), "l", 0);
            TVLyricsLayout.setSizeAndMargins(this.activeControls, i3, TVLyricsLayout.dp(this.activePanelRoot, 180), 0, 0, 0, TVLyricsLayout.dp(this.activePanelRoot, 10));
        }

        void stabilizeQueueViewport() {
            if (this.activeRecycler == null || this.activePanelRoot == null) {
                return;
            }
            int i = this.activePanelRoot.getResources().getDisplayMetrics().widthPixels;
            int i2 = i / 8;
            int i3 = (i * 5) / 16;
            int i4 = i / 16;
            int i5 = i / 8;
            int iDp = TVLyricsLayout.dp(this.activePanelRoot, 30);
            View viewFindViewById = this.activePanelRoot.findViewById(TVLyricsLayout.ID_PLAYER_ACTION_BUTTONS);
            View viewFindViewById2 = this.activePanelRoot.findViewById(TVLyricsLayout.ID_STICKY_HEADER_CLIP);
            if (viewFindViewById != null) {
                TVLyricsLayout.clearConstraints(viewFindViewById.getLayoutParams());
                TVLyricsLayout.setInt(viewFindViewById.getLayoutParams(), "t", 0);
                TVLyricsLayout.setInt(viewFindViewById.getLayoutParams(), "v", 0);
                TVLyricsLayout.setInt(viewFindViewById.getLayoutParams(), "i", 0);
                TVLyricsLayout.setSizeAndMargins(viewFindViewById, 0, -2, i2 + i3 + i4, iDp, i5, 0);
            }
            if (viewFindViewById2 != null) {
                TVLyricsLayout.clearConstraints(viewFindViewById2.getLayoutParams());
                TVLyricsLayout.setInt(viewFindViewById2.getLayoutParams(), "t", 0);
                TVLyricsLayout.setInt(viewFindViewById2.getLayoutParams(), "v", 0);
                TVLyricsLayout.setInt(viewFindViewById2.getLayoutParams(), "j", TVLyricsLayout.ID_PLAYER_ACTION_BUTTONS);
                TVLyricsLayout.setSizeAndMargins(viewFindViewById2, 0, -2, i2 + i3 + i4, 0, i5, 0);
            }
            TVLyricsLayout.clearConstraints(this.activeRecycler.getLayoutParams());
            TVLyricsLayout.setInt(this.activeRecycler.getLayoutParams(), "t", 0);
            TVLyricsLayout.setInt(this.activeRecycler.getLayoutParams(), "v", 0);
            TVLyricsLayout.setInt(this.activeRecycler.getLayoutParams(), "j", TVLyricsLayout.ID_STICKY_HEADER_CLIP);
            TVLyricsLayout.setInt(this.activeRecycler.getLayoutParams(), "l", 0);
            TVLyricsLayout.setSizeAndMargins(this.activeRecycler, 0, 0, i2 + i3 + i4, 0, i5, iDp);
        }

        void logQueueGeometry() {
            Method method;
            int childCount = this.activeMainContent instanceof ViewGroup ? ((ViewGroup) this.activeMainContent).getChildCount() : TVLyricsLayout.UNSET;
            int iIntValue = TVLyricsLayout.UNSET;
            try {
                Object objInvoke = this.activeMainContent.getClass().getMethod("getAdapter", new Class[0]).invoke(this.activeMainContent, new Object[0]);
                if (objInvoke != null) {
                    try {
                        method = objInvoke.getClass().getMethod("getItemCount", new Class[0]);
                    } catch (NoSuchMethodException e) {
                        method = objInvoke.getClass().getMethod("i", new Class[0]);
                    }
                    method.setAccessible(true);
                    Object objInvoke2 = method.invoke(objInvoke, new Object[0]);
                    if (objInvoke2 instanceof Number) {
                        iIntValue = ((Number) objInvoke2).intValue();
                    }
                }
            } catch (Throwable th) {
                Log.w(TVLyricsLayout.TAG, "QUEUE_DIAG adapter-error=" + th.getClass().getSimpleName());
            }
            Log.i(TVLyricsLayout.TAG, "QUEUE_DIAG wrapper=" + TVLyricsLayout.geometry(this.activeRecycler) + " list=" + TVLyricsLayout.geometry(this.activeMainContent) + " children=" + childCount + " adapterItems=" + iIntValue);
        }

        void keepVisible(View view, boolean z) {
            if (view == null) {
                return;
            }
            view.animate().cancel();
            view.clearAnimation();
            view.setVisibility(0);
            view.setAlpha(1.0f);
            if (z) {
                view.setTranslationX(0.0f);
                view.setTranslationY(0.0f);
            }
        }

        void rememberArtwork(View view) {
            if (view == null) {
                return;
            }
            View viewFindViewById = view.findViewById(TVLyricsLayout.ID_ARTWORK_IMAGE);
            if (!(viewFindViewById instanceof ImageView)) {
                return;
            }
            Drawable drawable = ((ImageView) viewFindViewById).getDrawable();
            if (isUsableArtwork(drawable)) {
                this.retainedArtwork = drawable;
                Log.i(TVLyricsLayout.TAG, "ARTWORK retained intrinsic=" + drawable.getIntrinsicWidth() + "x" + drawable.getIntrinsicHeight());
            }
        }

        void keepRetainedArtwork(View view) {
            if (view == null) {
                return;
            }
            View viewFindViewById = view.findViewById(TVLyricsLayout.ID_ARTWORK_IMAGE);
            if (!(viewFindViewById instanceof ImageView)) {
                return;
            }
            ImageView imageView = (ImageView) viewFindViewById;
            Drawable drawable = imageView.getDrawable();
            if (isUsableArtwork(drawable)) {
                this.retainedArtwork = drawable;
            } else if (this.retainedArtwork != null) {
                imageView.setImageDrawable(this.retainedArtwork);
                Log.i(TVLyricsLayout.TAG, "ARTWORK restored-to-panel mode=" + this.mode);
            }
            imageView.setImageAlpha(255);
            imageView.setVisibility(0);
            imageView.setAlpha(1.0f);
        }

        boolean isUsableArtwork(Drawable drawable) {
            return drawable != null && drawable.getIntrinsicWidth() > 64 && drawable.getIntrinsicHeight() > 64;
        }

        void restorePanelLayout(String str) {
            clearRetainedQueueVideo();
            this.persistenceGeneration += TVLyricsLayout.MODE_LYRICS;
            this.persistentViews.clear();
            this.persistentSeen.clear();
            this.activePanelRoot = null;
            this.activeCurrent = null;
            this.activeRecycler = null;
            this.activeMainContent = null;
            this.activeControls = null;
            this.activeArtwork = null;
            this.activeVideo = null;
            this.activeThumbnail = null;
            removeAddedViews();
            if (!this.panelSnapshots.isEmpty()) {
                boolean zRestoreSnapshots = TVLyricsLayout.restoreSnapshots(this.panelSnapshots);
                requestLayouts(this.panelSnapshots);
                Log.i(TVLyricsLayout.TAG, "RESTORE_PANEL reason=" + str + " match=" + zRestoreSnapshots + " count=" + this.panelSnapshots.size());
                this.panelSnapshots.clear();
                this.panelSeen.clear();
                this.panelMode = 0;
                return;
            }
            this.panelMode = 0;
        }

        void hideInactivePanel(View view) {
            if (view == null) {
                return;
            }
            Iterator<HiddenPanelState> it = this.hiddenPanels.iterator();
            while (it.hasNext()) {
                if (it.next().view == view) {
                    view.setVisibility(4);
                    view.setAlpha(0.0f);
                    return;
                }
            }
            this.hiddenPanels.add(new HiddenPanelState(view));
            view.setVisibility(4);
            view.setAlpha(0.0f);
            Log.i(TVLyricsLayout.TAG, "INACTIVE_PANEL hidden=" + TVLyricsLayout.describe(view));
        }

        void showPanel(View view) {
            if (view == null) {
                return;
            }
            for (int size = this.hiddenPanels.size() - TVLyricsLayout.MODE_LYRICS; size >= 0; size += TVLyricsLayout.UNSET) {
                HiddenPanelState hiddenPanelState = this.hiddenPanels.get(size);
                if (hiddenPanelState.view == view) {
                    hiddenPanelState.restore();
                    this.hiddenPanels.remove(size);
                    break;
                }
            }
            view.setVisibility(0);
            view.setAlpha(1.0f);
        }

        void restoreHiddenPanels() {
            for (int size = this.hiddenPanels.size() - TVLyricsLayout.MODE_LYRICS; size >= 0; size += TVLyricsLayout.UNSET) {
                this.hiddenPanels.get(size).restore();
            }
            this.hiddenPanels.clear();
        }

        void concealRetainedPanelsForCollapsedSheet() {
            int i = 0;
            View view = this.panelRoot;
            if (view != null && view.isAttachedToWindow()) {
                view.animate().cancel();
                view.setVisibility(View.INVISIBLE);
                view.setAlpha(0.0f);
                i++;
            }
            for (int size = this.hiddenPanels.size() - TVLyricsLayout.MODE_LYRICS; size >= 0; size += TVLyricsLayout.UNSET) {
                View view2 = this.hiddenPanels.get(size).view;
                if (view2 != view && view2.isAttachedToWindow()) {
                    view2.animate().cancel();
                    view2.setVisibility(View.INVISIBLE);
                    view2.setAlpha(0.0f);
                    i++;
                }
            }
            this.hiddenPanels.clear();
            this.panelRoot = null;
            this.homeRetainedMode = 0;
            Log.i(TVLyricsLayout.TAG, "COLLAPSED_SHEET concealed-player-panels=" + i);
        }

        void captureNativeHomeUnderlay(View view) {
            View viewFindViewById = this.windowRoot.findViewById(TVLyricsLayout.ID_PLAYER_FRAGMENTS_HOST);
            if (!(viewFindViewById instanceof ViewGroup)) {
                return;
            }
            ViewGroup viewGroup = (ViewGroup) viewFindViewById;
            for (int i = 0; i < viewGroup.getChildCount(); i += TVLyricsLayout.MODE_LYRICS) {
                View childAt = viewGroup.getChildAt(i);
                boolean z = childAt.getId() == TVLyricsLayout.ID_NATIVE_HOME_PLAYER;
                boolean z2 = childAt.getVisibility() == 0 && childAt.getAlpha() > 0.0f && childAt.findViewById(TVLyricsLayout.ID_CURRENT_PLAYER_ITEM) != null;
                if (childAt != view && (z || z2)) {
                    boolean z3 = false;
                    Iterator<HiddenPanelState> it = this.nativeHomeUnderlays.iterator();
                    while (it.hasNext()) {
                        if (it.next().view == childAt) {
                            z3 = true;
                            break;
                        }
                    }
                    if (!z3) {
                        this.nativeHomeUnderlays.add(new HiddenPanelState(childAt));
                        Log.i(TVLyricsLayout.TAG, "HOME_UNDERLAY captured=" + TVLyricsLayout.describe(childAt));
                    }
                }
            }
        }

        void hideNativeHomeUnderlay(View view) {
            captureNativeHomeUnderlay(view);
            Iterator<HiddenPanelState> it = this.nativeHomeUnderlays.iterator();
            while (it.hasNext()) {
                View view2 = it.next().view;
                if (view2 != view && view2.isAttachedToWindow()) {
                    if (this.sharedArtworkPinned && this.mode == 0
                            && !this.retainedQueueVideoAtHome
                            && view2 == this.nativeHomeRoot) {
                        view2.setVisibility(View.VISIBLE);
                        view2.setAlpha(1.0f);
                        view2.bringToFront();
                        continue;
                    }
                    view2.setVisibility(4);
                    view2.setAlpha(0.0f);
                }
            }
        }

        void restoreNativeHomeUnderlays() {
            if (this.nativeHomeUnderlays.isEmpty()) {
                return;
            }
            for (int size = this.nativeHomeUnderlays.size() - TVLyricsLayout.MODE_LYRICS; size >= 0; size += TVLyricsLayout.UNSET) {
                this.nativeHomeUnderlays.get(size).restore();
            }
            Log.i(TVLyricsLayout.TAG, "HOME_UNDERLAY restored count=" + this.nativeHomeUnderlays.size());
            this.nativeHomeUnderlays.clear();
        }

        void startLyricsAvailabilityWatch() {
            final int i = this.watchGeneration + TVLyricsLayout.MODE_LYRICS;
            this.watchGeneration = i;
            TVLyricsLayout.MAIN.postDelayed(new Runnable() {
                @Override // java.lang.Runnable
                public void run() {
                    if (TVLyricsLayout.active != Controller.this || !Controller.this.expanded || i != Controller.this.watchGeneration) {
                        return;
                    }
                    Controller.this.refreshSharedArtworkProxy(false);
                    Controller.this.refreshSharedVideoPresentation(false);
                    View view = Controller.this.sharedLyricsButton != null ? Controller.this.sharedLyricsButton : Controller.this.findVisible(TVLyricsLayout.ID_PLAYER_LYRICS);
                    View view2 = Controller.this.panelRoot != null ? Controller.this.panelRoot.findViewById(TVLyricsLayout.ID_PLAYER_LYRICS) : null;
                    if (view2 == null) {
                        view2 = TVLyricsLayout.findAttached(Controller.this.windowRoot, TVLyricsLayout.ID_PLAYER_LYRICS);
                    }
                    View view3 = Controller.this.panelRoot != null ? Controller.this.panelRoot.findViewById(TVLyricsLayout.ID_NO_LYRICS_AVAILABLE) : null;
                    if (view3 == null) {
                        view3 = TVLyricsLayout.findAttached(Controller.this.windowRoot, TVLyricsLayout.ID_NO_LYRICS_AVAILABLE);
                    }
                    boolean z = view3 != null && view3.getVisibility() == View.VISIBLE && view3.getAlpha() > 0.05f;
                    String currentKey = Controller.this.currentArtworkKey();
                    // The QUEUE and LYRICS fragments own separate native copies
                    // of the mode buttons.  Immediately after LYRICS -> QUEUE,
                    // Apple's new QUEUE copy can report enabled=false for one
                    // lifecycle pass even though this exact track has just
                    // displayed real lyrics.  Treat that value as transient,
                    // otherwise it disables the shared button and creates a
                    // self-sustaining grey state.  A visible no-lyrics result
                    // always wins and clears the positive evidence.
                    if (z) {
                        if (currentKey.equals(Controller.this.confirmedLyricsAvailableKey)) {
                            Controller.this.confirmedLyricsAvailableKey = null;
                        }
                    } else if (Controller.this.mode == TVLyricsLayout.MODE_LYRICS
                            && !Controller.this.sharedVideoActive
                            && Controller.this.panelRoot != null
                            && Controller.this.panelRoot.findViewById(TVLyricsLayout.ID_LYRICS_MAIN_CONTENT) != null) {
                        if (!currentKey.equals(Controller.this.confirmedLyricsAvailableKey)) {
                            Controller.this.confirmedLyricsAvailableKey = currentKey;
                            Log.i(TVLyricsLayout.TAG, "LYRICS_AVAILABILITY confirmed-key="
                                    + currentKey.replace('\n', '|'));
                        }
                    }
                    boolean z2 = view2 != null ? view2.isEnabled() : view == null || view.isEnabled();
                    // Music videos have no lyrics page in this landscape
                    // player.  Never inherit the enabled state or retained
                    // lyrics Fragment from the previously playing audio item.
                    boolean retainedPanelVideo = Controller.this.retainedQueueVideoAtHome
                            && Controller.this.isPanelNativeVideoAvailable(Controller.this.retainedQueueVideoRoot);
                    boolean confirmedForCurrent = currentKey.equals(Controller.this.confirmedLyricsAvailableKey);
                    boolean z3 = !Controller.this.sharedVideoActive && !retainedPanelVideo
                            && (z2 || confirmedForCurrent) && !z;
                    if (view != null && view.isEnabled() != z3) {
                        view.setEnabled(z3);
                        view.refreshDrawableState();
                        Log.i(TVLyricsLayout.TAG, "LYRICS_AVAILABILITY shared-enabled=" + z3
                                + " native=" + z2 + " confirmed=" + confirmedForCurrent
                                + " noLyricsView=" + z);
                    }
                    if (Controller.this.mode == TVLyricsLayout.MODE_LYRICS && !z3) {
                        Controller.this.enterHome("lyrics-became-unavailable");
                        return;
                    }
                    // During an audio -> video handoff the only artificial part
                    // of the wait is this sampling cadence.  Probe pending video
                    // frames more frequently, while leaving the normal steady
                    // state at 400 ms to avoid needless UI-thread work.
                    boolean pendingCurrentVideo = currentKey.equals(Controller.this.pendingVideoArtworkKey)
                            && Controller.this.usableBitmapAspect(Controller.this.pendingVideoArtworkBitmap) > 1.30f;
                    TVLyricsLayout.MAIN.postDelayed(this, pendingCurrentVideo ? 140L : 400L);
                }
            }, 160L);
        }

        void stopCallbacks() {
            this.watchGeneration += TVLyricsLayout.MODE_LYRICS;
            this.panelExitGeneration += TVLyricsLayout.MODE_LYRICS;
            this.panelShowGeneration += TVLyricsLayout.MODE_LYRICS;
            this.persistenceGeneration += TVLyricsLayout.MODE_LYRICS;
            this.sharedAnimationGeneration += TVLyricsLayout.MODE_LYRICS;
            this.homeCommitGeneration += TVLyricsLayout.MODE_LYRICS;
            this.handoffOutgoingRoot = null;
            this.handoffUntil = 0L;
            this.retainedArtwork = null;
            this.panelTransitioning = false;
            this.pendingPanelMode = 0;
            this.pendingPanelView = null;
            this.panelAnimationUntil = 0L;
            this.homeRetainedMode = 0;
            removeInputShield();
        }

        void removeAddedViews() {
            for (int size = this.addedViews.size() - TVLyricsLayout.MODE_LYRICS; size >= 0; size += TVLyricsLayout.UNSET) {
                View view = this.addedViews.get(size);
                ViewParent parent = view.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(view);
                }
            }
            this.addedViews.clear();
            this.manualToggle = null;
            this.queueLyricsOverlay = null;
            this.activeModeHomeOverlay = null;
        }

        void installInputShield() {
            if (!(this.sheet instanceof ViewGroup)) {
                return;
            }
            if (this.inputShield == null) {
                this.inputShield = new InputShield(this.sheet.getContext());
            }
            ViewParent parent = this.inputShield.getParent();
            if (parent != this.sheet) {
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(this.inputShield);
                }
                ((ViewGroup) this.sheet).addView(this.inputShield, 0, new ViewGroup.LayoutParams(TVLyricsLayout.UNSET, TVLyricsLayout.UNSET));
            } else if (((ViewGroup) this.sheet).indexOfChild(this.inputShield) != 0) {
                ((ViewGroup) this.sheet).removeView(this.inputShield);
                ((ViewGroup) this.sheet).addView(this.inputShield, 0, new ViewGroup.LayoutParams(TVLyricsLayout.UNSET, TVLyricsLayout.UNSET));
            }
            this.inputShield.setVisibility(0);
            Log.i(TVLyricsLayout.TAG, "INPUT_SHIELD installed behind-native-controls=true");
        }

        void removeInputShield() {
            if (this.inputShield != null && (this.inputShield.getParent() instanceof ViewGroup)) {
                ((ViewGroup) this.inputShield.getParent()).removeView(this.inputShield);
            }
            this.inputShield = null;
        }

        void captureInitial(View view) {
            if (view != null && !this.initialSeen.containsKey(view)) {
                this.initialSeen.put(view, Boolean.TRUE);
                this.initialSnapshots.add(new ViewSnapshot(view));
            }
        }

        void capturePanel(View view) {
            if (view != null && !this.panelSeen.containsKey(view)) {
                this.panelSeen.put(view, Boolean.TRUE);
                this.panelSnapshots.add(new ViewSnapshot(view));
            }
        }

        View findVisible(int i) {
            return TVLyricsLayout.findVisible(this.windowRoot, i);
        }

        void requestLayouts(List<ViewSnapshot> list) {
            Iterator<ViewSnapshot> it = list.iterator();
            while (it.hasNext()) {
                it.next().view.requestLayout();
            }
            if (this.outerContainer != null) {
                this.outerContainer.requestLayout();
            }
        }

        void configureControlGeometry(View view) {
            configureModeButton(view.findViewById(TVLyricsLayout.ID_PLAYER_LYRICS), true, true);
            configureModeButton(view.findViewById(TVLyricsLayout.ID_PLAYER_QUEUE), false, true);
            configureModeButton(view.findViewById(TVLyricsLayout.ID_MEDIA_ROUTE_BUTTON), true, false);
        }

        void configureModeButton(View view, boolean z, boolean z2) {
            if (view != null) {
                TVLyricsLayout.clearConstraints(view.getLayoutParams());
                if (z) {
                    TVLyricsLayout.setInt(view.getLayoutParams(), "t", 0);
                } else {
                    TVLyricsLayout.setInt(view.getLayoutParams(), "v", 0);
                }
                if (z2) {
                    TVLyricsLayout.setInt(view.getLayoutParams(), "l", 0);
                } else {
                    TVLyricsLayout.setInt(view.getLayoutParams(), "v", 0);
                    TVLyricsLayout.setInt(view.getLayoutParams(), "l", 0);
                }
                TVLyricsLayout.setSizeAndMargins(view, TVLyricsLayout.dp(view, 60), TVLyricsLayout.dp(view, 60), 0, 0, 0, TVLyricsLayout.dp(view, 4));
            }
        }

        void configureRightFill(View view, int i) {
            if (view != null) {
                TVLyricsLayout.clearConstraints(view.getLayoutParams());
                TVLyricsLayout.setInt(view.getLayoutParams(), "t", TVLyricsLayout.ID_RECYCLER_GRADIENTS);
                TVLyricsLayout.setInt(view.getLayoutParams(), "v", 0);
                TVLyricsLayout.setInt(view.getLayoutParams(), "i", 0);
                TVLyricsLayout.setInt(view.getLayoutParams(), "l", 0);
                TVLyricsLayout.setSizeAndMargins(view, 0, i, 0, 0, TVLyricsLayout.dp(view, 36), 0);
            }
        }

        void configureRightEdge(View view, boolean z) {
            if (view != null) {
                TVLyricsLayout.clearHorizontal(view.getLayoutParams());
                TVLyricsLayout.setInt(view.getLayoutParams(), z ? "t" : "v", z ? TVLyricsLayout.ID_RECYCLER_GRADIENTS : 0);
                TVLyricsLayout.setSizeAndMargins(view, view.getLayoutParams().width, view.getLayoutParams().height, 0, 0, z ? 0 : TVLyricsLayout.dp(view, 24), TVLyricsLayout.dp(view, 12));
            }
        }

        void configureRightSpan(View view) {
            if (view != null) {
                TVLyricsLayout.clearHorizontal(view.getLayoutParams());
                TVLyricsLayout.setInt(view.getLayoutParams(), "t", TVLyricsLayout.ID_RECYCLER_GRADIENTS);
                TVLyricsLayout.setInt(view.getLayoutParams(), "v", 0);
                TVLyricsLayout.setSizeAndMargins(view, 0, view.getLayoutParams().height, 0, 0, TVLyricsLayout.dp(view, 36), 0);
            }
        }
    }

    /* JADX INFO: loaded from: classes.jar:com/apple/android/music/player/fragment/TVLyricsLayout$SharedItem.class */
    private static final class SharedItem {
        final View view;
        final int originalX;
        final int originalY;
        final int originalWidth;
        final int originalHeight;
        int targetX;
        int targetY;
        int targetWidth;
        int targetHeight;
        int homeX;
        int homeY;
        int homeWidth;
        int homeHeight;
        float dismissStartX;
        float dismissStartY;
        float dismissStartScaleX;
        float dismissStartScaleY;
        float dismissStartAlpha;
        boolean pinned;
        int pinnedOffsetX;
        int pinnedOffsetY;
        SharedItem(View view, int i, int i2, int i3, int i4) {
            this(view, i, i2, i3, i4, false, 0, 0);
        }

        SharedItem(View view, int i, int i2, int i3, int i4, boolean pinned, int pinnedOffsetX, int pinnedOffsetY) {
            this.view = view;
            this.originalX = i;
            this.originalY = i2;
            this.originalWidth = i3;
            this.originalHeight = i4;
            this.pinned = pinned;
            this.pinnedOffsetX = pinnedOffsetX;
            this.pinnedOffsetY = pinnedOffsetY;
            setTarget(i, i2, i3, i4);
            setHome(i, i2, i3, i4);
        }

        void setHome(int i, int i2, int i3, int i4) {
            this.homeX = i;
            this.homeY = i2;
            this.homeWidth = Math.max(TVLyricsLayout.MODE_LYRICS, i3);
            this.homeHeight = Math.max(TVLyricsLayout.MODE_LYRICS, i4);
        }

        void setTarget(int i, int i2, int i3, int i4) {
            this.targetX = i;
            this.targetY = i2;
            this.targetWidth = Math.max(TVLyricsLayout.MODE_LYRICS, i3);
            this.targetHeight = Math.max(TVLyricsLayout.MODE_LYRICS, i4);
        }
    }

    private static final class DragGestureHost extends FrameLayout {
        final Controller owner;
        float downRawX;
        float downRawY;
        float distance;
        boolean candidate;
        boolean dragging;
        boolean childHandledDown;

        DragGestureHost(Context context, Controller controller) {
            super(context);
            this.owner = controller;
            setClipChildren(false);
            setClipToPadding(false);
        }

        @Override // android.view.ViewGroup
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == MotionEvent.ACTION_DOWN) {
                this.downRawX = motionEvent.getRawX();
                this.downRawY = motionEvent.getRawY();
                this.distance = 0.0f;
                this.dragging = false;
                this.candidate = motionEvent.getY() <= ((float) getHeight()) * 0.82f;
                this.childHandledDown = super.dispatchTouchEvent(motionEvent);
                return true;
            }
            if (actionMasked == MotionEvent.ACTION_MOVE) {
                float max = Math.max(0.0f, motionEvent.getRawY() - this.downRawY);
                float abs = Math.abs(motionEvent.getRawX() - this.downRawX);
                if (!this.dragging && this.candidate && max >= TVLyricsLayout.dp(this, 14) && max > abs * 1.15f) {
                    this.dragging = true;
                    this.owner.beginSharedDismissDrag();
                    MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    obtain.setAction(MotionEvent.ACTION_CANCEL);
                    super.dispatchTouchEvent(obtain);
                    obtain.recycle();
                    Log.i(TVLyricsLayout.TAG, "DRAG_SURFACE begin x=" + this.downRawX + " y=" + this.downRawY);
                }
                if (this.dragging) {
                    this.distance = max;
                    this.owner.updateSharedDismissDrag(max);
                    return true;
                }
                boolean dispatchTouchEvent = super.dispatchTouchEvent(motionEvent);
                return this.childHandledDown || dispatchTouchEvent;
            }
            if (actionMasked == MotionEvent.ACTION_UP) {
                if (this.dragging) {
                    boolean z = this.distance >= ((float) TVLyricsLayout.dp(this, 80));
                    Log.i(TVLyricsLayout.TAG, "DRAG_SURFACE end distance=" + this.distance + " dismiss=" + z);
                    this.owner.finishSharedDismissDrag(z);
                    resetGesture();
                    return true;
                }
                boolean dispatchTouchEvent2 = super.dispatchTouchEvent(motionEvent);
                resetGesture();
                return this.childHandledDown || dispatchTouchEvent2;
            }
            if (actionMasked == MotionEvent.ACTION_CANCEL) {
                if (this.dragging) {
                    this.owner.finishSharedDismissDrag(false);
                } else {
                    super.dispatchTouchEvent(motionEvent);
                }
                resetGesture();
                return true;
            }
            return super.dispatchTouchEvent(motionEvent);
        }

        void resetGesture() {
            this.candidate = false;
            this.dragging = false;
            this.childHandledDown = false;
            this.distance = 0.0f;
        }
    }

    /* JADX INFO: loaded from: classes.jar:com/apple/android/music/player/fragment/TVLyricsLayout$DragDismissOverlay.class */
    private static final class DragDismissOverlay extends View {
        final Controller owner;
        float downRawY;
        float distance;
        boolean tracking;

        DragDismissOverlay(Context context, Controller controller) {
            super(context);
            this.owner = controller;
            setBackgroundColor(0);
            setClickable(true);
            setFocusable(false);
            setImportantForAccessibility(TVLyricsLayout.MODE_QUEUE);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getActionMasked()) {
                case 0:
                    beginTracking(motionEvent.getRawY(), "touch");
                    break;
                case TVLyricsLayout.MODE_LYRICS /* 1 */:
                    if (this.tracking) {
                        endTracking("touch-up");
                    }
                    break;
                case TVLyricsLayout.MODE_QUEUE /* 2 */:
                    if (this.tracking) {
                        updateTracking(motionEvent.getRawY());
                    }
                    break;
                case 3:
                    if (this.tracking) {
                        this.tracking = false;
                        Log.i(TVLyricsLayout.TAG, "DRAG_INPUT cancel source=touch");
                        this.owner.finishSharedDismissDrag(false);
                    }
                    break;
            }
            return true;
        }

        @Override // android.view.View
        public boolean onGenericMotionEvent(MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            boolean z = (motionEvent.getButtonState() & MotionEvent.BUTTON_PRIMARY) != 0;
            if (actionMasked == MotionEvent.ACTION_BUTTON_PRESS && motionEvent.getActionButton() == MotionEvent.BUTTON_PRIMARY) {
                beginTracking(motionEvent.getRawY(), "mouse");
                return true;
            }
            if (actionMasked == MotionEvent.ACTION_HOVER_MOVE || actionMasked == MotionEvent.ACTION_MOVE) {
                if (z) {
                    if (!this.tracking) {
                        beginTracking(motionEvent.getRawY(), "mouse-hover");
                    }
                    updateTracking(motionEvent.getRawY());
                    return true;
                }
                if (this.tracking) {
                    endTracking("mouse-release-hover");
                    return true;
                }
            }
            if (actionMasked == MotionEvent.ACTION_BUTTON_RELEASE && motionEvent.getActionButton() == MotionEvent.BUTTON_PRIMARY && this.tracking) {
                endTracking("mouse-button-release");
                return true;
            }
            return super.onGenericMotionEvent(motionEvent);
        }

        void beginTracking(float f, String str) {
            this.downRawY = f;
            this.distance = 0.0f;
            this.tracking = true;
            Log.i(TVLyricsLayout.TAG, "DRAG_INPUT begin source=" + str + " y=" + f);
        }

        void updateTracking(float f) {
            this.distance = Math.max(0.0f, f - this.downRawY);
            this.owner.updateSharedDismissDrag(this.distance);
        }

        void endTracking(String str) {
            this.tracking = false;
            boolean z = this.distance >= ((float) TVLyricsLayout.dp(this, 80));
            Log.i(TVLyricsLayout.TAG, "DRAG_INPUT end source=" + str + " distance=" + this.distance + " dismiss=" + z);
            this.owner.finishSharedDismissDrag(z);
        }

        @Override // android.view.View
        public boolean onHoverEvent(MotionEvent motionEvent) {
            return true;
        }

        @Override // android.view.View
        public PointerIcon onResolvePointerIcon(MotionEvent motionEvent, int i) {
            return PointerIcon.getSystemIcon(getContext(), 1002);
        }
    }

    /* JADX INFO: loaded from: classes.jar:com/apple/android/music/player/fragment/TVLyricsLayout$InputShield.class */
    private static final class InputShield extends View {
        InputShield(Context context) {
            super(context);
            setClickable(true);
            setFocusable(false);
            setImportantForAccessibility(TVLyricsLayout.MODE_QUEUE);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return true;
        }

        @Override // android.view.View
        public boolean onHoverEvent(MotionEvent motionEvent) {
            return true;
        }

        @Override // android.view.View
        public boolean onGenericMotionEvent(MotionEvent motionEvent) {
            return true;
        }

        @Override // android.view.View
        public PointerIcon onResolvePointerIcon(MotionEvent motionEvent, int i) {
            return PointerIcon.getSystemIcon(getContext(), 1000);
        }
    }

    /* JADX INFO: loaded from: classes.jar:com/apple/android/music/player/fragment/TVLyricsLayout$HiddenPanelState.class */
    private static final class HiddenPanelState {
        final View view;
        final int visibility;
        final float alpha;

        HiddenPanelState(View view) {
            this.view = view;
            this.visibility = view.getVisibility();
            this.alpha = view.getAlpha();
        }

        void restore() {
            this.view.setVisibility(this.visibility);
            this.view.setAlpha(this.alpha);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static LinearLayout asLinear(View view) {
        if (view instanceof LinearLayout) {
            return (LinearLayout) view;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void move(View view, ViewGroup viewGroup, ViewGroup.LayoutParams layoutParams) {
        if (view == null || viewGroup == null) {
            return;
        }
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
            viewGroup.addView(view, layoutParams);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void fill(View view) {
        if (view == null || view.getLayoutParams() == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = UNSET;
        layoutParams.height = UNSET;
        view.setLayoutParams(layoutParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setSizeAndMargins(View view, int i, int i2, int i3, int i4, int i5, int i6) {
        if (view == null || view.getLayoutParams() == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = i;
        layoutParams.height = i2;
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.setMargins(i3, i4, i5, i6);
            marginLayoutParams.setMarginStart(i3);
            marginLayoutParams.setMarginEnd(i5);
        }
        view.setLayoutParams(layoutParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void zeroMargins(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.setMargins(0, 0, 0, 0);
            marginLayoutParams.setMarginStart(0);
            marginLayoutParams.setMarginEnd(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void clearConstraints(Object obj) {
        if (obj == null) {
            return;
        }
        String[] strArr = CONSTRAINT_FIELDS;
        int length = strArr.length;
        for (int i = 0; i < length; i += MODE_LYRICS) {
            setInt(obj, strArr[i], UNSET);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void clearHorizontal(Object obj) {
        String[] strArr = {"t", "u", "v", "w"};
        int length = strArr.length;
        for (int i = 0; i < length; i += MODE_LYRICS) {
            setInt(obj, strArr[i], UNSET);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setInt(Object obj, String str, int i) {
        if (obj == null) {
            return;
        }
        Class<?> superclass = obj.getClass();
        while (true) {
            Class<?> cls = superclass;
            if (cls != null) {
                try {
                    Field declaredField = cls.getDeclaredField(str);
                    if (declaredField.getType() == Integer.TYPE) {
                        declaredField.setAccessible(true);
                        declaredField.setInt(obj, i);
                        return;
                    }
                    return;
                } catch (Throwable th) {
                    superclass = cls.getSuperclass();
                }
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setBoolean(Object obj, String str, boolean z) {
        if (obj == null) {
            return;
        }
        Class<?> superclass = obj.getClass();
        while (true) {
            Class<?> cls = superclass;
            if (cls != null) {
                try {
                    Field declaredField = cls.getDeclaredField(str);
                    if (declaredField.getType() == Boolean.TYPE) {
                        declaredField.setAccessible(true);
                        declaredField.setBoolean(obj, z);
                        return;
                    }
                    return;
                } catch (Throwable th) {
                    superclass = cls.getSuperclass();
                }
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setObject(Object obj, String str, Object obj2) {
        if (obj == null) {
            return;
        }
        Class<?> superclass = obj.getClass();
        while (true) {
            Class<?> cls = superclass;
            if (cls != null) {
                try {
                    Field declaredField = cls.getDeclaredField(str);
                    if (!declaredField.getType().isPrimitive()) {
                        declaredField.setAccessible(true);
                        declaredField.set(obj, obj2);
                        return;
                    }
                    return;
                } catch (Throwable th) {
                    superclass = cls.getSuperclass();
                }
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int dp(View view, int i) {
        return (int) TypedValue.applyDimension(MODE_LYRICS, i, view.getResources().getDisplayMetrics());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean restoreSnapshots(ArrayList<ViewSnapshot> arrayList) {
        if (arrayList.isEmpty()) {
            return true;
        }
        ArrayList<ViewSnapshot> arrayList2 = new ArrayList();
        for (ViewSnapshot viewSnapshot : arrayList) {
            if (viewSnapshot.parent != null && viewSnapshot.view.getParent() != viewSnapshot.parent) {
                Log.i(TAG, "TEXTURE_RESTORE native-listener-preserved=true view=" + viewSnapshot.view.getClass().getSimpleName());
                ViewParent parent = viewSnapshot.view.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(viewSnapshot.view);
                }
                arrayList2.add(viewSnapshot);
            }
        }
        Collections.sort(arrayList2, new Comparator<ViewSnapshot>() {
            @Override // java.util.Comparator
            public int compare(ViewSnapshot viewSnapshot2, ViewSnapshot viewSnapshot3) {
                if (viewSnapshot2.parent == viewSnapshot3.parent) {
                    return viewSnapshot2.index - viewSnapshot3.index;
                }
                return System.identityHashCode(viewSnapshot2.parent) - System.identityHashCode(viewSnapshot3.parent);
            }
        });
        for (ViewSnapshot viewSnapshot2 : arrayList2) {
            viewSnapshot2.parent.addView(viewSnapshot2.view, Math.max(0, Math.min(viewSnapshot2.index, viewSnapshot2.parent.getChildCount())), cloneLayoutParams(viewSnapshot2.layoutParams));
        }
        Iterator<ViewSnapshot> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().restoreLayout();
        }
        Iterator<ViewSnapshot> it2 = arrayList.iterator();
        while (it2.hasNext()) {
            it2.next().restoreVisualAndHit();
        }
        boolean z = true;
        for (ViewSnapshot viewSnapshot3 : arrayList) {
            if (!viewSnapshot3.matches()) {
                Log.e(TAG, "RESTORE_MISMATCH " + viewSnapshot3.describeMismatch());
                z = false;
            }
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void guardTextureListenersForReparent(View view, ArrayList<TextureListenerState> arrayList) {
        if (view instanceof TextureView) {
            final TextureView textureView = (TextureView) view;
            final TextureView.SurfaceTextureListener original = textureView.getSurfaceTextureListener();
            if (original != null) {
                arrayList.add(new TextureListenerState(textureView, original));
                textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                    @Override
                    public void onSurfaceTextureAvailable(android.graphics.SurfaceTexture surface, int width, int height) {
                        original.onSurfaceTextureAvailable(surface, width, height);
                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(android.graphics.SurfaceTexture surface, int width, int height) {
                        original.onSurfaceTextureSizeChanged(surface, width, height);
                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(android.graphics.SurfaceTexture surface) {
                        try {
                            return original.onSurfaceTextureDestroyed(surface);
                        } catch (NullPointerException exception) {
                            // Apple Music 6.5 can leave the listener alive after
                            // its internal release queue has already been cleared
                            // by a video -> audio transition.  Preserve the normal
                            // detach/re-attach lifecycle while containing only
                            // that stale-listener failure.
                            Log.w(TAG, "TEXTURE_REPARENT stale-native-destroy-listener-caught=true", exception);
                            return true;
                        }
                    }

                    @Override
                    public void onSurfaceTextureUpdated(android.graphics.SurfaceTexture surface) {
                        original.onSurfaceTextureUpdated(surface);
                    }
                });
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                guardTextureListenersForReparent(viewGroup.getChildAt(i), arrayList);
            }
        }
    }

    public static void silenceTextureListeners(View view, ArrayList<TextureListenerState> arrayList) {
        TextureView textureView;
        TextureView.SurfaceTextureListener surfaceTextureListener;
        if ((view instanceof TextureView) && (surfaceTextureListener = (textureView = (TextureView) view).getSurfaceTextureListener()) != null) {
            arrayList.add(new TextureListenerState(textureView, surfaceTextureListener));
            textureView.setSurfaceTextureListener(null);
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i += MODE_LYRICS) {
                silenceTextureListeners(viewGroup.getChildAt(i), arrayList);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void restoreTextureListeners(ArrayList<TextureListenerState> arrayList) {
        for (TextureListenerState textureListenerState : arrayList) {
            textureListenerState.view.setSurfaceTextureListener(textureListenerState.listener);
        }
    }

    /* JADX INFO: loaded from: classes.jar:com/apple/android/music/player/fragment/TVLyricsLayout$TextureListenerState.class */
    private static final class TextureListenerState {
        final TextureView view;
        final TextureView.SurfaceTextureListener listener;

        TextureListenerState(TextureView textureView, TextureView.SurfaceTextureListener surfaceTextureListener) {
            this.view = textureView;
            this.listener = surfaceTextureListener;
        }
    }

    /* JADX INFO: loaded from: classes.jar:com/apple/android/music/player/fragment/TVLyricsLayout$ViewSnapshot.class */
    private static final class ViewSnapshot {
        final View view;
        final ViewGroup parent;
        final int index;
        final ViewGroup.LayoutParams layoutParams;
        final ArrayList<FieldValue> layoutFields;
        final int visibility;
        final float alpha;
        final float translationX;
        final float translationY;
        final float translationZ;
        final float elevation;
        final float rotation;
        final float rotationX;
        final float rotationY;
        final float scaleX;
        final float scaleY;
        final float pivotX;
        final float pivotY;
        final int paddingLeft;
        final int paddingTop;
        final int paddingRight;
        final int paddingBottom;
        final int layoutDirection;
        final boolean clickable;
        final boolean longClickable;
        final boolean focusable;
        final boolean focusableInTouchMode;
        final boolean enabled;
        final boolean selected;
        final boolean activated;
        final int importantForAccessibility;
        final int gravity;
        final int orientation;
        final float textSize;
        final boolean clipChildren;
        final boolean clipToPadding;

        ViewSnapshot(View view) {
            this.view = view;
            ViewParent parent = view.getParent();
            this.parent = parent instanceof ViewGroup ? (ViewGroup) parent : null;
            this.index = this.parent != null ? this.parent.indexOfChild(view) : TVLyricsLayout.UNSET;
            this.layoutParams = TVLyricsLayout.cloneLayoutParams(view.getLayoutParams());
            this.layoutFields = TVLyricsLayout.captureFields(view.getLayoutParams());
            this.visibility = view.getVisibility();
            this.alpha = view.getAlpha();
            this.translationX = view.getTranslationX();
            this.translationY = view.getTranslationY();
            this.translationZ = view.getTranslationZ();
            this.elevation = view.getElevation();
            this.rotation = view.getRotation();
            this.rotationX = view.getRotationX();
            this.rotationY = view.getRotationY();
            this.scaleX = view.getScaleX();
            this.scaleY = view.getScaleY();
            this.pivotX = view.getPivotX();
            this.pivotY = view.getPivotY();
            this.paddingLeft = view.getPaddingLeft();
            this.paddingTop = view.getPaddingTop();
            this.paddingRight = view.getPaddingRight();
            this.paddingBottom = view.getPaddingBottom();
            this.layoutDirection = view.getLayoutDirection();
            this.clickable = view.isClickable();
            this.longClickable = view.isLongClickable();
            this.focusable = view.isFocusable();
            this.focusableInTouchMode = view.isFocusableInTouchMode();
            this.enabled = view.isEnabled();
            this.selected = view.isSelected();
            this.activated = view.isActivated();
            this.importantForAccessibility = view.getImportantForAccessibility();
            this.gravity = view instanceof LinearLayout ? ((LinearLayout) view).getGravity() : TVLyricsLayout.UNSET;
            this.orientation = view instanceof LinearLayout ? ((LinearLayout) view).getOrientation() : TVLyricsLayout.UNSET;
            this.textSize = view instanceof TextView ? ((TextView) view).getTextSize() : -1.0f;
            this.clipChildren = (view instanceof ViewGroup) && ((ViewGroup) view).getClipChildren();
            this.clipToPadding = (view instanceof ViewGroup) && ((ViewGroup) view).getClipToPadding();
            Log.i(TVLyricsLayout.TAG, "SNAPSHOT " + TVLyricsLayout.describe(view) + " parent=" + TVLyricsLayout.describe(this.parent) + " index=" + this.index + " lp=" + TVLyricsLayout.layoutParamsToString(this.layoutParams) + " gravity=" + this.gravity + " orientation=" + this.orientation);
        }

        void restoreLayout() {
            ViewGroup.LayoutParams layoutParamsCloneLayoutParams = TVLyricsLayout.cloneLayoutParams(this.layoutParams);
            if (layoutParamsCloneLayoutParams != null) {
                this.view.setLayoutParams(layoutParamsCloneLayoutParams);
            } else if (this.view.getLayoutParams() != null) {
                TVLyricsLayout.restoreFields(this.view.getLayoutParams(), this.layoutFields);
                this.view.setLayoutParams(this.view.getLayoutParams());
            }
            this.view.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);
            this.view.setLayoutDirection(this.layoutDirection);
            if (this.view instanceof LinearLayout) {
                ((LinearLayout) this.view).setGravity(this.gravity);
                ((LinearLayout) this.view).setOrientation(this.orientation);
            }
            if (this.view instanceof TextView) {
                ((TextView) this.view).setTextSize(0, this.textSize);
            }
            if (this.view instanceof ViewGroup) {
                ((ViewGroup) this.view).setClipChildren(this.clipChildren);
                ((ViewGroup) this.view).setClipToPadding(this.clipToPadding);
            }
        }

        void restoreVisualAndHit() {
            this.view.setVisibility(this.visibility);
            this.view.setAlpha(this.alpha);
            this.view.setTranslationX(this.translationX);
            this.view.setTranslationY(this.translationY);
            this.view.setTranslationZ(this.translationZ);
            this.view.setElevation(this.elevation);
            this.view.setRotation(this.rotation);
            this.view.setRotationX(this.rotationX);
            this.view.setRotationY(this.rotationY);
            this.view.setScaleX(this.scaleX);
            this.view.setScaleY(this.scaleY);
            this.view.setPivotX(this.pivotX);
            this.view.setPivotY(this.pivotY);
            this.view.setClickable(this.clickable);
            this.view.setLongClickable(this.longClickable);
            this.view.setFocusable(this.focusable);
            this.view.setFocusableInTouchMode(this.focusableInTouchMode);
            this.view.setEnabled(this.enabled);
            this.view.setSelected(this.selected);
            this.view.setActivated(this.activated);
            this.view.setImportantForAccessibility(this.importantForAccessibility);
        }

        boolean matches() {
            if (this.view.getParent() != this.parent) {
                return false;
            }
            if ((this.parent != null && this.parent.indexOfChild(this.view) != this.index) || !TVLyricsLayout.fieldsMatch(this.view.getLayoutParams(), this.layoutFields) || this.view.getVisibility() != this.visibility || this.view.getAlpha() != this.alpha || this.view.getTranslationX() != this.translationX || this.view.getTranslationY() != this.translationY || this.view.getTranslationZ() != this.translationZ || this.view.getElevation() != this.elevation || this.view.getPaddingLeft() != this.paddingLeft || this.view.getPaddingTop() != this.paddingTop || this.view.getPaddingRight() != this.paddingRight || this.view.getPaddingBottom() != this.paddingBottom || this.view.isClickable() != this.clickable || this.view.isFocusable() != this.focusable || this.view.isEnabled() != this.enabled) {
                return false;
            }
            if (this.view instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) this.view;
                if (linearLayout.getGravity() != this.gravity || linearLayout.getOrientation() != this.orientation) {
                    return false;
                }
            }
            if ((this.view instanceof TextView) && Math.abs(((TextView) this.view).getTextSize() - this.textSize) > 0.01f) {
                return false;
            }
            if (this.view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) this.view;
                if (viewGroup.getClipChildren() != this.clipChildren || viewGroup.getClipToPadding() != this.clipToPadding) {
                    return false;
                }
                return true;
            }
            return true;
        }

        String describeMismatch() {
            return TVLyricsLayout.describe(this.view) + " expectedParent=" + TVLyricsLayout.describe(this.parent) + " actualParent=" + TVLyricsLayout.describe(this.view.getParent()) + " expectedIndex=" + this.index + " actualIndex=" + ((this.view.getParent() != this.parent || this.parent == null) ? TVLyricsLayout.UNSET : this.parent.indexOfChild(this.view)) + " expectedLP=" + TVLyricsLayout.fieldsToString(this.layoutFields) + " actualLP=" + TVLyricsLayout.layoutParamsToString(this.view.getLayoutParams()) + " expectedGravity=" + this.gravity + " actualGravity=" + (this.view instanceof LinearLayout ? ((LinearLayout) this.view).getGravity() : TVLyricsLayout.UNSET);
        }
    }

    /* JADX INFO: loaded from: classes.jar:com/apple/android/music/player/fragment/TVLyricsLayout$FieldValue.class */
    private static final class FieldValue {
        final Field field;
        final Object value;

        FieldValue(Field field, Object obj) {
            this.field = field;
            this.value = obj;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ArrayList<FieldValue> captureFields(Object obj) {
        ArrayList<FieldValue> arrayList = new ArrayList<>();
        if (obj == null) {
            return arrayList;
        }
        Class<?> superclass = obj.getClass();
        while (true) {
            Class<?> cls = superclass;
            if (cls == null || cls == Object.class) {
                break;
            }
            Field[] declaredFields = cls.getDeclaredFields();
            int length = declaredFields.length;
            for (int i = 0; i < length; i += MODE_LYRICS) {
                Field field = declaredFields[i];
                if (!Modifier.isStatic(field.getModifiers())) {
                    try {
                        field.setAccessible(true);
                        arrayList.add(new FieldValue(field, field.get(obj)));
                    } catch (Throwable th) {
                    }
                }
            }
            superclass = cls.getSuperclass();
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void restoreFields(Object obj, ArrayList<FieldValue> arrayList) {
        if (obj == null) {
            return;
        }
        for (FieldValue fieldValue : arrayList) {
            try {
                fieldValue.field.setAccessible(true);
                fieldValue.field.set(obj, fieldValue.value);
            } catch (Throwable unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean fieldsMatch(Object obj, ArrayList<FieldValue> arrayList) {
        if (obj == null) {
            return arrayList.isEmpty();
        }
        for (FieldValue fieldValue : arrayList) {
            try {
                fieldValue.field.setAccessible(true);
                Object obj2 = fieldValue.field.get(obj);
                Object obj3 = fieldValue.value;
                if (obj2 != obj3 && (obj2 == null || !obj2.equals(obj3))) {
                    return false;
                }
            } catch (Throwable unused) {
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ViewGroup.LayoutParams cloneLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams == null) {
            return null;
        }
        Class<?> cls = layoutParams.getClass();
        try {
            Constructor<?> declaredConstructor = cls.getDeclaredConstructor(cls);
            declaredConstructor.setAccessible(true);
            ViewGroup.LayoutParams layoutParams2 = (ViewGroup.LayoutParams) declaredConstructor.newInstance(layoutParams);
            restoreFields(layoutParams2, captureFields(layoutParams));
            return layoutParams2;
        } catch (Throwable th) {
            try {
                Constructor<?> declaredConstructor2 = cls.getDeclaredConstructor(ViewGroup.LayoutParams.class);
                declaredConstructor2.setAccessible(true);
                ViewGroup.LayoutParams layoutParams3 = (ViewGroup.LayoutParams) declaredConstructor2.newInstance(layoutParams);
                restoreFields(layoutParams3, captureFields(layoutParams));
                return layoutParams3;
            } catch (Throwable th2) {
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    try {
                        Constructor<?> declaredConstructor3 = cls.getDeclaredConstructor(ViewGroup.MarginLayoutParams.class);
                        declaredConstructor3.setAccessible(true);
                        ViewGroup.LayoutParams layoutParams4 = (ViewGroup.LayoutParams) declaredConstructor3.newInstance(layoutParams);
                        restoreFields(layoutParams4, captureFields(layoutParams));
                        return layoutParams4;
                    } catch (Throwable th3) {
                        Log.e(TAG, "LayoutParams clone failed: " + cls.getName());
                        return null;
                    }
                }
                Log.e(TAG, "LayoutParams clone failed: " + cls.getName());
                return null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String describe(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof View) {
            View view = (View) obj;
            return view.getClass().getSimpleName() + "#" + Integer.toHexString(view.getId()) + "@" + Integer.toHexString(System.identityHashCode(view));
        }
        return obj.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(obj));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String geometry(View view) {
        if (view == null) {
            return "null";
        }
        return describe(view) + "[" + view.getLeft() + "," + view.getTop() + " " + view.getWidth() + "x" + view.getHeight() + " vis=" + view.getVisibility() + " alpha=" + view.getAlpha() + " shown=" + view.isShown() + "]";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String layoutParamsToString(ViewGroup.LayoutParams layoutParams) {
        return fieldsToString(captureFields(layoutParams));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String fieldsToString(ArrayList<FieldValue> arrayList) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (int i = 0; i < arrayList.size(); i += MODE_LYRICS) {
            FieldValue fieldValue = arrayList.get(i);
            if (i > 0) {
                sb.append(',');
            }
            sb.append(fieldValue.field.getName()).append('=').append(String.valueOf(fieldValue.value));
        }
        sb.append('}');
        return sb.toString();
    }
}
