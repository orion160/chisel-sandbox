import org.chipsalliance.cde.config._

case object SomeKeyX extends Field[Boolean](false)
case object SomeKeyY extends Field[Boolean](false)
case object SomeKeyZ extends Field[Boolean](false)

class WithX(b: Boolean) extends Config((site, here, up) => {
  case SomeKeyX => b
})

class WithY(b: Boolean) extends Config((site, here, up) => {
  case SomeKeyY => b
})

val params = new Config(new WithX(true).orElse( new WithY(true))) 
params(SomeKeyX) // evaluates to true
params(SomeKeyY) // evaluates to true
params(SomeKeyZ) // evaluates to false
