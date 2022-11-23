package com.excelreport;

import java.time.LocalDateTime;

public class Overall {
    private LocalDateTime date;
    private String region;
    private String mapversion;
    private String year;
    private String periodicity;
    private String period;
    private int count;
    private double mape;
    private double smape;
    private double rmse;

    public Overall(
        LocalDateTime date,
        String region, 
        String mapversion, 
        String year, 
        String periodicity, 
        String period, 
        int count,
        double mape, 
        double smape, 
        double rmse) {
        this.date = date;
        this.region = region;
        this.mapversion = mapversion;
        this.year = year;
        this.periodicity = periodicity;
        this.period = period;
        this.count = count;
        this.mape = mape;
        this.smape = smape;
        this.rmse = rmse;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getRegion() {
        return region;
    }

    public String getMapversion() {
        return mapversion;
    }

    public String getYear() {
        return year;
    }

    public String getPeriodicity() {
        return periodicity;
    }

    public String getPeriod() {
        return period;
    }

    public int getCount() {
        return count;
    }

    public double getMape() {
        return mape;
    }

    public double getSmape() {
        return smape;
    }

    public double getRmse() {
        return rmse;
    }
}
