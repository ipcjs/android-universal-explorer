package com.ipcjs.explorer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ipcjs.explorer.menu.MenuCreator;
import com.ipcjs.explorer.menu.MenuFragment;
import com.ipcjs.explorer.menu.ObjectMenuCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ipcjs.explorer.ExUtils.tError;

/**
 * Created by JiangSong on 2015/12/2.
 */
public class ExplorerFragment extends MenuFragment implements AdapterView.OnItemClickListener, Explorer.ExplorerContainer {
    public static final String ARG_ALL_CLASS = "all_class";
    private InternalMenuObject mMenuObject;

    public static ExplorerFragment setupExplorer(FragmentActivity activity, Class... clss) {
        FragmentManager fm = activity.getSupportFragmentManager();
        String tag = ExplorerFragment.class.getName();
        ExplorerFragment fragment = (ExplorerFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = newInstance(clss);
            fm.beginTransaction()
                    .replace(android.R.id.content, fragment, tag)
                    .commit();
        }
        return fragment;
    }

    public static ExplorerFragment newInstance(Class... clss) {
        String[] names = new String[clss.length];
        for (int i = 0; i < clss.length; i++) {
            names[i] = clss[i].getName();
        }
        return newInstance(names);
    }

    public static ExplorerFragment newInstance(String... names) {
        ExplorerFragment fragment = new ExplorerFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_ALL_CLASS, new ArrayList<String>(Arrays.asList(names)));
        fragment.setArguments(args);
        return fragment;
    }

    private List<String> mAllClass;

    public static final String PREF_KEY_CUR_PATH = "PREF_KEY_CUR_PATH";

    @Override
    public int getContainId() {
        View view = getView();
        if (view != null && view.getParent() instanceof ViewGroup && ((ViewGroup) view.getParent()).getId() != View.NO_ID) {
            return ((ViewGroup) view.getParent()).getId();
        }
        return android.R.id.content;
    }

    @Override
    public List<String> getExploreRange() {
        return mAllClass;
    }

    private SharedPreferences mPref;
    private ListView mListView;
    private ExplorerAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExUtils.initEnvironment(getContext());
        if (getArguments() != null) {
            mAllClass = getArguments().getStringArrayList(ARG_ALL_CLASS);
        }
        mMenuObject = new InternalMenuObject();
        // 调用一次, 预防菜单方法被shrinking~~
        boolean preventShrinking = false;
        if (preventShrinking) {
            mMenuObject.app_dir();
            mMenuObject.app_ext_dir();
            mMenuObject.ext_dir();
            mMenuObject.pkg_class();
            mMenuObject.root_dir();
        }
        getMultiMenuCreator().add(new ObjectMenuCreator(mMenuObject));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListView = new ListView(getContext());
        return mListView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new ExplorerAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String curPath = mPref.getString(PREF_KEY_CUR_PATH, getContext().getPackageName() + ExClass.SPLIT_DOT);
        openExplorable(curPath);
    }

    public void openExplorable(String path) {
        openExplorable(ExUtils.newExplorable(path));
    }

    public void openExplorable(Explorer.Explorable ex) {
        if (ex == null) {
            tError("无访问权限的item");
            return;
        }
        if (ex.isDir()) {
            List<Explorer.Explorable> children = ex.getChildren(this);
            if (children == null) {
                tError(String.format("没有访问%s的权限", ex.getPath()));
                return;
            }
            mPref.edit().putString(PREF_KEY_CUR_PATH, ex.getPath()).apply();
            getActivity().setTitle(ex.getPath());
            children.add(0, ex.getParent());
            mAdapter.setExList(children);
        } else {
            ex.onAction(getContext(), this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        openExplorable(mAdapter.getItem(position));
    }

    private static class ExplorerAdapter extends BaseAdapter {
        private List<Explorer.Explorable> mExList = new ArrayList<>();

        public void setExList(List<Explorer.Explorable> list) {
            if (list == null) {
                tError("list not can null");
                return;
            }
            mExList = list;
            notifyDataSetChanged();
        }

        public ExplorerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ExplorerViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false)
            );
        }

        @Override
        public int getCount() {
            return mExList.size();
        }

        @Override
        public Explorer.Explorable getItem(int position) {
            return mExList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = onCreateViewHolder(parent, getItemViewType(position)).itemView;
            }
            onBindViewHolder((ExplorerViewHolder) convertView.getTag(), position);
            return convertView;
        }

        public void onBindViewHolder(ExplorerViewHolder holder, int position) {
            Explorer.Explorable item = getItem(position);
            String name;
            if (item != null) {
                name = position == 0 ? ".." : item.getName();
                if (item.isDir()) {
                    name = ">" + name;
                }
            } else {
                name = "无访问权限的item";
            }
            int splitIndex = name.indexOf(ExClass.SPLIT_LF);
            CharSequence text;
            if (splitIndex != -1) {// 回车之后的文本为summary, 要设置不一样的样式
                SpannableString ss = new SpannableString(name);
                ss.setSpan(new RelativeSizeSpan(0.8f), splitIndex, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new ForegroundColorSpan(0xff676767), splitIndex, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                text = ss;
            } else {
                text = name;
            }
            holder.tvTitle.setText(text);
        }
    }

    private static class ExplorerViewHolder {
        public final TextView tvTitle;
        private View itemView;

        public ExplorerViewHolder(View itemView) {
            this.itemView = itemView;
            this.itemView.setTag(this);
            tvTitle = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }

    private class InternalMenuObject {
        @MenuCreator.MenuItem
        void app_dir() {
            openExplorable(getContext().getFilesDir().getParentFile().getAbsolutePath());
        }

        @MenuCreator.MenuItem
        void app_ext_dir() {
            openExplorable(ExUtils.getDir(getContext(), false, true).getParentFile().getAbsolutePath());
        }

        @MenuCreator.MenuItem
        void ext_dir() {
            openExplorable(Environment.getExternalStorageDirectory().getAbsolutePath());
        }

        @MenuCreator.MenuItem
        void root_dir() {
            openExplorable("/");
        }

        @MenuCreator.MenuItem
        void pkg_class() {
            openExplorable(getContext().getPackageName() + ExClass.SPLIT_DOT);
        }
    }
}
