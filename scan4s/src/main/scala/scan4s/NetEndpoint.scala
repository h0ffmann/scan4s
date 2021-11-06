package scan4s

import enumeratum._

sealed abstract class NetEndpoint(override val entryName: String) extends EnumEntry

object NetEndpoint extends Enum[NetEndpoint] {

  case object MainNet extends NetEndpoint("https://api.etherscan.io")
  case object Goerli  extends NetEndpoint("https://api-goerli.etherscan.io/")
  case object Kovan   extends NetEndpoint("https://api-kovan.etherscan.io/")
  case object Rinkeby extends NetEndpoint("https://api-rinkeby.etherscan.io/")
  case object Ropsten extends NetEndpoint("https://api-ropsten.etherscan.io/")

  override def values: IndexedSeq[NetEndpoint] = findValues
}
