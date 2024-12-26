package sandboxb

import chisel3._
import chisel3.util._

case class NetworkParams[T <: chisel3.Data](numHosts: Int, payloadT: T) {
  def addrbitW = log2Ceil(numHosts + 1)
}

class Message[T <: chisel3.Data](p: NetworkParams[T]) extends Bundle {
  val addr = UInt(p.addrbitW.W)
  val data = p.payloadT
}

class PortIO[T <: chisel3.Data](p: NetworkParams[T]) extends Bundle {
  val in = Flipped(Decoupled(new Message(p)))
  val out = Decoupled(new Message(p))
}

abstract class Network[T <: chisel3.Data](p: NetworkParams[T]) extends Module {
  val io = IO(new Bundle {
    val ports = Vec(p.numHosts, new PortIO(p))
  })
}

class XBar[T <: chisel3.Data](p: NetworkParams[T]) extends Network[T](p) {
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

class RingRouter[T <: chisel3.Data](p: NetworkParams[T], id: Int)
    extends Module {
  val io = IO(new Bundle {
    val in = Flipped(Decoupled(new Message(p)))
    val out = Decoupled(new Message(p))
    val host = new PortIO(p)
  })
  val forMe = (io.in.bits.addr === id.U) && io.in.valid
  // ...
  io.host.in.ready := io.out.ready
  io.host.out.valid := forMe
  io.host.out.bits := io.in.bits
  io.in.ready := io.host.out.ready && io.out.ready
  io.out.valid := (io.in.fire && !forMe) || io.host.in.fire
  io.out.bits := Mux(io.host.in.fire, io.host.in.bits, io.in.bits)
}

class RingNetwork[T <: chisel3.Data](p: NetworkParams[T])
    extends Network[T](p) {
  val routers = Seq.tabulate(p.numHosts) { id => new RingRouter(p, id) }
  routers.foldLeft(routers.last) { (prev, cur) =>
    prev.io.out <> cur.io.in
    cur
  }
  routers.zip(io.ports).foreach { case (router, port) =>
    router.io.host <> port
  }
}
