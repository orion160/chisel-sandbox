package sandbox

import chisel3._

class ClippedRelu(upperBound: Int) extends Module {
  val io = IO(new Bundle {
    val x = Input(SInt(5.W))
    val y = Output(SInt(5.W))
  })

  io.y := 0.S.max(io.x.min(upperBound.S));
}
