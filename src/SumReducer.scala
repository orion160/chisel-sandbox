package sandbox

import chisel3._

class SumReducer(n: Int, w: Int) extends Module {
  require(n > 0 && w > 0)

  val io = IO(new Bundle {
    val in = Input(Vec(n, UInt(w.W)))
    val out = UInt(w.W)
  })

  var total = io.in(0)
  for (i <- 1 until n)
    total = total + io.in(i)

  io.out := total
}
