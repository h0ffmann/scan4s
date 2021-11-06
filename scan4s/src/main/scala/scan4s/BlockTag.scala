package scan4s

import enumeratum.EnumEntry.Lowercase
import enumeratum._

sealed trait BlockTag extends EnumEntry with Lowercase

object BlockTag extends Enum[BlockTag] {

  case object Earliest extends BlockTag
  case object Pending  extends BlockTag
  case object Latest   extends BlockTag

  override def values: IndexedSeq[BlockTag] = findValues
}
