package sandbox

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class PassthroughTest extends AnyFreeSpec with Matchers {
  "Passthrough should work" in {
    simulate(new PassthroughGenerator(4)) { dut =>
      dut.io.in.poke(0.U)
      dut.io.out.expect(0.U)

      dut.io.in.poke(1.U)
      dut.io.out.expect(1.U)
    }
  }
}
