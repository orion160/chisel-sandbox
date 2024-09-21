package sandbox

import chisel3._
import chisel3.util.Counter
import circt.stage.ChiselStage

class Blinky(freq: Int, startOn: Boolean = false) extends Module {
  val io = IO(new Bundle {
    val led0 = Output(Bool())
  })
  val led = RegInit(startOn.B)
  val (_, counterWrap) = Counter(true.B, freq / 2)
  when(counterWrap) {
    led := ~led
  }
  io.led0 := led
}

object BlinkyMain extends App {
  val printHeader = (header: String) => println(s"\u001B[32m${header}\u001B[0m")

  printHeader("System Verilog:")
  println(
    ChiselStage.emitSystemVerilog(
      new Blinky(1000),
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )

  printHeader("HW Dialect:")
  println(ChiselStage.emitHWDialect(new Blinky(1000)))

  printHeader("CHIRRTL:")
  println(ChiselStage.emitCHIRRTL(new Blinky(1000)))
}
