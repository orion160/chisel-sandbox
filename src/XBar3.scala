package sandboxa

import chisel3._
import chisel3.util._

case class XBarParams(numHosts: Int, payloadSize: Int) {
  def addrbitW = log2Ceil(numHosts + 1)
}

class Message(p: XBarParams) extends Bundle {
  val addr = UInt(p.addrbitW.W)
  val data = UInt(p.payloadSize.W)
}

class PortIO(p: XBarParams) extends Bundle {
  val in = Flipped(Decoupled(new Message(p)))
  val out = Decoupled(new Message(p))
}

class XBar(p: XBarParams) extends Module {
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
