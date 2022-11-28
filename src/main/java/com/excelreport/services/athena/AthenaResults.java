package com.excelreport.services.athena;

import software.amazon.awssdk.services.athena.model.Datum;

import java.util.List;

public interface AthenaResults {
    void loadData(List<Datum> data);
    void loadArray(String[] data);
}

