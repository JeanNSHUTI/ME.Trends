package tech.mobile.met.ui.meters;

import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import io.realm.RealmList;
import tech.mobile.met.ListViewMeterAdapter;
import tech.mobile.met.R;
import tech.mobile.met.Utility;
import tech.mobile.met.databinding.FragmentMetersBinding;
import tech.mobile.met.models.viewmodels.MeterViewModel;

public class MetersFragment extends ListFragment {

    private MetersViewModel metersViewModel;
    private FragmentMetersBinding binding;
    private ListView listviewMeters;
    private String deleteMeterText;

    public static MetersFragment newInstance() {
        return new MetersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        metersViewModel = new ViewModelProvider(this).get(MetersViewModel.class);

        binding = FragmentMetersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FloatingActionButton fab = binding.fabMeters;
        listviewMeters = binding.list;
        TextView header = binding.metersHeader;
        Button deleteButton = binding.deleteMeterButton;

        RealmList<MeterViewModel> meters = metersViewModel.CollectAllMeters();

        if(metersViewModel.CheckIfAnyMetersExist(meters)){
            deleteButton.setVisibility(View.VISIBLE);
            Log.v("REALMSEARCH", "Found some meters");
            header.setText(R.string.my_meters);
            listviewMeters.setVisibility(View.VISIBLE);

            ListViewMeterAdapter dataAdapter = new ListViewMeterAdapter(getContext(),meters);
            listviewMeters.setAdapter(dataAdapter);
            Utility.setListViewHeightBasedOnChildren(listviewMeters);

            listviewMeters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.v("METERS", "Clicked position ON ITEM CLICK" + i);
                }
            });


        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertName = new AlertDialog.Builder(getContext());
                final EditText editTextName1 = new EditText(getContext());
                editTextName1.setHint(R.string.delete_meter_sequence);

                alertName.setIcon(R.mipmap.ic_launcher);
                alertName.setTitle("Enter type and name of meter to delete.");
                // titles can be used regardless of a custom layout or not
                alertName.setView(editTextName1);
                LinearLayout layoutName = new LinearLayout(getContext());
                layoutName.setOrientation(LinearLayout.VERTICAL);
                layoutName.addView(editTextName1); // displays the user input bar
                alertName.setView(layoutName);
                alertName.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText txt = editTextName1; // variable to collect user input
                        deleteMeterText = txt.getText().toString(); // analyze input (txt) in this method

                        if(metersViewModel.DeleteMeterFromRealm(deleteMeterText)){
                            Snackbar.make(view, getString(R.string.meter_delete), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            Navigation.findNavController(view).navigate(R.id.action_nav_meters_self);
                        }
                        else {
                            Snackbar.make(view, getText(R.string.failed_delete_cerify_string), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });
                // set the negative button if the user is not interested to select or change already selected item
                alertName.setNegativeButton("Cancel", (dialog, which) -> {

                });

                // create and build the AlertDialog instance with the AlertDialog builder instance
                AlertDialog customAlertDialog = alertName.create();

                customAlertDialog.show();

            }
        });

        //Open dialog for meter selection and then create corresponding meter in realm
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int[] checkedItem = {-1};
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(container.getContext());

                alertDialog.setIcon(R.mipmap.ic_launcher);
                alertDialog.setTitle(getText(R.string.select_m_type));
                final String[] listItems = new String[]{getString(R.string.electric_meter), getString(R.string.gas_meter), getString(R.string.water_meter)};

                alertDialog.setSingleChoiceItems(listItems, checkedItem[0], (dialog, which) -> {
                    // update the selected item which is selected by the user so that it should be selected
                    // when user opens the dialog next time and pass the instance to setSingleChoiceItems method
                    //checkedItem[0] = which;

                    // when selected an item the dialog should be closed with the dismiss method
                    dialog.dismiss();

                    // Create meter
                    Log.v("SELECTED", "METER of type: " + listItems[which]);
                    if(metersViewModel.CreateMeter(listItems[which])){
                        Snackbar.make(view, getText(R.string.created_meter), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        Navigation.findNavController(view).navigate(R.id.action_nav_meters_self);
                    }
                    else {
                        Snackbar.make(view, "Failed to create Meter. Meter names should be unique!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }


                });

                // set the negative button if the user is not interested to select or change already selected item
                alertDialog.setNegativeButton("Cancel", (dialog, which) -> {

                });

                // create and build the AlertDialog instance with the AlertDialog builder instance
                AlertDialog customAlertDialog = alertDialog.create();

                // show the alert dialog when the button is clicked
                customAlertDialog.show();
            }
        });




        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        metersViewModel = new ViewModelProvider(this).get(MetersViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        metersViewModel.CloseRealm();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO implement some logic
        Log.v("DEVICES", "Clicked position" + position);
        MeterViewModel selectedMeter = (MeterViewModel) l.getItemAtPosition(position);
        if(selectedMeter != null){
            Log.v("DEVICES", "Clicked: " + selectedMeter.getName());

            String meterArgs = selectedMeter.getType() + "-" + selectedMeter.getName();

            @NonNull MetersFragmentDirections.ActionNavMetersToDetailsMetersFragment action = MetersFragmentDirections.actionNavMetersToDetailsMetersFragment(meterArgs) ;
            Navigation.findNavController(v).navigate(action);
        }

    }


}