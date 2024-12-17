package sandbox

import chisel3._
import chisel3.util._

class PriorityEncoder(w: Int) extends Module {
  require(w > 0)
  val io = IO(new Bundle {
    val in = Input(UInt(w.W))
    val out = Output(UInt())
  })
  def withGates(index: Int, expr: UInt): UInt = {
    if (index < w)
      withGates(index + 1, ~io.in(index) & expr) ## io.in(index) & expr
    else io.in(index) & expr
  }
  def withMuxes(index: Int): UInt = {
    if (index < w) Mux(io.in(index), (1 << index).U, withMuxes(index + 1))
    else 0.U
  }
  // io.out := withGates(0, 1.U)
  // io.out := withMuxes(9)
  io.out := PriorityEncoderOH(io.in)
}
