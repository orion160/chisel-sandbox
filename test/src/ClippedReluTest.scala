package sandbox

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class ReluTest extends AnyFreeSpec with Matchers {
  "Relu lower clamp domain" in {
    simulate(new ClippedRelu(3)) { dut =>
      dut.io.x.poke(0.S)
      dut.io.y.expect(0.S)

      dut.io.x.poke(-1.S)
      dut.io.y.expect(0.S)

      dut.io.x.poke(-2.S)
      dut.io.y.expect(0.S)
    }
  }

  "Relu within linear domain" in {
    simulate(new ClippedRelu(3)) { dut =>
      dut.io.x.poke(1.S)
      dut.io.y.expect(1.S)

      dut.io.x.poke(2.S)
      dut.io.y.expect(2.S)
    }
  }

  "Relu upper clamp domain" in {
    simulate(new ClippedRelu(3)) { dut =>
      dut.io.x.poke(3.S)
      dut.io.y.expect(3.S)

      dut.io.x.poke(4.S)
      dut.io.y.expect(3.S)

      dut.io.x.poke(5.S)
      dut.io.y.expect(3.S)
    }
  }
}
