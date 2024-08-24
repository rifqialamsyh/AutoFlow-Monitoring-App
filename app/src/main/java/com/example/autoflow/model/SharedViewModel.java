package com.example.autoflow.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<DataModel>> data = new MutableLiveData<>();

    public void setData(List<DataModel> data) {
        this.data.setValue(data);
    }

    public LiveData<List<DataModel>> getData() {
        return data;
    }
}
