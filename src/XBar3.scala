package sandboxa

import chisel3._
import chisel3.util._

case class XBarParams[T <: chisel3.Data](numHosts: Int, payloadT: T) {
  def addrbitW = log2Ceil(numHosts + 1)
}

class Message[T <: chisel3.Data](p: XBarParams[T]) extends Bundle {
  val addr = UInt(p.addrbitW.W)
  val data = p.payloadT
}

class PortIO[T <: chisel3.Data](p: XBarParams[T]) extends Bundle {
  val in = Flipped(Decoupled(new Message(p)))
  val out = Decoupled(new Message(p))
}

class XBar[T <: chisel3.Data](p: XBarParams[T]) extends Module {
  val io = IO(new Bundle {
    val ports = Vec(p.numHosts, new PortIO(p))
  })
  val arbs =
    Seq.fill(p.numHosts)(Module(new RRArbiter(new Message(p), p.numHosts)))
  for (ip <- 0 until p.numHosts) {
    io.ports(ip).in.ready := arbs.map { _.io.in(ip).ready }.reduce { _ || _ }
  }
  for (op <- 0 until p.numHosts) {
    arbs(op).io.in.zip(io.ports).foreach { case (arbIn, port) =>
      arbIn.bits <> port.in.bits
      arbIn.valid := port.in.valid && (port.in.bits.addr === op.U)
    }
    io.ports(op).out <> arbs(op).io.out
  }
}
