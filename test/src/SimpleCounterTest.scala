package sandbox

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class SimpleCounterTest extends AnyFreeSpec with Matchers {
  "SimpleCounter" in {
    simulate(new SimpleCounter(3)) { dut =>
      dut.io.en.poke(true.B)
      dut.io.out.expect(0.U)
      dut.clock.step()

      dut.io.en.poke(false.B)
      dut.io.out.expect(1.U)
      dut.clock.step()

      dut.io.en.poke(true.B)
      dut.io.out.expect(1.U)
      dut.clock.step()

      dut.io.en.poke(true.B)
      dut.io.out.expect(2.U)
      dut.clock.step()

      dut.io.en.poke(true.B)
      dut.io.out.expect(3.U)
      dut.clock.step()

      dut.io.en.poke(true.B)
      dut.io.out.expect(0.U)
      dut.clock.step()
    }
  }

  "SimpleCounter2" in {
    simulate(new SimpleCounter2(3)) { dut =>
      dut.io.en.poke(true.B)
      dut.io.out.expect(0.U)
      dut.clock.step()

      dut.io.en.poke(false.B)
      dut.io.out.expect(1.U)
      dut.clock.step()

      dut.io.en.poke(true.B)
      dut.io.out.expect(1.U)
      dut.clock.step()

      dut.io.en.poke(true.B)
      dut.io.out.expect(2.U)
      dut.clock.step()

      dut.io.en.poke(true.B)
      dut.io.out.expect(3.U)
      dut.clock.step()

      dut.io.en.poke(true.B)
      dut.io.out.expect(0.U)
      dut.clock.step()
    }
  }

  "SimpleCounter3" in {
    simulate(new SimpleCounter3(3)) { dut =>
      dut.io.en.poke(true.B)
      dut.io.out.expect(0.U)
      dut.clock.step()

      dut.io.en.poke(false.B)
      dut.io.out.expect(1.U)
      dut.clock.step()

      dut.io.en.poke(true.B)
      dut.io.out.expect(1.U)
      dut.clock.step()

      dut.io.en.poke(true.B)
      dut.io.out.expect(2.U)
      dut.clock.step()

      dut.io.en.poke(true.B)
      dut.io.out.expect(3.U)
      dut.clock.step()

      dut.io.en.poke(true.B)
      dut.io.out.expect(0.U)
      dut.clock.step()
    }
  }
}
