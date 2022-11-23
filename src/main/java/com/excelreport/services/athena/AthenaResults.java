package com.excelreport.services.athena;

import com.inrix.fusion.volumeestimate.evaluation.model.exceptions.GeometryException;
import software.amazon.awssdk.services.athena.model.Datum;

import java.util.List;

public interface AthenaResults {
    void loadData(List<Datum> data) throws GeometryException;
    void loadArray(String[] data) throws GeometryException;
}

