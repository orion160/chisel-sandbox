package sandbox

import chisel3._
import chisel3.util._

class QueueExample(maxVal: Int, numEntries: Int) extends Module {
  val io = IO(new Bundle {
    val en = Input(Bool())
    val out = Decoupled(UInt())
  })
  val q = Module(new Queue(UInt(), numEntries))
  val (count, wrap) = Counter(q.io.enq.fire, maxVal)
  q.io.enq.valid := io.en
  q.io.enq.bits := count
  io.out <> q.io.deq
}
