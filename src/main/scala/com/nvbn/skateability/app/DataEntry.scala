package com.nvbn.skateability.app

import android.content.Context
import com.orm.SugarRecord
import android.location.Location
import com.orm.query.{Condition, Select}
import scala.collection.JavaConverters._
import android.provider.Settings.Secure
import com.orm.dsl.Ignore

class DataEntry(context: Context) extends SugarRecord[DataEntry](context) {
  var time: Long = 0
  var synced: Boolean = false
  var latitude: Double = 0
  var longitude: Double = 0
  var speed: Double = 0
  var altitude: Double = 0
  var session: Long = 0

  def this(context: Context, location: Location, session: Long) = {
    this(context)

    time = location.getTime
    latitude = location.getLatitude
    longitude = location.getLongitude
    this.session = session
    if (location.hasSpeed) speed = location.getSpeed
    if (location.hasAltitude) altitude = location.getAltitude
  }

  @Ignore
  def pretty = s"$time;$latitude;$longitude;$speed;$altitude\n"

  @Ignore
  def uniqueSession = {
    val deviceId = Secure.getString(context.getContentResolver, Secure.ANDROID_ID)
    s"android:$deviceId:$session"
  }

  @Ignore
  def serialise = Map[String, Any](
    "time" -> time,
    "latitude" -> latitude,
    "longitude" -> longitude,
    "speed" -> speed,
    "altitude" -> altitude,
    "session" -> uniqueSession
  )
}

object DataEntry {

  def all = from(select.orderBy("time DESC"))

  def notSynced = from(select.where(
    Condition.prop("synced").eq(false)))

  def select = Select.from(classOf[DataEntry])

  def from(records: Select[_]) = records
    .list
    .asInstanceOf[java.util.List[DataEntry]]
    .asScala
    .toList
}
