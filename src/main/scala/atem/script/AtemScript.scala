package atem.script

import scala.math.*

enum AtemOp(id: String):
  case SleepOp(frames: Int) extends AtemOp("MacroSleep")
  case UserWaitOp extends AtemOp("MacroUserWait")
  case AudioInputGainOp(
    input: AtemInput,
    gain: Double,
    sourceId: String) extends AtemOp("FairlightAudioMixerInputSourceFaderGain")

case class AtemMacroSection(ops: List[AtemOp]):
  def combine(other: AtemMacroSection) = AtemMacroSection(this.ops ++ other.ops)

case class AtemMacro(ops: List[AtemOp], description: Option[String] = None)
object AtemMacro:
  def apply(section: AtemMacroSection): AtemMacro = AtemMacro(section.ops)

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
    val delta = (toGain - fromGain) / steps
    val gain = fromGain + delta * (step + 1)
    println(s"linear step $step gain $gain")
    gain
  val logFade: FadeFunction = (fromGain: Double, toGain: Double, steps: Int, step: Int) =>
    val minLog = log(max(abs(fromGain + 1), 0.1d))
    val maxLog = log(abs(toGain))
    val delta = (maxLog - minLog) / steps
    val gain = exp(abs(minLog + delta * (step + 1)))
    println(s"log step $step gain $gain")
    gain
  val DefaultFadeFunction = logFade
  private def micFade(
    input: AtemInput,
    fromGain: Double,
    toGain: Double,
    steps: Int,
    framesBetweenSteps: Int,
    fadeFunction: FadeFunction
  )(using sourceId: String
  ): AtemMacroSection =
    (for {
      step <- 0 until steps
      gain = fadeFunction(fromGain, toGain, steps, step)
    } yield micGain(input, gain)).toList :+ sleep(framesBetweenSteps)
  def mic1Fade(
    fromGain: Double = 0,
    toGain: Double = MinusInfMicGain,
    steps: Int = 30,
    framesBetweenSteps: Int = 4,
    fadeFunction: FadeFunction = DefaultFadeFunction
  )(using sourceId: String
  ): AtemMacroSection =
    micFade(AtemInput.Mic1, fromGain, toGain, steps, framesBetweenSteps, fadeFunction)
  def mic2Fade(
    fromGain: Double = 0,
    toGain: Double = MinusInfMicGain,
    steps: Int = 30,
    framesBetweenSteps: Int = 4,
    fadeFunction: FadeFunction = DefaultFadeFunction
  )(using sourceId: String
  ): AtemMacroSection =
    micFade(AtemInput.Mic2, fromGain, toGain, steps, framesBetweenSteps, fadeFunction)
  extension (sections: Seq[AtemMacroSection])
    def combineSections =
      sections.toList.reduce(_.combine(_))
  extension (s: String)
    def apply(op: AtemOp, ops: AtemOp*) =
      AtemMacro(op :: ops.toList)
    def apply(section: AtemMacroSection, sections: AtemMacroSection*) =
      AtemMacro((section :: (if sections.isEmpty then Nil else sections.toList)).combineSections)
    def apply(description: String, sections: AtemMacroSection*) =
      AtemMacro(sections.combineSections, Some(description))

//example

  given sourceId: String = "18446744073709486336"
  println {

    atemMiniPro(
//      "some macro" (
//        "some description",
//        mic1Gain(2),
//        sleep(5),
//        mic1Fade()
//      ),
      "fade" (mic1Fade())
//      "linear fade" (mic2Fade(fadeFunction = linearFade))
    )

  }
