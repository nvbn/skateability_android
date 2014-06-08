package com.nvbn.skateability.app

import org.scaloid.common._
import com.nvbn.skateability.app.utils.BindedService
import java.util.concurrent.{ScheduledFuture, Executors}
import java.util.concurrent.TimeUnit.SECONDS
import org.json.JSONArray

class SyncService extends BindedService{
  implicit val tag = LoggerTag("SyncService")

  val scheduler = Executors.newScheduledThreadPool(1)
  var syncHandler: Option[ScheduledFuture[_]] = None

  override def onCreate() = {
    info("Create service")
    syncHandler = Some(scheduler.scheduleAtFixedRate(
      sync, 0, 60, SECONDS))
    super.onCreate()
  }

  override def onDestroy() = {
    info("Destroy service")
    for (handler <- syncHandler)
      handler.cancel(true)
    super.onDestroy()
  }

  def sync() = {
    info("Start syncing")
    val notSynced = DataEntry.notSynced
    if (sendToServer(notSynced))
      markSynced(notSynced)
    info("Synced")
  }

  def sendToServer(items: List[DataEntry]) = {
    val toSend = items.map(_.serialise)
    info(new JSONArray(toSend).toString)
    true
  }

  def markSynced(items: List[DataEntry]) = for (entry <- items) {
    entry.synced = true
    entry.save()
  }
}
