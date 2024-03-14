package tech.mobile.met.ui.dashboard;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.Pie;
import com.anychart.anychart.ValueDataEntry;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import tech.mobile.met.LineChartXAxisValueFormatter;
import tech.mobile.met.R;
import tech.mobile.met.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        PieChart pieChartDevicesVsMeters = (PieChart) binding.pieChartDevicesVMeters;
        BarChart barChartEMeter = (BarChart) binding.barChartEMeters;
        BarChart barChartGMeter = (BarChart) binding.barChartGMeters;
        BarChart barChartWMeter = (BarChart) binding.barChartWMeters;
        TextView textViewPieChart = binding.textViewDevicesVMeters;
        TextView textViewEBarChart = binding.textViewEmeters;
        TextView textViewGBarChart = binding.textViewGmeters;
        TextView textViewWBarChart = binding.textViewWmeters;

        /*ArrayList<PieEntry> data = new ArrayList<>();
        data.add(new PieEntry( 10000, "John"));
        data.add(new PieEntry( 12000, "Jake"));
        data.add(new PieEntry( 18000, "Peter"));

        PieDataSet pieDataSet = new PieDataSet(data, "My label");
        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(20f);

        PieData pieChartData = new PieData(pieDataSet);*/
        if(dashboardViewModel.CheckIfDevicesMetersExist()){
            textViewPieChart.setText(getText(R.string.pie_header));
            textViewEBarChart.setText(getText(R.string.electric_meter));
            textViewGBarChart.setText(getText(R.string.gas_meter));
            textViewWBarChart.setText(getText(R.string.water_meter));

            //Pie chart
            pieChartDevicesVsMeters.setData(dashboardViewModel.GetPieChartData());
            pieChartDevicesVsMeters.getDescription().setEnabled(false);
            pieChartDevicesVsMeters.setEntryLabelTextSize(16f);
            pieChartDevicesVsMeters.setTouchEnabled(true);
            pieChartDevicesVsMeters.getLegend().setTextSize(14f);
            pieChartDevicesVsMeters.setCenterText("Device vs Total consumption");
            pieChartDevicesVsMeters.setCenterTextSize(14f);
            pieChartDevicesVsMeters.animate();


            //Bar chart elec
            barChartEMeter.setFitBars(true);
            barChartEMeter.setData(dashboardViewModel.GetBarChartElecMeter());
            //barChartEMeter.getDescription().setText("Electric meter bar chart");
            barChartEMeter.getLegend().setTextSize(16f);
            barChartEMeter.animateY(2000);


            if(dashboardViewModel.CheckIfGasMeterRecordsExist()){
                //Bar chart gas
                barChartGMeter.setFitBars(true);
                //barChartGMeter.getXAxis().setValueFormatter(new LineChartXAxisValueFormatter());
                barChartGMeter.setData(dashboardViewModel.GetBarChartEGasMeter());
                //barChartGMeter.getDescription().setText("Gas meter bar chart");
                barChartGMeter.getLegend().setTextSize(16f);
                barChartGMeter.animateY(2000);
            }

            if(dashboardViewModel.CheckIfWaterMeterRecordsExist()){
                //Bar chart water
                barChartWMeter.setFitBars(true);
                barChartWMeter.setData(dashboardViewModel.GetBarChartEWaterMeter());
                //barChartWMeter.getDescription().setText("Water meter bar chart");
                barChartWMeter.getLegend().setTextSize(16f);
                barChartWMeter.animateY(2000);
            }


        }

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        // TODO: Use the ViewModel
    }

}