package com.nvbn.skateability.app

import android.location.Location
import android.os.Bundle
import com.google.android.gms.common.{ConnectionResult, GooglePlayServicesClient}
import com.google.android.gms.location.{LocationRequest, LocationListener, LocationClient}
import org.scaloid.common._
import utils.{HasSettings, BindedService}

class DataService extends BindedService with GooglePlayServicesClient.ConnectionCallbacks
                                        with GooglePlayServicesClient.OnConnectionFailedListener
                                        with LocationListener
                                        with HasSettings {
  implicit val tag = LoggerTag("DataService")

  val UPDATE_INTERVAL = 1000
  val FASTEST_INTERVAL = 500

  var locationClient: Option[LocationClient] = None
  var locationRequest: Option[LocationRequest] = None

  override def onCreate() = {
    info("Create service")
    locationRequest = Some(
      LocationRequest.create
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(UPDATE_INTERVAL)
        .setFastestInterval(FASTEST_INTERVAL))
    val client = new LocationClient(this, this, this)
    client.connect()
    locationClient = Some(client)
    super.onCreate()
  }

  override def onDestroy() = {
    info("Destroy service")
    for (client <- locationClient)
      client.disconnect()
    settings.session = 0L
    super.onDestroy()
  }

  override def onConnected(dataBundle: Bundle) = {
    info("Connected")
    for {
      client <- locationClient
      request <- locationRequest
    } client.requestLocationUpdates(request, this)
  }

  override def onDisconnected() = {
    info("Disconnected")
    waitAndReconnect()
  }

  override def onConnectionFailed(connectionResult: ConnectionResult) = {
    info(s"Connection failed: $connectionResult")
    waitAndReconnect()
  }

  override def onLocationChanged(location: Location) = {
    info(s"Location changed: $location}")
    if (settings.session(0L) == 0L)
      settings.session = location.getTime

    val entry = new DataEntry(this, location, settings.session(0))
    entry.save()
  }

  def waitAndReconnect() = {
    info("Reconnect")
    Thread.sleep(500)
    for {client <- locationClient} client.connect()
  }
}