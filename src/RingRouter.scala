package sandboxb

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

class RingRouter[T <: chisel3.Data](p: XBarParams[T], id: Int) extends Module {
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

class RingNetwork[T <: chisel3.Data](p: XBarParams[T]) extends Module {
  val io = IO(new Bundle {
    val ports = Vec(p.numHosts, new PortIO(p))
  })
  val routers = Seq.tabulate(p.numHosts) { id => new RingRouter(p, id) }
  routers.foldLeft(routers.last) { (prev, cur) =>
    prev.io.out <> cur.io.in
    cur
  }
  routers.zip(io.ports).foreach { case (router, port) =>
    router.io.host <> port
  }
}
