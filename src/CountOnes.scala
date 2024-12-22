package sandbox

import chisel3._
import chisel3.util._

class CountOnes(n: Int) extends Module {
  require(n > 0)
  def linearPopCount(l: Seq[Bool]): UInt = {
    if (l.isEmpty) 0.U
    else l.head +& linearPopCount(l.tail)
  }
  def treePopCount(l: Seq[Bool]): UInt = l.size match {
    case 0 => 0.U
    case 1 => l.head
    case n => treePopCount(l take n / 2) +& treePopCount(l drop n / 2)
  }
  val io = IO(new Bundle {
    val in = Input(Vec(n, Bool()))
    val out = Output(UInt())
  })
  io.out := linearPopCount(io.in)
  io.out := treePopCount(io.in)
  // io.out := PopCount(io.in) chisel3.util
}
