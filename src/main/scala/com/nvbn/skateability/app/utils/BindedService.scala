package com.nvbn.skateability.app.utils

import org.scaloid.common.SService
import android.os.Binder
import android.content.Intent

class BindedService extends SService {
  val mBinder = new Binder {
    def getServerInstance = BindedService.this
  }

  override def onBind(intent: Intent) = mBinder
}
