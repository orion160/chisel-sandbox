package DiplomacySample

import org.chipsalliance.cde.config._
import freechips.rocketchip.diplomacy.{
  LazyModule,
  LazyModuleImp,
}

import chisel3._

class AdderMonitor(width: Int, numOperands: Int)(implicit p: Parameters)
    extends LazyModule {

  lazy val module = new LazyModuleImp(this) {
    val io = IO(new Bundle {
      val error = Output(Bool())
    })

    // //nodeSum.in.head._1 =/= nodeSeq.map(_.in.head._1).reduce(_ + _)
    io.error := true.B // above operation
  }

  override lazy val desiredName = "AdderMonitor"
}
