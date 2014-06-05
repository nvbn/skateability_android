package com.nvbn.skateability.app

import scala.collection.JavaConverters._
import android.content.Context
import android.widget.{TextView, ArrayAdapter}
import android.view.{LayoutInflater, ViewGroup, View}

class LogAdapter(values: List[DataEntry])(implicit context: Context)
  extends ArrayAdapter[DataEntry](context, R.layout.log_entry, values.asJava) {

  def view(parent: ViewGroup) = context
    .getSystemService(Context.LAYOUT_INFLATER_SERVICE)
    .asInstanceOf[LayoutInflater]
    .inflate(R.layout.log_entry, parent, false)

  override def getView(position: Int, convertView: View, parent: ViewGroup) = {
    val item = values(position)
    setAll(parent)(
      (R.id.session, item.session),
      (R.id.time, item.time),
      (R.id.lon, item.longitude),
      (R.id.lat, item.latitude),
      (R.id.speed, item.speed),
      (R.id.alt, item.altitude))
  }

  def set(id: Int, value: String, view: View) = view
    .findViewById(id)
    .asInstanceOf[TextView]
    .setText(value.toString)

  def setAll(parent: ViewGroup)(pairs: (Int, Any)*) = {
    val resultView = view(parent)
    for (pair <- pairs)
      set(pair._1, pair._2.toString, resultView)
    resultView
  }
}
