package com.example.autoflow.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.autoflow.R;
import com.example.autoflow.adapter.DataAdapter;
import com.example.autoflow.databinding.FragmentHomeBinding;
import com.example.autoflow.model.DataModel;
import com.example.autoflow.model.SharedViewModel;
import com.example.autoflow.network.ApiInterface;
import com.example.autoflow.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private DataAdapter adapter;
    private List<DataModel> dataList;
    private FragmentHomeBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedViewModel forViewModel;
    private static final String CHANNEL_ID = "WaterLevelChannel";
    private static final int NOTIFICATION_ID = 1;

    private static final String TAG = "HomeFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        forViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dataList = new ArrayList<>();
        adapter = new DataAdapter(getContext(), dataList);
        recyclerView.setAdapter(adapter); // Ensure adapter is set before fetching data

        // Check if the app has the notification permission
        if (!areNotificationsEnabled()) {
            // Request the notification permission
            requestNotificationPermission();
        }

        // Create a notification channel
        createNotificationChannel();

        fetchData();

        swipeRefreshLayout = binding.swipeRefresh;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });

        return root;
    }

    private void fetchData() {
//        PLEASE ADD YOUR URL HERE, CHANGE "URL" TO YOUR REAL URL
        ApiInterface apiInterface = RetrofitClient.getClient("URL").create(ApiInterface.class);
        Call<List<DataModel>> call = apiInterface.getData();
        call.enqueue(new Callback<List<DataModel>>() {
            @Override
            public void onResponse(Call<List<DataModel>> call, Response<List<DataModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dataList.clear();
                    dataList.addAll(response.body());
                    adapter.notifyDataSetChanged(); // Notify adapter about data changes
                    DataModel firstData = dataList.get(0);
                    if (firstData.getAmountOfWater() <= 0.5) {
                        showNotification();
                    }
                    forViewModel.setData(dataList);
                } else {
                    Log.e(TAG, "Response unsuccessful or body is null");
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<DataModel>> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private boolean areNotificationsEnabled() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        return notificationManager.areNotificationsEnabled();
    }

    private void requestNotificationPermission() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getContext().getPackageName());
        startActivity(intent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Water Level Channel";
            String description = "Channel for water level notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle("Water Level is Low!!!")
                .setContentText("The amount of water is near or has reached 0")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

        // Check if the POST_NOTIFICATIONS permission is granted
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // If permission is granted, show the notification
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            // If permission is not granted, handle it gracefully, for example by showing a message to the user
            Toast.makeText(getContext(), "Permission to post notifications is denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
