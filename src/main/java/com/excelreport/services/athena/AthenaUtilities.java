package com.excelreport.services.athena;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AthenaUtilities {
    private static final String OUTPUT_BUCKET_FOLDER = "fusion";
    private static final long SLEEP_AMOUNT_IN_MS = 1000;

    public <T extends AthenaResults> List<T> getRecords(Class<T> clazz, AthenaAccess athenaAccess, String querystring)
            throws InterruptedException, AthenaProcessingException {
        ProfileCredentialsProvider profileCredentialsProvider = getProfileCredentialsProvider(athenaAccess);
        AthenaClient athenaClient = athenaCreateClient(profileCredentialsProvider);
        S3Client s3Client = s3CreateClient(profileCredentialsProvider);

        String executionId = submitAthenaQuery(athenaClient, athenaAccess, querystring);
        waitForQueryToComplete(athenaClient, executionId);

        return downloadResultRows(clazz, s3Client, athenaAccess, executionId);
    }

    private ProfileCredentialsProvider getProfileCredentialsProvider(AthenaAccess athenaAccess) {
        return ProfileCredentialsProvider.builder()
                .build();
    }

    private AthenaClient athenaCreateClient(ProfileCredentialsProvider profileCredentialsProvider) {
        return AthenaClient.builder()
                .region(Region.US_WEST_2)
                .build();
    }

    private S3Client s3CreateClient(ProfileCredentialsProvider profileCredentialsProvider) {
        return S3Client.builder()
                .region(Region.US_WEST_2)
                .build();
    }

    // Submits a sample query to Amazon Athena and returns the execution ID of the
    // query
    private String submitAthenaQuery(AthenaClient athenaClient, AthenaAccess athenaAccess, String querystring)
            throws AthenaProcessingException {
        try {
            // The QueryExecutionContext allows us to set the database
            QueryExecutionContext queryExecutionContext = QueryExecutionContext.builder()
                    .database(athenaAccess.athenaDatabase()).build();

            // The result configuration specifies where the results of the query should go
            String outputLocation = String.format("s3://%s/%s", athenaAccess.outputBucket(), OUTPUT_BUCKET_FOLDER);
            ResultConfiguration resultConfiguration = ResultConfiguration.builder()
                    .outputLocation(outputLocation)
                    .build();

            StartQueryExecutionRequest startQueryExecutionRequest = StartQueryExecutionRequest.builder()
                    .workGroup(athenaAccess.athenaWorkgroup())
                    .queryString(querystring)
                    .queryExecutionContext(queryExecutionContext)
                    .resultConfiguration(resultConfiguration)
                    .build();

            StartQueryExecutionResponse startQueryExecutionResponse = athenaClient
                    .startQueryExecution(startQueryExecutionRequest);

            return startQueryExecutionResponse.queryExecutionId();
        } catch (Exception e) {
            throw new AthenaProcessingException("Error in querying Athena", e);
        }
    }

    // Wait for an Amazon Athena query to complete, fail or to be cancelled
    private void waitForQueryToComplete(AthenaClient athenaClient, String queryExecutionId)
            throws InterruptedException {
        GetQueryExecutionRequest getQueryExecutionRequest = GetQueryExecutionRequest.builder()
                .queryExecutionId(queryExecutionId).build();

        GetQueryExecutionResponse getQueryExecutionResponse;
        boolean isQueryStillRunning = true;
        while (isQueryStillRunning) {
            getQueryExecutionResponse = athenaClient.getQueryExecution(getQueryExecutionRequest);
            QueryExecutionState queryState = getQueryExecutionResponse.queryExecution().status().state();
            if (queryState.equals(QueryExecutionState.FAILED)) {
                throw new RuntimeException(
                        "The Amazon Athena query failed to run with error message: " + getQueryExecutionResponse
                                .queryExecution().status().stateChangeReason());
            } else if (queryState.equals(QueryExecutionState.CANCELLED)) {
                throw new RuntimeException("The Amazon Athena query was cancelled.");
            } else if (queryState.equals(QueryExecutionState.SUCCEEDED)) {
                isQueryStillRunning = false;
            } else if (queryState.equals(QueryExecutionState.QUEUED)) {
                Thread.sleep(SLEEP_AMOUNT_IN_MS / 2);
            } else {
                // Sleep an amount of time before retrying again
                Thread.sleep(SLEEP_AMOUNT_IN_MS);
            }
        }
    }

    private <T extends AthenaResults> List<T> downloadResultRows(Class<T> clazz, S3Client s3Client,
            AthenaAccess athenaAccess, String executionId)
            throws AthenaProcessingException {
        String key = String.format("%s/%s.csv", OUTPUT_BUCKET_FOLDER, executionId);

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(athenaAccess.outputBucket())
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(request);
        byte[] data = objectBytes.asByteArray();

        String string = new String(data, StandardCharsets.UTF_8);
        String[] rows = string.split("\n");

        List<T> classValues = new ArrayList<>();
        boolean isFirstRowOfFullResults = true;
        for (String row : rows) {
            if (isFirstRowOfFullResults) {
                isFirstRowOfFullResults = false;
            } else {
                String[] results = row.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                T rowAttributes = null;
                try {
                    rowAttributes = clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                        | NoSuchMethodException e) {
                    throw new AthenaProcessingException("Error getting result value ", e);
                }
                rowAttributes.loadArray(results);
                classValues.add(rowAttributes);
            }
        }

        return classValues;
    }
}
