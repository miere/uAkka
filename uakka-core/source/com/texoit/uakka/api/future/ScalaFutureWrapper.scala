package com.texoit.uakka.api.future

import scala.concurrent._
import ExecutionContext.Implicits.global
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ArrayBlockingQueue
import scala.util.Success
import scala.util.Failure

object ScalaFutureWrapper {

  def wrap[T](future: Future[T]): BlockingQueue[T] = {
    val data = new ArrayBlockingQueue[T](1)

    future onComplete {
      case Success(obj) =>
        data.put(obj)
      case Failure(obj) =>
        data.put(obj.asInstanceOf[T])
    }

    data
  }

  def handle[T](future:Future[T], onSuccess:Function[T], onFail:Function[Throwable]) = {
    future onComplete {
      case Success(obj) =>
        onSuccess.run(obj)
      case Failure(obj) =>
        onFail.run(obj)
    }
  }

}