package com.contentful.java.cma

import com.contentful.java.cma.lib.TestUtils
import com.contentful.java.cma.model.OAuthException
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import java.util.logging.LogManager
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.Test as test

class OAuthTests {
    lateinit var server: MockWebServer
    lateinit var client: OAuthClient

    @Before
    fun setUp() {
        LogManager.getLogManager().reset()
        // MockWebServer
        server = MockWebServer()
        server.start()

        // OAuth client - no access token required
        client = OAuthClient.Builder()
            .setEndpoint(server.url("/").toString())
            .build()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @test
    fun testExchangeAuthorizationCode() {
        val responseBody = TestUtils.fileToString("oauth_token_exchange_response.json")
        server.enqueue(MockResponse().setResponseCode(200).setBody(responseBody))

        val result = client.oauth().exchangeAuthorizationCode(
            "test-client-id",
            "test-client-secret",
            "test-authorization-code",
            "https://example.com/callback"
        )

        // Verify response
        assertNotNull(result)
        assertEquals("test-access-token", result.accessToken)
        assertEquals("test-refresh-token", result.refreshToken)
        assertEquals("Bearer", result.tokenType)
        assertEquals(2591999, result.expiresIn)
        assertEquals("content_management_manage", result.scope)
        assertEquals(1737558891L, result.createdAt)

        // Verify request
        val recordedRequest = server.takeRequest()
        assertEquals("POST", recordedRequest.method)
        assertEquals("/oauth/token", recordedRequest.path)
        assertEquals("application/x-www-form-urlencoded", recordedRequest.getHeader("Content-Type"))

        val requestBody = recordedRequest.body.readUtf8()
        assert(requestBody.contains("grant_type=authorization_code"))
        assert(requestBody.contains("client_id=test-client-id"))
        assert(requestBody.contains("client_secret=test-client-secret"))
        assert(requestBody.contains("code=test-authorization-code"))
        assert(requestBody.contains("redirect_uri=https%3A%2F%2Fexample.com%2Fcallback"))
    }

    @test
    fun testRefreshAccessToken() {
        val responseBody = TestUtils.fileToString("oauth_token_refresh_response.json")
        server.enqueue(MockResponse().setResponseCode(200).setBody(responseBody))

        val result = client.oauth().refreshAccessToken(
            "test-client-id",
            "test-client-secret",
            "old-refresh-token",
            "https://example.com/callback"
        )

        // Verify response
        assertNotNull(result)
        assertEquals("new-access-token", result.accessToken)
        assertEquals("new-refresh-token", result.refreshToken)
        assertEquals("Bearer", result.tokenType)
        assertEquals(2591999, result.expiresIn)
        assertEquals("content_management_manage", result.scope)
        assertEquals(1737558900L, result.createdAt)

        // Verify request
        val recordedRequest = server.takeRequest()
        assertEquals("POST", recordedRequest.method)
        assertEquals("/oauth/token", recordedRequest.path)
        assertEquals("application/x-www-form-urlencoded", recordedRequest.getHeader("Content-Type"))

        val requestBody = recordedRequest.body.readUtf8()
        assert(requestBody.contains("grant_type=refresh_token"))
        assert(requestBody.contains("client_id=test-client-id"))
        assert(requestBody.contains("client_secret=test-client-secret"))
        assert(requestBody.contains("refresh_token=old-refresh-token"))
        assert(requestBody.contains("redirect_uri=https%3A%2F%2Fexample.com%2Fcallback"))
    }

    @test
    fun testExchangeAuthorizationCodeWitError() {
        val errorResponse = """
            {
                "error": "invalid_grant",
                "errorMessage": "The provided authorization grant is invalid, expired, revoked, does not match the redirection URI used in the authorization request, or was issued to another client.",
                "requestId": "1b991683-6531-4a22-a765-1a9c6781c1ee"
            }
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(400).setBody(errorResponse))

        try {
            client.oauth().exchangeAuthorizationCode(
                "invalid-client-id",
                "invalid-secret",
                "test-code",
                "https://example.com/callback"
            )
            assert(false) { "Should have thrown OAuthException for 400" }
        } catch (e: OAuthException) {
            // Expected - verify it's a proper OAuth error
            assertNotNull(e)
            assertEquals(400, e.responseCode())
            assertNotNull(e.errorBody)
            assertEquals("invalid_grant", e.errorBody?.error)
            assertTrue(e.errorBody?.errorMessage?.contains("authorization grant is invalid") == true)
            assertEquals("1b991683-6531-4a22-a765-1a9c6781c1ee", e.errorBody?.requestId)
        }
    }

    @test
    fun testRefreshTokenWithError() {
        val errorResponse = """
            {
                "error": "invalid_grant",
                "errorMessage": "The refresh token is invalid or expired.",
                "requestId": "2c882794-7642-5b33-b876-2b0d7892d2ff"
            }
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(400).setBody(errorResponse))

        try {
            client.oauth().refreshAccessToken(
                "test-client-id",
                "test-secret",
                "expired-refresh-token",
                "https://example.com/callback"
            )
            assert(false) { "Should have thrown OAuthException for 400" }
        } catch (e: OAuthException) {
            // Expected - verify it's a proper OAuth error
            assertNotNull(e)
            assertEquals(400, e.responseCode())
            assertNotNull(e.errorBody)
            assertEquals("invalid_grant", e.errorBody?.error)
            assertTrue(e.errorBody?.errorMessage?.contains("refresh token is invalid") == true)
            assertEquals("2c882794-7642-5b33-b876-2b0d7892d2ff", e.errorBody?.requestId)
        }
    }
}
