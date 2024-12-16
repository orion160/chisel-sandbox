package sandbox

import chisel3._
import chisel3.util._

// TODO: Implement simple diplomacy example to negotiate w

class Generator(w: Int, c: Int) extends Module {
  val io = IO(new Bundle {
    val en = Input(Bool())
    val out = Valid(UInt(w.W))
  })

  val (value, wrap) = Counter(io.en, c)

  io.out.valid := io.en
  io.out.bits := value
}

class SquareProducer(w: Int) extends Module {
  val io = IO(new Bundle {
    val in = Flipped(Valid(UInt(w.W)))
    val out = Valid(UInt(w.W))
  })
  io.out.valid := io.in.valid
  when(io.in.valid) {
    io.out.bits := io.in.bits * io.in.bits
  }
}
