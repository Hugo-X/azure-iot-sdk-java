/*
*  Copyright (c) Microsoft. All rights reserved.
*  Licensed under the MIT license. See LICENSE file in the project root for full license information.
*/

package tests.unit.com.microsoft.azure.sdk.iot.service.devicetwin;

import com.microsoft.azure.sdk.iot.deps.serializer.ParserUtility;
import com.microsoft.azure.sdk.iot.deps.serializer.QueryRequestParser;
import com.microsoft.azure.sdk.iot.service.IotHubConnectionString;
import com.microsoft.azure.sdk.iot.service.devicetwin.*;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import com.microsoft.azure.sdk.iot.service.transport.http.HttpMethod;
import com.microsoft.azure.sdk.iot.service.transport.http.HttpResponse;
import mockit.Deencapsulation;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

/**
 * Unit tests for QueryCollection.java
 * Methods: 100%
 * Lines: 100%
 */
public class QueryCollectionTest
{
    @Mocked
    ParserUtility mockParserUtility;

    @Mocked
    QueryRequestParser mockQueryRequestParser;

    @Mocked
    IotHubConnectionString iotHubConnectionString;

    @Mocked
    HttpResponse mockHttpResponse;

    @Mocked
    QueryCollectionResponse mockQueryCollectionResponse;

    @Mocked
    QueryOptions mockQueryOptions;

    @Mocked
    IotHubConnectionString mockConnectionString;

    @Mocked
    URL mockUrl;

    @Mocked
    HttpMethod mockHttpMethod;

    @Mocked
    DeviceOperations mockDeviceOperations;

    private static final String expectedRequestContinuationToken = "someContinuationToken";
    private static final String expectedResponseContinuationToken = "someNewContinuationToken";

    private static final long connectTimeout = 60000;
    private static final long expectedTimeout = 10000;
    private static HashMap<String, String> expectedValidRequestHeaders;
    private static HashMap<String, String> expectedValidResponseHeaders;
    private static HashMap<String, String> expectedResponseHeadersMismatchedType;
    private static HashMap<String, String> expectedResponseHeadersUnknownQueryType;

    private static final int expectedPageSize = 22;

    @BeforeClass
    public static void initializeExpectedValues()
    {
        expectedValidRequestHeaders = new HashMap<>();
        expectedValidRequestHeaders.put("x-ms-max-item-count", String.valueOf(expectedPageSize));
        expectedValidRequestHeaders.put("x-ms-continuation", expectedRequestContinuationToken);

        expectedValidResponseHeaders = new HashMap<>();
        expectedValidResponseHeaders.put("x-ms-continuation", expectedResponseContinuationToken);
        expectedValidResponseHeaders.put("x-ms-item-type", "raw");

        expectedResponseHeadersMismatchedType = new HashMap<>();
        expectedResponseHeadersMismatchedType.put("x-ms-continuation", expectedResponseContinuationToken);
        expectedResponseHeadersMismatchedType.put("x-ms-item-type", "twin");

        expectedResponseHeadersUnknownQueryType = new HashMap<>();
        expectedResponseHeadersUnknownQueryType.put("x-ms-continuation", expectedResponseContinuationToken);
        expectedResponseHeadersUnknownQueryType.put("x-ms-item-type", "Unknown");

    }

    //Tests_SRS_QUERYCOLLECTION_34_001: [If the provided query string is invalid or does not contain both SELECT and FROM, an IllegalArgumentException shall be thrown.]
    @Test (expected = IllegalArgumentException.class)
    public void constructorThrowsForInvalidSqlQueryString()
    {
        //arrange
        new NonStrictExpectations()
        {
            {
                mockParserUtility.validateQuery((String) any);
                result = new IllegalArgumentException();
            }
        };

        //act
        Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class},"anyString", 20, QueryType.DEVICE_JOB, mockConnectionString, mockUrl, mockHttpMethod, expectedTimeout);
    }
    
    //Tests_SRS_QUERYCOLLECTION_34_002: [If the provided page size is not a positive integer, an IllegalArgumentException shall be thrown.]
    @Test (expected = IllegalArgumentException.class)
    public void constructorThrowsForNegativePageSize()
    {
        //act
        Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class},"anyString", -1, QueryType.DEVICE_JOB, mockConnectionString, mockUrl, mockHttpMethod, expectedTimeout);
    }

    //Tests_SRS_QUERYCOLLECTION_34_003: [If the provided page size is not a positive integer, an IllegalArgumentException shall be thrown.]
    @Test (expected = IllegalArgumentException.class)
    public void constructorThrowsForZeroPageSize()
    {
        //act
        Deencapsulation.newInstance(QueryCollection.class, new Class[] {int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class},0, QueryType.DEVICE_JOB, mockConnectionString, mockUrl, mockHttpMethod, expectedTimeout);
    }

    //Tests_SRS_QUERYCOLLECTION_34_004: [If the provided QueryType is null or UNKNOWN, an IllegalArgumentException shall be thrown.]
    @Test (expected = IllegalArgumentException.class)
    public void constructorThrowsForNullQueryType()
    {
        //act
        Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class},"anyString", 20, null, mockConnectionString, mockUrl, mockHttpMethod, expectedTimeout);
    }

    //Tests_SRS_QUERYCOLLECTION_34_005: [If the provided QueryType is null or UNKNOWN, an IllegalArgumentException shall be thrown.]
    @Test (expected = IllegalArgumentException.class)
    public void constructorThrowsForUnknownQueryType()
    {
        //act
        Deencapsulation.newInstance(QueryCollection.class, new Class[] {int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class},20, QueryType.UNKNOWN, mockConnectionString, mockUrl, mockHttpMethod, expectedTimeout);
    }

    //Tests_SRS_QUERYCOLLECTION_34_038: [If the provided connection string, url, or http method is null, this function shall throw an IllegalArgumentException.]
    @Test (expected = IllegalArgumentException.class)
    public void constructorWithQueryThrowsForNullConnectionString()
    {
        //act
        Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class}, "any query",20, QueryType.JOB_RESPONSE, null, mockUrl, mockHttpMethod, expectedTimeout);
    }

    //Tests_SRS_QUERYCOLLECTION_34_038: [If the provided connection string, url, or http method is null, this function shall throw an IllegalArgumentException.]
    @Test (expected = IllegalArgumentException.class)
    public void constructorWithQueryThrowsForNullUrl()
    {
        //act
        Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class},"any query", 20, QueryType.JOB_RESPONSE, mockConnectionString, null, mockHttpMethod, expectedTimeout);
    }

    //Tests_SRS_QUERYCOLLECTION_34_038: [If the provided connection string, url, or http method is null, this function shall throw an IllegalArgumentException.]
    @Test (expected = IllegalArgumentException.class)
    public void constructorWithQueryThrowsForNullHttpMethod()
    {
        //act
        Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class},"any query", 20, QueryType.JOB_RESPONSE, mockConnectionString, mockUrl, null, expectedTimeout);
    }

    //Tests_SRS_QUERYCOLLECTION_34_038: [If the provided connection string, url, or http method is null, this function shall throw an IllegalArgumentException.]
    @Test (expected = IllegalArgumentException.class)
    public void constructorThrowsForNullConnectionString()
    {
        //act
        Deencapsulation.newInstance(QueryCollection.class, new Class[] {int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class},20, QueryType.JOB_RESPONSE, null, mockUrl, mockHttpMethod, expectedTimeout);
    }

    //Tests_SRS_QUERYCOLLECTION_34_038: [If the provided connection string, url, or http method is null, this function shall throw an IllegalArgumentException.]
    @Test (expected = IllegalArgumentException.class)
    public void constructorThrowsForNullUrl()
    {
        //act
        Deencapsulation.newInstance(QueryCollection.class, new Class[] {int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class},20, QueryType.JOB_RESPONSE, mockConnectionString, null, mockHttpMethod, expectedTimeout);
    }

    //Tests_SRS_QUERYCOLLECTION_34_038: [If the provided connection string, url, or http method is null, this function shall throw an IllegalArgumentException.]
    @Test (expected = IllegalArgumentException.class)
    public void constructorThrowsForNullHttpMethod()
    {
        //act
        Deencapsulation.newInstance(QueryCollection.class, new Class[] {int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class},20, QueryType.JOB_RESPONSE, mockConnectionString, mockUrl, null, expectedTimeout);
    }

    //Tests_SRS_QUERYCOLLECTION_34_006: [This function shall save the provided query, pageSize, requestQueryType, iotHubConnectionString, url, httpMethod and timeout.]
    //Tests_SRS_QUERYCOLLECTION_34_008: [The constructed QueryCollection shall be a sql query type.]
    @Test
    public void constructorSavesQueryPageSizeAndQueryType()
    {
        //arrange
        String expectedQuery = "someQuery";
        int expectedPageSize = 22;
        QueryType expectedQueryType = QueryType.JOB_RESPONSE;

        //act
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, long.class}, expectedQuery, expectedPageSize, expectedQueryType, mockConnectionString, mockUrl, mockHttpMethod, connectTimeout, expectedTimeout);

        //assert
        String actualQuery = Deencapsulation.getField(queryCollection, "query");
        int actualPageSize = Deencapsulation.getField(queryCollection, "pageSize");
        QueryType actualQueryType = Deencapsulation.getField(queryCollection, "requestQueryType");
        boolean actualIsSqlQuery = Deencapsulation.getField(queryCollection, "isSqlQuery");
        IotHubConnectionString actualConnectionString = Deencapsulation.getField(queryCollection, "iotHubConnectionString");
        HttpMethod actualHttpMethod = Deencapsulation.getField(queryCollection, "httpMethod");
        URL actualUrl = Deencapsulation.getField(queryCollection, "url");
        long connectTimeoutInMs = Deencapsulation.getField(queryCollection, "connectTimeoutInMs");
        long responseTimeoutInMs = Deencapsulation.getField(queryCollection, "responseTimeoutInMs");

        assertEquals(expectedQuery, actualQuery);
        assertEquals(expectedPageSize, actualPageSize);
        assertEquals(expectedQueryType, actualQueryType);
        assertTrue(actualIsSqlQuery);
        assertEquals(actualConnectionString, mockConnectionString);
        assertEquals(actualHttpMethod, mockHttpMethod);
        assertEquals(connectTimeoutInMs, connectTimeout);
        assertEquals(responseTimeoutInMs, expectedTimeout);
        assertEquals(actualUrl, mockUrl);
    }

        //this.iotHubConnectionString = iotHubConnectionString;
        //this.httpMethod = httpMethod;
        //this.timeout = timeout;
        //this.url = url;

    //Tests_SRS_QUERYCOLLECTION_34_007: [This function shall save the provided pageSize, requestQueryType, iotHubConnectionString, url, httpMethod and timeout.]
    //Tests_SRS_QUERYCOLLECTION_34_009: [The constructed QueryCollection shall not be a sql query type.]
    @Test
    public void constructorSavesPageSizeAndQueryType()
    {
        //arrange
        int expectedPageSize = 22;
        QueryType expectedQueryType = QueryType.JOB_RESPONSE;

        //act
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, long.class}, expectedPageSize, expectedQueryType, mockConnectionString, mockUrl, mockHttpMethod, connectTimeout, expectedTimeout);

        //assert
        int actualPageSize = Deencapsulation.getField(queryCollection, "pageSize");
        QueryType actualQueryType = Deencapsulation.getField(queryCollection, "requestQueryType");
        boolean actualIsSqlQuery = Deencapsulation.getField(queryCollection, "isSqlQuery");
        IotHubConnectionString actualConnectionString = Deencapsulation.getField(queryCollection, "iotHubConnectionString");
        HttpMethod actualHttpMethod = Deencapsulation.getField(queryCollection, "httpMethod");
        URL actualUrl = Deencapsulation.getField(queryCollection, "url");
        long connectTimeoutInMs = Deencapsulation.getField(queryCollection, "connectTimeoutInMs");
        long responseTimeoutInMs = Deencapsulation.getField(queryCollection, "responseTimeoutInMs");

        assertEquals(expectedPageSize, actualPageSize);
        assertEquals(expectedQueryType, actualQueryType);
        assertFalse(actualIsSqlQuery);
        assertEquals(actualConnectionString, mockConnectionString);
        assertEquals(actualHttpMethod, mockHttpMethod);
        assertEquals(connectTimeoutInMs, connectTimeout);
        assertEquals(responseTimeoutInMs, expectedTimeout);
        assertEquals(actualUrl, mockUrl);
    }

    //Tests_SRS_QUERYCOLLECTION_34_010: [If the provided connection string, url, or method is null, an IllegalArgumentException shall be thrown.]
    @Test (expected = IllegalArgumentException.class)
    public void sendQueryRequestThrowsForNullConnectionString()
    {
        //arrange
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {int.class, QueryType.class}, 22, QueryType.JOB_RESPONSE);

        //act
        Deencapsulation.invoke(queryCollection, "sendQueryRequest", new Class[] {IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, QueryOptions.class}, null, mockUrl, mockHttpMethod, expectedTimeout, mockQueryOptions);
    }

    //Tests_SRS_QUERYCOLLECTION_34_010: [If the provided connection string, url, or method is null, an IllegalArgumentException shall be thrown.]
    @Test (expected = IllegalArgumentException.class)
    public void sendQueryRequestThrowsForNullURL()
    {
        //arrange
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {int.class, QueryType.class}, 22, QueryType.JOB_RESPONSE);

        //act
        Deencapsulation.invoke(queryCollection, "sendQueryRequest", new Class[] {IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, QueryOptions.class}, mockConnectionString, null, mockHttpMethod, expectedTimeout, mockQueryOptions);
    }

    //Tests_SRS_QUERYCOLLECTION_34_010: [If the provided connection string, url, or method is null, an IllegalArgumentException shall be thrown.]
    @Test (expected = IllegalArgumentException.class)
    public void sendQueryRequestThrowsForNullMethod()
    {
        //arrange
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {int.class, QueryType.class}, expectedPageSize, QueryType.JOB_RESPONSE);

        //act
        Deencapsulation.invoke(queryCollection, "sendQueryRequest", new Class[] {IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, QueryOptions.class}, mockConnectionString, mockUrl, null, expectedTimeout, mockQueryOptions);
    }

    //Tests_SRS_QUERYCOLLECTION_34_011: [If the provided query options is not null and contains a continuation token, it shall be put in the query headers to continue the query.]
    //Tests_SRS_QUERYCOLLECTION_34_013: [If the provided query options is not null, the query option's page size shall be included in the query headers.]
    //Tests_SRS_QUERYCOLLECTION_34_017: [This function shall send an HTTPS request using DeviceOperations.]
    //Tests_SRS_QUERYCOLLECTION_34_016: [If this is not a sql query, the payload of the query message shall be set to empty bytes.]
    @Test
    public void sendQueryRequestPrioritizesOptionsContinuationToken() throws IOException, IotHubException
    {
        //arrange
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, long.class}, expectedPageSize, QueryType.RAW, mockConnectionString, mockUrl, mockHttpMethod, connectTimeout, expectedTimeout);
        Deencapsulation.setField(queryCollection, "isSqlQuery", false);

        new NonStrictExpectations()
        {
            {
                mockQueryOptions.getContinuationToken();
                result = expectedRequestContinuationToken;

                mockQueryOptions.getPageSize();
                result = expectedPageSize;

                DeviceOperations.request(mockConnectionString, mockUrl, mockHttpMethod, (byte[]) any, anyString, connectTimeout ,expectedTimeout);
                result = mockHttpResponse;

                mockHttpResponse.getHeaderFields();
                result = expectedValidResponseHeaders;
            }
        };

        //act
        Deencapsulation.invoke(queryCollection, "sendQueryRequest", new Class[] {QueryOptions.class}, mockQueryOptions);

        //assert
        new Verifications()
        {
            {
                DeviceOperations.setHeaders(expectedValidRequestHeaders);
                times = 1;

                DeviceOperations.request(mockConnectionString, mockUrl, mockHttpMethod, new byte[0], anyString, connectTimeout,expectedTimeout);
                times = 1;
            }
        };
    }

    //Tests_SRS_QUERYCOLLECTION_34_018: [The method shall read the continuation token (x-ms-continuation) and response type (x-ms-item-type) from the HTTP Headers and save it.]
    //Tests_SRS_QUERYCOLLECTION_34_019: [If the response type is Unknown or not found then this method shall throw IOException.]
    @Test (expected = IOException.class)
    public void sendQueryRequestThrowsForUnknownResponseQueryType() throws IOException, IotHubException
    {
        //arrange
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, long.class}, expectedPageSize, QueryType.RAW, mockConnectionString, mockUrl, mockHttpMethod, connectTimeout, expectedTimeout);

        new NonStrictExpectations()
        {
            {
                mockQueryOptions.getContinuationToken();
                result = expectedRequestContinuationToken;

                mockQueryOptions.getPageSize();
                result = expectedPageSize;

                DeviceOperations.request(mockConnectionString, mockUrl, mockHttpMethod, (byte[]) any, anyString, connectTimeout, expectedTimeout);
                result = mockHttpResponse;

                mockHttpResponse.getHeaderFields();
                result = expectedResponseHeadersUnknownQueryType;
            }
        };

        //act
        Deencapsulation.invoke(queryCollection, "sendQueryRequest", new Class[] {QueryOptions.class}, mockQueryOptions);
    }

    //Tests_SRS_QUERYCOLLECTION_34_012: [If a continuation token is not provided from the passed in query options, but there is a continuation token saved in the latest queryCollectionResponse, that token shall be put in the query headers to continue the query.]
    //Tests_SRS_QUERYCOLLECTION_34_014: [If the provided query options is null, this object's page size shall be included in the query headers.]
    @Test
    public void sendQueryRequestGetsContinuationTokenFromCurrentIfOptionsHasNone() throws IOException, IotHubException
    {
        //arrange
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, long.class}, expectedPageSize, QueryType.RAW, mockConnectionString, mockUrl, mockHttpMethod, connectTimeout, expectedTimeout);
        Deencapsulation.setField(queryCollection, "isSqlQuery", false);
        Deencapsulation.setField(queryCollection, "responseContinuationToken", expectedRequestContinuationToken);

        new NonStrictExpectations()
        {
            {
                DeviceOperations.request(mockConnectionString, mockUrl, mockHttpMethod, (byte[]) any, anyString, connectTimeout, expectedTimeout);
                result = mockHttpResponse;

                mockHttpResponse.getHeaderFields();
                result = expectedValidResponseHeaders;
            }
        };

        QueryOptions options = null;

        //act
        Deencapsulation.invoke(queryCollection, "sendQueryRequest", new Class[] {QueryOptions.class}, options);

        //assert
        new Verifications()
        {
            {
                DeviceOperations.setHeaders(expectedValidRequestHeaders);
                times = 1;

                DeviceOperations.request(mockConnectionString, mockUrl, mockHttpMethod, new byte[0], anyString, connectTimeout, expectedTimeout);
                times = 1;
            }
        };
    }

    //Tests_SRS_QUERYCOLLECTION_34_015: [If this is a sql query, the payload of the query message shall be set to the json bytes representation of this object's query string.]
    @Test
    public void sendQueryRequestUsesQueryStringJsonBytesAsPayload() throws IOException, IotHubException
    {
        //arrange
        String expectedQueryString = "some query";
        String expectedQueryStringJson = "some query json";
        byte[] expectedQueryStringBytes = expectedQueryStringJson.getBytes();
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, long.class}, expectedQueryString, expectedPageSize, QueryType.RAW, mockConnectionString, mockUrl, mockHttpMethod, connectTimeout, expectedTimeout);

        new NonStrictExpectations()
        {
            {
                mockQueryOptions.getContinuationToken();
                result = expectedRequestContinuationToken;

                Deencapsulation.newInstance(QueryRequestParser.class, new Class[] {String.class}, expectedQueryString);
                result = mockQueryRequestParser;

                mockQueryRequestParser.toJson();
                result = expectedQueryStringJson;

                expectedQueryStringJson.getBytes();
                result = expectedQueryStringBytes;

                DeviceOperations.request((IotHubConnectionString) any, (URL) any, (HttpMethod) any, expectedQueryStringBytes, null, connectTimeout, expectedTimeout);
                result = mockHttpResponse;

                mockHttpResponse.getHeaderFields();
                result = expectedValidResponseHeaders;
            }
        };

        //act
        Deencapsulation.invoke(queryCollection, "sendQueryRequest", new Class[] {QueryOptions.class}, mockQueryOptions);

        //assert
        new Verifications()
        {
            {
                DeviceOperations.request((IotHubConnectionString) any, (URL) any, (HttpMethod) any, expectedQueryStringBytes, null, connectTimeout, expectedTimeout);
                times = 1;
            }
        };
    }

    //Tests_SRS_QUERYCOLLECTION_34_018: [The method shall read the continuation token (x-ms-continuation) and response type (x-ms-item-type) from the HTTP Headers and save it.]
    //Tests_SRS_QUERYCOLLECTION_34_021: [The method shall create a QueryResponse object with the contents from the response body and its continuation token and save it.]
    @Test
    public void sendQueryRequestSavesContinuationTokenAndResponseType() throws IOException, IotHubException
    {
        //arrange
        String expectedQueryString = "some query";
        String expectedQueryStringJson = "some query json";
        byte[] expectedQueryStringBytes = expectedQueryStringJson.getBytes();
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, long.class}, expectedQueryString, expectedPageSize, QueryType.RAW, mockConnectionString, mockUrl, mockHttpMethod, connectTimeout, expectedTimeout);

        new NonStrictExpectations()
        {
            {
                mockQueryOptions.getContinuationToken();
                result = expectedRequestContinuationToken;

                Deencapsulation.newInstance(QueryRequestParser.class, new Class[] {String.class}, expectedQueryString);
                result = mockQueryRequestParser;

                mockQueryRequestParser.toJson();
                result = expectedQueryStringJson;

                expectedQueryStringJson.getBytes();
                result = expectedQueryStringBytes;

                DeviceOperations.request((IotHubConnectionString) any, (URL) any, (HttpMethod) any, expectedQueryStringBytes, null);
                result = mockHttpResponse;

                mockHttpResponse.getHeaderFields();
                result = expectedValidResponseHeaders;

                mockHttpResponse.getBody();
                result = expectedQueryStringBytes;
            }
        };

        //act
        Deencapsulation.invoke(queryCollection, "sendQueryRequest", new Class[] {QueryOptions.class}, (QueryOptions) null);

        //assert
        new Verifications()
        {
            {
                Deencapsulation.newInstance(QueryCollectionResponse.class, new Class[] {String.class, String.class}, expectedQueryStringJson, expectedResponseContinuationToken);
                times = 1;
            }
        };
    }

    //Tests_SRS_QUERYCOLLECTION_34_020: [If the request type and response does not match then the method shall throw IOException.]
    @Test (expected = IOException.class)
    public void sendQueryRequestThrowsForMismatchedResponseQueryType() throws IOException, IotHubException
    {
        //arrange
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, long.class}, expectedPageSize, QueryType.JOB_RESPONSE, mockConnectionString, mockUrl, mockHttpMethod, connectTimeout, expectedTimeout);

        new NonStrictExpectations()
        {
            {
                mockQueryOptions.getContinuationToken();
                result = expectedRequestContinuationToken;

                mockQueryOptions.getPageSize();
                result = expectedPageSize;

                DeviceOperations.request(mockConnectionString, mockUrl, mockHttpMethod, (byte[]) any, anyString, connectTimeout, expectedTimeout);
                result = mockHttpResponse;

                mockHttpResponse.getHeaderFields();
                result = expectedValidResponseHeaders;
            }
        };

        //act
        Deencapsulation.invoke(queryCollection, "sendQueryRequest", new Class[] {QueryOptions.class}, mockQueryOptions);
    }

    //Tests_SRS_QUERYCOLLECTION_34_025: [If this query is the initial query, this function shall return true.]
    @Test
    public void hasNextReturnsTrueIfItIsTheInitialQuery()
    {
        //arrange
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, long.class}, "some query", expectedPageSize, QueryType.DEVICE_JOB, mockConnectionString, mockUrl, mockHttpMethod, connectTimeout, expectedTimeout);
        Deencapsulation.setField(queryCollection, "isInitialQuery", true);

        //act
        boolean hasNext = Deencapsulation.invoke(queryCollection, "hasNext");

        //assert
        assertTrue(hasNext);
    }

    //Tests_SRS_QUERYCOLLECTION_34_026: [If this query is not the initial query, this function shall return true if there is a continuation token and false otherwise.]
    @Test
    public void hasNextReturnsFalseIfNoContinuationToken() throws IOException, IotHubException
    {
        //arrange
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, long.class}, "some query", expectedPageSize, QueryType.DEVICE_JOB, mockConnectionString, mockUrl, mockHttpMethod, connectTimeout, expectedTimeout);
        Deencapsulation.setField(queryCollection, "responseContinuationToken", null);
        Deencapsulation.setField(queryCollection, "isInitialQuery", false);

        //act
        boolean hasNext = Deencapsulation.invoke(queryCollection, "hasNext");

        //assert
        assertFalse(hasNext);
    }

    //Tests_SRS_QUERYCOLLECTION_34_026: [If this query is not the initial query, this function shall return true if there is a continuation token and false otherwise.]
    @Test
    public void hasNextReturnsTrueIfContinuationTokenSaved() throws IOException, IotHubException
    {
        //arrange
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, long.class}, "some query", expectedPageSize, QueryType.DEVICE_JOB, mockConnectionString, mockUrl, mockHttpMethod, connectTimeout, expectedTimeout);
        Deencapsulation.setField(queryCollection, "responseContinuationToken", "any continuation token");
        Deencapsulation.setField(queryCollection, "isInitialQuery", false);

        //act
        boolean hasNext = Deencapsulation.invoke(queryCollection, "hasNext");

        //assert
        assertTrue(hasNext);
    }

    //Tests_SRS_QUERYCOLLECTION_34_032: [If this object has a next set to return, this function shall return it.]
    @Test
    public void nextReturnsNextSetIfItHasOne() throws IOException, IotHubException
    {
        //arrange
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, long.class},"anyString", expectedPageSize, QueryType.JOB_RESPONSE, mockConnectionString, mockUrl, mockHttpMethod, connectTimeout, expectedTimeout);

        //only mocking one method for QueryCollection, not the whole class
        new NonStrictExpectations(queryCollection)
        {
            {
                Deencapsulation.invoke(queryCollection, "hasNext");
                result = true;

                new QueryOptions();
                result = mockQueryOptions;

                Deencapsulation.invoke(queryCollection, "sendQueryRequest", new Class[] {QueryOptions.class}, mockQueryOptions);
                result = mockQueryCollectionResponse;
            }
        };

        //act
        QueryCollectionResponse actualResponse = Deencapsulation.invoke(queryCollection, "next");

        //assert
        assertEquals(mockQueryCollectionResponse, actualResponse);
    }

    //Tests_SRS_QUERYCOLLECTION_34_033: [If this object does not have a next set to return, this function shall return null.]
    @Test
    public void nextReturnsNullIfItDoesNotHaveNext() throws IOException, IotHubException
    {
        //arrange
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, long.class},"anyString", expectedPageSize, QueryType.JOB_RESPONSE, mockConnectionString, mockUrl, mockHttpMethod, connectTimeout, expectedTimeout);

        //only mocking one method for QueryCollection, not the whole class
        new NonStrictExpectations(queryCollection)
        {
            {
                Deencapsulation.invoke(queryCollection, "hasNext");
                result = false;
            }
        };

        //act
        QueryCollectionResponse actualResponse = Deencapsulation.invoke(queryCollection, "next");

        //assert
        assertNull(actualResponse);
    }

    //Tests_SRS_QUERYCOLLECTION_34_034: [If this object has a next set to return using the provided query options, this function shall return it.]
    @Test
    public void nextWithOptionsReturnsNextSetIfItHasOne() throws IOException, IotHubException
    {
        //arrange
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, long.class},"anyString", expectedPageSize, QueryType.JOB_RESPONSE, mockConnectionString, mockUrl, mockHttpMethod, connectTimeout, expectedTimeout);

        //only mocking one method for QueryCollection, not the whole class
        new NonStrictExpectations(queryCollection)
        {
            {
                Deencapsulation.invoke(queryCollection, "hasNext");
                result = true;

                Deencapsulation.invoke(queryCollection, "sendQueryRequest", new Class[] {QueryOptions.class}, mockQueryOptions);
                result = mockQueryCollectionResponse;
            }
        };

        //act
        QueryCollectionResponse actualResponse = Deencapsulation.invoke(queryCollection, "next", new Class[] {QueryOptions.class}, mockQueryOptions);

        //assert
        assertEquals(mockQueryCollectionResponse, actualResponse);
    }

    //Tests_SRS_QUERYCOLLECTION_34_035: [If this object does not have a next set to return, this function shall return null.]
    @Test
    public void nextWithOptionsReturnsNullIfItDoesNotHaveNext() throws IOException, IotHubException
    {
        //arrange
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, long.class},"anyString", expectedPageSize, QueryType.JOB_RESPONSE, mockConnectionString, mockUrl, mockHttpMethod, connectTimeout, expectedTimeout);

        //only mocking one method for QueryCollection, not the whole class
        new NonStrictExpectations(queryCollection)
        {
            {
                Deencapsulation.invoke(queryCollection, "hasNext");
                result = false;
            }
        };

        //act
        QueryCollectionResponse actualResponse = Deencapsulation.invoke(queryCollection, "next", new Class[] {QueryOptions.class}, mockQueryOptions);

        //assert
        assertNull(actualResponse);
    }

    //Tests_SRS_QUERYCOLLECTION_34_036: [This function shall return the saved page size.]
    @Test
    public void getPageSize()
    {
        //arrange
        Integer expectedPageSize = 290;
        QueryCollection queryCollection = Deencapsulation.newInstance(QueryCollection.class, new Class[] {String.class, int.class, QueryType.class, IotHubConnectionString.class, URL.class, HttpMethod.class, long.class, long.class},"anyString", expectedPageSize, QueryType.JOB_RESPONSE, mockConnectionString, mockUrl, mockHttpMethod, connectTimeout, expectedTimeout);
        Deencapsulation.setField(queryCollection, "pageSize", expectedPageSize);

        //act
        Integer actualPageSize = Deencapsulation.invoke(queryCollection, "getPageSize");

        //assert
        assertEquals(expectedPageSize, actualPageSize);
    }

    private void sentRequestVerifications() throws IOException, IotHubException
    {
        new Verifications()
        {
            {
                DeviceOperations.setHeaders(expectedValidRequestHeaders);
                times = 1;

                DeviceOperations.request(mockConnectionString, mockUrl, mockHttpMethod, (byte[]) any, null, connectTimeout, expectedTimeout);
                times = 1;
            }
        };
    }
}
