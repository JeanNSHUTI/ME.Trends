package tech.mobile.met;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import tech.mobile.met.models.realmentity.ElectricDevice;
import tech.mobile.met.realmadapters.RealmBaseAdapter;


public class RealmListViewAdapter extends RealmBaseAdapter<ElectricDevice> implements ListAdapter{

    String TAG = "REALM_LIST_ADAPTER";
    public RealmListViewAdapter(OrderedRealmCollection<ElectricDevice> realmResults) {
        super(realmResults);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            Log.i(TAG, "Creating view holder");
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listview_ressource, parent, false);

            // create a top-level layout for our item views
            /*LinearLayout layout = new LinearLayout(parent.getContext());
            layout.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));*/
            // create a text view to display item names
            //TextView titleView = new TextView(parent.getContext());
            /*titleView.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));*/
            // attach the text view to the item view layout
            viewHolder = new ViewHolder();
            viewHolder.firstLine = (TextView) convertView.findViewById(R.id.firstLine);
            viewHolder.details = (TextView) convertView.findViewById(R.id.secondLine);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon_listview);
            //layout.addView(titleView);
            //convertView = layout;
            //viewHolder = new ViewHolder(titleView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // as long as we have data
        if (adapterData != null) {
            final ElectricDevice electricDevice = adapterData.get(position);
            viewHolder.firstLine.setText(electricDevice.getName());
            viewHolder.details.setText(new StringBuilder().append("Usage: ")
                    .append(electricDevice.getHours_used_per_day()).append("h    Number: ")
                    .append(electricDevice.getNumber_of_devices()).append("    Rating: ")
                    .append(electricDevice.getPower_rating()).append("W").toString());

            Log.i(TAG, "Populated view holder with data: " + electricDevice.getName());
        } else {
            Log.e(TAG, "No data in adapter! Failed to populate view holder.");
        }
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
