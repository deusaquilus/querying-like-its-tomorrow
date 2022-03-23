package deusaquilus.graphql

import io.getquill.CalibanIntegration._
import zio.ZIOAppDefault
import caliban.execution.Field
import io.getquill.ProductArgs
import zio.Task
import deusaquilus.Record
import caliban.GraphQL.graphQL
import caliban.RootResolver
import zio.ZIO
import deusaquilus.QuillContext
import deusaquilus.advanced.DataServiceLiveAdvanced
import deusaquilus.advanced.DataServiceAdvanced
import zhttp.service.Server
import zhttp.http.Http
import zhttp.http.Request
import caliban.ZHttpAdapter
import zhttp.http._

case class RecordPlanQuery(plan: String, records: List[Record])

object GraphqlService extends ZIOAppDefault:
  case class Queries(customers: Field => (ProductArgs[Record] => Task[List[Record]]))

  def graphqlService(dsa: DataServiceAdvanced) =
    graphQL(
      RootResolver(
        Queries(customers =>
          (productArgs => dsa.getCustomers(productArgs.keyValues, quillColumns(customers)))
        )
      )
    ).interpreter

  val myApp = (for {
    dsa         <- ZIO.environment[DataServiceAdvanced]
    interpreter <- graphqlService(dsa.get)
    _ <- Server.start(
           port = 8088,
           http = Http.route[Request] { case _ -> !! / "api" / "graphql" =>
             ZHttpAdapter.makeHttpService(interpreter)
           }
         ).forever
  } yield ()).provide(QuillContext.dataSourceLayer, DataServiceAdvanced.live)

  def run = myApp.exitCode

end GraphqlService
