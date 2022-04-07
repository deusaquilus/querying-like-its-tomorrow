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
import zhttp.service.Server
import zhttp.http.Http
import zhttp.http.Request
import caliban.ZHttpAdapter
import zhttp.http._

object GraphqlService extends ZIOAppDefault:
  case class Queries(
    customers: Field => (ProductArgs[Record] => Task[List[Record]]),
    customersPlan: Field => (ProductArgs[Record] => Task[RecordPlanQuery]),
    superCustomers: Field => (ProductArgs[Record] => Task[List[Record]]),
    superCustomersPlan: Field => (ProductArgs[Record] => Task[RecordPlanQuery])
  )

  def graphqlService(dsa: AdvDataService) =
    graphQL(
      RootResolver(
        Queries(
          customers =>
            (productArgs => dsa.getCustomers(productArgs.keyValues, quillColumns(customers))),
          customersPlan =>
            (productArgs => dsa.getCustomersWithPlan(productArgs.keyValues, quillColumns(customersPlan))),
          superCustomers =>
            (productArgs => dsa.getSuperCustomers(productArgs.keyValues, quillColumns(superCustomers))),
          superCustomersPlan =>
            (productArgs => dsa.getSuperCustomersWithPlan(productArgs.keyValues, quillColumns(superCustomersPlan))),
        )
      )
    ).interpreter

  val myApp = (for {
    dsa         <- ZIO.environment[AdvDataService]
    interpreter <- graphqlService(dsa.get)
    _ <- Server.start(
           port = 8088,
           http = Http.route[Request] { case _ -> !! / "api" / "graphql" =>
             ZHttpAdapter.makeHttpService(interpreter)
           }
         ).forever
  } yield ()).provide(QuillContext.dataSourceLayer, AdvDataService.live)

  def run = myApp.exitCode

end GraphqlService
