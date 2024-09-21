package sandbox

import chisel3._
import circt.stage.ChiselStage

class PassthroughGenerator(width: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(width.W))
    val out = Output(UInt(width.W))
  })
  io.out := io.in
}

object PassthroughMain extends App {
  val printHeader = (header: String) => println(s"\u001B[32m${header}\u001B[0m")

  printHeader("System Verilog:")
  println(
    ChiselStage.emitSystemVerilog(
      new PassthroughGenerator(4),
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )

  printHeader("HW Dialect:")
  println(ChiselStage.emitHWDialect(new PassthroughGenerator(4)))

  printHeader("CHIRRTL:")
  println(ChiselStage.emitCHIRRTL(new PassthroughGenerator(4)))
}
