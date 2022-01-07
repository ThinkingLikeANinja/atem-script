package atem.script

enum AtemMicInput(val name: String):
  case Mic1 extends AtemMicInput("ExternalMic")
  case Mic2 extends AtemMicInput("ExternalMic2")

enum AtemHdmiInput(val name: String):
  case Hdmi1 extends AtemHdmiInput("Camera1")
  case Hdmi2 extends AtemHdmiInput("Camera2")
  case Hdmi3 extends AtemHdmiInput("Camera3")
  case Hdmi4 extends AtemHdmiInput("Camera4")

enum AtemOp(id: String):
  case SleepOp(frames: Int) extends AtemOp("MacroSleep")
  case UserWaitOp extends AtemOp("MacroUserWait")
  case AudioInputGainOp(
    input: AtemMicInput,
    gain: Double,
    sourceId: String) extends AtemOp("FairlightAudioMixerInputSourceFaderGain")

enum AtemProduct(
  val name: String,
  hdmi: List[String],
  mic: List[AtemMicInput],
  sourceId: String):
  case AtemMiniPro(sourceId: String)
      extends AtemProduct("ATEM Mini Pro", Nil, List(AtemMicInput.Mic1, AtemMicInput.Mic2), sourceId)

case class AtemMacroSection(ops: List[AtemOp]):
  def combine(other: AtemMacroSection): AtemMacroSection = AtemMacroSection(this.ops ++ other.ops)

case class AtemMacro(
  name: String,
  ops: List[AtemOp],
  description: Option[String] = None)

case class AtemProfile(product: AtemProduct, macros: List[AtemMacro])
