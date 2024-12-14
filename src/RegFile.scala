package sandbox

import chisel3._

class RegFile extends Module {
  val io = IO(new Bundle {
    val r0addr = Input(UInt(5.W))
    val r1addr = Input(UInt(5.W))
    val w0addr = Input(UInt(5.W))
    val w0en = Input(Bool())
    val w0data = Input(UInt(64.W))
    val r0out = Output(UInt(64.W))
    val r1out = Output(UInt(64.W))
  })

  // val regs = Mem(32, UInt(64.W))
  val regs = Reg(Vec(32, UInt(64.W)))
  io.r0out := regs(io.r0addr)
  io.r1out := regs(io.r1addr)
  when(io.w0en) {
    regs(io.w0addr) := io.w0data
  }
}

class RegFileExt(nRead: Int) extends Module {
  val io = IO(new Bundle {
    val raddr = Input(Vec(nRead, UInt(5.W)))
    val w0addr = Input(UInt(5.W))
    val w0en = Input(Bool())
    val w0data = Input(UInt(64.W))
    val rout = Output(Vec(nRead, UInt(64.W)))
  })
  val regs = Mem(32, UInt(64.W))
  for (i <- 0 until nRead)
    io.rout(i) := regs(io.raddr(i))
  when(io.w0en) {
    regs(io.w0addr) := io.w0data
  }
}
