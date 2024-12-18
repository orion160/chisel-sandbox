package sandbox

import chisel3._
import chisel3.util._

class Message(numOuts: Int, length: Int) extends Bundle {
  val addr = UInt(log2Ceil(numOuts).W)
  val data = UInt(length.W)
}

class XBarIO(numIns: Int, numOuts: Int, length: Int) extends Bundle {
  val in = Vec(numIns, Flipped(Decoupled(new Message(numOuts, length))))
  val out = Vec(numOuts, Decoupled(new Message(numOuts, length)))
}

class Crossbar2(numIns: Int, numOuts: Int, length: Int) extends Module {
  var io = IO(new XBarIO(numIns, numOuts, length))
  val arbs = Seq.fill(numOuts)(
    Module(new RRArbiter(new Message(numOuts, length), numIns))
  )
  for (ip <- 0 until numIns) {
    val inReadys = Wire(Vec(numOuts, Bool()))
    for (op <- 0 until numOuts) {
      inReadys(op) := arbs(op).io.in(ip).ready
    }
    io.in(ip).ready := inReadys.asUInt.orR
  }
  for (op <- 0 until numOuts) {
    for (ip <- 0 until numIns) {
      arbs(op).io.in(ip).bits <> io.in(ip).bits
      arbs(op).io
        .in(ip)
        .valid := io.in(ip).valid && (io.in(ip).bits.addr === op.U)
    }
    io.out(op) <> arbs(op).io.out
  }
}
