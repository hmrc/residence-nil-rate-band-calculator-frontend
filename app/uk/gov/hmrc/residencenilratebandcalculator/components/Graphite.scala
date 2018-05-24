/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.residencenilratebandcalculator.components

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit.{MILLISECONDS, SECONDS}

import javax.inject.{Inject, Singleton}
import com.codahale.metrics.graphite.{Graphite, GraphiteReporter}
import com.codahale.metrics.jvm.{GarbageCollectorMetricSet, MemoryUsageGaugeSet, ThreadStatesGaugeSet}
import com.codahale.metrics.{JvmAttributeGaugeSet, MetricFilter, MetricRegistry, SharedMetricRegistries}
import com.typesafe.config.ConfigFactory
import play.api.inject.ApplicationLifecycle

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class Graphite @Inject()(lifecycle: ApplicationLifecycle,
                                  reporter: GraphiteReporter) {

  private val configLoader = ConfigFactory.load()
  lazy val metricHost: String = configLoader.getString("graphite.host")
  lazy val metricPort: Int = configLoader.getInt("graphite.port")
  lazy val metricPrefix: String = configLoader.getString("graphite.prefix")
  lazy val metricsEnabled: Boolean = configLoader.getBoolean("graphite.enabled")
  lazy val metricRefreshInterval: Int = configLoader.getInt("graphite.refreshInterval")
  
  lazy val prefix: String = metricPrefix

  def defaultRegistry: MetricRegistry = {
    SharedMetricRegistries.getOrCreate(prefix)
  }

  def clearRegistry(): Unit = {
    SharedMetricRegistries.clear()
  }


  lifecycle.addStopHook { () =>
    //Exit cleanly
    clearRegistry()
    Future(reporter.stop())
  }
}