package tech.mobile.met.ui.dashboard;

import android.graphics.Color;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;
import tech.mobile.met.models.realmentity.ClientModelDevice;
import tech.mobile.met.models.realmentity.ElectricDevice;
import tech.mobile.met.models.realmentity.ElectricMeter;
import tech.mobile.met.models.realmentity.ElectricMeter_records;
import tech.mobile.met.models.realmentity.GasMeter;
import tech.mobile.met.models.realmentity.GasMeter_records;
import tech.mobile.met.models.realmentity.WaterMeter;
import tech.mobile.met.models.realmentity.WaterMeter_records;

public class DashboardViewModel extends ViewModel {
    //Fields
    //Realm Auth
    ClientModelDevice myModel;
    String appID = "app-energy-trends-iiaxj";
    private final App app = new App(new AppConfiguration.Builder(appID).appName("M.E. Trends")
            .requestTimeout(30, TimeUnit.SECONDS).build());
    User user = app.currentUser();
    private Realm backgroundThreadRealm;

    public DashboardViewModel(){
        myModel = new ClientModelDevice();

    }

    //Methods

    // Get all devices and add to model
    public RealmResults<ElectricDevice> GetAllDevices(){
        backgroundThreadRealm = Realm.getDefaultInstance();

        return backgroundThreadRealm.where(ElectricDevice.class).findAll();
    }


    //Get all gas meter records
    public RealmList<GasMeter_records> GetAllGasMeterRecords(){
        RealmList<GasMeter_records> records = new RealmList<GasMeter_records>();
        backgroundThreadRealm = Realm.getDefaultInstance();

        GasMeter selectedGasMeter = backgroundThreadRealm.where(GasMeter.class).findFirst();

        if(selectedGasMeter != null){
            records = selectedGasMeter.getRecords();
        }
        return records;
    }

    //Get all water meter records
    public RealmList<WaterMeter_records> GetAllWaterMeterRecords(){
        RealmList<WaterMeter_records> records = new RealmList<WaterMeter_records>();
        backgroundThreadRealm = Realm.getDefaultInstance();

        WaterMeter selectedWaterMeter = backgroundThreadRealm.where(WaterMeter.class).findFirst();

        if(selectedWaterMeter != null){
            records = selectedWaterMeter.getRecords();
        }
        return records;
    }

    //Get all electric meter records
    public RealmList<ElectricMeter_records> GetAllElectricMeterRecords(){
        RealmList<ElectricMeter_records> records = new RealmList<ElectricMeter_records>();
        backgroundThreadRealm = Realm.getDefaultInstance();

        ElectricMeter selectedElectricMeter = backgroundThreadRealm.where(ElectricMeter.class).findFirst();

        if(selectedElectricMeter != null){
            records = selectedElectricMeter.getMy_meter_records();
        }
        return records;
    }

    public int SizeOf(RealmResults<ElectricDevice> results){
        return results.size();
    }

    //Compute device consumption
    public int ComputeDeviceConsumption(){
        int result = 0;
        List<ElectricDevice> devices = GetAllDevices().subList(0, SizeOf(GetAllDevices()));

        for(ElectricDevice device : devices){
            result = result + (device.getHours_used_per_day() * device.getPower_rating() * device.getNumber_of_devices());

        }
        //Convert to KWh
        result /= 1_000;
        return result;
    }

    //get latest e-consumption to compare to device consumption
    public Long GetLastConsumptionReading(){
        backgroundThreadRealm = Realm.getDefaultInstance();
        if(!backgroundThreadRealm.where(ElectricMeter.class).findFirst().getMy_meter_records().isEmpty()){
            ElectricMeter_records lastRecordAdded = backgroundThreadRealm.where(ElectricMeter.class).findFirst().getMy_meter_records().last();
            return lastRecordAdded.getConsumption();
        }
        return null;
    }

    // Create pie data set
    public PieData GetPieChartData(){
        ArrayList<PieEntry> data = new ArrayList<>();

        data.add(new PieEntry( ComputeDeviceConsumption(), "Devices(KWh)"));
        data.add(new PieEntry( GetLastConsumptionReading(), "Total (KWh)"));

        PieDataSet pieDataSet = new PieDataSet(data, "");

        PieData pieChartData = new PieData(pieDataSet);

        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(20f);


        return pieChartData;
    }

    //Check if devices exist
    public boolean CheckIfDevicesMetersExist(){
        if(GetAllDevices().size() > 0){
            if(!(GetAllElectricMeterRecords().isEmpty())){
                return true;
            }
        }
        return false;
    }

    public boolean CheckIfGasMeterRecordsExist(){
        if(!(GetAllGasMeterRecords().isEmpty())){
            return true;
        }
        return false;
    }

    public boolean CheckIfWaterMeterRecordsExist(){
        if(!(GetAllWaterMeterRecords().isEmpty())){
            return true;
        }
        return false;
    }

    //Check if any records exist in meters

    //get barchart data set for electric meter
    public BarData GetBarChartElecMeter(){
        ArrayList<BarEntry> data = new ArrayList<>();

        float i = 0f;
        for(ElectricMeter_records rec : GetAllElectricMeterRecords()){
            i = i + 1f;
            data.add(new BarEntry( i, rec.getConsumption()));
        }

        BarDataSet barDataSet = new BarDataSet(data, "Consumption (KWh)");
        barDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        return barData;
    }

    //get barchart data set for gas meter
    public BarData GetBarChartEGasMeter(){

        ArrayList<BarEntry> data = new ArrayList<>();

        float i = 0f;
        for(GasMeter_records rec : GetAllGasMeterRecords()){
            i = i + 1f;
            data.add(new BarEntry( i, rec.getConsumption()));
            //Log.v("DATEEPOCH", "Date:" + rec.getRecordDate().getTime());
            //Log.v("DATEEPOCH", "Date:" + rec.getRecordDate().getTime()/1000f);
        }

        BarDataSet barDataSet = new BarDataSet(data, "Consumption (cubic meter)");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        return barData;

    }

    //get barchart data set for water meter
    public BarData GetBarChartEWaterMeter(){

        ArrayList<BarEntry> data = new ArrayList<>();

        float i = 0f;
        for(WaterMeter_records rec : GetAllWaterMeterRecords()){
            //data.add(new BarEntry( rec.getRecordDate().getDate(), rec.getConsumption()));
            i = i + 1f;
            data.add(new BarEntry( i, rec.getConsumption()));
        }

        BarDataSet barDataSet = new BarDataSet(data, "Consumption (cubic meter)");
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        return barData;

    }




}