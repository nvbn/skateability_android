package com.nvbn.skateability.app

import org.scaloid.common._
import android.os.Bundle
import android.widget.ListView

class LogActivity extends SActivity {

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_log)
    fillLog()
  }

  def fillLog() = find[ListView](R.id.activity_log)
      .setAdapter(new LogAdapter(DataEntry.all))
}
