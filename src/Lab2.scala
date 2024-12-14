package Lab2

import chisel._

class Delay2 extends Module {
    val io = IO(new Bundle {
        val in  = Input(UInt(5.W))
        val out = Output(UInt(5.W))
    })

    val r0 = Reg()
    val r1 = Reg()

    r0 := io.in
    r1 := r0
    io.out := r1
}
