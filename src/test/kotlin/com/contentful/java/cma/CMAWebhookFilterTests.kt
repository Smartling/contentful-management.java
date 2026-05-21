package com.contentful.java.cma

import com.contentful.java.cma.model.CMAWebhookFilter
import com.google.gson.Gson
import org.skyscreamer.jsonassert.JSONAssert.assertEquals as assertEqualJsons
import org.junit.Test as test

class CMAWebhookFilterTests {

    @test
    fun testEqualsFilterSerialization() {
        val filter = CMAWebhookFilter.equals("sys.environment.sys.id", "master")
        val json = Gson().toJson(filter)
        assertEqualJsons(
            """{"equals":[{"doc":"sys.environment.sys.id"},"master"]}""",
            json,
            true
        )
    }
}
