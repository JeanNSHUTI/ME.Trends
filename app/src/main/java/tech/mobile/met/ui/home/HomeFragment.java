package tech.mobile.met.ui.home;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivilegedExceptionAction;

import tech.mobile.met.HomeActivity;
import tech.mobile.met.R;
import tech.mobile.met.databinding.FragmentHomeBinding;
import tech.mobile.met.models.realmentity.UserCredentials;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private final int GALLERY_REQUEST_CODE = 1000;
    private final int RESULT_OK = -1;
    private TextView textViewWelcome, textViewFullname, textViewEmail, textViewEnergyProvider,
            textViewStreetNo, textViewCityPC, textViewProvinceCountry;
    private Button editbutton, uploadButton;
    private ProgressBar progressBar;
    private ImageView imgProfileFromGallery;
    private final String profilImageName = "profileImageMET.png";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        progressBar = binding.progressBar;
        progressBar.setVisibility(View.VISIBLE);
        final TextView textView = binding.textHome;
        textViewWelcome = binding.textViewWelcomeMsg;
        textViewFullname = binding.textViewDisplayFullname;
        textViewEmail = binding.textViewDisplayEmail;
        textViewEnergyProvider = binding.textViewDisplayEP;
        textViewStreetNo = binding.textViewDisplayStreetAddress;
        textViewProvinceCountry = binding.textViewDisplayProvinceCountry;
        textViewCityPC = binding.textViewDisplayCityPC;
        editbutton = binding.buttonEdit;
        uploadButton = binding.buttonUploadPhoto;
        imgProfileFromGallery = binding.imageViewProfilePic;

        homeViewModel.UpdateClientData();
        homeViewModel.LoadViewData();


        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        homeViewModel.getWelcomeText().observe(getViewLifecycleOwner(), textViewWelcome::setText);
        homeViewModel.getEmailText().observe(getViewLifecycleOwner(), textViewEmail::setText);
        homeViewModel.getFullnameText().observe(getViewLifecycleOwner(), textViewFullname::setText);
        homeViewModel.getEnergyProviderText().observe(getViewLifecycleOwner(), textViewEnergyProvider::setText);
        homeViewModel.getStreetNoText().observe(getViewLifecycleOwner(), textViewStreetNo::setText);
        homeViewModel.getViewCityPCText().observe(getViewLifecycleOwner(), textViewCityPC::setText);
        homeViewModel.getViewProvinceCountryText().observe(getViewLifecycleOwner(), textViewProvinceCountry::setText);

        File[] sdCardDirectory = getParentFragment().getActivity().getExternalFilesDirs(Environment.DIRECTORY_DCIM);
        File image = new File(sdCardDirectory[0], profilImageName);

        if(image.exists()){
            Bitmap profileBitMap = BitmapFactory.decodeFile(image.getAbsolutePath());
            imgProfileFromGallery.setImageBitmap(profileBitMap);
            imgProfileFromGallery.setBackgroundColor(Color.TRANSPARENT);
        }


        progressBar.setVisibility(View.GONE);

        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_editClientFragment);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQUEST_CODE);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        //homeViewModel.CloseRealm();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode==GALLERY_REQUEST_CODE){
                imgProfileFromGallery.setImageURI(data.getData());
                BitmapDrawable drawable = (BitmapDrawable) imgProfileFromGallery.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                //File sdCardDirectory = Environment.getExternalStorageDirectory();
                File[] sdCardDirectory = getParentFragment().getActivity().getExternalFilesDirs(Environment.DIRECTORY_DCIM);
                //File sdCardDirectory = new File(Environment.DIRECTORY_PICTURES);

                File image = new File(sdCardDirectory[0], profilImageName);
                //File image = new File(Environment.DIRECTORY_PICTURES, "testProfile.png");

                //File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                //bmpUri = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", image);

                boolean success = false;

                // Encode the file as a PNG image.
                FileOutputStream outStream;
                try {

                    outStream = new FileOutputStream(image);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    /* 100 to keep full quality of the image */

                    outStream.flush();
                    outStream.close();
                    success = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (success) {
                    Toast.makeText(getContext(), "Image saved with success",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(),
                            "Error during image saving", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


}