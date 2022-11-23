package com.excelreport.services.athena;

public class AthenaDao implements Runnable {
    private final AthenaUtilities athenaUtilities;

    private static String VOLUME_ESTIMATES_DB = "volumes_prod";
    private static String VOLUME_ESTIMATES_TABLE = "volume_estimates_all_periods_v2";

    @Override
    public void run() {
        
    }
    
}
