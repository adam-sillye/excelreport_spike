package com.excelreport.services.athena;

import java.util.List;

import com.excelreport.Overall;

public class AthenaDao implements Runnable {
    private final AthenaUtilities athenaUtilities;

    private static String VOLUME_ESTIMATES_DB = "visualization_tool";
    private static String VOLUME_ESTIMATES_TABLE = "validation";

    private static final String FUSION_ATHENA_OUTPUT_BUCKET = "s3://aws-athena-query-results-428731972867-us-west-2/";

    public AthenaDao(AthenaUtilities athenaUtilities){
        this.athenaUtilities = athenaUtilities;
    }

    public AthenaDao(
            AthenaUtilities athenaUtilities,
            String volumeEstimatesDb,
            String volumeEstimatesTable){
        this.athenaUtilities = athenaUtilities;
        VOLUME_ESTIMATES_DB = volumeEstimatesDb;
        VOLUME_ESTIMATES_TABLE = volumeEstimatesTable;
    }

    public List<Overall> getOverall(String osmMapVersion, String region, String country, String state)
        throws InterruptedException {
    AthenaAccess athenaAccess =
            ImmutableAthenaAccess.builder()
                    .awsProfile(FUSION_ATHENA_PROFILE)
                    .athenaWorkgroup(FUSION_ATHENA_WORKGROUP)
                    .athenaDatabase(FUSION_ANCILLARY_DB)
                    .outputBucket(FUSION_ATHENA_OUTPUT_BUCKET)
                    .build();

    QueryConstructor sampler = new QueryConstructor(HIVE_QUERIES_FOLDER);
    String querystring = sampler.buildOsmSegmentMetricsQueryString(osmMapVersion, region, country, state);

    return athenaUtilities.getRecords(Overall.class, athenaAccess, querystring);
    }
}
