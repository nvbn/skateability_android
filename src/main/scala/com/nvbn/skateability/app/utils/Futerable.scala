package com.nvbn.skateability.app.utils

import scala.concurrent.ExecutionContext
import java.util.concurrent.{LinkedBlockingQueue, TimeUnit, ThreadPoolExecutor}

trait Futerable {

  implicit val exec = ExecutionContext.fromExecutor(
    new ThreadPoolExecutor(100, 100, 1000, TimeUnit.SECONDS,
      new LinkedBlockingQueue[Runnable]))
}
