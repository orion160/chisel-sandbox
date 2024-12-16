package sandbox

import chisel3._
import chisel3.util._

// TODO: Implement simple diplomacy example to negotiate w

class GeneratorDecoupled(w: Int, c: Int) extends Module {
  val io = IO(new Bundle {
    val en = Input(Bool())
    val out = Decoupled(UInt(w.W))
  })
  val advance = io.en && io.out.ready
  val (counter, wrap) = Counter(advance, c)
  when(io.en) {
    io.out.enq(counter)
  }.otherwise {
    io.out.noenq()
  }
}
