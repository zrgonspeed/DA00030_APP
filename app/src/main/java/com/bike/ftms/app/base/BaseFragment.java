package com.bike.ftms.app.base;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bike.ftms.app.activity.OnOrientationChanged;
import com.bike.ftms.app.utils.Logger;

import org.jetbrains.annotations.NotNull;


/**
 * @Description 基类fragment
 * @Author YeYueHong
 * @Date 2021/3/30
 */
public abstract class BaseFragment extends Fragment implements OnOrientationChanged {
    private static String TAG = BaseFragment.class.getSimpleName();

    protected Activity mActivity;

    public BaseFragment() {
        super();
        Logger.i("构造方法 " + getTAG() + "()  " + this);
    }

    /**
     * onAttach()在fragment与Activity关联之后调调查用。
     * 需要注意的是，初始化fragment参数可以从getArguments()获得，但是，当Fragment附加到Activity之后，就无法再调用setArguments()。
     * 所以除了在最开始时，其它时间都无法向初始化参数添加内容。
     *
     * @param context
     */
    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
        Logger.d(getTAG() + " - " + "1 onAttach()");
    }

    protected abstract String getTAG();

    /**
     * 这个只是用来创建Fragment的。此时的Activity还没有创建完成。
     * 因为我们的Fragment也是Activity创建的一部分。所以如果你想在这里使用Activity中的一些资源，将会获取不到
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(getTAG() + " - " + "2 onCreate()");
    }


    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d(getTAG() + " - " + "3 onCreateView()  savedInstanceState == " + savedInstanceState);

        View view = LayoutInflater.from(mActivity).inflate(getLayoutId(), container, false);
        initView(view, container, savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 在Activity的OnCreate()结束后，会调用此方法。所以到这里的时候，Activity已经创建完成！
     * 在这个函数中才可以使用Activity的所有资源。
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        Logger.d(getTAG() + " - " + "4 onActivityCreated()");

    }

    /**
     * 当到OnStart()时，Fragment对用户就是可见的了。但用户还未开始与Fragment交互。
     * 在生命周期中也可以看到Fragment的OnStart()过程与Activity的OnStart()过程是绑定的。
     * 意义即是一样的。以前你写在Activity的OnStart()中来处理的代码，用Fragment来实现时，依然可以放在OnStart()中来处理。
     */
    @Override
    public void onStart() {
        super.onStart();
        Logger.d(getTAG() + " - " + "5 onStart()");
    }

    /**
     * 当这个fragment对用户可见并且正在运行时调用。这是Fragment与用户交互之前的最后一个回调。
     * 从生命周期对比中，可以看到，Fragment的OnResume与Activity的OnResume是相互绑定的，意义是一样的。
     * 它依赖于包含它的activity的Activity.onResume。当OnResume()结束后，就可以正式与用户交互了。
     */
    @Override
    public void onResume() {
        super.onResume();
        Logger.d(getTAG() + " - " + "6 onResume()");
        Logger.d(getTAG() + " - " + "Fragment 处于活动状态");

        Configuration cf = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = cf.orientation; //获取屏幕方向
        Logger.d(getTAG() + " 横竖屏: " + ori);
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            setLandLayout();
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            setPortLayout();
        }
    }

    /**
     * 此回调与Activity的OnPause()相绑定，与Activity的OnPause()意义一样。
     */
    @Override
    public void onPause() {
        super.onPause();
        Logger.d(getTAG() + " - " + "7 onPause()");
    }

    /**
     * 这个回调与Activity的OnStop()相绑定，意义一样。
     * 已停止的Fragment可以直接返回到OnStart()回调，然后调用OnResume()。
     */
    @Override
    public void onStop() {
        super.onStop();
        Logger.d(getTAG() + " - " + "8 onStop()");

    }

    /**
     * 如果Fragment即将被结束或保存，那么撤销方向上的下一个回调将是onDestoryView()。
     * 会将在onCreateView创建的视图与这个fragment分离。下次这个fragment若要显示，那么将会创建新视图。
     * 这会在onStop之后和onDestroy之前调用。这个方法的调用同onCreateView是否返回非null视图无关。
     * 它会潜在的在这个视图状态被保存之后以及它被它的父视图回收之前调用。
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.d(getTAG() + " - " + "9 onDestroyView()");
    }


    /**
     * 当这个fragment不再使用时调用。需要注意的是，它即使经过了onDestroy()阶段，但仍然能从Activity中找到，因为它还没有Detach。
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(getTAG() + " - " + "10 onDestroy()");

    }

    /**
     * Fragment生命周期中最后一个回调是onDetach()。
     * 调用它以后，Fragment就不再与Activity相绑定，它也不再拥有视图层次结构，它的所有资源都将被释放。
     */
    @Override
    public void onDetach() {
        super.onDetach();
        Logger.d(getTAG() + " - " + "11 onDetach()");

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Logger.d(getTAG() + " - setUserVisibleHint() " + isVisibleToUser);
    }

    /**
     * 该抽象方法就是 onCreateView中需要的layoutID
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 该抽象方法就是 初始化view
     *
     * @param view
     * @param savedInstanceState
     */
    protected abstract void initView(View view, ViewGroup container, Bundle savedInstanceState);

    /**
     * 执行数据的加载
     */
    protected abstract void initData();

    @Override
    public String toString() {
        String s = super.toString();
        return s.substring(s.indexOf('{') + 1, s.indexOf('('));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Logger.d(getTAG() + " newConfig 横竖屏 " + newConfig.orientation);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setPortLayout();
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setLandLayout();
        }
    }

    protected int getIntDimen(int id) {
        return (int) getResources().getDimension(id);
    }
}

