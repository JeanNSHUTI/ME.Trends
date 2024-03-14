package tech.mobile.met.ui.devices;

import static tech.mobile.met.ui.devices.DevicesFragmentDirections.*;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import io.realm.RealmResults;
import tech.mobile.met.R;
import tech.mobile.met.RealmListViewAdapter;
import tech.mobile.met.Utility;
import tech.mobile.met.databinding.FragmentDevicesBinding;
import tech.mobile.met.models.realmentity.ElectricDevice;

public class DevicesFragment extends ListFragment {

    private FragmentDevicesBinding binding;
    private ListView listviewDevices;
    private DevicesViewModel devicesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       devicesViewModel =
                new ViewModelProvider(this).get(DevicesViewModel.class);

        binding = FragmentDevicesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FloatingActionButton fab = binding.fabDevices;
        listviewDevices = binding.list;
        TextView header = binding.devicesHeader;

        RealmResults<ElectricDevice> myDevices = devicesViewModel.GetAllDevices();

        //Check if at least one device was found and display in list
        if(devicesViewModel.CheckIfDevicesExist(myDevices)){
            if(devicesViewModel.CheckIfInvoiceExists()){
                devicesViewModel.UpdateDeviceCosts();
            }
            Log.v("REALMSEARCH", "Found some devices");
            header.setText(R.string.my_devices);
            listviewDevices.setVisibility(View.VISIBLE);

            RealmListViewAdapter dataAdapter = new RealmListViewAdapter(myDevices);
            listviewDevices.setAdapter(dataAdapter);
            Utility.setListViewHeightBasedOnChildren(listviewDevices);

            // Set OnItemClickListener so we can be notified on item clicks
            listviewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.v("DEVICES", "Clicked position ON ITEM CLICK" + i);
                }
            });
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                devicesViewModel.CreateDevice();
                Snackbar.make(view, getText(R.string.created_device), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Navigation.findNavController(view).navigate(R.id.action_nav_mydevices_self);
            }
        });


        //final TextView textView = binding.textGallery;
        //devicesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        devicesViewModel.CloseRealm();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO implement some logic
        Log.v("DEVICES", "Clicked position" + position);
        ElectricDevice device = (ElectricDevice) l.getItemAtPosition(position);
        if(device != null){
            Log.v("DEVICES", "Clicked name" + device.getName());

            @NonNull ActionNavEpToEditDevicesFragment action = actionNavEpToEditDevicesFragment(device.getName());
            Navigation.findNavController(v).navigate(action);
        }

    }


}