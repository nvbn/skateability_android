package com.nvbn.skateability.app

import android.location.Location
import android.os.Bundle
import com.google.android.gms.common.{ConnectionResult, GooglePlayServicesClient}
import com.google.android.gms.location.{LocationRequest, LocationListener, LocationClient}
import org.scaloid.common._

class DataService extends BindedService with GooglePlayServicesClient.ConnectionCallbacks
                                        with GooglePlayServicesClient.OnConnectionFailedListener
                                        with LocationListener
                                        with HasSettings {
  val UPDATE_INTERVAL = 1000
  val FASTEST_INTERVAL = 500

  var locationClient: Option[LocationClient] = None
  var locationRequest: Option[LocationRequest] = None

  override def onCreate() = {
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
    for (client <- locationClient)
      client.disconnect()
    settings.session = 0L
    super.onDestroy()
  }

  override def onConnected(dataBundle: Bundle) = {
    for {
      client <- locationClient
      request <- locationRequest
    } client.requestLocationUpdates(request, this)
  }

  override def onDisconnected() = waitAndReconnect()

  override def onConnectionFailed(connectionResult: ConnectionResult) = waitAndReconnect()

  override def onLocationChanged(location: Location) = {
    if (settings.session(0L) == 0L)
      settings.session = location.getTime

    val entry = new DataEntry(this, location, settings.session(0))
    entry.save()
  }

  def waitAndReconnect() = {
    Thread.sleep(500)
    for {client <- locationClient} client.connect()
  }
}