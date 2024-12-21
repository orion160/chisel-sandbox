package sandbox

import chisel3._
import chisel3.util._

class Queue1(numEntries: Int, bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val enq = Flipped(Decoupled(UInt(bitWidth.W)))
    val deq = Decoupled(UInt(bitWidth.W))
  })
}

class Queue2(bitWidth: Int) extends Queue1(1, bitWidth) {
  val entry = Reg(UInt(bitWidth.W))
  val full = RegInit(false.B)
  io.enq.ready := !full || io.deq.fire
  io.deq.valid := full
  io.deq.bits := entry
  when(io.deq.fire) {
    full := false.B
  }
  when(io.enq.fire) {
    entry := io.enq.bits
    full := true.B
  }
}

class Queue3(numEntries: Int, bitWidth: Int)
    extends Queue1(numEntries, bitWidth) {
  require(numEntries > 0)
  // enqueue tail: numEntries - 1
  // dequeue head: 0
  val entries = Seq.fill(numEntries)(Reg(UInt(bitWidth.W)))
  val fullBits = Seq.fill(numEntries)(RegInit(false.B))
  val shiftDown = io.deq.fire || !fullBits.head // dequeue
  io.enq.ready := !fullBits.last || shiftDown
  io.deq.valid := fullBits.head
  io.deq.bits := entries.head
  when(shiftDown) {
    for (i <- 0 until numEntries - 1) {
      entries(i) := entries(i + 1)
      fullBits(i) := fullBits(i + 1)
    }
    fullBits.last := false.B
  }
  when(io.enq.fire) {
    entries.last := io.enq.bits
    fullBits.last := true.B
  }
}

class Queue4(numEntries: Int, bitWidth: Int)
    extends Queue1(numEntries, bitWidth) {
  require(numEntries > 0)
  val entries = Reg(Vec(numEntries, UInt(bitWidth.W)))
  val fullBits = RegInit(VecInit(Seq.fill(numEntries)(false.B)))
  val emptyBits = fullBits map { !_ }
  io.enq.ready := emptyBits reduce { _ || _ }
  io.deq.valid := fullBits.head
  io.deq.bits := entries.head
  when(io.deq.fire) {
    fullBits.last := false.B
    for (i <- 0 until numEntries - 1) {
      entries(i) := entries(i + 1)
      fullBits(i) := fullBits(i + 1)
    }
  }
  when(io.enq.fire) {
    val currentFreeIndex = PriorityEncoder(emptyBits)
    val writeIndex = Mux(io.deq.fire, currentFreeIndex - 1.U, currentFreeIndex)
    entries(writeIndex) := io.enq.bits
    fullBits(writeIndex) := true.B
  }
}

class Queue5(numEntries: Int, bitWidth: Int)
    extends Queue1(numEntries, bitWidth) {
  require(numEntries > 1)
  require(isPow2(numEntries))
  val entries = Reg(Vec(numEntries, UInt(bitWidth.W)))
  val enqIndex = RegInit(0.U(log2Ceil(numEntries).W))
  val deqIndex = RegInit(0.U(log2Ceil(numEntries).W))
  val empty = enqIndex === deqIndex
  val full = (enqIndex +% 1.U) === deqIndex
  io.enq.ready := !full
  io.deq.valid := !empty
  io.deq.bits := entries(deqIndex)
  when(io.deq.fire) {
    deqIndex := deqIndex +% 1.U
  }
  when(io.enq.fire) {
    entries(enqIndex) := io.enq.bits
    enqIndex := enqIndex +% 1.U
  }
}

// dont waste one entry for signaling fullness or emptiness
class Queue6(numEntries: Int, bitWidth: Int)
    extends Queue1(numEntries, bitWidth) {
  require(numEntries > 1)
  require(isPow2(numEntries))
  val entries = Reg(Vec(numEntries, UInt(bitWidth.W)))
  val enqIndex = RegInit(0.U(log2Ceil(numEntries).W))
  val deqIndex = RegInit(0.U(log2Ceil(numEntries).W))
  val maybeFull = RegInit(false.B)
  val empty = enqIndex === deqIndex && !maybeFull
  val full = enqIndex === deqIndex && maybeFull
  io.enq.ready := !full
  io.deq.valid := !empty
  io.deq.bits := entries(deqIndex)
  when(io.deq.fire) {
    deqIndex := deqIndex +% 1.U
    when(enqIndex =/= deqIndex) {
      maybeFull := false.B
    }
  }
  when(io.enq.fire) {
    entries(enqIndex) := io.enq.bits
    enqIndex := enqIndex +% 1.U
    when((enqIndex +% 1.U) === deqIndex) {
      maybeFull := true.B
    }
  }
}

class Queue7(numEntries: Int, bitWidth: Int)
    extends Queue1(numEntries, bitWidth) {
  require(numEntries > 1)
  require(isPow2(numEntries))
  val entries = Reg(Vec(numEntries, UInt(bitWidth.W)))
  val enqIndex = RegInit(0.U(log2Ceil(numEntries).W))
  val deqIndex = RegInit(0.U(log2Ceil(numEntries).W))
  val maybeFull = RegInit(false.B)
  val empty = enqIndex === deqIndex && !maybeFull
  val full = enqIndex === deqIndex && maybeFull
  // enqueue/dequeue when full
  // possible combinational loop as io.enq.ready is attached to io.deq.ready
  io.enq.ready := !full || io.deq.ready
  io.deq.valid := !empty
  io.deq.bits := entries(deqIndex)
  when(io.deq.fire) {
    deqIndex := deqIndex +% 1.U
    when(enqIndex =/= deqIndex) {
      maybeFull := false.B
    }
  }
  when(io.enq.fire) {
    entries(enqIndex) := io.enq.bits
    enqIndex := enqIndex +% 1.U
    when((enqIndex +% 1.U) === deqIndex) {
      maybeFull := true.B
    }
  }
}

class Queue8(numEntries: Int, bitWidth: Int, pipe: Boolean = true)
    extends Queue1(numEntries, bitWidth) {
  require(numEntries > 1)
  val entries = Mem(numEntries, UInt(bitWidth.W))
  val enqIndex = Counter(numEntries)
  val deqIndex = Counter(numEntries)
  val maybefull = RegInit(false.B)
  val indicesEqual = enqIndex.value === deqIndex.value
  val empty = indicesEqual && !maybefull
  val full = indicesEqual && maybefull
  if (pipe) io.enq.ready := !full || io.deq.ready
  else io.enq.ready := !full
  io.deq.valid := empty
  io.deq.bits := entries(deqIndex.value)
  when(io.deq.fire =/= io.enq.fire) {
    maybefull := io.enq.fire
  }
  when(io.deq.fire) {
    deqIndex.inc()
  }
  when(io.enq.fire) {
    entries(enqIndex.value) := io.enq.bits
    enqIndex.inc()
  }
}
