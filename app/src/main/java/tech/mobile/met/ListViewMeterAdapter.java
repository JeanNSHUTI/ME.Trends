package tech.mobile.met;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;


import io.realm.RealmList;

import tech.mobile.met.models.viewmodels.MeterViewModel;

public class ListViewMeterAdapter extends BaseAdapter implements ListAdapter{

    String TAG = "REALM_LIST_ADAPTER";
    String FORMAT = "yyyy/MM/dd";
    Context context;
    RealmList<MeterViewModel>  mViewModels;
    //LayoutInflater inflter;

    public ListViewMeterAdapter(Context applicationContext, RealmList<MeterViewModel> meters){
        this.context = applicationContext;
        this.mViewModels = meters;
        //inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return mViewModels.size();
    }

    @Override
    public MeterViewModel getItem(int i) {
        return mViewModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mViewModels.get(i).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListViewMeterAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            Log.i(TAG, "Creating view holder");
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listview_meter_ressource, parent, false);

            viewHolder = new ListViewMeterAdapter.ViewHolder();
            viewHolder.firstLine = (TextView) convertView.findViewById(R.id.firstLine);
            viewHolder.details = (TextView) convertView.findViewById(R.id.secondLine);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon_listview);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ListViewMeterAdapter.ViewHolder) convertView.getTag();
        }

        final MeterViewModel meterViewModel = mViewModels.get(position);
        String lastRecordDate = android.text.format.DateFormat.format(FORMAT, meterViewModel.getRecord_date()).toString();
        viewHolder.firstLine.setText(meterViewModel.getName());
        if(meterViewModel.getType().equals("Electric")){
            viewHolder.details.setText(new StringBuilder().append("Record: ")
                    .append(lastRecordDate).append("  E: ")
                    .append(meterViewModel.getConsumption()).append("KWh  Type: ")
                    .append(meterViewModel.getType()).toString());
        }
        else{
            viewHolder.details.setText(new StringBuilder().append("Record: ")
                    .append(lastRecordDate).append("  E: ")
                    .append(meterViewModel.getConsumption()).append("m3  Type: ")
                    .append(meterViewModel.getType()).toString());
        }


        Log.i(TAG, "Populated view holder with data: " + meterViewModel.getName());

        return convertView;
    }
    private static class ViewHolder {
        TextView firstLine;
        TextView details;
        ImageView icon;

        public ViewHolder(TextView textView) {
            firstLine = textView;
        }

        public ViewHolder(){
        }
    }
}
