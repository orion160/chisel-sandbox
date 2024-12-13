package sandbox

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

class SimpleCounter2(maxVal: Int) extends Module {
  require(maxVal > 0)

  val io = IO(new Bundle {
    val en = Input(Bool())
    val out = Output(UInt())
  })

  val count = RegInit(0.U(log2Ceil(maxVal + 1).W))
  val nextVal = Mux(count < maxVal.U, count + 1.U, 0.U)
  count := Mux(io.en, nextVal, count)
  io.out := count
}

class SimpleCounter3(maxVal: Int) extends Module {
  require(maxVal > 0)

  val io = IO(new Bundle {
    val en = Input(Bool())
    val out = Output(UInt())
  })

  val count = RegInit(0.U(log2Ceil(maxVal + 1).W))

  when(io.en) {
    when(count < maxVal.U) {
      count := count + 1.U
    }.otherwise {
      count := 0.U
    }
  }
  io.out := count
}
