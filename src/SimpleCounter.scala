import chisel3._
import chisel3.util._

class SimpleCounter(maxVal: Int) extends Module {
  require(maxVal > 0)

  val io = IO(new Bundle {
    val en = Input(Bool())
    val out = Output(UInt())
  })

  val count = Reg(UInt(log2Ceil(maxVal + 1).W))
  val nextVal = Mux(count < maxVal.U, count + 1.U, count)
  val applyEn = Mux(io.en, nextVal, count)
  count := Mux(reset.asBool, 0.U, applyEn)
  io.out := count
}
