package com.ipcjs.explorer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JiangSong on 2015/12/2.
 */
public class ExplorerFragment extends Fragment implements AdapterView.OnItemClickListener, Explorer.ExplorerContainer {
    public static final String ARG_ALL_CLASS = "all_class";
    private static Context sApplication;

    public static Context getApplication() {
        return sApplication;
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

    private enum DirAction implements Explorer.OnActionListener {
        app_dir() {
            @Override
            public String getPath(Context context) {
                return context.getFilesDir().getParentFile().getAbsolutePath();
            }
        },
        app_ext_dir() {
            public String getPath(Context context) {
                return ExUtils.getDir(context, false, true).getParentFile().getAbsolutePath();
            }
        },
        ext_dir() {
            @Override
            public String getPath(Context context) {
                return Environment.getExternalStorageDirectory().getAbsolutePath();
            }
        },
        root_dir() {
            @Override
            public String getPath(Context context) {
                return "/";
            }
        },
        pkg_class() {
            @Override
            public String getPath(Context context) {
                return context.getPackageName() + ExClass.DOT;
            }
        },
        click() {
            @Override
            public void onAction(Context context, Object hack) {
                ExUtils.error("click");
            }
        },
            /*end*/;
        Explorer.Explorable ex;

        @Override
        public void onAction(Context context, Object hack) {
            if (ex == null) {
                String path = getPath(context);
                if (path != null) {
                    ex = ExUtils.newExplorable(path);
                }
            }
            if (ex != null) {
                ((ExplorerFragment) hack).openExplorable(ex);
            }
        }

        public String getPath(Context context) {
            return "";// 空串也是一个合法的path..., 表示根包..
        }
    }

    private SharedPreferences mPref;
    private ListView mListView;
    private ExplorerAdapter mAdapter;
    private EnumMenuHelper<DirAction> mEnumMenuHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEnumMenuHelper = new EnumMenuHelper<>(DirAction.class, getContext(), this);
        sApplication = getContext().getApplicationContext();
        ExUtils.forceShowOverflowMenu(getContext());
        if (getArguments() != null) {
            mAllClass = getArguments().getStringArrayList(ARG_ALL_CLASS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(mEnumMenuHelper.getItemCount() > 0);
        mListView = new ListView(getContext());
        return mListView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mEnumMenuHelper.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mEnumMenuHelper.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new ExplorerAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String curPath = mPref.getString(PREF_KEY_CUR_PATH, DirAction.pkg_class.getPath(getContext()));
        openExplorable(ExUtils.newExplorable(curPath));
    }

    public void openExplorable(Explorer.Explorable ex) {
        if (ex == null) {
            ExUtils.error("无访问权限的item");
            return;
        }
        if (ex.isDir()) {
            List<Explorer.Explorable> children = ex.getChildren(this);
            if (children == null) {
                ExUtils.error(String.format("没有访问%s的权限", ex.getPath()));
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
                ExUtils.error("list not can null");
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
            holder.tvTitle.setText(name);
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
}
