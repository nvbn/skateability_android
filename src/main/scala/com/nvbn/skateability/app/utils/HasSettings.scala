package com.nvbn.skateability.app.utils

import org.scaloid.common.Preferences
import android.preference.PreferenceManager
import android.content.Context

trait HasSettings {
  def settings = new Preferences(
    PreferenceManager.getDefaultSharedPreferences(
      this.asInstanceOf[Context]))
}
