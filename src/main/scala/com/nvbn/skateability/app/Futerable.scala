package com.nvbn.skateability.app

import java.util.concurrent.{TimeUnit, ThreadPoolExecutor, LinkedBlockingQueue}
import scala.concurrent.ExecutionContext

trait Futerable {

  implicit val exec = ExecutionContext.fromExecutor(
    new ThreadPoolExecutor(100, 100, 1000, TimeUnit.SECONDS,
      new LinkedBlockingQueue[Runnable]))
}
