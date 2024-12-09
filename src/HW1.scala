package HW1

import chisel3._

// https://github.com/agile-hw/hw1

// io.instWord: 32b UInt Input
// io.opcode: 7b UInt Output
// io.funct3: 3b UInt Output
// io.rs1: 5b UInt Output
// io.rd: 5b UInt Output
// io.immSignExtended: 32b UInt Output
class RiscvITypeDecoder extends Module {
  val io = IO(new Bundle {
    val instWord = Input(UInt(32.W))
    val opcode = Output(UInt(7.W))
    val funct3 = Output(UInt(3.W))
    val rs1 = Output(UInt(5.W))
    val rd = Output(UInt(5.W))
    val immSingExtended = Output(UInt(32.W))
  })

  io.opcode := io.instWord(6, 0)
  io.funct3 := io.instWord(14, 12)
  io.rs1 := io.instWord(19, 15)
  io.rd := io.instWord(11, 7)
  io.immSingExtended := io.instWord(31, 20)
}

class MajorityCircuit extends Module {
  val io = IO(new Bundle {
    val a = Input(Bool())
    val b = Input(Bool())
    val c = Input(Bool())
    val out = Output(Bool())
  })
  io.out := (io.a & io.b) | (io.a & io.c) | (io.b & io.c)
}
