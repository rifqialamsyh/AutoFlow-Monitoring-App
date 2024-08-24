package com.example.autoflow.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.autoflow.model.SharedViewModel;
import com.example.autoflow.databinding.FragmentDashboardBinding;
import com.example.autoflow.model.DataModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private SharedViewModel viewModel;
    private LineChart lineChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        lineChart = binding.lineChart;

        viewModel.getData().observe(getViewLifecycleOwner(), this::updateGraph);

        return root;
    }

    private void updateGraph(List<DataModel> data) {
        List<Entry> entriesMoisture = new ArrayList<>();
        List<Entry> entriesTemperature = new ArrayList<>();
        List<Entry> entriesWaterAmount = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            entriesMoisture.add(new Entry(i, data.get(i).getSoilMoisture()));
            entriesTemperature.add(new Entry(i, data.get(i).getTemperature()));
            entriesWaterAmount.add(new Entry(i, data.get(i).getAmountOfWater()));
        }

        LineDataSet dataSetMoisture = new LineDataSet(entriesMoisture, "Soil Moisture");
        dataSetMoisture.setColor(Color.RED);
        dataSetMoisture.setLineWidth(3f);
        LineDataSet dataSetTemperature = new LineDataSet(entriesTemperature, "Temperature");
        dataSetTemperature.setColor(Color.GREEN);
        dataSetTemperature.setLineWidth(3f);
        LineDataSet dataSetWaterAmount = new LineDataSet(entriesWaterAmount, "Amount of Water");
        dataSetWaterAmount.setColor(Color.BLUE);
        dataSetWaterAmount.setLineWidth(3f);

        LineData lineData = new LineData();
        lineData.addDataSet(dataSetMoisture);
        lineData.addDataSet(dataSetTemperature);
        lineData.addDataSet(dataSetWaterAmount);

        lineChart.setData(lineData);
        lineChart.invalidate(); // refresh the chart
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}