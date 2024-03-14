package tech.mobile.met.ui.home;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;

import tech.mobile.met.R;
import tech.mobile.met.databinding.FragmentEditClientBinding;

public class EditClientFragment extends Fragment {

    private FragmentEditClientBinding binding;
    private EditClientViewModel editClientViewModel;
    private EditText etextViewFullname, etextViewEnergyProvider,
            etextViewStreetNo, etextViewCityPC, etextViewProvinceCountry;
    private TextView etextViewEmail;
    private TextView textViewWelcome;
    private Button updatebutton;
    private ProgressBar loadProgressBar, updateProgressBar;
    private ImageView imgProfileFromGallery;
    private final String profilImageName = "profileImageMET.png";

    public static EditClientFragment newInstance() {
        return new EditClientFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        editClientViewModel =
                new ViewModelProvider(this).get(EditClientViewModel.class);
        //HomeViewModel homeViewModelEditProfile =
        //        new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentEditClientBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        editClientViewModel.LoadViewData();

        loadProgressBar = binding.progressBar;
        loadProgressBar.setVisibility(View.VISIBLE);
        final TextView textView = binding.textHome;
        textViewWelcome = binding.textViewWelcomeMsg;
        etextViewFullname = binding.etextViewDisplayFullname;
        etextViewEmail = binding.etextViewDisplayEmail;
        etextViewEnergyProvider = binding.etextViewDisplayEP;
        etextViewStreetNo = binding.etextViewDisplayStreetAddress;
        etextViewProvinceCountry = binding.etextViewDisplayProvinceCountry;
        etextViewCityPC = binding.etextViewDisplayCityPC;
        updatebutton = binding.updateButton;
        loadProgressBar = binding.progressBar;
        updateProgressBar = binding.updateProfileProgressBar;
        imgProfileFromGallery = binding.imageViewProfilePic;

        editClientViewModel.getEmailText().observe(getViewLifecycleOwner(), etextViewEmail::setText);
        editClientViewModel.getFullnameText().observe(getViewLifecycleOwner(), etextViewFullname::setText);
        editClientViewModel.getEnergyProviderText().observe(getViewLifecycleOwner(), etextViewEnergyProvider::setText);
        editClientViewModel.getStreetNoText().observe(getViewLifecycleOwner(), etextViewStreetNo::setText);
        editClientViewModel.getViewCityPCText().observe(getViewLifecycleOwner(), etextViewCityPC::setText);
        editClientViewModel.getViewProvinceCountryText().observe(getViewLifecycleOwner(), etextViewProvinceCountry::setText);

        loadProgressBar.setVisibility(View.GONE);

        File[] sdCardDirectory = getParentFragment().getActivity().getExternalFilesDirs(Environment.DIRECTORY_DCIM);
        File image = new File(sdCardDirectory[0], profilImageName);

        if(image.exists()){
            Bitmap profileBitMap = BitmapFactory.decodeFile(image.getAbsolutePath());
            imgProfileFromGallery.setImageBitmap(profileBitMap);
            imgProfileFromGallery.setBackgroundColor(Color.TRANSPARENT);
        }

        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean invalidate = false;
                updateProgressBar.setVisibility(View.VISIBLE);
                String[] clientDetails = new String[10];
                String[] fullname = etextViewFullname.getText().toString().split(" ");
                Log.v("UPDATE", "Firstname:" + fullname[0].trim());
                clientDetails[0] = fullname[0].trim();
                Log.v("UPDATE", "Lastname:"+ fullname[1].trim());
                clientDetails[1] = fullname[1].trim();
                Log.v("UPDATE", "EnergyProvider:"+ etextViewEnergyProvider.getText().toString());
                clientDetails[2] = etextViewEnergyProvider.getText().toString().trim();
                if(!etextViewStreetNo.getText().toString().contains(",")){
                    invalidate = true;
                }
                else{
                    String[] street_no = etextViewStreetNo.getText().toString().split(getString(R.string.comma_separator));
                    Log.v("UPDATE", "Street:" + street_no[0].trim());
                    clientDetails[3] = street_no[0].trim();
                    Log.v("UPDATE", "Number:"+ street_no[1].trim());
                    clientDetails[4] = street_no[1].trim();
                }
                if(!etextViewCityPC.getText().toString().contains(",")){
                    invalidate = true;
                }
                else{
                    String[] city_postcode = etextViewCityPC.getText().toString().split(getString(R.string.comma_separator));
                    Log.v("UPDATE", "City:" + city_postcode[0].trim());
                    clientDetails[5] = city_postcode[0].trim();
                    Log.v("UPDATE", "Postcode:"+ city_postcode[1].trim());
                    clientDetails[6] = city_postcode[1].trim();
                }
                if(!etextViewProvinceCountry.getText().toString().contains(",")){
                    invalidate = true;
                }
                else{
                    String[] province_cntry = etextViewProvinceCountry.getText().toString().split(getString(R.string.comma_separator));
                    Log.v("UPDATE", "City:" + province_cntry[0].trim());
                    clientDetails[7] = province_cntry[0].trim();
                    Log.v("UPDATE", "Postcode:"+ province_cntry[1].trim());
                    clientDetails[8] = province_cntry[1].trim();
                }


                if(invalidate != true){
                    editClientViewModel.UpdateClientInfo(clientDetails);
                    Snackbar.make(view, getText(R.string.updated_client), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    updateProgressBar.setVisibility(View.GONE);
                    //Navigation.findNavController(view).navigate(R.id.action_editClientFragment_to_nav_home);
                    Navigation.findNavController(view).navigate(R.id.action_editClientFragment_self);
                }
                else{
                    editClientViewModel.UpdateClientInfo(clientDetails);
                    Snackbar.make(view, getString(R.string.invalidate_string), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    updateProgressBar.setVisibility(View.GONE);
                }


            }
        });

        //return inflater.inflate(R.layout.fragment_edit_client, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        editClientViewModel = new ViewModelProvider(this).get(EditClientViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        editClientViewModel.CloseRealm();
    }

}