package sandbox

import chisel3._

class DelayN(n: Int) extends Module {
  require(n > 0)
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Output(Bool())
  })

  val regs = Seq.fill(n)(Reg(Bool()))
  regs(0) := io.in
  for (i <- 1 until n)
    regs(i) := regs(i - 1)

  io.out := regs(n - 1)
}

class DelayN2(n: Int) extends Module {
  require(n > 0)
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Output(Bool())
  })
  io.out := (0 until n).foldLeft(io.in) { (lastConn, i) => RegNext(lastConn) }
}
