package com.excelreport.services.athena;

public interface AthenaAccess {
    String awsProfile();
    String athenaWorkgroup();
    String athenaDatabase();
    String outputBucket();
}
