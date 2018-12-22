package com.estsoft.api.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class LoadTest extends Simulation {
  private val baseUrl = "http://localhost:8080"
  private val contentType = "application/json"
  private val endpoint = "/users"
  private val requestCount = 10000


  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(baseUrl)
    .inferHtmlResources()
    .acceptHeader("*/*")
    .contentTypeHeader(contentType)
    .userAgentHeader("curl/7.54.0")

  val scn: ScenarioBuilder = scenario("ApplicationLoadTest")
    .exec(http("request_app")
      .get(endpoint)
      .check(status.is(200)))

  setUp(scn.inject(atOnceUsers(requestCount))).protocols(httpProtocol)
}