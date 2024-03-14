package tech.mobile.met;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmList;
import tech.mobile.met.models.viewmodels.MeterViewModel;
import tech.mobile.met.models.viewmodels.RecordViewModel;
import tech.mobile.met.realmadapters.RealmBaseAdapter;

public class ListViewRecordsAdapter extends BaseAdapter implements ListAdapter {

    String TAG = "REALM_LIST_ADAPTER";
    String FORMAT = "yyyy/MM/dd";
    RealmList<RecordViewModel> recordViewModels;
    Context context;


    public ListViewRecordsAdapter(Context applicationContext, RealmList<RecordViewModel> records){
        this.context = applicationContext;
        this.recordViewModels = records;
    }

    @Override
    public int getCount() {
        return recordViewModels.size();
    }

    @Override
    public RecordViewModel getItem(int i) {
        return recordViewModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return recordViewModels.get(i).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListViewRecordsAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            Log.i(TAG, "Creating view holder");
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listview_records_ressource, parent, false);

            viewHolder = new ListViewRecordsAdapter.ViewHolder();
            viewHolder.firstLine = (TextView) convertView.findViewById(R.id.firstLine);
            viewHolder.details = (TextView) convertView.findViewById(R.id.secondLine);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon_listview);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ListViewRecordsAdapter.ViewHolder) convertView.getTag();
        }
        // as long as we have data
        final RecordViewModel recordViewModel = recordViewModels.get(position);
        String recordDate = android.text.format.DateFormat.format(FORMAT, recordViewModel.getRecord_date()).toString();
        String header = "Record " + recordDate;
        viewHolder.firstLine.setText(header);
        if(recordViewModel.getType().equals("Electric")){
            viewHolder.details.setText(new StringBuilder().append("E-consumption: ")
                    .append(recordViewModel.getConsumption()).append(" KWh  Reading: ")
                    .append(recordViewModel.getCurrent_reading()).toString());

        }
        else{
            viewHolder.details.setText(new StringBuilder().append("E-consumption: ")
                    .append(recordViewModel.getConsumption()).append(" m3  Reading: ")
                    .append(recordViewModel.getCurrent_reading()).toString());
        }

        Log.i(TAG, "Populated view holder with data: " + recordViewModel.getCurrent_reading());
        return convertView;
    }
    private static class ViewHolder {
        TextView firstLine;
        TextView details;
        ImageView icon;

        public ViewHolder(TextView textView) {
            firstLine = textView;
        }

        public ViewHolder(){}
    }
}
