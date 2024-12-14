package HW1

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class PassthroughTest extends AnyFreeSpec with Matchers {
  "Exhaustive test MajorityCircuit" in {
    simulate(new MajorityCircuit()) { dut =>
      dut.io.a.poke(true.B)
      dut.io.b.poke(true.B)
      dut.io.c.poke(true.B)
      dut.io.out.expect(true.B)

      dut.io.a.poke(false.B)
      dut.io.b.poke(true.B)
      dut.io.c.poke(true.B)
      dut.io.out.expect(true.B)

      dut.io.a.poke(true.B)
      dut.io.b.poke(false.B)
      dut.io.c.poke(true.B)
      dut.io.out.expect(true.B)

      dut.io.a.poke(true.B)
      dut.io.b.poke(true.B)
      dut.io.c.poke(false.B)
      dut.io.out.expect(true.B)

      dut.io.a.poke(false.B)
      dut.io.b.poke(false.B)
      dut.io.c.poke(true.B)
      dut.io.out.expect(false.B)

      dut.io.a.poke(false.B)
      dut.io.b.poke(true.B)
      dut.io.c.poke(false.B)
      dut.io.out.expect(false.B)

      dut.io.a.poke(true.B)
      dut.io.b.poke(false.B)
      dut.io.c.poke(false.B)
      dut.io.out.expect(false.B)

      dut.io.a.poke(false.B)
      dut.io.b.poke(false.B)
      dut.io.c.poke(false.B)
      dut.io.out.expect(false.B)
    }
  }

  "Exhaustive test MajorityCircuit w/ loop" in {
    simulate(new MajorityCircuit()) { dut =>
      for (
        i <- Seq(true, false); j <- Seq(true, false); k <- Seq(true, false)
      ) {
        dut.io.a.poke(i.B)
        dut.io.b.poke(j.B)
        dut.io.c.poke(k.B)
        val expected = (i && j) || (i && k) || (j && k)
        dut.io.out.expect(expected.B)
      }
    }
  }
}
