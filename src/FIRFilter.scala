package sandbox

import chisel3._
import chisel3.util.Counter
import circt.stage.ChiselStage

class FIRFilter(bitWidth: Int, coeffs: Seq[UInt]) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(bitWidth.W))
    val out = Output(UInt(bitWidth.W))
  })
  val zs = Reg(Vec(coeffs.length, UInt(bitWidth.W)))
  zs(0) := io.in
  for (i <- 1 until coeffs.length) {
    zs(i) := zs(i - 1)
  }

  val products = VecInit.tabulate(coeffs.length)(i => zs(i) * coeffs(i))

  io.out := products.reduce(_ + _)
}

object FIRMain extends App {
  val printHeader = (header: String) => println(s"\u001B[32m${header}\u001B[0m")

  printHeader("System Verilog:")
  println(
    ChiselStage.emitSystemVerilog(
      new FIRFilter(8, Seq(0.U, 1.U, 2.U, 1.U, 0.U)),
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )

  printHeader("HW Dialect:")
  println(
    ChiselStage.emitHWDialect(new FIRFilter(8, Seq(0.U, 1.U, 2.U, 1.U, 0.U)))
  )

  printHeader("CHIRRTL:")
  println(
    ChiselStage.emitCHIRRTL(new FIRFilter(8, Seq(0.U, 1.U, 2.U, 1.U, 0.U)))
  )
}
