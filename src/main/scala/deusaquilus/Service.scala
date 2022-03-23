package deusaquilus

import zio.IO
import java.sql.SQLException
import javax.sql.DataSource
import zio.Console.printLine
import io.getquill._

import QuillContext._
import ServiceQueries._
import io.getquill.context.ZioJdbc.DataSourceLayer
import zio._

object QuillContext extends PostgresZioJdbcContext(Literal):
  val dataSourceLayer: ULayer[DataSource] =
    DataSourceLayer.fromPrefix("database").orDie

trait DataService:
  def getCustomers: IO[SQLException, List[Record]]
  def getCustomersPlan: IO[SQLException, List[String]]

object DataService:
  val live = (DataServiceLive.apply _).toLayer[DataService]

final case class DataServiceLive(dataSource: DataSource) extends DataService:
  def getCustomers: IO[SQLException, List[Record]] =
    run(customers).provideService(dataSource)
  def getCustomersPlan: IO[SQLException, List[String]] =
    run(customersPlan(customers), OuterSelectWrap.Never).provideService(dataSource)

trait DataServiceAdvanced:
  def getCustomers(params: Map[String, String], columns: List[String]): IO[SQLException, List[Record]]
  def getCustomersPlan(params: Map[String, String], columns: List[String]): IO[SQLException, List[String]]

object DataServiceAdvanced:
  val live = (DataServiceLiveAdvanced.apply _).toLayer[DataServiceAdvanced]

final case class DataServiceLiveAdvanced(dataSource: DataSource) extends DataServiceAdvanced:
  def getCustomers(params: Map[String, String], columns: List[String]): IO[SQLException, List[Record]] =
    if (columns.nonEmpty) //
      run(customersWithFiltersAndColumns(params, columns)).provideService(dataSource)
    else
      run(customersWithFilters(params)).provideService(dataSource)
  def getCustomersPlan(params: Map[String, String], columns: List[String]): IO[SQLException, List[String]] =
    if (columns.nonEmpty) //
      run(customersPlan(customersWithFiltersAndColumns(params, columns)), OuterSelectWrap.Never).provideService(dataSource)
    else
      run(customersPlan(customersWithFilters(params)), OuterSelectWrap.Never).provideService(dataSource)
