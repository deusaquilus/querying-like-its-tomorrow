package deusaquilus.intermediate

import zhttp.service.Server
import zhttp.http._
import zio._
import zio.json._
import zio.Console.printLine
import deusaquilus.Record
import deusaquilus.intermediate.DataService
import deusaquilus.intermediate.DataServiceLive
import deusaquilus.QuillContext

object RestService extends ZIOAppDefault:
  given JsonEncoder[Record] = DeriveJsonEncoder.gen[Record]
  override def run =
    Server.start(
      8088,
      Http.collectZIO[Request] {
        case req @ Method.GET -> !! / "customers" =>
          ZIO.environment[DataService].flatMap { dsl =>
            val lastParams    = req.url.queryParams.map((k, v) => (k, v.head))
            val columnsString = req.url.queryParams.get("columns").map(_.headOption).flatten
            val columns       = columnsString.map(_.split(",").map(_.trim).toList).getOrElse(List.empty)
            dsl.get.getCustomers(lastParams, columns).map(cs => Response.text(cs.map(_.toJson).mkString(",\n")))
          }
      }
    ).provide(QuillContext.dataSourceLayer, DataService.live).exitCode

end RestService
