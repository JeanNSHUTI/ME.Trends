package tech.mobile.met.ui.devices;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import tech.mobile.met.R;
import tech.mobile.met.databinding.FragmentEditClientBinding;
import tech.mobile.met.databinding.FragmentEditDevicesBinding;

public class EditDevicesFragment extends Fragment {

    private FragmentEditDevicesBinding binding;
    private EditDevicesViewModel mViewModel;
    //private EditDevicesFragmentArgs args;
    private EditDevicesViewModel editClientViewModel;
    TextView cost_per_day, cost_per_watt, welcome_name;
    EditText ename, ehours_used, epower_rating, enumber_devices;
    private Button updatebutton, deletebutton;
    private ProgressBar loadProgressBar, updateProgressBar;

    public static EditDevicesFragment newInstance() {
        return new EditDevicesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        editClientViewModel = new ViewModelProvider(this).get(EditDevicesViewModel.class);

        binding = FragmentEditDevicesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loadProgressBar = binding.progressBar;
        updateProgressBar = binding.updateDeviceProgressBar;
        loadProgressBar.setVisibility(View.VISIBLE);
        welcome_name = binding.textViewDevice;
        ename = binding.etextViewDeviceName;
        ehours_used = binding.etextViewDisplayHourUsage;
        epower_rating = binding.etextViewDisplayPowerRating;
        enumber_devices = binding.etextViewDisplayNumberDevices;
        updatebutton = binding.updateDeviceButton;
        deletebutton = binding.deleteDeviceButton;
        cost_per_day = binding.textViewDisplayCostDay;
        cost_per_watt = binding.textViewDisplayCostWatt;

        //Collect for selected device
        editClientViewModel.GetSelectedDevice(EditDevicesFragmentArgs.fromBundle(getArguments()).getDeviceName());

        //Update text views
        editClientViewModel.getTextViewWelcomeName().observe(getViewLifecycleOwner(), welcome_name::setText);
        editClientViewModel.getTextViewEname().observe(getViewLifecycleOwner(), ename::setText);
        editClientViewModel.getTextViewEHousUsed().observe(getViewLifecycleOwner(), ehours_used::setText);
        editClientViewModel.getTextViewPowerRating().observe(getViewLifecycleOwner(), epower_rating::setText);
        editClientViewModel.getTextViewNumberDevices().observe(getViewLifecycleOwner(), enumber_devices::setText);
        editClientViewModel.getTextViewCostDay().observe(getViewLifecycleOwner(), cost_per_day::setText);
        editClientViewModel.getTextViewCostWatt().observe(getViewLifecycleOwner(), cost_per_watt::setText);

        loadProgressBar.setVisibility(View.GONE);

        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProgressBar.setVisibility(View.VISIBLE);
                String[] deviceDetails = new String[5];
                deviceDetails[0] = ename.getText().toString().trim();
                deviceDetails[1] = ehours_used.getText().toString().trim();
                deviceDetails[2] = epower_rating.getText().toString().trim();
                deviceDetails[3] = enumber_devices.getText().toString().trim();
                editClientViewModel.UpdateDevice(EditDevicesFragmentArgs.fromBundle(getArguments()).getDeviceName(), deviceDetails);
                updateProgressBar.setVisibility(View.GONE);
                Snackbar.make(view, getText(R.string.updated_device), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Navigation.findNavController(view).navigate(R.id.action_editDevicesFragment_to_nav_ep);
            }
        });

        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProgressBar.setVisibility(View.VISIBLE);
                editClientViewModel.DeleteDeviceFromRealm(EditDevicesFragmentArgs.fromBundle(getArguments()).getDeviceName());
                updateProgressBar.setVisibility(View.GONE);
                Snackbar.make(view, getText(R.string.deleted_device), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Navigation.findNavController(view).navigate(R.id.action_editDevicesFragment_to_nav_ep);

            }
        });


        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditDevicesViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        editClientViewModel.CloseRealm();
    }

}