package tech.mobile.met.ui.energy_provider;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.regex.Pattern;

import tech.mobile.met.R;
import tech.mobile.met.databinding.FragmentEditEpBinding;

public class EditEpFragment extends Fragment {

    private EditEpViewModel editEpViewModel;
    private TextView textViewGasCon, textViewElecCon, textViewWaterCon;
    private Button addButton, deleteButton;
    private EditText etextViewGasCon, etextViewElecCon, etextViewWaterCon,
            etextViewStartDate, etextViewEndDate, etextViewConCharge, etextViewCostPerKwh;
    private FragmentEditEpBinding binding;
    private String argMode;
    Pattern NUMBERS_WITH_DOTS = Pattern.compile("^\\d+(\\.\\d+)*$");

    public static EditEpFragment newInstance() {
        return new EditEpFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        editEpViewModel =
                new ViewModelProvider(this).get(EditEpViewModel.class);

        binding = FragmentEditEpBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        argMode = EditEpFragmentArgs.fromBundle(getArguments()).getInvoiceDate();

        textViewGasCon = binding.etextViewGasConsumption;
        textViewElecCon = binding.etextViewDisplayElectricUsage;
        textViewWaterCon = binding.etextViewDisplayWaterUsage;
        etextViewStartDate = binding.etextViewStartDate;
        etextViewEndDate = binding.etextViewDisplayEndDate;
        etextViewGasCon = binding.etextViewDisplayInGasCon;
        etextViewElecCon = binding.etextViewDisplayInElecCon;
        etextViewWaterCon = binding.etextViewDisplayInWaterCon;
        etextViewConCharge = binding.etextViewDisplayChargeCon;
        etextViewCostPerKwh = binding.etextViewDisplayCostKwh;
        addButton = binding.saveButton;
        deleteButton = binding.deleteButton;

        Log.v("EDIT_INVOICE", "Received following arg:" + argMode);

        if(!EditEpFragmentArgs.fromBundle(getArguments()).getInvoiceDate().equals("add")){
            deleteButton.setVisibility(View.VISIBLE);
            editEpViewModel.LoadEditInvoice(argMode);



        }
        editEpViewModel.UpdateConsumptions();

        editEpViewModel.getEtextViewStarDate().observe(getViewLifecycleOwner(), etextViewStartDate::setText);
        editEpViewModel.getEtextViewEndDate().observe(getViewLifecycleOwner(), etextViewEndDate::setText);
        editEpViewModel.getTextViewGasCon().observe(getViewLifecycleOwner(), etextViewGasCon::setText);
        editEpViewModel.getEtextViewElecCon().observe(getViewLifecycleOwner(), etextViewElecCon::setText);
        editEpViewModel.getTextViewWaterCon().observe(getViewLifecycleOwner(), etextViewWaterCon::setText);
        editEpViewModel.getEtextViewConCharge().observe(getViewLifecycleOwner(), etextViewConCharge::setText);
        editEpViewModel.getEtextViewCostKwh().observe(getViewLifecycleOwner(), etextViewCostPerKwh::setText);

        editEpViewModel.getTextViewElecCon().observe(getViewLifecycleOwner(), textViewElecCon::setText);
        editEpViewModel.getTextViewGasCon().observe(getViewLifecycleOwner(), textViewGasCon::setText);
        editEpViewModel.getTextViewWaterCon().observe(getViewLifecycleOwner(), textViewWaterCon::setText);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(EditEpFragmentArgs.fromBundle(getArguments()).getInvoiceDate().equals("add")){
                    //Get current invoice details
                    String[] invoiceDetails = new String[7];
                    invoiceDetails[0] = etextViewStartDate.getText().toString().trim();
                    invoiceDetails[1] = etextViewEndDate.getText().toString().trim();
                    invoiceDetails[2] = etextViewGasCon.getText().toString().trim();
                    invoiceDetails[3] = etextViewElecCon.getText().toString().trim();
                    invoiceDetails[4] = etextViewWaterCon.getText().toString().trim();
                    invoiceDetails[5] = etextViewConCharge.getText().toString().trim();
                    invoiceDetails[6] = etextViewCostPerKwh.getText().toString().trim();

                    if(invoiceDetails[5].isEmpty() || !(invoiceDetails[5].matches(getString(R.string.number_with_dots))) || invoiceDetails[5].length() == 0 ||
                            invoiceDetails[6].isEmpty() || !(invoiceDetails[6].matches(getString(R.string.number_with_dots))) || invoiceDetails[6].length() == 0){
                        Snackbar.make(view, "Failed. please verify format of charge and cost", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    else{
                        editEpViewModel.Addinvoice(invoiceDetails);
                        Snackbar.make(view, getText(R.string.added_invoice), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        Log.v("ADD_INVOICE", "Successfully added invoice");
                        Navigation.findNavController(view).navigate(R.id.action_editEpFragment_to_nav_energy_provider);
                    }

                }
                else{
                    Log.v("UPDATE_INVOICE", "Updating invoice");

                }

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEpViewModel.DeleteInvoice(argMode);

                Snackbar.make(view, getText(R.string.removed_invoice), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Navigation.findNavController(view).navigate(R.id.action_editEpFragment_to_nav_energy_provider);

            }
        });


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        editEpViewModel = new ViewModelProvider(this).get(EditEpViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        editEpViewModel.CloseRealm();
        binding = null;
    }

}