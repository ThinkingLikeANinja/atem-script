package atem.script

import scala.math.*

enum AtemOp(id: String):
  case SleepOp(frames: Int) extends AtemOp("MacroSleep")
  case UserWaitOp extends AtemOp("MacroUserWait")
  case AudioInputGainOp(input: AtemInput, gain: Double, sourceId: String) extends AtemOp("FairlightAudioMixerInputSourceFaderGain")

case class AtemMacro(ops: List[AtemOp], description: Option[String] = None)

case class AtemMacroSection(ops: List[AtemOp]):
  def combine(other: AtemMacroSection) = AtemMacroSection(this.ops ++ other.ops)

given Conversion[AtemOp, AtemMacroSection] with
  def apply(op: AtemOp): AtemMacroSection = AtemMacroSection(List(op))

given Conversion[List[AtemOp], AtemMacroSection] with
  def apply(ops: List[AtemOp]): AtemMacroSection = AtemMacroSection(ops)

given Conversion[AtemMacroSection, List[AtemOp]] with
  def apply(macroSection: AtemMacroSection): List[AtemOp] = macroSection.ops

enum AtemProduct(name: String):
  case AtemMiniPro extends AtemProduct("ATEM Mini Pro")

enum AtemInput(name: String):
  case Mic1 extends AtemInput("ExternalMic")
  case Mic2 extends AtemInput("ExternalMic2")

case class AtemProfile(product: AtemProduct, macros: List[AtemMacro])

object AtemScript extends App:
  val MinusInfMicGain = -120.41d
  def atemMiniPro(macros: AtemMacro*): AtemProfile =
    AtemProfile(AtemProduct.AtemMiniPro, macros.toList)
  def sleep(frames: Int): AtemOp =
    AtemOp.SleepOp(frames)
  def userWait: AtemOp =
    AtemOp.UserWaitOp
  private def micGain(input: AtemInput, gain: Double)(using sourceId: String): AtemOp =
    AtemOp.AudioInputGainOp(input, gain, sourceId)
  def mic1Gain(gain: Double)(using sourceId: String): AtemOp =
    micGain(AtemInput.Mic1, gain)
  def mic2Gain(gain: Double)(using sourceId: String): AtemOp =
    micGain(AtemInput.Mic2, gain)
  type FadeFunction = (fromGain: Double, toGain: Double, steps: Int, step: Int) => Double
  val linearFade: FadeFunction = (fromGain: Double, toGain: Double, steps: Int, step: Int) =>
    fromGain + ((toGain - fromGain) / steps) * step
  val logFade: FadeFunction = (fromGain: Double, toGain: Double, steps: Int, step: Int) =>
    val minLog = log(max(abs(fromGain), 0.1d))
    val maxLog = log(abs(toGain))
    val delta = (maxLog - minLog) / (steps - 1)
    exp(abs(minLog + (delta * step)))
  private def micFade(input: AtemInput, fromGain: Double, toGain: Double, steps: Int, framesBetweenSteps: Int,
                      fadeFunction: FadeFunction = logFade)(using sourceId: String): AtemMacroSection =
    (for {
      step <- 0 until steps
      gain = fadeFunction(fromGain, toGain, steps, step)
    } yield micGain(input, gain)).toList :+ sleep(framesBetweenSteps)
  def mic1Fade(fromGain: Double = 0, toGain: Double = MinusInfMicGain, steps: Int = 30, framesBetweenSteps: Int = 4)(using sourceId: String): AtemMacroSection =
    micFade(AtemInput.Mic1, fromGain, toGain, steps, framesBetweenSteps)
  def mic2Fade(fromGain: Double = 0, toGain: Double = MinusInfMicGain, steps: Int = 30, framesBetweenSteps: Int = 4)(using sourceId: String): AtemMacroSection =
    micFade(AtemInput.Mic2, fromGain, toGain, steps, framesBetweenSteps)
  extension(s: String)
    def apply(op: AtemOp, ops: AtemOp*) =
      AtemMacro(op :: ops.toList)
    def apply(section: AtemMacroSection, sections: AtemMacroSection*) =
      AtemMacro(section.combine(sections.reduce(_.combine(_))).ops)
    def apply(description: String, sections: AtemMacroSection*) =
      AtemMacro(sections.reduce(_.combine(_)).ops, Some(description))

//example

  given sourceId: String = "18446744073709486336"
  println({

    atemMiniPro(
      "some macro"(
        "some description",
        mic1Gain(2),
        sleep(5),
        mic1Fade()
      ),

      "fade"(mic1Fade())
    )

  })
