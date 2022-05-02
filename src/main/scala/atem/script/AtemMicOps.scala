package atem.script

import scala.math._

trait AtemMicOps extends AtemControlOps:

  case class AudioFadeArguments(
    fromGain: Double = 0,
    toGain: Double = -120.41d,
    steps: Int = 50,
    framesBetweenSteps: Int = 1)

  val DefaultAudioFadeArguments: AudioFadeArguments = AudioFadeArguments()

  private def micGain(input: AtemMicInput, gain: Double)(using product: AtemProduct.AtemMiniPro): AtemOp =
    AtemOp.AudioInputGainOp(input, gain, product.sourceId)

  def mic1Gain(gain: Double)(using product: AtemProduct.AtemMiniPro): AtemOp =
    micGain(AtemMicInput.Mic1, gain)

  def mic2Gain(gain: Double)(using product: AtemProduct.AtemMiniPro): AtemOp =
    micGain(AtemMicInput.Mic2, gain)

  type FadeFunction = (fromGain: Double, toGain: Double, steps: Int, step: Int) => Double

  val linearFade: FadeFunction = (fromGain: Double, toGain: Double, steps: Int, step: Int) =>
    val delta = (toGain - fromGain) / steps
    val gain = fromGain + delta * (step + 1)
    gain

  val logFade: FadeFunction = (fromGain: Double, toGain: Double, steps: Int, step: Int) =>
    val minLog = log(max(abs(fromGain + 1), 0.1d))
    val maxLog = log(abs(toGain))
    val delta = (maxLog - minLog) / steps
    val gain = exp(abs(minLog + delta * (step + 1)))
    gain * -1

  val DefaultFadeFunction: FadeFunction = logFade

  private def micFade(
    input: AtemMicInput,
    fadeArguments: AudioFadeArguments,
    fadeFunction: FadeFunction
  )(using product: AtemProduct.AtemMiniPro
  ): AtemMacroSection =
    import fadeArguments.*
    (for {
      step <- 0 until steps
      gain = fadeFunction(fromGain, toGain, steps, step)
    } yield micGain(input, gain)).flatMap(sleep(framesBetweenSteps) :: _ :: Nil).toList.tail

  def mic1Fade(
    fromGain: Double = DefaultAudioFadeArguments.fromGain,
    toGain: Double = DefaultAudioFadeArguments.toGain,
    steps: Int = DefaultAudioFadeArguments.steps,
    framesBetweenSteps: Int = DefaultAudioFadeArguments.framesBetweenSteps,
    fadeFunction: FadeFunction = DefaultFadeFunction
  )(using product: AtemProduct.AtemMiniPro
  ): AtemMacroSection =
    micFade(
      AtemMicInput.Mic1,
      AudioFadeArguments(fromGain, toGain, steps, framesBetweenSteps),
      fadeFunction
    )

  def mic2Fade(
    fromGain: Double = DefaultAudioFadeArguments.fromGain,
    toGain: Double = DefaultAudioFadeArguments.toGain,
    steps: Int = DefaultAudioFadeArguments.steps,
    framesBetweenSteps: Int = DefaultAudioFadeArguments.framesBetweenSteps,
    fadeFunction: FadeFunction = DefaultFadeFunction
  )(using product: AtemProduct.AtemMiniPro
  ): AtemMacroSection =
    micFade(
      AtemMicInput.Mic2,
      AudioFadeArguments(fromGain, toGain, steps, framesBetweenSteps),
      fadeFunction
    )
