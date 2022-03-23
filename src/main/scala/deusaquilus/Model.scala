package deusaquilus

trait HumanLike { def id: Int; def age: Int }
case class Human(id: Int, firstName: String, lastName: String, age: Int, membership: String, segment: String) extends HumanLike
case class SuperHuman(id: Int, heroName: String, age: Int, side: String)                                      extends HumanLike

trait RobotLike { def id: Int; def assemblyYear: Int }
case class Robot(id: Int, model: String, assemblyYear: Int)                       extends RobotLike
case class KillerRobot(id: Int, model: String, assemblyYear: Int, series: String) extends RobotLike

case class Customer(id: Int, name: String, age: Int, membership: String)
case class Yetti(id: Int, uniqueGruntingSound: String, age: Int)

case class Houses(id: Int, owner: Int, origin: String, hasChargingPort: Boolean)
case class PricingYears(startYear: Int, endYear: Int, pricing: String, insaneMembership: String, voltage: Int)
case class Record(name: String, age: Int, membership: String, id: Int, hid: Int)
