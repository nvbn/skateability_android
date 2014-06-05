package com.nvbn.skateability.app

import android.content.Context
import com.orm.SugarRecord
import android.location.Location
import com.orm.query.Select
import scala.collection.JavaConverters._

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

  def pretty = s"$time;$latitude;$longitude;$speed;$altitude\n"
}

object DataEntry {

  def all = from(select.orderBy("time DESC"))

  def select = Select.from(classOf[DataEntry])

  def from(records: Select[_]) = records.list.asInstanceOf[java.util.List[DataEntry]].asScala
}
