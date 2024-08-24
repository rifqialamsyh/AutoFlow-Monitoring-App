package com.example.autoflow.ui.notifications;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.autoflow.databinding.FragmentNotificationsBinding;
import com.example.autoflow.model.DataModel;
import com.example.autoflow.model.SharedViewModel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private static final int CREATE_FILE_REQUEST_CODE = 1;

    private FragmentNotificationsBinding binding;
    private SharedViewModel sharedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button exportButton = binding.exportButton;
        exportButton.setEnabled(false); // Disable the button initially

        sharedViewModel.getData().observe(getViewLifecycleOwner(), dataList -> {
            exportButton.setEnabled(!dataList.isEmpty()); // Enable the button only if there is data to export
        });

        exportButton.setOnClickListener(v -> {
            List<DataModel> dataList = sharedViewModel.getData().getValue();
            if (dataList != null && !dataList.isEmpty()) {
                createFile(dataList);
            } else {
                Toast.makeText(getContext(), "No data to export", Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    private void createFile(List<DataModel> dataList) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.putExtra(Intent.EXTRA_TITLE, "data.xlsx");
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                exportToExcel(sharedViewModel.getData().getValue(), uri);
            }
        }
    }

    private void exportToExcel(List<DataModel> dataList, Uri uri) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Data");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Soil Moisture");
        header.createCell(1).setCellValue("Temperature");
        header.createCell(2).setCellValue("Amount of Water");
        header.createCell(3).setCellValue("Date");

        for (int i = 0; i < dataList.size(); i++) {
            DataModel data = dataList.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(data.getSoilMoisture());
            row.createCell(1).setCellValue(data.getTemperature());
            row.createCell(2).setCellValue(data.getAmountOfWater());
            row.createCell(3).setCellValue(data.getDate());
        }

        try {
            OutputStream os = requireContext().getContentResolver().openOutputStream(uri);
            if (os != null) {
                workbook.write(os);
                workbook.close(); // Close the workbook
                os.close();
                Toast.makeText(getContext(), "Exported to " + uri.getPath(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to open OutputStream", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Export failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
