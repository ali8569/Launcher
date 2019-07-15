package ir.markazandroid.launcher.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ir.markazandroid.launcher.R;

/**
 * Coded by Ali on 7/8/2019.
 */
public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private static final int TYPE_ADVERTISER=2;
    private static final int TYPE_3PARTY_APP=1;

    private boolean isAdvertiserLogoShown;


    public interface ApplicationIconClickedListener{
        void onApplicationIconClicked(String applicationId);
    }

    private Context context;
    private ArrayList<String> applicationIds;
    private PackageManager packageManager;
    private ApplicationIconClickedListener listener;
    private String counterTimer;

    public ApplicationAdapter(Context context, ApplicationIconClickedListener listener) {
        this.context = context;
        packageManager=context.getPackageManager();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==TYPE_3PARTY_APP)
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.entry_app_icon,parent,false));
        else
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.entry_app_advertiser_logo,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (getItemViewType(position)==TYPE_ADVERTISER){
            //holder.icon.setImageResource(R.drawable.logo_small);
            holder.counter.setText(counterTimer);
        }
        else {
            final String appId = applicationIds.get(isAdvertiserLogoShown?position-1:position);

            try {
                holder.icon.setImageDrawable(packageManager.getApplicationIcon(appId));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                holder.itemView.setVisibility(View.GONE);
                return;
            }

            holder.itemView.setVisibility(View.VISIBLE);
            holder.icon.setOnClickListener(v -> {
                listener.onApplicationIconClicked(appId);
            });

        }

    }



    @Override
    public int getItemCount() {
        int size= applicationIds.isEmpty()?0:applicationIds.size();
        if (isAdvertiserLogoShown)
            size++;
        return size;
    }

    public ArrayList<String> getApplicationIds() {
        return applicationIds;
    }

    public void setApplicationIds(ArrayList<String> applicationIds) {
        this.applicationIds = applicationIds;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (isAdvertiserLogoShown && position==0)
            return TYPE_ADVERTISER;
        else
            return TYPE_3PARTY_APP;
    }

    public void setCounterTimer(String counterTimer) {
        this.counterTimer = counterTimer;
        if (isAdvertiserLogoShown)
            notifyItemChanged(0);
    }

    public void showAdvertiserCounter(boolean isShown){
        isAdvertiserLogoShown=isShown;
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView icon;
        private TextView counter;
        public ViewHolder(View itemView) {
            super(itemView);
            icon= itemView.findViewById(R.id.icon);
            counter=itemView.findViewById(R.id.counter);
        }
    }
}
