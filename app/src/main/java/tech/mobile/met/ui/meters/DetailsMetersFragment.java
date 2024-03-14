package tech.mobile.met.ui.meters;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import tech.mobile.met.ListViewRecordsAdapter;
import tech.mobile.met.R;
import tech.mobile.met.Utility;
import tech.mobile.met.databinding.FragmentDetailsMetersBinding;
import tech.mobile.met.models.viewmodels.MeterViewModel;
import tech.mobile.met.models.viewmodels.RecordViewModel;

public class DetailsMetersFragment extends ListFragment {

    private DetailsMetersViewModel detailsMetersViewModel;
    private FragmentDetailsMetersBinding binding;
    private String argMeterName;
    private String resultText;
    private ListView listViewRecords;
    private Button btnDeleteRecord;
    private Bitmap takenImage;
    private ProgressBar progressBar;
    private Date deleteRecordSequence;
    public final String APP_TAG = "METrends";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private final int RESULT_OK = -1;
    private ActivityResultLauncher<Intent> cameraResultLauncher;
    public String photoFileName = "photo.png";
    //private ImageView imgReading;
    File photoFile;

    public static DetailsMetersFragment newInstance() {
        return new DetailsMetersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        detailsMetersViewModel = new ViewModelProvider(this).get(DetailsMetersViewModel.class);

        binding = FragmentDetailsMetersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EditText eTextViewMeterName = binding.etextViewMeterName;
        TextView textViewHeader = binding.recordsHeader;
        Button btnUpdateMeterName = binding.updateMeterName;
        Button btnAddRecordManual = binding.addRecordButton;
        Button btnAddRecordCam = binding.addRecordCamButton;
        btnDeleteRecord = binding.deleteRecordButton;
        progressBar = binding.progressBar;
        listViewRecords = binding.list;
        //imgReading = binding.readingImage;
        final CharSequence SUCCESS = "Successfully analysed photo";
        final CharSequence FAIL = "Failed to analyse photo. Sorry.";
        int duration = Toast.LENGTH_SHORT;

        //Get arguments
        argMeterName = DetailsMetersFragmentArgs.fromBundle(getArguments()).getMeterName();

        //Verify arguments are not null
        if(argMeterName != null){
            Log.v("RECORDS_METER", "Received following arg:" + argMeterName);

            String[] type_name = argMeterName.split("-");
            detailsMetersViewModel.setMeterName(type_name[1].trim());
            detailsMetersViewModel.setMeterType(type_name[0].trim());
            detailsMetersViewModel.seteTextViewMeterName(type_name[1].trim());

            if(detailsMetersViewModel.CheckIfRecordsExist()){
                Log.v("REALMSEARCH", "Found some records for meter");
                textViewHeader.setText(R.string.records_header);
                listViewRecords.setVisibility(View.VISIBLE);

                RealmList<RecordViewModel> recordsVM = detailsMetersViewModel.LoadRecordsForDisplay();

                ListViewRecordsAdapter dataAdapter = new ListViewRecordsAdapter(getContext(), recordsVM);
                listViewRecords.setAdapter(dataAdapter);
                Utility.setListViewHeightBasedOnChildren(listViewRecords);

                listViewRecords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.v("Records", "Clicked position ON ITEM CLICK" + i);
                    }
                });


            }

        }
        else{
            Log.v("RECORDS_METER", "No args received");
        }

        btnAddRecordManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] type_name = argMeterName.split("-");
                detailsMetersViewModel.setMeterName(type_name[1].trim());
                detailsMetersViewModel.setMeterType(type_name[0].trim());

                AlertDialog.Builder alertName = new AlertDialog.Builder(getContext());
                final EditText editTextReading = new EditText(getContext());
                editTextReading.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                editTextReading.setHint(R.string.reading_hint);

                alertName.setIcon(R.mipmap.ic_launcher);
                alertName.setTitle(getText(R.string.enter_meter_reading));
                alertName.setView(editTextReading);

                alertName.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText txt = editTextReading; // variable to collect user input
                        String enteredReading = txt.getText().toString(); // analyze input (txt) in this method

                        if(detailsMetersViewModel.SaveNewRecord(enteredReading)){
                            Snackbar.make(view, getString(R.string.added_reading), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            Navigation.findNavController(view).navigate(R.id.action_detailsMetersFragment_to_nav_meters);
                            //Navigation.findNavController(view).navigate(DetailsMetersFragmentDirections.actionDetailsMetersFragmentSelf(argMeterName));
                        }
                        else {
                            Snackbar.make(view, getString(R.string.faled_to_add_record), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            //Navigation.findNavController(view).navigate(DetailsMetersFragmentDirections.actionDetailsMetersFragmentSelf(argMeterName));
                            Navigation.findNavController(view).navigate(R.id.action_detailsMetersFragment_to_nav_meters);
                        }
                    }
                });
                // set the negative button if the user is not interested to select or change already selected item
                alertName.setNegativeButton("Cancel", (dialog, which) -> {

                });

                AlertDialog customAlertDialog = alertName.create();

                customAlertDialog.show();

            }
        });

        btnUpdateMeterName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] type_name = argMeterName.split("-");
                detailsMetersViewModel.setMeterName(type_name[1].trim());
                detailsMetersViewModel.setMeterType(type_name[0].trim());

                if(detailsMetersViewModel.UpdateMeterName(eTextViewMeterName.getText().toString())){
                    Snackbar.make(view, getText(R.string.updated_meter_name), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Navigation.findNavController(view).navigate(R.id.action_detailsMetersFragment_to_nav_meters);
                }
                else{
                    Snackbar.make(view, getString(R.string.failed_meter_name_update), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //Navigation.findNavController(view).navigate(DetailsMetersFragmentDirections.actionDetailsMetersFragmentSelf(argMeterName));
                    Navigation.findNavController(view).navigate(R.id.action_detailsMetersFragment_to_nav_meters);
                }

            }
        });

        btnDeleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] type_name = argMeterName.split("-");
                detailsMetersViewModel.setMeterName(type_name[1].trim());
                detailsMetersViewModel.setMeterType(type_name[0].trim());

                AlertDialog.Builder alertDelete = new AlertDialog.Builder(getContext());
                final EditText editTextDelete = new EditText(getContext());

                editTextDelete.setInputType(InputType.TYPE_CLASS_DATETIME);
                editTextDelete.setText(android.text.format.DateFormat.format("yyyy-MM-dd", deleteRecordSequence));

                alertDelete.setIcon(R.mipmap.ic_launcher);
                alertDelete.setTitle(getText(R.string.delete_record_with_this_date));
                alertDelete.setView(editTextDelete);

                alertDelete.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(detailsMetersViewModel.RemoveRecordFromSelectedMeter(deleteRecordSequence)){
                            Snackbar.make(view, getString(R.string.removed_record), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            //Navigation.findNavController(view).navigate(DetailsMetersFragmentDirections.actionDetailsMetersFragmentSelf(argMeterName));
                            Navigation.findNavController(view).navigate(R.id.action_detailsMetersFragment_to_nav_meters);
                        }
                        else {
                            Snackbar.make(view, getString(R.string.failed_remove_string), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            //Navigation.findNavController(view).navigate(DetailsMetersFragmentDirections.actionDetailsMetersFragmentSelf(argMeterName));
                        }
                    }
                });
                // set the negative button if the user is not interested to select or change already selected item
                alertDelete.setNegativeButton("Cancel", (dialog, which) -> {

                });

                AlertDialog customAlertDialog = alertDelete.create();

                customAlertDialog.show();
            }
        });

        cameraResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // by this point we have the camera photo on disk
                        //takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        //progressBar.setVisibility(View.GONE);
                        try {
                            takenImage = handleSamplingAndRotationBitmap(getContext(), Uri.parse(photoFile.getAbsolutePath()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // RESIZE BITMAP, see section below
                        // Load the taken image into a preview
                        //ImageView ivPreview = ivPostImg;
                        //ivPreview.setImageBitmap(takenImage);

                        //Save image here?

                        }
                    else { // Result was a failure
                        Toast.makeText(getContext(), "Error analysing image values", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btnAddRecordCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Take image, save, load, analyse, return digit string and delete resource
                progressBar.setVisibility(View.VISIBLE);
                if(isIntentAvailable(getContext(), MediaStore.ACTION_IMAGE_CAPTURE)){
                    photoFile = getPhotoFileUri(photoFileName);
                    dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);


                }
            }
        });



        detailsMetersViewModel.geteTextViewMeterName().observe(getViewLifecycleOwner(), eTextViewMeterName::setText);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        detailsMetersViewModel = new ViewModelProvider(this).get(DetailsMetersViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        detailsMetersViewModel.CloseRealm();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO implement some logic
        Log.v("DEVICES", "Clicked position" + position);
        RecordViewModel selectedRecord = (RecordViewModel) l.getItemAtPosition(position);
        if(selectedRecord != null){
            Log.v("DEVICES", "Clicked: " + selectedRecord.getRecord_date());

            deleteRecordSequence = selectedRecord.getRecord_date();
            btnDeleteRecord.setVisibility(View.VISIBLE);
        }

    }

    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        startActivityForResult(takePictureIntent, actionCode);
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        //File[] sdCardDirectory = getParentFragment().getActivity().getExternalFilesDirs(Environment.DIRECTORY_DCIM);
        File sdCardDirectory = new File(Environment.DIRECTORY_PICTURES);

        //File mediaStorageDir = new File(sdCardDirectory[0], APP_TAG);
        File mediaStorageDir = new File(getParentFragment().getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
                progressBar.setVisibility(View.GONE);

                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                try {
                    takenImage = handleSamplingAndRotationBitmap(getContext(), Uri.parse(photoFile.getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Analyse image and extract digits -> Maybe move to On create scope !
                int rotationDegree = 90;
                TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                InputImage image = InputImage.fromBitmap(takenImage, rotationDegree);

                Task<Text> resultProcess =
                        recognizer.process(image)
                                .addOnSuccessListener(new OnSuccessListener<Text>() {
                                    @Override
                                    public void onSuccess(Text visionText) {
                                        // Task completed successfully
                                        // ...
                                        progressBar.setVisibility(View.GONE);
                                        if(resultText != null){
                                            Toast.makeText(getContext(), "Successfully processed image " + resultText, Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(getContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                //Definitely here
                resultProcess.addOnCompleteListener(new OnCompleteListener<Text>() {
                    @Override
                    public void onComplete(@NonNull Task<Text> task) {
                        resultText = resultProcess.getResult().getText();
                        if(resultText != null){
                            Log.v("IMAGE", "Analysed result: " + resultText);

                            AlertDialog.Builder alertName = new AlertDialog.Builder(getContext());
                            final EditText editTextReading = new EditText(getContext());
                            editTextReading.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                            //editTextReading.setHint(R.string.reading_hint);
                            editTextReading.setText(resultText);

                            alertName.setIcon(R.mipmap.ic_launcher);
                            alertName.setTitle(getText(R.string.enter_meter_reading));
                            alertName.setView(editTextReading);

                            alertName.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    EditText txt = editTextReading; // variable to collect user input
                                    String enteredReading = txt.getText().toString(); // analyze input (txt) in this method

                                    if(detailsMetersViewModel.SaveNewRecord(enteredReading)){
                                        Toast.makeText(getContext(), "Successfully processed image", Toast.LENGTH_SHORT).show();
                                        //Snackbar.make(getContext(), getString(R.string.added_reading), Snackbar.LENGTH_LONG)
                                         //       .setAction("Action", null).show();
                                        Navigation.findNavController(getView()).navigate(R.id.action_detailsMetersFragment_to_nav_meters);
                                        //Navigation.findNavController(view).navigate(DetailsMetersFragmentDirections.actionDetailsMetersFragmentSelf(argMeterName));
                                    }
                                    else {
                                        Toast.makeText(getContext(), "Failed processed image ", Toast.LENGTH_SHORT).show();
                                        //Snackbar.make(view, getString(R.string.faled_to_add_record), Snackbar.LENGTH_LONG)
                                        //        .setAction("Action", null).show();
                                        Navigation.findNavController(getView()).navigate(DetailsMetersFragmentDirections.actionDetailsMetersFragmentSelf(argMeterName));
                                        //Navigation.findNavController(view).navigate(R.id.action_detailsMetersFragment_to_nav_meters);
                                    }
                                }
                            });
                            // set the negative button if the user is not interested to select or change already selected item
                            alertName.setNegativeButton("Cancel", (dialog, which) -> {

                            });

                            AlertDialog customAlertDialog = alertName.create();

                            customAlertDialog.show();
                        }

                    }
                });


            }
        }
    }


    /**
     * This method is responsible for solving the rotation issue if exist. Also scale the images to
     * 1024x1024 resolution
     *
     * @param context       The current context
     * @param selectedImage The Image URI
     * @return Bitmap image results
     * @throws IOException
     */
    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage);
        return img;
    }


    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }


    /**
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }


    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

}