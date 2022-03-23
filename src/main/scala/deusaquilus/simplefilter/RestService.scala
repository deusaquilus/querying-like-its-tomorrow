package deusaquilus.simplefilter

import zio._
import zio.json._
import deusaquilus.Record
import zhttp.service.Server
import zhttp.http._
import deusaquilus.simple.DataService._
import deusaquilus.QuillContext

object RestService extends ZIOAppDefault:
  given JsonEncoder[Record] = DeriveJsonEncoder.gen[Record]
  override def run =
    Server.start(
      8088,
      Http.collectZIO[Request] {
        case req @ Method.GET -> !! / "customers" / id =>
          ZIO.environment[DataService].flatMap(dsl =>
            dsl.get.getCustomers(id.toInt).map(cs => Response.json(cs.toJson))
          )
      }
    ).provide(QuillContext.dataSourceLayer, DataService.live).exitCode
