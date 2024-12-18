package sandbox

import chisel3._
import chisel3.util._

class ArbiterDemo(numPorts: Int, w: Int) extends Module {
  require(numPorts > 0)
  val io = IO(new Bundle {
    val req = Flipped(Vec(numPorts, Decoupled(UInt(w.W))))
    val out = Decoupled(UInt(w.W))
  })

  val arb = Module(new Arbiter(UInt(w.W), numPorts))
  arb.io.in <> io.req
  io.out <> arb.io.out
}

class Arbiter2(numPorts: Int, w: Int) extends Module {
  require(numPorts > 0)
  val io = IO(new Bundle {
    val req = Flipped(Vec(numPorts, Decoupled(UInt(w.W))))
    val out = Decoupled(UInt(w.W))
  })
  val inValids = Wire(Vec(numPorts, Bool()))
  val inBits = Wire(Vec(numPorts, UInt(w.W)))
  for (p <- 0 until numPorts) {
    io.req(p).ready := false.B
    inValids(p) := io.req(p).valid
    inBits(p) := io.req(p).bits
  }
  val chosenOH = PriorityEncoderOH(inValids)
  io.out.valid := inValids.asUInt.orR
  io.out.bits := Mux1H(chosenOH, inBits)
  val chosen = OHToUInt(chosenOH)
  when(io.out.fire) {
    io.req(chosen).ready := true.B
  }
}

class Arbiter3(numPorts: Int, w: Int) extends Module {
  require(numPorts > 0)
  val io = IO(new Bundle {
    val req = Flipped(Vec(numPorts, Decoupled(UInt(w.W))))
    val out = Decoupled(UInt(w.W))
  })
  val inValids = Wire(Vec(numPorts, Bool()))
  val inBits = Wire(Vec(numPorts, UInt(w.W)))
  val chosenOH = PriorityEncoderOH(inValids)
  for (p <- 0 until numPorts) {
    io.req(p).ready := chosenOH(p) && io.out.fire
    inValids(p) := io.req(p).valid
    inBits(p) := io.req(p).bits
  }
  io.out.valid := inValids.asUInt.orR
  io.out.bits := Mux1H(chosenOH, inBits)
}

class Arbiter4(numPorts: Int, w: Int) extends Module {
  require(numPorts > 0)
  val io = IO(new Bundle {
    val req = Flipped(Vec(numPorts, Decoupled(UInt(w.W))))
    val out = Decoupled(UInt(w.W))
  })
  val inValids = io.req.map { _.valid }
  io.out.valid := VecInit(inValids).asUInt.orR
  val chosenOH = PriorityEncoderOH(inValids)
  io.out.bits := Mux1H(chosenOH, io.req.map { _.bits })
  io.req.zip(chosenOH) foreach { case (i, c) => i.ready := c && io.out.fire }
}
