package tech.mobile.met.ui.energy_provider;

import static tech.mobile.met.ui.devices.DevicesFragmentDirections.actionNavEpToEditDevicesFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import tech.mobile.met.ListViewInvoicesAdapter;
import tech.mobile.met.R;
import tech.mobile.met.Utility;
import tech.mobile.met.databinding.FragmentEnergyProviderBinding;
import tech.mobile.met.models.realmentity.Client_energy_provider_invoices;

public class EnergyProviderFragment extends ListFragment {

    private FragmentEnergyProviderBinding binding;
    private ListView listviewInvoices;
    private EnergyProviderViewModel energyProviderViewModel;
    String FORMAT = "yyyy-MM-dd";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        energyProviderViewModel =
                new ViewModelProvider(this).get(EnergyProviderViewModel.class);

        binding = FragmentEnergyProviderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FloatingActionButton fab = binding.fabInvoices;
        ProgressBar progressBar = binding.progressBar;
        EditText etextViewEPname = binding.etextViewEnergyProviderName;
        TextView textViewHeader = binding.energyproviderHeader;
        Button btnEditEPName = binding.updateEpName;
        listviewInvoices = binding.list;

        energyProviderViewModel.LoadEPName();

        if(energyProviderViewModel.CheckIfInvoicesExist()){
            Log.v("REALMSEARCH", "Found some invoices");
            listviewInvoices.setVisibility(View.VISIBLE);
            textViewHeader.setText(R.string.invoices);
            ListViewInvoicesAdapter dataAdapter = new ListViewInvoicesAdapter(energyProviderViewModel.GetAllClientInvoice());
            listviewInvoices.setAdapter(dataAdapter);
            Utility.setListViewHeightBasedOnChildren(listviewInvoices);

            // Set OnItemClickListener so we can be notified on item clicks
            listviewInvoices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.v("DEVICES", "Clicked position ON ITEM CLICK" + i);
                }
            });
        }

        energyProviderViewModel.geteTextViewEPName().observe(getViewLifecycleOwner(), etextViewEPname::setText);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigation.findNavController(view).navigate(R.id.action_nav_energy_provider_to_editEpFragment);
                @NonNull EnergyProviderFragmentDirections.ActionNavEnergyProviderToEditEpFragment action = EnergyProviderFragmentDirections.actionNavEnergyProviderToEditEpFragment("add");
                Navigation.findNavController(view).navigate(action);
            }
        });

        btnEditEPName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                energyProviderViewModel.UpdateEnergyProviderName(etextViewEPname.getText().toString());
                Snackbar.make(view, getText(R.string.updated_ep_name), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                progressBar.setVisibility(View.GONE);
                Navigation.findNavController(view).navigate(R.id.action_nav_energy_provider_self);

            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        energyProviderViewModel.CloseRealm();
        binding = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO implement some logic
        Log.v("INVOICES", "Clicked position" + position);
        Client_energy_provider_invoices invoice = (Client_energy_provider_invoices) l.getItemAtPosition(position);
        if(invoice != null){
            Log.v("INVOICES", "Clicked invoice with end date: " + invoice.getInvoice_end_date());
            Log.v("INVOICES", "Clicked invoice with start date: " + invoice.getInvoice_start_date());

            String endDate = android.text.format.DateFormat.format(FORMAT, invoice.getInvoice_end_date()).toString();

            //@NonNull DevicesFragmentDirections.ActionNavEpToEditDevicesFragment action = actionNavEpToEditDevicesFragment(device.getName());
            @NonNull EnergyProviderFragmentDirections.ActionNavEnergyProviderToEditEpFragment action = EnergyProviderFragmentDirections.actionNavEnergyProviderToEditEpFragment(endDate);
            Navigation.findNavController(v).navigate(action);
        }

    }
}