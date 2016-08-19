package android.mars.customlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Mars on 29.07.2016.
 */
public class CustomLauncherFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private static final String TAG = "CustomLauncherFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_custom_launcher, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_nerd_launcher_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        setupAdapter();
        return v;
    }

    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ResolveInfo mResolveInfo;
        private TextView mNameTextView;
        private ImageView mIconImageView;

        public ActivityHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_activity, parent, false));
            mNameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            mIconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);
            mIconImageView.setOnClickListener(this);
        }

        public void bindActivity(ResolveInfo resolveInfo) {
            mResolveInfo = resolveInfo;
            PackageManager pm = getActivity().getPackageManager();
            String appName = mResolveInfo.loadLabel(pm).toString();
            mIconImageView.setImageDrawable(resolveInfo.loadIcon(pm));
            mNameTextView.setText(appName);
        }

        @Override
        public void onClick(View view) {
            Log.i("Click", "Clack");
            ActivityInfo activityInfo = mResolveInfo.activityInfo;
            Intent i = new Intent(Intent.ACTION_MAIN).setClassName(activityInfo.applicationInfo.packageName, activityInfo.name).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
        private final List<ResolveInfo> mActivities;

        public ActivityAdapter(List<ResolveInfo> activities) {
            mActivities = activities;
        }

        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ActivityHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ActivityHolder holder, int position) {
            ResolveInfo resolveInfo = mActivities.get(position);
            holder.bindActivity(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }
    }

    private void setupAdapter() {
        Intent staryupIntent = new Intent(Intent.ACTION_MAIN);
        staryupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(staryupIntent, 0);
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo resolveInfo, ResolveInfo t1) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(resolveInfo.loadLabel(pm).toString(),
                        t1.loadLabel(pm).toString());
            }
        });
        Log.i(TAG, "Found: " + activities.size() + " activities");
        mRecyclerView.setAdapter(new ActivityAdapter(activities));
    }

    public static CustomLauncherFragment newInstance() {
        return new CustomLauncherFragment();
    }

}
