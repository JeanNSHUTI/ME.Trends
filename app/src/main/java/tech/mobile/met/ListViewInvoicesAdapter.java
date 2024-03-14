package tech.mobile.met;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import tech.mobile.met.models.realmentity.Client_energy_provider_invoices;
import tech.mobile.met.realmadapters.RealmBaseAdapter;

public class ListViewInvoicesAdapter extends RealmBaseAdapter<Client_energy_provider_invoices> implements ListAdapter {

    String TAG = "REALM_LIST_ADAPTER";
    String FORMAT = "yyyy/MM/dd";
    //DateTimeFormatter formatter = new DateTimeFormatter("dd/MM/yyyy");
    public ListViewInvoicesAdapter(OrderedRealmCollection<Client_energy_provider_invoices> realmResults) {
        super(realmResults);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListViewInvoicesAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            Log.i(TAG, "Creating view holder");
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listview_invoices_ressource, parent, false);

            viewHolder = new ListViewInvoicesAdapter.ViewHolder();
            viewHolder.firstLine = (TextView) convertView.findViewById(R.id.firstLine);
            viewHolder.details = (TextView) convertView.findViewById(R.id.secondLine);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon_listview);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ListViewInvoicesAdapter.ViewHolder) convertView.getTag();
        }
        // as long as we have data
        if (adapterData != null) {
            final Client_energy_provider_invoices providerInvoice = adapterData.get(position);
            String startDate = android.text.format.DateFormat.format(FORMAT, providerInvoice.getInvoice_start_date()).toString();
            String endDate = android.text.format.DateFormat.format(FORMAT, providerInvoice.getInvoice_end_date()).toString();
            String header = "Invoice " + startDate + " - " + endDate;
                    viewHolder.firstLine.setText(header);
            viewHolder.details.setText(new StringBuilder().append("Charge: ")
                    .append(providerInvoice.getConsumptionCharge()).append(" €  Cost/KWh: ")
                    .append(providerInvoice.getCostPerKwh()).append(" €  E: ")
                    .append(providerInvoice.getElec_yearly_consumption()).append(" KWh").toString());

            Log.i(TAG, "Populated view holder with data: " + providerInvoice.getInvoice_end_date());
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
