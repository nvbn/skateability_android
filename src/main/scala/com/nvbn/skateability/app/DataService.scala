package com.nvbn.skateability.app

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.{Binder, Bundle}
import com.google.android.gms.common.{ConnectionResult, GooglePlayServicesClient}
import com.google.android.gms.location.{LocationRequest, LocationListener, LocationClient}

class DataService extends Service with GooglePlayServicesClient.ConnectionCallbacks
                                  with GooglePlayServicesClient.OnConnectionFailedListener
                                  with LocationListener
                                  with HasSettings {
  val UPDATE_INTERVAL = 1000
  val FASTEST_INTERVAL = 500

  var mLocationClient: Option[LocationClient] = None
  var mLocationRequest: Option[LocationRequest] = None
  val mBinder = new LocalBinder()

  class LocalBinder extends Binder {
    def getServerInstance = DataService.this
  }

  override def onBind(intent: Intent) = mBinder

  override def onCreate() = {
    super.onCreate()
    mLocationRequest = Some(
      LocationRequest.create
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(UPDATE_INTERVAL)
        .setFastestInterval(FASTEST_INTERVAL))
    val client = new LocationClient(this, this, this)
    client.connect()
    mLocationClient = Some(client)
  }

  override def onDestroy() = {
    for (client <- mLocationClient)
      client.disconnect()
    settings.session = 0L
    super.onDestroy()
  }

  override def onConnected(dataBundle: Bundle) = {
    for {
      client <- mLocationClient
      request <- mLocationRequest
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
    for {client <- mLocationClient} client.connect()
  }
}