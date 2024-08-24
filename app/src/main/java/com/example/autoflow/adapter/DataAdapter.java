package com.example.autoflow.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoflow.R;
import com.example.autoflow.model.DataModel;
import com.example.autoflow.network.ApiInterface;
import com.example.autoflow.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private List<DataModel> dataList;
    private Context context;

    public DataAdapter(Context context, List<DataModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel data = dataList.get(position);
        String moisture = data.getSoilMoisture() + " %";
        holder.tvMoisture.setText(moisture);

        String temperature = data.getTemperature() + " C";
        holder.tvTemperature.setText(temperature);

        String waterAmount = data.getAmountOfWater() + " Litres";
        holder.tvWaterAmount.setText(waterAmount);

        String date = data.getDate() + "+7";
        holder.tvDate.setText(date);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(data.getId(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void showDeleteDialog(final String id, final int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Data")
                .setMessage("Are you sure you want to delete this data?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteData(id, position);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteData(String id, int position) {
        ApiInterface apiInterface = RetrofitClient.getClient("url").create(ApiInterface.class);
        Call<Void> call = apiInterface.deleteData(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Data deleted successfully", Toast.LENGTH_SHORT).show();
                    dataList.remove(position);
                    notifyItemRemoved(position);
                } else {
                    Toast.makeText(context, "Failed to delete data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Failed to delete data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMoisture, tvTemperature, tvWaterAmount, tvDate;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMoisture = itemView.findViewById(R.id.tv_moisture);
            tvTemperature = itemView.findViewById(R.id.tv_temperature);
            tvWaterAmount = itemView.findViewById(R.id.tv_water_amount);
            tvDate = itemView.findViewById(R.id.tv_date);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
