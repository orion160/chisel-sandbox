package sandbox

import chisel3._
import chisel3.util._

class OneHotEncoder(inWidth: Int) extends Module {
  require(inWidth > 0)
  val outWidth = 1 << inWidth
  val io = IO(new Bundle {
    val in = Input(UInt(inWidth.W))
    val out = Output(UInt(outWidth.W))
  })
  def helper(index: Int): UInt = {
    if (index < outWidth - 1) helper(index + 1) ## io.in === index.U
    else io.in === index.U
  }
  io.out := helper(0)
}

class OneHotEncoder2(inWidth: Int) extends Module {
  require(inWidth > 0)
  val outWidth = 1 << inWidth
  val io = IO(new Bundle {
    val in = Input(UInt(inWidth.W))
    val out = Output(UInt(outWidth.W))
  })
  io.out := UIntToOH(io.in)
}
