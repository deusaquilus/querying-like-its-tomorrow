package deusaquilus.graphql

import zio.IO
import java.sql.SQLException
import javax.sql.DataSource
import zio.Console.printLine
import io.getquill._

import io.getquill.context.ZioJdbc.DataSourceLayer
import zio._
import deusaquilus.QuillContext._
import deusaquilus.Record
import deusaquilus.graphql.AdvServiceQueries._

case class RecordPlanQuery(plan: List[String], records: List[Record])

trait AdvDataService:
  def getCustomers(params: Map[String, String], columns: List[String]): IO[SQLException, List[Record]]
  def getCustomersWithPlan(params: Map[String, String], columns: List[String]): IO[SQLException, RecordPlanQuery]
  def getSuperCustomers(params: Map[String, String], columns: List[String]): IO[SQLException, List[Record]]
  def getSuperCustomersWithPlan(params: Map[String, String], columns: List[String]): IO[SQLException, RecordPlanQuery]

object AdvDataService:
  val live = (AdvDataServiceLive.apply _).toLayer[AdvDataService]

final case class AdvDataServiceLive(dataSource: DataSource) extends AdvDataService:
  def getCustomers(params: Map[String, String], columns: List[String]): IO[SQLException, List[Record]] =
    run(customersWithFiltersAndColumns(params, columns)).provideService(dataSource).debug("====== Customers:")
  def getCustomersWithPlan(params: Map[String, String], columns: List[String]): IO[SQLException, RecordPlanQuery] =
    (for {
      results <- run(customersWithFiltersAndColumns(params, columns)).provideService(dataSource)
      plan    <- run(customersPlan(customersWithFiltersAndColumns(params, columns)), OuterSelectWrap.Never).provideService(dataSource)
    } yield RecordPlanQuery(plan, results)).debug("====== Customers With Plan:")

  def getSuperCustomers(params: Map[String, String], columns: List[String]): IO[SQLException, List[Record]] =
    run(superCustomersWithFiltersAndColumns(params, columns)).provideService(dataSource).debug("====== Super Customers:")
  def getSuperCustomersWithPlan(params: Map[String, String], columns: List[String]): IO[SQLException, RecordPlanQuery] =
    (for {
      results <- run(superCustomersWithFiltersAndColumns(params, columns)).provideService(dataSource)
      plan    <- run(superCustomersPlan(superCustomersWithFiltersAndColumns(params, columns)), OuterSelectWrap.Never).provideService(dataSource)
    } yield RecordPlanQuery(plan, results)).debug("====== Super Customers With Plan:")
