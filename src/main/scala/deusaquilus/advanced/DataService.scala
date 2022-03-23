package deusaquilus.advanced

import zio.IO
import java.sql.SQLException
import javax.sql.DataSource
import zio.Console.printLine
import io.getquill._

import deusaquilus.ServiceQueries._
import io.getquill.context.ZioJdbc.DataSourceLayer
import zio._
import deusaquilus.QuillContext._
import deusaquilus.Record

trait DataServiceAdvanced:
  def getCustomers(params: Map[String, String], columns: List[String]): IO[SQLException, List[Record]]
  def getCustomersPlan(params: Map[String, String], columns: List[String]): IO[SQLException, List[String]]

object DataServiceAdvanced:
  val live = (DataServiceLiveAdvanced.apply _).toLayer[DataServiceAdvanced]

final case class DataServiceLiveAdvanced(dataSource: DataSource) extends DataServiceAdvanced:
  def getCustomers(params: Map[String, String], columns: List[String]): IO[SQLException, List[Record]] =
    if (columns.nonEmpty)
      run(customersWithFiltersAndColumns(params, columns)).provideService(dataSource)
    else
      run(customersWithFilters(params)).provideService(dataSource)
  def getCustomersPlan(params: Map[String, String], columns: List[String]): IO[SQLException, List[String]] =
    if (columns.nonEmpty)
      run(customersPlan(customersWithFiltersAndColumns(params, columns)), OuterSelectWrap.Never).provideService(dataSource)
    else
      run(customersPlan(customersWithFilters(params)), OuterSelectWrap.Never).provideService(dataSource)
